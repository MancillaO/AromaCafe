package Datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLConnection {

    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/aroma_y_cafe";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            //Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se pudo cargar el driver JDBC", e);
        }
    }

    // CRUD para la tabla 'categorias'
    public void insertCategoria(String nombre) {
        String query = "INSERT INTO categorias (nombre) VALUES (?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombre);
            stmt.executeUpdate();
            System.out.println("Categoría insertada correctamente en MySQL.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listCategorias() {
        String query = "SELECT * FROM categorias";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Nombre: " + rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCategoria(int id) {
        String query = "DELETE FROM categorias WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Categoría eliminada correctamente en MySQL.");
            } else {
                System.out.println("No se encontró ninguna categoría con ID " + id + " en MySQL.");
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
                System.out.println("Categoría actualizada correctamente en MySQL.");
            } else {
                System.out.println("No se encontró ninguna categoría con ID " + id + " en MySQL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // CRUD para la tabla 'productos'
    public void insertProducto(String nombre, int categoriaId, double precio, String descripcion) {
        String query = "INSERT INTO productos (nombre, categoria_id, precio, descripcion) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombre);
            stmt.setInt(2, categoriaId);
            stmt.setDouble(3, precio);
            stmt.setString(4, descripcion);
            stmt.executeUpdate();
            System.out.println("Producto insertado correctamente en MySQL.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listProductos() {
        String query = "SELECT * FROM productos";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Nombre: " + rs.getString("nombre") +
                        ", Precio: " + rs.getDouble("precio") + ", Descripción: " + rs.getString("descripcion"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteProducto(int id) {
        String query = "DELETE FROM productos WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Producto eliminado correctamente en MySQL.");
            } else {
                System.out.println("No se encontró ningún producto con ID " + id + " en MySQL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateProducto(int id, String nuevoNombre, int nuevaCategoriaId, double nuevoPrecio,
            String nuevaDescripcion) {
        String query = "UPDATE productos SET nombre = ?, categoria_id = ?, precio = ?, descripcion = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nuevoNombre);
            stmt.setInt(2, nuevaCategoriaId);
            stmt.setDouble(3, nuevoPrecio);
            stmt.setString(4, nuevaDescripcion);
            stmt.setInt(5, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Producto actualizado correctamente en MySQL.");
            } else {
                System.out.println("No se encontró ningún producto con ID " + id + " en MySQL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // CRUD para la tabla 'pedidos'
    public void insertPedido(double total) {
        String query = "INSERT INTO pedidos (total) VALUES (?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, total);
            stmt.executeUpdate();
            System.out.println("Pedido insertado correctamente en MySQL.");
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
                System.out.println("Pedido eliminado correctamente en MySQL.");
            } else {
                System.out.println("No se encontró ningún pedido con ID " + id + " en MySQL.");
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
                System.out.println("Pedido actualizado correctamente en MySQL.");
            } else {
                System.out.println("No se encontró ningún pedido con ID " + id + " en MySQL.");
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
            System.out.println("Detalle de pedido insertado correctamente en MySQL.");
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
                System.out.println("Detalle de pedido eliminado correctamente en MySQL.");
            } else {
                System.out.println("No se encontró ningún detalle de pedido con ID " + id + " en MySQL.");
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
                System.out.println("Detalle de pedido actualizado correctamente en MySQL.");
            } else {
                System.out.println("No se encontró ningún detalle de pedido con ID " + id + " en MySQL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
