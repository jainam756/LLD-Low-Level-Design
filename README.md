# Jainam's Low-Level Design Revision

Java implementations with requirements, responsibilities, diagrams, complete execution flows, trade-offs, and interview notes.

| Problem | Implementation and notes |
| --- | --- |
| 01. Tic-Tac-Toe | [Open revision package](01-tic-tac-toe/README.md) |
| 02. Parking Lot | [Open revision package](02-parking-lot/README.md) |
| 03. Snake and Ladder | [Open revision package](03-snake-and-ladder/README.md) |

Requires **JDK 17 or newer**. No external libraries or build-tool downloads are needed.

From this repository folder in PowerShell:

```powershell
./01-tic-tac-toe/run.ps1 -TestOnly
./01-tic-tac-toe/run.ps1
./02-parking-lot/run.ps1 -TestOnly
./02-parking-lot/run.ps1
./03-snake-and-ladder/run.ps1 -TestOnly
./03-snake-and-ladder/run.ps1
```

## GitHub setup and manual push

This folder is a standalone Git repository with branch `main` and origin:
`https://github.com/jainam756/LLD-Low-Level-Design.git`.
Changes are committed locally. Push manually when ready.

```powershell
git status
git remote -v
git push -u origin main
```

Authenticate with your own GitHub account when prompted. If the GitHub repository
already contains commits, fetch and reconcile that history before pushing; do not
force-push over existing work. Git user name/email must be configured for a commit.

For future problems, add numbered folders with the same documentation sections.
