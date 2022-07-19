package modelo;

import database.Firebird;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import view.TelaErro;

/**
 *
 * @author Gabriel Moraes
 */
public class NovidadeDAO {
    public Novidade carregarUltimaNovidade() {
        Novidade n = new Novidade();
        try {
            String sql = "SELECT * FROM NOVIDADE WHERE ID = (SELECT MAX(ID) FROM NOVIDADE)";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                n.setId(rs.getInt("ID"));
                n.setTextoHtml(rs.getString("TEXTO_HTML"));
                n.setSysVersao(rs.getInt("SIS_VERSAO"));
                n.setVisualizada(rs.getBoolean("VISUALIZADA"));
            }
            
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return n;
    }
    
    public void visualizada(int id) {
        try {
            String sql = "UPDATE NOVIDADE SET VISUALIZADA = ? WHERE ID = ?";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            
            pstm.setBoolean(1, true);
            pstm.setInt(2, id);
            pstm.execute();
            
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }
}
