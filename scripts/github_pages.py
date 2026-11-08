"""Configura Pages y consulta CI mediante la credencial Git existente, sin imprimirla.

Uso: python scripts/github_pages.py status|enable
`enable` solo se usa con autorización explícita de publicación.
"""
import json
import subprocess
import sys
import urllib.error
import urllib.request

REPO = "MJSPlayground/fp-dam2-pmdm-android"


def request(path, method="GET", data=None):
    credential = subprocess.run(
        ["git", "credential", "fill"], input="protocol=https\nhost=github.com\n\n",
        text=True, capture_output=True, check=True,
    )
    fields = dict(line.split("=", 1) for line in credential.stdout.splitlines() if "=" in line)
    token = fields.get("password")
    if not token:
        raise RuntimeError("No se ha obtenido una credencial GitHub del gestor Git")
    req = urllib.request.Request(
        f"https://api.github.com/repos/{REPO}/{path}",
        data=json.dumps(data).encode() if data is not None else None,
        headers={"Authorization": f"Bearer {token}", "Accept": "application/vnd.github+json", "X-GitHub-Api-Version": "2022-11-28", "User-Agent": "MJS-PMDM-QA"},
        method=method,
    )
    try:
        with urllib.request.urlopen(req, timeout=30) as response:
            return json.load(response)
    except urllib.error.HTTPError as error:
        if error.code == 404 and method == "GET":
            return None
        raise RuntimeError(f"GitHub {method} {path}: HTTP {error.code}") from None


def main():
    operation = sys.argv[1] if len(sys.argv) > 1 else "status"
    if operation not in {"status", "enable"}:
        raise SystemExit("Uso: github_pages.py status|enable")
    pages = request("pages")
    if operation == "enable":
        if pages is None:
            pages = request("pages", "POST", {"build_type": "workflow"})
        elif pages.get("build_type") != "workflow":
            pages = request("pages", "PUT", {"build_type": "workflow"})
    print("Pages:", json.dumps({key: pages.get(key) for key in ("html_url", "status", "build_type")} if pages else "sin configurar"))
    runs = request("actions/runs?per_page=5")
    for run in (runs or {}).get("workflow_runs", []):
        print(json.dumps({key: run.get(key) for key in ("id", "head_sha", "status", "conclusion", "html_url")}))


if __name__ == "__main__":
    main()
