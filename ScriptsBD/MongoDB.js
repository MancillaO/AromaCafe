// Crear base de datos
use aroma_y_cafe

// Crear colecciones
db.createCollection("categorias")
db.createCollection("productos")
db.createCollection("pedidos")
db.createCollection("detalles_pedido")

// Insertar categorías
db.categorias.insertMany([
    { id: 1, nombre: "Bebidas Calientes" },
    { id: 2, nombre: "Bebidas Frias" },
    { id: 3, nombre: "Platillos Salados" },
    { id: 4, nombre: "Platillos Dulces" },
    { id: 5, nombre: "Platillos Veganos" }
])

// Insertar productos
db.productos.insertMany([
    { id: 1, nombre: "Cafe Espresso", categoria_id: 1, precio: 65.00, descripcion: "Un clasico, fuerte y concentrado." },
    { id: 2, nombre: "Cafe Americano", categoria_id: 1, precio: 70.00, descripcion: "Espresso con agua caliente." },
    { id: 3, nombre: "Capuccino", categoria_id: 1, precio: 75.00, descripcion: "Cafe espresso con leche espumosa." },
    { id: 4, nombre: "Latte", categoria_id: 1, precio: 70.00, descripcion: "Cafe con mucha leche vaporizada." },
    { id: 5, nombre: "Mocha", categoria_id: 1, precio: 70.00, descripcion: "Cafe con chocolate y leche vaporizada." },
    { id: 6, nombre: "Te helado", categoria_id: 2, precio: 20.00, descripcion: "Te helado de limon con miel." },
    { id: 7, nombre: "Soda", categoria_id: 2, precio: 20.00, descripcion: "Refresco burbujeante y muy refrescante." },
    { id: 8, nombre: "Limonada", categoria_id: 2, precio: 25.00, descripcion: "Muy refrescante con limones recien recolectados." },
    { id: 9, nombre: "Iced Latte", categoria_id: 2, precio: 30.00, descripcion: "Cafe con hielo y leche. Algo clasico." },
    { id: 10, nombre: "Club Sandwich", categoria_id: 3, precio: 40.00, descripcion: "Con bacon, lechuga, salchicha y aderezo especial." },
    { id: 11, nombre: "Ensalada Cesar", categoria_id: 3, precio: 45.00, descripcion: "Lechuga, pollo a la parrilla, aderezo y queso panela." },
    { id: 12, nombre: "Torta clasica", categoria_id: 3, precio: 30.00, descripcion: "Jamon, queso panela y aderezo." },
    { id: 13, nombre: "Wrap de pollo", categoria_id: 3, precio: 45.00, descripcion: "Con ensalada y salsa secreta." },
    { id: 14, nombre: "Churros", categoria_id: 4, precio: 40.00, descripcion: "Con chocolate y azucar glas." },
    { id: 15, nombre: "Cheescake", categoria_id: 4, precio: 30.00, descripcion: "Con frutas frescas, recien recolectadas." },
    { id: 16, nombre: "Tarta", categoria_id: 4, precio: 45.00, descripcion: "Con mermelada de manzana y suero de frutas frescas." },
    { id: 17, nombre: "Brownies", categoria_id: 4, precio: 45.00, descripcion: "De chocolate con chispas." },
    { id: 18, nombre: "Batido de frutas", categoria_id: 5, precio: 35.00, descripcion: "Con leche de almendras." },
    { id: 19, nombre: "Sandwich de Tofu", categoria_id: 5, precio: 45.00, descripcion: "Con tofu y verduras a la parrilla." },
    { id: 20, nombre: "Palitos de Humus", categoria_id: 5, precio: 20.00, descripcion: "Acompañados de humus casero." },
    { id: 21, nombre: "Emparedado de seitan", categoria_id: 5, precio: 45.00, descripcion: "A base de trigo. Un delicioso sustituto de la carne." }
])

// Crear índices para relaciones (opcional pero recomendado)
db.productos.createIndex({ categoria_id: 1 })
db.detalles_pedido.createIndex({ pedido_id: 1 })
db.detalles_pedido.createIndex({ producto_id: 1 })

// Crear usuario con permisos
db.createUser({
    user: "cafemon",
    pwd: "123",
    roles: [
        { role: "readWrite", db: "aroma_y_cafe" }
    ]
})

