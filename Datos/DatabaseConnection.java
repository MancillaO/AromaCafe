package Datos;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnection {
    Connection getConnection() throws SQLException; // Permitimos que lance SQLException
    void listCategorias();
    void listProductos(int categoriaId);
    void mostrarProductoPorId(int productoId);
    void resumenOrden(int[] ids);
    void insertPedido(double total);
    double calcularTotalOrden(int[] ids);
}