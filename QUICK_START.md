# AI Browser - 快速开始指南

## 🚀 快速构建

### 方式一：使用自动构建脚本（推荐）

```powershell
# 1. 以管理员身份运行 PowerShell
# 2. 进入项目目录
cd C:\Users\winto\CodeBuddy\20260509144732

# 3. 运行自动构建脚本
.\scripts\build-app.ps1
```

### 方式二：手动构建

#### Windows

```powershell
# 1. 安装 Java JDK 17
#    下载地址: https://adoptium.net/temurin/releases/?version=17

# 2. 安装 Android Studio
#    下载地址: https://developer.android.com/studio

# 3. 配置环境变量
setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-17.0.9.9-hotspot"
setx ANDROID_HOME "C:\Users\YourUser\AppData\Local\Android\Sdk"

# 4. 进入应用目录
cd app

# 5. 构建 Debug APK
.\gradlew.bat assembleDebug

# 6. APK 输出位置
#    app\build\outputs\apk\debug\app-debug.apk
```

#### macOS / Linux

```bash
# 1. 安装依赖
brew install openjdk@17 android-sdk

# 2. 配置环境变量
export JAVA_HOME=$(/usr/libexec/java_home)
export ANDROID_HOME=~/Library/Android/sdk

# 3. 构建
cd app
chmod +x gradlew
./gradlew assembleDebug
```

## 🐳 Docker 部署后端

```bash
# 1. 复制环境变量配置
cp .env.example .env

# 2. 编辑 .env 文件配置数据库等信息

# 3. 启动所有服务
docker-compose up -d

# 4. 查看服务状态
docker-compose ps

# 5. 查看日志
docker-compose logs -f backend
```

## 📱 安装 APK

```bash
# 1. 连接 Android 设备（启用 USB 调试）

# 2. 安装 APK
adb install app\build\outputs\apk\debug\app-debug.apk

# 3. 打开应用
adb shell am start -n com.aibrowser.app/.MainActivity
```

## 🔧 开发模式

### Android 应用

```bash
cd app
.\gradlew.bat assembleDebug
# 在 Android Studio 中运行或使用 adb 安装
```

### 后端服务

```bash
cd backend
npm install
cp ../.env.example .env
# 编辑 .env 文件
npm run dev
```

## 📁 项目结构

```
AI-Browser/
├── app/                          # Android 应用
│   ├── src/main/
│   │   ├── java/com/aibrowser/app/
│   │   │   ├── ai/               # AI 聊天模块
│   │   │   ├── browser/          # 浏览器核心
│   │   │   ├── data/             # 数据层
│   │   │   ├── di/               # 依赖注入
│   │   │   ├── domain/           # 业务逻辑
│   │   │   ├── presentation/     # UI 层
│   │   │   ├── util/             # 工具类
│   │   │   └── vpn/              # VPN 模块
│   │   └── res/                  # 资源文件
│   └── build.gradle.kts
│
├── backend/                      # Node.js 后端
│   ├── src/
│   │   ├── config/              # 配置文件
│   │   ├── middleware/          # 中间件
│   │   ├── models/              # 数据模型
│   │   ├── routes/              # API 路由
│   │   └── utils/               # 工具函数
│   ├── package.json
│   └── Dockerfile
│
├── nginx/                       # Nginx 配置
├── scripts/                     # 脚本文件
├── docker-compose.yml           # Docker Compose 配置
├── build.gradle.kts            # 项目配置
└── README.md                   # 项目文档
```

## 🎯 主要功能

- ✅ AI 智能助手（多模型支持）
- ✅ WebView 浏览器核心
- ✅ 内置 VPN（V2Ray）
- ✅ 多标签页管理
- ✅ 书签同步
- ✅ 下载管理
- ✅ 用户认证系统
- ✅ VIP 会员系统
- ✅ 邀请奖励系统
- ✅ 每日签到系统

## 🛠️ 技术栈

**Android:**
- Kotlin
- Jetpack Compose
- Hilt (依赖注入)
- Room (数据库)
- Retrofit (网络)
- DataStore (偏好设置)
- WebView (浏览器)
- V2Ray (VPN)

**Backend:**
- Node.js
- Express
- PostgreSQL
- Redis
- JWT (认证)
- Docker

## 📞 帮助

如有问题，请查看：
- `BUILD_GUIDE.md` - 详细构建指南
- `README.md` - 完整项目文档
