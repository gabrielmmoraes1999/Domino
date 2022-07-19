package modelo;

import database.Firebird;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import view.TelaErro;

/**
 *
 * @author Gabriel Moraes
 */
public class VersaoBancoDAO {
    public int retornaVersaoAtualBanco() {
        int versao = 0;
        try {
            String sql = "SELECT * FROM VERSAO_BANCO ORDER BY ID DESC";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                versao = rs.getInt("DB_VERSAO");
            }
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return versao;
    }
}
