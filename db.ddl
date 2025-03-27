
-- Create a table for customers
CREATE TABLE customers (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           email VARCHAR(100) UNIQUE NOT NULL,
                           phone VARCHAR(20)
);

-- Create a table for products
CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          description TEXT,
                          price DECIMAL(10, 2) NOT NULL -- Price with 2 decimal points
);

-- Create a table for sales transactions
CREATE TABLE sales (
                       id SERIAL PRIMARY KEY,
                       customer_id INT REFERENCES customers(id),
                       product_id INT REFERENCES products(id),
                       quantity INT NOT NULL,
                       sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       total DECIMAL(10, 2) -- The total price (quantity * price)
);

-- Step 4: Add some sample data

-- Insert some customers
INSERT INTO customers (name, email, phone) VALUES
                                               ('John Doe', 'johndoe@example.com', '123-456-7890'),
                                               ('Jane Smith', 'janesmith@example.com', '987-654-3210'),
                                               ('Alice Johnson', 'alicej@example.com', '555-123-4567');

-- Insert some products
INSERT INTO products (name, description, price) VALUES
                                                    ('Laptop', 'High-performance laptop', 1200.00),
                                                    ('Smartphone', 'Latest model smartphone', 800.00),
                                                    ('Headphones', 'Noise-canceling headphones', 150.00);

-- Step 5: Insert sales transactions (calculating total price)
INSERT INTO sales (customer_id, product_id, quantity, total) VALUES
                                                                 (1, 1, 1, 1200.00), -- John Doe bought 1 Laptop
                                                                 (2, 2, 2, 1600.00), -- Jane Smith bought 2 Smartphones
                                                                 (3, 3, 3, 450.00);  -- Alice Johnson bought 3 Headphones