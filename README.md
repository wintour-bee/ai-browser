# 🤖 AI Browser - Android 智能浏览器

<div align="center">

![AI Browser](docs/images/logo.png)

**集 AI 聊天、VPN、浏览器于一体的 Android 应用**

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Android-8.0%2B-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-orange.svg)](https://kotlinlang.org)

</div>

---

## ✨ 功能特性

### 🤖 AI 智能助手
- 支持 7 个主流 AI 模型：ChatGPT、Claude、Gemini、文心一言、智谱 AI、通义千问、讯飞星火
- 智能对话，Markdown 渲染，代码高亮
- 对话历史保存

### 🔒 VPN 功能
- 内置 V2Ray 支持
- VMess、VLESS、Trojan 协议
- 自动路由分流
- 流量统计

### 🌐 浏览器核心
- 基于 Android WebView
- 广告拦截（AdBlock）
- 隐私保护模式
- 多标签页管理
- 书签同步

### 👤 用户系统
- JWT 认证
- VIP 会员系统
- 邀请奖励
- 每日签到
- 个性化设置

---

## 🚀 快速开始

### ☁️ 在线构建（推荐！无需安装任何软件）

```powershell
# 1. 运行一键推送脚本
.\scripts\run-all.ps1 -GitHubUsername "你的GitHub用户名"

# 2. 在 GitHub 创建仓库
# 访问 https://github.com/new 创建 ai-browser 仓库

# 3. 推送代码
git push -u origin main

# 4. 5-10 分钟后，在 Actions 下载 APK
# https://github.com/YOUR_USERNAME/ai-browser/actions
```

详细指南：[在线构建指南](ONLINE_BUILD.md)

### 💻 本地构建

#### 环境要求
- Java JDK 17
- Android SDK (API 34)
- Gradle 8.4

#### 构建步骤

```bash
# Android 应用
cd app
./gradlew assembleDebug

# APK 位置: app/build/outputs/apk/debug/app-debug.apk
```

详细指南：[本地构建指南](BUILD_INSTRUCTIONS.md)

---

## 📁 项目结构

```
ai-browser/
├── app/                          # Android 应用
│   ├── src/main/
│   │   ├── java/com/aibrowser/app/
│   │   │   ├── di/              # 依赖注入 (Hilt)
│   │   │   ├── data/            # 数据层
│   │   │   │   ├── local/       # 本地数据库 (Room)
│   │   │   │   ├── remote/      # 远程 API (Retrofit)
│   │   │   │   └── repository/  # 仓库模式
│   │   │   ├── domain/          # 业务逻辑层
│   │   │   │   ├── model/       # 数据模型
│   │   │   │   ├── repository/  # 仓库接口
│   │   │   │   └── usecase/     # 用例
│   │   │   └── ui/              # UI 层 (Jetpack Compose)
│   │   │       ├── theme/       # 主题
│   │   │       ├── components/  # 组件
│   │   │       ├── screens/     # 页面
│   │   │       └── navigation/  # 导航
│   │   └── res/                 # 资源文件
│   └── build.gradle.kts         # 应用构建配置
│
├── backend/                       # 后端服务
│   ├── src/
│   │   ├── controllers/         # 控制器
│   │   ├── models/              # 数据模型
│   │   ├── routes/              # 路由
│   │   ├── services/            # 服务
│   │   └── middleware/          # 中间件
│   ├── config/                  # 配置文件
│   ├── tests/                   # 测试
│   └── package.json
│
├── docs/                         # 文档
│   ├── api/                     # API 文档
│   ├── images/                  # 图片资源
│   └── *.md                     # 说明文档
│
├── scripts/                      # 构建脚本
│   ├── diagnose.ps1             # 环境诊断
│   ├── build-android-app.ps1   # 构建应用
│   ├── setup-build-environment.ps1  # 环境安装
│   ├── github-push.ps1          # GitHub 推送
│   └── run-all.ps1             # 一键运行
│
├── .github/                      # GitHub 配置
│   └── workflows/
│       ├── android-ci.yml       # Android CI/CD
│       └── backend-ci.yml       # Backend CI/CD
│
├── gradle/                       # Gradle Wrapper
├── web-preview.html             # Web 预览
├── docker-compose.yml           # Docker 编排
├── Dockerfile                   # Docker 镜像
├── README.md                    # 项目说明
└── LICENSE                      # 许可证
```

---

## 🛠️ 技术栈

### Android 应用
- **语言**: Kotlin 1.9+
- **UI**: Jetpack Compose
- **架构**: MVVM + Clean Architecture
- **依赖注入**: Hilt
- **数据库**: Room
- **网络**: Retrofit + OkHttp
- **异步**: Kotlin Coroutines + Flow
- **导航**: Navigation Compose

### 后端服务
- **运行时**: Node.js 18+
- **框架**: Express.js
- **数据库**: PostgreSQL
- **缓存**: Redis
- **认证**: JWT
- **容器化**: Docker

---

## 📦 模块说明

### AI 模块
支持多个 AI 模型的无缝切换：
- OpenAI (GPT-3.5/GPT-4)
- Anthropic (Claude)
- Google (Gemini)
- 百度 (文心一言)
- 智谱 AI (GLM)
- 阿里 (通义千问)
- 讯飞 (星火)

### VPN 模块
完整的 VPN 解决方案：
- V2Ray 协议支持
- 节点管理
- 流量监控
- 自动重连
- 分应用代理

### 浏览器模块
功能丰富的浏览器：
- 标签页管理
- 书签同步
- 下载管理
- 广告拦截
- 无痕模式
- 页面预览

---

## 🐳 Docker 部署（后端）

```bash
# 构建并启动
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

---

## 📚 文档

- [完整设置指南](COMPLETE_SETUP.md) ⭐⭐⭐⭐⭐ - **推荐先读这个**
- [在线构建指南](ONLINE_BUILD.md) ⭐⭐⭐⭐⭐ - **推荐使用**
- [本地构建指南](BUILD_INSTRUCTIONS.md) ⭐⭐⭐⭐
- [GitHub 设置](GITHUB_SETUP.md) ⭐⭐⭐⭐
- [项目总结](PROJECT_SUMMARY.md) ⭐⭐⭐⭐
- [检查清单](CHECKLIST.md) ⭐⭐⭐

---

## ⚙️ 配置说明

### Android 配置

在 `app/build.gradle.kts` 中配置：
```kotlin
android {
    namespace = "com.aibrowser.app"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.aibrowser.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }
}
```

### 后端配置

创建 `.env` 文件：
```env
DATABASE_URL=postgresql://user:pass@localhost:5432/aibrowser
REDIS_URL=redis://localhost:6379
JWT_SECRET=your-secret-key
PORT=3000
```

---

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

---

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

---

## 🙏 致谢

- [Jetpack Compose](https://developer.android.com/compose)
- [Hilt](https://dagger.dev/hilt/)
- [V2Ray](https://www.v2fly.org/)
- [Express.js](https://expressjs.com/)
- 所有开源贡献者！

---

## 📞 联系

- **GitHub Issues**: [提交问题](https://github.com/issues)
- **Email**: support@aibrowser.app
- **Telegram**: [@aibrowser](https://t.me/aibrowser)

---

<div align="center">

**如果这个项目对你有帮助，请给我们一个 ⭐️**

Made with ❤️ by AI Browser Team

</div>
