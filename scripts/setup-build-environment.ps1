# AI Browser Android App - Build Environment Setup Script
# Run this script as Administrator to set up the build environment

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "  AI Browser App - Build Setup" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# Check if running as Administrator
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
if (-not $isAdmin) {
    Write-Host "Please run this script as Administrator!" -ForegroundColor Red
    exit 1
}

# Install Chocolatey
Write-Host "[1/5] Installing Chocolatey package manager..." -ForegroundColor Yellow
if (-not (Get-Command choco -ErrorAction SilentlyContinue)) {
    Set-ExecutionPolicy Bypass -Scope Process -Force
    [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
    Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
}
Write-Host "Chocolatey installed successfully!" -ForegroundColor Green

# Install Java JDK 17
Write-Host "[2/5] Installing OpenJDK 17..." -ForegroundColor Yellow
choco install openjdk17 -y --no-progress
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.9.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
Write-Host "OpenJDK 17 installed successfully!" -ForegroundColor Green

# Install Android SDK
Write-Host "[3/5] Installing Android SDK..." -ForegroundColor Yellow
$androidSdkPath = "C:\Android\android-sdk"
if (-not (Test-Path $androidSdkPath)) {
    New-Item -ItemType Directory -Path $androidSdkPath -Force | Out-Null
    
    # Download command line tools
    $cmdlineToolsUrl = "https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip"
    $cmdlineToolsZip = "$env:TEMP\cmdline-tools.zip"
    Write-Host "Downloading Android SDK command line tools..."
    Invoke-WebRequest -Uri $cmdlineToolsUrl -OutFile $cmdlineToolsZip -UseBasicParsing
    
    # Extract
    Expand-Archive -Path $cmdlineToolsZip -DestinationPath "$androidSdkPath\cmdline-tools-temp" -Force
    New-Item -ItemType Directory -Path "$androidSdkPath\cmdline-tools" -Force | Out-Null
    Move-Item -Path "$androidSdkPath\cmdline-tools-temp\cmdline-tools" -Destination "$androidSdkPath\cmdline-tools\latest" -Force
    Remove-Item -Path "$androidSdkPath\cmdline-tools-temp" -Recurse -Force
    Remove-Item -Path $cmdlineToolsZip -Force
    
    # Set environment variables
    [Environment]::SetEnvironmentVariable("ANDROID_HOME", $androidSdkPath, [EnvironmentVariableTarget]::User)
    [Environment]::SetEnvironmentVariable("ANDROID_SDK_ROOT", $androidSdkPath, [EnvironmentVariableTarget]::User)
    $env:ANDROID_HOME = $androidSdkPath
    $env:ANDROID_SDK_ROOT = $androidSdkPath
    $env:PATH = "$androidSdkPath\cmdline-tools\latest\bin;$androidSdkPath\platform-tools;$env:PATH"
    
    # Accept licenses and install required packages
    Write-Host "Installing Android SDK packages (build-tools, platforms, etc.)..."
    & "$androidSdkPath\cmdline-tools\latest\bin\sdkmanager.bat" --licenses | Out-Null
    & "$androidSdkPath\cmdline-tools\latest\bin\sdkmanager.bat" "platform-tools" "platforms;android-34" "build-tools;34.0.0" "ndk;26.1.10909125" | Out-Null
}

Write-Host "Android SDK installed successfully!" -ForegroundColor Green

# Install Gradle
Write-Host "[4/5] Installing Gradle 8.4..." -ForegroundColor Yellow
if (-not (Get-Command gradle -ErrorAction SilentlyContinue)) {
    choco install gradle -y --version=8.4 --no-progress
}
Write-Host "Gradle installed successfully!" -ForegroundColor Green

# Update local.properties
Write-Host "[5/5] Configuring project files..." -ForegroundColor Yellow
$projectRoot = Split-Path -Parent $PSScriptRoot
$localProps = "sdk.dir=$androidSdkPath`nsdk.dir=$androidSdkPath"
Set-Content -Path "$projectRoot\local.properties" -Value $localProps
Write-Host "local.properties created!" -ForegroundColor Green

Write-Host ""
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "  Setup Complete!" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Environment Variables Set:" -ForegroundColor Green
Write-Host "  JAVA_HOME: $env:JAVA_HOME" -ForegroundColor White
Write-Host "  ANDROID_HOME: $androidSdkPath" -ForegroundColor White
Write-Host "  ANDROID_SDK_ROOT: $androidSdkPath" -ForegroundColor White
Write-Host ""
Write-Host "Please restart your terminal and run:" -ForegroundColor Yellow
Write-Host "  cd $projectRoot" -ForegroundColor White
Write-Host "  cd app" -ForegroundColor White
Write-Host "  .\gradlew.bat assembleDebug" -ForegroundColor White
Write-Host ""
