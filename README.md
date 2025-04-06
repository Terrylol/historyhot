# 历史热搜

历史热搜是一个追踪和展示不同平台热搜数据的 Web 应用。后端使用 Spring Boot 框架，前端使用 React 和 Material UI。应用每天定时获取不同平台的热搜数据并存储到 SQLite 数据库中，用户可以通过前端界面查看最新的热搜信息以及历史热搜数据。

## 功能特点

- 支持多平台热搜数据的获取和展示（目前支持微博、百度、知乎等）
- 定时自动抓取各平台最新热搜数据
- 可按平台筛选查看热搜数据
- 可查看历史热搜数据
- 支持手动触发热搜数据获取

## 技术栈

### 后端
- Spring Boot 3.2.3
- Spring Data JPA
- SQLite 数据库
- RESTful API

### 前端
- React 18
- Material UI 5
- Axios（API 请求）
- React Router（路由管理）

## 部署指南

### 环境要求
- JDK 17 或更高版本
- Node.js 16 或更高版本
- npm 或 yarn

### 后端部署
1. 进入后端目录：
   ```bash
   cd backend
   ```

2. 使用 Maven 构建项目：
   ```bash
   ./mvnw clean package
   ```
   或者 Windows 上：
   ```bash
   mvnw.cmd clean package
   ```

3. 运行 JAR 文件：
   ```bash
   java -jar target/backend-0.0.1-SNAPSHOT.jar
   ```

### 前端部署
1. 进入前端目录：
   ```bash
   cd frontend
   ```

2. 安装依赖：
   ```bash
   npm install
   ```
   或者使用 yarn：
   ```bash
   yarn
   ```

3. 开发模式运行：
   ```bash
   npm start
   ```
   或者：
   ```bash
   yarn start
   ```

4. 构建生产版本：
   ```bash
   npm run build
   ```
   或者：
   ```bash
   yarn build
   ```

5. 将构建好的静态文件部署到 Web 服务器（如 Nginx）或者使用 serve 工具：
   ```bash
   npx serve -s build
   ```

### 使用 Docker 部署（可选）

1. 构建后端 Docker 镜像：
   ```bash
   cd backend
   docker build -t historyhot-backend .
   ```

2. 构建前端 Docker 镜像：
   ```bash
   cd frontend
   docker build -t historyhot-frontend .
   ```

3. 使用 Docker Compose 启动服务：
   ```bash
   docker-compose up -d
   ```

## 自定义配置

### 添加新平台
要添加新的热搜平台，需要：

1. 在数据库中添加平台信息（可通过 API 或初始化脚本）
2. 实现对应的 `PlatformFetcherService` 接口

### 修改定时任务配置
编辑 `application.properties` 文件中的 `scheduler.cron.fetch-trending` 属性来调整定时任务的执行频率。

### 数据库位置
默认情况下，SQLite 数据库文件 `historyhot.db` 将在应用程序运行目录中创建。可以通过修改 `application.properties` 中的 `spring.datasource.url` 属性来自定义数据库文件的位置。

## 访问应用
- 后端 API: http://localhost:8080/api
- 前端页面: http://localhost:3000

## 贡献指南

欢迎贡献代码、报告问题或提出建议。请遵循以下步骤：

1. Fork 项目仓库
2. 创建您的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交您的更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 开启一个 Pull Request
