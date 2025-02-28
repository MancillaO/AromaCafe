package Datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostgreSQLConnection implements DatabaseConnection {

    private String ip;
    private String dbName;
    private String user;
    private String password;


    public PostgreSQLConnection(String ip, String dbName, String user, String password) {
        this.ip = ip;
        this.dbName = dbName;
        this.user = user;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {

        if (ip == null || dbName == null || user == null || password == null) {

            throw new SQLException("Los parámetros de conexión son inválidos.");

        }
        try {
            String url = "jdbc:postgresql://" + ip + ":5432/" + dbName;
            
            return DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            throw new SQLException("No se pudo conectar a la base de datos", e);
        }
    }

    public void listCategorias() {
        String query = "SELECT * FROM categorias";
        final int LINE_WIDTH = 60;
        final int MARGIN = 1;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                StringBuilder line = new StringBuilder();
                line.append("|");
                line.append(" ".repeat(MARGIN));
                String categoryText = rs.getInt("id") + ". " + rs.getString("nombre");
                line.append(categoryText);
                int remainingSpace = LINE_WIDTH - categoryText.length() - MARGIN - 1;
                line.append(" ".repeat(remainingSpace));
                line.append("|");
                System.out.println(line.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // En PostgreSQLConnection.java
    public List<Integer> getValidCategoryIds() {
        List<Integer> validIds = new ArrayList<>();
        String query = "SELECT id FROM categorias";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                validIds.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return validIds;
    }

    public List<Integer> getValidOrderIds() {
        List<Integer> validIds = new ArrayList<>();
        String query = "SELECT id FROM pedidos";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                validIds.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return validIds;
    }

    public void listProductos(int categoriaId) {
        String query = "SELECT * FROM productos WHERE categoria_id = ?";
        final int LINE_WIDTH = 59;
        final int MARGIN = 1;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, categoriaId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("\n|===========================================================|");
            System.out.println("|                   Productos disponibles                   |");
            System.out.println("|===========================================================|");
            System.out.println("|                                                           |");
            while (rs.next()) {
                StringBuilder line = new StringBuilder();
                line.append("|");
                line.append(" ".repeat(MARGIN));
                String id = String.valueOf(rs.getInt("id"));
                String nombre = rs.getString("nombre");
                String precio = String.format("$%d", rs.getInt("precio"));
                String productText = id + ". " + nombre + " ";
                line.append(productText);
                int dashesLength = LINE_WIDTH - productText.length() - precio.length() - MARGIN - 2;
                line.append("-".repeat(dashesLength));
                line.append(" " + precio);
                line.append(" |");
                System.out.println(line.toString());
            }
            System.out.println("|                                                           |");
            System.out.println("|===========================================================|\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isProductInCategory(int productId, int categoryId) {
        String query = "SELECT COUNT(*) FROM productos WHERE id = ? AND categoria_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, categoryId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void mostrarProductoPorId(int id) {
        String query = "SELECT * FROM productos WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n" + rs.getString("nombre") + ": " + rs.getString("descripcion") + "\n");
                } else {
                    System.out.println("No se encontró ningún producto con el ID: " + id);
                }
            }
        } catch (SQLException e) {
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
        String query = "SELECT * FROM productos WHERE id IN (" + String.join(",", Collections.nCopies(ids.length, "?"))
                + ")";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < ids.length; i++) {
                stmt.setInt(i + 1, ids[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                boolean encontrado = false;
                while (rs.next()) {
                    encontrado = true;
                    StringBuilder line = new StringBuilder();
                    line.append("|");
                    line.append(" ".repeat(MARGIN));
                    String categoryText = rs.getString("nombre") + " $" + rs.getInt("precio");
                    line.append(categoryText);
                    int remainingSpace = LINE_WIDTH - categoryText.length() - MARGIN - 1;
                    line.append(" ".repeat(remainingSpace));
                    line.append("|");
                    System.out.println(line.toString());
                }
                if (!encontrado) {
                    System.out.println("No se encontraron productos con los IDs proporcionados.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double calcularTotalOrden(int[] ids) {
        // Validar entrada
        if (ids == null || ids.length == 0) {
            System.out.println("No se proporcionaron IDs para calcular el total.");
            return 0.0;
        }

        // Query para obtener los precios de los productos
        String query = "SELECT precio FROM productos WHERE id IN ("
                + String.join(",", Collections.nCopies(ids.length, "?")) + ")";
        double total = 0.0;

        try (Connection conn = getConnection(); // Asegúrate de que getConnection() esté implementado
                PreparedStatement stmt = conn.prepareStatement(query)) {

            // Asignar los IDs al PreparedStatement
            for (int i = 0; i < ids.length; i++) {
                stmt.setInt(i + 1, ids[i]);
            }

            // Ejecutar la consulta
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Sumar el precio de cada producto al total
                    total += rs.getDouble("precio");
                }
            }

        } catch (SQLException e) {
            // Manejar errores de la base de datos
            System.err.println("Error al calcular el total de la orden: " + e.getMessage());
        }

        return total;
    }

    // CRUD para la tabla 'pedidos'
    public int insertPedido(double total) {
        String query = "INSERT INTO pedidos (total) VALUES (?) RETURNING id";
        int pedidoId = -1;
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, total);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pedidoId = rs.getInt("id"); // Obtener el ID generado
                    // System.out.println("\nPedido insertado correctamente en PostgreSQL. ID del
                    // pedido: " + pedidoId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pedidoId; // Retornar el ID del pedido
    }

    public void listPedidos() {
        String query = "SELECT * FROM pedidos";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("| ID: " + rs.getInt("id") + ". " + "Pedido del dia: " + rs.getDate("fecha")
                        + "                         |");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePedido(int id) {
        String query = "DELETE FROM pedidos WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\nPedido eliminado correctamente en PostgreSQL.");
            } else {
                System.out.println("No se encontró ningún pedido con ID " + id + " en PostgreSQL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // CRUD para la tabla 'detalles_pedido'
    public void insertDetallePedido(int pedidoId, int productoId) {
        String query = "INSERT INTO detalles_pedido (pedido_id, producto_id) VALUES (?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, pedidoId);
            stmt.setInt(2, productoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listDetallesPedido(int pedidoId) {
        String query = "SELECT * FROM detalles_pedido WHERE pedido_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, pedidoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ArrayList<Integer> productosSeleccionados = new ArrayList<>();
                    productosSeleccionados.add(rs.getInt("producto_id"));
                    resumenOrden(productosSeleccionados.stream().mapToInt(i -> i).toArray());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}