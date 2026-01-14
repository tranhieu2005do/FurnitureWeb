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