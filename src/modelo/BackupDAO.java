package modelo;

import database.Firebird;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import view.TelaErro;

/**
 *
 * @author Gabriel Moraes
 */
public class BackupDAO {
    public void inserir(Backup b) {
        try {
            String sql = "INSERT INTO BACKUP (DATA) VALUES(?)";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setTimestamp(1, b.getData());
            pstm.execute();
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }
    
    public ArrayList<Backup> lerTodos() {
        ArrayList<Backup> backups = new ArrayList<>();
        try {
            String sql = "SELECT * FROM BACKUP ORDER BY ID DESC";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            
            while (rs.next()) {
                Backup b = new Backup();
                b.setId(rs.getInt("ID"));
                b.setData(rs.getTimestamp("DATA"));
                backups.add(b);
            }
            
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return backups;
    }
}
