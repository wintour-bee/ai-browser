# AI Browser - Quick Environment Check
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "AI Browser Environment Check" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$issues = 0
$warnings = 0

# Java Check
Write-Host "[1/5] Java Check:" -ForegroundColor Yellow
$javaPath = "C:\Program Files\Java\jdk-17\bin\java.exe"
if (Test-Path $javaPath) {
    & $javaPath -version 2>&1 | Select-Object -First 1
    Write-Host "PASS: Java JDK 17 installed" -ForegroundColor Green
} else {
    Write-Host "FAIL: Java JDK 17 not found at $javaPath" -ForegroundColor Red
    $issues++
}

# Gradle Check
Write-Host ""
Write-Host "[2/5] Gradle Check:" -ForegroundColor Yellow
$gradlew = "C:\Users\winto\CodeBuddy\20260509144732\app\gradlew.bat"
if (Test-Path $gradlew) {
    Write-Host "PASS: Gradle Wrapper found" -ForegroundColor Green
} else {
    Write-Host "FAIL: Gradle Wrapper not found" -ForegroundColor Red
    $issues++
}

# Android SDK Check
Write-Host ""
Write-Host "[3/5] Android SDK Check:" -ForegroundColor Yellow
$androidHome = "C:\Users\winto\AppData\Local\Android\Sdk"
if (Test-Path $androidHome) {
    Write-Host "PASS: Android SDK found at $androidHome" -ForegroundColor Green
} else {
    Write-Host "FAIL: Android SDK not found. Install with: winget install Google.AndroidSdk" -ForegroundColor Red
    $issues++
}

# Node.js Check
Write-Host ""
Write-Host "[4/5] Node.js Check:" -ForegroundColor Yellow
$node = Get-Command node -ErrorAction SilentlyContinue
if ($node) {
    $nodeVersion = & node --version
    Write-Host "PASS: Node.js $nodeVersion" -ForegroundColor Green
} else {
    Write-Host "WARN: Node.js not found (needed for backend)" -ForegroundColor Yellow
    $warnings++
}

# Project Files Check
Write-Host ""
Write-Host "[5/5] Project Files Check:" -ForegroundColor Yellow
$projectRoot = "C:\Users\winto\CodeBuddy\20260509144732"
$files = @(
    "app\build.gradle.kts",
    "settings.gradle.kts",
    "gradle\wrapper\gradle-wrapper.properties"
)

$allFound = $true
foreach ($f in $files) {
    $path = Join-Path $projectRoot $f
    if (Test-Path $path) {
        Write-Host "  Found: $f" -ForegroundColor Green
    } else {
        Write-Host "  Missing: $f" -ForegroundColor Red
        $allFound = $false
    }
}

if ($allFound) {
    Write-Host "PASS: All project files present" -ForegroundColor Green
} else {
    Write-Host "FAIL: Some project files missing" -ForegroundColor Red
    $issues++
}

# Summary
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Summary" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Issues: $issues" -ForegroundColor $(if ($issues -eq 0) { "Green" } else { "Red" })
Write-Host "Warnings: $warnings" -ForegroundColor $(if ($warnings -eq 0) { "Green" } else { "Yellow" })
Write-Host ""

if ($issues -eq 0) {
    Write-Host "Environment is ready for building!" -ForegroundColor Green
    Write-Host "Run: .\scripts\build-android-app.ps1" -ForegroundColor Cyan
} else {
    Write-Host "Please install missing tools first." -ForegroundColor Yellow
}
