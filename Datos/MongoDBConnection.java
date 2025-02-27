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

    public MongoDBConnection() {
        String uri = EnvLoader.get("MONGODB_URI");
        MongoClient mongoClient = MongoClients.create(uri);
        this.database = mongoClient.getDatabase("aroma_y_cafe");
    }

    @Override
    public Connection getConnection() throws SQLException {
        // MongoDB no usa JDBC, retornamos null pero mantenemos la interfaz
        return null;
    }

    @Override
    public void listCategorias() {
        MongoCollection<Document> categorias = database.getCollection("categorias");
        final int LINE_WIDTH = 60;
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
    }

    @Override
    public List<Integer> getValidCategoryIds() {
        MongoCollection<Document> categorias = database.getCollection("categorias");
        List<Integer> ids = new ArrayList<>();
        for (Document doc : categorias.find()) {
            ids.add(doc.getInteger("id"));
        }
        return ids;
    }

    @Override
    public void listProductos(int categoriaId) {
        MongoCollection<Document> productos = database.getCollection("productos");
        final int LINE_WIDTH = 59;
        final int MARGIN = 1;

        System.out.println("\n|===========================================================|");
        System.out.println("|                   Productos disponibles                    |");
        System.out.println("|===========================================================|");

        for (Document prod : productos.find(Filters.eq("categoria_id", categoriaId))) {
            String id = prod.getInteger("id").toString();
            String nombre = prod.getString("nombre");
            String precio = String.format("$%d", prod.getInteger("precio"));
            String productText = id + ". " + nombre + " ";
            int dashesLength = LINE_WIDTH - productText.length() - precio.length() - MARGIN - 2;

            System.out.println(
                    "|" + " ".repeat(MARGIN) + productText +
                            "-".repeat(dashesLength) + " " + precio + " |");
        }
        System.out.println("|===========================================================|\n");
    }

    @Override
    public boolean isProductInCategory(int productId, int categoryId) {
        MongoCollection<Document> productos = database.getCollection("productos");
        return productos.find(
                Filters.and(
                        Filters.eq("id", productId),
                        Filters.eq("categoria_id", categoryId)))
                .first() != null;
    }

    @Override
    public void mostrarProductoPorId(int id) {
        MongoCollection<Document> productos = database.getCollection("productos");
        Document prod = productos.find(Filters.eq("id", id)).first();

        if (prod != null) {
            System.out.println("\n" + prod.getString("nombre") + ": " + prod.getString("descripcion") + "\n");
        } else {
            System.out.println("No se encontró ningún producto con el ID: " + id);
        }
    }

    @Override
    public void resumenOrden(int[] ids) {
        MongoCollection<Document> productos = database.getCollection("productos");
        List<Integer> idList = new ArrayList<>();
        for (int id : ids)
            idList.add(id);

        for (Document prod : productos.find(Filters.in("id", idList))) {
            String productText = prod.getString("nombre") + " $" + prod.getInteger("precio");
            System.out.println("| " + productText + " |");
        }
    }

    @Override
    public double calcularTotalOrden(int[] ids) {
        MongoCollection<Document> productos = database.getCollection("productos");
        List<Integer> idList = new ArrayList<>();
        for (int id : ids)
            idList.add(id);

        double total = 0;
        for (Document prod : productos.find(Filters.in("id", idList))) {
            total += prod.getDouble("precio");
        }
        return total;
    }

    @Override
    public int insertPedido(double total) {
        MongoCollection<Document> pedidos = database.getCollection("pedidos");
        InsertOneResult result = pedidos.insertOne(
                new Document("total", total)
                        .append("fecha", new java.util.Date()));
        return result.getInsertedId().asObjectId().getValue().hashCode(); // Conversión a int
    }

    @Override
    public void insertDetallePedido(int pedidoId, int productoId) {
        MongoCollection<Document> detalles = database.getCollection("detalles_pedido");
        detalles.insertOne(
                new Document("pedido_id", pedidoId)
                        .append("producto_id", productoId));
    }
}