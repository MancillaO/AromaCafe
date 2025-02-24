package Datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

public class PostgreSQLConnection implements DatabaseConnection {

    private static final String POSTGRESQL_URL = EnvLoader.get("POSTGRESQL_URL");
    private static final String POSTGRESQL_USER = EnvLoader.get("POSTGRESQL_USER");
    private static final String POSTGRESQL_PASSWORD = EnvLoader.get("POSTGRESQL_PASSWORD");

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(POSTGRESQL_URL, POSTGRESQL_USER, POSTGRESQL_PASSWORD);
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

    public void updateCategoria(int id, String nuevoNombre) {
        String query = "UPDATE categorias SET nombre = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nuevoNombre);
            stmt.setInt(2, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Categoría actualizada correctamente en PostgreSQL.");
            } else {
                System.out.println("No se encontró ninguna categoría con ID " + id + " en PostgreSQL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    public void insertPedido(double total) {
        String query = "INSERT INTO pedidos (total) VALUES (?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, total);
            stmt.executeUpdate();
            System.out.println("Pedido insertado correctamente en PostgreSQL.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listPedidos() {
        String query = "SELECT * FROM pedidos";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Fecha: " + rs.getTimestamp("fecha") +
                        ", Total: " + rs.getDouble("total"));
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
                System.out.println("Pedido eliminado correctamente en PostgreSQL.");
            } else {
                System.out.println("No se encontró ningún pedido con ID " + id + " en PostgreSQL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePedido(int id, double nuevoTotal) {
        String query = "UPDATE pedidos SET total = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, nuevoTotal);
            stmt.setInt(2, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Pedido actualizado correctamente en PostgreSQL.");
            } else {
                System.out.println("No se encontró ningún pedido con ID " + id + " en PostgreSQL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // CRUD para la tabla 'detalles_pedido'
    public void insertDetallePedido(int pedidoId, int productoId, int cantidad, double precioUnitario) {
        String query = "INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, pedidoId);
            stmt.setInt(2, productoId);
            stmt.setInt(3, cantidad);
            stmt.setDouble(4, precioUnitario);
            stmt.executeUpdate();
            System.out.println("Detalle de pedido insertado correctamente en PostgreSQL.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listDetallesPedido() {
        String query = "SELECT * FROM detalles_pedido";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Pedido ID: " + rs.getInt("pedido_id") +
                        ", Producto ID: " + rs.getInt("producto_id") + ", Cantidad: " + rs.getInt("cantidad") +
                        ", Precio Unitario: " + rs.getDouble("precio_unitario"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteDetallePedido(int id) {
        String query = "DELETE FROM detalles_pedido WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Detalle de pedido eliminado correctamente en PostgreSQL.");
            } else {
                System.out.println("No se encontró ningún detalle de pedido con ID " + id + " en PostgreSQL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateDetallePedido(int id, int nuevoPedidoId, int nuevoProductoId, int nuevaCantidad,
            double nuevoPrecioUnitario) {
        String query = "UPDATE detalles_pedido SET pedido_id = ?, producto_id = ?, cantidad = ?, precio_unitario = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, nuevoPedidoId);
            stmt.setInt(2, nuevoProductoId);
            stmt.setInt(3, nuevaCantidad);
            stmt.setDouble(4, nuevoPrecioUnitario);
            stmt.setInt(5, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Detalle de pedido actualizado correctamente en PostgreSQL.");
            } else {
                System.out.println("No se encontró ningún detalle de pedido con ID " + id + " en PostgreSQL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}