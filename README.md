# Jainam's Low-Level Design Revision

Java implementations with requirements, responsibilities, diagrams, complete execution flows, trade-offs, and interview notes.

| Problem | Implementation and notes |
| --- | --- |
| 01. Tic-Tac-Toe | [Open revision package](01-tic-tac-toe/README.md) |

Requires **JDK 17 or newer**. No external libraries or build-tool downloads are needed.

From this repository folder in PowerShell:

```powershell
./01-tic-tac-toe/run.ps1 -TestOnly
./01-tic-tac-toe/run.ps1
```

## GitHub setup and manual push

This folder is a standalone Git repository with branch `main` and origin:
`https://github.com/jainam756/LLD-Low-Level-Design.git`.
No push has been performed. Files are initially uncommitted so you can review them.

```powershell
git status
git remote -v
git add .
git commit -m "Add Java Tic-Tac-Toe LLD revision package"
git push -u origin main
```

Authenticate with your own GitHub account when prompted. If the GitHub repository
already contains commits, fetch and reconcile that history before pushing; do not
force-push over existing work. Git user name/email must be configured for a commit.

For future problems, add numbered folders with the same documentation sections.
