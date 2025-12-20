# 数据库初始化脚本使用说明

## 概述

本目录包含汽车销售管理系统的完整数据库初始化脚本，适用于 openGauss 和 PostgreSQL 数据库。

## 文件说明

- `init.sql` - 完整的数据库初始化脚本，包含：
  - 表结构定义（sys_user, car_info, customer, sales_order）
  - 索引创建
  - 视图创建（v_sales_statistics, v_monthly_sales_trend）
  - 存储过程（proc_create_order）
  - 测试数据

## 使用方法

### 1. 前置条件

- 已安装 openGauss 或 PostgreSQL 数据库
- 已创建目标数据库
- 具有数据库管理员权限

### 2. 执行初始化脚本

#### 使用 psql 命令行工具

```bash
# 连接到数据库并执行脚本
psql -U username -d database_name -f init.sql

# 或者先连接数据库，再执行脚本
psql -U username -d database_name
\i init.sql
```

#### 使用 openGauss gsql 工具

```bash
gsql -U username -d database_name -f init.sql
```

### 3. 验证安装

执行以下查询验证数据是否正确初始化：

```sql
-- 查看所有表
\dt

-- 查看所有视图
\dv

-- 查看所有函数
\df proc_create_order

-- 查询数据统计
SELECT '用户数量' AS item, COUNT(*) AS count FROM sys_user
UNION ALL
SELECT '车辆数量', COUNT(*) FROM car_info
UNION ALL
SELECT '在库车辆', COUNT(*) FROM car_info WHERE status = 0
UNION ALL
SELECT '已售车辆', COUNT(*) FROM car_info WHERE status = 2
UNION ALL
SELECT '客户数量', COUNT(*) FROM customer
UNION ALL
SELECT '订单数量', COUNT(*) FROM sales_order;
```

## 数据库设计要点

### 主键设计

- **禁止使用自增主键**：所有表的主键均使用 BIGINT 类型
- **雪花算法**：实际应用中应在应用层使用雪花算法生成唯一ID
- **测试数据**：脚本中使用固定ID值便于测试

### 约束设计

1. **唯一约束**：
   - sys_user.username
   - car_info.vin
   - customer.phone
   - customer.id_card
   - sales_order.order_no

2. **外键约束**：
   - sales_order.sales_user_id → sys_user.id
   - sales_order.customer_id → customer.id
   - sales_order.car_id → car_info.id

3. **检查约束**：
   - 用户角色：ADMIN, SALESPERSON
   - 车辆状态：0-在库, 1-锁定, 2-已售
   - 订单状态：1-已完成, 2-已取消
   - 价格必须大于0

### 索引设计

为高频查询字段创建索引：
- car_info: vin, brand, status, price
- customer: phone, name
- sales_order: sales_user_id, customer_id, car_id, order_date, order_no
- sys_user: username

## 核心功能

### 1. 销售统计视图 (v_sales_statistics)

按品牌、型号、销售员统计销售数据：

```sql
SELECT * FROM v_sales_statistics 
ORDER BY total_amount DESC;
```

### 2. 月度销售趋势视图 (v_monthly_sales_trend)

按月统计销售趋势：

```sql
SELECT * FROM v_monthly_sales_trend 
ORDER BY month DESC 
LIMIT 12;
```

### 3. 订单创建存储过程 (proc_create_order)

原子性地创建订单并更新车辆状态：

```sql
-- 调用存储过程创建订单
SELECT * FROM proc_create_order(
    1002,      -- 销售员ID
    3005,      -- 客户ID
    2004,      -- 车辆ID
    275000.00  -- 实际成交价
);

-- 返回结果：
-- p_order_id: 创建的订单ID
-- p_result_code: 0-成功, 非0-失败
-- p_result_msg: 结果消息
```

**存储过程特性**：
- 自动检查车辆状态（必须为"在库"）
- 使用 FOR UPDATE 行锁防止并发问题
- 原子性操作：创建订单 + 更新车辆状态
- 完整的错误处理和事务回滚

## 测试数据说明

### 用户账号

| 用户名 | 密码 | 角色 | 姓名 |
|--------|------|------|------|
| admin | 123456 | ADMIN | 系统管理员 |
| sales001 | 123456 | SALESPERSON | 张三 |
| sales002 | 123456 | SALESPERSON | 李四 |
| sales003 | 123456 | SALESPERSON | 王五 |

### 车辆数据

- 在库车辆：10辆（ID: 2001-2010）
- 已售车辆：3辆（ID: 2011-2013）
- 品牌包括：奔驰、宝马、奥迪、特斯拉、比亚迪、丰田、本田、大众、日产、马自达

### 客户数据

- 10位测试客户（ID: 3001-3010）
- 包含完整的姓名、手机号、身份证号、地址信息

### 订单数据

- 3笔已完成订单（ID: 4001-4003）
- 关联已售车辆和客户信息

## 常见问题

### Q1: 如何重新初始化数据库？

直接重新执行 `init.sql` 脚本，脚本会先删除所有已存在的对象。

### Q2: 如何修改测试用户密码？

脚本中的密码使用 BCrypt 加密。如需修改：
1. 使用 BCrypt 工具生成新密码的哈希值
2. 替换 INSERT 语句中的 password 字段值

### Q3: 存储过程中的ID生成方式

脚本中使用时间戳模拟ID生成，实际生产环境应：
1. 在应用层使用雪花算法生成ID
2. 将生成的ID作为参数传入存储过程

### Q4: 如何扩展测试数据？

参考现有 INSERT 语句格式，添加更多测试数据。注意：
- 确保主键唯一
- 确保唯一约束字段不重复
- 外键引用必须存在

## 技术规范

- **数据库版本**：openGauss 3.x / PostgreSQL 12+
- **字符编码**：UTF-8
- **时区**：使用数据库默认时区
- **命名规范**：
  - 表名：小写字母 + 下划线
  - 字段名：小写字母 + 下划线
  - 索引名：idx_ + 表名 + 字段名
  - 视图名：v_ + 描述
  - 存储过程：proc_ + 功能描述

## 课程设计要求符合性

✅ 禁止使用自增主键（使用 BIGINT 类型）  
✅ 使用外键约束保证引用完整性  
✅ 创建视图用于复杂查询  
✅ 创建存储过程处理事务逻辑  
✅ 创建索引优化查询性能  
✅ 符合第三范式（3NF）  
✅ 使用约束保证数据一致性

