"""Comprueba el sitio bajo su subruta pública y los recursos que descarga."""
from __future__ import annotations

import functools
import io
import threading
from zipfile import ZipFile
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from urllib.parse import urlparse

from playwright.sync_api import sync_playwright
from pypdf import PdfReader

from export_pdfs import launch_chromium
from navigation import ROOT, config, find_sections, flatten_nav

SITE = ROOT / "site"
PREFIX = urlparse(config()["site_url"]).path


class Handler(SimpleHTTPRequestHandler):
    def log_message(self, *_):
        pass

    def translate_path(self, path):
        if not path.startswith(PREFIX):
            return str(SITE / "__outside_site__")
        return super().translate_path("/" + path[len(PREFIX):])


def route(source):
    path = Path(source)
    return (path.parent if path.name == "index.md" else path.with_suffix("")).as_posix().strip("./") + "/" if source != "index.md" else ""


def set_scheme(page, theme):
    if page.locator("body").get_attribute("data-md-color-scheme") != theme:
        page.locator('form[data-md-component="palette"] label:visible').click()
    assert page.locator("body").get_attribute("data-md-color-scheme") == theme


def check_tab_contrast(page):
    for link in page.locator(".md-tabs__link:visible").all():
        ratio = link.evaluate("""el => {
          const rgba = s => (s.match(/[\\d.]+/g) || []).map(Number);
          let opacity = 1, node = el, background = null;
          while (node) {
            const style = getComputedStyle(node);
            opacity *= Number(style.opacity);
            const color = rgba(style.backgroundColor);
            if (!background && color.length >= 3 && (color.length < 4 || color[3] > 0.99)) background = color;
            node = node.parentElement;
          }
          background ||= [255,255,255];
          const foreground = rgba(getComputedStyle(el).color);
          opacity *= foreground.length > 3 ? foreground[3] : 1;
          const painted = foreground.slice(0,3).map((c,i) => opacity*c + (1-opacity)*background[i]);
          const luminance = color => color.slice(0,3).map(c => c/255).map(c => c <= .04045 ? c/12.92 : ((c+.055)/1.055)**2.4).reduce((s,c,i) => s+c*[.2126,.7152,.0722][i],0);
          const a=luminance(painted), b=luminance(background);
          return (Math.max(a,b)+.05)/(Math.min(a,b)+.05);
        }""")
        assert ratio >= 4.5, (link.inner_text(), ratio)


