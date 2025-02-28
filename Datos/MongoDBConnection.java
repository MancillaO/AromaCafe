package Datos;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
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

    public MongoDBConnection() {

        try {
            System.out.println("Iniciando conexión a MongoDB...");
            String uri = EnvLoader.get("MONGODB_URI");
            System.out.println("URI de conexión: " + uri);

            MongoClient mongoClient = MongoClients.create(uri);
            this.database = mongoClient.getDatabase("aroma_y_cafe");

            System.out.println("Conexión establecida con la base de datos: aroma_y_cafe");

            // Verificar si las colecciones existen
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

    public Connection getConnection() throws SQLException {
        // MongoDB no usa JDBC, retornamos null pero mantenemos la interfaz
        return null;
    }

    public void listCategorias() {
        try {
            MongoCollection<Document> categorias = database.getCollection("categorias");
            long count = categorias.countDocuments();

            final int LINE_WIDTH = 59;
            final int MARGIN = 1;

            for (Document cat : categorias.find()) {
                StringBuilder line = new StringBuilder();
                line.append("|")
                        .append(" ".repeat(MARGIN))
                        .append(cat.getInteger("id")).append(". ")
                        .append(cat.getString("nombre"));
                int remainingSpace = LINE_WIDTH - line.length() + 1;
                line.append(" ".repeat(Math.max(0, remainingSpace)))
                        .append("|");
                System.out.println(line);
            }
        } catch (Exception e) {
            System.err.println("Error al listar categorías: " + e.getMessage());
            e.printStackTrace();
        }
    }

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

    public List<Integer> getValidOrderIds() {
        try {
            MongoCollection<Document> pedidos = database.getCollection("pedidos");
            List<Integer> ids = new ArrayList<>();
            for (Document doc : pedidos.find()) {
                // Usamos el valor hash del ObjectId como nuestro id numérico
                ObjectId objId = doc.getObjectId("_id");
                ids.add(objId.hashCode());
            }
            return ids;
        } catch (Exception e) {
            System.err.println("Error al obtener IDs de pedidos: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void listProductos(int categoriaId) {
        try {
            MongoCollection<Document> productos = database.getCollection("productos");

            final int LINE_WIDTH = 59;
            final int MARGIN = 1;

            System.out.println("\n|===========================================================|");
            System.out.println("|                   Productos disponibles                   |");
            System.out.println("|===========================================================|");
            System.out.println("|                                                           |");

            for (Document prod : productos.find(Filters.eq("categoria_id", categoriaId))) {
                StringBuilder line = new StringBuilder();
                line.append("|");
                line.append(" ".repeat(MARGIN));
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
                line.append(productText);
                int dashesLength = LINE_WIDTH - productText.length() - precio.length() - MARGIN - 2;
                line.append("-".repeat(Math.max(0, dashesLength)));
                line.append(" " + precio);
                line.append(" |");

                System.out.println(line.toString());
            }

            System.out.println("|                                                           |");
            System.out.println("|===========================================================|\n");
        } catch (Exception e) {
            System.err.println("Error al listar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isProductInCategory(int productId, int categoryId) {
        try {
            MongoCollection<Document> productos = database.getCollection("productos");
            boolean exists = productos.find(
                    Filters.and(
                            Filters.eq("id", productId),
                            Filters.eq("categoria_id", categoryId)))
                    .first() != null;
            return exists;
        } catch (Exception e) {
            System.err.println("Error al verificar producto en categoría: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void mostrarProductoPorId(int id) {
        try {
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

    public double calcularTotalOrden(int[] ids) {
        if (ids == null || ids.length == 0) {
            System.out.println("No se proporcionaron IDs para calcular el total.");
            return 0.0;
        }

        try {
            MongoCollection<Document> productos = database.getCollection("productos");
            List<Integer> idList = new ArrayList<>();
            for (int id : ids) {
                idList.add(id);
            }

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

    public int insertPedido(double total) {
        try {
            MongoCollection<Document> pedidos = database.getCollection("pedidos");
            Document pedido = new Document("total", total)
                    .append("fecha", new java.util.Date());

            InsertOneResult result = pedidos.insertOne(pedido);
            ObjectId id = result.getInsertedId().asObjectId().getValue();

            int pedidoId = id.hashCode(); // Conversión a int para mantener compatibilidad
            System.out.println("\nPedido insertado correctamente en MongoDB. ID del pedido: " + pedidoId);

            return pedidoId;
        } catch (Exception e) {
            System.err.println("Error al insertar pedido: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public void listPedidos() {
        try {
            MongoCollection<Document> pedidos = database.getCollection("pedidos");

            for (Document pedido : pedidos.find()) {
                ObjectId objId = pedido.getObjectId("_id");
                int id = objId.hashCode();
                java.util.Date fecha = pedido.getDate("fecha");

                System.out.println("| " + id + ". " + "Pedido del dia: " + fecha + "                             |");
            }
        } catch (Exception e) {
            System.err.println("Error al listar pedidos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deletePedido(int id) {
        try {
            // Como usamos hashCode() del ObjectId para el id, no podemos buscar
            // directamente.
            // Necesitamos buscar todos los pedidos y comparar su hashCode.
            MongoCollection<Document> pedidos = database.getCollection("pedidos");
            Document pedidoToDelete = null;

            for (Document pedido : pedidos.find()) {
                ObjectId objId = pedido.getObjectId("_id");
                if (objId.hashCode() == id) {
                    pedidoToDelete = pedido;
                    break;
                }
            }

            if (pedidoToDelete != null) {
                DeleteResult result = pedidos.deleteOne(Filters.eq("_id", pedidoToDelete.getObjectId("_id")));
                if (result.getDeletedCount() > 0) {
                    System.out.println("\nPedido eliminado correctamente en MongoDB.");

                    // También eliminar detalles asociados
                    MongoCollection<Document> detalles = database.getCollection("detalles_pedido");
                    detalles.deleteMany(Filters.eq("pedido_id", id));
                } else {
                    System.out.println("No se pudo eliminar el pedido con ID " + id + " en MongoDB.");
                }
            } else {
                System.out.println("No se encontró ningún pedido con ID " + id + " en MongoDB.");
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar pedido: " + e.getMessage());
            e.printStackTrace();
        }
    }

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

    public void listDetallesPedido(int pedidoId) {
        try {
            MongoCollection<Document> detalles = database.getCollection("detalles_pedido");

            for (Document detalle : detalles.find(Filters.eq("pedido_id", pedidoId))) {
                ArrayList<Integer> productosSeleccionados = new ArrayList<>();
                productosSeleccionados.add(detalle.getInteger("producto_id"));
                resumenOrden(productosSeleccionados.stream().mapToInt(i -> i).toArray());
            }
        } catch (Exception e) {
            System.err.println("Error al listar detalles de pedido: " + e.getMessage());
            e.printStackTrace();
        }
    }
}