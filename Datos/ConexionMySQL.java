package Datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConexionMySQL {
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/aroma_y_cafe";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "";

    // Método para obtener la conexión a MySQL
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Cargar el driver JDBC para MySQL
            return DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se pudo cargar el driver JDBC para MySQL", e);
        }
    }

    // Insertar un producto en MySQL
    public void insertProducto(String nombre, String categoria, double precio, String descripcion) {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO productos (nombre, categoria, precio, descripcion) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, nombre);
            stmt.setString(2, categoria);
            stmt.setDouble(3, precio);
            stmt.setString(4, descripcion);
            stmt.executeUpdate();
            System.out.println("Producto insertado correctamente en MySQL.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Listar productos en MySQL
    public void listProductos() {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM productos");
                ResultSet rs = stmt.executeQuery()) {
            System.out.println("Productos en MySQL:");
            while (rs.next()) {
                System.out.printf("ID: %d, Nombre: %s, Categoría: %s, Precio: %.2f, Descripción: %s%n",
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getDouble("precio"),
                        rs.getString("descripcion"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Eliminar un producto en MySQL
    public void deleteProducto(int id) {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM productos WHERE id = ?")) {
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

    // Actualizar un producto en MySQL
    public void updateProducto(int id, String nuevoNombre, String nuevaCategoria, double nuevoPrecio,
            String nuevaDescripcion) {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE productos SET nombre = ?, categoria = ?, precio = ?, descripcion = ? WHERE id = ?")) {
            stmt.setString(1, nuevoNombre);
            stmt.setString(2, nuevaCategoria);
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
}