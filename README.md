# 阳光图书馆管理系统

基于 Java Swing + MySQL 的图书馆管理系统，支持用户登录、图书查询借还、借书证管理、管理员后台等功能。

## 技术栈

- Java 23 + Swing GUI
- MySQL 8.x + mysql-connector-java 8.0.27
- IntelliJ IDEA

## 环境准备

1. 安装 [JDK 23](https://www.oracle.com/java/technologies/downloads/)
2. 安装 MySQL 8.x
3. 安装 IntelliJ IDEA

## 快速开始

### 1. 导入项目

在 IDEA 中选择 **文件 → 新建 → 来自现有源代码的项目**，选择项目根目录，按提示完成导入。

### 2. 添加依赖

打开 **文件 → 项目结构 → 库**，点击 **+** 选择项目根目录下的 `lib` 文件夹，然后在 **模块 → 依赖** 中勾选 `lib`。

### 3. 导入数据库

1. 连接 MySQL，新建数据库 `yangguotushuguan`，字符集选 `utf8mb4`，排序规则选 `utf8mb4_bin`
2. 运行项目根目录下的 `yangguotushuguan.sql` 导入表结构

### 4. 修改数据库配置

编辑 `src/project/utils/JDBC.java`，将 `user` 和 `password` 改为你自己的 MySQL 账号密码：

```java
private String user = "root";
private String password = "your_password";
```

### 5. 运行

运行 `src/project/ui/Main.java` 的 `main` 方法即可启动。

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 12345 |
| 普通用户 | user | 123 |

> 导入 SQL 文件时会自动插入以上账号。

## 功能概览

- **用户**：注册登录、查询图书、借阅归还、办理查看借书证
- **管理员**：管理图书、管理借书证、管理用户
- **访客**：无需登录即可查询图书
- **隐藏功能**：管理员管理（编辑 `MainFrameA.java` 第 145-146 行，将 `false` 改为 `true`）

## 项目结构

```
src/
├── image/              # 图标资源
└── project/
    ├── dao/            # 数据访问层（BookDao, CardDao, RecordDao, UserDao）
    ├── model/          # 实体类（Book, Card, Record, User）
    ├── ui/             # 界面层（登录、主界面、查询、借还、管理等）
    └── utils/          # 工具类（JDBC, Str）
```
