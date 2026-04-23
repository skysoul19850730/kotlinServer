# 题目管理功能使用指南

## 🎉 功能已完成！

### ✅ 实现的功能

1. **查看所有题目** - 列表展示，带卡片样式
2. **添加新题目** - 弹窗表单，包含标题、内容、答案、难度
3. **编辑题目** - 修改现有题目的所有信息
4. **删除题目** - 带确认对话框的删除功能
5. **难度选择** - Easy/Medium/Hard 下拉选择
6. **实时刷新** - 增删改后自动刷新列表

---

## 🚀 立即使用

### 1️⃣ 同步 Gradle

```powershell
cd E:\serverdemos\kotlin-server
.\gradlew build
```

或在 IntelliJ IDEA 中点击右上角 **Sync Now**

### 2️⃣ 运行管理员应用

**找到并运行** `AdminApp.kt`

点击 `main()` 函数旁边的 ▶️ 运行按钮

### 3️⃣ 启动服务器

1. 在弹出的窗口中，点击 **"Start Server"**
2. 等待状态变为 ✅ **Server Running**

### 4️⃣ 进入题目管理

1. 点击底部导航栏的 **"Questions"** 标签
2. 🎉 题目管理界面出现！

---

## 📝 使用演示

### 添加题目

1. 点击右上角 **"Add Question"** 按钮
2. 填写表单：
   - **Title**: 题目标题（必填）
   - **Question Content**: 题目内容（必填）
   - **Answer**: 答案（必填）
   - **Difficulty**: 选择难度（Easy/Medium/Hard）
3. 点击 **"Add"**
4. ✅ 题目自动添加到列表！

### 编辑题目

1. 找到要编辑的题目卡片
2. 点击 ✏️ **编辑图标**
3. 修改信息
4. 点击 **"Save"**
5. ✅ 题目更新成功！

### 删除题目

1. 找到要删除的题目
2. 点击 🗑️ **删除图标**
3. 确认对话框弹出
4. 点击 **"Delete"**
5. ✅ 题目删除成功！

---

## 🎨 界面预览

```
┌────────────────────────────────────────────────────┐
│  Server Admin Panel                                │
├────────────────────────────────────────────────────┤
│                                                    │
│  Question Management          [➕ Add Question]    │
│                                                    │
│  ┌──────────────────────────────────────────┐    │
│  │ 题目标题                          ✏️ 🗑️  │    │
│  │ Difficulty: Easy                         │    │
│  │ Content: 这是题目内容...                  │    │
│  │ Answer: 这是答案...                      │    │
│  └──────────────────────────────────────────┘    │
│                                                    │
│  ┌──────────────────────────────────────────┐    │
│  │ 另一个题目                        ✏️ 🗑️  │    │
│  │ Difficulty: Hard                         │    │
│  │ Content: ...                             │    │
│  │ Answer: ...                              │    │
│  └──────────────────────────────────────────┘    │
│                                                    │
└────────────────────────────────────────────────────┘
│ Server │ Users │ Questions │ Submissions │
└────────────────────────────────────────────────────┘
```

---

## 🏗️ 技术实现

### MVVM 架构

```
QuestionsManagementScreen (UI)
    ↓
QuestionManagementViewModel (State Management)
    ↓
ReasoningQuestionDao (Database)
    ↓
SQLite Database
```

### 数据流

```kotlin
// 1. UI 触发事件
Button(onClick = { viewModel.createQuestion(...) })

// 2. ViewModel 处理业务逻辑
fun createQuestion(...) {
    dao.insert(...)  // 调用数据库
    loadQuestions()  // 刷新数据
}

// 3. State 自动更新
val state by viewModel.state.collectAsState()

// 4. UI 自动重组
LazyColumn {
    items(state.questions) { question ->
        QuestionCard(question)
    }
}
```

---

## 💡 关键代码

### 题目卡片

```kotlin
@Composable
fun QuestionCard(
    question: ReasoningQuestion,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card {
        Column {
            Text(question.title)
            Text("Difficulty: ${getDifficultyText(question.difficulty)}")
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete")
                }
            }
        }
    }
}
```

### 添加对话框

```kotlin
@Composable
fun AddQuestionDialog(...) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    
    AlertDialog(
        title = { Text("Add New Question") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                // ...
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(title, content, ...) }) {
                Text("Add")
            }
        }
    )
}
```

---

## 🎯 功能特性

### ✅ 表单验证

- 必填字段不能为空
- 空白时按钮自动禁用
- 实时验证反馈

### ✅ 错误处理

- 数据库错误显示在顶部
- 可关闭的错误提示卡片
- 友好的错误信息

### ✅ 用户体验

- 加载状态显示
- 空状态提示
- 删除确认对话框
- 操作成功后自动刷新

---

## 📊 数据库字段

### ReasoningQuestions 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键 |
| userId | INT | 创建者ID |
| title | VARCHAR(200) | 题目标题 |
| contentText | TEXT | 题目内容 |
| answerText | TEXT | 答案 |
| difficulty | INT | 难度（1=Easy, 2=Medium, 3=Hard）|
| createdAt | LONG | 创建时间 |
| updatedAt | LONG | 更新时间 |

---

## 🔮 下一步功能

可以继续实现：

### 1️⃣ 用户管理
- 查看所有注册用户
- 删除用户
- 重置密码
- 查看用户活动

### 2️⃣ 答案批改
- 查看学生提交的答案
- 批改/打分
- 添加反馈评语
- 查看批改历史

### 3️⃣ 统计分析
- 题目完成率
- 用户答题统计
- 难度分布图表
- 导出报表

### 4️⃣ 高级功能
- 题目导入/导出（Excel/JSON）
- 批量操作
- 搜索和筛选
- 题目分类/标签

---

## 🎉 总结

**你现在拥有：**

✅ 一个完整的管理员桌面应用  
✅ 服务器控制功能  
✅ 题目完整的增删改查  
✅ Material Design 3 精美界面  
✅ 前后端一体化架构  
✅ 真正的 Compose Desktop API  

**一个项目搞定所有！** 🚀

---

需要我继续实现用户管理或答案批改功能吗？😊
