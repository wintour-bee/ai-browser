# AI Browser - 一键构建脚本
# 自动安装依赖并构建 Android 应用

param(
    [switch]$SkipJava,
    [switch]$SkipAndroid,
    [switch]$BackendOnly,
    [switch]$AndroidOnly
)

$ErrorActionPreference = "Stop"
$projectRoot = $PSScriptRoot | Split-Path -Parent

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  AI Browser - 自动构建脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

function Test-Command {
    param($command)
    try {
        Get-Command $command -ErrorAction Stop | Out-Null
        return $true
    } catch {
        return $false
    }
}

function Install-Java {
    Write-Host "[1/4] 检查 Java 环境..." -ForegroundColor Yellow
    
    if (Test-Command "java") {
        $javaVersion = java -version 2>&1 | Select-Object -First 1
        Write-Host "✓ Java 已安装: $javaVersion" -ForegroundColor Green
        return
    }
    
    if (-not (Test-Command "choco")) {
        Write-Host "  安装 Chocolatey..." -ForegroundColor Gray
        Set-ExecutionPolicy Bypass -Scope Process -Force
        [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
        Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
    }
    
    Write-Host "  安装 OpenJDK 17..." -ForegroundColor Gray
    choco install openjdk17 -y --no-progress
    
    # 设置环境变量
    $javaHome = "C:\Program Files\Eclipse Adoptium\jdk-17.0.9.9-hotspot"
    if (Test-Path $javaHome) {
        $env:JAVA_HOME = $javaHome
        $env:PATH = "$javaHome\bin;$env:PATH"
        [Environment]::SetEnvironmentVariable("JAVA_HOME", $javaHome, [EnvironmentVariableTarget]::User)
        Write-Host "✓ Java 17 安装完成" -ForegroundColor Green
    }
}

function Install-AndroidSDK {
    Write-Host "[2/4] 检查 Android SDK..." -ForegroundColor Yellow
    
    if ($env:ANDROID_HOME) {
        Write-Host "✓ Android SDK 已配置: $env:ANDROID_HOME" -ForegroundColor Green
        return
    }
    
    $androidSdkPath = "C:\Android\android-sdk"
    if (Test-Path "$androidSdkPath\platforms\android-34") {
        $env:ANDROID_HOME = $androidSdkPath
        $env:ANDROID_SDK_ROOT = $androidSdkPath
        Write-Host "✓ Android SDK 已安装" -ForegroundColor Green
        return
    }
    
    Write-Host "  安装 Android SDK..." -ForegroundColor Gray
    
    # 创建目录
    New-Item -ItemType Directory -Path $androidSdkPath -Force | Out-Null
    
    # 下载 command line tools
    $cmdlineToolsUrl = "https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip"
    $cmdlineToolsZip = "$env:TEMP\cmdline-tools.zip"
    
    Write-Host "  下载 Android SDK command line tools..." -ForegroundColor Gray
    Invoke-WebRequest -Uri $cmdlineToolsUrl -OutFile $cmdlineToolsZip -UseBasicParsing
    
    # 解压
    Expand-Archive -Path $cmdlineToolsZip -DestinationPath "$androidSdkPath\cmdline-tools-temp" -Force
    New-Item -ItemType Directory -Path "$androidSdkPath\cmdline-tools" -Force | Out-Null
    Move-Item -Path "$androidSdkPath\cmdline-tools-temp\cmdline-tools" -Destination "$androidSdkPath\cmdline-tools\latest" -Force
    Remove-Item -Path "$androidSdkPath\cmdline-tools-temp" -Recurse -Force
    Remove-Item -Path $cmdlineToolsZip -Force
    
    # 设置环境变量
    $env:ANDROID_HOME = $androidSdkPath
    $env:ANDROID_SDK_ROOT = $androidSdkPath
    [Environment]::SetEnvironmentVariable("ANDROID_HOME", $androidSdkPath, [EnvironmentVariableTarget]::User)
    [Environment]::SetEnvironmentVariable("ANDROID_SDK_ROOT", $androidSdkPath, [EnvironmentVariableTarget]::User)
    
    # 安装必要的包
    Write-Host "  安装 Android SDK packages..." -ForegroundColor Gray
    & "$androidSdkPath\cmdline-tools\latest\bin\sdkmanager.bat" --licenses | Out-Null
    & "$androidSdkPath\cmdline-tools\latest\bin\sdkmanager.bat" "platform-tools" "platforms;android-34" "build-tools;34.0.0" | Out-Null
    
    Write-Host "✓ Android SDK 安装完成" -ForegroundColor Green
}

function Install-NodeJS {
    Write-Host "[3/4] 检查 Node.js 环境..." -ForegroundColor Yellow
    
    if (Test-Command "node") {
        $nodeVersion = node --version
        Write-Host "✓ Node.js 已安装: $nodeVersion" -ForegroundColor Green
        return
    }
    
    if (-not (Test-Command "choco")) {
        Write-Host "  安装 Chocolatey..." -ForegroundColor Gray
        Set-ExecutionPolicy Bypass -Scope Process -Force
        [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
        Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
    }
    
    Write-Host "  安装 Node.js 18..." -ForegroundColor Gray
    choco install nodejs --version=18.19.0 -y --no-progress
    
    Write-Host "✓ Node.js 安装完成" -ForegroundColor Green
}

function Build-Android {
    Write-Host "[4/4] 构建 Android 应用..." -ForegroundColor Yellow
    
    Set-Location "$projectRoot\app"
    
    # 创建 local.properties
    $localProps = "sdk.dir=$env:ANDROID_HOME"
    Set-Content -Path "$projectRoot\local.properties" -Value $localProps
    
    # 清理并构建
    Write-Host "  清理项目..." -ForegroundColor Gray
    .\gradlew.bat clean
    
    Write-Host "  构建 Debug APK..." -ForegroundColor Gray
    .\gradlew.bat assembleDebug
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Android 应用构建成功!" -ForegroundColor Green
        Write-Host "  APK 位置: $projectRoot\app\build\outputs\apk\debug\app-debug.apk" -ForegroundColor White
    } else {
        Write-Host "✗ Android 应用构建失败!" -ForegroundColor Red
        exit 1
    }
}

function Build-Backend {
    Write-Host "构建后端服务..." -ForegroundColor Yellow
    
    Set-Location "$projectRoot\backend"
    
    # 安装依赖
    Write-Host "  安装 Node.js 依赖..." -ForegroundColor Gray
    npm install
    
    # 复制环境变量
    if (-not (Test-Path "$projectRoot\backend\.env")) {
        Copy-Item "$projectRoot\.env.example" "$projectRoot\backend\.env"
        Write-Host "  请编辑 $projectRoot\backend\.env 文件配置环境变量" -ForegroundColor Yellow
    }
    
    # 构建
    Write-Host "  构建后端..." -ForegroundColor Gray
    npm run build
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ 后端服务构建成功!" -ForegroundColor Green
    } else {
        Write-Host "✗ 后端服务构建失败!" -ForegroundColor Red
        exit 1
    }
}

function Start-Backend {
    Write-Host "启动后端服务..." -ForegroundColor Yellow
    
    Set-Location "$projectRoot\backend"
    
    if (Test-Path ".env") {
        npm start
    } else {
        Write-Host "请先配置 .env 文件" -ForegroundColor Red
        exit 1
    }
}

# 主流程
try {
    # Android 构建
    if (-not $BackendOnly) {
        if (-not $SkipJava) { Install-Java }
        if (-not $SkipAndroid) { Install-AndroidSDK }
        Build-Android
    }
    
    # 后端构建
    if (-not $AndroidOnly) {
        Install-NodeJS
        Build-Backend
    }
    
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "  构建完成!" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "下一步:" -ForegroundColor Yellow
    Write-Host "  1. 配置后端环境变量 (.env)" -ForegroundColor White
    Write-Host "  2. 启动后端服务: cd backend && npm start" -ForegroundColor White
    Write-Host "  3. 安装 APK 到设备: adb install app\build\outputs\apk\debug\app-debug.apk" -ForegroundColor White
    Write-Host ""
    
} catch {
    Write-Host ""
    Write-Host "错误: $_" -ForegroundColor Red
    Write-Host $_.ScriptStackTrace -ForegroundColor Red
    exit 1
}
