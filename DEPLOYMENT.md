# 部署和网络访问指南

## 🎯 快速开始

### 方法 1：直接运行（开发模式）

在 IntelliJ IDEA 中运行 `Application.kt`

### 方法 2：打包成可执行文件（推荐）⭐

#### 步骤 1：构建 JAR

**双击运行**：
```
build-and-run.bat
```

或在命令行：
```powershell
.\gradlew fatJar
```

#### 步骤 2：运行服务器

**双击运行**：
```
run-server.bat
```

或在命令行：
```powershell
java -jar build\libs\kotlin-server.jar
```

✅ **这样就可以不用打开 IntelliJ IDEA 了！**

---

## 📦 文件说明

| 文件 | 用途 |
|------|------|
| `build-and-run.bat` | 构建 JAR 文件 |
| `run-server.bat` | 运行服务器（双击即可） |
| `build/libs/kotlin-server.jar` | 可执行的 JAR 文件 |

---

## 🌐 网络访问场景

### 场景 1：本机访问

```
你的电脑：
- 后端：http://localhost:8080
- Web 前端：http://localhost:8081
- 桌面应用：直接运行 .exe
```

**需要 Tomcat？** ❌ 不需要

---

### 场景 2：同一 WiFi 设备访问

#### 适用场景：
- 手机访问电脑上的服务器
- 平板访问
- 另一台电脑访问

#### 步骤：

**1. 查看你的电脑 IP**
```powershell
ipconfig
```
找到 **IPv4 地址**，例如：`192.168.1.100`

**2. 配置防火墙**
- Windows 设置 → 防火墙 → 允许应用通过防火墙
- 找到 Java 或 javaw.exe
- 勾选 "专用" 和 "公用" 网络

**3. 其他设备访问**
```
手机浏览器访问：http://192.168.1.100:8080
Android 应用配置：baseUrl = "http://192.168.1.100:8080"
```

**需要 Tomcat？** ❌ 不需要

---

### 场景 3：不同 WiFi 访问（公网访问）

#### 适用场景：
- 朋友在另一个地方访问
- 外网访问自己家里的服务器

#### 方案 A：内网穿透（推荐新手）⭐⭐⭐⭐⭐

使用 **ngrok** 或 **cpolar**：

```powershell
# 1. 下载 ngrok
# https://ngrok.com/download

# 2. 运行
ngrok http 8080

# 3. 会生成一个公网地址
Forwarding: https://xxxx-xx-xx-xx.ngrok.io -> http://localhost:8080
```

**任何人都可以访问**：
```
https://xxxx-xx-xx-xx.ngrok.io
```

**优点**：
- ✅ 简单，5 分钟搞定
- ✅ 不需要公网 IP
- ✅ 自动 HTTPS

**缺点**：
- ⚠️ 免费版地址会变
- ⚠️ 有流量限制

**需要 Tomcat？** ❌ 不需要

---

#### 方案 B：路由器端口映射

**前提条件**：
- 有公网 IP（问你的网络运营商）

**步骤**：

1. **登录路由器**
   - 浏览器访问：`192.168.1.1` 或 `192.168.0.1`
   - 输入管理员密码

2. **设置端口转发**
   - 找到 "端口转发" 或 "虚拟服务器"
   - 添加规则：
     ```
     外部端口：8080
     内部 IP：192.168.1.100（你的电脑）
     内部端口：8080
     协议：TCP
     ```

3. **获取公网 IP**
   ```
   百度搜索：ip
   或访问：https://www.ip.cn/
   ```

4. **访问**
   ```
   http://你的公网IP:8080
   ```

**需要 Tomcat？** ❌ 不需要

---

#### 方案 C：云服务器（最稳定）⭐⭐⭐⭐⭐

**购买云服务器**：
- 阿里云
- 腾讯云
- AWS

**部署步骤**：

```bash
# 1. 上传 JAR 到服务器
scp kotlin-server.jar root@your-server:/home/

# 2. SSH 登录服务器
ssh root@your-server

# 3. 运行
nohup java -jar /home/kotlin-server.jar &

# 4. 访问
http://你的服务器IP:8080
```

