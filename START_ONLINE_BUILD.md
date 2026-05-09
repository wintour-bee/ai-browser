# 🚀 AI Browser - 在线构建立即开始

## ⚡ 3 步完成云端构建

### 第 1 步：运行一键脚本

打开 PowerShell（管理员），运行：

```powershell
cd C:\Users\winto\CodeBuddy\20260509144732
.\scripts\run-all.ps1 -GitHubUsername "你的GitHub用户名"
```

例如：
```powershell
.\scripts\run-all.ps1 -GitHubUsername "john"
```

### 第 2 步：创建 GitHub 仓库

脚本会打开 GitHub 创建仓库页面，填写：
- **Repository name**: `ai-browser`（或你喜欢的名字）
- **不要勾选** "Initialize this repository with a README"

点击 "Create repository"。

### 第 3 步：推送代码并自动构建

回到 PowerShell，运行：

```powershell
git branch -M main
git push -u origin main
```

**完成！** GitHub Actions 会自动开始构建。

---

## 📊 构建状态查看

### 查看构建进度

1. 访问: https://github.com/YOUR_USERNAME/ai-browser/actions
2. 点击正在运行的工作流
3. 查看实时构建日志

### 下载构建产物

构建完成后（约 5-10 分钟）：

1. 进入 `Actions` 页面
2. 选择 `Build Debug APK`
3. 点击构建任务名称
4. 页面底部找到 `Artifacts`
5. 点击 `debug-apk` 下载

---

## 🎯 完全无需安装任何软件！

### 传统方式 vs 在线构建

| 项目 | 传统方式 | ☁️ 在线构建 |
|------|---------|------------|
| Java JDK | ❌ 需要安装 | ✅ 不需要 |
| Android SDK | ❌ 需要安装 | ✅ 不需要 |
| Gradle | ❌ 需要安装 | ✅ 不需要 |
| Node.js | ❌ 需要安装 | ✅ 不需要 |
| PostgreSQL | ❌ 需要安装 | ✅ Docker 自动配置 |
| 构建时间 | 本地决定 | 约 5-10 分钟 |
| 费用 | $0（本地）+ 电费 | ✅ 免费（每月 2000 分钟）|

---

## 🎁 你将获得

### Android 应用
- ✅ Debug APK（可安装到任何 Android 设备测试）
- ✅ Release APK（可发布到应用商店）
- ✅ Lint 代码检查报告

### 后端服务
- ✅ Docker 镜像（自动构建并推送到 Docker Hub）
- ✅ 完整的 CI/CD 流程
- ✅ 自动部署到服务器（可选）

---

## 📚 相关文档

- [在线构建指南](ONLINE_BUILD.md) - 详细的云端构建说明
- [GitHub 设置指南](GITHUB_SETUP.md) - 配置 CI/CD 工作流
- [快速开始](QUICK_START.md) - 其他构建方式
- [项目总结](PROJECT_SUMMARY.md) - 完整功能说明

---

## 🔧 如果你想自定义

### 修改构建配置

编辑 `.github/workflows/android-ci.yml`：

```yaml
# 改 Android API 级别
- name: Setup Android SDK
  uses: android-actions/setup-android@v2
  with:
    api-level: 34  # 改为 33, 32 等
```

### 添加代码签名（Release 构建需要）

1. 生成签名密钥
2. 在 GitHub 仓库设置中添加 Secrets
3. 构建会自动签名

详细步骤: [GitHub 设置指南 - 添加签名密钥](GITHUB_SETUP.md#添加签名密钥)

---

## ❓ 常见问题

### Q: 构建失败怎么办？

1. 点击失败的构建任务
2. 查看构建日志
3. 根据错误信息修复代码
4. 重新推送代码

### Q: 超过免费额度怎么办？

GitHub 每月提供 **2000 分钟**免费构建时间。
- Debug APK: 约 5 分钟
- Release APK: 约 10 分钟

一般情况下不会超限！

### Q: 如何加快构建速度？

1. 使用更少的依赖
2. 优化 Gradle 配置
3. 购买 GitHub Pro（每月 $4）

### Q: 可以构建 iOS 版本吗？

不行，iOS 构建只能在 macOS 上进行。
但可以使用 MacStadium 等云 Mac 服务。

---

## 🎉 成功标志

当你看到：

```
✓ Build Debug APK
✓ Upload Debug APK
```

就说明构建成功了！

---

## 📞 获取帮助

- **GitHub Actions 文档**: https://docs.github.com/actions
- **本项目 Issues**: https://github.com/YOUR_USERNAME/ai-browser/issues
- **在线帮助**: https://github.community/

---

## 🌟 开始吧！

现在就运行：

```powershell
.\scripts\run-all.ps1 -GitHubUsername "你的GitHub用户名"
```

5-10 分钟后，你将拥有自己的 AI Browser APK！

**祝你好运！ 🚀**
