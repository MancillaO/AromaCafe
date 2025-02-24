package Datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class PostgreSQLConnection {

    private static final String POSTGRESQL_URL = EnvLoader.get("POSTGRESQL_URL");
    private static final String POSTGRESQL_USER = EnvLoader.get("POSTGRESQL_USER");
    private static final String POSTGRESQL_PASSWORD = EnvLoader.get("POSTGRESQL_PASSWORD");

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
                line.append(cTxt(color(categoryText))); 
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
                System.out.println("Categoría actualizada correctamente en PostgreSQL.");
            } else {
                System.out.println("No se encontró ninguna categoría con ID " + id + " en PostgreSQL.");
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
            System.out.println(cTxt(color("|~|")));
            System.out.println(cTxt(color("|                   Productos disponibles                   |")));
            System.out.println(cTxt(color("|~|")));
            System.out.println(cTxt(color("|                                                           |")));
            while (rs.next()) {
                StringBuilder line = new StringBuilder();
                line.append("|");
                line.append(" ".repeat(MARGIN));
                String id = String.valueOf(rs.getInt("id"));
                String nombre = rs.getString("nombre");
                String precio = String.format("$%d", rs.getInt("precio"));
                String productText = id + ". " + nombre + " ";
                line.append(cTxt(color(productText))); 
                int dashesLength = LINE_WIDTH - productText.length() - precio.length() - MARGIN - 2;
                line.append("-".repeat(dashesLength));
                line.append(" " + precio);
                line.append(" |");
                System.out.println(line.toString());
            }
            System.out.println(cTxt(color("|                                                           |")));
            System.out.println(cTxt(color("|~|\n")));
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
                    System.out.println("No se encontró ningún producto con el ID: " + id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // CRUD para la tabla 'pedidos'
    public void insertPedido(double total) {
        Calendar calendario = Calendar.getInstance();
        java.sql.Date fechaSQL = new java.sql.Date(calendario.getTimeInMillis());
        String query = "INSERT INTO pedidos (fecha, total) VALUES (?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(2,total );
            stmt.setDate(1, fechaSQL);
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
                System.out.println("No se encontró ningún pedido con ID " + id + " en PostgreSQL.");
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
                System.out.println("No se encontró ningún pedido con ID " + id + " en PostgreSQL.");
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
                System.out.println("No se encontró ningún detalle de pedido con ID " + id + " en PostgreSQL.");
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
                System.out.println("No se encontró ningún detalle de pedido con ID " + id + " en PostgreSQL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // centrar texto 
    public static String cTxt(String texto) {
        int LINE_WIDTH = 60; 
        int espacio = (LINE_WIDTH - texto.length()) / 2;
        StringBuilder sb = new StringBuilder();
    
    // Agregar espacios antes del texto para centrarlo
    for (int i = 0; i < espacio; i++) {
        sb.append(" ");
    }
    
    sb.append(texto);
    return sb.toString();
    }

    // color de ~ a gris
    public static String color(String texto) {
    return texto.replace("", "\033[38;5;238m\033[0m"); 
    }


}