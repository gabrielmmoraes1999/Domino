package outros.bancos;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import modelo.Conversao;
import modelo.DeParaRubrica;
import modelo.EmpregadoDAO;
import modelo.Empresa;
import modelo.EmpresaDAO;
import modelo.Lancamento;
import modelo.LancamentoESocial;
import modelo.Rubrica;
import modelo.RubricaConfiguracao;
import modelo.RubricaDAO;
import modelo.RubricaNaoLocalizada;
import modelo.RubricaNaoLocalizadaDAO;
import modelo.RubricaUtilizada;
import modelo.RubricaUtilizadaDAO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import view.TelaErro;

/**
 *
 * @author Gabriel Moraes
 */
public class eSocial {

    public void lerXMl(ArrayList<Empresa> listaEmpresa, String caminho) throws SAXException, ParserConfigurationException, IOException {
        //Listas
        ArrayList<Rubrica> listaRubrica = new ArrayList<>();
        ArrayList<LancamentoESocial> listaLancamento = new ArrayList<>();
        
        for (String arquivo : this.retornaArquivos(caminho)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document d = builder.parse(new File(arquivo));
            Element xml = d.getDocumentElement();

            if (xml.getElementsByTagName("evtRemun").getLength() != 0) {

                for (int i = 0; i < xml.getElementsByTagName("evtRemun").getLength(); i++) {
                    Element evtRemun = (Element) xml.getElementsByTagName("evtRemun").item(i);

                    if (!evtRemun.getAttribute("Id").equals("")) {
                        //Evento S-1200

                        //Pega periodo
                        Element ideEvento = (Element) evtRemun.getElementsByTagName("ideEvento").item(0);
                        String perApur = ideEvento.getElementsByTagName("perApur").item(0).getTextContent();

                        //Pega Primeira Parte do cnpj
                        Element ideEmpregador = (Element) evtRemun.getElementsByTagName("ideEmpregador").item(0);
                        String nrInsc = ideEmpregador.getElementsByTagName("nrInsc").item(0).getTextContent();

                        //Pegar CPF do Empregado
                        Element ideTrabalhador = (Element) evtRemun.getElementsByTagName("ideTrabalhador").item(0);
                        String cpfTrab = ideTrabalhador.getElementsByTagName("cpfTrab").item(0).getTextContent();

                        //Pega CNPJ da Empresa
                        Element dmDev = (Element) evtRemun.getElementsByTagName("dmDev").item(0);
                        Element infoPerApur = (Element) dmDev.getElementsByTagName("infoPerApur").item(0);
                        Element ideEstabLot = (Element) infoPerApur.getElementsByTagName("ideEstabLot").item(0);
                        String nrInscIdeEstabLot = ideEstabLot.getElementsByTagName("nrInsc").item(0).getTextContent();

                        //Pega Inscrição do Empregado
                        Element remunPerApur = (Element) ideEstabLot.getElementsByTagName("remunPerApur").item(0);
                        String matricula = remunPerApur.getElementsByTagName("matricula").item(0).getTextContent();

                        //Pega Lancamentos
                        for (int j = 0; j < remunPerApur.getElementsByTagName("itensRemun").getLength(); j++) {
                            String qtdRubr = null;
                            Element itensRemun = (Element) remunPerApur.getElementsByTagName("itensRemun").item(j);
                            String codRubr = itensRemun.getElementsByTagName("codRubr").item(0).getTextContent();

                            if (itensRemun.getElementsByTagName("qtdRubr").getLength() != 0) {
                                qtdRubr = itensRemun.getElementsByTagName("qtdRubr").item(0).getTextContent();
                            }

                            String vrRubr = itensRemun.getElementsByTagName("vrRubr").item(0).getTextContent();

                            //Carregar Lista
                            LancamentoESocial le = new LancamentoESocial();
                            le.setPeriodo(perApur);
                            le.setCnpjSimples(nrInsc);
                            le.setCnpjCompleto(nrInscIdeEstabLot);
                            le.setCPF(cpfTrab);
                            le.setMatricula(matricula);
                            le.setCodRubrica(codRubr);
                            le.setReferencia(qtdRubr);
                            le.setValor(vrRubr);
                            listaLancamento.add(le);
                        }
                    }
                }
            } else if (xml.getElementsByTagName("evtTabRubrica").getLength() != 0) {
                //Evento S-1010
                for (int i = 0; i < xml.getElementsByTagName("evtTabRubrica").getLength(); i++) {
                    Element evtTabRubrica = (Element) xml.getElementsByTagName("evtTabRubrica").item(i);
                    if (!evtTabRubrica.getAttribute("Id").equals("")) {
                        Element ideEmpregador = (Element) evtTabRubrica.getElementsByTagName("ideEmpregador").item(0);

                        Element infoRubrica = (Element) evtTabRubrica.getElementsByTagName("infoRubrica").item(0);

                        //Parar caso não exista inclusao
                        if (infoRubrica.getElementsByTagName("inclusao").getLength() == 0) {
                            break;
                        }

                        Element inclusao = (Element) infoRubrica.getElementsByTagName("inclusao").item(0);
                        Element ideRubrica = (Element) inclusao.getElementsByTagName("ideRubrica").item(0);
                        Element dadosRubrica = (Element) inclusao.getElementsByTagName("dadosRubrica").item(0);

                        String nrInsc = ideEmpregador.getElementsByTagName("nrInsc").item(0).getTextContent();
                        String codRubr = ideRubrica.getElementsByTagName("codRubr").item(0).getTextContent();
                        String dscRubr = dadosRubrica.getElementsByTagName("dscRubr").item(0).getTextContent();
                        String natRubr = dadosRubrica.getElementsByTagName("natRubr").item(0).getTextContent();
                        String tpRubr = dadosRubrica.getElementsByTagName("tpRubr").item(0).getTextContent();

                        Rubrica r = new Rubrica();
                        //r.setConversao(conversao);
                        r.setCnpj(nrInsc);
                        r.setCodigo(codRubr);
                        r.setDescricao(dscRubr);
                        r.setNatureza(natRubr);
                        r.setTipo(tpRubr); // 1 - Provento / 2 - Desconto
                        listaRubrica.add(r);
                    }
                }
            }
        }
        //Transformar os dados
        this.converterDados(listaEmpresa, listaRubrica, listaLancamento);
    }
    
