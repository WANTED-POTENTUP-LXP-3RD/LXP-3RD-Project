create table if not exists order_items
(
    id         bigint auto_increment primary key, -- Java: orderItemId
    order_id   varchar(255)   not null,           -- FK to orders.id
    item_type  varchar(50)    not null,           -- Enum (COURSE 등)
    item_id    bigint         not null,           -- CourseId 등
    price      decimal(10, 0) not null,           -- precision 10, scale 0
    created_at datetime(6)    not null,
    updated_at datetime(6)    not null,

    constraint fk_order_items_order_id
    foreign key (order_id) references orders (id)
    on delete cascade
);