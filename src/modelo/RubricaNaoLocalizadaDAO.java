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
public class RubricaNaoLocalizadaDAO {
    public void inserir(ArrayList<RubricaNaoLocalizada> lista) {
        try {
            String sql = "INSERT INTO RUBRICA_NAO_LOCALIZADA (ID_CONVERSAO, ID_EMPRESA, CODIGO) VALUES(?,?,?)";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            
            for(RubricaNaoLocalizada rnl : lista) {
                pstm.setInt(1, rnl.getConversao().getId());
                pstm.setInt(2, rnl.getEmpresa().getId());
                pstm.setString(3, rnl.getCodigo());
                pstm.execute();
            }
            
            Firebird.conn.commit();
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }
    
    public ArrayList<RubricaNaoLocalizada> carregarPorConversao(int idConversao) {
        ArrayList<RubricaNaoLocalizada> lista = new ArrayList<>();
        
        try {
            String sql = "SELECT * FROM RUBRICA_NAO_LOCALIZADA WHERE ID_CONVERSAO = ? ORDER BY ID ASC";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            ResultSet rs = pstm.executeQuery();
            
            EmpresaDAO eDAO = new EmpresaDAO();
            
            while (rs.next()) {
                RubricaNaoLocalizada rnl = new RubricaNaoLocalizada();
                rnl.setId(rs.getInt("ID"));
                rnl.setCodigo(rs.getString("CODIGO"));
                rnl.setEmpresa(eDAO.carregarPorId(rs.getInt("ID_EMPRESA")));
                lista.add(rnl);
            }
            
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return lista;
    }
    
    public void deletar(int idConversao) {
        try {
            String sql = "DELETE FROM RUBRICA_NAO_LOCALIZADA WHERE ID_CONVERSAO = ?";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            pstm.execute();
            Firebird.conn.commit();
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }
}
