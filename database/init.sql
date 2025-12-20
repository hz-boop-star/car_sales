-- ============================================================================
-- 汽车销售管理系统 - 数据库初始化脚本
-- Database: openGauss / PostgreSQL Compatible
-- Description: 完整的 DDL 脚本，包含表、索引、视图、存储过程
-- ============================================================================

-- ============================================================================
-- 第一部分：删除已存在的对象（用于重新初始化）
-- ============================================================================

-- 删除视图
DROP VIEW IF EXISTS v_monthly_sales_trend CASCADE;
DROP VIEW IF EXISTS v_sales_statistics CASCADE;

-- 删除存储过程/函数
DROP FUNCTION IF EXISTS proc_create_order(BIGINT, BIGINT, BIGINT, NUMERIC) CASCADE;

-- 删除表（按依赖关系逆序删除）
DROP TABLE IF EXISTS sales_order CASCADE;
DROP TABLE IF EXISTS customer CASCADE;
DROP TABLE IF EXISTS car_info CASCADE;
DROP TABLE IF EXISTS sys_user CASCADE;

-- 删除序列（如果使用）
DROP SEQUENCE IF EXISTS seq_order_id CASCADE;

-- ============================================================================
-- 第二部分：创建核心表结构
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. sys_user 表（系统用户表）
-- ----------------------------------------------------------------------------
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY,                          -- 主键（雪花算法生成，禁止自增）
    username VARCHAR(50) NOT NULL UNIQUE,           -- 用户名（唯一）
    password VARCHAR(255) NOT NULL,                 -- 密码（加密存储）
    real_name VARCHAR(50) NOT NULL,                 -- 真实姓名
    role VARCHAR(20) NOT NULL,                      -- 角色：ADMIN, SALESPERSON
    phone VARCHAR(20),                              -- 联系电话
    email VARCHAR(100),                             -- 邮箱
    status SMALLINT DEFAULT 1,                      -- 状态：0-禁用, 1-启用
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- 约束
    CONSTRAINT chk_user_role CHECK (role IN ('ADMIN', 'SALESPERSON')),
    CONSTRAINT chk_user_status CHECK (status IN (0, 1))
);

COMMENT ON TABLE sys_user IS '系统用户表';
COMMENT ON COLUMN sys_user.id IS '主键ID（雪花算法生成）';
COMMENT ON COLUMN sys_user.username IS '用户名（唯一）';
COMMENT ON COLUMN sys_user.password IS '密码（BCrypt加密）';
COMMENT ON COLUMN sys_user.real_name IS '真实姓名';
COMMENT ON COLUMN sys_user.role IS '角色：ADMIN-管理员, SALESPERSON-销售员';
COMMENT ON COLUMN sys_user.status IS '状态：0-禁用, 1-启用';

-- ----------------------------------------------------------------------------
-- 2. car_info 表（车辆信息表）
-- ----------------------------------------------------------------------------
CREATE TABLE car_info (
    id BIGINT PRIMARY KEY,                          -- 主键（雪花算法生成）
    vin VARCHAR(17) NOT NULL UNIQUE,                -- 车架号（唯一标识，17位）
    brand VARCHAR(50) NOT NULL,                     -- 品牌
    model VARCHAR(100) NOT NULL,                    -- 型号
    color VARCHAR(30),                              -- 颜色
    year INTEGER,                                   -- 年份
    price NUMERIC(12, 2) NOT NULL,                  -- 价格
    status SMALLINT DEFAULT 0,                      -- 状态：0-在库, 1-锁定, 2-已售
    purchase_date DATE,                             -- 进货日期
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- 约束
    CONSTRAINT chk_car_status CHECK (status IN (0, 1, 2)),
    CONSTRAINT chk_car_price CHECK (price > 0),
    CONSTRAINT chk_car_year CHECK (year IS NULL OR (year >= 1900 AND year <= 2100))
);

COMMENT ON TABLE car_info IS '车辆信息表（一车一码）';
COMMENT ON COLUMN car_info.id IS '主键ID（雪花算法生成）';
COMMENT ON COLUMN car_info.vin IS '车架号VIN（17位唯一标识）';
COMMENT ON COLUMN car_info.brand IS '品牌';
COMMENT ON COLUMN car_info.model IS '型号';
COMMENT ON COLUMN car_info.price IS '价格';
COMMENT ON COLUMN car_info.status IS '状态：0-在库, 1-锁定, 2-已售';

