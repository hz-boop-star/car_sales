# 需求文档 - 汽车销售系统移动端改造

## 简介

将现有的基于 Element UI 的汽车销售管理系统改造为适配移动端的应用，使用 Vant UI 组件库重新实现，以便打包成 APK 后能够正常显示和使用。本次改造采用 MVP 策略，优先实现核心功能。

## 术语表

- **System**: 汽车销售管理系统移动端应用
- **User**: 使用系统的销售人员或管理员
- **Vant_UI**: 移动端 Vue 3 组件库
- **Backend_API**: 现有的后端 API 接口
- **APK**: Android 应用安装包

## 需求

### 需求 1：用户认证

**用户故事：** 作为用户，我希望能够在移动端登录系统，以便访问业务功能。

#### 验收标准

1. WHEN 用户输入用户名和密码并点击登录按钮 THEN THE System SHALL 调用后端登录接口进行身份验证
2. WHEN 登录成功 THEN THE System SHALL 存储 token 并跳转到首页
3. WHEN 登录失败 THEN THE System SHALL 显示错误提示信息
4. WHEN 用户未登录访问需要认证的页面 THEN THE System SHALL 重定向到登录页面
5. THE System SHALL 在移动端屏幕上正确显示登录表单和按钮

### 需求 2：数据概览

**用户故事：** 作为用户，我希望在首页看到关键业务数据的概览，以便快速了解业务状况。

#### 验收标准

1. WHEN 用户进入首页 THEN THE System SHALL 显示总车辆数、可售车辆数、已售车辆数和总收入等统计数据
2. THE System SHALL 以移动端友好的卡片形式展示统计数据
3. WHEN 数据加载失败 THEN THE System SHALL 显示错误提示
4. THE System SHALL 在移动端屏幕上合理布局统计卡片（单列或双列）

### 需求 3：车辆列表查询

**用户故事：** 作为用户，我希望能够查看和搜索车辆列表，以便了解库存情况。

#### 验收标准

1. WHEN 用户进入车辆列表页面 THEN THE System SHALL 显示分页的车辆列表
2. WHEN 用户下拉刷新 THEN THE System SHALL 重新加载车辆列表数据
3. WHEN 用户上拉加载 THEN THE System SHALL 加载下一页数据
4. WHEN 用户点击搜索按钮 THEN THE System SHALL 显示搜索表单（品牌、状态、价格区间）
5. WHEN 用户提交搜索条件 THEN THE System SHALL 根据条件筛选车辆列表
6. WHEN 用户点击车辆项 THEN THE System SHALL 显示车辆详情
7. THE System SHALL 在列表中显示车辆的品牌、型号、价格、状态等关键信息
8. THE System SHALL 使用移动端友好的列表组件展示数据

### 需求 4：车辆详情查看

**用户故事：** 作为用户，我希望能够查看车辆的详细信息，以便了解车辆的完整情况。

#### 验收标准

1. WHEN 用户点击车辆列表中的某一项 THEN THE System SHALL 显示该车辆的详细信息
2. THE System SHALL 显示 VIN 码、品牌、型号、颜色、年份、价格、状态、采购日期等信息
3. THE System SHALL 使用移动端友好的布局展示详情信息
4. WHEN 用户点击返回按钮 THEN THE System SHALL 返回车辆列表页面

### 需求 5：订单列表查询

**用户故事：** 作为用户，我希望能够查看和搜索订单列表，以便了解销售情况。

#### 验收标准

1. WHEN 用户进入订单列表页面 THEN THE System SHALL 显示分页的订单列表
2. WHEN 用户下拉刷新 THEN THE System SHALL 重新加载订单列表数据
3. WHEN 用户上拉加载 THEN THE System SHALL 加载下一页数据
4. WHEN 用户点击搜索按钮 THEN THE System SHALL 显示搜索表单（日期范围、销售员、状态）
5. WHEN 用户提交搜索条件 THEN THE System SHALL 根据条件筛选订单列表
6. WHEN 用户点击订单项 THEN THE System SHALL 显示订单详情
7. THE System SHALL 在列表中显示订单号、客户姓名、车辆信息、成交价、订单日期等关键信息
8. THE System SHALL 使用移动端友好的列表组件展示数据

