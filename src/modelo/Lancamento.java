package modelo;

import java.util.ArrayList;

/**
 *
 * @author Gabriel Moraes
 */
public class Lancamento {
    private int id;
    private Conversao conversao;
    private int codEmpresa;
    private String codEmpregado;
    private String competencia;
    private String codRubrica;
    private String referencia;
    private String valor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Conversao getConversao() {
        return conversao;
    }

    public void setConversao(Conversao conversao) {
        this.conversao = conversao;
    }

    public int getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodEmpresa(int codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    public String getCodEmpregado() {
        return codEmpregado;
    }

    public void setCodEmpregado(String codEmpregado) {
        this.codEmpregado = codEmpregado;
    }

    public String getCompetencia() {
        return competencia;
    }

    public void setCompetencia(String competencia) {
        this.competencia = competencia;
    }

    public String getCodRubrica() {
        return codRubrica;
    }

    public void setCodRubrica(String codRubrica) {
        this.codRubrica = codRubrica;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
    
    public ArrayList<LayoutDominio> preparaLancamentoESocial(int idConversao) {
        ArrayList<LayoutDominio> listaLayDominio = new ArrayList<>();
        ArrayList<DeParaRubrica> listaDPR = new ArrayList<>();
        
        RubricaDAO rDAO = new RubricaDAO();
        LancamentoDAO lDAO = new LancamentoDAO();
        
        //Rubricas Usadas
        RubricaUtilizadaDAO ruDAO = new RubricaUtilizadaDAO();
        ArrayList<RubricaUtilizada> listaRubricaUsa = ruDAO.retornarRubricaUtilizadaPorConversao(idConversao);
        
        for(RubricaUtilizada ru : listaRubricaUsa) {
            //Realizar DePara
            for(DeParaRubrica dpr : rDAO.retornaDeParaRubrica(listaRubricaUsa)) {
                if(ru.getCodRubrica().equals(dpr.getOrigem()) && ru.getCodEmpresa() == dpr.getCodEmpresa()) {
                    listaDPR.add(dpr);
                }
            }
        }
        
        //Buscar configuracao das Rubricas
        RubricaConfiguracaoDAO rcDAO = new RubricaConfiguracaoDAO();
        ArrayList<RubricaConfiguracao> listaRC = rcDAO.carregarPorConversao(idConversao);
        
        for(Lancamento l : lDAO.carregarPorConversao(idConversao)) {
            for(DeParaRubrica dpr : listaDPR) {
                if(l.getCodEmpresa() == dpr.getCodEmpresa() && l.getCodRubrica().equals(dpr.getOrigem())) {
                    if(l.getCodEmpregado().length() < 11) {
                        LayoutDominio ld = new LayoutDominio();
                        ld.setCodEmpregado(Integer.valueOf(l.getCodEmpregado()));
                        ld.setCompetencia(l.getCompetencia());
                        ld.setCodRubrica(dpr.getDestino());
                        
                        for(RubricaConfiguracao rc : listaRC) {
                            if(rc.getCodEmpresa() == l.getCodEmpresa() && dpr.getDestino().equals(rc.getCodigo())) {
                                if(rc.isHoras()) {
                                    ld.setValor(l.getReferencia());
                                    break;
                                } else {
                                    ld.setValor(l.getValor());
                                    break;
                                }
                            }
                        }
                        
                        ld.setCodEmpresa(l.getCodEmpresa());
                        
                        //Remover Lancamentos que não estão na Lista das Rubricas
                        if(ld.getValor() != null) {
                            listaLayDominio.add(ld);
                        }
                    }
                    break;
                }
            }
        }
        
        return listaLayDominio;
    }
}
