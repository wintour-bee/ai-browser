# AI Browser - 诊断脚本
# 检查环境配置并提供修复建议

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  AI Browser 环境诊断" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$issues = 0
$warnings = 0

# 1. 检查 Java
Write-Host "[1/8] 检查 Java..." -ForegroundColor Yellow
$java = Get-Command java -ErrorAction SilentlyContinue
if ($java) {
    $version = & java -version 2>&1 | Select-Object -First 1
    if ($version -match "17") {
        Write-Host "  ✓ Java 17 已安装" -ForegroundColor Green
    } else {
        Write-Host "  ⚠ Java 版本可能不兼容（推荐 JDK 17）" -ForegroundColor Yellow
        $warnings++
    }
} else {
    $javaPath = "C:\Program Files\Java\jdk-17\bin\java.exe"
    if (Test-Path $javaPath) {
        Write-Host "  ✓ Java JDK 17 已安装但未在 PATH 中" -ForegroundColor Green
        Write-Host "    路径: $javaPath" -ForegroundColor Gray
    } else {
        Write-Host "  ✗ Java 未安装" -ForegroundColor Red
        Write-Host "    修复: 运行 .\scripts\setup-build-environment.ps1" -ForegroundColor Gray
        $issues++
    }
}

# 2. 检查 JAVA_HOME
Write-Host ""
Write-Host "[2/8] 检查 JAVA_HOME..." -ForegroundColor Yellow
if ($env:JAVA_HOME) {
    if (Test-Path $env:JAVA_HOME) {
        Write-Host "  ✓ JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Green
    } else {
        Write-Host "  ✗ JAVA_HOME 路径无效" -ForegroundColor Red
        $issues++
    }
} else {
    Write-Host "  ⚠ JAVA_HOME 未设置" -ForegroundColor Yellow
    Write-Host "    建议: setx JAVA_HOME 'C:\Program Files\Java\jdk-17'" -ForegroundColor Gray
    $warnings++
}

