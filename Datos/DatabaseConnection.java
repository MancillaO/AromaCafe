package Datos;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DatabaseConnection {
    Connection getConnection() throws SQLException;
    void listCategorias();
    void listProductos(int categoriaId);
    void mostrarProductoPorId(int productoId);
    void resumenOrden(int[] ids);
    int insertPedido(double total);
    double calcularTotalOrden(int[] ids);
    void insertDetallePedido(int pedidoId, int productoId);
    boolean isProductInCategory(int productId, int categoryId);
    List<Integer> getValidCategoryIds();
    List<Integer> getValidOrderIds();
    void listPedidos();
    void listDetallesPedido(int pedidoId);
    void deletePedido(int id);
}