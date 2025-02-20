package Datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConexionPostgres {
    private static final String POSTGRESQL_URL = "jdbc:postgresql://localhost:5432/aroma_y_cafe";
    private static final String POSTGRESQL_USER = "postgres";
    private static final String POSTGRESQL_PASSWORD = "2801";

    // Método para obtener la conexión a PostgreSQL
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver"); // Cargar el driver JDBC para PostgreSQL
            return DriverManager.getConnection(POSTGRESQL_URL, POSTGRESQL_USER, POSTGRESQL_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se pudo cargar el driver JDBC para PostgreSQL", e);
        }
    }

    // Insertar un producto en PostgreSQL
    public void insertProducto(String nombre, String categoria, double precio, String descripcion) {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO productos (nombre, categoria, precio, descripcion) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, nombre);
            stmt.setString(2, categoria);
            stmt.setDouble(3, precio);
            stmt.setString(4, descripcion);
            stmt.executeUpdate();
            System.out.println("Producto insertado correctamente en PostgreSQL.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Listar productos en PostgreSQL
    public void listProductos() {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM productos");
                ResultSet rs = stmt.executeQuery()) {
            System.out.println("Productos en PostgreSQL:");
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

    // Eliminar un producto en PostgreSQL
    public void deleteProducto(int id) {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM productos WHERE id = ?")) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Producto eliminado correctamente en PostgreSQL.");
            } else {
                System.out.println("No se encontró ningún producto con ID " + id + " en PostgreSQL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Actualizar un producto en PostgreSQL
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
                System.out.println("Producto actualizado correctamente en PostgreSQL.");
            } else {
                System.out.println("No se encontró ningún producto con ID " + id + " en PostgreSQL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}