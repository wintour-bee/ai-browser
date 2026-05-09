# AI Browser - 一键构建脚本
# 自动安装依赖并构建 Android 应用

param(
    [switch]$SkipInstall,      # 跳过安装步骤
    [switch]$CleanBuild,       # 清理后重新构建
    [switch]$DebugBuild,       # Debug 版本
    [switch]$ReleaseBuild      # Release 版本
)

$ErrorActionPreference = "Continue"
$projectRoot = "C:\Users\winto\CodeBuddy\20260509144732"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  AI Browser 一键构建脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检测 Java
Write-Host "[1/5] 检测 Java 环境..." -ForegroundColor Yellow
$javaCmd = Get-Command java -ErrorAction SilentlyContinue
if (-not $javaCmd) {
    $javaPath = "C:\Program Files\Java\jdk-17\bin\java.exe"
    if (Test-Path $javaPath) {
        Write-Host "  ✓ Java JDK 17 已安装: $javaPath" -ForegroundColor Green
        $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
        $env:Path += ";$env:JAVA_HOME\bin"
    } else {
        Write-Host "  ✗ Java 未安装" -ForegroundColor Red
        Write-Host "  请先运行: .\scripts\setup-build-environment.ps1" -ForegroundColor Yellow
        exit 1
    }
} else {
    & java -version 2>&1 | Select-Object -First 1
    Write-Host "  ✓ Java 已就绪" -ForegroundColor Green
}

# 检测 Android SDK
Write-Host ""
Write-Host "[2/5] 检测 Android SDK..." -ForegroundColor Yellow
$androidHome = $env:ANDROID_HOME
if (-not $androidHome) {
    $androidHome = "C:\Users\winto\AppData\Local\Android\Sdk"
}

if (Test-Path $androidHome) {
    Write-Host "  ✓ Android SDK 已安装: $androidHome" -ForegroundColor Green
    $env:ANDROID_HOME = $androidHome
} else {
    Write-Host "  ⚠ Android SDK 未检测到" -ForegroundColor Yellow
    if (-not $SkipInstall) {
        Write-Host "  正在尝试安装 Android SDK..." -ForegroundColor Yellow
        winget install --id Google.AndroidSdk -e --accept-package-agreements --accept-source-agreements 2>&1 | Out-Null
        if ($?) {
            Write-Host "  ✓ Android SDK 安装成功" -ForegroundColor Green
            $env:ANDROID_HOME = $androidHome
        } else {
            Write-Host "  ✗ Android SDK 安装失败，请手动安装" -ForegroundColor Red
        }
    }
}

# 检测 Gradle
Write-Host ""
Write-Host "[3/5] 检测 Gradle..." -ForegroundColor Yellow
$gradleCmd = Get-Command gradle -ErrorAction SilentlyContinue
if (-not $gradleCmd) {
    $gradlePath = "$env:USERPROFILE\.gradle\gradle-8.4\bin\gradle.bat"
    if (Test-Path $gradlePath) {
        Write-Host "  ✓ Gradle 8.4 已找到: $gradlePath" -ForegroundColor Green
    } else {
        Write-Host "  ⚠ Gradle 未安装，将使用 Gradle Wrapper" -ForegroundColor Yellow
        Write-Host "  (项目已包含 gradlew.bat)" -ForegroundColor Gray
    }
} else {
    & gradle --version 2>&1 | Select-Object -First 1
    Write-Host "  ✓ Gradle 已就绪" -ForegroundColor Green
}

# 检测 Node.js (后端)
Write-Host ""
Write-Host "[4/5] 检测 Node.js..." -ForegroundColor Yellow
$nodeCmd = Get-Command node -ErrorAction SilentlyContinue
if ($nodeCmd) {
    & node --version 2>&1
    Write-Host "  ✓ Node.js 已就绪" -ForegroundColor Green
} else {
    Write-Host "  ⚠ Node.js 未安装（构建后端需要）" -ForegroundColor Yellow
}

# 构建 Android 应用
Write-Host ""
Write-Host "[5/5] 开始构建 Android 应用..." -ForegroundColor Yellow
Write-Host ""

Set-Location "$projectRoot\app"

# 清理
if ($CleanBuild) {
    Write-Host "正在清理构建目录..." -ForegroundColor Gray
    if (Test-Path "build") { Remove-Item -Recurse -Force "build" }
    if (Test-Path ".gradle") { Remove-Item -Recurse -Force ".gradle" }
}

# 构建类型
if ($ReleaseBuild) {
    Write-Host "正在构建 Release 版本..." -ForegroundColor Cyan
    .\gradlew.bat assembleRelease
} else {
    Write-Host "正在构建 Debug 版本..." -ForegroundColor Cyan
    .\gradlew.bat assembleDebug
}

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  ✅ 构建成功！" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    
    if ($ReleaseBuild) {
        $apkPath = "$projectRoot\app\build\outputs\apk\release\app-release.apk"
    } else {
        $apkPath = "$projectRoot\app\build\outputs\apk\debug\app-debug.apk"
    }
    
    if (Test-Path $apkPath) {
        $apkSize = (Get-Item $apkPath).Length / 1MB
        Write-Host "APK 位置: $apkPath" -ForegroundColor Cyan
        Write-Host "APK 大小: $([math]::Round($apkSize, 2)) MB" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "下一步:" -ForegroundColor Yellow
        Write-Host "  1. 将 APK 传输到 Android 设备" -ForegroundColor Gray
        Write-Host "  2. 在设备上安装 APK" -ForegroundColor Gray
        Write-Host "  3. 启动应用享受 AI 浏览器！" -ForegroundColor Gray
    }
} else {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "  ❌ 构建失败！" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "请检查错误信息，或运行诊断脚本：" -ForegroundColor Yellow
    Write-Host "  .\scripts\diagnose.ps1" -ForegroundColor Gray
}

Set-Location $projectRoot
