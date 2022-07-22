package modelo;

import database.Firebird;
import database.Sybase;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import view.TelaErro;

/**
 *
 * @author Gabriel Moraes
 */
public class EmpregadoDAO {
    public void inserir(ArrayList<Empregado> empregados, int idConversao) {
        try {
            String sql = "INSERT INTO empregado (id_conversao, cod_empresa, codigo, cod_esocial, nome, cpf, vinculo) VALUES(?,?,?,?,?,?,?)";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            
            for(Empregado e : empregados) {
                pstm.setInt(1, idConversao);
                pstm.setInt(2, e.getCodEmpresa());
                pstm.setInt(3, e.getCodigo());
                pstm.setString(4, e.getCodEsocial());
                pstm.setString(5, e.getNome());
                pstm.setString(6, e.getCpf());
                pstm.setInt(7, e.getVinculo());
                pstm.execute();
            }
            
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }
    
    public ArrayList<Empregado> carregarPorConversao(int idConversao) {
        ArrayList<Empregado> empregados = new ArrayList<>();
        try {
            String sql = "SELECT * FROM empregado WHERE id_conversao = ? ORDER BY id ASC";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            ResultSet rs = pstm.executeQuery();
            
            while (rs.next()) {
                Empregado e = new Empregado();
                e.setId(rs.getInt("id"));
                e.setCodEmpresa(rs.getInt("cod_empresa"));
                e.setCodigo(rs.getInt("codigo"));
                e.setCodEsocial(rs.getString("cod_esocial"));
                e.setNome(rs.getString("nome"));
                e.setCpf(rs.getString("cpf"));
                e.setVinculo(rs.getInt("vinculo"));
                empregados.add(e);
            }
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return empregados;
    }
    
    public ArrayList<Empregado> carregarPorConversaoFiltroEstagiarios(int idConversao) {
        ArrayList<Empregado> empregados = new ArrayList<>();
        try {
            String sql = "SELECT * FROM empregado WHERE id_conversao = ? AND (vinculo is null OR vinculo <> 50) ORDER BY id ASC";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            ResultSet rs = pstm.executeQuery();
            
            while (rs.next()) {
                Empregado e = new Empregado();
                e.setId(rs.getInt("id"));
                e.setCodEmpresa(rs.getInt("cod_empresa"));
                e.setCodigo(rs.getInt("codigo"));
                e.setCodEsocial(rs.getString("cod_esocial"));
                e.setNome(rs.getString("nome"));
                e.setCpf(rs.getString("cpf"));
                e.setVinculo(rs.getInt("vinculo"));
                empregados.add(e);
            }
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return empregados;
    }
    
    public int retornaQuantidadeEmpregadoPorConversaoPorEmpresa(int idConversao, int codEmpresa) {
        int qtdEmpregado = 0;
        try {
            String sql = "SELECT COUNT(DISTINCT COD_EMPREGADO) FROM LANCAMENTO WHERE ID_CONVERSAO = ? AND COD_EMPRESA = ?";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            pstm.setInt(2, codEmpresa);
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                qtdEmpregado = rs.getInt("COUNT");
            }
            
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return qtdEmpregado;
    }
    
    public int retornaCodEmpregado(int idConversao, int codEmpresa, String cpf) {
        int codigo = 0;
        try {
            String sql = "SELECT * FROM EMPREGADO WHERE ID_CONVERSAO = ? AND COD_EMPRESA = ? AND CPF = ?";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            pstm.setInt(2, codEmpresa);
            pstm.setString(3, cpf);
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                codigo = rs.getInt("CODIGO");
            }
            
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return codigo;
    }
    
    public void alterarVarios(ArrayList<Empregado> empregados, int idConversao) {
        try {
            String sql = "UPDATE empregado SET cod_esocial=? WHERE id_conversao = ? AND cod_empresa = ? AND codigo = ?";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            
            for(Empregado e : empregados) {
                pstm.setString(1, e.getCodEsocial());
                pstm.setInt(2, idConversao);
                pstm.setInt(3, e.getCodEmpresa());
                pstm.setInt(4, e.getCodigo());
                pstm.execute();
            }
            
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }
    
    public ArrayList<Empregado> empregadosDominio(ArrayList<Empresa> emp) {
        ArrayList<Empregado> empregados = new ArrayList<>();
        try {
            Statement stment = Sybase.conn.createStatement();
            
            for(Empresa e : emp) {
                String qry = "SELECT * FROM bethadba.foempregados WHERE codi_emp = "+e.getCodigo()+" ORDER BY i_empregados";
                ResultSet rs = stment.executeQuery(qry);
                while(rs.next()) {
                    Empregado ee = new Empregado();
                    ee.setCodEmpresa(rs.getInt("codi_emp"));
                    ee.setCodigo(rs.getInt("i_empregados"));
                    String codeSocial = rs.getString("CODIGO_ESOCIAL");
                    ee.setCodEsocial(codeSocial);
                    ee.setNome(rs.getString("nome"));
                    ee.setCpf(rs.getString("cpf"));
                    ee.setVinculo(rs.getInt("vinculo"));
                    if(codeSocial != null){
                        empregados.add(ee);
                    }
                }
                
            }
        } catch (SQLException ex) {
            new TelaErro(9, ex.getStackTrace()).setVisible(true);
        }
        return empregados;
    }
    
    public void deletar(int idConversao) {
        try {
            String sql = "DELETE FROM empregado WHERE id_conversao = ?";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            pstm.execute();
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }
}
