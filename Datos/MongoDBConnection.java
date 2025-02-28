package Datos;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MongoDBConnection implements DatabaseConnection {

    private final MongoDatabase database;
    private String ip;
    private String dbName;
    private String user;
    private String password;
    

   public MongoDBConnection(String ip, String dbName, String user, String password) {
        this.ip = ip;
        this.dbName = dbName;
        this.user = user;
        this.password = password;

        try {
            System.out.println("Iniciando conexión a MongoDB...");
            
            String uri = "mongodb://" + user + ":" + password + "@" + ip + ":27017/" + dbName;
            System.out.println("URI de conexión: " + uri);

            MongoClient mongoClient = MongoClients.create(uri);
            this.database = mongoClient.getDatabase(dbName);

            System.out.println("Conexión establecida con la base de datos: " + dbName);

            System.out.println("Colecciones disponibles:");
            database.listCollectionNames().forEach(collectionName -> {
                System.out.println(" - " + collectionName);
            });
        } catch (Exception e) {
            System.err.println("Error al inicializar la conexión MongoDB: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error de conexión a MongoDB", e);
        }
    }

    public MongoDatabase getDatabase() {
        return this.database;
    }

    public void getUsers() {
        try {
            MongoCollection<Document> usersCollection = database.getCollection("users");
            
            if (usersCollection == null) {
                System.out.println("La colección 'users' no existe en la base de datos.");
                return;
            }

            System.out.println("===================================================");

            for (Document doc : usersCollection.find()) {
                System.out.println(doc.toJson());
            }
            
            System.out.println("===================================================");

        } catch (MongoException e) {
            System.err.println("Error al acceder a la colección 'users': " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        // MongoDB no usa JDBC, retornamos null pero mantenemos la interfaz
        return null;
    }

    @Override
    public void listCategorias() {
        try {
            MongoCollection<Document> categorias = database.getCollection("categorias");
            long count = categorias.countDocuments();

            final int LINE_WIDTH = 62;
            final int MARGIN = 1;

            for (Document cat : categorias.find()) {
                StringBuilder line = new StringBuilder();
                line.append("|")
                        .append(" ".repeat(MARGIN))
                        .append(cat.getInteger("id")).append(". ")
                        .append(cat.getString("nombre"));
                int remainingSpace = LINE_WIDTH - line.length() - MARGIN - 1;
                line.append(" ".repeat(remainingSpace))
                        .append("|");
                System.out.println(line);
            }
        } catch (Exception e) {
            System.err.println("Error al listar categorías: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Integer> getValidCategoryIds() {
        try {
            MongoCollection<Document> categorias = database.getCollection("categorias");
            List<Integer> ids = new ArrayList<>();
            for (Document doc : categorias.find()) {
                ids.add(doc.getInteger("id"));
            }
            return ids;
        } catch (Exception e) {
            System.err.println("Error al obtener IDs de categorías: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void listProductos(int categoriaId) {
        try {
            System.out.println("Listando productos para categoría ID: " + categoriaId);
            MongoCollection<Document> productos = database.getCollection("productos");
            long count = productos.countDocuments(Filters.eq("categoria_id", categoriaId));
            System.out.println("Número de productos encontrados: " + count);

            final int LINE_WIDTH = 59;
            final int MARGIN = 1;

            System.out.println("\n|===========================================================|");
            System.out.println("|                  Productos disponibles                    |");
            System.out.println("|===========================================================|");

            for (Document prod : productos.find(Filters.eq("categoria_id", categoriaId))) {
                String id = prod.getInteger("id").toString();
                String nombre = prod.getString("nombre");
                // Maneja tanto Integer como Double para el precio
                String precio;
                if (prod.get("precio") instanceof Integer) {
                    precio = String.format("$%d", prod.getInteger("precio"));
                } else {
                    precio = String.format("$%.2f", prod.getDouble("precio"));
                }
                String productText = id + ". " + nombre + " ";
                int dashesLength = LINE_WIDTH - productText.length() - precio.length() - MARGIN - 2;

                System.out.println(
                        "|" + " ".repeat(MARGIN) + productText +
                                "-".repeat(Math.max(0, dashesLength)) + " " + precio + " |");
            }
            System.out.println("|===========================================================|\n");
        } catch (Exception e) {
            System.err.println("Error al listar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean isProductInCategory(int productId, int categoryId) {
        try {
            // System.out.println("Verificando si el producto " + productId + " está en la
            // categoría " + categoryId);
            MongoCollection<Document> productos = database.getCollection("productos");
            boolean exists = productos.find(
                    Filters.and(
                            Filters.eq("id", productId),
                            Filters.eq("categoria_id", categoryId)))
                    .first() != null;
            // System.out.println("Resultado: " + exists);
            return exists;
        } catch (Exception e) {
            System.err.println("Error al verificar producto en categoría: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void mostrarProductoPorId(int id) {
        try {
            // System.out.println("Buscando producto con ID: " + id);
            MongoCollection<Document> productos = database.getCollection("productos");
            Document prod = productos.find(Filters.eq("id", id)).first();

            if (prod != null) {
                System.out.println("\n" + prod.getString("nombre") + ": " + prod.getString("descripcion") + "\n");
            } else {
                System.out.println("No se encontró ningún producto con el ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("Error al mostrar producto por ID: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void resumenOrden(int[] ids) {
        final int LINE_WIDTH = 60;
        final int MARGIN = 1;

        if (ids == null || ids.length == 0) {
            System.out.println("No se proporcionaron IDs para buscar.");
            return;
        }

        try {
            MongoCollection<Document> productos = database.getCollection("productos");
            List<Integer> idList = new ArrayList<>();
            for (int id : ids) {
                idList.add(id);
            }

            boolean encontrado = false;
            for (Document prod : productos.find(Filters.in("id", idList))) {
                encontrado = true;
                StringBuilder line = new StringBuilder();
                line.append("|");
                line.append(" ".repeat(MARGIN));

                // Maneja tanto Integer como Double para el precio
                String precio;
                if (prod.get("precio") instanceof Integer) {
                    precio = "$" + prod.getInteger("precio");
                } else {
                    precio = String.format("$%.2f", prod.getDouble("precio"));
                }

                String categoryText = prod.getString("nombre") + " " + precio;
                line.append(categoryText);

                int remainingSpace = LINE_WIDTH - categoryText.length() - MARGIN - 1;
                line.append(" ".repeat(Math.max(remainingSpace, 0)));
                line.append("|");

                System.out.println(line.toString());
            }

            if (!encontrado) {
                System.out.println("No se encontraron productos con los IDs proporcionados.");
            }
        } catch (Exception e) {
            System.err.println("Error al generar resumen de orden: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public double calcularTotalOrden(int[] ids) {
        try {
            MongoCollection<Document> productos = database.getCollection("productos");
            List<Integer> idList = new ArrayList<>();
            for (int id : ids)
                idList.add(id);

            double total = 0;
            for (Document prod : productos.find(Filters.in("id", idList))) {
                // Maneja tanto Integer como Double para el precio
                if (prod.get("precio") instanceof Integer) {
                    total += prod.getInteger("precio");
                } else {
                    total += prod.getDouble("precio");
                }
            }
            return total;
        } catch (Exception e) {
            System.err.println("Error al calcular total: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int insertPedido(double total) {
        try {
            MongoCollection<Document> pedidos = database.getCollection("pedidos");
            InsertOneResult result = pedidos.insertOne(
                    new Document("total", total)
                            .append("fecha", new java.util.Date()));

            ObjectId id = result.getInsertedId().asObjectId().getValue();
            return id.hashCode(); // Conversión a int
        } catch (Exception e) {
            System.err.println("Error al insertar pedido: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void insertDetallePedido(int pedidoId, int productoId) {
        try {
            MongoCollection<Document> detalles = database.getCollection("detalles_pedido");
            detalles.insertOne(
                    new Document("pedido_id", pedidoId)
                            .append("producto_id", productoId));
        } catch (Exception e) {
            System.err.println("Error al insertar detalle de pedido: " + e.getMessage());
            e.printStackTrace();
        }
    }
}