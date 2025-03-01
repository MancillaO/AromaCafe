package Negocio;

import Datos.PostgreSQLConnection;
import Datos.DatabaseConnection;
import Datos.MongoDBConnection;
import Datos.MySQLConnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private Scanner scanner = new Scanner(System.in);
    private ArrayList<Integer> productosSeleccionados = new ArrayList<>();
    Pedidos pedidos = new Pedidos();

    private void ConectarPostgres() {
        PostgreSQLConnection postgres = new PostgreSQLConnection();
        try (Connection conn = postgres.getConnection()) {
            if (conn != null) {
                menuInicio(postgres);
                productosSeleccionados.clear();
            }
        } catch (Exception e) {
            System.out.println("Error al conectar a PostgreSQL: " + e.getMessage());
        }
    }

    private void conectarMySQL() {
        MySQLConnection mysql = new MySQLConnection();
        try (Connection conn = mysql.getConnection()) {
            if (conn != null) {
                menuInicio(mysql);
                productosSeleccionados.clear();
            }
        } catch (Exception e) {
            System.out.println("Error al conectar a MySQL: " + e.getMessage());
        }
    }

    private void conectarMongoDB() {
        MongoDBConnection mongo = new MongoDBConnection();
        try {
            menuInicio(mongo);
            productosSeleccionados.clear();
        } catch (Exception e) {
            System.out.println("Error al conectar a MongoDB: " + e.getMessage());
        }
    }

    public void iniciar() {
        int opcion = 0;
        while (opcion != 3) {
            System.out.println("\n|===========================================================|");
            System.out.println("|                 BIENVENIDO A AROMA Y CAFE                 |");
            System.out.println("|                  \"UN CAFE, MIL MOMENTOS\"                  |");
            System.out.println("|===========================================================|");
            System.out.println("|                                                           |");
            System.out.println("| Seleccione la base de datos a la que desea conectarse:    |");
            System.out.println("|                                                           |");
            System.out.println("| 1. PostgreSQL                                             |");
            System.out.println("| 2. MySQL                                                  |");
            System.out.println("| 3. MongoDB                                                |");
            System.out.println("| 4. Salir                                                  |");
            System.out.println("|                                                           |");
            System.out.println("|===========================================================|");
            System.out.print("Selecciona una opcion: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    ConectarPostgres();
                    break;
                case 2:
                    conectarMySQL();
                    break;
                case 3:
                    conectarMongoDB();
                    break;
                case 4:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opcion no válida. Intente de nuevo.");
            }
        }
    }

    public void menuInicio(DatabaseConnection dbConnection) {
        System.out.println("\n|===========================================================|");
        System.out.println("|                 BIENVENIDO A AROMA Y CAFE                 |");
        System.out.println("|                 \"UN CAFE, MIL MOMENTOS\"                   |");
        System.out.println("|===========================================================|");
        System.out.println("|                                                           |");
        System.out.println("| Esperamos que te sientas genial.                          |");
        System.out.println("| ¿Que deseas hacer?                                        |");
        System.out.println("|                                                           |");
        System.out.println("| 1. Menu                                                   |");
        System.out.println("| 2. Historial de Pedidos                                   |");
        System.out.println("| 3. Salir                                                  |");
        System.out.println("|                                                           |");
        System.out.println("|===========================================================|");
        System.out.print("Selecciona una opcion: ");
        int opcion = scanner.nextInt();

        switch (opcion) {
            case 1:
                mostrarCategorias(dbConnection);
                break;
            case 2:
                pedidos.MostrarPedidos(dbConnection);
                break;
            case 3:
                System.out.println("Saliendo...");
                break;
            default:
                System.out.println("\nOpcion invalida");
                menuInicio(dbConnection);
                break;
        }
    }

    private void mostrarCategorias(DatabaseConnection dbConnection) {
        while (true) {
            System.out.println("|===========================================================|");
            System.out.println("|                           MENU                            |");
            System.out.println("|                       AROMA Y CAFE                        |");
            System.out.println("|                 \"UN CAFE, MIL MOMENTOS\"                   |");
            System.out.println("|===========================================================|");
            System.out.println("|                                                           |");
            System.out.println("| ¿Alguna preferencia de tu eleccion en este momento?       |");
            System.out.println("|                                                           |");
            dbConnection.listCategorias();
            System.out.println("|                                                           |");
            System.out.println("|===========================================================|");

            List<Integer> validCategoryIds = dbConnection.getValidCategoryIds();
            System.out.print("Selecciona una opcion o ingresar 0 para regresar: ");

            while (!scanner.hasNextInt()) {
                System.out.print("Opción inválida. Intente de nuevo: ");
                scanner.next();
            }
            int opcionCat = scanner.nextInt();
            if (opcionCat == 0) {
                menuInicio(dbConnection);
                return;
            } else if (validCategoryIds.contains(opcionCat)) {
                mostrarProductos(dbConnection, opcionCat);
                break;
            } else {
                System.out.println("\nOpción invalida, intenta de nuevo.");
            }
        }
    }

    private void mostrarProductos(DatabaseConnection dbConnection, int opcionCat) {
        while (true) {
            dbConnection.listProductos(opcionCat);
            System.out.print("Selecciona una opcion o ingresar 0 para regresar: ");
            while (!scanner.hasNextInt()) {
                System.out.print("Opción inválida. Intente de nuevo: ");
                scanner.next();
            }
            int opcionProd = scanner.nextInt();
            if (opcionProd == 0) {
                mostrarCategorias(dbConnection);
                return;
            } else if (!dbConnection.isProductInCategory(opcionProd, opcionCat)) {
                System.out.println("\nOpcion no valida");
                continue;
            }
            if (opcionProd == 0) {
                mostrarCategorias(dbConnection);
                return;

            }
            System.out.println("\n=============================================================");

            dbConnection.mostrarProductoPorId(opcionProd);
            System.out.println("¿Desea agregar este producto? SI/NO");
            System.out.print("Selecciona: ");
            String opcionAdd = scanner.next();
            if (opcionAdd.equalsIgnoreCase("si")) {
                productosSeleccionados.add(opcionProd);
                // System.out.println(productosSeleccionados);
                while (true) {
                    System.out.println("\n¿Desea algo mas? SI/NO");
                    System.out.print("Selecciona: ");
                    String opcionAdd2 = scanner.next();
                    if (opcionAdd2.equalsIgnoreCase("si")) {
                        break;
                    } else if (opcionAdd2.equalsIgnoreCase("no")) {
                        resumenOrden(dbConnection);
                        return;
                    } else {
                        System.out.println("Opcion invalida. Intente de nuevo.");
                    }
                }
            } else if (opcionAdd.equalsIgnoreCase("no")) {
                System.out.println("Producto no agregado. Por favor, selecciona otro producto.");
            } else {
                System.out.println("Opción invalida. Intente de nuevo.");
            }
        }
    }

    private void resumenOrden(DatabaseConnection dbConnection) {
        System.out.println("\n|===========================================================|");
        System.out.println("|                         MI ORDEN                          |");
        System.out.println("|                       AROMA Y CAFE                        |");
        System.out.println("|                 \"UN CAFE, MIL MOMENTOS\"                   |");
        System.out.println("|===========================================================|");
        System.out.println("|                                                           |");
        System.out.println("| Tu orden hasta ahora es:                                  |");
        System.out.println("|                                                           |");
        dbConnection.resumenOrden(productosSeleccionados.stream().mapToInt(i -> i).toArray());
        System.out.println("|                                                           |");
        System.out.println("|===========================================================|\n");
        System.out.print("¿Desea agregar algo mas o confirmar su orden (A/C)? ");
        String opcion = scanner.next();
        if (opcion.equalsIgnoreCase("a")) {
            mostrarCategorias(dbConnection);
        } else if (opcion.equalsIgnoreCase("c")) {

            double total = dbConnection.calcularTotalOrden(productosSeleccionados.stream().mapToInt(i -> i).toArray());
            int idPedido = dbConnection.insertPedido(total);
            for (int idProducto : productosSeleccionados) {
                dbConnection.insertDetallePedido(idPedido, idProducto);
            }
            System.out.println("\n¡Pedido #" + idPedido + " confirmado con éxito!");
            System.out.println("¡Gracias por su compra!");
            productosSeleccionados.clear();
            menuInicio(dbConnection);

        } else {
            System.out.println("Opcion invalida. Intente de nuevo.");
            resumenOrden(dbConnection);
        }
    }
}