# AI Browser - 本地构建指南

## 环境状态

### ✅ 已安装
- **Java JDK 17**: `C:\Program Files\Java\jdk-17`

### ❌ 待安装
- Android SDK (约 5GB)
- Gradle 8.4 (约 120MB)
- Node.js 18+ (约 30MB)

---

## 🎯 快速开始（推荐）

### 方式一：自动安装脚本（需要管理员权限）

```powershell
cd C:\Users\winto\CodeBuddy\20260509144732

# 运行一键安装构建环境
.\scripts\setup-build-environment.ps1
```

### 方式二：手动安装

#### 1. 安装 Android SDK
```powershell
# 使用 winget 安装
winget install --id Google.AndroidSdk -e --accept-package-agreements

# 或手动下载：
# https://developer.android.com/studio#command-line-tools-only
```

#### 2. 安装 Gradle
```powershell
# 下载 Gradle
Invoke-WebRequest -Uri "https://services.gradle.org/distributions/gradle-8.4-bin.zip" -OutFile "$env:USERPROFILE\Downloads\gradle-8.4.zip"

# 解压到 C:\Gradle
Expand-Archive -Path "$env:USERPROFILE\Downloads\gradle-8.4.zip" -DestinationPath "C:\Gradle"
```

#### 3. 安装 Node.js
```powershell
# 使用 winget 安装
winget install --id NodeJS.LTS -e --accept-package-agreements

# 或从 https://nodejs.org 下载
```

#### 4. 配置环境变量
```powershell
# 添加到系统 PATH（需要管理员权限）
[Environment]::SetEnvironmentVariable("Path", $env:Path + ";C:\Gradle\gradle-8.4\bin;C:\Program Files\Java\jdk-17\bin", "Machine")
```

---

## 📦 Android SDK 配置

### 自动配置（推荐）
```powershell
# 安装 Android SDK 后，运行此脚本
.\scripts\setup-android-sdk.ps1
```

### 手动配置
```powershell
# 设置 ANDROID_HOME
[Environment]::SetEnvironmentVariable("ANDROID_HOME", "C:\Users\winto\AppData\Local\Android\Sdk", "Machine")

# 安装必要的 SDK 组件
sdkmanager --install "platforms;android-34" "build-tools;34.0.0" "platform-tools"
```

---

## 🚀 构建应用

### Android 应用
```powershell
cd C:\Users\winto\CodeBuddy\20260509144732\app

# 方式一：使用 Gradle Wrapper
.\gradlew.bat assembleDebug

# 方式二：使用已安装的 Gradle
gradle assembleDebug

# APK 输出位置
# app\build\outputs\apk\debug\app-debug.apk
```

### 后端服务
```powershell
cd C:\Users\winto\CodeBuddy\20260509144732\backend

# 安装依赖
npm install

# 启动服务
npm run dev

# 或生产环境
npm run start
```

---

## 🐳 Docker 部署（后端）

```powershell
cd C:\Users\winto\CodeBuddy\20260509144732

# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d

# 查看日志
docker-compose logs -f
```

---

## ❌ 常见问题

### Q: winget 找不到包？
```powershell
# 更新 winget
winget upgrade --id Microsoft.Winget.Source

# 重新搜索
winget search Android
```

### Q: Android SDK 下载慢？
使用国内镜像：
```powershell
# 设置清华镜像
sdkmanager --mirrors https://mirrors.tuna.tsinghua.edu.cn/android/repository/
```

### Q: Gradle 下载慢？
```powershell
# 使用阿里镜像
# 在 gradle.properties 中添加：
# distributionUrl=https://maven.aliyun.com/repository/gradle-proxy/gradle/gradle-8.4-bin.zip
```

### Q: Node.js 安装失败？
从官网下载安装包：https://nodejs.org/download/

---

## 📊 构建时间估算

| 步骤 | 首次 | 后续 |
|------|------|------|
| 下载工具 | 30-60 分钟 | 0 分钟 |
| Android SDK | 20-40 分钟 | 5-10 分钟 |
| 首次构建 | 15-20 分钟 | 3-5 分钟 |
| **总计** | **65-120 分钟** | **8-15 分钟** |

---

## 🆘 获取帮助

1. 查看 [GitHub Issues](https://github.com/)
2. 查看 [项目 Wiki](https://github.com/)
3. 运行诊断脚本：`.\scripts\diagnose.ps1`
