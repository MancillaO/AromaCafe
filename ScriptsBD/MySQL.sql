CREATE DATABASE aroma_y_cafe;
USE aroma_y_cafe;

-- Tabla de categorias
CREATE TABLE categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla de productos (modificada)
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    categoria_id INT NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    descripcion TEXT,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE CASCADE
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
    precio_unitario DECIMAL(10, 2),
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
);

-- Insertar categorias
INSERT INTO categorias (nombre) VALUES
('Bebidas Calientes'),
('Bebidas Frias'),
('Platillos Salados'),
('Platillos Dulces'),
('Platillos Veganos');

-- Insertar productos
INSERT INTO productos (nombre, categoria_id, precio, descripcion) VALUES
('Cafe Espresso', 1, 65.00, 'Un clasico, fuerte y concentrado.'),
('Cafe Americano', 1, 70.00, 'Espresso con agua caliente.'),
('Capuccino', 1, 75.00, 'Cafe espresso con leche espumosa.'),
('Latte', 1, 70.00, 'Cafe con mucha leche vaporizada.'),
('Mocha', 1, 70.00, 'Cafe con chocolate y leche vaporizada.'),
('Te helado', 2, 20.00, 'Te helado de limon con miel.'),
('Soda', 2, 20.00, 'Refresco burbujeante y muy refrescante.'),
('Limonada', 2, 25.00, 'Muy refrescante con limones recien recolectados.'),
('Iced Latte', 2, 30.00, 'Cafe con hielo y leche. Algo clasico.'),
('Club Sandwich', 3, 40.00, 'Con bacon, lechuga, salchicha y aderezo especial.'),
('Ensalada Cesar', 3, 45.00, 'Lechuga, pollo a la parrilla, aderezo y queso panela.'),
('Torta clasica', 3, 30.00, 'Jamon, queso panela y aderezo.'),
('Wrap de pollo', 3, 45.00, 'Con ensalada y salsa secreta.'),
('Churros', 4, 40.00, 'Con chocolate y azucar glas.'),
('Cheescake', 4, 30.00, 'Con frutas frescas, recien recolectadas.'),
('Tarta', 4, 45.00, 'Con mermelada de manzana y suero de frutas frescas.'),
('Brownies', 4, 45.00, 'De chocolate con chispas.'),
('Batido de frutas', 5, 35.00, 'Con leche de almendras.'),
('Sandwich de Tofu', 5, 45.00, 'Con tofu y verduras a la parrilla.'),
('Palitos de Humus', 5, 20.00, 'Acompanados de humus casero.'),
('Emparedado de seitan', 5, 45.00, 'A base de trigo. Un delicioso sustituto de la carne.');

DELIMITER $$

CREATE TRIGGER before_insert_detalles_pedido
BEFORE INSERT ON detalles_pedido
FOR EACH ROW
BEGIN
    -- Buscar el precio del producto en la tabla productos
    DECLARE precio_producto DECIMAL(10, 2);

    SELECT precio INTO precio_producto
    FROM productos
    WHERE id = NEW.producto_id;

    -- Asignar el precio encontrado o NULL si no se encuentra
    IF precio_producto IS NOT NULL THEN
        SET NEW.precio_unitario = precio_producto;
    ELSE
        SET NEW.precio_unitario = NULL;
    END IF;
END$$

DELIMITER ;