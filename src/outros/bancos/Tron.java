package outros.bancos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.ArrayList;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import modelo.Conversao;
import modelo.DeParaRubrica;
import modelo.Empregado;
import modelo.Empresa;
import modelo.EmpresaDAO;
import modelo.Lancamento;
import modelo.Rubrica;
import modelo.RubricaConfiguracao;
import modelo.RubricaDAO;
import modelo.RubricaNaoLocalizada;
import modelo.RubricaNaoLocalizadaDAO;
import modelo.RubricaUtilizada;
import modelo.RubricaUtilizadaDAO;

/**
 *
 * @author Gabriel Moraes
 */
public class Tron {
    public ArrayList<String> buscarEmpresas(String arquivoOrigem) throws IOException {
        ArrayList<String> lista = new ArrayList<>();
        
        BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoOrigem), "Cp1252"));
        String linha = buffRead.readLine();
        
        boolean pegarCNPJ = false;
        
        while(linha != null) {
            if(pegarCNPJ) {
                String cnpj = linha.replaceAll("\\.", "").replaceAll("-", "").replaceAll("/", "");
                if(!lista.contains(cnpj)) {
                    lista.add(cnpj);
                }
                
                pegarCNPJ = false;
            }
            
            if(linha.contains("CPF")) {
                pegarCNPJ = true;
            }
            
            linha = buffRead.readLine();
        }
        
        return lista;
    }
    
    public ArrayList<Rubrica> retornarRubricasXLS(ArrayList<String> arquivosXLS, String caminho) throws IOException, BiffException {
        ArrayList<Rubrica> lista = new ArrayList<>();
        
        for(String arqExcel : arquivosXLS) {
            WorkbookSettings ws = new WorkbookSettings();
            ws.setEncoding("Cp1252");
            Workbook wrk = Workbook.getWorkbook(new File(caminho+"\\"+arqExcel), ws);
            Sheet sheet = wrk.getSheet(0);
            int linhas = sheet.getRows();
            String cnpj = arqExcel.replaceAll(".xls", "");
            
            for (int i = 1; i < linhas; i++) {
                Rubrica r = new Rubrica();
                Cell col1row1 = sheet.getCell(0,i); //Codigo
                Cell col1row2 = sheet.getCell(1,i); //Descrição
                Cell col1row3 = sheet.getCell(2,i); //Tipo
                r.setCnpj(cnpj);
                r.setCodigo(col1row1.getContents());
                r.setDescricao(col1row2.getContents());
                r.setTipo(col1row3.getContents());
                lista.add(r);
            }
        }
        
        return lista;
    }
    
    public ArrayList<Lancamento> retornarLancamentos(ArrayList<Empresa> listaEmpresa, ArrayList<Empregado> listaEmpregado, String arquivoOrigem) throws IOException {
        ArrayList<Lancamento> lista = new ArrayList<>();
        
        BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoOrigem), "Cp1252"));
        String linha = buffRead.readLine();
        
        boolean pegarLancamento = true;
        boolean pegarCNPJ = false;
        String cnpj = "";
        String cpf = "";
        
        //Armazenar lancamentos do empregado
        ArrayList<String> listaTemp = new ArrayList<>();
        
        while(linha != null) {
            
            if(pegarCNPJ) {
                cnpj = linha;
                
                pegarCNPJ = false;
            }
            
            if(linha.contains("CPF")) {
                cpf = linha.substring(linha.lastIndexOf(" "), linha.length());
                pegarLancamento = false;
                pegarCNPJ = true;
            }
            
            if(pegarLancamento) {
                listaTemp.add(linha);
            }
            
            if(linha.contains("Adm:")) {
                String competencia = linha.split(" ")[0];
                
                //Competencia
                String[] campos = competencia.split("/");
                String mes = null;
                String ano = campos[1];
                
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
                
                for(String l : listaTemp) {
                    String codRubrica = l.substring(0, l.indexOf(" "));
                    String referencia = null;
                    String valor = l.split(" ")[l.split(" ").length - 1];
                    
                    //Pegar referencia
                    String pegarReferencia = l.split(" ")[l.split(" ").length - 2];
                    if(pegarReferencia.contains(".") || pegarReferencia.matches("^\\d+$")) {
                        //Preparar a referencia
                        if(!pegarReferencia.contains(".")) {
                            pegarReferencia = pegarReferencia + ".00";
                        }

                        String[] sep = pegarReferencia.split("\\.");
                        if(sep.length >= 2) {
                            if(sep[1].length() < 2) {
                                pegarReferencia = pegarReferencia + "0";
                            }
                        } else {
                            pegarReferencia = pegarReferencia + "00";
                        }
                        
                        referencia = pegarReferencia;
                    }
                    
                    //Limpar variaveis
                    cnpj = cnpj.replaceAll("\\.", "").replaceAll("-", "").replaceAll("/", "");
                    cpf = cpf.replaceAll("\\.", "").replaceAll("-", "");
                    
                    int codEmpresa = 0;
                    int codEmpregado = 0;
                    
                    for(Empresa e : listaEmpresa) {
                        if(cnpj.equals(e.getCnpj())) {
                            codEmpresa = e.getCodigo();
                            break;
                        }
                    }
                    
                    for(Empregado ee : listaEmpregado) {
                        if(cpf.replaceAll(" ", "").equals(ee.getCpf().replaceAll(" ", ""))) {
                            codEmpregado = ee.getCodigo();
                            break;
                        }
                    }
                    
                    Lancamento la = new Lancamento();
                    la.setCodEmpresa(codEmpresa);
                    la.setCodEmpregado(String.valueOf(codEmpregado));
                    la.setCompetencia(mes+"/"+ano);
                    la.setCodRubrica(codRubrica);
                    la.setReferencia(referencia);
                    la.setValor(valor);
                    lista.add(la);
                }
                
                listaTemp.clear();
                
                pegarLancamento = true;
            }
            
            linha = buffRead.readLine();
        }
        
        return lista;
    }
    
    public ArrayList<RubricaConfiguracao> retornaConfiguracaoRubrica(int idConversao) {
        RubricaDAO rDAO = new RubricaDAO();
        EmpresaDAO eDAO = new EmpresaDAO();
        RubricaUtilizadaDAO ruDAO = new RubricaUtilizadaDAO();
        
        ArrayList<RubricaConfiguracao> rcList = new ArrayList<>();
        ArrayList<RubricaUtilizada> ruList = ruDAO.retornarRubricaUtilizadaPorConversao(idConversao);
        
        //Rubricas Não Localizada
        RubricaNaoLocalizadaDAO rnlDAO = new RubricaNaoLocalizadaDAO();
        ArrayList<RubricaNaoLocalizada> listaRNL = new ArrayList<>();
        
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
                    
                    //Não gravar quando nao houver Rubrica
                    if(r.getTipo() != null) {
                        if(r.getTipo().equals("D")) { //Rubrica de Desconto Valor
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
                        } else if(r.getTipo().equals("P") && !r.getDescricao().contains("HORA EXTRA ") && !(r.getDescricao().contains("Adicional") && r.getDescricao().contains("Noturno") && r.getDescricao().contains("INFOR"))) { //Rubrica de Provento Valor
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
                        } else if(r.getTipo().equals("P") && r.getDescricao().contains("HORA EXTRA ")) { //Rubrica de Horas Extras
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
                        } else if(r.getTipo().equals("P") && (r.getDescricao().contains("Adicional") && r.getDescricao().contains("Noturno") && r.getDescricao().contains("INFOR"))) { //Rubrica de Adicional Noturno (INFOR)
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
                    } else {
                        //Adicionar na lista Rubricas não localizadas
                        RubricaNaoLocalizada rnl = new RubricaNaoLocalizada();
                        Conversao c = new Conversao();
                        c.setId(idConversao);
                        rnl.setConversao(c);
                        Empresa e = new Empresa();
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
        return rcList;
    }
}