### 需求 6：订单详情查看

**用户故事：** 作为用户，我希望能够查看订单的详细信息，以便了解交易的完整情况。

#### 验收标准

1. WHEN 用户点击订单列表中的某一项 THEN THE System SHALL 显示该订单的详细信息
2. THE System SHALL 显示订单号、客户信息、车辆信息、价格信息、销售员、状态、备注等信息
3. THE System SHALL 使用移动端友好的布局展示详情信息
4. WHEN 用户点击返回按钮 THEN THE System SHALL 返回订单列表页面

### 需求 7：底部导航更新

**用户故事：** 作为用户，我希望通过底部导航栏快速访问所有主要功能，以便提高操作效率。

#### 验收标准

1. THE System SHALL 在应用底部显示固定的导航栏
2. THE System SHALL 在导航栏中包含首页、车辆、客户、订单、我的五个主要入口
3. WHEN 用户点击导航项 THEN THE System SHALL 切换到对应的页面
4. THE System SHALL 高亮显示当前激活的导航项
5. THE System SHALL 使用移动端友好的图标和文字展示导航项

### 需求 8：响应式布局

**用户故事：** 作为用户，我希望应用能够适配不同尺寸的移动设备屏幕，以便在各种设备上正常使用。

#### 验收标准

1. THE System SHALL 使用响应式布局适配不同屏幕尺寸
2. THE System SHALL 在小屏幕设备上正确显示所有内容
3. THE System SHALL 确保文字大小和按钮尺寸适合触摸操作
4. THE System SHALL 避免横向滚动条的出现
5. WHEN 设备方向改变 THEN THE System SHALL 自动调整布局

### 需求 9：API 兼容性

**用户故事：** 作为开发者，我希望移动端应用能够复用现有的后端 API，以便减少开发工作量。

#### 验收标准

1. THE System SHALL 使用现有的后端 API 接口
2. THE System SHALL 保持与现有 API 的请求和响应格式一致
3. THE System SHALL 正确处理 API 返回的数据结构
4. WHEN API 返回错误 THEN THE System SHALL 显示友好的错误提示
5. THE System SHALL 在请求头中携带认证 token

### 需求 10：加载状态和错误处理

**用户故事：** 作为用户，我希望在数据加载时看到加载提示，在出错时看到明确的错误信息，以便了解系统状态。

#### 验收标准

1. WHEN 数据正在加载 THEN THE System SHALL 显示加载指示器
2. WHEN 数据加载完成 THEN THE System SHALL 隐藏加载指示器并显示数据
3. WHEN 网络请求失败 THEN THE System SHALL 显示错误提示信息
4. WHEN 用户点击重试按钮 THEN THE System SHALL 重新发起请求
5. THE System SHALL 使用移动端友好的加载和错误提示组件

### 需求 11：客户列表查询

**用户故事：** 作为销售员，我希望能够在移动端查看和搜索客户列表，以便随时了解客户信息。

#### 验收标准

1. WHEN 用户进入客户列表页面 THEN THE System SHALL 显示分页的客户列表
2. WHEN 用户下拉刷新 THEN THE System SHALL 重新加载客户列表数据
3. WHEN 用户上拉加载 THEN THE System SHALL 加载下一页数据
4. WHEN 用户点击搜索按钮 THEN THE System SHALL 显示搜索表单（姓名、手机号）
5. WHEN 用户在姓名搜索框输入内容 THEN THE System SHALL 进行模糊搜索
6. WHEN 用户在手机号搜索框输入内容 THEN THE System SHALL 进行精确搜索
7. WHEN 用户点击客户项 THEN THE System SHALL 显示客户详情
8. THE System SHALL 在列表中显示客户姓名、手机号、身份证号等关键信息
9. THE System SHALL 使用移动端友好的列表组件展示数据

