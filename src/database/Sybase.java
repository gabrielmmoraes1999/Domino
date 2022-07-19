package database;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Gabriel Moraes
 */
public class Sybase {
    public static String host;
    public static String porta;
    public static String nome;
    public static String usuario;
    public static String senha;
    
    public static Connection conn;
    
    public void connect() throws Exception{
        String url = "jdbc:sybase:Tds:"+host+":"+porta+"?ServiceName="+nome;
        Class.forName("com.sybase.jdbc3.jdbc.SybDriver").newInstance();
        conn = DriverManager.getConnection(url, usuario, senha);
    }
    
    public void disconnect() throws Exception{
        if(!conn.isClosed()){
            conn.close();
            host = null;
            porta = null;
            nome = null;
            usuario = null;
            senha = null;
        }
    }
}
