CREATE database fooddrive;

USE fooddrive;

CREATE TABLE IF NOT EXISTS restaurants
(
	id INT PRIMARY KEY AUTO_INCREMENT,
    restaurant_name VARCHAR(127),
    logo_url TEXT
)
CHARACTER SET utf8;

CREATE TABLE IF NOT EXISTS dishes
(
	id INT PRIMARY KEY AUTO_INCREMENT,
    dish_name VARCHAR(127),
    restaurant_id INT NOT NULL,
    ingredients TEXT NOT NULL,
    vegan BOOLEAN NOT NULL,
    dish_description TEXT NOT NULL,
    availability BOOLEAN NOT NULL,
    price INT NOT NULL,
    image_url TEXT,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
)
CHARACTER SET utf8;

CREATE TABLE IF NOT EXISTS users
(
	id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(127) NOT NULL,
    phone LONG NOT NULL,
    email VARCHAR(320) NOT NULL,
    hashed_password TEXT NOT NULL,
    role_id INT NOT NULL DEFAULT 1,
    restaurant_id INT,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
)
CHARACTER SET utf8;

CREATE TABLE IF NOT EXISTS reviews
(
	id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    restaurant_id INT NOT NULL,
    review_text TEXT NOT NULL,
    restaurant_rating INT NOT NULL,
    add_time DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
)
CHARACTER SET utf8;

CREATE TABLE IF NOT EXISTS orders
(
	id INT PRIMARY KEY AUTO_INCREMENT,
    client_id INT NOT NULL,
    restaurant_id INT NOT NULL,
    order_status VARCHAR(127) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    order_check INT NOT NULL,
    CHECK (end_time>start_time),
    FOREIGN KEY (client_id) REFERENCES users (id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
)
CHARACTER SET utf8;

CREATE TABLE IF NOT EXISTS order_dishes
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	order_id INT NOT NULL,
	dish_id INT NOT NULL,
	FOREIGN KEY (order_id) REFERENCES orders (id),
	FOREIGN KEY (dish_id) REFERENCES dishes (id)
)
CHARACTER SET utf8;
