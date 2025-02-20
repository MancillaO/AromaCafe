-- Crear la base de datos
CREATE DATABASE aroma_y_cafe;
USE aroma_y_cafe;

-- Tabla de productos
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    categoria ENUM('Bebidas Calientes', 'Bebidas Frias', 'Platillos Salados', 'Platillos Dulces', 'Platillos Veganos') NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    descripcion TEXT
);

-- Tabla de pedidos
CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10, 2) NOT NULL
);

-- Tabla de detalles del pedido
CREATE TABLE detalles_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
);

INSERT INTO productos (nombre, categoria, precio, descripcion) VALUES
('Café Espresso', 'Bebidas Calientes', 65.00, 'Un clásico, fuerte y concentrado.'),
('Café Americano', 'Bebidas Calientes', 70.00, 'Espresso con agua caliente.'),
('Capuccino', 'Bebidas Calientes', 75.00, 'Café espresso con leche espumosa.'),
('Latte', 'Bebidas Calientes', 70.00, 'Café con mucha leche vaporizada.'),
('Mocha', 'Bebidas Calientes', 70.00, 'Café con chocolate y leche vaporizada.'),
('Té helado', 'Bebidas Frias', 20.00, 'Té helado de limón con miel.'),
('Soda', 'Bebidas Frias', 20.00, 'Refresco burbujeante y muy refrescante.'),
('Limonada', 'Bebidas Frias', 25.00, 'Muy refrescante con limones recién recolectados.'),
('Iced Latte', 'Bebidas Frias', 30.00, 'Café con hielo y leche. Algo clásico.'),
('Club Sándwich', 'Platillos Salados', 40.00, 'Con bacon, lechuga, salchicha y aderezo especial.'),
('Ensalada Cesar', 'Platillos Salados', 45.00, 'Lechuga, pollo a la parrilla, aderezo y queso panela.'),
('Torta clásica', 'Platillos Salados', 30.00, 'Jamón, queso panela y aderezo.'),
('Wrap de pollo', 'Platillos Salados', 45.00, 'Con ensalada y salsa secreta.'),
('Churros', 'Platillos Dulces', 40.00, 'Con chocolate y azúcar glas.'),
('Cheescake', 'Platillos Dulces', 30.00, 'Con frutas frescas, recién recolectadas.'),
('Tarta', 'Platillos Dulces', 45.00, 'Con mermelada de manzana y suero de frutas frescas.'),
('Brownies', 'Platillos Dulces', 45.00, 'De chocolate con chispas.'),
('Batido de frutas', 'Platillos Veganos', 35.00, 'Con leche de almendras.'),
('Sándwich de Tofu', 'Platillos Veganos', 45.00, 'Con tofu y verduras a la parrilla.'),
('Palitos de Humus', 'Platillos Veganos', 20.00, 'Acompañados de humus casero.'),
('Emparedado de seitán', 'Platillos Veganos', 45.00, 'A base de trigo. Un delicioso sustituto de la carne.');