package Datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLConnection {

    private static final String MYSQL_URL = EnvLoader.get("MYSQL_URL");
    private static final String MYSQL_USER = EnvLoader.get("MYSQL_USER");
    private static final String MYSQL_PASSWORD = EnvLoader.get("MYSQL_PASSWORD");

    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("No se pudo cargar el driver JDBC", e);
        }
    }

    // centrar texto
    public static String cTxt(String texto) {
        int LINE_WIDTH = 60;
        int espacio = (LINE_WIDTH - texto.length()) / 2;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < espacio; i++) {
            sb.append(" ");
        }

        sb.append(texto);
        return sb.toString();
    }

    // CRUD para la tabla 'categorias'
    public void insertCategoria(String nombre) {
        String query = "INSERT INTO categorias (nombre) VALUES (?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombre);
            stmt.executeUpdate();
            System.out.println(cTxt("Categoría insertada correctamente en MySQL."));
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
                System.out.println(cTxt("ID: " + rs.getInt("id") + ", Nombre: " + rs.getString("nombre")));
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
                System.out.println(cTxt("Categoría eliminada correctamente en MySQL."));
            } else {
                System.out.println(cTxt("No se encontró ninguna categoría con ID " + id + " en MySQL."));
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
                System.out.println(cTxt("Categoría actualizada correctamente en MySQL."));
            } else {
                System.out.println(cTxt("No se encontró ninguna categoría con ID " + id + " en MySQL."));
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
            System.out.println(cTxt("Producto insertado correctamente en MySQL."));
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
                System.out.println(cTxt("ID: " + rs.getInt("id") + ", Nombre: " + rs.getString("nombre") +
                        ", Precio: " + rs.getDouble("precio") + ", Descripción: " + rs.getString("descripcion")));
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
                System.out.println(cTxt("Producto eliminado correctamente en MySQL."));
            } else {
                System.out.println(cTxt("No se encontró ningún producto con ID " + id + " en MySQL."));
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
                System.out.println(cTxt("Producto actualizado correctamente en MySQL."));
            } else {
                System.out.println(cTxt("No se encontró ningún producto con ID " + id + " en MySQL."));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}