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
public class RubricaUtilizadaDAO {
    public ArrayList<RubricaUtilizada> retornarRubricaUtilizadaPorConversao(int idConversao) {
        ArrayList<RubricaUtilizada> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT COD_EMPRESA, COD_RUBRICA FROM LANCAMENTO WHERE ID_CONVERSAO = "+idConversao+" ORDER BY COD_EMPRESA, COD_RUBRICA";
        try {
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            
            while (rs.next()) {
                RubricaUtilizada ru = new RubricaUtilizada();
                ru.setCodEmpresa(rs.getInt("COD_EMPRESA"));
                ru.setCodRubrica(rs.getString("COD_RUBRICA"));
                lista.add(ru);
            }
            
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return lista;
    }
}
