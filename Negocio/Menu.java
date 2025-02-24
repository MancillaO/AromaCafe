package Negocio;

import Datos.PostgreSQLConnection;
import Datos.MySQLConnection;
import java.sql.Connection;
import java.util.Scanner;

public class Menu {
    private Scanner scanner = new Scanner(System.in);

    public void iniciar() {
        int opcion = 0;
        while (opcion != 3) {
            System.out.println("|=========================================================|");
            System.out.println("|                 Bienvenido a Aroma y Café               |");
            System.out.println("|                  \"Un cafe, mil momentos\"                |");
            System.out.println("|=========================================================|");
            System.out.println("|Seleccione la base de datos a la que desea conectarse:   |");
            System.out.println("|1. PostgreSQL                                            |");
            System.out.println("|2. MySQL                                                 |");
            System.out.println("|3. Salir                                                 |");
            System.out.println("|=========================================================|");
            System.out.print("Opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    conectarPostgreSQL();
                    break;
                case 2:
                    conectarMySQL();
                    break;
                case 3:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    private void conectarPostgreSQL() {
        PostgreSQLConnection postgres = new PostgreSQLConnection();

        try (Connection conn = postgres.getConnection()) {
            if (conn != null) {
                System.out.println("Conexión exitosa a PostgreSQL.");
                System.out.println("|=========================================================|");
                System.out.println("|                          Menu                           |");
                System.out.println("|                      Aroma y Café                       |");
                System.out.println("|                 \"Un cafe, mil momentos\"               |");
                System.out.println("|=========================================================|");
                System.out.println("|¿Alguna preferencia de tu eleccion en este momento?      |");
                System.out.println("|1. Bebidas calientes                                     |");
                System.out.println("|2. Bebidas Frias                                         |");
                System.out.println("|3. Platillos Salados                                     |");
                System.out.println("|4. Platillos Dulces                                      |");
                System.out.println("|5. Platillos Veganos                                     |");
                System.out.println("|=========================================================|");
                System.out.print("Selecciona: ");
                int opcion = 0;
                opcion = scanner.nextInt();
                postgres.listCategorias(opcion);
                

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