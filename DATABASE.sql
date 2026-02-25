CREATE DATABASE furniture;

CREATE TABLE roles(
   id int primary key auto_increment,
   name varchar(20) not null
);

CREATE TABLE users(
   id bigint primary key auto_increment,
   phone varchar(11) not null,
   full_name varchar(50) not null,
   address varchar(100) not null,
   date_of_birth date,
   is_active tinyint(1),
   password varchar(100),
   created_at datetime,
   updated_at datetime,
   role_id int not null,
   foreign key (role_id) references roles(id)
);

CREATE TABLE categories(
   id bigint primary key auto_increment,
   name varchar(50) not null,
   description longtext not null,
   parent_category_id bigint,
   foreign key (parent_category_id) references categories(id),
   created_at datetime,
   updated_at datetime,
   is_active tinyint(1)
);

CREATE TABLE products(
   id bigint primary key auto_increment,
   name varchar(50) not null,
   description longtext not null,
   category_id bigint not null,
   created_at datetime,
   updated_at datetime,
   instock int not null,  -- bằng tổng instock của các loại
   regard_star float,
   purchase_count int default 0, -- cập nhật lượt bán chung , không riền từng loại
   is_active tinyint(1),
   min_price decimal(12,2),
   max_price decimal(12,2),
   foreign key (category_id) references categories(id)
);

CREATE TABLE products_variants(
   id bigint primary key auto_increment,
   product_id bigint not null,
   instock bigint not null,
   material enum ('Wood', 'Plastic', 'Glass'),
   price float not null, -- giá phải phụ thuộc vòa kích thước,
   color varchar(50) not null,
   is_active tinyint(1),
   foreign key (product_id) references products(id)
);

CREATE TABLE product_images(
   id bigint primary key auto_increment,
   variant_id bigint not null,
   image_url varchar(255),
   foreign key (variant_id) references products_variants(id)
);

CREATE TABLE comments(
   id bigint primary key auto_increment,
   product_id bigint not null,
   user_id bigint not null,
   parent_id bigint,
   content longtext,
   created_at datetime,
   foreign key (product_id) references products(id),
   foreign key (user_id) references users(id),
   foreign key (parent_id) references comments(id)
);

CREATE TABLE ratings(
   id bigint primary key auto_increment,
   product_id bigint not null,
   user_id bigint not null,
   star int not null,
   created_at datetime,
   comments longtext,
   foreign key (product_id) references products(id),
   foreign key (user_id) references users(id)
);

CREATE TABLE carts(
   id bigint primary key auto_increment,
   user_id bigint not null,
   created_at datetime,
   updated_at datetime,
   foreign key (user_id) references users(id)
);

CREATE TABLE cart_items(
   id bigint primary key auto_increment,
   cart_id bigint not null,
   variant_id bigint not null,
   quantity int default 1,
   created_at datetime,
   updated_at datetime,
   foreign key (cart_id) references carts(id),
   foreign key (variant_id) references products_variants(id)
);

CREATE TABLE orders(
   id bigint primary key auto_increment,
   user_id bigint not null,
   staff_id bigint not null,
   payment_status enum('UNPAID','DEPOSIT','PARTIAL','PAID','REFUND'), -- chưa thanh toán, đặt cọc, thanh toán 1 phần, đã trả đủ, hoàn tiền
   install_status enum('NOT_INSTALLED', 'INSTALLED'),
   order_status enum('PENDING', 'DELIVERED', 'SHIPPING', 'CANCELLED', 'PROCESSING'),
   promotion_code varchar(100),
   total float not null,
   order_date datetime default current_timestamp,
   address varchar(200) not null,
   phone_number varchar(11) not null,
   name varchar(50) not null,
   payment_method varchar(100) not null,
   note longtext,
   tracking_number varchar(50) not null,
   foreign key (user_id) references users(id),
   foreign key (staff_id) references users(id)
);


CREATE TABLE order_items(
   id bigint primary key auto_increment,
   order_id bigint not null,
   variant_id bigint not null, -- mã sản phẩm đã chọn kích thước
   price float not null,
   quantity int not null,
   foreign key (variant_id) references products_variants(id),
   foreign key (order_id) references orders(id)
);

CREATE TABLE inventory_logs(
   id bigint primary key auto_increment,
   staff_id bigint not null,
   quantity_change bigint not null,
   type enum('IN', 'OUT'),
   content varchar(200),
   product_id bigint not null,
   created_at datetime,
   foreign key (staff_id) references users(id),
   foreign key (product_id) references products_variants(id)
);

