# 悦听 Yueting

悦听是一个面向用户、艺人和管理员的全栈音乐流媒体与社交平台。项目支持音乐上传、在线播放、收藏、歌单管理、榜单生成、艺人身份管理、内容审核和后台运营等核心功能。

本仓库采用前后端分离架构：

- `yueting-server/`：Spring Boot 后端服务
- `yueting-web/`：Vue 3 前端应用

## 功能特性

- 用户注册、登录与 JWT 鉴权
- 音乐浏览、播放、收藏与播放状态管理
- 歌单创建、维护与歌曲管理
- 艺人身份申请、激活与作品上传
- 歌曲、歌单、艺人等多维度内容展示
- 管理后台登录与员工鉴权
- 后台内容审核与平台管理
- 定时任务生成榜单或维护平台数据
- WebSocket 消息连接能力
- 本地歌曲文件存储与 OSS 图片资源管理

## 技术栈

### 后端

- Java 17
- Spring Boot 3.5.11
- Spring Web
- Spring Security
- Spring Data Redis
- Spring Mail
- Spring WebSocket
- MyBatis-Plus 3.5.7
- JWT `io.jsonwebtoken` 0.12.5
- BCrypt
- MySQL
- Redis
- 阿里云 OSS

### 前端

- Vue 3
- Vite 6
- Vue Router 4
- Pinia 3
- Element Plus
- Axios
- `jwt-decode`
- `vue-cropper`
- STOMP over SockJS

## 项目结构

```text
yueting/
├── yueting-server/                 # 后端 Spring Boot 服务
│   ├── src/main/java/               # Java 源码
│   │   └── com/jiangce/yueting/
│   │       ├── config/              # 配置类
│   │       ├── controller/          # 控制器
│   │       │   ├── web/             # 前台用户 API
│   │       │   └── admin/           # 管理后台 API
│   │       ├── domain/              # PO / DTO / VO / Result
│   │       ├── mapper/              # MyBatis Mapper 接口
│   │       ├── service/             # 业务服务
│   │       └── task/                # 定时任务
│   └── src/main/resources/
│       ├── mapper/                  # MyBatis XML 映射文件
│       ├── application.yml          # 基础配置
│       └── application-dev.yml      # 开发环境配置
│
└── yueting-web/                     # 前端 Vue 应用
    ├── src/
    │   ├── api/                     # 前后台 Axios 请求封装
    │   ├── router/                  # Vue Router 路由与守卫
    │   ├── stores/                  # Pinia 状态管理
    │   ├── services/                # WebSocket 等服务
    │   ├── views/                   # 页面视图
    │   └── components/              # 公共组件
    └── vite.config.js               # Vite 配置
```

## 环境要求

请确保本地已安装：

- JDK 17+
- Maven 3.8+
- Node.js 18+ 或 20+
- npm
- MySQL
- Redis

## 快速开始

### 1. 克隆项目

```bash
git clone <your-repository-url>
cd yueting
```

### 2. 配置后端

进入后端目录：

```bash
cd yueting-server
```

根据本地环境修改配置文件：

- `src/main/resources/application.yml`
- `src/main/resources/application-dev.yml`

需要重点配置：

- MySQL 连接信息
- Redis 连接信息
- JWT 密钥
- 阿里云 OSS AccessKey、Bucket、Endpoint
- 本地歌曲文件存储路径 `yueting.song.path`

### 3. 启动后端服务

```bash
mvn spring-boot:run
```

后端默认运行在：

```text
http://localhost:8080
```

也可以在 IDE 中直接运行主类：

```text
com.jiangce.yueting.YuetingServerApplication
```

### 4. 启动前端应用

打开新的终端，进入前端目录：

```bash
cd yueting-web
npm install
npm run dev
```

前端开发服务默认运行在：

```text
http://localhost
```

Vite 开发服务器会将以下请求代理到后端：

- `/api` -> `http://localhost:8080`
- `/ws` -> `http://localhost:8080`

## 构建部署

### 后端构建

```bash
cd yueting-server
mvn clean package
```

构建完成后，可运行生成的 JAR 文件：

```bash
java -jar target/*.jar
```

### 前端构建

```bash
cd yueting-web
npm run build
```

构建产物默认输出到：

```text
yueting-web/dist/
```

本地预览生产构建：

```bash
npm run preview
```

## API 分层约定

后端控制器分为前台和后台两类：

- `controller/web/**`：面向用户侧页面的接口
- `controller/admin/**`：面向管理后台的接口

前端也对应拆分了请求层：

- `src/api/frontend/request.js`：用户侧接口请求，基础路径为 `/api/web`
- `src/api/admin/request.js`：后台管理接口请求，基础路径为 `/api/admin`

开发新功能时，请保持前后台接口、权限和状态管理边界清晰。

## 认证与权限

项目使用 JWT 进行认证：

- 后端登录成功后签发 JWT
- Token 中包含用户权限信息，例如 `ROLE_ARTIST`
- 前端通过 `jwt-decode` 解析 token
- Token 存储在 `localStorage`
- Axios 拦截器会自动携带 token
- 接口返回 `401` 时，前端会自动清理登录状态并跳转登录页

前端路由守卫支持：

- `requiresAuth`：需要普通用户登录
- `requiresArtist`：需要拥有艺人角色
- `requiresArtistActive`：需要艺人身份已激活
- `requiresEmp`：需要后台员工或管理员登录

## 艺人身份生命周期

用户拥有 `ROLE_ARTIST` 角色后，不一定已经完成艺人身份激活。平台会进一步校验艺人身份信息，例如歌手、作词人、作曲人等身份。

前端通过以下状态判断访问权限：

- `isArtist`：用户拥有艺人角色
- `isArtistActive`：艺人身份信息已完善，且 `artist.identities.length > 0`

## 数据访问约定

后端使用 MyBatis-Plus，并大量通过 XML Mapper 完成多表联查。

新增涉及歌曲、歌单、艺人、收藏、榜单等复杂查询时，建议：

1. 在 `mapper/` 中新增或扩展 Mapper 接口
2. 在 `src/main/resources/mapper/` 中编写 XML SQL
3. 在 Service 层组织业务逻辑
4. Controller 只负责参数接收、权限校验入口和结果返回

## 常用命令

### 后端

```bash
cd yueting-server

# 启动开发服务
mvn spring-boot:run

# 构建可执行 JAR
mvn clean package
```

### 前端

```bash
cd yueting-web

# 安装依赖
npm install

# 启动开发服务
npm run dev

# 生产构建
npm run build

# 本地预览
npm run preview
```

## 开发说明

- 本仓库目前未配置专门的前端或后端测试套件。
- 前端开发环境端口为 `80`，如端口被占用，请在 Vite 配置中调整。
- 后端默认使用 `dev` profile。
- 上传歌曲文件使用本地存储路径。
- 图片资源上传到阿里云 OSS。
- 修改认证、角色或路由守卫逻辑时，需要同时关注前端 Pinia Store、Axios 拦截器和后端 Spring Security 配置。

## License

本项目仅用于学习、课程设计或个人项目展示。如需开源发布，请根据实际情况补充许可证信息。
