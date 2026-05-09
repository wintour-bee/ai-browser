# AI Browser - GitHub 推送脚本
# 自动初始化 Git 仓库并准备推送到 GitHub

param(
    [Parameter(Mandatory=$true)]
    [string]$GitHubUsername,
    
    [Parameter(Mandatory=$true)]
    [string]$RepoName = "ai-browser",
    
    [switch]$SkipGitHubCreate,
    [switch]$Help
)

if ($Help) {
    Write-Host ""
    Write-Host "AI Browser - GitHub 推送脚本" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "用法:" -ForegroundColor Yellow
    Write-Host "  .\github-push.ps1 -GitHubUsername 'yourusername' [-RepoName 'ai-browser']" -ForegroundColor White
    Write-Host ""
    Write-Host "示例:" -ForegroundColor Yellow
    Write-Host "  .\github-push.ps1 -GitHubUsername 'john' -RepoName 'my-browser'" -ForegroundColor White
    Write-Host ""
    exit 0
}

$ErrorActionPreference = "Stop"
$projectRoot = $PSScriptRoot | Split-Path -Parent

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  AI Browser - GitHub 推送助手" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查 Git 是否安装
Write-Host "[1/5] 检查 Git 安装..." -ForegroundColor Yellow
try {
    $gitVersion = git --version 2>&1
    Write-Host "  ✓ Git 已安装: $gitVersion" -ForegroundColor Green
} catch {
    Write-Host "  ✗ Git 未安装" -ForegroundColor Red
    Write-Host "  请先安装 Git: https://git-scm.com/download/win" -ForegroundColor Yellow
    exit 1
}

# 检查 SSH 密钥
Write-Host "[2/5] 检查 SSH 密钥..." -ForegroundColor Yellow
$sshKeyPath = "$env:USERPROFILE\.ssh\id_rsa"
if (Test-Path $sshKeyPath) {
    Write-Host "  ✓ SSH 密钥已存在" -ForegroundColor Green
} else {
    Write-Host "  ! SSH 密钥不存在，建议创建以方便推送" -ForegroundColor Yellow
    Write-Host "  创建命令: ssh-keygen -t rsa -b 4096 -C 'your_email@example.com'" -ForegroundColor Gray
}

# 初始化 Git 仓库
Write-Host "[3/5] 初始化 Git 仓库..." -ForegroundColor Yellow
Set-Location $projectRoot

# 检查是否已经是 Git 仓库
if (Test-Path ".git") {
    Write-Host "  ✓ 已经是 Git 仓库" -ForegroundColor Green
} else {
    git init
    git add .
    git commit -m "Initial commit: AI Browser App

Features:
- AI Chat Assistant (ChatGPT, Claude, Gemini, etc.)
- VPN with V2Ray integration
- WebView Browser Core
- User Authentication System
- VIP Membership

Tech Stack:
- Android: Kotlin, Jetpack Compose, Hilt
- Backend: Node.js, Express, PostgreSQL

Build: ./gradlew assembleDebug"
    Write-Host "  ✓ Git 仓库已初始化并提交" -ForegroundColor Green
}

# 配置 .gitignore
Write-Host "[4/5] 配置 .gitignore..." -ForegroundColor Yellow
$gitignoreContent = @"
# Gradle
.gradle/
build/
!gradle/wrapper/gradle-wrapper.jar

# Android Studio / IntelliJ
.idea/
*.iml
*.ipr
*.iws
.project
.classpath
.settings/
local.properties

# Build outputs
*.apk
*.aab
*.ap_
*.dex
*.class
bin/
gen/
out/

# Debug
*.log

# Keystore
*.keystore
*.jks
signing.properties

# Environment
.env
.env.local
.env.*.local

# OS
.DS_Store
Thumbs.db
*~

# Node
node_modules/
npm-debug.log*
yarn-debug.log*
yarn-error.log*

# Backend
dist/
coverage/
.nyc_output/

# IDE
.vscode/
*.swp
*.swo
"@

