param(
    [ValidateSet('1.18.2', '1.19.2', '1.20.1')]
    [string]$MinecraftVersion = '1.20.1'
)

$ErrorActionPreference = 'Stop'
$projectDir = Split-Path -Parent $PSScriptRoot
Push-Location -LiteralPath $projectDir
try {
    # This directory and its world are disposable test fixtures, separate from normal run directories.
    $regressionDir = Join-Path $projectDir "build/feature-regression/$MinecraftVersion"
    $packDir = Join-Path $regressionDir 'world/datapacks/iafp-feature-test'
    $tagDir = Join-Path $packDir 'data/iceandfire/tags/items'
    New-Item -ItemType Directory -Force -Path $tagDir | Out-Null
    Set-Content -LiteralPath (Join-Path $regressionDir 'eula.txt') -Value 'eula=true' -Encoding ASCII
    $packFormat = switch ($MinecraftVersion) { '1.18.2' { 9 } '1.19.2' { 10 } '1.20.1' { 15 } }
    @{ pack = @{ pack_format = $packFormat; description = 'IAF Patcher feature regression fixtures' } } |
        ConvertTo-Json -Depth 4 | Set-Content -LiteralPath (Join-Path $packDir 'pack.mcmeta') -Encoding ASCII
    $foods = @{
        breed_ampithere = @('minecraft:emerald')
        breed_hippocampus = @('minecraft:emerald')
        breed_hippogryph = @('minecraft:emerald')
        heal_ampithere = @('minecraft:diamond')
        heal_cockatrice = @('minecraft:diamond')
        heal_hippocampus = @('minecraft:diamond')
        heal_pixie = @('minecraft:emerald')
        tame_hippogryph = @('minecraft:diamond')
        # The older patch regression suite also uses cake as a search fixture.
        tame_pixie = @('minecraft:diamond', 'minecraft:cake')
    }
    foreach ($food in $foods.GetEnumerator()) {
        @{ replace = $true; values = @($food.Value) } | ConvertTo-Json -Depth 3 |
            Set-Content -LiteralPath (Join-Path $tagDir "$($food.Key).json") -Encoding ASCII
    }
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
    & .\gradlew.bat -PfeatureRegression ":${MinecraftVersion}:runServer" -x ":${MinecraftVersion}:downloadAssets" --offline --console=plain
    if ($LASTEXITCODE -ne 0) { throw "Gradle failed for $MinecraftVersion" }
    if (!(Test-Path -LiteralPath $resultFile)) { throw 'Server did not reach the regression checks; inspect its log.' }
    $result = Get-Content -LiteralPath $resultFile -Raw
    if ($result -match 'FAIL:' -or ([regex]::Matches($result, '(?m)^PASS: feature:')).Count -ne 9) {
        throw "Feature regression did not pass all nine checks:`n$result"
    }
    $result
} finally {
    Pop-Location
}
