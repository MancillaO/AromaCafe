package Negocio;

// import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Datos.DatabaseConnection;

public class Pedidos {
    private Scanner scanner = new Scanner(System.in);

    public void MostrarPedidos(DatabaseConnection dbConnection) {
        System.out.println("\n|===========================================================|");
        System.out.println("|                                                           |");
        System.out.println("|                    HISTORIAL DE PEDIDOS                   |");
        System.out.println("|                                                           |");
        System.out.println("|===========================================================|");
        System.out.println("|                                                           |");
        System.out.println("| Ingresa el ID de un pedido para ver su detalle            |");
        System.out.println("| Ingresa 0 para salir                                      |");
        System.out.println("|                                                           |");
        dbConnection.listPedidos();
        System.out.println("|                                                           |");
        System.out.println("|===========================================================|");
        System.out.print("Selecciona una opcion: ");
        int opcion = scanner.nextInt();
        List<Integer> idsValidos = dbConnection.getValidOrderIds();

        if (opcion == 0) {
            System.out.println("\nSaliendo...");
        } else if (idsValidos.contains(opcion)) {
            detallePedido(dbConnection, opcion);
        } else {
            System.out.println("\nEl ID de pedido no es valido");
            MostrarPedidos(dbConnection);
        }
    }

    public void detallePedido(DatabaseConnection dbConnection, int opcion) {
        System.out.println("\n|===========================================================|");
        System.out.println("|                                                           |");
        System.out.println("|                     DETALLES DE PEDIDO                    |");
        System.out.println("|                                                           |");
        System.out.println("|===========================================================|");
        System.out.println("|                                                           |");
        System.out.println("| Productos Seleccionados                                   |");
        System.out.println("|                                                           |");
        dbConnection.listDetallesPedido(opcion);
        System.out.println("|                                                           |");
        System.out.println("| Si desea eliminar este pedido ingrese \"E\"                 |");
        System.out.println("| Si desea volver al historial ingrese \"V\"                  |");
        System.out.println("|                                                           |");
        System.out.println("|===========================================================|");
        System.out.print("Selecciona una opcion: ");
        String opi = scanner.next();

        if (opi.equalsIgnoreCase("E")) {
            dbConnection.deletePedido(opcion);
            MostrarPedidos(dbConnection);
        } else if (opi.equalsIgnoreCase("V")) {
            MostrarPedidos(dbConnection);
        } else {
            System.out.println("\nOpcion no valida");
            MostrarPedidos(dbConnection);
        }
    }
}