def check():
    project = config()
    sections = find_sections(project)
    server = ThreadingHTTPServer(("127.0.0.1", 0), functools.partial(Handler, directory=str(SITE)))
    threading.Thread(target=server.serve_forever, daemon=True).start()
    base = f"http://127.0.0.1:{server.server_port}{PREFIX}"
    captures = ROOT / "qa-captures"
    captures.mkdir(exist_ok=True)
    try:
        with sync_playwright() as pw:
            browser = launch_chromium(pw)
            try:
                page = browser.new_page(viewport={"width": 1366, "height": 844}, accept_downloads=True)
                errors = []
                page.on("pageerror", lambda error: errors.append(str(error)))
                pages = flatten_nav(project["nav"])
                for source in pages:
                    response = page.goto(base + route(source), wait_until="networkidle")
                    assert response and response.ok, source
                    assert page.locator("article h1").first.is_visible(), source
                    assert "Mikel Aingeru Jorge Soteras" in page.locator(".md-copyright").inner_text()
                    assert page.locator(".md-tabs__link").count() == len(project["nav"])
                    assert page.locator(".md-header__button.md-logo").evaluate("el => el.href") == base
                    assert page.locator(".mjs-pdf-trigger").count() == 1
                    for image in page.locator("article img").all():
                        assert image.get_attribute("alt"), source
                        assert image.evaluate("el => el.complete && el.naturalWidth > 0"), source
                    for width in (1366, 390):
                        page.set_viewport_size({"width": width, "height": 844})
                        assert page.evaluate("document.documentElement.scrollWidth <= innerWidth + 2"), (source, width)
                        for link in page.locator(".md-footer__link").all():
                            box = link.bounding_box()
                            assert box and box["width"] <= 280, (source, width, box)
                    page.set_viewport_size({"width": 1366, "height": 844})
                for source in ("index.md", sections[0][2][0]):
                    for width in (1366, 390):
                        page.set_viewport_size({"width": width, "height": 844})
                        for theme in ("default", "slate"):
                            page.goto(base + route(source), wait_until="networkidle")
                            set_scheme(page, theme)
                            check_tab_contrast(page)
                            label = "inicio" if source == "index.md" else sections[0][0].replace("/", "-")
                            page.screenshot(path=str(captures / f"{label}-{width}-{theme}.png"), full_page=True)
                page.set_viewport_size({"width": 1366, "height": 844})
                for slug, title, _ in sections:
                    page.goto(base + slug + "/", wait_until="networkidle")
                    page.locator(".mjs-pdf-trigger").click()
                    links = page.locator(".mjs-pdf-menu a")
                    assert links.count() == 2, slug
                    assert title in links.first.inner_text(), slug
                    for index, filename in enumerate((slug.replace("/", "-") + ".pdf", "documentacion-completa.pdf")):
                        if index:
                            page.locator(".mjs-pdf-trigger").click()
                        link = page.locator(".mjs-pdf-menu a").nth(index)
                        assert link.get_attribute("href") == base + "descargas/" + filename
                        response = page.request.get(link.get_attribute("href"))
                        assert response.ok
                        pdf = PdfReader(io.BytesIO(response.body()))
                        assert pdf.pages
                        if filename == "documentacion-completa.pdf":
                            text = "\n".join(p.extract_text() for p in pdf.pages)
                            assert "Publicación progresiva" in text and "En construcción" in text
                        with page.expect_download() as download:
                            link.click()
                        assert download.value.suggested_filename == filename
                        assert download.value.failure() is None
                page.goto(base, wait_until="networkidle")
                page.locator(".md-search__button").click()
                query = page.get_by_role("combobox").first
                query.fill("Gradle")
                page.wait_for_timeout(1000)
                assert query.is_visible()
                # El buscador moderno usa un combobox y enlaces de resultados
                # fuera de la navegación normal del documento.
                results = query.locator("xpath=../../..").locator('a[href*="ecosistema/"]')
                assert results.count() > 0
                result_url = results.first.evaluate("el => el.href")
                results.first.click()
                page.wait_for_url(result_url)
                assert page.locator("article h1").is_visible()
                page.keyboard.press("Escape")
                page.goto(base + "ecosistema/practica/", wait_until="networkidle")
                figure = page.locator("article figure").first
                if figure.count():
                    for width in (1366, 390):
                        page.set_viewport_size({"width": width, "height": 844})
                        for theme in ("default", "slate"):
                            set_scheme(page, theme)
                            assert figure.locator("figcaption").inner_text().strip()
                            assert figure.evaluate("el => Math.abs((el.getBoundingClientRect().left + el.getBoundingClientRect().right)/2 - (el.parentElement.getBoundingClientRect().left + el.parentElement.getBoundingClientRect().right)/2) < 3"), "Figura descentrada"
                            assert figure.locator("img").evaluate("el => Math.abs((el.getBoundingClientRect().left + el.getBoundingClientRect().right)/2 - (el.closest('figure').getBoundingClientRect().left + el.closest('figure').getBoundingClientRect().right)/2) < 3"), "Captura descentrada"
                            page.mouse.move(0, 0)
                            page.keyboard.press("Escape")
                            page.wait_for_timeout(300)
                            figure.screenshot(path=str(captures / f"captura-figura-{width}-{theme}.png"))
                    page.set_viewport_size({"width": 1366, "height": 844})
                zip_links = page.locator('article a[href$=".zip"]')
                assert zip_links.count() >= 2
                for link in zip_links.all():
                    response = page.request.get(link.evaluate("el => el.href"))
                    assert response.ok
                    with ZipFile(io.BytesIO(response.body())) as archive:
                        assert archive.testzip() is None
                        names = archive.namelist()
                        assert "settings.gradle.kts" in names and "gradlew.bat" in names
                        assert not any("build/" in name or ".gradle/" in name or "local.properties" in name for name in names)
                    with page.expect_download() as download:
                        link.click()
                    assert download.value.failure() is None
                page.goto(base, wait_until="networkidle")
                page.locator(".mjs-pdf-trigger").focus()
                page.keyboard.press("Enter")
                assert page.locator(".mjs-pdf-menu").is_visible()
                page.keyboard.press("Escape")
                assert not page.locator(".mjs-pdf-menu").is_visible()
                assert page.locator(".mjs-pdf-trigger").evaluate("el => el === document.activeElement")
                assert not errors, errors
                print(f"OK UI: {len(pages)} páginas, 2 tamaños, 2 temas, {len(sections)} pares de descargas y teclado")
            finally:
                browser.close()
    finally:
        server.shutdown()
        server.server_close()


if __name__ == "__main__":
    check()
