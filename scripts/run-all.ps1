# AI Browser - 一键在线构建脚本

<#
.SYNOPSIS
    AI Browser - 在线构建助手
    
.DESCRIPTION
    这个脚本会帮助你：
    1. 检查 Git 安装
    2. 初始化 Git 仓库
    3. 准备推送到 GitHub
    4. 触发 GitHub Actions 构建
    
.EXAMPLE
    .\online-build.ps1 -GitHubUsername "yourusername"
#>

param(
    [Parameter(Mandatory=$true)]
    [string]$GitHubUsername,
    
    [string]$RepoName = "ai-browser",
    
    [switch]$OpenGitHub,
    
    [switch]$Help
)

if ($Help) {
    Write-Host ""
    Write-Host "╔═══════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
    Write-Host "║     AI Browser - 在线构建助手 v1.0                       ║" -ForegroundColor Cyan
    Write-Host "╚═══════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "用法:" -ForegroundColor Yellow
    Write-Host "  .\online-build.ps1 -GitHubUsername 'yourusername' [-RepoName 'ai-browser']" -ForegroundColor White
    Write-Host ""
    Write-Host "参数:" -ForegroundColor Yellow
    Write-Host "  -GitHubUsername  GitHub 用户名（必填）" -ForegroundColor Gray
    Write-Host "  -RepoName        仓库名称，默认 ai-browser" -ForegroundColor Gray
    Write-Host "  -OpenGitHub      构建完成后自动打开 GitHub" -ForegroundColor Gray
    Write-Host ""
    Write-Host "示例:" -ForegroundColor Yellow
    Write-Host "  .\online-build.ps1 -GitHubUsername 'john'" -ForegroundColor White
    Write-Host "  .\online-build.ps1 -GitHubUsername 'john' -RepoName 'my-browser' -OpenGitHub" -ForegroundColor White
    Write-Host ""
    exit 0
}

# 颜色定义
$Colors = @{
    Success = "Green"
    Error = "Red"
    Warning = "Yellow"
    Info = "Cyan"
    Step = "Magenta"
}

# 打印分隔线
function Write-Separator {
    Write-Host ""
    Write-Host "─────────────────────────────────────────────────────────" -ForegroundColor DarkGray
    Write-Host ""
}

# 打印步骤
function Write-Step {
    param([string]$Message)
    Write-Host "▶ $Message" -ForegroundColor $Colors.Step
}

# 打印成功
function Write-Success {
    param([string]$Message)
    Write-Host "✓ $Message" -ForegroundColor $Colors.Success
}

# 打印错误
function Write-Error {
    param([string]$Message)
    Write-Host "✗ $Message" -ForegroundColor $Colors.Error
}

# 打印信息
function Write-Info {
    param([string]$Message)
    Write-Host "  $Message" -ForegroundColor Gray
}

Clear-Host

Write-Host ""
Write-Host "╔═══════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║                                                             ║" -ForegroundColor Cyan
Write-Host "║        🤖 AI Browser - 在线构建助手 🤖                    ║" -ForegroundColor Cyan
Write-Host "║                                                             ║" -ForegroundColor Cyan
Write-Host "║     ☁️ 使用 GitHub Actions 云端构建，无需安装任何软件！    ║" -ForegroundColor Yellow
Write-Host "║                                                             ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# 变量
$projectRoot = Split-Path -Parent $PSScriptRoot
$GitHubURL = "https://github.com/$GitHubUsername/$RepoName"
$ActionsURL = "$GitHubURL/actions"

# 步骤 1: 检查 Git
Write-Step "检查 Git 安装..."
try {
    $gitVersion = git --version 2>&1
    Write-Success "Git 已安装: $gitVersion"
} catch {
    Write-Error "Git 未安装"
    Write-Host ""
    Write-Host "请先安装 Git:" -ForegroundColor Yellow
    Write-Host "  访问: https://git-scm.com/download/win" -ForegroundColor White
    Write-Host "  或运行: winget install Git.Git" -ForegroundColor White
    exit 1
}

Write-Separator

# 步骤 2: 进入项目目录
Write-Step "进入项目目录..."
Set-Location $projectRoot
Write-Success "当前目录: $projectRoot"

Write-Separator

# 步骤 3: 检查 .github 目录
Write-Step "检查 GitHub Actions 配置..."
$githubDir = "$projectRoot\.github"
$workflowsDir = "$githubDir\workflows"

if (-not (Test-Path $workflowsDir)) {
    New-Item -ItemType Directory -Path $workflowsDir -Force | Out-Null
    Write-Info "已创建 .github\workflows 目录"
} else {
    Write-Info "GitHub Actions 配置已存在"
}

Write-Success "GitHub Actions 配置文件就绪"
Write-Separator

# 步骤 4: 初始化 Git
Write-Step "初始化 Git 仓库..."
if (Test-Path ".git") {
    Write-Info "已是 Git 仓库"
} else {
    git init
    Write-Success "Git 仓库已初始化"
}

Write-Separator

