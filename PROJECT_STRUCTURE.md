# Kotlin Server - 项目结构说明

## ?? 项目架构（分层架构）

```
kotlin-server/
├── src/main/kotlin/com/example/
│   ├── Application.kt              # ?? 主入口（启动服务器）
│   │
│   ├── plugins/                    # ?? 插件配置层
│   │   ├── Routing.kt             # 路由总配置
│   │   ├── Security.kt            # 安全配置（CORS、Session、异常）
│   │   └── Serialization.kt       # JSON 序列化配置
│   │
│   ├── routes/                     # ??? 路由定义层（按模块）
│   │   ├── AuthRoutes.kt          # 认证路由（/api/auth/*）
│   │   └── DiaryRoutes.kt         # 日记路由（/api/diaries/*）
│   │
│   ├── controllers/                # ?? 控制器层（处理HTTP请求）
│   │   ├── AuthController.kt      # 认证控制器
│   │   └── DiaryController.kt     # 日记控制器
│   │
│   ├── services/                   # ?? 服务层（业务逻辑）
│   │   ├── AuthService.kt         # 认证服务
│   │   └── DiaryService.kt        # 日记服务
│   │
│   ├── database/                   # ??? 数据库层
│   │   ├── DatabaseFactory.kt     # 数据库初始化
│   │   ├── tables/                # 表定义
│   │   │   ├── Users.kt          # 用户表
│   │   │   └── Diaries.kt        # 日记表
│   │   └── dao/                   # 数据访问对象
│   │       ├── UserDao.kt        # 用户数据访问
│   │       └── DiaryDao.kt       # 日记数据访问
│   │
│   ├── models/                     # ?? 数据模型层
│   │   ├── User.kt                # 用户模型
│   │   ├── Diary.kt               # 日记模型
│   │   └── ApiResponse.kt         # API 响应模型
│   │
│   └── utils/                      # ??? 工具类
│       └── PasswordUtil.kt        # 密码加密工具
│
└── resources/
    └── logback.xml                 # 日志配置
```

---

## ?? 架构说明

### 1?? **Application.kt** - 主入口
```kotlin
fun main() {
    DatabaseFactory.init()  // 初始化数据库
    embeddedServer(Netty, port = 8080) {
        configureSerialization()  // 配置JSON
        configureSecurity()       // 配置安全
        configureRouting()        // 配置路由
    }.start(wait = true)
}
```
**职责**：启动服务器，加载所有配置

---

### 2?? **plugins/** - 插件配置层

#### Routing.kt - 路由总配置
```kotlin
fun Application.configureRouting() {
    routing {
        get("/") { /* API文档 */ }
        route("/api") {
            authRoutes()    // 加载认证路由
            diaryRoutes()   // 加载日记路由
        }
    }
}
```
**职责**：定义总路由结构，组织各模块路由

#### Security.kt - 安全配置
```kotlin
fun Application.configureSecurity() {
    install(CORS) { /* CORS配置 */ }
    install(Sessions) { /* Session配置 */ }
    install(StatusPages) { /* 异常处理 */ }
}
```
**职责**：配置跨域、会话、异常处理

#### Serialization.kt - 序列化配置
```kotlin
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json() // JSON序列化
    }
}
```
**职责**：配置 JSON 序列化

---

### 3?? **routes/** - 路由定义层

#### AuthRoutes.kt
```kotlin
fun Route.authRoutes() {
    route("/auth") {
        post("/register") { AuthController.register(call) }
        post("/login") { AuthController.login(call) }
        post("/logout") { AuthController.logout(call) }
        get("/me") { AuthController.getCurrentUser(call) }
    }
}
```
**职责**：定义认证相关的所有路由，委托给控制器处理

#### DiaryRoutes.kt
```kotlin
fun Route.diaryRoutes() {
    route("/diaries") {
        post { DiaryController.createDiary(call) }
        get { DiaryController.getAllDiaries(call) }
        get("/{id}") { DiaryController.getDiary(call) }
        put("/{id}") { DiaryController.updateDiary(call) }
        delete("/{id}") { DiaryController.deleteDiary(call) }
    }
}
```
**职责**：定义日记相关的所有路由（RESTful 风格）

---

### 4?? **controllers/** - 控制器层

#### AuthController.kt
```kotlin
object AuthController {
    suspend fun register(call: ApplicationCall) {
        val request = call.receive<RegisterRequest>()
        val result = AuthService.register(request)
        call.respond(result)
    }
}
```
**职责**：
- 接收 HTTP 请求
- 解析请求参数
- 调用 Service 层处理业务
- 返回 HTTP 响应

---

### 5?? **services/** - 服务层（核心业务逻辑）

#### AuthService.kt
```kotlin
object AuthService {
    fun register(request: RegisterRequest): Result<User> {
        // 1. 验证输入
        if (request.password.length < 6) {
            return Result.failure(Exception("密码太短"))
        }
        // 2. 调用 DAO 层
        UserDao.createUser(...)
        // 3. 返回结果
        return Result.success(user)
    }
}
```
**职责**：
- 业务逻辑验证
- 调用 DAO 层操作数据库
- 处理业务流程

#### DiaryService.kt
```kotlin
object DiaryService {
    fun createDiary(userId: Int, request: CreateDiaryRequest): Result<Diary> {
        // 验证 + 调用 DAO
    }
    
    fun getUserDiaries(userId: Int): List<Diary> {
        return DiaryDao.getDiariesByUserId(userId)
    }
}
```
**职责**：日记的增删改查业务逻辑

---

### 6?? **database/** - 数据库层

