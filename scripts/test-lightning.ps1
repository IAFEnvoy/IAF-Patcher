param(
    [ValidateSet('1.16.5', '1.18.2', '1.19.2', '1.20.1')]
    [string]$MinecraftVersion = '1.20.1'
)

$ErrorActionPreference = 'Stop'
$projectDir = Split-Path -Parent $PSScriptRoot
Push-Location -LiteralPath $projectDir
try {
    $regressionDir = Join-Path $projectDir "build/lightning-regression/$MinecraftVersion"
    New-Item -ItemType Directory -Force -Path $regressionDir | Out-Null
    Set-Content -LiteralPath (Join-Path $regressionDir 'eula.txt') -Value 'eula=true' -Encoding ASCII
    @'
server-ip=127.0.0.1
server-port=0
online-mode=false
level-name=world
level-type=flat
generate-structures=false
spawn-animals=false
spawn-monsters=false
spawn-npcs=false
max-tick-time=-1
view-distance=2
simulation-distance=2
level-seed=1
'@ | Set-Content -LiteralPath (Join-Path $regressionDir 'server.properties') -Encoding ASCII
    $resultFile = Join-Path $regressionDir 'patch-regression-result.txt'
    Remove-Item -LiteralPath $resultFile -Force -ErrorAction SilentlyContinue
    & .\gradlew.bat -PlightningRegression ":${MinecraftVersion}:runServer" -x ":${MinecraftVersion}:downloadAssets" '-Dorg.gradle.jvmargs=-Xmx2G' '-Dnet.minecraftforge.gradle.check.certs=false' --no-parallel --max-workers=2 --offline --console=plain
    if ($LASTEXITCODE -ne 0) { throw "Gradle failed for $MinecraftVersion" }
    if (!(Test-Path -LiteralPath $resultFile)) { throw 'Server did not reach the regression checks; inspect its log.' }
    $result = Get-Content -LiteralPath $resultFile -Raw
    $expectedChecks = 12
    if ($result -match 'FAIL:' -or ([regex]::Matches($result, '(?m)^PASS: lightning:')).Count -ne $expectedChecks) {
        throw "Lightning regression failed:`n$result"
    }
    if ($MinecraftVersion -eq '1.16.5' -and $result -notmatch '(?m)^PASS: mapping:') {
        throw "Legacy mapping checks did not run:`n$result"
    }
    $result
} finally {
    Pop-Location
}
