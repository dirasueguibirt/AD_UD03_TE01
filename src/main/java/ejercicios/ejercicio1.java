package ejercicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ejercicio1 {
	public static void muestraErrorSQL(SQLException e) {
		System.err.println("SQL ERROR mensaje: " + e.getMessage());
		System.err.println("SQL estado: " + e.getSQLState());
		System.err.println("SQL codigo especifico: " + e.getErrorCode());
	}
	
	public static void main(String[] args) {
		
		String basedatos= "dbeventos";
		String host = "localhost";
		String port = "3306";
		//String parAdic
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
			rs = s.executeQuery("SELECT nombre_evento, count(distinct dni) as asistentes, nombre, direccion FROM eventos e "
					+ "left join asistentes_eventos a on a.id_evento = e.id_evento "
					+ "left join ubicaciones u on u.id_ubicacion = e.id_ubicacion group by nombre_evento, e.id_ubicacion order by nombre_evento desc;");
			
			int i = 1;
			String formatoEncabezado = "%-30s | %-11s | %-33s | %-30s\n";
			System.out.printf(formatoEncabezado, "Evento", "Asistentes", "Ubicación", "Dirección");
			//System.out.println("Evento" + "\t\t\t\t" + "| Asistentes"+ "\t\t" + "| Ubicación" + "\t\t" + "| Dirección");
			System.out.println("------------------------------------------------------------------------------------------------");
			while (rs.next()) {
				String formatoresultado = "%-30s | %-11s | %-33s | %-30s\n";
				System.out.printf(formatoresultado, rs.getString("nombre_evento"),rs.getString("asistentes"),rs.getString("nombre"),rs.getString("direccion"));
				//System.out.println("APELLIDOS: " + rs.getString("APELLIDOS"));
				//System.out.println("CP: " + rs.getString("CP"));
				
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