CREATE TABLE notifications(
   id bigint primary key auto_increment,
   user_id bigint not null, -- id người nhận
   topic varchar(50) not null,
   message varchar(200) not null,
   is_read boolean default false,
   created_at datetime default current_timestamp,
   foreign key (user_id) references users(id)
);

CREATE TABLE promotions(
   id bigint primary key auto_increment,
   content varchar(255) not null,
   expired datetime not null,
   is_active boolean,
   sale int not null,
   product_id bigint not null,
   created_at datetime default current_timestamp,
   foreign key (product_id) references products(id)
);

CREATE TABLE conversations (
   id bigint primary key auto_increment,
   customer_id bigint not null,
   staff_id bigint,
   status enum('OPEN','CLOSED'),
   created_at datetime,
   foreign key (customer_id) references users(id),
   foreign key (staff_id) references users(id)
);

CREATE TABLE message(
   id bigint primary key auto_increment,
   conversation_id bigint not null,
   sender_id bigint not null,
   content longtext,
   created_at datetime,
   foreign key (conversation_id) references conversations(id),
   foreign key (sender_id) references users(id)
);

CREATE TABLE promotion_usage(
   id bigint primary key auto_increment,
   promotion_id bigint not null,
   user_id bigint not null,
   used boolean not null,
   foreign key (promotion_id) references promotions(id),
   foreign key (user_id) references users(id)
);
alter table promotion_usage
modify column used datetime not null;


create table promotions(
   id bigint primary key auto_increment,
   code varchar(100) not null,
   discount_type enum('BIRTHDAY', 'SHIPPING', 'PRODUCT', 'ORDER'),
   discount_value int not null, -- PERCENT
   start_date datetime not null default current_timestamp,
   end_date datetime not null default current_timestamp,
   is_personal boolean default false
);

create table promotion_products(
   id bigint primary key auto_increment,
   promotion_id bigint not null,
   product_id bigint not null,
   foreign key (promotion_id) references promotions(id),
   foreign key (product_id) references products(id)
);

create table promotion_users(
   id bigint primary key auto_increment,
   promotion_id bigint not null,
   user_id bigint not null,
   used_at datetime,
   foreign key (promotion_id) references promotions(id),
   foreign key (user_id) references users(id)
);

CREATE TABLE numbers (
  n INT PRIMARY KEY
);

