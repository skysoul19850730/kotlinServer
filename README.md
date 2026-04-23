# Kotlin Authentication API Server

纯后端 API 服务器，提供用户注册、登录功能

## ? 功能特性

- ? RESTful API
- ? 用户注册
- ? 用户登录
- ? Session 会话管理
- ? 密码加密存储（BCrypt）
- ? SQLite 数据库
- ? CORS 跨域支持
- ? JSON 响应格式

## ??? 技术栈

- **后端框架**: Kotlin 1.9.22 + Ktor 2.3.7
- **数据库**: SQLite + Exposed ORM
- **密码加密**: BCrypt
- **构建工具**: Gradle (Kotlin DSL)
- **JDK**: 17

## ?? 项目结构

```
kotlin-server/
├── build.gradle.kts              # Gradle 构建配置
├── settings.gradle.kts           # Gradle 设置
├── data/
│   └── users.db                  # SQLite 数据库（自动创建）
├── src/
│   └── main/
│       ├── kotlin/com/example/
│       │   ├── Application.kt    # 主应用程序 + API 路由
│       │   ├── Database.kt       # 数据库模型和操作
│       │   └── Sessions.kt       # Session 管理
│       └── resources/
│           └── logback.xml       # 日志配置
└── README.md
```

## ?? 如何运行

### 使用 IntelliJ IDEA

1. **打开项目**
   - File → Open → 选择 `E:\serverdemos\kotlin-server`
   - 等待 Gradle 同步完成

2. **运行服务器**
   - 打开 `src/main/kotlin/com/example/Application.kt`
   - 点击 `main()` 函数左侧的 ?? 运行按钮

3. **验证启动**
   - 看到日志: `Application started`
   - 访问: http://localhost:8080

### 使用命令行

```powershell
cd E:\serverdemos\kotlin-server
.\gradlew run
```

## ?? API 接口文档

### 基础信息

- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json`
- **端口**: 8080

### 1. 根路径 - API 信息

```http
GET /
```

**响应示例:**
```json
{
  "name": "Kotlin User Authentication API",
  "version": "1.0.0",
  "endpoints": [
    "POST /api/register - 用户注册",
    "POST /api/login - 用户登录",
    "POST /api/logout - 退出登录",
    "GET /api/me - 获取当前用户信息"
  ],
  "frontend": "http://localhost:8081"
}
```

### 2. 用户注册

```http
POST /api/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "123456"
}
```

**成功响应 (200):**
```json
{
  "success": true,
  "message": "注册成功",
  "data": null
}
```

**失败响应 (200):**
```json
{
  "success": false,
  "message": "用户名或邮箱已存在",
  "data": null
}
```

### 3. 用户登录

```http
POST /api/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456"
}
```

**成功响应 (200):**
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com"
  }
}
```

**失败响应 (200):**
```json
{
  "success": false,
  "message": "用户名或密码错误",
  "data": null
}
```

**注意**: 登录成功后会设置 Session Cookie

### 4. 退出登录

```http
POST /api/logout
```

**响应:**
```json
{
  "success": true,
  "message": "退出登录成功",
  "data": null
}
```

### 5. 获取当前用户信息

```http
GET /api/me
```

**成功响应 (200):**
```json
{
  "success": true,
  "message": "获取成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com"
  }
}
```

**未登录响应 (401):**
```json
{
  "success": false,
  "message": "未登录",
  "data": null
}
```

### 6. 健康检查

```http
GET /api/health
```

**响应:**
```json
{
  "status": "ok",
  "timestamp": 1703001234567
}
```

## ?? CORS 配置

服务器已配置 CORS，允许以下域名访问：
- `http://localhost:8081`（前端应用）
- `http://127.0.0.1:8081`

允许的方法：
- GET, POST, PUT, DELETE, OPTIONS

允许携带凭证（Credentials）用于 Session 管理。

## ?? 数据库

**位置**: `./data/users.db`（自动创建）

**Users 表结构:**
```sql
CREATE TABLE Users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    created_at BIGINT NOT NULL
);
```

- 密码使用 BCrypt 加密存储
- 用户名和邮箱必须唯一

## ?? 测试 API

### 使用 curl

```powershell
# 注册用户
curl -X POST http://localhost:8080/api/register `
  -H "Content-Type: application/json" `
  -d ''{"username":"test","email":"test@example.com","password":"123456"}''

# 登录
curl -X POST http://localhost:8080/api/login `
  -H "Content-Type: application/json" `
  -d ''{"username":"test","password":"123456"}'' `
  -c cookies.txt

# 获取用户信息（需要 Cookie）
curl http://localhost:8080/api/me -b cookies.txt

# 退出登录
curl -X POST http://localhost:8080/api/logout -b cookies.txt
```

### 使用 Postman

1. 创建新的 Collection
2. 添加请求（如上 API 文档）
3. 注意勾选 "Send cookies" 以携带 Session

### 使用 IntelliJ IDEA HTTP Client

创建 `test.http` 文件：

```http
### 注册用户
POST http://localhost:8080/api/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "123456"
}

### 登录
POST http://localhost:8080/api/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456"
}

### 获取当前用户
GET http://localhost:8080/api/me

### 退出登录
POST http://localhost:8080/api/logout
```

## ?? 与前端集成

本 API 服务器设计用于与前端 Compose for Web 应用配合使用：

**前端项目**: `E:\serverdemos\compose-web-frontend`

运行步骤：
1. 先启动本后端服务器（端口 8080）
2. 再启动前端应用（端口 8081）
3. 前端通过 CORS 调用本 API

## ?? 常见问题

### 端口 8080 被占用
修改 `Application.kt` 中的端口：
```kotlin
embeddedServer(Netty, port = 8080, ...) // 改成其他端口
```

### 数据库文件找不到
程序会自动创建 `./data/users.db`，确保有写入权限

### CORS 错误
检查前端访问的域名是否在 CORS 配置中

## ?? 开发笔记

- Session 有效期：24 小时
- 密码最小长度：6 位
- 所有 API 返回统一的 `ApiResponse` 格式
- 使用 BCrypt 加密密码，不可逆

## ?? 后续改进建议

- [ ] 添加 JWT Token 认证
- [ ] 添加邮箱验证
- [ ] 添加密码重置功能
- [ ] 添加请求频率限制
- [ ] 添加 API 文档（Swagger/OpenAPI）
- [ ] 使用 PostgreSQL/MySQL 替代 SQLite
- [ ] 部署到云服务器

---

**项目类型**: RESTful API Server  
**前端项目**: compose-web-frontend  
**作者**: Kotlin 学习者  
**日期**: 2024  
