"""Crea descargas individuales reproducibles sin cachés ni configuración privada."""
from pathlib import Path
from zipfile import ZipFile, ZIP_DEFLATED, ZipInfo
from navigation import config, find_sections

ROOT = Path(__file__).resolve().parent.parent
EXCLUDED = {"build", ".gradle", ".idea", ".git", ".kotlin", "__pycache__"}


def package():
    output = ROOT / "docs" / "descargas" / "proyectos"
    output.mkdir(parents=True, exist_ok=True)
    published = {slug for slug, _, _ in find_sections(config())}
    projects = sorted(settings for settings in (ROOT / "proyectos").rglob("settings.gradle.kts")
                      if any(settings.relative_to(ROOT / "proyectos").as_posix().startswith(slug + "/") for slug in published))
    assert projects, "No hay proyectos para empaquetar"
    for settings in projects:
        project = settings.parent
        slug = "-".join(project.relative_to(ROOT / "proyectos").parts)
        archive = output / f"{slug}.zip"
        for required in ("gradlew", "gradlew.bat", "gradle/wrapper/gradle-wrapper.jar", "README.md"):
            assert (project / required).is_file(), (slug, required)
        with ZipFile(archive, "w", compression=ZIP_DEFLATED) as target:
            for source in sorted(project.rglob("*")):
                relative = source.relative_to(project)
                if not source.is_file() or EXCLUDED.intersection(relative.parts):
                    continue
                if source.name == "local.properties" or source.suffix.lower() in {".apk", ".aab", ".jks", ".keystore", ".log"}:
                    continue
                info = ZipInfo(relative.as_posix(), date_time=(2026, 1, 1, 0, 0, 0))
                info.compress_type = ZIP_DEFLATED
                info.external_attr = (0o755 if source.name == "gradlew" else 0o644) << 16
                target.writestr(info, source.read_bytes())
        with ZipFile(archive) as check:
            assert check.testzip() is None, archive
            assert "settings.gradle.kts" in check.namelist()
            assert not any(EXCLUDED.intersection(Path(name).parts) for name in check.namelist())
        print(f"OK ZIP: {archive.relative_to(ROOT)}")


if __name__ == "__main__":
    package()
