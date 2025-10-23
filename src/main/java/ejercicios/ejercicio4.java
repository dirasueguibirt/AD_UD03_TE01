package ejercicios;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ejercicio4 {
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
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			//nos conectamos a la base de datos.
			c = DriverManager.getConnection(urlConnection, user, pwd);
			//System.out.println("Conexion realizada.");
			s= c.createStatement();
			Scanner teclado = new Scanner(System.in);
			
			//Listamos los eventos de la bbdd
			System.out.println("Lista de eventos:");
			
			rs = s.executeQuery("SELECT e.id_evento, e.nombre_evento from eventos e "
					+ "left join asistentes_eventos a on a.id_evento=e.id_evento "
					+ "group by id_evento;");
			int idevento = 0;
			while (rs.next()) {
				System.out.println(rs.getString(1) + ". " + rs.getString(2));
			}
			//damos opcion a elegir el evento.
			System.out.println("Introduce el ID del evento para consultar la cantidad de asistentes: ");
			idevento = teclado.nextInt();
			//lamamos a la función para obtener el numero de asistentes.
			cs = c.prepareCall("{call obtener_numero_asistentes(?,?)}");
			cs.setInt(1, idevento);
			cs.registerOutParameter(2, java.sql.Types.INTEGER);
			
			cs.execute();
			
			int asistentes = cs.getInt(2);
			//mostramos por pantalla el mensaje con el número de asistentes.
			System.out.println("El número de asistentes para el evento seleccionado es: " + asistentes);

			teclado.close();
			
		} catch (SQLException e) {
			muestraErrorSQL(e);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		finally {
		        try {
		        	
		        	if(cs != null) cs.close();
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