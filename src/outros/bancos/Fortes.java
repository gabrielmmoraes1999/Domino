package outros.bancos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Scanner;
import modelo.DeParaRubrica;
import modelo.Empresa;
import modelo.EmpresaDAO;
import modelo.Lancamento;
import modelo.Rubrica;
import modelo.RubricaConfiguracao;
import modelo.RubricaDAO;
import modelo.RubricaUtilizada;
import modelo.RubricaUtilizadaDAO;

/**
 *
 * @author Gabriel Moraes
 */
public class Fortes {

    public ArrayList<String> buscarEmpresas(String arquivo) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        ArrayList<String> lista = new ArrayList<>();
        try ( BufferedReader lerArq = new BufferedReader(new InputStreamReader(new FileInputStream(new File(arquivo)), "Cp1252"))) {
            String linha = lerArq.readLine();

            while (linha != null) {

                if (linha.contains("CNPJ:")) {
                    if (!lista.contains(linha.replaceAll("[^0-9]", ""))) {
                        lista.add(linha.replaceAll("[^0-9]", ""));
                    }
                }

                linha = lerArq.readLine();
            }
        }
        return lista;
    }

    public void retornarRubricasExcel() {
        //Em desenvolvimento
    }

    public ArrayList<Lancamento> retornarLancamentos(ArrayList<Empresa> listaEmpresa, String arquivo) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        ArrayList<Lancamento> lista = new ArrayList<>();

        try ( BufferedReader lerArq = new BufferedReader(new InputStreamReader(new FileInputStream(new File(arquivo)), "Cp1252"))) {
            String linha = lerArq.readLine();

            boolean empregado = false;
            boolean competencia = false;
            boolean lancamento = false;
            
            String comp = null;
            String cnpj = "";
            String codEmpregado = null;

            while (linha != null) {
                //Pegar a competencia
                if (linha.equals("Inscrição")) {
                    competencia = false;
                }

                if (competencia) {
                    String[] campos = linha.split(" ");
                    String mes = null;
                    String ano = linha.substring(linha.length() - 4, linha.length());
                    
                    switch(campos[0]) {
                        case "Janeiro":
                            mes = "01";
                            break;
                        case "Fevereiro":
                            mes = "02";
                            break;
                        case "Março":
                            mes = "03";
                            break;
                        case "Abril":
                            mes = "04";
                            break;
                        case "Maio":
                            mes = "05";
                            break;
                        case "Junho":
                            mes = "06";
                            break;
                        case "Julho":
                            mes = "07";
                            break;
                        case "Agosto":
                            mes = "08";
                            break;
                        case "Setembro":
                            mes = "09";
                            break;
                        case "Outubro":
                            mes = "10";
                            break;
                        case "Novembro":
                            mes = "11";
                            break;
                        case "Dezembro":
                            mes = "12";
                            break;
                    }
                    
                    comp = mes + "/" + ano;
                }

                if (linha.equals("Competência")) {
                    competencia = true;
                }

                //Pegar o CNPJ
                if (linha.contains("CNPJ:")) {
                    cnpj = linha.replaceAll("[^0-9]", "");
                }

                //Pegar o Empregado
                if (linha.equals("PIS") || linha.contains("PIS ")) {
                    empregado = false;
                }

                if (empregado) {
                    codEmpregado = linha.split(" ")[0];
                }

                if (linha.equals("Empregado")) {
                    empregado = true;
                }

                //Pegar os lancamentos
                if (linha.equals("Total de Proventos")) {
                    lancamento = false;
                }

                if (lancamento) {
                    int indexValor = linha.indexOf(",") + 3;
                    int codEmpresa = 0;
                    String rubrica = linha.substring(linha.length() - 3, linha.length());
                    String referencia = null;
                    String valor = linha.substring(0, indexValor);
                    
                    if(linha.contains("h")) {
                        referencia = linha.substring(indexValor, linha.indexOf("h")) + ".0";
                    }
                    
                    for(Empresa e : listaEmpresa) {
                        if(cnpj.equals(e.getCnpj())) {
                            codEmpresa = e.getCodigo();
                        }
                    }

                    Lancamento la = new Lancamento();
                    la.setCodEmpresa(codEmpresa);
                    la.setCodEmpregado(codEmpregado);
                    la.setCompetencia(comp);
                    la.setCodRubrica(rubrica);
                    
                    //Ajustar referencia
                    if(referencia == null) {
                        la.setReferencia(referencia);
                    } else {
                        if(!referencia.contains(".")) {
                            referencia = referencia + ".00";
                        }

                        String[] sep = referencia.split("\\.");
                        if(sep.length >= 2) {
                            if(sep[1].length() < 2) {
                                referencia = referencia + "0";
                            }
                        } else {
                            referencia = referencia + "00";
                        }
                        
                        la.setReferencia(referencia);
                    }
                    la.setValor(valor);
                    lista.add(la);
                }

                if (linha.contains("DescontoProvento")) {
                    lancamento = true;
                }

                linha = lerArq.readLine();
            }
        }

        return lista;
    }
    
    public ArrayList<Rubrica> retornarRubricasCSV(ArrayList<String> arquivosCSV, String caminho) throws UnsupportedEncodingException, FileNotFoundException {
        ArrayList<Rubrica> lista = new ArrayList<>();
        
        for(String arqCSV : arquivosCSV) {
            BufferedReader arqIn = new BufferedReader(new InputStreamReader(new FileInputStream(caminho + "\\" + arqCSV), "Cp1252"));
            
            Scanner scanner = new Scanner(arqIn).useDelimiter("\n");
            String cnpj = arqCSV.replaceAll(".csv", "");
            
            while (scanner.hasNext()) {
                String[] split = scanner.next().split(";");
                
                Rubrica r = new Rubrica();
                r.setCnpj(cnpj);
                r.setCodigo(split[0].replaceAll("\"", ""));
                r.setDescricao(split[1].replaceAll("\"", ""));
                r.setTipo(split[2].replaceAll("\"", ""));
                lista.add(r);
            }
        }
        
        return lista;
    }
    
    public ArrayList<RubricaConfiguracao> retornaConfiguracaoRubrica(int idConversao) {
        RubricaDAO rDAO = new RubricaDAO();
        EmpresaDAO eDAO = new EmpresaDAO();
        RubricaUtilizadaDAO ruDAO = new RubricaUtilizadaDAO();
        
        ArrayList<RubricaConfiguracao> rcList = new ArrayList<>();
        ArrayList<RubricaUtilizada> ruList = ruDAO.retornarRubricaUtilizadaPorConversao(idConversao);
        
        int codEmpresa = 0;
        String cnpj = null;
        Date dataS = null;
        Rubrica r;
        
        for(RubricaUtilizada ru : ruList) {
            if(codEmpresa != ru.getCodEmpresa()) {
                codEmpresa = ru.getCodEmpresa();
                cnpj = eDAO.retornaCNPJ(ru.getCodEmpresa(), idConversao);
                dataS = eDAO.retornaData(ru.getCodEmpresa(), idConversao);
            }
            
            for(DeParaRubrica dpr : rDAO.retornaDeParaRubrica(ruList)) {
                if(ru.getCodRubrica().equals(dpr.getOrigem()) && ru.getCodEmpresa() == dpr.getCodEmpresa()) { //Alterado
                    //Carregar Dados da Rubrica
                    r = rDAO.carregarPorConversaoEmpresaCodigo(cnpj, dpr.getOrigem(), idConversao);
                    
                    if(r.getTipo().equals("Desconto")) { //Rubrica de Desconto Valor
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
                        rcList.add(rc);
                    } else if(r.getTipo().equals("Provento") && !r.getDescricao().contains("Hora-Extra ") && !(r.getDescricao().contains("Adicional") && r.getDescricao().contains("Noturno") && r.getDescricao().contains("INFOR"))) { //Rubrica de Provento Valor
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
                        rcList.add(rc);
                    } else if(r.getTipo().equals("Provento") && r.getDescricao().contains("Hora-Extra ")) { //Rubrica de Horas Extras
                        //String valor = r.getDescricao();
                        //valor = valor.replaceAll("%", "");
                        //valor = valor.replaceAll("[^0-9]", "");
                        //int valorInt = 100;

                        if(r.getDescricao().contains("Normal")) {
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
                            rcList.add(rc);
                        } else {
                            //valorInt = valorInt + Integer.parseInt(valor);
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
                            rcList.add(rc);
                        }
                    } else if(r.getTipo().equals("Rendimento") && (r.getDescricao().contains("Adicional") && r.getDescricao().contains("Noturno") && r.getDescricao().contains("INFOR"))) { //Rubrica de Adicional Noturno (INFOR)
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
                        rcList.add(rc);
                    }
                }
            }
        }
        
        return rcList;
    }
}