# 步骤 5: 配置 .gitignore
Write-Step "配置 .gitignore..."
$gitignoreContent = @"
# Gradle
.gradle/
build/
!gradle/wrapper/gradle-wrapper.jar

# Android Studio
.idea/
*.iml

# Build outputs
*.apk
*.aab
*.ap_
*.dex
*.class

# Debug
*.log

# Keystore
*.keystore
*.jks
signing.properties

# Environment
.env
.env.local

# OS
.DS_Store
Thumbs.db

# Node
node_modules/

# Backend
dist/

# IDE
.vscode/
"@

Set-Content -Path ".gitignore" -Value $gitignoreContent -Force
Write-Success ".gitignore 已配置"

Write-Separator

# 步骤 6: 添加所有文件
Write-Step "添加文件到 Git..."
git add .
$status = git status --short
$fileCount = ($status | Measure-Object -Line).Lines
Write-Success "已添加 $fileCount 个文件"

Write-Separator

# 步骤 7: 创建初始提交
Write-Step "创建初始提交..."
$commitMessage = "Initial commit: AI Browser App

✨ Features:
- AI Chat Assistant (ChatGPT, Claude, Gemini, etc.)
- VPN with V2Ray integration
- WebView Browser Core
- User Authentication System
- VIP Membership

🛠 Tech Stack:
- Android: Kotlin, Jetpack Compose, Hilt
- Backend: Node.js, Express, PostgreSQL

☁️ Build: GitHub Actions CI/CD"

git commit -m $commitMessage
Write-Success "初始提交已创建"

Write-Separator

# 步骤 8: 配置远程仓库
Write-Step "配置远程仓库..."
$remoteUrl = "https://github.com/$GitHubUsername/$RepoName.git"

$currentRemote = git remote get-url origin 2>$null
if ($currentRemote) {
    Write-Info "当前远程: $currentRemote"
    if ($currentRemote -ne $remoteUrl) {
        git remote set-url origin $remoteUrl
        Write-Success "远程仓库地址已更新"
    } else {
        Write-Success "远程仓库已配置"
    }
} else {
    git remote add origin $remoteUrl
    Write-Success "远程仓库已添加"
}

Write-Separator

# 步骤 9: 推送代码
Write-Host ""
Write-Host "╔═══════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║                    下一步操作                              ║" -ForegroundColor Green
Write-Host "╚═══════════════════════════════════════════════════════════╝" -ForegroundColor Green
Write-Host ""
Write-Host "1. 创建 GitHub 仓库:" -ForegroundColor Yellow
Write-Host "   访问: https://github.com/new" -ForegroundColor White
Write-Host ""
Write-Host "   设置:" -ForegroundColor Yellow
Write-Host "   - Repository name: $RepoName" -ForegroundColor White
Write-Host "   - 不要勾选 'Initialize this repository with a README'" -ForegroundColor White
Write-Host ""
Write-Host "2. 推送代码到 GitHub:" -ForegroundColor Yellow
Write-Host "   运行以下命令:" -ForegroundColor White
Write-Host ""
Write-Host "   git branch -M main" -ForegroundColor Cyan
Write-Host "   git push -u origin main" -ForegroundColor Cyan
Write-Host ""
Write-Host "3. 查看构建进度:" -ForegroundColor Yellow
Write-Host "   $ActionsURL" -ForegroundColor Cyan
Write-Host ""

# 询问是否打开 GitHub
if ($OpenGitHub) {
    Start-Process "https://github.com/new?repo_name=$RepoName"
} else {
    $response = Read-Host "是否立即打开 GitHub 创建仓库页面？(y/n)"
    if ($response -eq 'y' -or $response -eq 'Y') {
        Start-Process "https://github.com/new?repo_name=$RepoName"
    }
}

Write-Separator

# 打印总结
Write-Host ""
Write-Host "╔═══════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║                      完成！                                ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""
Write-Host "📋 后续步骤:" -ForegroundColor Yellow
Write-Host ""
Write-Host "  1. 在 GitHub 上创建 '$RepoName' 仓库" -ForegroundColor White
Write-Host "  2. 运行: git push -u origin main" -ForegroundColor White
Write-Host "  3. 等待 GitHub Actions 自动构建（约 5-10 分钟）" -ForegroundColor White
Write-Host "  4. 下载 APK: Actions → Build Debug APK → Artifacts" -ForegroundColor White
Write-Host ""
Write-Host "🔗 链接:" -ForegroundColor Yellow
Write-Host "  - GitHub 仓库: $GitHubURL" -ForegroundColor White
Write-Host "  - Actions: $ActionsURL" -ForegroundColor White
Write-Host ""
Write-Host "📚 文档:" -ForegroundColor Yellow
Write-Host "  - 在线构建指南: ONLINE_BUILD.md" -ForegroundColor White
Write-Host "  - GitHub 设置: GITHUB_SETUP.md" -ForegroundColor White
Write-Host ""
Write-Host "祝您使用愉快！ 🎉" -ForegroundColor Green
Write-Host ""