    public void converterDados(ArrayList<Empresa> listaEmpresa, ArrayList<Rubrica> listaRubrica, ArrayList<LancamentoESocial> listaLancamento) {
        for(Rubrica r : listaRubrica) {
            for(Empresa e : listaEmpresa) {
                if(e.getCnpj().contains(r.getCnpj())) {
                    //System.out.println(e.getCodigo());
                    break;
                }
            }
        }
        
        System.out.println(listaLancamento.size());
    }
    
    //Daqui esta Ok

    public ArrayList<String> retornaArquivos(String caminho) {
        ArrayList<String> listaArquivo = new ArrayList<>();

        for (File f : new File(caminho).listFiles()) {
            //Verificar se é diretorio
            if (f.isDirectory()) {
                for (File ff : f.listFiles()) {
                    listaArquivo.add(ff.getAbsolutePath());
                }
            } else {
                //Verificar se o arquivo é formato XML
                if (f.getName().contains("xml")) {
                    listaArquivo.add(f.getAbsolutePath());
                }
            }
        }

        return listaArquivo;
    }
    
    public ArrayList<String> buscaEmpresas(String caminho) {
        ArrayList<String> listaCnpj = new ArrayList<>();
        try {
            for (String arquivo : this.retornaArquivos(caminho)) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                
                Document d = builder.parse(new File(arquivo));
                Element xml = d.getDocumentElement();
                
                if (xml.getElementsByTagName("evtRemun").getLength() != 0) {
                    for (int i = 0; i < xml.getElementsByTagName("evtRemun").getLength(); i++) {
                        Element evtRemun = (Element) xml.getElementsByTagName("evtRemun").item(i);
                        if (!evtRemun.getAttribute("Id").equals("")) {
                            //Evento S-1200
                            
                            //Pega Primeira Parte do cnpj
                            //Element ideEmpregador = (Element) evtRemun.getElementsByTagName("ideEmpregador").item(0);
                            //String nrInsc = ideEmpregador.getElementsByTagName("nrInsc").item(0).getTextContent();
                            
                            //Pega CNPJ da Empresa
                            Element dmDev = (Element) evtRemun.getElementsByTagName("dmDev").item(0);
                            Element infoPerApur = (Element) dmDev.getElementsByTagName("infoPerApur").item(0);
                            Element ideEstabLot = (Element) infoPerApur.getElementsByTagName("ideEstabLot").item(0);
                            String nrInscIdeEstabLot = ideEstabLot.getElementsByTagName("nrInsc").item(0).getTextContent();
                            
                            if(!listaCnpj.contains(nrInscIdeEstabLot)) {
                                listaCnpj.add(nrInscIdeEstabLot);
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException ex) {
            new TelaErro(12, ex.getStackTrace()).setVisible(true);
        } catch (SAXException ex) {
            new TelaErro(13, ex.getStackTrace()).setVisible(true);
        } catch (IOException ex) {
            new TelaErro(3, ex.getStackTrace()).setVisible(true);
        }
        return listaCnpj;
    }
    
    public ArrayList<Rubrica> retornarRubricas(String caminho) {
        ArrayList<Rubrica> listaRubrica = new ArrayList<>();
        
        ArrayList<String> naoDuplicar = new ArrayList<>();
        
        try {
            for (String arquivo : this.retornaArquivos(caminho)) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                
                Document d = builder.parse(new File(arquivo));
                Element xml = d.getDocumentElement();
                
                if (xml.getElementsByTagName("evtTabRubrica").getLength() != 0) {
                    //Evento S-1010
                    for (int i = 0; i < xml.getElementsByTagName("evtTabRubrica").getLength(); i++) {
                        
                        Element evtTabRubrica = (Element) xml.getElementsByTagName("evtTabRubrica").item(i);
                        
                        if (!evtTabRubrica.getAttribute("Id").equals("")) {
                            Element ideEmpregador = (Element) evtTabRubrica.getElementsByTagName("ideEmpregador").item(0);
                            Element infoRubrica = (Element) evtTabRubrica.getElementsByTagName("infoRubrica").item(0);
                            
                            //Parar caso não exista inclusao
                            if (infoRubrica.getElementsByTagName("inclusao").getLength() == 0) {
                                break;
                            }
                            
                            Element inclusao = (Element) infoRubrica.getElementsByTagName("inclusao").item(0);
                            Element ideRubrica = (Element) inclusao.getElementsByTagName("ideRubrica").item(0);
                            Element dadosRubrica = (Element) inclusao.getElementsByTagName("dadosRubrica").item(0);

                            String nrInsc = ideEmpregador.getElementsByTagName("nrInsc").item(0).getTextContent();
                            String codRubr = ideRubrica.getElementsByTagName("codRubr").item(0).getTextContent();
                            String dscRubr = dadosRubrica.getElementsByTagName("dscRubr").item(0).getTextContent();
                            String natRubr = dadosRubrica.getElementsByTagName("natRubr").item(0).getTextContent();
                            String tpRubr = dadosRubrica.getElementsByTagName("tpRubr").item(0).getTextContent();

                            Rubrica r = new Rubrica();
                            //r.setConversao(conversao);
                            r.setCnpj(nrInsc);
                            r.setCodigo(codRubr);
                            r.setDescricao(dscRubr);
                            r.setNatureza(natRubr);
                            r.setTipo(tpRubr); // 1 - Provento / 2 - Desconto / 3- Informativa / 4 - Informativa dedutora
                            
                            //Não duplicar
                            if(!naoDuplicar.contains(r.getCnpj()+"|"+r.getCodigo())) {
                                naoDuplicar.add(r.getCnpj()+"|"+r.getCodigo());
                                listaRubrica.add(r);
                            }
                        }
                        
                    }
                    
                }
            }
        } catch (ParserConfigurationException ex) {
            new TelaErro(12, ex.getStackTrace()).setVisible(true);
        } catch (SAXException ex) {
            new TelaErro(13, ex.getStackTrace()).setVisible(true);
        } catch (IOException ex) {
            new TelaErro(3, ex.getStackTrace()).setVisible(true);
        }
        return listaRubrica;
    }
    
    public ArrayList<Lancamento> retornarLancamentos(int idConversao, String caminho) {
        ArrayList<Lancamento> listaLancamento = new ArrayList<>();
        ArrayList<LancamentoESocial> listaLancamentoESocial = new ArrayList<>();
        
        try {
            for (String arquivo : this.retornaArquivos(caminho)) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                
                Document d = builder.parse(new File(arquivo));
                Element xml = d.getDocumentElement();
                
                if (xml.getElementsByTagName("evtRemun").getLength() != 0) {
                    for (int i = 0; i < xml.getElementsByTagName("evtRemun").getLength(); i++) {
                        Element evtRemun = (Element) xml.getElementsByTagName("evtRemun").item(i);
                        if (!evtRemun.getAttribute("Id").equals("")) {
                            //Evento S-1200
                            
                            //Pega periodo
                            Element ideEvento = (Element) evtRemun.getElementsByTagName("ideEvento").item(0);
                            int indApuracao = Integer.valueOf(ideEvento.getElementsByTagName("indApuracao").item(0).getTextContent());
                            String perApur = ideEvento.getElementsByTagName("perApur").item(0).getTextContent();

                            //Pega Primeira Parte do cnpj
                            Element ideEmpregador = (Element) evtRemun.getElementsByTagName("ideEmpregador").item(0);
                            String nrInsc = ideEmpregador.getElementsByTagName("nrInsc").item(0).getTextContent();

                            //Pegar CPF do Empregado
                            Element ideTrabalhador = (Element) evtRemun.getElementsByTagName("ideTrabalhador").item(0);
                            String cpfTrab = ideTrabalhador.getElementsByTagName("cpfTrab").item(0).getTextContent();

                            //Pega CNPJ da Empresa
                            Element dmDev = (Element) evtRemun.getElementsByTagName("dmDev").item(0);
                            Element infoPerApur = (Element) dmDev.getElementsByTagName("infoPerApur").item(0);
                            Element ideEstabLot = (Element) infoPerApur.getElementsByTagName("ideEstabLot").item(0);
                            String nrInscIdeEstabLot = ideEstabLot.getElementsByTagName("nrInsc").item(0).getTextContent();

                            //Pega Inscrição do Empregado
                            Element remunPerApur = (Element) ideEstabLot.getElementsByTagName("remunPerApur").item(0);
                            String matricula = remunPerApur.getElementsByTagName("matricula").item(0).getTextContent();
                            
                            //1 - Salario Mensal / 2 - 13º Salario
                            if(indApuracao == 1) {
                                //Pega Lancamento
                                for (int j = 0; j < remunPerApur.getElementsByTagName("itensRemun").getLength(); j++) {
                                    String qtdRubr = null;
                                    Element itensRemun = (Element) remunPerApur.getElementsByTagName("itensRemun").item(j);
                                    String codRubr = itensRemun.getElementsByTagName("codRubr").item(0).getTextContent();

                                    if (itensRemun.getElementsByTagName("qtdRubr").getLength() != 0) {
                                        qtdRubr = itensRemun.getElementsByTagName("qtdRubr").item(0).getTextContent();
                                    }

                                    String vrRubr = itensRemun.getElementsByTagName("vrRubr").item(0).getTextContent();

                                    //Carregar Lista
                                    LancamentoESocial le = new LancamentoESocial();
                                    le.setPeriodo(perApur);
                                    le.setCnpjSimples(nrInsc);
                                    le.setCnpjCompleto(nrInscIdeEstabLot);
                                    le.setCPF(cpfTrab);
                                    le.setMatricula(matricula);
                                    le.setCodRubrica(codRubr);
                                    le.setReferencia(qtdRubr);
                                    le.setValor(vrRubr); //.replaceAll("\\.", ",")
                                    listaLancamentoESocial.add(le);
                                }
                            }
                        }
                    }
                }
            }
            
            EmpresaDAO empDAO = new EmpresaDAO();
            EmpregadoDAO eDAO = new EmpregadoDAO();
            
            //Converter lista LancamentoESocial para Lancamentos
            for(LancamentoESocial le : listaLancamentoESocial) {
                Lancamento la = new Lancamento();
                int codEmpresa = empDAO.retornarCodigo(idConversao, le.getCnpjCompleto());
                la.setCodEmpresa(codEmpresa);
                
                int codEmpregado = eDAO.retornaCodEmpregado(idConversao, codEmpresa, le.getCPF());
                
                if(codEmpregado != 0) {
                    la.setCodEmpregado(String.valueOf(codEmpregado));
                } else {
                    la.setCodEmpregado(le.getCPF());
                }
                
                //Preparar Conversao
                String periodo = le.getPeriodo();
                periodo = periodo.replaceAll("-", "");
                String mes = periodo.substring(4);
                String ano = periodo.substring(0, 4);
                la.setCompetencia(mes+"/"+ano);
                la.setCodRubrica(le.getCodRubrica());
                la.setReferencia(le.getReferencia());
                la.setValor(le.getValor());
                listaLancamento.add(la);
            }
        } catch (ParserConfigurationException ex) {
            new TelaErro(12, ex.getStackTrace()).setVisible(true);
        } catch (SAXException ex) {
            new TelaErro(13, ex.getStackTrace()).setVisible(true);
        } catch (IOException ex) {
            new TelaErro(3, ex.getStackTrace()).setVisible(true);
        }
        
        return listaLancamento;
    }
    
    public ArrayList<RubricaConfiguracao> retornaConfiguracaoRubrica(int idConversao) {
        ArrayList<RubricaConfiguracao> listaRubricaConf = new ArrayList<>();
        
        //Classes
        Empresa e = new Empresa();
        Conversao c = new Conversao();
        
        //Classes DAO
        EmpresaDAO eDAO = new EmpresaDAO();
        RubricaDAO rDAO = new RubricaDAO();
        RubricaNaoLocalizadaDAO rnlDAO = new RubricaNaoLocalizadaDAO();
        
        //Rubricas Usadas
        RubricaUtilizadaDAO ruDAO = new RubricaUtilizadaDAO();
        ArrayList<RubricaUtilizada> listaRubricaUsa = ruDAO.retornarRubricaUtilizadaPorConversao(idConversao);
        
        //Rubricas Não Localizada
        ArrayList<RubricaNaoLocalizada> listaRNL = new ArrayList<>();
        
        //Variaveis interna
        int codEmpresa = 0;
        String cnpj = null;
        Date dataS = null;
        Rubrica r;
        
        for(RubricaUtilizada ru : listaRubricaUsa) {
            if(codEmpresa != ru.getCodEmpresa()) {
                codEmpresa = ru.getCodEmpresa();
                cnpj = eDAO.retornaCNPJ(ru.getCodEmpresa(), idConversao);
                dataS = eDAO.retornaData(ru.getCodEmpresa(), idConversao);
            }
            
            //Realizar DePara
            for(DeParaRubrica dpr : rDAO.retornaDeParaRubrica(listaRubricaUsa)) {
                if(ru.getCodRubrica().equals(dpr.getOrigem()) && ru.getCodEmpresa() == dpr.getCodEmpresa()) {
                    
                    //Carregar Dados da Rubrica
                    r = rDAO.carregarPorConversaoEmpresaCodigo(cnpj.substring(0,8), dpr.getOrigem(), idConversao);
                    
                    //IF de rubricas não localizadas
                    if(r.getCodigo() != null) {
                        
                        //IF para remover as rubricas de Ferias
                        if(!r.getNatureza().equals("1020")) {

                            if(r.getTipo().equals("2")) { //Rubrica de Desconto Valor
                                //Configuração Rubrica de Desconto Valor
                                RubricaConfiguracao rc = new RubricaConfiguracao();
                                rc.setCodigo(dpr.getDestino());
                                rc.setCodEmpresa(codEmpresa);
                                rc.setDescricao(r.getDescricao());
                                rc.setInicioData(dataS);
                                rc.setSituacao(true);
                                rc.setSituacaoData(null);
                                rc.setTipo(0);
                                rc.setAvisoPrevio(false);
                                rc.setSalarioFerias(false);
                                rc.setLicencaPremio(false);
                                //Medias
                                rc.setAvisoPrevioMedias(false);
                                rc.setSalarioMedias(false);
                                rc.setFeriasMedias(false);
                                rc.setLicencaPremioMedias(false);
                                rc.setSaldoSalarioMedias(false);
                                rc.setHoras(false);
                                listaRubricaConf.add(rc);
                            } else if(r.getTipo().equals("1") && !(!r.getDescricao().toLowerCase().replaceAll("\\.", "").contains("dsr") && r.getDescricao().toLowerCase().contains("hora") && r.getDescricao().toLowerCase().contains("extra")) && !(r.getDescricao().toLowerCase().contains("adicional") && r.getDescricao().toLowerCase().contains("noturno") && r.getDescricao().toLowerCase().contains("infor"))) { //Rubrica de Provento Valor
                                RubricaConfiguracao rc = new RubricaConfiguracao();
                                rc.setCodigo(dpr.getDestino());
                                rc.setCodEmpresa(codEmpresa);
                                rc.setDescricao(r.getDescricao());
                                rc.setInicioData(dataS);
                                rc.setSituacao(true);
                                rc.setSituacaoData(null);
                                rc.setTipo(1);
                                rc.setAvisoPrevio(false);
                                rc.setSalarioFerias(false);
                                rc.setLicencaPremio(false);
                                //Medias
                                rc.setAvisoPrevioMedias(false);
                                rc.setSalarioMedias(false);
                                rc.setFeriasMedias(false);
                                rc.setLicencaPremioMedias(false);
                                rc.setSaldoSalarioMedias(false);
                                rc.setHoras(false);
                                listaRubricaConf.add(rc);
                            } else if(r.getTipo().equals("1") && (!r.getDescricao().toLowerCase().replaceAll("\\.", "").contains("dsr") && r.getDescricao().toLowerCase().contains("hora") && r.getDescricao().toLowerCase().contains("extra"))) { //Rubrica de Horas Extras
                                String valor = r.getDescricao();
                                valor = valor.replaceAll("%", "");
                                valor = valor.replaceAll("[^0-9]", "");
                                int valorInt = 100;

                                if(r.getDescricao().toLowerCase().contains("normal")) {
                                    RubricaConfiguracao rc = new RubricaConfiguracao();
                                    rc.setCodigo(dpr.getDestino());
                                    rc.setCodEmpresa(codEmpresa);
                                    rc.setDescricao(r.getDescricao());
                                    rc.setInicioData(dataS);
                                    rc.setSituacao(true);
                                    rc.setSituacaoData(null);
                                    rc.setTipo(1);
                                    rc.setAvisoPrevio(false);
                                    rc.setSalarioFerias(false);
                                    rc.setLicencaPremio(false);
                                    //Medias
                                    rc.setAvisoPrevioMedias(true);
                                    rc.setSalarioMedias(true);
                                    rc.setFeriasMedias(true);
                                    rc.setLicencaPremioMedias(false);
                                    rc.setSaldoSalarioMedias(true);
                                    rc.setHoras(true);
                                    listaRubricaConf.add(rc);
                                } else {
                                    valorInt = valorInt + Integer.parseInt(valor);
                                    RubricaConfiguracao rc = new RubricaConfiguracao();
                                    rc.setCodigo(dpr.getDestino());
                                    rc.setCodEmpresa(codEmpresa);
                                    rc.setDescricao(r.getDescricao());
                                    rc.setInicioData(dataS);
                                    rc.setSituacao(true);
                                    rc.setSituacaoData(null);
                                    rc.setTipo(1);
                                    rc.setAvisoPrevio(false);
                                    rc.setSalarioFerias(false);
                                    rc.setLicencaPremio(false);
                                    //Medias
                                    rc.setAvisoPrevioMedias(true);
                                    rc.setSalarioMedias(true);
                                    rc.setFeriasMedias(true);
                                    rc.setLicencaPremioMedias(false);
                                    rc.setSaldoSalarioMedias(true);
                                    rc.setHoras(true);
                                    listaRubricaConf.add(rc);
                                }
                            } else if(r.getTipo().equals("1") && (r.getDescricao().toLowerCase().contains("adicional") && r.getDescricao().toLowerCase().contains("noturno") && r.getDescricao().toLowerCase().contains("infor"))) { //Rubrica de Adicional Noturno (INFOR)
                                RubricaConfiguracao rc = new RubricaConfiguracao();
                                rc.setCodigo(dpr.getDestino());
                                rc.setCodEmpresa(codEmpresa);
                                rc.setDescricao(r.getDescricao());
                                rc.setInicioData(dataS);
                                rc.setSituacao(true);
                                rc.setSituacaoData(null);
                                rc.setTipo(1);
                                rc.setAvisoPrevio(false);
                                rc.setSalarioFerias(false);
                                rc.setLicencaPremio(false);
                                //Medias
                                rc.setAvisoPrevioMedias(true);
                                rc.setSalarioMedias(true);
                                rc.setFeriasMedias(true);
                                rc.setLicencaPremioMedias(false);
                                rc.setSaldoSalarioMedias(true);
                                rc.setHoras(true);
                                listaRubricaConf.add(rc);
                            }
                        }
                        //Fim do IF da Rubrica sem o Codigo 
                    } else {
                        //Adicionar na lista Rubricas não localizadas
                        RubricaNaoLocalizada rnl = new RubricaNaoLocalizada();
                        c.setId(idConversao);
                        rnl.setConversao(c);
                        e.setId(eDAO.retornaID(dpr.getCodEmpresa(), idConversao)); //Metodo para retornar com o codigo da empresa
                        rnl.setEmpresa(e);
                        rnl.setCodigo(dpr.getOrigem());
                        listaRNL.add(rnl);
                    }
                }
            }
        }
        
        //Registrar Rubrica não localizada no banco de dados
        if(!listaRNL.isEmpty()) {
            rnlDAO.inserir(listaRNL);
        }
        
        return listaRubricaConf;
    }
}
