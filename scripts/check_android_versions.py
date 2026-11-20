"""Comprueba coherencia de los catálogos independientes, SDK y wrappers del curso."""
from pathlib import Path
import hashlib
import re
import subprocess
import tomllib

ROOT = Path(__file__).resolve().parent.parent


def main():
    # Git evita recorrer builds y node_modules y no incluye proyectos temporales de QA.
    tracked = subprocess.check_output(
        ["git", "ls-files", "proyectos/**/settings.gradle.kts"], cwd=ROOT, text=True
    ).splitlines()
    assert tracked, "No hay proyectos Android versionados"
    common = {}
    for settings in tracked:
        project = ROOT / settings.removesuffix("settings.gradle.kts")
        catalog = tomllib.loads((project / "gradle/libs.versions.toml").read_text(encoding="utf-8"))
        values = {"version:" + key: value for key, value in catalog["versions"].items()}
        for library in catalog.get("libraries", {}).values():
            if isinstance(library.get("version"), str):
                values["library:" + library["module"]] = library["version"]
        for name in ("gradlew", "gradlew.bat", "gradle/wrapper/gradle-wrapper.jar"):
            data = (project / name).read_bytes()
            if name != "gradle/wrapper/gradle-wrapper.jar":
                data = data.replace(b"\r\n", b"\n")
            values["wrapper:" + name] = hashlib.sha256(data).hexdigest()
        values["wrapper:properties"] = (project / "gradle/wrapper/gradle-wrapper.properties").read_text()
        criteria = (project / "gradle/gradle-daemon-jvm.properties").read_text(encoding="utf-8")
        assert "toolchainUrl" not in criteria, settings
        values["daemon:jdk"] = criteria
        app = (project / "app/build.gradle.kts").read_text(encoding="utf-8")
        top = (project / "build.gradle.kts").read_text(encoding="utf-8")
        for name in ("compileSdk", "targetSdk", "minSdk"):
            match = re.search(rf"\b{name}\s*=\s*(\d+)", app)
            assert match, (settings, name)
            values[name] = match[1]
        for name in ("sourceCompatibility", "targetCompatibility"):
            match = re.search(rf"{name}\s*=\s*JavaVersion.VERSION_(\d+)", app)
            assert match, (settings, name)
            values[name] = match[1]
        assert "org.jetbrains.kotlin.android" not in str(catalog.get("plugins", {})), settings
        assert "libs.plugins.kotlin.android" not in top + app, settings
        assert "org.gradle.java.home" not in (project / "gradle.properties").read_text(), settings
        for key, value in values.items():
            if key in common:
                assert common[key][0] == value, f"{settings}: {key}={value}; difiere de {common[key][1]}"
            else:
                common[key] = (value, settings)
    resources = (ROOT / "docs/recursos/index.md").read_text(encoding="utf-8")
    rows = {parts[1].strip(): parts[2].strip() for line in resources.splitlines()
            if line.startswith("|") and len(parts := line.split("|")) >= 4}
    documented = {"agp": "Android Gradle Plugin", "kotlin": "Kotlin y plugin Compose",
                  "compose-bom": "Compose BOM", "activity": "Activity Compose",
                  "lifecycle": "Lifecycle", "navigation": "Navigation Compose",
                  "room": "Room", "ksp": "KSP", "media": "Media3",
                  "maplibre": "MapLibre Native Android", "hilt": "Hilt"}
    for key, row in documented.items():
        assert rows[row] == common["version:" + key][0], f"Recursos: versión incoherente de {row}"
    assert rows["Espresso Core (instrumentación Compose)"] == common["library:androidx.test.espresso:espresso-core"][0]
    print(f"OK Android: {len(tracked)} proyectos; catálogos, SDK, bytecode y wrappers coherentes")


if __name__ == "__main__":
    main()