#### tables/Users.kt - 表定义
```kotlin
object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 60)
    val createdAt = long("created_at")
    override val primaryKey = PrimaryKey(id)
}
```
**职责**：定义数据库表结构

#### dao/UserDao.kt - 数据访问对象
```kotlin
object UserDao {
    fun createUser(username: String, email: String, password: String): Boolean {
        return transaction {
            Users.insert { /* 插入数据 */ }
        }
    }
    
    fun authenticateUser(username: String, password: String): User? {
        return transaction {
            Users.select { /* 查询 */ }
        }
    }
}
```
**职责**：
- 执行数据库操作（CRUD）
- 不包含业务逻辑
- 只负责数据访问

---

### 7?? **models/** - 数据模型层

#### User.kt
```kotlin
@Serializable
data class User(val id: Int, val username: String, val email: String)

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class RegisterRequest(val username: String, val email: String, val password: String)
```
**职责**：定义数据结构（请求、响应、实体）

---

### 8?? **utils/** - 工具类

#### PasswordUtil.kt
```kotlin
object PasswordUtil {
    fun hashPassword(password: String): String { /* BCrypt */ }
    fun verifyPassword(password: String, hash: String): Boolean { /* BCrypt */ }
}
```
**职责**：通用工具函数（加密、日期、字符串处理等）

---

## ?? 请求流程示例

### 用户登录流程

```
客户端发送请求
    ↓
 POST /api/auth/login
    ↓
【routes/AuthRoutes.kt】接收路由
    ↓
【controllers/AuthController.kt】login() 方法
    - 解析请求体: LoginRequest
    - 调用 AuthService.login()
    ↓
【services/AuthService.kt】login() 方法
    - 验证输入
    - 调用 UserDao.authenticateUser()
    ↓
【database/dao/UserDao.kt】authenticateUser() 方法
    - 查询数据库
    - 验证密码（调用 PasswordUtil）
    - 返回 User 对象
    ↓
【services/AuthService.kt】返回 Result<User>
    ↓
【controllers/AuthController.kt】
    - 设置 Session
    - 构造 ApiResponse
    - 返回 JSON 响应
    ↓
客户端收到响应
```

---

## ?? 如何添加新功能（以"文章管理"为例）

### Step 1: 创建数据模型
```kotlin
// models/Article.kt
@Serializable
data class Article(val id: Int, val title: String, val content: String)

@Serializable
data class CreateArticleRequest(val title: String, val content: String)
```

### Step 2: 定义数据库表
```kotlin
// database/tables/Articles.kt
object Articles : Table("articles") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)
    val title = varchar("title", 200)
    val content = text("content")
    val createdAt = long("created_at")
    override val primaryKey = PrimaryKey(id)
}
```

### Step 3: 创建 DAO
```kotlin
// database/dao/ArticleDao.kt
object ArticleDao {
    fun createArticle(userId: Int, title: String, content: String): Article? {
        return transaction { /* 插入操作 */ }
    }
    
    fun getAllArticles(userId: Int): List<Article> {
        return transaction { /* 查询操作 */ }
    }
}
```

### Step 4: 创建 Service
```kotlin
// services/ArticleService.kt
object ArticleService {
    fun createArticle(userId: Int, request: CreateArticleRequest): Result<Article> {
        // 验证逻辑
        // 调用 ArticleDao
    }
}
```

### Step 5: 创建 Controller
```kotlin
// controllers/ArticleController.kt
object ArticleController {
    suspend fun createArticle(call: ApplicationCall) {
        val session = call.sessions.get<UserSession>() ?: return
        val request = call.receive<CreateArticleRequest>()
        val result = ArticleService.createArticle(session.userId, request)
        call.respond(result)
    }
}
```

### Step 6: 定义路由
```kotlin
// routes/ArticleRoutes.kt
fun Route.articleRoutes() {
    route("/articles") {
        post { ArticleController.createArticle(call) }
        get { ArticleController.getAllArticles(call) }
    }
}
```

### Step 7: 注册路由
```kotlin
// plugins/Routing.kt
fun Application.configureRouting() {
    routing {
        route("/api") {
            authRoutes()
            diaryRoutes()
            articleRoutes()  // ← 添加这行
        }
    }
}
```

---

## ?? 设计原则

### 1. **分层架构**
- Route → Controller → Service → DAO → Database
- 每层职责单一，易于测试和维护

### 2. **模块化**
- 按功能模块组织代码（auth、diary、article...）
- 新增功能只需添加新模块

### 3. **依赖方向**
```
Controller  →  Service  →  DAO  →  Database
           ↓           ↓        ↓
         Models     Models   Tables
```
- 高层依赖低层
- 不能反向依赖

### 4. **统一响应格式**
```kotlin
ApiResponse<T>(
    success: Boolean,
    message: String,
    data: T?
)
```

---

## ?? API 端点总览

### 认证模块
```
POST   /api/auth/register   # 注册
POST   /api/auth/login      # 登录
POST   /api/auth/logout     # 退出
GET    /api/auth/me         # 获取当前用户
```

### 日记模块
```
POST   /api/diaries         # 创建日记
GET    /api/diaries         # 获取所有日记
GET    /api/diaries/{id}    # 获取单个日记
PUT    /api/diaries/{id}    # 更新日记
DELETE /api/diaries/{id}    # 删除日记
```

---

## ?? 运行项目

1. 打开 IntelliJ IDEA
2. 运行 `Application.kt`
3. 访问 http://localhost:8080
4. 看到 API 文档表示启动成功

---

**这就是一个清晰、专业、易扩展的后端项目结构！** ??
