"""Evita que PDF y menú confundan pestañas agrupadoras con temas."""
from navigation import config, find_sections, section_navigation


def test_nested():
    project = {
        "nav": [
            {"Inicio": "index.md"},
            {"Itinerario": [{"Tema": ["ecosistema/index.md", "ecosistema/entorno.md"]}]},
            {"Ampliaciones": ["ampliaciones/index.md", {"Kotlin": ["ampliaciones/kotlin/index.md", "ampliaciones/kotlin/flow.md"]}]},
        ],
        "extra": {
            "pdf_sections": ["ecosistema", "ampliaciones/kotlin"],
            "pdf_titles": {"ecosistema": "Ecosistema", "ampliaciones/kotlin": "Kotlin avanzado"},
        },
    }
    sections = find_sections(project)
    assert [slug for slug, _, _ in sections] == ["ecosistema", "ampliaciones/kotlin"]
    assert sections[1][2] == ["ampliaciones/kotlin/index.md", "ampliaciones/kotlin/flow.md"]
    assert section_navigation(project, "ecosistema") == ["ecosistema/index.md", "ecosistema/entorno.md"]


if __name__ == "__main__":
    test_nested()
    result = find_sections(config())
    print(f"OK navegación jerárquica: {len(result)} temas PDF")
