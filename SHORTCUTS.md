# 创建桌面快捷方式指南

## 🖱️ 方法 1：直接创建快捷方式（最简单）

### 步骤：

1. **右键点击** `run-server.bat`
2. 选择 **"发送到" → "桌面快捷方式"**
3. ✅ 完成！双击桌面图标即可启动服务器

---

## 🎨 方法 2：创建带图标的快捷方式

### 步骤 1：创建快捷方式

1. 右键桌面 → 新建 → 快捷方式
2. 输入位置：
   ```
   E:\serverdemos\kotlin-server\run-server.bat
   ```
3. 名称：`Kotlin Server`
4. 点击完成

### 步骤 2：自定义图标（可选）

1. 右键快捷方式 → 属性
2. 点击 "更改图标"
3. 选择一个图标或浏览自定义图标
4. 确定

---

## ⚡ 方法 3：开机自启动

### 让服务器开机自动运行

1. 按 `Win + R`
2. 输入：`shell:startup`
3. 回车打开启动文件夹
4. 将 `run-server.bat` 的快捷方式复制到这里
5. ✅ 下次开机自动启动服务器！

**注意**：可能需要配置为最小化启动：
- 右键快捷方式 → 属性
- 运行方式：选择 "最小化"

---

## 🔧 方法 4：创建 Windows 服务（高级）

### 使用 NSSM（Non-Sucking Service Manager）

1. **下载 NSSM**
   - https://nssm.cc/download
   - 解压到任意位置

2. **安装服务**
   ```powershell
   # 以管理员身份运行 PowerShell
   cd path\to\nssm
   .\nssm.exe install KotlinServer
   ```

3. **配置**
   - Path：`C:\Program Files\Java\jdk-17\bin\java.exe`
   - Startup directory：`E:\serverdemos\kotlin-server\build\libs`
   - Arguments：`-jar kotlin-server.jar`

4. **启动服务**
   ```powershell
   net start KotlinServer
   ```

**优点**：
- ✅ 后台运行
- ✅ 开机自启
- ✅ 崩溃自动重启
- ✅ 可以用服务管理器控制

---

## 📱 桌面应用的打包

### Compose Desktop 打包成 .exe

在 `compose-desktop-app` 项目中：

```powershell
# 打包成 Windows 安装程序
.\gradlew packageMsi

# 或打包成独立 exe
.\gradlew createDistributable
```

**生成的文件**：
```
build/compose/binaries/main/
├── msi/           # Windows 安装程序
└── app/           # 独立应用文件夹
    └── Kotlin Auth App.exe  # 可执行文件
```

**创建桌面快捷方式**：
- 右键 `.exe` 文件
- 发送到 → 桌面快捷方式

---

## 🎯 最终效果

### 桌面上的图标：

```
桌面
├── 🟦 Kotlin Server.lnk       (后端服务器)
├── 🟩 Kotlin Auth App.lnk     (桌面客户端)
└── 🌐 Web App.url             (Web 前端)
```

**使用流程**：
1. 双击 "Kotlin Server" → 后端启动
2. 双击 "Kotlin Auth App" → 桌面客户端启动
3. 或打开浏览器 → Web 前端

---

## 🌐 创建 Web 快捷方式

### 创建 URL 快捷方式

1. 右键桌面 → 新建 → 快捷方式
2. 输入位置：`http://localhost:8081`
3. 名称：`Kotlin Web App`
4. ✅ 双击即可在浏览器打开

---

## 🔄 完整启动流程

### 方案 A：手动启动

```
1. 双击 "Kotlin Server" → 启动后端
2. 等待 "Application started" 提示
3. 双击 "Kotlin Auth App" → 启动桌面应用
```

### 方案 B：一键启动（批处理）

创建 `start-all.bat`：

```batch
@echo off
echo Starting Kotlin Server...
start "Kotlin Server" run-server.bat

echo Waiting for server to start...
timeout /t 5 /nobreak

echo Starting Desktop App...
start "" "E:\serverdemos\compose-desktop-app\build\compose\binaries\main\app\Kotlin Auth App.exe"

echo All applications started!
```

**双击 `start-all.bat` → 自动启动所有！**

---

## 💡 推荐配置

### 开发阶段
- ✅ 使用 IntelliJ IDEA 运行
- ✅ 方便调试和修改

### 日常使用
- ✅ 桌面快捷方式（双击启动）
- ✅ JAR 文件（无需 IDE）

### 演示/分享
- ✅ 打包成安装程序
- ✅ 发给别人直接安装

### 生产环境
- ✅ Windows 服务（后台运行）
- ✅ 或云服务器部署

---

## 📦 分发给其他人

### 后端分发

**打包内容**：
```
kotlin-server-release/
├── kotlin-server.jar      # JAR 文件
├── run-server.bat         # 启动脚本
└── README.txt            # 说明文档
```

**说明文档内容**：
```
使用说明：
1. 确保安装 Java 17
2. 双击 run-server.bat
3. 访问 http://localhost:8080
```

### 桌面应用分发

**选项 1：MSI 安装程序**
```
双击安装 → 自动配置 → 开始菜单快捷方式
```

**选项 2：绿色版（免安装）**
```
kotlin-app-portable/
├── Kotlin Auth App.exe
├── runtime/              # JRE 运行时
└── resources/
```

---

## ✅ 总结

| 方法 | 适用场景 | 难度 |
|------|---------|------|
| 桌面快捷方式 | 个人使用 | ⭐ |
| 开机自启动 | 长期运行 | ⭐⭐ |
| Windows 服务 | 生产环境 | ⭐⭐⭐⭐ |
| 打包分发 | 给别人用 | ⭐⭐⭐ |

**推荐**：
- 自己用 → 桌面快捷方式
- 服务器 → Windows 服务或云部署
- 给别人 → 打包成安装程序

🎉 **不用每次打开 IntelliJ IDEA 了！**