# 3. 检查 Gradle
Write-Host ""
Write-Host "[3/8] 检查 Gradle..." -ForegroundColor Yellow
$gradle = Get-Command gradle -ErrorAction SilentlyContinue
if ($gradle) {
    $version = & gradle --version 2>&1 | Select-Object -First 1
    Write-Host "  ✓ Gradle 已安装: $version" -ForegroundColor Green
} else {
    $gradlew = "C:\Users\winto\CodeBuddy\20260509144732\app\gradlew.bat"
    if (Test-Path $gradlew) {
        Write-Host "  ✓ Gradle Wrapper 可用（推荐）" -ForegroundColor Green
    } else {
        Write-Host "  ✗ Gradle 未安装" -ForegroundColor Red
        Write-Host "    修复: 运行 .\scripts\setup-build-environment.ps1" -ForegroundColor Gray
        $issues++
    }

# 4. 检查 Android SDK
Write-Host ""
Write-Host "[4/8] 检查 Android SDK..." -ForegroundColor Yellow
$androidHome = $env:ANDROID_HOME
if (-not $androidHome) {
    $androidHome = "C:\Users\winto\AppData\Local\Android\Sdk"
}

if (Test-Path $androidHome) {
    Write-Host "  ✓ Android SDK: $androidHome" -ForegroundColor Green
    
    # 检查 SDK 组件
    $platforms = "$androidHome\platforms"
    $buildTools = "$androidHome\build-tools"
    
    if (Test-Path $platforms) {
        $latestPlatform = Get-ChildItem $platforms -Directory | Sort-Object Name -Descending | Select-Object -First 1
        if ($latestPlatform) {
            Write-Host "    ✓ 平台: $($latestPlatform.Name)" -ForegroundColor Green
        }
    }
    
    if (Test-Path $buildTools) {
        $latestBuildTools = Get-ChildItem $buildTools -Directory | Sort-Object Name -Descending | Select-Object -First 1
        if ($latestBuildTools) {
            Write-Host "    ✓ 构建工具: $($latestBuildTools.Name)" -ForegroundColor Green
        }
    }
} else {
    Write-Host "  ✗ Android SDK 未安装" -ForegroundColor Red
    Write-Host "    大小: ~5GB" -ForegroundColor Gray
    Write-Host "    修复: 运行 .\scripts\setup-build-environment.ps1" -ForegroundColor Gray
    Write-Host "    或: winget install Google.AndroidSdk" -ForegroundColor Gray
    $issues++
}

# 5. 检查 Node.js
Write-Host ""
Write-Host "[5/8] 检查 Node.js..." -ForegroundColor Yellow
$node = Get-Command node -ErrorAction SilentlyContinue
if ($node) {
    $version = & node --version 2>&1
    Write-Host "  ✓ Node.js: $version" -ForegroundColor Green
} else {
    Write-Host "  ⚠ Node.js 未安装（构建后端需要）" -ForegroundColor Yellow
    Write-Host "    修复: winget install NodeJS.LTS" -ForegroundColor Gray
    $warnings++
}

# 6. 检查项目文件
Write-Host ""
Write-Host "[6/8] 检查项目文件..." -ForegroundColor Yellow
$projectRoot = "C:\Users\winto\CodeBuddy\20260509144732"
$requiredFiles = @(
    "app\build.gradle.kts",
    "app\src\main\AndroidManifest.xml",
    "settings.gradle.kts",
    "gradle\wrapper\gradle-wrapper.properties"
)

$allFilesExist = $true
foreach ($file in $requiredFiles) {
    $fullPath = Join-Path $projectRoot $file
    if (Test-Path $fullPath) {
        Write-Host "  ✓ $file" -ForegroundColor Green
    } else {
        Write-Host "  ✗ $file 缺失" -ForegroundColor Red
        $allFilesExist = $false
        $issues++
    }
}

# 7. 检查网络连接
Write-Host ""
Write-Host "[7/8] 检查网络连接..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "https://google.com" -TimeoutSec 5 -UseBasicParsing -ErrorAction Stop
    Write-Host "  ✓ 网络连接正常" -ForegroundColor Green
} catch {
    Write-Host "  ⚠ 网络连接可能有问题" -ForegroundColor Yellow
    $warnings++
}

# 8. 检查磁盘空间
Write-Host ""
Write-Host "[8/8] 检查磁盘空间..." -ForegroundColor Yellow
$drive = Get-PSDrive -Name C
$freeSpaceGB = [math]::Round($drive.Free / 1GB, 2)
Write-Host "  C 盘可用空间: $freeSpaceGB GB" -ForegroundColor $(if ($freeSpaceGB -gt 10) { "Green" } else { "Yellow" })

if ($freeSpaceGB -lt 5) {
    Write-Host "  ⚠ 磁盘空间不足，可能导致构建失败" -ForegroundColor Yellow
    $warnings++
}

# 总结
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  诊断结果" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

if ($issues -eq 0 -and $warnings -eq 0) {
    Write-Host "  ✅ 环境配置完美！可以开始构建。" -ForegroundColor Green
    Write-Host ""
    Write-Host "  运行构建: .\scripts\build-android-app.ps1" -ForegroundColor Cyan
} elseif ($issues -eq 0) {
    Write-Host "  ✅ 无严重问题，可以尝试构建。" -ForegroundColor Green
    Write-Host "  ⚠ 警告数: $warnings" -ForegroundColor Yellow
} else {
    Write-Host "  ❌ 发现 $issues 个问题，$warnings 个警告" -ForegroundColor Red
    Write-Host ""
    Write-Host "  请先修复问题再构建：" -ForegroundColor Yellow
    Write-Host "  1. 运行: .\scripts\setup-build-environment.ps1" -ForegroundColor Gray
    Write-Host "  2. 重新运行诊断: .\scripts\diagnose.ps1" -ForegroundColor Gray
    Write-Host "  3. 如果问题仍存在，请手动安装缺失的组件" -ForegroundColor Gray
}

Write-Host ""
