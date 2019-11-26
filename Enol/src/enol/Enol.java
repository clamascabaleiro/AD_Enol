/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author oracle
 */
public class Enol {

    public static Connection conexion = null;
        Connection conn;

    public static Connection getConexion() throws SQLException {
        String usuario = "hr";
        String password = "hr";
        String host = "localhost";
        String puerto = "1521";
        String sid = "orcl";
        String urljdbc = "jdbc:oracle:thin:" + usuario + "/" + password + "@" + host + ":" + puerto + ":" + sid;

        conexion = DriverManager.getConnection(urljdbc);
        return conexion;
    }

    public static void closeConexion() throws SQLException {
        conexion.close();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, SQLException, IOException {
        // TODO code application logic here
        BufferedReader leer = new BufferedReader(new FileReader(new File("/home/oracle/Desktop/examenRepaso/Ejercio3/analisis.txt")));
        getConexion();
        String delimitador = ",";
        while((delimitador = leer.readLine())  != null){
            String [] fields = delimitador.split(",");
            String num = fields[0];
            String tipo = fields[4];
            int acidez = Integer.parseInt(fields[1]);
            int cantidad = Integer.parseInt(fields[5]);
            int total = cantidad * 15;
            String dni = fields[6];
            
            String nombreUva = "";
            String tratarAcidez = "";
            int numAnalisis = 0;
            
            
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery("select nomeu,acidezmin,acidezmax from uvas where tipo =  '" + tipo + "'");
            
            while(rs.next()){
                nombreUva = rs.getString("nomeu");
                int acidezmin = rs.getInt("acidezmin");
                int acidezmax = rs.getInt("acidezmax");
                
                if(acidez > acidezmax){
                    tratarAcidez = "Baixar acidez";
                }
                
                else if (acidez < acidezmin){
                    tratarAcidez = "Subir acidez";
                }
                
                else {
                    tratarAcidez = "Acidez correcta";
                }
            }
            
            rs.close();
            rs = st.executeQuery("insert into xerado values('" + num + "', '" + nombreUva + "', '" + tratarAcidez + "', '" + total + "')");
            
            Statement sta = conexion.createStatement();
            ResultSet rsa = st.executeQuery("select numerodeanalisis from clientes where dni =  '" + dni + "'");
            
            while(rsa.next()){
                numAnalisis = rsa.getInt("numerodeanalisis");
                
            }
            
            rsa.close();
            rsa = sta.executeQuery("update clientes set numerodeanalisis ='" + (numAnalisis + 1) + "' where dni = '" + dni + "'");
            
        }
    }
    
}
