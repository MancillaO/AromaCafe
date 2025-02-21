-- Crear la base de datos
CREATE DATABASE aroma_y_cafe;

-- Conectar a la base de datos
\c aroma_y_cafe;

-- Tabla de categorías
CREATE TABLE categorias (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla de productos
CREATE TABLE productos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    categoria_id INT NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    descripcion TEXT,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE CASCADE
);

-- Tabla de pedidos
CREATE TABLE pedidos (
    id SERIAL PRIMARY KEY,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10, 2) NOT NULL
);

-- Tabla de detalles del pedido
CREATE TABLE detalles_pedido (
    id SERIAL PRIMARY KEY,
    pedido_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
);

-- Insertar categorías
INSERT INTO categorias (nombre) VALUES
('Bebidas Calientes'),
('Bebidas Frias'),
('Platillos Salados'),
('Platillos Dulces'),
('Platillos Veganos');

-- Insertar productos
INSERT INTO productos (nombre, categoria_id, precio, descripcion) VALUES
('Café Espresso', 1, 65.00, 'Un clásico, fuerte y concentrado.'),
('Café Americano', 1, 70.00, 'Espresso con agua caliente.'),
('Capuccino', 1, 75.00, 'Café espresso con leche espumosa.'),
('Latte', 1, 70.00, 'Café con mucha leche vaporizada.'),
('Mocha', 1, 70.00, 'Café con chocolate y leche vaporizada.'),
('Té helado', 2, 20.00, 'Té helado de limón con miel.'),
('Soda', 2, 20.00, 'Refresco burbujeante y muy refrescante.'),
('Limonada', 2, 25.00, 'Muy refrescante con limones recién recolectados.'),
('Iced Latte', 2, 30.00, 'Café con hielo y leche. Algo clásico.'),
('Club Sándwich', 3, 40.00, 'Con bacon, lechuga, salchicha y aderezo especial.'),
('Ensalada Cesar', 3, 45.00, 'Lechuga, pollo a la parrilla, aderezo y queso panela.'),
('Torta clásica', 3, 30.00, 'Jamón, queso panela y aderezo.'),
('Wrap de pollo', 3, 45.00, 'Con ensalada y salsa secreta.'),
('Churros', 4, 40.00, 'Con chocolate y azúcar glas.'),
('Cheescake', 4, 30.00, 'Con frutas frescas, recién recolectadas.'),
('Tarta', 4, 45.00, 'Con mermelada de manzana y suero de frutas frescas.'),
('Brownies', 4, 45.00, 'De chocolate con chispas.'),
('Batido de frutas', 5, 35.00, 'Con leche de almendras.'),
('Sándwich de Tofu', 5, 45.00, 'Con tofu y verduras a la parrilla.'),
('Palitos de Humus', 5, 20.00, 'Acompañados de humus casero.'),
('Emparedado de seitán', 5, 45.00, 'A base de trigo. Un delicioso sustituto de la carne.');