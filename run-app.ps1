# Servicegest Companion App - Run Script (PowerShell)
# Launches the API Health Monitor application

param(
    [switch]$Console = $false
)

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Servicegest Companion App" -ForegroundColor Cyan
Write-Host "API Health Monitor" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

$jarPath = Join-Path (Get-Location) "target\companion-app-all.jar"

if (-not (Test-Path $jarPath)) {
    Write-Host "ERROR: JAR file not found at: $jarPath" -ForegroundColor Red
    Write-Host ""
    Write-Host "Solutions:" -ForegroundColor Yellow
    Write-Host "1. Run build-all.bat to build the application"
    Write-Host "2. Ensure you're in the companion2 directory"
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Launching application from: $jarPath" -ForegroundColor Green
Write-Host ""

try {
    if ($Console) {
        # Run with console window
        & java -jar $jarPath
    } else {
        # Run without console window (background)
        Start-Process -FilePath "javaw.exe" -ArgumentList "-jar `"$jarPath`"" -WindowStyle Hidden -ErrorAction Stop
        Write-Host "Application launched successfully!" -ForegroundColor Green
    }
} catch {
    Write-Host ""
    Write-Host "ERROR: Failed to launch application" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Possible issues:" -ForegroundColor Yellow
    Write-Host "1. Java 17+ not installed or not in PATH"
    Write-Host "2. JAR file corrupted"
    Write-Host ""
    Write-Host "Solutions:" -ForegroundColor Yellow
    Write-Host "1. Download Java from https://adoptium.net/"
    Write-Host "2. Run: build-all.bat"
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}
