# 汽车销售系统 - 移动端

基于 Vue 3 + Vant UI 的移动端应用，使用 Capacitor 打包为 Android APK。

## 技术栈

- **前端框架**: Vue 3 + TypeScript + Composition API
- **UI 组件库**: Vant 4.x
- **状态管理**: Pinia
- **路由管理**: Vue Router 4
- **HTTP 客户端**: Axios
- **构建工具**: Vite
- **移动端打包**: Capacitor 8.x

## 项目结构

```
frontend-mobile/
├── src/
│   ├── api/              # API 服务层（待创建）
│   ├── components/       # 公共组件（待创建）
│   ├── router/           # 路由配置
│   ├── stores/           # Pinia 状态管理
│   ├── types/            # TypeScript 类型定义
│   ├── utils/            # 工具函数
│   ├── views/            # 页面组件
│   ├── App.vue           # 根组件
│   └── main.ts           # 入口文件
├── capacitor.config.ts   # Capacitor 配置
├── vite.config.ts        # Vite 配置
├── tsconfig.json         # TypeScript 配置
└── package.json          # 项目依赖
```

## 开发命令

```bash
# 安装依赖
pnpm install

# 启动开发服务器（端口 5174）
pnpm dev

# 构建生产版本
pnpm build

# 预览生产构建
pnpm preview

# 同步到 Capacitor
pnpm cap:sync

# 打开 Android Studio
pnpm cap:open

# 构建并同步到 Android
pnpm build:android
```

## 与 PC 端的关系

- **PC 端**: `frontend/` 目录 - Vue 3 + Element Plus
- **移动端**: `frontend-mobile/` 目录 - Vue 3 + Vant UI
- **后端**: `backend/` 目录 - Spring Boot（共享）

两个前端项目完全独立，但共享同一个后端 API。

## 开发说明

1. 移动端使用端口 5174（PC 端使用 5173）
2. 开发时通过 Vite 代理访问后端 API（http://localhost:8080）
3. 生产环境需要配置 `.env.production` 中的 `VITE_API_BASE_URL`
4. 使用 Vant 的 rem 适配方案，设计稿宽度为 375px

## 当前进度

✅ 项目初始化完成
✅ Vant UI 依赖安装完成
✅ 基础配置完成（Vite、TypeScript、PostCSS）
✅ 工具层创建完成（request、format）
✅ 状态管理创建完成（user store）
✅ 路由基础配置完成
⏳ 待实现：API 服务层、页面组件、业务功能
