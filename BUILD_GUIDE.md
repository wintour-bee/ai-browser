# AI Browser - 构建与部署指南

## 📋 目录

1. [环境要求](#环境要求)
2. [Android 应用构建](#android-应用构建)
3. [后端服务部署](#后端服务部署)
4. [Docker 部署](#docker-部署)
5. [常见问题](#常见问题)

---

## 🖥️ 环境要求

### Android 应用

- **Java JDK**: 17 或更高版本
- **Gradle**: 8.4 或更高版本
- **Android SDK**: API 34 (Android 14)
- **Build Tools**: 34.0.0
- **NDK**: 26.1.10909125

### 后端服务

- **Node.js**: 18.x 或更高版本
- **PostgreSQL**: 14.x 或更高版本
- **Redis**: 6.x 或更高版本
- **Docker**: 20.x 或更高版本（可选）

---

## 📱 Android 应用构建

### 步骤 1: 安装构建工具

#### Windows

下载并安装以下工具：

1. **Java JDK 17**
   - 下载地址: https://adoptium.net/temurin/releases/?version=17
   - 安装后设置环境变量: `JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x-hotspot`

2. **Android SDK**
   - 下载地址: https://developer.android.com/studio
   - 安装 Android Studio 或 command line tools
   - 设置环境变量: `ANDROID_HOME=C:\Users\YourUser\AppData\Local\Android\Sdk`

3. **Gradle 8.4**（可选，项目包含 wrapper）
   - 下载地址: https://gradle.org/releases/

#### macOS / Linux

```bash
# 安装 Homebrew (macOS)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# 安装 Java JDK 17
brew install openjdk@17
export JAVA_HOME=$(/usr/libexec/java_home)

# 安装 Android SDK command line tools
brew install --cask android-sdk
export ANDROID_HOME=~/Library/Android/sdk
```

### 步骤 2: 配置环境变量

编辑 `~/.bashrc` 或 `~/.zshrc`（macOS/Linux）或系统环境变量（Windows）：

```bash
# Java
export JAVA_HOME=/path/to/jdk-17
export PATH=$JAVA_HOME/bin:$PATH

# Android SDK
export ANDROID_HOME=/path/to/android-sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH

# 验证安装
java -version
echo $ANDROID_HOME
```

### 步骤 3: 创建 local.properties

在项目根目录创建 `local.properties` 文件：

```properties
sdk.dir=C:\\Users\\YourUser\\AppData\\Local\\Android\\Sdk
```

### 步骤 4: 构建 Debug APK

```bash
# 进入应用目录
cd app

# 清理并构建
./gradlew clean
./gradlew assembleDebug

# APK 输出位置
# app/build/outputs/apk/debug/app-debug.apk
```

### 步骤 5: 构建 Release APK

```bash
# 创建签名密钥
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias

# 配置签名（在 app/build.gradle.kts 中）
# 然后构建
./gradlew assembleRelease
```

### 步骤 6: 安装到设备

```bash
# 通过 USB 连接设备
adb devices

# 安装 Debug APK
adb install app/build/outputs/apk/debug/app-debug.apk

# 安装 Release APK
adb install app/build/outputs/apk/release/app-release.apk
```

---

## 🖥️ 后端服务部署

### 方式一：本地开发

```bash
# 进入后端目录
cd backend

# 安装依赖
npm install

# 复制环境变量配置
cp ../.env.example .env
# 编辑 .env 文件，配置数据库等信息

# 运行开发服务器
npm run dev

# 或运行生产服务器
npm run build
npm start
```

### 方式二：使用 Docker

```bash
# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

### 方式三：手动部署到服务器

```bash
# 1. 安装 Node.js 18+
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# 2. 安装 PostgreSQL
sudo apt-get install -y postgresql postgresql-contrib

# 3. 安装 Redis
sudo apt-get install -y redis-server

# 4. 配置数据库
sudo -u postgres psql
CREATE DATABASE aibrowser;
CREATE USER aibrowser_user WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE aibrowser TO aibrowser_user;

# 5. 部署应用
cd /path/to/backend
npm install --production
npm run build
npm start
```

---

## 🐳 Docker 部署

### 完整 Docker Compose 部署

```bash
# 克隆项目
git clone <project-url>
cd <project-directory>

# 配置环境变量
cp .env.example .env
# 编辑 .env 文件

# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f backend

# 停止服务
docker-compose down
```

### Nginx 反向代理配置

项目包含预配置的 Nginx 配置（`nginx/nginx.conf`），包括：

- SSL/TLS 证书配置
- HTTP/2 支持
- Gzip 压缩
- 静态文件缓存
- API 代理

### 生产环境建议

1. **使用负载均衡器**（如 AWS ALB、Nginx）
2. **配置 CDN**（如 CloudFlare、AWS CloudFront）
3. **设置监控**（如 Prometheus、Grafana）
4. **配置日志收集**（如 ELK Stack）
5. **定期备份数据库**

---

## ❓ 常见问题

### Android 构建问题

#### Q: Gradle 构建失败

```
# 清理 Gradle 缓存
./gradlew clean --refresh-dependencies

# 删除 .gradle 目录并重试
rm -rf ~/.gradle/caches
rm -rf ~/.gradle/daemon
```

#### Q: Android SDK 未找到

```
# 确保设置正确的 ANDROID_HOME
export ANDROID_HOME=/path/to/android-sdk

# 接受许可证
yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses
```

#### Q: Kotlin 编译器错误

```
# 检查 Kotlin 版本
./gradlew kotlinUpgradeYarnLock

# 同步项目
./gradlew :app:dependencies
```

### 后端部署问题

#### Q: 数据库连接失败

```bash
# 检查 PostgreSQL 服务状态
sudo systemctl status postgresql

# 测试数据库连接
psql -h localhost -U aibrowser_user -d aibrowser
```

#### Q: Redis 连接失败

```bash
# 检查 Redis 服务状态
sudo systemctl status redis-server

# 测试 Redis 连接
redis-cli ping
```

#### Q: 端口被占用

```bash
# 查找占用端口的进程
lsof -i :3000  # Node.js 默认端口
lsof -i :5432  # PostgreSQL 默认端口
lsof -i :6379  # Redis 默认端口

# 停止进程或修改配置使用其他端口
```

---

## 📞 获取帮助

如有问题，请提交 Issue 或联系开发者。

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 LICENSE 文件。