**需要 Tomcat？** ❌ 不需要！

---

## 🤔 什么时候需要 Tomcat？

### ❌ 不需要 Tomcat 的情况（你的项目）

**现代框架（内嵌服务器）**：
- ✅ Spring Boot
- ✅ Ktor（你用的）
- ✅ Quarkus
- ✅ Micronaut

**原因**：
```kotlin
embeddedServer(Netty, port = 8080) { ... }
//    ↑
// 内嵌的 Netty 服务器
// 打包成 JAR 后直接运行：java -jar app.jar
```

### ✅ 需要 Tomcat 的情况

**传统 Java Web（老技术）**：
- Servlet/JSP
- 传统 Spring（不是 Spring Boot）
- Struts

**特点**：
- 打包成 `.war` 文件
- 需要部署到 Tomcat/Jetty
- 无法独立运行

---

## 📱 Android 应用访问配置

### 同一 WiFi

```kotlin
// Android 项目配置
object ApiConfig {
    const val BASE_URL = "http://192.168.1.100:8080"  // 你的电脑 IP
}
```

### 不同 WiFi（需要公网访问）

```kotlin
object ApiConfig {
    // 方案 1：使用 ngrok
    const val BASE_URL = "https://xxxx.ngrok.io"
    
    // 方案 2：使用云服务器
    const val BASE_URL = "http://your-server.com:8080"
}
```

### 注意事项

⚠️ **Android 9.0+ 默认禁止 HTTP（只允许 HTTPS）**

解决方法：

1. 在 `AndroidManifest.xml` 添加：
```xml
<application
    android:usesCleartextTraffic="true"
    ...>
```

2. 或者使用 HTTPS（ngrok 自动提供）

---

## 🎯 完整部署流程

### 开发阶段

```
你的电脑（开发机）
├── 后端：IntelliJ IDEA 运行
├── Web 前端：npm run dev
└── 桌面应用：直接运行

访问：localhost:8080
```

### 测试阶段（同一 WiFi）

```
你的电脑（192.168.1.100）
├── 后端：run-server.bat
└── 防火墙：允许 8080 端口

手机/平板（同一 WiFi）
└── 访问：http://192.168.1.100:8080
```

### 生产阶段（公网）

**方案 1：内网穿透**
```
你的电脑
├── 后端：run-server.bat
└── ngrok：ngrok http 8080

任何设备
└── 访问：https://xxxx.ngrok.io
```

**方案 2：云服务器**
```
云服务器（公网 IP）
└── 后端：java -jar kotlin-server.jar

任何设备
└── 访问：http://your-server.com:8080
```

---

## 🛡️ 安全建议

### 开发/测试环境
- ✅ 使用 HTTP（简单）
- ✅ 内网访问

### 生产环境
- ⚠️ 必须使用 HTTPS
- ⚠️ 添加认证（已有 Session）
- ⚠️ 配置防火墙
- ⚠️ 限制请求频率

---

## 📊 总结对比

| 场景 | 配置难度 | 需要 Tomcat | 推荐方案 |
|------|---------|------------|----------|
| 本机开发 | ⭐ | ❌ | IntelliJ 运行 |
| 本机双击运行 | ⭐ | ❌ | JAR + bat 脚本 |
| 同 WiFi 访问 | ⭐⭐ | ❌ | IP + 防火墙 |
| 公网访问（临时） | ⭐⭐ | ❌ | ngrok/cpolar |
| 公网访问（稳定） | ⭐⭐⭐⭐ | ❌ | 云服务器 |
| 传统 War 部署 | ⭐⭐⭐⭐⭐ | ✅ | 不推荐 |

---

## 🎉 结论

**你的项目（Ktor）根本不需要 Tomcat！**

- ✅ 开发：IntelliJ 直接运行
- ✅ 本地：双击 `run-server.bat`
- ✅ 局域网：配置防火墙 + IP 访问
- ✅ 公网：ngrok（简单）或云服务器（稳定）

**Tomcat 是上个时代的东西！** 现代框架都内嵌服务器了！🚀
