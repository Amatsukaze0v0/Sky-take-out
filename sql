Create DATABASE if not exists 'sky_take_out';
use 'sky_take_out';

drop table if exists 'emplotyee';
create table 'emplotyee'(
    id bigint not null auto_increment comment '自增',
    name varchar(32) not null,
    username varchar(32) not null unique comment '唯一',
    password varchar(64) not null,
    phone varchar(11),
    sex varchar(2),
    id_number varchar(18),
    status int comment '1正常0锁定',
    create_time datetime,
    update_time datetime,
    create_user bigint,
    update_user bigint,
    primary key (id)
);

drop table if exists 'category';
create table 'category'(
    id bigint not null auto_increment comment '自增',
    name varchar(32) unique comment '唯一',
    type int comment '1菜品分类 2套餐分类',
    sort int comment '排序',
    status int comment '1启用0禁用',
    create_time datetime,
    update_time datetime,
    create_user bigint,
    update_user bigint,
    primary key (id)
);

drop table if exists 'dish';
create table 'dish'(
    id bigint not null auto_increment comment '自增',
    name varchar(32) not null unique comment '唯一',
    category_id bigint not null references category(id) comment '分类id',
    price decimal(10, 2),
    image varchar(255) comment '图片路径',
    description varchar(255) comment '菜品描述',
    status int comment '1启用0禁用',
    create_time datetime,
    update_time datetime,
    create_user bigint,
    update_user bigint,
    primary key (id)
);

drop table if exists 'dish_flavor';
create table 'dish_flavor'(
    id bigint not null auto_increment comment '自增',
    dish_id bigint not null  references dish(id),
    name varchar(32) comment '口味名称',
    value varchar(255) comment '口味值',
    primary key (id)
);

drop table if exists 'setmeal';
create table 'setmeal'(
    id bigint not null auto_increment comment '自增',
    name varchar(32) unique comment '套餐名称',
    category_id bigint not null references category(id) comment '分类id',
    price decimal(10, 2),
    image varchar(255) comment '图片路径',
    description varchar(255) comment '套餐描述',
    status int comment '1起售0停售',
    create_time datetime,
    update_time datetime,
    create_user bigint,
    update_user bigint,
    primary key (id)
);

drop table if exists 'setmeal_dish';
create table 'setmeal_dish'(
    id bigint not null auto_increment comment '自增',
    setmeal_id bigint not null references setmeal(id) comment '套餐id',
    dish_id bigint not null references dish(id) comment '菜品id',
    name varchar(32) comment '菜品名称',
    price decimal(10, 2) comment '菜品单价',
    copies int comment '菜品份数',
    primary key (id)
);

drop table if exists 'user';
create table 'user'(
    id bigint not null auto_increment comment '自增',
    openid varchar(45) unique comment '微信用户唯一标识',
    name varchar(32) not null,
    phone varchar(11) comment '手机号',
    sex varchar(2),
    id_number varchar(18) unique comment '身份证号',
    avatar varchar(500) comment '微信用户头像路径',
    create_time datetime,
    primary key (id)
);

drop table if exists 'address_book';
create table 'address_book'(
    id bigint not null auto_increment comment '自增',
    user_id bigint not null references user(id),
    consignee varchar(50) not null comment '收货人',
    sex varchar(2) not null,
    phone varchar(11) not null,
    province_code varchar(12) comment '省份编码',
    province_name varchar(32) comment '省份',
    city_code varchar(12) comment '城市编码',
    city_name varchar(32) comment '城市名',
    district_code varchar(12) comment '区县编码',
    district_name varchar(32) comment '区县名称',
    detail varchar(200) comment '地址信息，具体到门牌号',
    label varchar(100) comment '公司、家、学校',
    is_default tinyint(1) comment '1是0否',
    primary key (id)
);

drop table if exists 'shopping_cart';
create table 'shopping_cart'
(
    id          bigint not null auto_increment comment '自增',
    name        varchar(32) comment '商品名称',
    image       varchar(255) comment '图片路径',
    user_id     bigint not null references user (id),
    dish_id     bigint not null references dish (id),
    setmeal_id  bigint not null references setmeal (id) comment '套餐id',
    dish_flavor varchar(50) comment '菜品口味',
    number      int,
    amount      decimal(10, 2) comment '商品单价',
    create_time datetime,
    primary key (id)
);

drop table if exists 'orders';
create table 'orders'(
    id bigint not null auto_increment comment '自增',
    number varchar(50) comment '订单号',
    status int comment '订单状态：1待付款、2待接单、3已接单、4派送中、5已完成、6已取消',
    user_id bigint not null references user (id),
    address_book_id bigint not null references address_book(id),
    order_time datetime comment '下单时间',
    checkout_time datetime comment '付款时间',
    pay_method int comment '1微信2支付宝',
    pay_status tinyint comment '0未支付1已支付2退款',
    amount decimal(10, 2) comment '订单金额',
    remark varchar(100) comment '备注信息',
    phone varchar(11) not null,
    address varchar(255) comment '详细地址',
    user_name varchar(32) comment '用户姓名',
    consignee varchar(50) not null comment '收货人',
    cancel_reason varchar(255) comment '取消原因',
    rejection_reason varchar(255) comment '拒单原因',
    cancel_time datetime,
    estimated_delivery_time datetime,
    delivery_status tinyint comment '1立即送出2选择时间',
    delivery_time datetime,
    pack_amount int comment '打包费',
    tableware_number int,
    tableware_status tinyint comment '1按餐量2选择数量',
    primary key (id)
);

drop table if exists 'order_detail';
create table 'order_detail'(
    id bigint not null auto_increment comment '自增',
    name varchar(32),
    image varchar(255) comment '图片路径',
    order_id bigint not null references orders(id),
    dish_id     bigint not null references dish (id),
    setmeal_id  bigint not null references setmeal (id) comment '套餐id',
    dish_flavor varchar(50) comment '菜品口味',
    number      int,
    amount      decimal(10, 2) comment '商品单价',
    primary key (id)
);