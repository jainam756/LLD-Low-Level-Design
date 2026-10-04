param([switch]$TestOnly)
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    New-Item -ItemType Directory -Force out | Out-Null
    $javaSources = @(Get-ChildItem src/main/java,src/test/java -Recurse -Filter *.java | ForEach-Object FullName)
    & javac --release 17 -d out $javaSources
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed' }
    & java -cp out parkinglot.ParkingLotTest
    if ($LASTEXITCODE -ne 0) { throw 'Tests failed' }
    if (!$TestOnly) { & java -cp out parkinglot.Main }
} finally { Pop-Location }