INSERT INTO numbers (n)
SELECT a.N + b.N * 10 + c.N * 100 + d.N * 1000 + e.N * 10000 + 1
FROM 
(SELECT 0 N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 
 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
(SELECT 0 N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 
 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
(SELECT 0 N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 
 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c,
(SELECT 0 N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 
 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) d,
(SELECT 0 N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 
 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) e
LIMIT 1000000;

INSERT INTO users (
  phone,
  full_name,
  address,
  date_of_birth,
  is_active,
  password,
  created_at,
  updated_at,
  role_id
)
SELECT
  CONCAT('09', LPAD(n, 9, '0')),
  CONCAT('User ', n),
  CONCAT('Address ', n),
  DATE_ADD('1990-01-01', INTERVAL (n % 10000) DAY),
  n % 2,
  'hashed_password',
  DATE_ADD('2020-01-01', INTERVAL n SECOND),
  NOW(),
  (n % 5) + 1
FROM numbers;

SELECT * FROM users;

drop INDEX idx_role_created
ON users;

CREATE INDEX idx_role_created_cover
ON users(role_id, created_at DESC, id, phone);


EXPLAIN
SELECT id, phone, created_at
FROM users
WHERE role_id = 3
  AND created_at >= '2026-01-01'
ORDER BY created_at DESC
LIMIT 20;


INSERT INTO products
(name, description, category_id, created_at, updated_at, instock, regard_star, purchase_count, is_active, max_price, min_price)
VALUES
('Bàn Làm Việc Gỗ Sồi', 'Thiết kế hiện đại', 11, NOW(), NOW(), 20, 4.5, 120, 1, 7500000, 6500000),
('Tủ Hồ Sơ Gỗ Tần Bì', 'Chắc chắn và bền bỉ', 11, NOW(), NOW(), 15, 4.3, 80, 1, 6800000, 5800000),
('Kệ Trang Trí Gỗ Óc Chó', 'Sang trọng cao cấp', 9, NOW(), NOW(), 12, 4.7, 95, 1, 12500000, 11000000),
('Bàn Ăn Marble Cao Cấp', 'Mặt đá tự nhiên', 10, NOW(), NOW(), 10, 4.8, 140, 1, 22000000, 19500000),
('Bàn Trà Granite', 'Phong cách hiện đại', 9, NOW(), NOW(), 18, 4.4, 90, 1, 9500000, 8200000),
('Kệ Tivi Inox', 'Thiết kế tối giản', 9, NOW(), NOW(), 25, 4.2, 60, 1, 7200000, 6200000),
('Bàn Console Nhôm', 'Nhẹ và bền', 7, NOW(), NOW(), 30, 4.1, 50, 1, 5800000, 4800000),
('Tủ Kính Trang Trí', 'Thiết kế kính cường lực', 7, NOW(), NOW(), 14, 4.6, 85, 1, 10500000, 9200000),
('Bàn Làm Việc Gỗ Óc Chó', 'Phong cách sang trọng', 11, NOW(), NOW(), 16, 4.9, 200, 1, 15500000, 13500000),
('Bàn Ăn Gỗ Sồi 6 Ghế', 'Phù hợp gia đình', 10, NOW(), NOW(), 9, 4.5, 100, 1, 18500000, 16500000);


INSERT INTO product_variants
(color, height, instock, is_active, length, material, price, width, product_id)
VALUES

-- 1 Gỗ Sồi
('trắng', 75, 10, b'1', 120, 'gỗ sồi', 6500000, 60, 1),
('nâu gỗ đậm', 75, 10, b'1', 140, 'gỗ sồi', 7500000, 70, 1),

-- 2 Gỗ Tần Bì
('be', 180, 7, b'1', 80, 'gỗ tần bì', 5800000, 40, 2),
('xám nhạt', 200, 8, b'1', 90, 'gỗ tần bì', 6800000, 45, 2),

-- 3 Gỗ Óc Chó
('đen', 200, 5, b'1', 120, 'gỗ óc chó', 11000000, 35, 3),
('nâu gỗ nhạt', 220, 7, b'1', 140, 'gỗ óc chó', 12500000, 40, 3),

-- 4 Marble
('trắng', 75, 4, b'1', 160, 'marble', 19500000, 90, 4),
('xám đậm', 75, 6, b'1', 180, 'marble', 22000000, 100, 4),

-- 5 Granite
('đen', 45, 9, b'1', 100, 'granite', 8200000, 60, 5),
('xám nhạt', 50, 9, b'1', 120, 'granite', 9500000, 70, 5),

-- 6 Inox
('xám đậm', 60, 12, b'1', 140, 'inox', 6200000, 40, 6),
('đen', 60, 13, b'1', 160, 'inox', 7200000, 50, 6),

-- 7 Nhôm
('be', 80, 15, b'1', 120, 'nhôm', 4800000, 40, 7),
('trắng', 85, 15, b'1', 140, 'nhôm', 5800000, 45, 7),

-- 8 Kính
('trắng', 190, 7, b'1', 80, 'kính', 9200000, 35, 8),
('đen', 200, 7, b'1', 90, 'kính', 10500000, 40, 8),

-- 9 Gỗ Óc Chó
('nâu gỗ đậm', 75, 8, b'1', 140, 'gỗ óc chó', 13500000, 60, 9),
('đen', 75, 8, b'1', 160, 'gỗ óc chó', 15500000, 70, 9),

-- 10 Gỗ Sồi
('trắng', 75, 4, b'1', 160, 'gỗ sồi', 16500000, 90, 10),
('nâu gỗ nhạt', 75, 5, b'1', 180, 'gỗ sồi', 18500000, 100, 10);

INSERT INTO ratings (id, product_id, user_id, star, created_at, comments) VALUES
-- Product 1 (avg ~4.5)
(1, 1, 1, 5, '2026-02-14 09:00:00', 'Sản phẩm rất chắc chắn'),
(2, 1, 7, 4, '2026-02-14 10:15:00', 'Thiết kế đẹp, hài lòng'),
(3, 1, 8, 5, '2026-02-15 08:30:00', 'Chất lượng tốt'),

-- Product 2 (avg ~4.3)
(4, 2, 9, 4, '2026-02-14 11:00:00', 'Khá ổn trong tầm giá'),
(5, 2, 10, 4, '2026-02-15 09:45:00', 'Bền và chắc'),
(6, 2, 11, 5, '2026-02-15 14:20:00', 'Rất đáng mua'),

-- Product 3 (avg ~4.7)
(7, 3, 1, 5, '2026-02-14 12:00:00', 'Gỗ đẹp, cao cấp'),
(8, 3, 131082, 5, '2026-02-15 13:10:00', 'Rất sang trọng'),
(9, 3, 7, 4, '2026-02-16 09:00:00', 'Giá hơi cao nhưng xứng đáng'),

-- Product 4 (avg ~4.8)
(10, 4, 8, 5, '2026-02-14 16:00:00', 'Mặt đá rất đẹp'),
(11, 4, 9, 5, '2026-02-15 17:30:00', 'Gia đình rất thích'),
(12, 4, 131083, 4, '2026-02-16 08:45:00', 'Chất lượng tốt'),

-- Product 5 (avg ~4.4)
(13, 5, 10, 4, '2026-02-14 10:40:00', 'Thiết kế hiện đại'),
(14, 5, 11, 5, '2026-02-15 11:15:00', 'Rất hài lòng'),
(15, 5, 1, 4, '2026-02-16 12:00:00', 'Ổn'),

-- Product 6 (avg ~4.2)
(16, 6, 7, 4, '2026-02-14 09:25:00', 'Tối giản đẹp'),
(17, 6, 8, 4, '2026-02-15 10:00:00', 'Phù hợp phòng khách'),
(18, 6, 9, 5, '2026-02-16 11:30:00', 'Chất lượng tốt'),

-- Product 7 (avg ~4.1)
(19, 7, 10, 4, '2026-02-14 15:20:00', 'Nhẹ và bền'),
(20, 7, 11, 4, '2026-02-15 16:40:00', 'Ổn trong tầm giá'),
(21, 7, 131082, 5, '2026-02-16 09:50:00', 'Rất tiện lợi'),

-- Product 8 (avg ~4.6)
(22, 8, 131083, 5, '2026-02-14 14:30:00', 'Kính đẹp và chắc'),
(23, 8, 1, 4, '2026-02-15 15:00:00', 'Hài lòng'),
(24, 8, 7, 5, '2026-02-16 16:00:00', 'Thiết kế sang trọng'),

-- Product 9 (avg ~4.9)
(25, 9, 8, 5, '2026-02-14 17:00:00', 'Rất xuất sắc'),
(26, 9, 9, 5, '2026-02-15 18:00:00', 'Chất lượng cao'),
(27, 9, 10, 5, '2026-02-16 19:00:00', 'Hoàn hảo'),

-- Product 10 (avg ~4.5)
(28, 10, 11, 5, '2026-02-14 09:10:00', 'Phù hợp gia đình'),
(29, 10, 131082, 4, '2026-02-15 10:20:00', 'Giá hợp lý'),
(30, 10, 131083, 4, '2026-02-16 11:45:00', 'Khá hài lòng');


CREATE TABLE comment_media( -- bảng chứa các ảnh/vide được gửi kèm comment
	id bigint primary key auto_increment,
    comment_id bigint not null,
    url longtext, -- đường dẫn tới nơi chứa ảnh/video
    media_type enum('image', 'video'),
    thumbnail_url longtext, -- ảnh đại diện
    duration_seconds int,
    position int, -- thứ tứ trong comment
    foreign key (comment_id) references comments(id)
);

CREATE TABLE message_media(
    id bigint primary key auto_increment,
    message_id bigint not null,
    url longtext not null,
    thumbnail longtext,
    type enum('IMAGE', 'VIDEO', 'FILE'),
    foreign key (message_id) references messages(id)
);

set SQL_SAFE_UPDATES = 0;
UPDATE products p
JOIN (
    SELECT r.product_id, AVG(r.star) AS avg_star
    FROM ratings r
    GROUP BY r.product_id
) t ON p.id = t.product_id
SET p.regard_star = ROUND(t.avg_star, 2);
select * from categories;
select * from roles;
select * from products;
select * from product_variants;
select * from ratings;
select * from users;
select * from carts;
select * from cart_items;
select * from comments;
select * from comment_media;
select * from product_images;
select * from promotions;

select * from promotion_users;
select * from promotion_products;

select * from promotion_usage;
select * from products p 
where p.min_price ;

select * 
from promotions p 
where current_timestamp() between p.start_date and p.end_date
   and p.discount_type <> "BIRTHDAY"
   and p.is_personal = false
   and not exists (
		select 1 from promotion_usage pu
        where pu.promotion_id = p.id
           and pu.user_id = 1
   );
   






