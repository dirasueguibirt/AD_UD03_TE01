package ejercicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ejercicio3 {
	public static void muestraErrorSQL(SQLException e) {
		System.err.println("SQL ERROR mensaje: " + e.getMessage());
		System.err.println("SQL estado: " + e.getSQLState());
		System.err.println("SQL codigo especifico: " + e.getErrorCode());
	}
	
	public static void main(String[] args) {
		
		
		//creamos los parametros de conexion
		String basedatos= "dbeventos";
		String host = "localhost";
		String port = "3306";
		String urlConnection = "jdbc:mysql://" + host + ":" + port + "/" + basedatos;
		String user = "dbeventos";
		String pwd = "dbeventos";
		
		//inicializamos las variables necesarias
		Connection c = null;
		Statement s = null;
		ResultSet rs = null;
		
		try {
			//nos conectamos a la base de datos.
			c = DriverManager.getConnection(urlConnection, user, pwd);
			//System.out.println("Conexion realizada.");
			s= c.createStatement();
			Scanner teclado = new Scanner(System.in);
			System.out.println("Introduce el DNI del asistente:");
			String cambioDNI = teclado.nextLine();
			// Validar el formato del DNI
            if (!validarDNI(cambioDNI)) {
                System.out.println("Error: El DNI debe tener el formato correcto (8 números seguidos de una letra).");
                return; // Salir si el DNI no es válido
            }
			String nombre = null;
			rs = s.executeQuery("SELECT nombre from asistentes where dni ='" + cambioDNI + "';");
			//si existe el DNI, seguimos con el proceso, si no, insertamos en la tabla de asistentes, pidiendo el DNI.
			if (rs.next()) {
				nombre = rs.getString("nombre");
			} else {
				System.out.println("No se encontró un asistente con el DNI proporcionado.");
				System.out.println("Introduce el nombre para realizar la reserva: ");
				nombre = teclado.nextLine();
				s.executeUpdate("INSERT INTO asistentes (dni,nombre) VALUES ('" + cambioDNI + "', '" + nombre+");");
			}
			System.out.println("Estás realizando la reserva para: " + rs.getString("nombre"));
			//seguimos con el programa. Listando los eventos disponibles y su capacidad.
			System.out.println("Lista de eventos:");
			
			rs = s.executeQuery("SELECT e.id_evento, e.nombre_evento, u.capacidad-count(a.dni) from eventos e "
					+ "left join ubicaciones u on e.id_ubicacion=u.id_ubicacion "
					+ "left join asistentes_eventos a on a.id_evento=e.id_evento "
					+ "group by id_evento;");
			int idevento = 0;
			while (rs.next()) {
				System.out.println(rs.getString(1) + ". " + rs.getString(2) + " - Espacios disponibles: " + rs.getString(3));
			}
			//damos opcion a elegir el evento.
			System.out.println("Elige el número del evento al que quiere asistir: ");
			idevento = teclado.nextInt();
			
			//verificamos que quedan plazas disponibles.
			rs = s.executeQuery("SELECT nombre_evento from eventos where id_evento =" + idevento + ";");
			while (rs.next()) {
				System.out.println("Evento: " + rs.getString(1));
				rs =  s.executeQuery("SELECT u.capacidad-count(a.dni) from eventos e "
						+ " left join ubicaciones u on e.id_ubicacion=u.id_ubicacion "
						+ "left join asistentes_eventos a on a.id_evento=e.id_evento "
						+ "where e.id_evento =" + idevento
						+ " group by e.id_evento=" + idevento + ";");
				if(rs.next() ) {
					int capacidad = rs.getInt(1);
					//Si no hay capacidad disponible, mostramos error, si no, inscribimos.
					if (capacidad < 1) {
						System.out.println("No se puede inscribir en el evento por falta de capacidad.");
					} else {
						s.executeUpdate("INSERT INTO asistentes_eventos (dni,id_evento) VALUES ('" + cambioDNI + "', '" + idevento+");");
						System.out.println(nombre + " ha sido registrado para el evento seleccionado.");
					}
					
				}
				
				
			}
			
			teclado.close();
			
		} catch (SQLException e) {
			muestraErrorSQL(e);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		finally {
		        try {
		        	
		            if(rs != null) rs.close();
		            if(s != null) s.close();
		            if(c != null) c.close();
		            //System.out.println("Conexión cerrada.");
		        } catch (SQLException ex) {
		            System.err.println("Error al cerrar la conexión:");
		            ex.printStackTrace(System.err);
		        }
		    }
		}



	//Método para validar el DNI (8 números seguidos de una letra)
	public static boolean validarDNI(String dni) {
	    // Expresión regular para el DNI
	    String regex = "^[0-9]{8}[A-Za-z]$";
	    Pattern pattern = Pattern.compile(regex);
	    return pattern.matcher(dni).matches();
	}
}