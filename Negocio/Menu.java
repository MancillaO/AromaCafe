package Negocio;

import Datos.PostgreSQLConnection;
import Datos.MySQLConnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

public class Menu {
    private Scanner scanner = new Scanner(System.in);
    String cone;

    public void iniciar() {
        int opcion = 0;
        while (opcion != 3) {
            System.out.println("|===========================================================|");
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
                    mostraMenu();
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

    private void mostraMenu() {
        PostgreSQLConnection postgres = new PostgreSQLConnection();

        try (Connection conn = postgres.getConnection()) {
            if (conn != null) {
                System.out.println("\nConexion exitosa a PostgreSQL.");
                System.out.println("|===========================================================|");
                System.out.println("|                           Menu                            |");
                System.out.println("|                       Aroma y Cafe                        |");
                System.out.println("|                 \"Un cafe, mil momentos\"                   |");
                System.out.println("|===========================================================|");
                System.out.println("|                                                           |");
                System.out.println("| ¿Alguna preferencia de tu eleccion en este momento?       |");
                System.out.println("|                                                           |");
                postgres.listCategorias();
                System.out.println("|                                                           |");
                System.out.println("|===========================================================|");
                System.out.print("Selecciona una opcion: ");
                int opcionCat = scanner.nextInt();

                if (opcionCat == 6) {
                    System.out.println("Gracias por visitarnos. ¡Hasta pronto!");
                }
                if (opcionCat >= 1 && opcionCat <= 5) {
                    while (true) {
                        postgres.listProductos(opcionCat);
                        System.out.print("Selecciona una opcion: ");
                        int opcionProd = scanner.nextInt();
                        postgres.mostrarProductoPorId(opcionProd);
                        System.out.println("*************************************************************");
                        System.out.println("\n¿Desea agregar? SI/NO");
                        System.out.print("Selecciona: ");
                        String opcionAdd = scanner.next();
                        ArrayList<Integer> productosSeleccionados = new ArrayList<>();
                        if (opcionAdd.equals("si")) {
                            productosSeleccionados.add(opcionProd);
                            System.out.println("\n¿Desea agregar otro producto? SI/NO");
                            String opcionAdd2 = scanner.next();
                            if (opcionAdd2.equals("no")) {
                                break;
                            }
                        }
                    }

                } else {
                    System.out.println("Opción inválida, intenta de nuevo.");
                }

            }
        } catch (Exception e) {
            System.out.println("Error al conectar a PostgreSQL: " + e.getMessage());
        }
    }

    private void conectarMySQL() {
        MySQLConnection mysql = new MySQLConnection();

        try (Connection conn = mysql.getConnection()) {
            if (conn != null) {
                System.out.println("Conexión exitosa a MySQL.");

            }
        } catch (Exception e) {
            System.out.println("Error al conectar a MySQL: " + e.getMessage());
        }
    }

}