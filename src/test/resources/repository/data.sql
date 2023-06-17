INSERT INTO categories (name)
VALUES ('coffee'),
       ('tea');

INSERT INTO products (name, price, category_id)
VALUES ('herbal tea', 150, 2),
       ('green tea', 100, 2),
       ('americano', 50, 1),
       ('cappuccino', 150, 1);

INSERT INTO regions (name)
VALUES ('Sochi'),
       ('Moscow');

INSERT INTO storages (name, region_id)
VALUES ('Sochi-str', 1),
       ('Moscow-str-1', 2),
       ('Moscow-str-2', 2);

INSERT INTO items_storage (product_id, storage_id, count)
VALUES (1, 1, 100),
       (2, 1, 100),
       (3, 1, 100),
       (4, 1, 100),
       (1, 2, 25),
       (3, 2, 50),
       (2, 3, 25),
       (4, 3, 25);

INSERT INTO roles (name)
VALUES ('ADMIN'),
       ('MANAGER');

INSERT INTO users (username)
VALUES ('admin');

INSERT INTO users_roles (user_id, role_id)
VALUES (1,1),
       (1, 2);


INSERT INTO operation_histories (product_id, storage_id, operation, count, date)
VALUES (1, 1, 'LOADING', 100, '2023-06-10 10:00:00'),
       (2, 1, 'LOADING', 100, '2023-06-10 10:00:00'),
       (3, 1, 'LOADING', 100, '2023-06-10 10:00:00'),
       (4, 1, 'LOADING', 100, '2023-06-10 10:00:00'),
       (1, 2, 'LOADING', 25, '2023-06-12 10:00:00'),
       (3, 2, 'LOADING', 50, '2023-06-12 10:00:00'),
       (2, 3, 'LOADING', 25, '2023-06-13 10:00:00'),
       (4, 3, 'LOADING', 25, '2023-06-13 10:00:00');