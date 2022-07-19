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
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import modelo.DeParaRubrica;
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
public class Nasajon {
    public ArrayList buscaEmpresas(String origem) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        ArrayList<String> listaCnpj = new ArrayList<>();
        BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(origem), "Cp1252"));
        String linha = buffRead.readLine();
        
        //Adicionar CNPJ na lista
        while(linha != null) {
            if(linha.contains("CNPJ") && linha.contains("Período")) {
                linha = linha.replaceAll("CNPJ", "");
                linha = linha.replaceAll("Período", "");
                linha = linha.replaceAll(":", "");
                linha = linha.replaceAll(" ", "");
                
                if(!listaCnpj.contains(linha)) {
                    listaCnpj.add(linha);
                }
            }
            linha = buffRead.readLine();
        }
        buffRead.close();
        return listaCnpj;
    }
    
    public ArrayList retornarRubricasExcel(ArrayList<String> arquivosExcel, String caminho) throws IOException, BiffException { //DB
        ArrayList<Rubrica> rubricas = new ArrayList<>();
        
        for(String arqExcel : arquivosExcel) {
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
                rubricas.add(r);
            }
        }
        
        return rubricas;
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
                    } else if(r.getTipo().equals("Rendimento") && !r.getDescricao().contains("Hora Extra ") && !(r.getDescricao().contains("Adicional") && r.getDescricao().contains("Noturno") && r.getDescricao().contains("INFOR"))) { //Rubrica de Provento Valor
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
                    } else if(r.getTipo().equals("Rendimento") && r.getDescricao().contains("Hora Extra ")) { //Rubrica de Horas Extras
                        String valor = r.getDescricao();
                        valor = valor.replaceAll("%", "");
                        valor = valor.replaceAll("[^0-9]", "");
                        int valorInt = 100;

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
    
    public ArrayList<Lancamento> retornarLancamentos(ArrayList<modelo.Empresa> listaEmpresa, String arquivo) throws FileNotFoundException, IOException{
        File arq = new File(arquivo);
        BufferedReader lerArq = new BufferedReader(new InputStreamReader(new FileInputStream(arq), "Cp1252")); //new BufferedReader(arq);
        String linha = lerArq.readLine();
        
        int count = 0;
        int countConf = 0;
        int countConf2 = 0;
        int countRubricas = 0;
        boolean inifim = true;
        boolean bComp = false;
        
        ArrayList<Lancamento> lancList = new ArrayList<>();
        
        ArrayList<String> list = new ArrayList<>();
        
        ArrayList<String> cnpjList = new ArrayList<>();
        ArrayList<String> codEmpregadoList = new ArrayList<>();
        ArrayList<String> competenciaList = new ArrayList<>();
        ArrayList<String> codRubricaList = new ArrayList<>();
        ArrayList<String> valorRubricaList = new ArrayList<>();
        ArrayList<Integer> nRubricas = new ArrayList<>();
        
        while(linha != null){
            if(linha.equals("Código Nome do Funcionário CBO Departamento")){
                inifim = false;
            } else if(linha.contains("Cargo:")){
                inifim = true;
            }
            
            //Rubricas
            if(inifim){
                if(!linha.contains("Cargo:")){
                    String[] split = linha.split(" ");
                    codRubricaList.add(split[0]);
                    if(linha.contains("Hora Extra ") || (linha.contains("Adicional") && linha.contains("Noturno") && linha.contains("INFOR"))) {
                        valorRubricaList.add(split[split.length - 2]);
                    } else {
                        valorRubricaList.add(split[split.length - 1]);
                    }
                    
                    countRubricas++;
                }
            }
            
            //Capiturar Competencia
            if(linha.equals("DATA ASSINATURA")){
                linha = linha.replaceAll("DATA ASSINATURA", "\n");
                bComp = true;
                count++;
            } else if(linha.contains("Cargo:")){
                bComp = false;
                count = 0;
                countConf = 0;
                countConf2 = 0;
            }
            
            if(bComp){
                count++;
            }
            
            if(bComp && count == 3) {
                linha = linha.replaceAll(",", "");
                if(!linha.matches("[0-9]*")){
                    countConf++;
                } else if(linha.matches("[0-9]*")) { //Adicionado para corrigir o bug do IRRF
                    countConf++;
                    countConf2--;
                }
            }
            
            if(bComp && countConf == 1) {
                countConf2++;
                if(linha.equals("*****")) {
                    countConf2--;
                }
            }
            
            //Retorna competencia
            if(countConf2 == 2) {
                linha = linha.replaceAll("Janeiro", "01");
                linha = linha.replaceAll("Fevereiro", "02");
                linha = linha.replaceAll("Março", "03");
                linha = linha.replaceAll("Abril", "04");
                linha = linha.replaceAll("Maio", "05");
                linha = linha.replaceAll("Junho", "06");
                linha = linha.replaceAll("Julho", "07");
                linha = linha.replaceAll("Agosto", "08");
                linha = linha.replaceAll("Setembro", "09");
                linha = linha.replaceAll("Outubro", "10");
                linha = linha.replaceAll("Novembro", "11");
                linha = linha.replaceAll("Dezembro", "12");
                competenciaList.add(linha);
            }
            
            //Adicionar CNPJ na lista
            if(linha.contains("CNPJ :")) {
                linha = linha.replaceAll("CNPJ :", "");
                linha = linha.replaceAll("Período :", "");
                linha = linha.replaceAll(" ", "");
                String cnpj = linha;
                cnpj = cnpj.replaceAll("\\.", "");
                cnpj = cnpj.replaceAll("/", "");
                cnpj = cnpj.replaceAll("-", "");
                cnpjList.add(cnpj);
            }
            
            //Retorna CodEmpregado
            if(countConf2 == 3) {
                codEmpregadoList.add(linha.replaceAll("[^0-9]", ""));
                nRubricas.add(countRubricas);
                countRubricas = 0;
            }
            
            linha = lerArq.readLine();
        }
        
        int rub = 0;
        for(int i=0; i < codEmpregadoList.size();i++) {
            for(int j=0; j < nRubricas.get(i);j++) {
                for(modelo.Empresa e : listaEmpresa){
                    if(cnpjList.get(i).equals(e.getCnpj())) {
                        if(!list.contains(e.getCodigo()+"|"+codEmpregadoList.get(i)+"|"+competenciaList.get(i)+"|"+codRubricaList.get(rub)+"|"+valorRubricaList.get(rub))) {
                            list.add(e.getCodigo()+"|"+codEmpregadoList.get(i)+"|"+competenciaList.get(i)+"|"+codRubricaList.get(rub)+"|"+valorRubricaList.get(rub)); //Não duplicar
                            
                            Lancamento la = new Lancamento();
                            la.setCodEmpresa(e.getCodigo());
                            la.setCodEmpregado(codEmpregadoList.get(i));
                            la.setCompetencia(competenciaList.get(i));
                            la.setCodRubrica(codRubricaList.get(rub));
                            la.setValor(valorRubricaList.get(rub));
                            lancList.add(la);
                        }
                    }
                }
                rub++;
            }
        }
        
        lerArq.close();
        return lancList;
    }
}
