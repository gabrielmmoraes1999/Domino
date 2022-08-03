package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import view.TelaErro;

/**
 *
 * @author Gabriel Moraes
 */
public class Firebird {
    public static Connection conn;
    
    public void connect() throws Exception {
        //String arquivo = "C:\\Domino\\Dados\\DOMINO.FDB";
        String arquivo = System.getProperty("user.dir")+"\\Dados\\DOMINO.FDB";
        String url = "jdbc:firebirdsql:localhost/2550:"+arquivo+"?encoding=WIN1252"; //"?encoding=UTF8"
        String usuario = "SYSDBA";
        String senha = "masterkey";
        Class.forName("org.firebirdsql.jdbc.FBDriver").newInstance();
        conn = DriverManager.getConnection(url, usuario, senha);
        conn.setAutoCommit(false);
    }
    
    public void disconnect() {
        try {
            if(!conn.isClosed()){
                conn.close();
            }
        } catch (SQLException ex) {
            new TelaErro(9, ex.getStackTrace()).setVisible(true);
        }
    }
}
