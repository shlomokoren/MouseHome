$ErrorActionPreference = "Stop"
$project = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $project

if (-not (Test-Path ".\local.properties")) {
    "sdk.dir=C\:\\Android" | Set-Content ".\local.properties" -Encoding ASCII
}

$gradle = Join-Path $project ".build-tools\gradle-8.7\bin\gradle.bat"
if (-not (Test-Path $gradle)) {
    $zip = Join-Path $project ".build-tools\gradle-8.7-bin.zip"
    New-Item -ItemType Directory -Force (Split-Path $zip) | Out-Null
    Invoke-WebRequest "https://services.gradle.org/distributions/gradle-8.7-bin.zip" -OutFile $zip
    Expand-Archive $zip (Join-Path $project ".build-tools") -Force
}
Write-Host "`nBuilding MouseHome v3.8...`n"
& $gradle --no-daemon assembleDebug
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Write-Host "`nBUILD SUCCESSFUL"
Write-Host "`nAPK:"
Write-Host (Join-Path $project "app\build\outputs\apk\debug\app-debug.apk")
