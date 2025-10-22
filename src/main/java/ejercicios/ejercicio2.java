package ejercicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ejercicio2 {
	public static void muestraErrorSQL(SQLException e) {
		System.err.println("SQL ERROR mensaje: " + e.getMessage());
		System.err.println("SQL estado: " + e.getSQLState());
		System.err.println("SQL codigo especifico: " + e.getErrorCode());
	}
	
	public static void main(String[] args) {
		
		
		
		String basedatos= "dbeventos";
		String host = "localhost";
		String port = "3306";
		String urlConnection = "jdbc:mysql://" + host + ":" + port + "/" + basedatos;
		String user = "dbeventos";
		String pwd = "dbeventos";
		
		Connection c = null;
		Statement s = null;
		ResultSet rs = null;
		
		try {
			c = DriverManager.getConnection(urlConnection, user, pwd);
			//System.out.println("Conexion realizada.");
			s= c.createStatement();
			Scanner teclado = new Scanner(System.in);
			System.out.println("Introduce el DNI del asistente:");
			String cambioDNI = teclado.nextLine();
			rs = s.executeQuery("SELECT nombre from asistentes where dni ='" + cambioDNI + "';");
			
			//int i = 1;
			//String formatoEncabezado = "%-30s | %-11s | %-33s | %-30s\n";
			//System.out.printf(formatoEncabezado, "Evento", "Asistentes", "Ubicación", "Dirección");
			//System.out.println("Evento" + "\t\t\t\t" + "| Asistentes"+ "\t\t" + "| Ubicación" + "\t\t" + "| Dirección");
			//System.out.println("------------------------------------------------------------------------------------------------");
			while (rs.next()) {
				System.out.println("Nombre actual: : " + rs.getString("nombre"));				
			}
			System.out.println("Introduce el nuevo nombre (deja en blanco para no modificar):");
			String nombreNuevo = teclado.nextLine();
			if(nombreNuevo.length() == 0) {
				System.out.println("No se introduce nombre para efectuar el cambio.");
			} else {
				s.executeUpdate("UPDATE asistentes set nombre = '" + nombreNuevo + "' where DNI = '" + cambioDNI + "';");
				System.out.println("Nombre actualizado con éxito");

			}
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

}