"""Navegación compartida por PDF, menú de descargas y comprobaciones."""
from __future__ import annotations

import json
from pathlib import Path
import tomllib

ROOT = Path(__file__).resolve().parent.parent


def config():
    with (ROOT / "zensical.toml").open("rb") as source:
        return tomllib.load(source)["project"]


def flatten_nav(node):
    if isinstance(node, str):
        return [node] if node.endswith(".md") else []
    if isinstance(node, dict):
        return [page for value in node.values() for page in flatten_nav(value)]
    return [page for item in node for page in flatten_nav(item)]


def section_navigation(project, slug):
    """Devuelve la rama más profunda que contiene el índice del tema."""
    wanted = f"{slug}/index.md"

    def visit(node):
        if not isinstance(node, list):
            return None
        for item in node:
            if isinstance(item, dict):
                for child in item.values():
                    result = visit(child)
                    if result is not None:
                        return result
        if wanted in flatten_nav(node):
            return node
        return None

    branch = visit(project["nav"])
    if branch is None:
        raise ValueError(f"No existe navegación para {slug}")
    return branch


def find_sections(project):
    """Los temas se declaran una sola vez y se ordenan como la navegación."""
    requested = set(project.get("extra", {}).get("pdf_sections", []))
    titles = project.get("extra", {}).get("pdf_titles", {})
    result = []
    for source in flatten_nav(project["nav"]):
        slug = Path(source).parent.as_posix()
        if Path(source).name != "index.md" or slug not in requested:
            continue
        pages = [p for p in flatten_nav(section_navigation(project, slug)) if p.startswith(slug + "/")]
        if slug not in titles:
            raise ValueError(f"Falta el título PDF de {slug}")
        result.append((slug, titles[slug], pages))
    found = {slug for slug, _, _ in result}
    if found != requested:
        raise ValueError(f"Temas PDF sin índice en navegación: {requested - found}")
    if not result:
        raise ValueError("No hay temas publicables en PDF")
    return result


def write_pdf_manifest(project):
    payload = [{"id": slug, "file": slug.replace("/", "-") + ".pdf", "label": title} for slug, title, _ in find_sections(project)]
    destination = ROOT / "docs" / "javascripts" / "pdf-sections.js"
    destination.write_text("// Generado desde zensical.toml; no editar.\nwindow.MJS_PDF_SECTIONS = " + json.dumps(payload, ensure_ascii=False) + ";\n", encoding="utf-8")


def complete_sections(project):
    """Incluye también portada y avisos de construcción, en el orden de nav."""
    topics = find_sections(project)
    owner = {source: topic for topic in topics for source in topic[2]}
    result = []
    seen = set()
    for source in flatten_nav(project["nav"]):
        if source in owner:
            topic = owner[source]
            if topic[0] not in seen:
                result.append(topic)
                seen.add(topic[0])
        else:
            slug = "inicio" if source == "index.md" else Path(source).with_suffix("").as_posix().replace("/index", "")
            lines = (ROOT / "docs" / source).read_text(encoding="utf-8").splitlines()
            title = next(line[2:].strip() for line in lines if line.startswith("# "))
            result.append((slug, title, [source]))
    return result


if __name__ == "__main__":
    write_pdf_manifest(config())