Set-Content -Path "$projectRoot\.gitignore" -Value $gitignoreContent
Write-Host "  ✓ .gitignore 已创建" -ForegroundColor Green

# 显示状态
Write-Host "[5/5] 准备推送..." -ForegroundColor Yellow
$remoteUrl = "https://github.com/$GitHubUsername/$RepoName.git"
Write-Host "  远程仓库: $remoteUrl" -ForegroundColor White
Write-Host ""

# 检查是否有远程仓库
$currentRemote = git remote get-url origin 2>$null
if ($currentRemote) {
    Write-Host "  当前远程仓库: $currentRemote" -ForegroundColor Gray
    if ($currentRemote -ne $remoteUrl) {
        git remote set-url origin $remoteUrl
        Write-Host "  ✓ 远程仓库地址已更新" -ForegroundColor Green
    }
} else {
    git remote add origin $remoteUrl
    Write-Host "  ✓ 远程仓库已添加" -ForegroundColor Green
}

# 创建 .github 目录（如果不存在）
$githubDir = "$projectRoot\.github"
if (-not (Test-Path $githubDir)) {
    New-Item -ItemType Directory -Path $githubDir -Force | Out-Null
    New-Item -ItemType Directory -Path "$githubDir\workflows" -Force | Out-Null
    Write-Host "  ✓ .github 目录已创建" -ForegroundColor Green
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  准备完成！" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "下一步操作:" -ForegroundColor Yellow
Write-Host ""
if (-not $SkipGitHubCreate) {
    Write-Host "1. 在 GitHub 上创建仓库:" -ForegroundColor White
    Write-Host "   访问: https://github.com/new" -ForegroundColor Gray
    Write-Host "   Repository name: $RepoName" -ForegroundColor Gray
    Write-Host "   不要勾选 'Initialize this repository with a README'" -ForegroundColor Gray
    Write-Host ""
}
Write-Host "2. 推送代码到 GitHub:" -ForegroundColor White
Write-Host "   git branch -M main" -ForegroundColor Gray
Write-Host "   git push -u origin main" -ForegroundColor Gray
Write-Host ""
Write-Host "3. 在 GitHub Actions 中查看构建状态:" -ForegroundColor White
Write-Host "   https://github.com/$GitHubUsername/$RepoName/actions" -ForegroundColor Gray
Write-Host ""
Write-Host "4. 下载构建完成的 APK:" -ForegroundColor White
Write-Host "   进入 Actions → Build Debug APK → Artifacts" -ForegroundColor Gray
Write-Host ""

# 显示 .gitignore 统计
$gitStatus = git status --short
$trackedFiles = ($gitStatus | Where-Object { $_ -notmatch "^\?\?" }).Count
$untrackedFiles = ($gitStatus | Where-Object { $_ -match "^\?\?" }).Count

Write-Host "仓库统计:" -ForegroundColor Yellow
Write-Host "  已跟踪文件: $trackedFiles" -ForegroundColor White
Write-Host "  未跟踪文件: $untrackedFiles" -ForegroundColor White
Write-Host ""

# 询问是否立即推送
$response = Read-Host "是否立即推送到 GitHub？(y/n)"
if ($response -eq 'y' -or $response -eq 'Y') {
    Write-Host ""
    Write-Host "正在推送..." -ForegroundColor Yellow
    try {
        git branch -M main
        git push -u origin main
        Write-Host ""
        Write-Host "✓ 推送成功！" -ForegroundColor Green
        Write-Host "  仓库地址: https://github.com/$GitHubUsername/$RepoName" -ForegroundColor White
        Write-Host "  Actions: https://github.com/$GitHubUsername/$RepoName/actions" -ForegroundColor White
    } catch {
        Write-Host ""
        Write-Host "✗ 推送失败: $_" -ForegroundColor Red
        Write-Host "请检查远程仓库是否已创建，或使用 HTTPS 推送" -ForegroundColor Yellow
    }
}

Write-Host ""
