package Negocio;

import Datos.PostgreSQLConnection;
import Datos.DatabaseConnection;
import Datos.MySQLConnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

public class Menu {
    private Scanner scanner = new Scanner(System.in);
    private ArrayList<Integer> productosSeleccionados = new ArrayList<>();

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
            System.out.println("| 3. Salir                                                  |");
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
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opcion no válida. Intente de nuevo.");
            }
        }
    }

    private void ConectarPostgres() {
        PostgreSQLConnection postgres = new PostgreSQLConnection();
        try (Connection conn = postgres.getConnection()) {
            if (conn != null) {
                mostrarCategorias(postgres);
            }
        } catch (Exception e) {
            System.out.println("Error al conectar a PostgreSQL: " + e.getMessage());
        }
    }

    private void conectarMySQL() {
        MySQLConnection mysql = new MySQLConnection();
        try (Connection conn = mysql.getConnection()) {
            if (conn != null) {
                mostrarCategorias(mysql);
            }
        } catch (Exception e) {
            System.out.println("Error al conectar a MySQL: " + e.getMessage());
        }
    }

    private void mostrarCategorias(DatabaseConnection dbConnection) {
        System.out.println("\nConexión exitosa.");
        System.out.println("|===========================================================|");
        System.out.println("|                         MI ORDEN                          |");
        System.out.println("|                       AROMA Y CAFE                        |");
        System.out.println("|                 \"UN CAFE, MIL MOMENTOS\"                   |");
        System.out.println("|===========================================================|");
        System.out.println("|                                                           |");
        System.out.println("| ¿Alguna preferencia de tu elección en este momento?       |");
        System.out.println("|                                                           |");
        dbConnection.listCategorias();
        System.out.println("|                                                           |");
        System.out.println("|===========================================================|");
        System.out.print("Selecciona una opción: ");
        int opcionCat = scanner.nextInt();

        if (opcionCat == 6) {
            System.out.println("Gracias por visitarnos. ¡Hasta pronto!");
        }
        if (opcionCat >= 1 && opcionCat <= 5) {
            mostrarProductos(dbConnection, opcionCat);
        } else {
            System.out.println("Opción inválida, intenta de nuevo.");
        }
    }

    private void mostrarProductos(DatabaseConnection dbConnection, int opcionCat) {
        while (true) {
            dbConnection.listProductos(opcionCat);
            System.out.print("Selecciona una opción: ");
            int opcionProd = scanner.nextInt();
            dbConnection.mostrarProductoPorId(opcionProd);
            System.out.println("\n¿Desea agregar este producto? SI/NO");
            System.out.print("Selecciona: ");
            String opcionAdd = scanner.next();
            if (opcionAdd.equalsIgnoreCase("si")) {
                productosSeleccionados.add(opcionProd); // Usa la variable de instancia
                System.out.println(productosSeleccionados);
                while (true) {
                    System.out.println("\n¿Desea agregar otro producto? SI/NO");
                    System.out.print("Selecciona: ");
                    String opcionAdd2 = scanner.next();
                    if (opcionAdd2.equalsIgnoreCase("si")) {
                        break;
                    } else if (opcionAdd2.equalsIgnoreCase("no")) {
                        resumenOrden(dbConnection);
                        return;
                    } else {
                        System.out.println("Opción inválida. Intente de nuevo.");
                    }
                }
            } else if (opcionAdd.equalsIgnoreCase("no")) {
                System.out.println("Producto no agregado. Por favor, selecciona otro producto.");
            } else {
                System.out.println("Opción inválida. Intente de nuevo.");
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
            dbConnection.insertPedido(total);

            for (int idProducto : productosSeleccionados) {
                // logica para insertar detalle pedido
                System.out.println("Agregando detalle: " + idProducto);
            }

        } else {
            System.out.println("Opción inválida. Intente de nuevo.");
        }

    }
}