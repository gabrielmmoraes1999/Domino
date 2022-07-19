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
import java.util.StringTokenizer;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import modelo.DeParaRubrica;
import modelo.Empresa;
import modelo.EmpresaDAO;
import modelo.Lancamento;
import modelo.Layout;
import modelo.Rubrica;
import modelo.RubricaConfiguracao;
import modelo.RubricaDAO;
import modelo.RubricaUtilizada;
import modelo.RubricaUtilizadaDAO;

/**
 *
 * @author Gabriel Moraes
 */
public class SCI {
    public ArrayList buscaEmpresas(String origem) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        ArrayList<String> listaCnpj = new ArrayList<>();
        BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(origem), "Cp1252"));
        String linha = buffRead.readLine();
        while(linha != null) {
            if(linha.toLowerCase().contains("CNPJ".toLowerCase())){
                StringTokenizer st = new StringTokenizer(linha, " ");
                st.nextToken();
                String cnpj = st.nextToken();
                cnpj = cnpj.replaceAll("\\.", "");
                cnpj = cnpj.replaceAll("/", "");
                cnpj = cnpj.replaceAll("-", "");
                if(!listaCnpj.contains(cnpj)) {
                    listaCnpj.add(cnpj);
                }
            }
            linha = buffRead.readLine();
        }
        buffRead.close();
        return listaCnpj;
    }
    
    public ArrayList<Lancamento> retornarLancamentos(ArrayList<Empresa> listaEmpresa, String arquivoOrigem) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        ArrayList<Lancamento> lancList = new ArrayList<>();
        BufferedReader lerArq = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoOrigem), "Cp1252"));
        String linha = lerArq.readLine();
        boolean inicioFim = false;
        boolean codEmpregadoConf = false;
        boolean confCompetencia = false;
        int countEmpregado = 0;
        int contComp = 0;
        String codEmpresa = "";
        String codEmpregado = null;
        String competencia = null;
        
        ArrayList<Layout> list = new ArrayList<>();
        
        while (linha != null) {
            //Capiturar CNPJ
            if(linha.contains("CNPJ")) {
                String[] split = linha.split(" ");
                codEmpresa = split[1];
            }
            
            //Inicio Competencia
            if(linha.toLowerCase().contains("CNPJ".toLowerCase())){
                confCompetencia = true;
            }

            if(confCompetencia){
                contComp++;
            }

            if(contComp == 1){
                linha = linha.substring(linha.lastIndexOf(" ")+1);
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
                competencia = linha;
            }
                
            if(linha.toLowerCase().contains("Código Nome do funcionário C.C".toLowerCase())){
                contComp = 0;
                confCompetencia = false;
            }
            //Fim Competencia
            
            //Inicio Funcionario
            if(linha.toLowerCase().contains("Código Nome do funcionário".toLowerCase())){
                codEmpregadoConf = true;
            }

            if(codEmpregadoConf){
                countEmpregado++;
            }

            if(countEmpregado == 2){
                codEmpregado = linha.substring(0,linha.indexOf(" ")+1).replaceAll(" ", "");
            }
                
            if(linha.toLowerCase().contains("Admissão".toLowerCase())){
                countEmpregado= 0;
                codEmpregadoConf = false;
            }
            //Fim Funcionario
            
            // Inicio Rubricas
            if(linha.toLowerCase().contains("CÓDIGO DESCRIÇÕES REFERÊNCIAS".toLowerCase())){
                inicioFim = true;
                linha = linha.replaceAll("CÓDIGO DESCRIÇÕES REFERÊNCIAS PROVENTOS DESCONTOS", "");
            } else if(linha.toLowerCase().contains("Totais".toLowerCase())) {
                inicioFim = false;
            }
            
            if(inicioFim){
                int indexu = linha.lastIndexOf(" ");
		int indexp = linha.indexOf(" ");
                if(!"".equals(linha)){
                    Layout l = new Layout();
                    codEmpresa = codEmpresa.replaceAll("\\.", "");
                    codEmpresa = codEmpresa.replaceAll("/", "");
                    codEmpresa = codEmpresa.replaceAll("-", "");
                    l.setEmpresa(codEmpresa);
                    l.setEmpregado(codEmpregado);
                    l.setCompetencia(competencia);
                    l.setRubrica(linha.substring(0,indexp+1).replaceAll(" ", ""));
                    
                    //Capturar referencia
                    ArrayList<String> lista = new ArrayList<>();
        
                    for(String s : linha.split(" ")) {
                        if(!s.equals("")) {
                            lista.add(s);
                        }
                    }
                    
                    if(lista.get(lista.size() - 2).replaceAll("\\.", "").replaceAll(",", "").matches("\\d+$")) {
                        l.setReferencia(lista.get(lista.size() - 2));
                    }
                    
                    l.setValor(linha.substring(indexu+1));
                    list.add(l);
                }
            }
            //Fim Rubrica
            linha = lerArq.readLine();
        }
        lerArq.close();
        
        ArrayList<String> temp = new ArrayList<>();
        
        for(Layout l : list) {
            for(Empresa e : listaEmpresa) {
                if(l.getEmpresa().equals(e.getCnpj())) {
                    if(!temp.contains(e.getCodigo()+"|"+l.getEmpregado()+"|"+l.getCompetencia()+"|"+l.getRubrica()+"|"+l.getValor())) {
                        temp.add(e.getCodigo()+"|"+l.getEmpregado()+"|"+l.getCompetencia()+"|"+l.getRubrica()+"|"+l.getValor()); //Não duplicar
                        
                        Lancamento la = new Lancamento();
                        la.setCodEmpresa(e.getCodigo());
                        la.setCodEmpregado(l.getEmpregado());
                        la.setCompetencia(l.getCompetencia());
                        la.setCodRubrica(l.getRubrica());
                        la.setReferencia(l.getReferencia());
                        la.setValor(l.getValor());
                        lancList.add(la);
                    }
                }
            }
        }
        
        return lancList;
    }
    
    public ArrayList retornarRubricasExcel(ArrayList<String> arquivosExcel, String caminho) throws IOException, BiffException {
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
                Cell col1row2 = sheet.getCell(2,i); //Descrição
                Cell col1row3 = sheet.getCell(3,i); //Tipo
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
        
        for(RubricaUtilizada ru : ruList) {
            if(codEmpresa != ru.getCodEmpresa()) {
                codEmpresa = ru.getCodEmpresa();
                cnpj = eDAO.retornaCNPJ(ru.getCodEmpresa(), idConversao);
                dataS = eDAO.retornaData(ru.getCodEmpresa(), idConversao);
            }
            
            for(DeParaRubrica dpr : rDAO.retornaDeParaRubrica(ruList)) {
                if(ru.getCodRubrica().equals(dpr.getOrigem()) && ru.getCodEmpresa() == dpr.getCodEmpresa()) {
                    //Carregar Dados da Rubrica
                    Rubrica r = rDAO.carregarPorConversaoEmpresaCodigo(cnpj, dpr.getOrigem(), idConversao);
                    if(r != null) {
                        if(r.getTipo().toLowerCase().equals("desconto") || r.getTipo().toLowerCase().contains("desconto")) { //D
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
                        } else if(r.getTipo().toLowerCase().equals("provento") || r.getTipo().toLowerCase().contains("provento")) { //P
                            //IF para verificar se a rubrica é de Hora Extras
                            if((r.getDescricao().toLowerCase().contains("hora") && r.getDescricao().toLowerCase().contains("extra")) && !r.getDescricao().contains("D.S.R.")) {
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
                            }
                        }
                    } else {
                        
                    }
                }
            }
        }
        return rcList;
    }
}
