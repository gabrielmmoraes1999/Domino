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
public class RubricaConfiguracaoDAO {
    public void inserir(ArrayList<RubricaConfiguracao> rcList, int idConversao) {
        try {
            String sql = "INSERT INTO rubrica_configuracao (id_conversao, codigo, cod_empresa, descricao, inicio_data, situacao, situacao_data, tipo, aviso_previo, salario_ferias, licenca_premio,"
                    + "ficha_financeira, dirf, rais, aviso_previo_medias, salario_medias, ferias_medias, licenca_premio_medias, saldo_salario_medias, horas) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            
            for(RubricaConfiguracao rc : rcList) {
                pstm.setInt(1, idConversao);
                pstm.setString(2, rc.getCodigo());
                pstm.setInt(3, rc.getCodEmpresa());
                pstm.setString(4, rc.getDescricao());
                pstm.setDate(5, rc.getInicioData());
                pstm.setBoolean(6, rc.isSituacao());
                pstm.setDate(7, rc.getSituacaoData());
                pstm.setInt(8, rc.getTipo());
                pstm.setBoolean(9, rc.isAvisoPrevio());
                pstm.setBoolean(10, rc.isSalarioFerias());
                pstm.setBoolean(11, rc.isLicencaPremio());
                pstm.setBoolean(12, true);
                pstm.setBoolean(13, true);
                pstm.setBoolean(14, true);
                pstm.setBoolean(15, rc.isAvisoPrevioMedias());
                pstm.setBoolean(16, rc.isSalarioMedias());
                pstm.setBoolean(17, rc.isFeriasMedias());
                pstm.setBoolean(18, rc.isLicencaPremioMedias());
                pstm.setBoolean(19, rc.isSaldoSalarioMedias());
                pstm.setBoolean(20, rc.isHoras());
                pstm.execute();
            }
            
            Firebird.conn.commit();
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }
    
    public ArrayList<RubricaConfiguracao> carregarPorConversao(int idConversao) {
        ArrayList<RubricaConfiguracao> rcList = new ArrayList<>();
        
        try {
            String sql = "SELECT * FROM rubrica_configuracao WHERE id_conversao = ? ORDER BY id ASC";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            ResultSet rs = pstm.executeQuery();
            
            while (rs.next()) {
                RubricaConfiguracao rc = new RubricaConfiguracao();
                rc.setId(rs.getInt("id"));
                rc.setCodigo(rs.getString("codigo"));
                rc.setCodEmpresa(rs.getInt("cod_empresa"));
                rc.setDescricao(rs.getString("descricao"));
                rc.setInicioData(rs.getDate("inicio_data"));
                rc.setSituacao(rs.getBoolean("situacao"));
                rc.setSituacaoData(rs.getDate("situacao_data"));
                rc.setTipo(rs.getInt("tipo"));
                rc.setAvisoPrevio(rs.getBoolean("aviso_previo"));
                rc.setSalarioFerias(rs.getBoolean("salario_ferias"));
                rc.setLicencaPremio(rs.getBoolean("licenca_premio"));
                rc.setFichaFinanceira(rs.getBoolean("ficha_financeira"));
                rc.setDirf(rs.getBoolean("dirf"));
                rc.setRais(rs.getBoolean("rais"));
                rc.setAvisoPrevioMedias(rs.getBoolean("aviso_previo_medias"));
                rc.setSalarioMedias(rs.getBoolean("salario_medias"));
                rc.setFeriasMedias(rs.getBoolean("ferias_medias"));
                rc.setLicencaPremioMedias(rs.getBoolean("licenca_premio_medias"));
                rc.setSaldoSalarioMedias(rs.getBoolean("saldo_salario_medias"));
                rc.setHoras(rs.getBoolean("horas"));
                rcList.add(rc);
            }
            
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return rcList;
    }
    
    public RubricaConfiguracao carregarPorID(int id) {
        RubricaConfiguracao rc = new RubricaConfiguracao();
        try {
            String sql = "SELECT * FROM rubrica_configuracao WHERE id = ?";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, id);
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                rc.setId(rs.getInt("id"));
                rc.setCodigo(rs.getString("codigo"));
                rc.setCodEmpresa(rs.getInt("cod_empresa"));
                rc.setDescricao(rs.getString("descricao"));
                rc.setInicioData(rs.getDate("inicio_data"));
                rc.setSituacao(rs.getBoolean("situacao"));
                rc.setSituacaoData(rs.getDate("situacao_data"));
                rc.setTipo(rs.getInt("tipo"));
                rc.setAvisoPrevio(rs.getBoolean("aviso_previo"));
                rc.setSalarioFerias(rs.getBoolean("salario_ferias"));
                rc.setLicencaPremio(rs.getBoolean("licenca_premio"));
                rc.setFichaFinanceira(rs.getBoolean("ficha_financeira"));
                rc.setDirf(rs.getBoolean("dirf"));
                rc.setRais(rs.getBoolean("rais"));
                rc.setAvisoPrevioMedias(rs.getBoolean("aviso_previo_medias"));
                rc.setSalarioMedias(rs.getBoolean("salario_medias"));
                rc.setFeriasMedias(rs.getBoolean("ferias_medias"));
                rc.setLicencaPremioMedias(rs.getBoolean("licenca_premio_medias"));
                rc.setSaldoSalarioMedias(rs.getBoolean("saldo_salario_medias"));
                rc.setHoras(rs.getBoolean("horas"));
            }
            
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return rc;
    }
    
    public void alterar(RubricaConfiguracao rc) {
        try {
            String sql = "UPDATE rubrica_configuracao SET situacao = ?, situacao_data = ?, tipo = ?, aviso_previo = ?, salario_ferias = ?, licenca_premio = ?, ficha_financeira = ?,"
                    + "dirf = ?, rais = ?, aviso_previo_medias = ?, salario_medias = ?, ferias_medias = ?, licenca_premio_medias = ?, saldo_salario_medias = ? WHERE id = ?";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setBoolean(1, rc.isSituacao());
            pstm.setDate(2, rc.getSituacaoData());
            pstm.setInt(3, rc.getTipo());
            pstm.setBoolean(4, rc.isAvisoPrevio());
            pstm.setBoolean(5, rc.isSalarioFerias());
            pstm.setBoolean(6, rc.isLicencaPremio());
            pstm.setBoolean(7, rc.isFichaFinanceira());
            pstm.setBoolean(8, rc.isDirf());
            pstm.setBoolean(9, rc.isRais());
            pstm.setBoolean(10, rc.isAvisoPrevioMedias());
            pstm.setBoolean(11, rc.isSalarioMedias());
            pstm.setBoolean(12, rc.isFeriasMedias());
            pstm.setBoolean(13, rc.isLicencaPremioMedias());
            pstm.setBoolean(14, rc.isSalarioMedias());
            pstm.setInt(15, rc.getId());
            pstm.execute();
            Firebird.conn.commit();
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }
    
    public void replicar(RubricaConfiguracao rc) {
        try {
            String sql = "UPDATE rubrica_configuracao SET situacao = ?, situacao_data = ?, aviso_previo = ?, salario_ferias = ?, licenca_premio = ?, ficha_financeira = ?, "
                    + "dirf = ?, rais = ?, aviso_previo_medias = ?, salario_medias = ?, ferias_medias = ?, licenca_premio_medias = ?, saldo_salario_medias = ? "
                    + "WHERE id_conversao = ? AND horas = ?";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setBoolean(1, rc.isSituacao());
            pstm.setDate(2, rc.getSituacaoData());
            pstm.setBoolean(3, rc.isAvisoPrevio());
            pstm.setBoolean(4, rc.isSalarioFerias());
            pstm.setBoolean(5, rc.isLicencaPremio());
            pstm.setBoolean(6, rc.isFichaFinanceira());
            pstm.setBoolean(7, rc.isDirf());
            pstm.setBoolean(8, rc.isRais());
            pstm.setBoolean(9, rc.isAvisoPrevioMedias());
            pstm.setBoolean(10, rc.isSalarioMedias());
            pstm.setBoolean(11, rc.isFeriasMedias());
            pstm.setBoolean(12, rc.isLicencaPremioMedias());
            pstm.setBoolean(13, rc.isSalarioMedias());
            pstm.setInt(14, rc.getConversao().getId());
            pstm.setBoolean(15, rc.isHoras());
            pstm.execute();
            Firebird.conn.commit();
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }
    
    public int retornaIDPrimeiraRubricaPorConversao(int idConversao) {
        int generatedKey = 0;
        try {
            String sql = "SELECT MIN(ID) FROM RUBRICA_CONFIGURACAO WHERE ID_CONVERSAO = ?";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            ResultSet rs = pstm.executeQuery();
            rs.next();
            generatedKey = rs.getInt(1);
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return generatedKey;
    }
    
    public int retornaIDUltimaRubricaPorConversao(int idConversao) {
        int generatedKey = 0;
        try {
            String sql = "SELECT MAX(ID) FROM RUBRICA_CONFIGURACAO WHERE ID_CONVERSAO = ?";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            ResultSet rs = pstm.executeQuery();
            rs.next();
            generatedKey = rs.getInt(1);
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return generatedKey;
    }
    
    public int retornaQuantidadeRubricaPorConveraoPorEmpresa(int idConversao, int codEmpresa) {
        int total = 0;
        
        try {
            String sql = "SELECT COUNT(*) FROM RUBRICA_CONFIGURACAO WHERE ID_CONVERSAO = ? AND COD_EMPRESA = ?";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            pstm.setInt(2, codEmpresa);
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                total = rs.getInt("COUNT");
            }
            
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return total;
    }
    
    public void deletar(int idConversao) {
        try {
            String sql = "DELETE FROM rubrica_configuracao WHERE id_conversao = ?";
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
