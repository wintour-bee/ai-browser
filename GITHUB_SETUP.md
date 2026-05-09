# GitHub Actions 在线构建指南

本项目配置了完整的 GitHub Actions CI/CD 工作流，支持在线构建 Android APK 和部署后端服务。

## 🚀 快速开始

### 1. 创建 GitHub 仓库

```bash
# 初始化 Git 仓库
cd C:\Users\winto\CodeBuddy\20260509144732
git init
git add .
git commit -m "Initial commit: AI Browser App"

# 创建 GitHub 仓库（在 GitHub 网站上创建）
# 然后关联远程仓库
git remote add origin https://github.com/YOUR_USERNAME/ai-browser.git
git branch -M main
git push -u origin main
```

### 2. 配置 GitHub Secrets

在 GitHub 仓库的 `Settings → Secrets and variables → Actions` 中添加以下密钥：

#### Android Release 构建（可选）
- `KEYSTORE_BASE64`: Base64 编码的签名密钥文件
- `KEYSTORE_PASSWORD`: 密钥库密码
- `KEY_ALIAS`: 密钥别名
- `KEY_PASSWORD`: 密钥密码

#### Docker 部署（可选）
- `DOCKERHUB_USERNAME`: Docker Hub 用户名
- `DOCKERHUB_TOKEN`: Docker Hub 访问令牌
- `STAGING_HOST`: 预发布服务器地址
- `STAGING_USER`: 服务器用户名
- `STAGING_SSH_KEY`: SSH 私钥

### 3. 触发构建

#### 方式一：推送代码
```bash
git push origin main
```

#### 方式二：创建 Pull Request
在 GitHub 上创建 Pull Request，自动触发构建和测试。

#### 方式三：手动触发
在 GitHub 仓库的 `Actions` 标签页，选择工作流并点击 `Run workflow`。

## 📱 Android 构建

### 自动构建流程

每次推送到 `main` 或 `master` 分支，GitHub Actions 会自动：

1. **安装环境**
   - Ubuntu Latest
   - JDK 17
   - Android SDK

2. **构建 APK**
   - Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
   - Release APK: `app/build/outputs/apk/release/app-release.apk`（仅 main/master 分支）

3. **代码检查**
   - 运行 Lint 检查
   - 生成构建报告

4. **上传产物**
   - Debug APK: 保留 7 天
   - Release APK: 保留 30 天

### 下载构建产物

1. 进入 GitHub 仓库
2. 点击 `Actions` 标签
3. 选择构建任务
4. 点击 `Artifacts` 下载 APK

### 添加签名密钥

#### 生成签名密钥

```bash
# 在本地生成密钥
keytool -genkey -v -keystore release.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias

# 转换为 Base64
certutil -encodefile release.keystore release.keystore.b64
```

#### 配置 GitHub Secrets

1. 读取 Base64 内容
2. 在 GitHub 添加 Secret：
   - `KEYSTORE_BASE64`: Base64 编码内容
   - `KEYSTORE_PASSWORD`: 密钥库密码
   - `KEY_ALIAS`: my-key-alias
   - `KEY_PASSWORD`: 密钥密码

## 🖥️ 后端部署

### 自动部署流程

每次推送到 `backend/` 目录，GitHub Actions 会：

1. **测试**
   - 安装依赖
   - 运行单元测试
   - 构建项目

2. **构建 Docker 镜像**
   - 构建并推送到 Docker Hub
   - 自动生成版本标签

3. **部署到服务器**（仅 develop 分支）
   - SSH 连接到服务器
   - 拉取最新镜像
   - 重启服务

### 配置部署

#### Docker Hub

1. 创建 Docker Hub 账号
2. 创建 Access Token
3. 添加 GitHub Secrets：
   - `DOCKERHUB_USERNAME`
   - `DOCKERHUB_TOKEN`

#### 服务器部署

1. 生成 SSH 密钥
2. 将公钥添加到服务器
3. 添加 GitHub Secrets：
   - `STAGING_HOST`
   - `STAGING_USER`
   - `STAGING_SSH_KEY`

## 🔄 工作流说明

### Android CI/CD

```yaml
# 触发条件
on:
  push:
    branches: [ main, master ]
  pull_request:
    branches: [ main, master ]
```

#### Jobs

1. **build-debug-apk**: 构建 Debug 版本
2. **build-release-apk**: 构建 Release 版本（仅 main/master）
3. **lint**: 代码检查

### Backend CI/CD

```yaml
# 触发条件
on:
  push:
    branches: [ main, master ]
    paths:
      - 'backend/**'
```

#### Jobs

1. **build-and-test**: 构建并测试
2. **docker-build**: 构建 Docker 镜像
3. **deploy-staging**: 部署到预发布环境

## ⚙️ 自定义配置

### 修改 Node 版本

编辑 `.github/workflows/backend-ci.yml`：

```yaml
- name: Setup Node.js
  uses: actions/setup-node@v4
  with:
    node-version: '20'  # 修改为所需版本
```

### 修改 Android SDK 版本

编辑 `.github/workflows/android-ci.yml`：

```yaml
- name: Setup Android SDK
  uses: android-actions/setup-android@v2
  with:
    api-level: 34  # 修改为所需 API 级别
```

### 修改 Gradle 参数

在构建命令中添加参数：

```yaml
- name: Build Debug APK
  run: |
    cd app
    ./gradlew assembleDebug --no-daemon -x lint
```

## 📊 监控构建

### 查看构建状态

1. GitHub 仓库首页显示最新构建状态
2. `Actions` 标签查看所有构建历史
3. 邮件通知（可在设置中配置）

### 构建时间

- Debug APK: 约 5-10 分钟
- Release APK: 约 10-15 分钟
- Backend 测试: 约 2-3 分钟

## ❓ 常见问题

### Q: 构建失败怎么办？

1. 点击失败的构建任务
2. 查看构建日志
3. 修复问题并重新推送

### Q: 如何跳过构建？

在提交信息中添加 `[skip ci]`：

```bash
git commit -m "docs: update readme [skip ci]"
```

### Q: 如何手动触发构建？

1. 进入 `Actions` 标签
2. 选择工作流
3. 点击 `Run workflow`

### Q: 存储空间不足？

GitHub Actions 提供：
- 免费版: 500 MB 存储
- Pro 版: 1 GB 存储

可定期清理旧构建产物。

## 🎯 推荐工作流

### 开发流程

1. 从 `main` 创建 `feature/xxx` 分支
2. 开发并提交代码
3. 推送到远程
4. GitHub Actions 自动构建 Debug APK
5. 测试通过后创建 Pull Request
6. 合并到 `main`，自动构建 Release APK

### 部署流程

1. 合并到 `main`
2. GitHub Actions 自动：
   - 构建 Release APK
   - 构建 Docker 镜像
   - 推送到 Docker Hub
3. 在服务器上拉取最新镜像
4. 重启服务

## 📞 获取帮助

- [GitHub Actions 文档](https://docs.github.com/actions)
- [GitHub Community](https://github.community/)

## 🔗 相关链接

- [GitHub Actions Marketplace](https://github.com/marketplace?type=actions)
- [Android GitHub Action](https://github.com/marketplace/actions/android-actions)
- [Docker Build and Push](https://github.com/marketplace/actions/build-and-push-docker-images)
