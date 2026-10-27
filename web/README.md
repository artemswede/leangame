# Lean Bicycle Factory — browser build

This is the local browser implementation. It uses no package manager or backend.

```bash
cd web
python3 -m http.server 8080
```

Open `http://localhost:8080` in a browser. The core loop is: introduction, timed production, round result, two-change editing phase, move confirmation, and final score after six rounds.
