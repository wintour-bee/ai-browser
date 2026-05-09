# AI Browser - 启动本地开发服务器

param(
    [switch]$Backend,
    [switch]$Frontend,
    [switch]$All,
    [switch]$Help
)

if ($Help) {
    Write-Host ""
    Write-Host "AI Browser - 本地开发启动脚本" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "用法:" -ForegroundColor Yellow
    Write-Host "  .\start-local-dev.ps1 -All         启动所有服务" -ForegroundColor White
    Write-Host "  .\start-local-dev.ps1 -Backend     仅启动后端" -ForegroundColor White
    Write-Host "  .\start-local-dev.ps1 -Frontend    启动前端预览" -ForegroundColor White
    Write-Host "  .\start-local-dev.ps1 -Help        显示帮助" -ForegroundColor White
    Write-Host ""
    exit 0
}

$projectRoot = $PSScriptRoot | Split-Path -Parent

function Start-Backend-Server {
    Write-Host "启动后端服务..." -ForegroundColor Green
    Set-Location "$projectRoot\backend"
    
    # 检查依赖
    if (-not (Test-Path "node_modules")) {
        Write-Host "安装后端依赖..." -ForegroundColor Yellow
        npm install
    }
    
    # 检查环境变量
    if (-not (Test-Path ".env")) {
        if (Test-Path "$projectRoot\.env.example") {
            Copy-Item "$projectRoot\.env.example" ".env"
            Write-Host "已创建 .env 文件，请编辑配置" -ForegroundColor Yellow
        }
    }
    
    # 启动服务
    Write-Host "后端服务运行在: http://localhost:3000" -ForegroundColor Cyan
    npm run dev
}

function Start-Frontend-Preview {
    Write-Host "启动 Web 预览服务器..." -ForegroundColor Green
    Set-Location $projectRoot
    
    # 检查 Python
    try {
        $pythonVersion = python --version 2>&1
        Write-Host "Python 已安装: $pythonVersion" -ForegroundColor Gray
    } catch {
        Write-Host "警告: Python 未安装，无法启动预览服务器" -ForegroundColor Red
        Write-Host "请访问: http://localhost:8080/web-preview.html 手动预览" -ForegroundColor Yellow
        return
    }
    
    Write-Host "Web 预览运行在: http://localhost:8080" -ForegroundColor Cyan
    Write-Host "应用预览页面: http://localhost:8080/web-preview.html" -ForegroundColor Cyan
    python -m http.server 8080
}

Write-Host ""
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "  AI Browser - 开发服务器启动器" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

if ($All -or (-not $Backend -and -not $Frontend)) {
    Write-Host "启动所有服务..." -ForegroundColor Yellow
    Write-Host ""
    
    # 启动后端
    Start-Backend-Server
} elseif ($Backend) {
    Start-Backend-Server
} elseif ($Frontend) {
    Start-Frontend-Preview
}

Write-Host ""
Write-Host "按 Ctrl+C 停止服务器" -ForegroundColor Yellow