### 需求 12：客户详情查看

**用户故事：** 作为销售员，我希望能够查看客户的详细信息，以便了解客户的完整档案。

#### 验收标准

1. WHEN 用户点击客户列表中的某一项 THEN THE System SHALL 显示该客户的详细信息
2. THE System SHALL 显示姓名、手机号、身份证号、性别、地址等信息
3. THE System SHALL 使用移动端友好的布局展示详情信息
4. WHEN 用户点击返回按钮 THEN THE System SHALL 返回客户列表页面
5. THE System SHALL 提供拨打电话的快捷操作（点击手机号直接拨号）

### 需求 13：订单创建

**用户故事：** 作为销售员，我希望能够在移动端快速创建订单，以便在外出时也能完成销售流程。

#### 验收标准

1. WHEN 用户点击创建订单按钮 THEN THE System SHALL 显示订单创建页面
2. THE System SHALL 提供客户选择功能（支持搜索）
3. THE System SHALL 提供车辆选择功能（只显示在库状态的车辆）
4. WHEN 用户选择车辆后 THEN THE System SHALL 自动填充车辆原价
5. THE System SHALL 允许用户输入优惠金额
6. WHEN 用户输入优惠金额 THEN THE System SHALL 自动计算成交价
7. THE System SHALL 允许用户选择订单日期（默认当天）
8. THE System SHALL 允许用户输入备注信息
9. WHEN 用户提交订单 THEN THE System SHALL 调用后端 API 创建订单
10. WHEN 订单创建成功 THEN THE System SHALL 显示成功提示并跳转到订单列表
11. WHEN 订单创建失败 THEN THE System SHALL 显示错误信息
12. THE System SHALL 使用分步表单或简化表单提升移动端体验

### 需求 14：个人中心

**用户故事：** 作为用户，我希望能够查看个人信息并安全退出系统，以便管理我的账户。

**功能说明：**
- ✅ 查看个人信息（用户名、真实姓名、角色、手机、邮箱）
- ✅ 退出登录
- ❌ 不包括修改密码功能（后端暂无相关接口）
- ❌ 不包括修改个人信息功能（后端暂无相关接口）

#### 验收标准

1. WHEN 用户进入个人中心页面 THEN THE System SHALL 调用 `/api/auth/info` 接口获取用户信息
2. THE System SHALL 显示用户名、真实姓名、角色（管理员/销售员）、手机号、邮箱等基本信息
3. THE System SHALL 显示用户角色标识（管理员/销售员）
4. WHEN 用户信息包含手机号 THEN THE System SHALL 提供点击拨号的快捷操作
5. THE System SHALL 提供退出登录功能按钮
6. WHEN 用户点击退出登录 THEN THE System SHALL 显示确认对话框
7. WHEN 用户确认退出 THEN THE System SHALL 调用 `/api/auth/logout` 接口
8. WHEN 退出成功 THEN THE System SHALL 清除本地存储的 token 和用户信息
9. WHEN 退出成功 THEN THE System SHALL 跳转到登录页面
10. THE System SHALL 使用移动端友好的布局展示个人中心

### 需求 15：底部导航更新

**用户故事：** 作为用户，我希望通过底部导航栏快速访问所有主要功能，以便提高操作效率。

#### 验收标准

1. THE System SHALL 在应用底部显示固定的导航栏
2. THE System SHALL 在导航栏中包含首页、车辆、客户、订单、我的五个主要入口
3. WHEN 用户点击导航项 THEN THE System SHALL 切换到对应的页面
4. THE System SHALL 高亮显示当前激活的导航项
5. THE System SHALL 使用移动端友好的图标和文字展示导航项
