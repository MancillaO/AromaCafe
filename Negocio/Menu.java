package Negocio;

import Datos.PostgreSQLConnection;
import Datos.DatabaseConnection;
import Datos.MySQLConnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

public class Menu {
    private Scanner scanner = new Scanner(System.in);

    public void iniciar() {
        int opcion = 0;
        while (opcion != 3) {
            System.out.println("\n|===========================================================|");
            System.out.println("|                 Bienvenido a Aroma y Cafe                 |");
            System.out.println("|                  \"Un cafe, mil momentos\"                  |");
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
        System.out.println("|                           Menu                            |");
        System.out.println("|                       Aroma y Cafe                        |");
        System.out.println("|                 \"Un cafe, mil momentos\"                   |");
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
        ArrayList<Integer> productosSeleccionados = new ArrayList<>();

        while (true) {
            dbConnection.listProductos(opcionCat); 
            System.out.print("Selecciona una opción: ");
            int opcionProd = scanner.nextInt();
            dbConnection.mostrarProductoPorId(opcionProd); 

            System.out.println("\n¿Desea agregar este producto? SI/NO");
            System.out.print("Selecciona: ");
            String opcionAdd = scanner.next();

            if (opcionAdd.equalsIgnoreCase("si")) {
                productosSeleccionados.add(opcionProd);
                System.out.println(productosSeleccionados);

                while (true) {
                    System.out.println("\n¿Desea agregar otro producto? SI/NO");
                    System.out.print("Selecciona: ");
                    String opcionAdd2 = scanner.next();

                    if (opcionAdd2.equalsIgnoreCase("si")) {
                        break;
                    } else if (opcionAdd2.equalsIgnoreCase("no")) {
                        //mostrar resumen de orden
                        for (int idProducto : productosSeleccionados) {
                            // Aqui iria la logica para agregar los productos como detalle de pedido
                            System.out.println("Producto con ID " + idProducto + " insertado en la base de datos.");
                        }
                        System.out.println("Todos los productos han sido agregados exitosamente.");
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
}