-- ----------------------------------------------------------------------------
-- 3. customer 表（客户信息表）
-- ----------------------------------------------------------------------------
CREATE TABLE customer (
    id BIGINT PRIMARY KEY,                          -- 主键（雪花算法生成）
    name VARCHAR(50) NOT NULL,                      -- 姓名
    phone VARCHAR(20) NOT NULL UNIQUE,              -- 手机号（唯一）
    id_card VARCHAR(18) NOT NULL UNIQUE,            -- 身份证号（唯一）
    gender CHAR(1),                                 -- 性别：M-男, F-女
    address VARCHAR(200),                           -- 地址
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- 约束
    CONSTRAINT chk_customer_gender CHECK (gender IS NULL OR gender IN ('M', 'F'))
);

COMMENT ON TABLE customer IS '客户信息表';
COMMENT ON COLUMN customer.id IS '主键ID（雪花算法生成）';
COMMENT ON COLUMN customer.name IS '客户姓名';
COMMENT ON COLUMN customer.phone IS '手机号（唯一）';
COMMENT ON COLUMN customer.id_card IS '身份证号（唯一）';
COMMENT ON COLUMN customer.gender IS '性别：M-男, F-女';

-- ----------------------------------------------------------------------------
-- 4. sales_order 表（销售订单表）
-- ----------------------------------------------------------------------------
CREATE TABLE sales_order (
    id BIGINT PRIMARY KEY,                          -- 主键（雪花算法生成）
    order_no VARCHAR(32) NOT NULL UNIQUE,           -- 订单编号（唯一）
    sales_user_id BIGINT NOT NULL,                  -- 销售员ID
    customer_id BIGINT NOT NULL,                    -- 客户ID
    car_id BIGINT NOT NULL,                         -- 车辆ID
    original_price NUMERIC(12, 2) NOT NULL,         -- 原价
    actual_price NUMERIC(12, 2) NOT NULL,           -- 实际成交价
    discount_amount NUMERIC(12, 2) DEFAULT 0,       -- 优惠金额
    order_date DATE NOT NULL,                       -- 订单日期
    status SMALLINT DEFAULT 1,                      -- 订单状态：1-已完成, 2-已取消
    remark TEXT,                                    -- 备注
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- 外键约束
    CONSTRAINT fk_order_sales_user FOREIGN KEY (sales_user_id) 
        REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) 
        REFERENCES customer(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_order_car FOREIGN KEY (car_id) 
        REFERENCES car_info(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    
    -- 检查约束
    CONSTRAINT chk_order_status CHECK (status IN (1, 2)),
    CONSTRAINT chk_order_actual_price CHECK (actual_price > 0),
    CONSTRAINT chk_order_original_price CHECK (original_price > 0),
    CONSTRAINT chk_order_discount CHECK (discount_amount >= 0)
);

COMMENT ON TABLE sales_order IS '销售订单表';
COMMENT ON COLUMN sales_order.id IS '主键ID（雪花算法生成）';
COMMENT ON COLUMN sales_order.order_no IS '订单编号（唯一）';
COMMENT ON COLUMN sales_order.sales_user_id IS '销售员ID（外键）';
COMMENT ON COLUMN sales_order.customer_id IS '客户ID（外键）';
COMMENT ON COLUMN sales_order.car_id IS '车辆ID（外键）';
COMMENT ON COLUMN sales_order.original_price IS '原价';
COMMENT ON COLUMN sales_order.actual_price IS '实际成交价';
COMMENT ON COLUMN sales_order.discount_amount IS '优惠金额';
COMMENT ON COLUMN sales_order.status IS '订单状态：1-已完成, 2-已取消';


-- ============================================================================
-- 第三部分：创建索引
-- ============================================================================

-- ----------------------------------------------------------------------------
-- sys_user 表索引
-- ----------------------------------------------------------------------------
CREATE INDEX idx_user_username ON sys_user(username);
COMMENT ON INDEX idx_user_username IS '用户名索引（用于登录查询）';

-- ----------------------------------------------------------------------------
-- car_info 表索引
-- ----------------------------------------------------------------------------
CREATE INDEX idx_car_vin ON car_info(vin);
CREATE INDEX idx_car_brand ON car_info(brand);
CREATE INDEX idx_car_status ON car_info(status);
CREATE INDEX idx_car_price ON car_info(price);

COMMENT ON INDEX idx_car_vin IS '车架号索引（唯一性已由UNIQUE约束保证）';
COMMENT ON INDEX idx_car_brand IS '品牌索引（用于品牌筛选查询）';
COMMENT ON INDEX idx_car_status IS '状态索引（用于状态筛选查询）';
COMMENT ON INDEX idx_car_price IS '价格索引（用于价格区间查询）';

-- ----------------------------------------------------------------------------
-- customer 表索引
-- ----------------------------------------------------------------------------
CREATE INDEX idx_customer_phone ON customer(phone);
CREATE INDEX idx_customer_name ON customer(name);

COMMENT ON INDEX idx_customer_phone IS '手机号索引（用于精确查询）';
COMMENT ON INDEX idx_customer_name IS '姓名索引（用于模糊查询）';

-- ----------------------------------------------------------------------------
-- sales_order 表索引
-- ----------------------------------------------------------------------------
CREATE INDEX idx_order_sales_user ON sales_order(sales_user_id);
CREATE INDEX idx_order_customer ON sales_order(customer_id);
CREATE INDEX idx_order_car ON sales_order(car_id);
CREATE INDEX idx_order_date ON sales_order(order_date);
CREATE INDEX idx_order_no ON sales_order(order_no);

COMMENT ON INDEX idx_order_sales_user IS '销售员ID索引（用于按销售员筛选）';
COMMENT ON INDEX idx_order_customer IS '客户ID索引（用于查询客户订单）';
COMMENT ON INDEX idx_order_car IS '车辆ID索引（用于查询车辆销售记录）';
COMMENT ON INDEX idx_order_date IS '订单日期索引（用于日期区间查询）';
COMMENT ON INDEX idx_order_no IS '订单编号索引（用于订单号查询）';


-- ============================================================================
-- 第四部分：创建视图
-- ============================================================================

-- ----------------------------------------------------------------------------
-- v_sales_statistics 视图（销售统计视图）
-- 功能：按品牌、型号、销售员统计销售数据
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_sales_statistics AS
SELECT 
    c.brand,                                        -- 品牌
    c.model,                                        -- 型号
    COUNT(o.id) AS sales_count,                     -- 销售数量
    SUM(o.actual_price) AS total_amount,            -- 总销售额
    AVG(o.actual_price) AS avg_price,               -- 平均成交价
    MIN(o.order_date) AS first_sale_date,           -- 首次销售日期
    MAX(o.order_date) AS last_sale_date,            -- 最近销售日期
    u.id AS salesperson_id,                         -- 销售员ID
    u.real_name AS salesperson_name                 -- 销售员姓名
FROM sales_order o
INNER JOIN car_info c ON o.car_id = c.id
INNER JOIN sys_user u ON o.sales_user_id = u.id
WHERE o.status = 1                                  -- 只统计已完成订单
GROUP BY c.brand, c.model, u.id, u.real_name;

COMMENT ON VIEW v_sales_statistics IS '销售统计视图：按品牌、型号、销售员统计销售数据';

-- ----------------------------------------------------------------------------
-- v_monthly_sales_trend 视图（月度销售趋势视图）
-- 功能：按月统计销售趋势
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_monthly_sales_trend AS
SELECT 
    TO_CHAR(o.order_date, 'YYYY-MM') AS month,     -- 月份（格式：YYYY-MM）
    COUNT(o.id) AS sales_count,                     -- 销售数量
    SUM(o.actual_price) AS total_amount,            -- 总销售额
    AVG(o.actual_price) AS avg_price,               -- 平均成交价
    COUNT(DISTINCT o.sales_user_id) AS active_salesperson_count  -- 活跃销售员数量
FROM sales_order o
WHERE o.status = 1                                  -- 只统计已完成订单
GROUP BY TO_CHAR(o.order_date, 'YYYY-MM')
ORDER BY month DESC;

COMMENT ON VIEW v_monthly_sales_trend IS '月度销售趋势视图：按月统计销售数据';


-- ============================================================================
-- 第五部分：创建存储过程
-- ============================================================================

-- ----------------------------------------------------------------------------
-- proc_create_order 存储过程（创建订单）
-- 功能：原子性地创建订单并更新车辆状态
-- 参数：
--   p_sales_user_id: 销售员ID
--   p_customer_id: 客户ID
--   p_car_id: 车辆ID
--   p_actual_price: 实际成交价
-- 返回：
--   p_order_id: 创建的订单ID
--   p_result_code: 结果码（0-成功, 非0-失败）
--   p_result_msg: 结果消息
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION proc_create_order(
    p_sales_user_id BIGINT,
    p_customer_id BIGINT,
    p_car_id BIGINT,
    p_actual_price NUMERIC,
    OUT p_order_id BIGINT,
    OUT p_result_code INTEGER,
    OUT p_result_msg VARCHAR
)
RETURNS RECORD AS $$
DECLARE
    v_car_status SMALLINT;
    v_original_price NUMERIC;
    v_order_no VARCHAR(32);
    v_user_exists BOOLEAN;
    v_customer_exists BOOLEAN;
BEGIN
    -- 初始化返回值
    p_result_code := 0;
    p_result_msg := 'Success';
    p_order_id := NULL;
    
    -- 验证输入参数
    IF p_sales_user_id IS NULL OR p_customer_id IS NULL OR p_car_id IS NULL OR p_actual_price IS NULL THEN
        p_result_code := -1;
        p_result_msg := '参数不能为空';
        RETURN;
    END IF;
    
    IF p_actual_price <= 0 THEN
        p_result_code := -2;
        p_result_msg := '实际成交价必须大于0';
        RETURN;
    END IF;
    
    -- 检查销售员是否存在
    SELECT EXISTS(SELECT 1 FROM sys_user WHERE id = p_sales_user_id) INTO v_user_exists;
    IF NOT v_user_exists THEN
        p_result_code := 1;
        p_result_msg := '销售员不存在';
        RETURN;
    END IF;
    
    -- 检查客户是否存在
    SELECT EXISTS(SELECT 1 FROM customer WHERE id = p_customer_id) INTO v_customer_exists;
    IF NOT v_customer_exists THEN
        p_result_code := 2;
        p_result_msg := '客户不存在';
        RETURN;
    END IF;
    
    -- 检查车辆状态并锁定行（FOR UPDATE 防止并发问题）
    SELECT status, price INTO v_car_status, v_original_price
    FROM car_info
    WHERE id = p_car_id
    FOR UPDATE;
    
    IF NOT FOUND THEN
        p_result_code := 3;
        p_result_msg := '车辆不存在';
        RETURN;
    END IF;
    
    IF v_car_status != 0 THEN
        p_result_code := 4;
        p_result_msg := '车辆状态不可售（当前状态：' || 
            CASE v_car_status 
                WHEN 1 THEN '已锁定' 
                WHEN 2 THEN '已售出' 
                ELSE '未知' 
            END || '）';
        RETURN;
    END IF;
    
    -- 生成订单编号（格式：ORD + 时间戳 + 3位随机数）
    v_order_no := 'ORD' || TO_CHAR(CURRENT_TIMESTAMP, 'YYYYMMDDHH24MISS') || 
                  LPAD(FLOOR(RANDOM() * 1000)::TEXT, 3, '0');
    
    -- 生成订单ID（使用雪花算法，这里使用时间戳模拟）
    -- 注意：实际生产环境应在应用层使用真正的雪花算法
    p_order_id := EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT * 1000 + 
                  FLOOR(RANDOM() * 1000)::BIGINT;
    
    -- 创建订单
    INSERT INTO sales_order (
        id, 
        order_no, 
        sales_user_id, 
        customer_id, 
        car_id,
        original_price, 
        actual_price, 
        discount_amount, 
        order_date, 
        status,
        create_time,
        update_time
    ) VALUES (
        p_order_id,
        v_order_no,
        p_sales_user_id,
        p_customer_id,
        p_car_id,
        v_original_price,
        p_actual_price,
        v_original_price - p_actual_price,
        CURRENT_DATE,
        1,  -- 状态：已完成
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );
    
    -- 更新车辆状态为已售
    UPDATE car_info
    SET status = 2,
        update_time = CURRENT_TIMESTAMP
    WHERE id = p_car_id;
    
    p_result_msg := '订单创建成功，订单号：' || v_order_no;
    
EXCEPTION
    WHEN unique_violation THEN
        p_result_code := -3;
        p_result_msg := '订单编号冲突，请重试';
        p_order_id := NULL;
    WHEN foreign_key_violation THEN
        p_result_code := -4;
        p_result_msg := '外键约束违反：关联数据不存在';
        p_order_id := NULL;
    WHEN check_violation THEN
        p_result_code := -5;
        p_result_msg := '数据验证失败：违反检查约束';
        p_order_id := NULL;
    WHEN OTHERS THEN
        p_result_code := -99;
        p_result_msg := '系统错误：' || SQLERRM;
        p_order_id := NULL;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION proc_create_order(BIGINT, BIGINT, BIGINT, NUMERIC) IS '创建订单存储过程：原子性地创建订单并更新车辆状态';


-- ============================================================================
-- 第六部分：插入测试数据
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. 插入系统用户测试数据
-- 注意：密码使用 BCrypt 加密，这里使用明文 "123456" 的 BCrypt 哈希值
-- 实际应用中应在应用层加密
-- ----------------------------------------------------------------------------
INSERT INTO sys_user (id, username, password, real_name, role, phone, email, status) VALUES
(1001, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'ADMIN', '13800138000', 'admin@example.com', 1),
(1002, 'sales001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', 'SALESPERSON', '13800138001', 'zhangsan@example.com', 1),
(1003, 'sales002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四', 'SALESPERSON', '13800138002', 'lisi@example.com', 1),
(1004, 'sales003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王五', 'SALESPERSON', '13800138003', 'wangwu@example.com', 1);

-- ----------------------------------------------------------------------------
-- 2. 插入车辆信息测试数据
-- ----------------------------------------------------------------------------
INSERT INTO car_info (id, vin, brand, model, color, year, price, status, purchase_date) VALUES
-- 在库车辆
(2001, 'LSVAA4182ES123456', '奔驰', 'C260L', '黑色', 2023, 350000.00, 0, '2024-01-15'),
(2002, 'LSVBB5293FT234567', '宝马', '530Li', '白色', 2023, 480000.00, 0, '2024-01-20'),
(2003, 'LSVCC6304GU345678', '奥迪', 'A6L', '银色', 2023, 420000.00, 0, '2024-02-01'),
(2004, 'LSVDD7415HV456789', '特斯拉', 'Model 3', '红色', 2024, 280000.00, 0, '2024-02-10'),
(2005, 'LSVEE8526IW567890', '比亚迪', '汉EV', '蓝色', 2024, 250000.00, 0, '2024-02-15'),
(2006, 'LSVFF9637JX678901', '丰田', '凯美瑞', '白色', 2023, 220000.00, 0, '2024-03-01'),
(2007, 'LSVGG0748KY789012', '本田', '雅阁', '黑色', 2023, 210000.00, 0, '2024-03-05'),
(2008, 'LSVHH1859LZ890123', '大众', '帕萨特', '灰色', 2023, 230000.00, 0, '2024-03-10'),
(2009, 'LSVII2960MA901234', '日产', '天籁', '白色', 2023, 200000.00, 0, '2024-03-15'),
(2010, 'LSVJJ3071NB012345', '马自达', '阿特兹', '红色', 2023, 190000.00, 0, '2024-03-20'),

-- 已售车辆（用于测试）
(2011, 'LSVKK4182OC123456', '奔驰', 'E300L', '黑色', 2023, 550000.00, 2, '2024-01-10'),
(2012, 'LSVLL5293PD234567', '宝马', 'X5', '白色', 2023, 680000.00, 2, '2024-01-12'),
(2013, 'LSVMM6304QE345678', '奥迪', 'Q7', '银色', 2023, 720000.00, 2, '2024-01-18');

-- ----------------------------------------------------------------------------
-- 3. 插入客户信息测试数据
-- ----------------------------------------------------------------------------
INSERT INTO customer (id, name, phone, id_card, gender, address) VALUES
(3001, '陈先生', '13900139001', '110101199001011234', 'M', '北京市朝阳区建国路88号'),
(3002, '刘女士', '13900139002', '110101199102022345', 'F', '北京市海淀区中关村大街1号'),
(3003, '赵先生', '13900139003', '110101198803033456', 'M', '北京市东城区王府井大街100号'),
(3004, '孙女士', '13900139004', '110101199204044567', 'F', '北京市西城区金融街35号'),
(3005, '周先生', '13900139005', '110101198505055678', 'M', '北京市丰台区南三环西路88号'),
(3006, '吴女士', '13900139006', '110101199306066789', 'F', '北京市石景山区石景山路22号'),
(3007, '郑先生', '13900139007', '110101198707077890', 'M', '北京市通州区新华大街1号'),
(3008, '王女士', '13900139008', '110101199408088901', 'F', '北京市顺义区顺平路99号'),
(3009, '冯先生', '13900139009', '110101198909099012', 'M', '北京市昌平区回龙观西大街88号'),
(3010, '陈女士', '13900139010', '110101199510100123', 'F', '北京市大兴区黄村镇兴华大街100号');

-- ----------------------------------------------------------------------------
-- 4. 插入销售订单测试数据
-- 注意：这里直接插入订单数据，实际应用中应通过存储过程创建
-- ----------------------------------------------------------------------------
INSERT INTO sales_order (id, order_no, sales_user_id, customer_id, car_id, original_price, actual_price, discount_amount, order_date, status) VALUES
(4001, 'ORD20240115143022001', 1002, 3001, 2011, 550000.00, 540000.00, 10000.00, '2024-01-15', 1),
(4002, 'ORD20240118101530002', 1003, 3002, 2012, 680000.00, 670000.00, 10000.00, '2024-01-18', 1),
(4003, 'ORD20240122153045003', 1002, 3003, 2013, 720000.00, 700000.00, 20000.00, '2024-01-22', 1);

-- ----------------------------------------------------------------------------
-- 5. 验证数据插入
-- ----------------------------------------------------------------------------
-- 查询统计信息
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


-- ============================================================================
-- 第七部分：使用说明和测试查询
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 使用说明
-- ----------------------------------------------------------------------------
-- 1. 执行本脚本初始化数据库：
--    psql -U username -d database_name -f init.sql
--
-- 2. 测试存储过程创建订单：
--    SELECT * FROM proc_create_order(1002, 3004, 2001, 345000.00);
--
-- 3. 查询销售统计视图：
--    SELECT * FROM v_sales_statistics;
--
-- 4. 查询月度销售趋势：
--    SELECT * FROM v_monthly_sales_trend;
--
-- 5. 默认用户密码均为：123456（BCrypt加密）
--
-- ----------------------------------------------------------------------------

-- ----------------------------------------------------------------------------
-- 测试查询示例
-- ----------------------------------------------------------------------------

-- 查询所有在库车辆
-- SELECT * FROM car_info WHERE status = 0 ORDER BY price;

-- 查询特定品牌的车辆
-- SELECT * FROM car_info WHERE brand = '奔驰';

-- 查询价格区间的车辆
-- SELECT * FROM car_info WHERE price BETWEEN 200000 AND 300000;

-- 查询客户信息（模糊搜索）
-- SELECT * FROM customer WHERE name LIKE '%陈%';

-- 查询订单详情（包含关联信息）
-- SELECT 
--     o.order_no,
--     o.order_date,
--     u.real_name AS salesperson,
--     c.name AS customer_name,
--     c.phone AS customer_phone,
--     car.brand,
--     car.model,
--     car.vin,
--     o.actual_price
-- FROM sales_order o
-- INNER JOIN sys_user u ON o.sales_user_id = u.id
-- INNER JOIN customer c ON o.customer_id = c.id
-- INNER JOIN car_info car ON o.car_id = car.id
-- WHERE o.status = 1
-- ORDER BY o.order_date DESC;

-- 查询销售统计
-- SELECT * FROM v_sales_statistics ORDER BY total_amount DESC;

-- 查询月度销售趋势
-- SELECT * FROM v_monthly_sales_trend;

-- 测试存储过程（创建新订单）
-- SELECT * FROM proc_create_order(1002, 3005, 2004, 275000.00);

-- 验证订单创建后车辆状态是否更新
-- SELECT id, vin, brand, model, status FROM car_info WHERE id = 2004;

-- ============================================================================
-- 脚本执行完成
-- ============================================================================
COMMENT ON DATABASE car_sales_db IS '汽车销售管理系统数据库 - 初始化完成';

