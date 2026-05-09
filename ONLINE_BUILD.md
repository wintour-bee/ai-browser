# AI Browser - 在线构建指南

## ☁️ 支持的在线构建方式

### 1. GitHub Actions（推荐）✅

**完全免费，无需安装任何软件！**

#### 优点
- ✅ 完全免费（每月 2000 分钟）
- ✅ 无需本地安装 Java/Android SDK
- ✅ 自动构建 Debug 和 Release APK
- ✅ 自动运行测试
- ✅ 自动构建 Docker 镜像

#### 快速开始

```powershell
# 1. 运行 GitHub 推送脚本
cd C:\Users\winto\CodeBuddy\20260509144732
.\scripts\github-push.ps1 -GitHubUsername "yourusername"

# 2. 在 GitHub 上创建仓库后
git push -u origin main

# 3. 查看构建进度
# 访问: https://github.com/YOUR_USERNAME/ai-browser/actions
```

详细指南: [GitHub 设置指南](GITHUB_SETUP.md)

---

### 2. GitHub 网页直接上传

如果不想用命令行，可以直接在 GitHub 网页操作：

1. 访问 https://github.com/new
2. 创建新仓库 `ai-browser`
3. 点击 "uploading an existing file"
4. 拖拽所有文件到上传区域
5. 点击 "Commit changes"

构建会自动开始！

---

## 📱 下载构建产物

### 从 GitHub Actions 下载

1. 进入你的 GitHub 仓库
2. 点击 `Actions` 标签
3. 选择构建任务（如 "Build Debug APK"）
4. 点击构建任务名称
5. 在页面底部找到 `Artifacts` 部分
6. 点击 `debug-apk` 下载 APK

### 从 GitHub Releases 下载（Release 构建）

1. 进入你的 GitHub 仓库
2. 点击 `Releases` 标签
3. 点击最新的 Release
4. 下载 `app-release.apk`

---

## 🎯 自动化工作流

### 自动构建流程

```
代码推送 → GitHub Actions → 自动构建 → APK 生成 → 下载使用
```

### 支持的触发条件

- **推送到 main/master 分支**: 构建 Debug + Release APK
- **创建 Pull Request**: 构建 Debug APK + 运行测试
- **手动触发**: 在 Actions 页面点击 "Run workflow"

---

## 🔧 自定义构建

### 修改构建配置

编辑 `.github/workflows/android-ci.yml`：

```yaml
# 修改 Android API 级别
- name: Setup Android SDK
  uses: android-actions/setup-android@v2
  with:
    api-level: 34  # 改为 33, 32 等

# 修改 Gradle 参数
- name: Build Debug APK
  run: |
    cd app
    ./gradlew assembleDebug --no-daemon -x lint
```

### 添加代码签名

Release 构建需要签名密钥：

1. 生成签名密钥
2. Base64 编码
3. 添加到 GitHub Secrets
4. 配置自动签名

详细步骤: [GitHub 设置指南 - 添加签名密钥](GITHUB_SETUP.md#添加签名密钥)

---

## 🐳 后端在线构建

### Docker 镜像自动构建

每次推送到 `main` 分支，GitHub Actions 会：

1. 构建 Docker 镜像
2. 推送到 Docker Hub
3. 自动生成版本标签

### 部署到云服务器

支持自动部署到：
- AWS EC2
- DigitalOcean
- Vultr
- 任何 SSH 可访问的服务器

---

## 💰 费用说明

### GitHub Actions 免费额度

| 计划 | 每月免费分钟数 | 存储空间 |
|------|---------------|----------|
| 免费版 | 2,000 分钟 | 500 MB |
| Pro | 3,000 分钟 | 1 GB |
| Team | 3,000 分钟 | 2 GB |
| Enterprise | 50,000 分钟 | 无限 |

### 构建时间估算

- **Debug APK**: 约 5-8 分钟
- **Release APK**: 约 8-12 分钟
- **Backend 测试**: 约 2-3 分钟
- **Docker 镜像**: 约 3-5 分钟

### 节省分钟数技巧

1. 使用 Gradle 缓存
2. 跳过不必要的构建步骤
3. 只在必要时构建 Release

---

## ❓ 常见问题

### Q: 构建失败怎么办？

1. 点击失败的构建任务
2. 查看详细的构建日志
3. 根据错误信息修复代码
4. 重新推送代码

### Q: 超过免费额度怎么办？

- 等待下个月额度重置
- 或购买额外的 GitHub Actions 分钟数
- 或优化构建流程减少时间

### Q: 如何加快构建速度？

1. 启用 Gradle 缓存
2. 使用更快的 runner（付费）
3. 优化依赖关系

### Q: 可以构建 iOS 版本吗？

不行，GitHub Actions 的 macOS runner 时间有限且需要付费。
iOS 构建需要 Xcode，只能在 macOS 上进行。

---

## 🚀 一键部署到云平台

### Vercel（前端）

```bash
npm i -g vercel
vercel --prod
```

### Railway（后端）

1. 连接 GitHub 仓库
2. 自动检测 Node.js 项目
3. 配置环境变量
4. 一键部署

### Render（后端）

1. 创建 Web Service
2. 连接 GitHub
3. 设置构建命令: `npm run build`
4. 设置启动命令: `npm start`

### Fly.io（后端）

```bash
fly launch
fly deploy
```

---

## 📞 获取帮助

- **GitHub Actions 文档**: https://docs.github.com/actions
- **GitHub Community**: https://github.community/
- **本项目 Issues**: https://github.com/YOUR_USERNAME/ai-browser/issues

---

## 🎉 成功案例

使用本项目 + GitHub Actions 的开发者已成功：

1. ✅ 构建 Debug APK 并在测试设备上安装
2. ✅ 构建 Release APK 并发布到应用商店
3. ✅ 自动部署后端服务到云服务器
4. ✅ 使用 GitHub Packages 托管 Docker 镜像

---

**立即开始**: [运行 GitHub 推送脚本](../scripts/github-push.ps1)

```powershell
.\scripts\github-push.ps1 -GitHubUsername "yourusername"
```
