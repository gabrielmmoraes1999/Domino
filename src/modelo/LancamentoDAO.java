package modelo;

import database.Firebird;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import util.Config;
import view.TelaErro;

/**
 *
 * @author Gabriel Moraes
 */
public class LancamentoDAO {
    public void inserir(ArrayList<Lancamento> listaLancamento, int idConversao) {
        try {
            String sql = "INSERT INTO LANCAMENTO (ID_CONVERSAO, COD_EMPRESA, COD_EMPREGADO, COMPETENCIA, COD_RUBRICA, REFERENCIA, VALOR) VALUES(?,?,?,?,?,?,?)";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            
            for(Lancamento l : listaLancamento) {
                pstm.setInt(1, idConversao);
                pstm.setInt(2, l.getCodEmpresa());
                pstm.setString(3, l.getCodEmpregado());
                pstm.setString(4, l.getCompetencia());
                pstm.setString(5, l.getCodRubrica());
                pstm.setString(6, l.getReferencia());
                pstm.setString(7, l.getValor());
                pstm.execute();
            }
            
            Firebird.conn.commit();
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }
    
    public ArrayList<Lancamento> carregarPorConversao(int idConversao) {
        ArrayList<Lancamento> lancamentos = new ArrayList<>();
        try {
            String sql = "SELECT * FROM LANCAMENTO WHERE ID_CONVERSAO = ? ORDER BY ID ASC";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            ResultSet rs = pstm.executeQuery();
            
            while (rs.next()) {
                Lancamento l = new Lancamento();
                l.setId(rs.getInt("ID"));
                l.setCodEmpresa(rs.getInt("COD_EMPRESA"));
                l.setCodEmpregado(rs.getString("COD_EMPREGADO"));
                l.setCompetencia(rs.getString("COMPETENCIA"));
                l.setCodRubrica(rs.getString("COD_RUBRICA"));
                l.setReferencia(rs.getString("REFERENCIA"));
                l.setValor(rs.getString("VALOR"));
                lancamentos.add(l);
            }
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return lancamentos;
    }
    
    public int retornaQuantidadeLancamentoPorConversaoPorEmpresa(int idConversao, int codEmpresa) {
        int total = 0;
        try {
            String sql = "SELECT COUNT(*) FROM LANCAMENTO WHERE ID_CONVERSAO = ? AND COD_EMPRESA = ?";
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
            String sql = "DELETE FROM LANCAMENTO WHERE ID_CONVERSAO = ?";
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
    
    public ArrayList<LayoutDominio> dePara(boolean aplicarDeParaEmpregado) {
        ArrayList<LayoutDominio> listLD = new ArrayList<>();
        
        RubricaDAO rDAO = new RubricaDAO();
        RubricaUtilizadaDAO ruDAO = new RubricaUtilizadaDAO();
        EmpregadoDAO eDAO = new EmpregadoDAO();
        LancamentoDAO lDAO = new LancamentoDAO();
        
        int dprEmpregado = 0;
        
        if(aplicarDeParaEmpregado) {
            dprEmpregado = 1;
        }
        
        ArrayList<Empregado> listEmpregado = eDAO.carregarPorConversao(Config.conversao.getId());
        ArrayList<DeParaRubrica> listDpr = rDAO.retornaDeParaRubrica(ruDAO.retornarRubricaUtilizadaPorConversao(Config.conversao.getId()));
        ArrayList<RubricaConfiguracao> listRC = new RubricaConfiguracaoDAO().carregarPorConversao(Config.conversao.getId());
        
        for(Lancamento l : lDAO.carregarPorConversao(Config.conversao.getId())) {
            for(DeParaRubrica drp : listDpr) {
                if(l.getCodRubrica().equals(drp.getOrigem()) && l.getCodEmpresa() == drp.getCodEmpresa()) {
                    LayoutDominio ld = new LayoutDominio();
                    switch(dprEmpregado) {
                        case 0:
                            ld.setCodEmpresa(l.getCodEmpresa());
                            ld.setCodEmpregado(Integer.valueOf(l.getCodEmpregado()));
                            ld.setCompetencia(l.getCompetencia());
                            ld.setCodRubrica(drp.getDestino());
                            ld.setValor(l.getValor());
                            for(RubricaConfiguracao rc : listRC) {
                                if(rc.getCodEmpresa() == l.getCodEmpresa() && rc.getCodigo().equals(drp.getDestino()) && rc.isHoras()) {
                                    ld.setValor(l.getReferencia());
                                    break;
                                } else if(rc.getCodEmpresa() == l.getCodEmpresa() && rc.getCodigo().equals(drp.getDestino()) && !rc.isHoras()) {
                                    ld.setValor(l.getValor());
                                    break;
                                }
                            }
                            listLD.add(ld);
                            break;
                        case 1:
                            for(Empregado e : listEmpregado) {
                                if(l.getCodEmpresa() == e.getCodEmpresa() && l.getCodEmpregado().equals(e.getCodEsocial())) {
                                    ld.setCodEmpresa(l.getCodEmpresa());
                                    ld.setCodEmpregado(e.getCodigo());
                                    ld.setCompetencia(l.getCompetencia());
                                    ld.setCodRubrica(drp.getDestino());
                                    ld.setValor(l.getValor());
                                    for(RubricaConfiguracao rc : listRC) {
                                        if(rc.getCodEmpresa() == l.getCodEmpresa() && rc.getCodigo().equals(drp.getDestino()) && rc.isHoras()) {
                                            ld.setValor(l.getReferencia());
                                            break;
                                        } else if(rc.getCodEmpresa() == l.getCodEmpresa() && rc.getCodigo().equals(drp.getDestino()) && !rc.isHoras()) {
                                            ld.setValor(l.getValor());
                                            break;
                                        }
                                    }
                                    listLD.add(ld);
                                    break;
                                }
                            }
                            break;
                    }
                    break;
                }
            }
        }
        
        return listLD;
    }
}
