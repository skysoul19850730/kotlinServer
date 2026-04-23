# 管理员桌面应用说明

## 🎉 项目结构（前后端一体）

```
kotlin-server/
├── src/main/kotlin/com/example/
│   ├── Application.kt              # 纯后端入口（命令行运行）
│   ├── AdminApp.kt                 # 管理员桌面应用入口（GUI）⭐
│   │
│   ├── ui/                         # 桌面 UI 层（NEW!）
│   │   ├── admin/
│   │   │   ├── ServerManager.kt   # 服务器控制器
│   │   │   └── AdminScreen.kt     # 管理界面
│   │   ├── theme/
│   │   │   └── Theme.kt           # Material 主题
│   │   └── components/
│   │
│   ├── controllers/                # 后端 API 控制器
│   ├── services/                   # 业务逻辑
│   ├── database/                   # 数据库
│   ├── models/                     # 数据模型
│   └── plugins/                    # Ktor 插件
│
└── build.gradle.kts                # 同时支持服务器和桌面 UI
```

---

## 🚀 运行方式

### 方式 1：管理员桌面应用（推荐）⭐⭐⭐⭐⭐

**在 IntelliJ IDEA 中：**

1. 打开项目 `kotlin-server`
2. 找到 `AdminApp.kt`
3. 点击 `main()` 函数旁边的 ▶️ 运行按钮
4. 🎉 桌面窗口弹出！

**功能**：
- ✅ 图形界面启动/停止服务器
- ✅ 用户管理（开发中）
- ✅ 题目管理（开发中）
- ✅ 答案批改（开发中）

**或使用 Gradle：**
```powershell
.\gradlew run
```

---

### 方式 2：纯后端服务器（无 GUI）

**在 IntelliJ IDEA 中：**

1. 找到 `Application.kt`
2. 运行 `main()` 函数
3. 命令行启动，无界面

**或打包成 JAR：**
```powershell
.\gradlew fatJar
java -jar build\libs\kotlin-server.jar
```

---

## 🎯 两种入口对比

| 特性 | AdminApp.kt | Application.kt |
|------|------------|---------------|
| **界面** | ✅ 图形界面 | ❌ 命令行 |
| **服务器控制** | ✅ 一键启动/停止 | ⚠️ 需要手动重启 |
| **用户管理** | ✅ GUI 界面 | ❌ 需要数据库工具 |
| **题目管理** | ✅ GUI 界面 | ❌ 需要 API 调用 |
| **适用场景** | 管理员使用 | 服务器部署 |

---

## 📱 管理员应用界面

### 1. 服务器控制页

```
┌─────────────────────────────────┐
│  Server Admin Panel             │
├─────────────────────────────────┤
│                                 │
│         ✅ Server Running       │
│                                 │
│  ┌───────────────────────────┐ │
│  │ Status                    │ │
│  │ Server is running on      │ │
│  │ http://localhost:8080     │ │
│  │ Port: 8080                │ │
│  └───────────────────────────┘ │
│                                 │
│  [▶️ Start Server] [⏹️ Stop]   │
│                                 │
└─────────────────────────────────┘
│ Server │ Users │ Questions │ Submissions │
└─────────────────────────────────┘
```

### 2. 题目管理页（规划中）

```
┌─────────────────────────────────┐
│  Question Management            │
├─────────────────────────────────┤
│  [➕ Add New Question]          │
│                                 │
│  ┌───────────────────────────┐ │
│  │ ID │ Title │ Difficulty   │ │
│  ├────┼───────┼──────────────┤ │
│  │ 1  │ 题目1 │ Easy   [✏️][❌]│ │
│  │ 2  │ 题目2 │ Hard   [✏️][❌]│ │
│  └───────────────────────────┘ │
└─────────────────────────────────┘
```

---

## 🔧 开发计划

### ✅ 已完成
- [x] 项目结构整合
- [x] Compose Desktop 集成
- [x] 服务器启动/停止控制
- [x] 底部导航栏
- [x] Material Design 主题

### 🚧 开发中
- [ ] 用户管理界面
  - [ ] 查看所有用户列表
  - [ ] 删除用户
  - [ ] 重置密码
- [ ] 题目管理界面 ⭐
  - [ ] 查看所有题目
  - [ ] 添加新题目
  - [ ] 编辑题目
  - [ ] 删除题目
- [ ] 答案批改界面
  - [ ] 查看提交列表
  - [ ] 批改/打分
  - [ ] 添加反馈

---

## 💡 技术亮点

### 1. 前后端一体化

```kotlin
// 同一个项目，两个入口

// 入口 1：桌面管理应用
fun main() = application {
    Window(...) {
        AdminApp()
        // 内部可以调用：
        serverManager.startServer()  // 启动后端
    }
}

// 入口 2：纯后端服务器
fun main() {
    embeddedServer(Netty, 8080) { ... }.start()
}
```

**优势**：
- ✅ 一个项目维护
- ✅ 共享所有后端代码
- ✅ 桌面应用直接调用后端 API
- ✅ 不需要网络请求（直接调用）

---

### 2. 真正的 Compose API

```kotlin
@Composable
fun ServerControlScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text("Server Running")
        Button(onClick = { startServer() }) {
            Text("Start")
        }
    }
}
```

**和 Android Compose 完全一样！** ✨

---

## 📦 打包部署

### 打包成桌面应用（.exe）

```powershell
.\gradlew packageMsi
```

生成：`build/compose/binaries/main/msi/Kotlin Server Admin-1.0.0.msi`

双击安装，自带服务器！

---

### 打包成纯服务器（.jar）

```powershell
.\gradlew fatJar
```

生成：`build/libs/kotlin-server.jar`

部署到服务器：
```bash
java -jar kotlin-server.jar
```

---

## 🎯 使用场景

### 场景 1：管理员本地使用

```
1. 双击 "Kotlin Server Admin.exe"
2. 点击 "Start Server" → 服务器启动
3. 切换到 "Questions" 标签
4. 添加、编辑、删除题目
5. 学生通过 Android 应用访问
```

### 场景 2：服务器部署

```
1. 上传 kotlin-server.jar 到服务器
2. 运行：java -jar kotlin-server.jar
3. 配置开机自启
4. 学生从任何地方访问
```

---

## 🎉 总结

**这就是你想要的！** ✨

- ✅ 一个项目，两个入口
- ✅ 桌面 UI + 后端服务器一体
- ✅ 真正的 Compose API
- ✅ 不需要来回切换项目
- ✅ 图形界面管理题目、用户

**下一步**：实现题目管理界面的增删改查功能！
