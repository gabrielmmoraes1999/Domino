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
import java.util.Iterator;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import modelo.Conversao;
import modelo.DeParaRubrica;
import modelo.Empregado;
import modelo.EmpregadoDAO;
import modelo.Empresa;
import modelo.EmpresaDAO;
import modelo.Lancamento;
import modelo.Layout;
import modelo.Rubrica;
import modelo.RubricaConfiguracao;
import modelo.RubricaDAO;
import modelo.RubricaNaoLocalizada;
import modelo.RubricaNaoLocalizadaDAO;
import modelo.RubricaUtilizada;
import modelo.RubricaUtilizadaDAO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.Sistema;

/**
 *
 * @author Gabriel Moraes
 */
public class Ledware {

    public ArrayList buscaEmpresas(String origem) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        ArrayList<String> listaCnpj = new ArrayList<>();
        BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(origem), "Cp1252"));
        String linha = buffRead.readLine();

        int countInicial = 1;
        boolean segundo = false;
        String linhaInicial = "Recibo de Pagamento de Salário";
        int count = 1;
        boolean capturarRubricas = false;

        while (linha != null) {
            if (linha.equals(linhaInicial)) {
                capturarRubricas = false;
                if (countInicial++ == 2) {
                    segundo = true;
                    count = 1;
                }
            }
            if (segundo) {
                switch (count) {
                    case 17:
                        if (!listaCnpj.contains(linha)) {
                            listaCnpj.add(linha);
                        }
                        break;
                    default:
                        break;
                }
            }
            if (linha.equals("Função")) {
                buffRead.readLine();
                buffRead.readLine();
                capturarRubricas = true;
            }

            if (capturarRubricas) {
                countInicial = 1;
            }
            linha = buffRead.readLine();
            count++;
        }

        buffRead.close();
        return listaCnpj;
    }

    public ArrayList<Lancamento> retornarLancamentos(ArrayList<Empresa> listaEmpresa, String arquivoOrigem, int idConversao) throws FileNotFoundException, UnsupportedEncodingException, IOException, Exception {
        ArrayList<Lancamento> lancList = new ArrayList<>();
        ArrayList<Layout> list = new ArrayList<>();

        BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoOrigem), "Cp1252"));
        String linha = buffRead.readLine();

        int countInicial = 1;
        boolean segundo = false;
        String linhaInicial = "Recibo de Pagamento de Salário";

        int count = 1;

        //Variavel para verificar iniciar coleta das Rubricas
        boolean capturarRubricas = false;

        //Variaves
        String nome = null;
        String cnpj = null;
        String competencia = null;

        while (linha != null) {
            //Pegar a segunda linha inicial
            if (linha.equals(linhaInicial)) {
                capturarRubricas = false;
                if (countInicial++ == 2) {
                    segundo = true;
                    count = 1;
                }
            }

            //Resetar
            //Executar segunda linha inicial
            if (segundo) {
                switch (count) {
                    case 7:
                        nome = linha;
                        break;
                    case 17:
                        cnpj = linha;
                        break;
                    case 18:
                        competencia = linha;
                        break;
                    default:
                        break;
                }
            }

            //Capturar rubricas
            if (linha.equals("Função")) {
                buffRead.readLine();
                linha = buffRead.readLine();
                capturarRubricas = true;
            }

            if (capturarRubricas) {
                Layout l = new Layout();
                l.setEmpresa(cnpj);
                l.setEmpregado(nome);
                l.setCompetencia(competencia);
                l.setRubrica(linha.split(" ")[0]);
                
                if (linha.contains("HORA EXTRA")) {
                    l.setValor(linha.split(" ")[linha.split(" ").length - 2]);
                } else {
                    l.setValor(linha.split(" ")[linha.split(" ").length - 1]);
                }
                
                list.add(l);
                countInicial = 1;
            }
            linha = buffRead.readLine();
            count++;
        }

        EmpresaDAO eDAO = new EmpresaDAO();
        ArrayList<Empresa> empList = eDAO.carregarPorConversao(idConversao);

        EmpregadoDAO emDAO = new EmpregadoDAO();
        ArrayList<Empregado> eList = emDAO.carregarPorConversao(idConversao);

        ArrayList<String> empregadoLocalizado = new ArrayList<>();

        //Retorna com os empregados localizados
        for (Layout l : list) {
            for (Empresa e : empList) {
                if (l.getEmpresa().equals(e.getCnpj())) {
                    for (Empregado em : eList) {
                        if (em.getCodEmpresa() == e.getCodigo() && em.getNome().equals(l.getEmpregado())) {
                            if (!empregadoLocalizado.contains(e.getCodigo() + "|" + l.getEmpregado())) {
                                empregadoLocalizado.add(e.getCodigo() + "|" + l.getEmpregado());
                            }
                        }
                    }
                }
            }
        }

        ArrayList<String> empregadoNaoLocalizado = new ArrayList<>();

        //Retorna com os empregados nao localizados
        for (Layout l : list) {
            for (Empresa e : empList) {
                if (l.getEmpresa().equals(e.getCnpj())) {
                    for (Empregado em : eList) {
                        if (em.getCodEmpresa() == e.getCodigo()) {
                            if (!empregadoLocalizado.contains(e.getCodigo() + "|" + l.getEmpregado())) {
                                if (!empregadoNaoLocalizado.contains(e.getCodigo() + "|" + l.getEmpregado())) {
                                    empregadoNaoLocalizado.add(e.getCodigo() + "|" + l.getEmpregado());
                                }
                            }
                        }
                    }
                }
            }
        }

        ArrayList<String> temp = new ArrayList<>();

        for (Layout l : list) {
            for (Empresa e : empList) {
                if (l.getEmpresa().equals(e.getCnpj())) {

                    for (Empregado em : eList) {
                        if (em.getCodEmpresa() == e.getCodigo() && em.getNome().equals(l.getEmpregado())) {
                            //Não deixar duplicar
                            if (!temp.contains(e.getCodigo() + "|" + em.getCodigo() + "|" + l.getCompetencia() + "|" + l.getRubrica() + "|" + l.getValor())) {
                                temp.add(e.getCodigo() + "|" + em.getCodigo() + "|" + l.getCompetencia() + "|" + l.getRubrica() + "|" + l.getValor());
                                Lancamento la = new Lancamento();
                                la.setCodEmpresa(e.getCodigo());
                                la.setCodEmpregado(String.valueOf(em.getCodigo()));
                                la.setCompetencia(l.getCompetencia());
                                la.setCodRubrica(l.getRubrica());
                                la.setValor(l.getValor());
                                lancList.add(la);
                            }
                            break;
                        } else if (em.getCodEmpresa() == e.getCodigo() && empregadoNaoLocalizado.contains(e.getCodigo() + "|" + l.getEmpregado())) {
                            float acerto = (float) 0.75; //Margem de acerto
                            if (acerto < Sistema.checkSimilarity(l.getEmpregado(), em.getNome())) {
                                //Não deixar duplicar
                                if (!temp.contains(e.getCodigo() + "|" + em.getCodigo() + "|" + l.getCompetencia() + "|" + l.getRubrica() + "|" + l.getValor())) {
                                    temp.add(e.getCodigo() + "|" + em.getCodigo() + "|" + l.getCompetencia() + "|" + l.getRubrica() + "|" + l.getValor());
                                    Lancamento la = new Lancamento();
                                    la.setCodEmpresa(e.getCodigo());
                                    la.setCodEmpregado(String.valueOf(em.getCodigo()));
                                    la.setCompetencia(l.getCompetencia());
                                    la.setCodRubrica(l.getRubrica());
                                    la.setValor(l.getValor());
                                    lancList.add(la);
                                }
                                break;
                            }
                        }
                    }
                    break;

                }
            }
        }

        return lancList;
    }
    
    //Versão XLSX
    public ArrayList<Rubrica> retornarRubricasXLSX(ArrayList<String> arquivosExcel, String caminho) throws IOException {
        ArrayList<Rubrica> lista = new ArrayList<>();
        
        for (String arqExcel : arquivosExcel) {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(new File(caminho + "\\" + arqExcel)));
            XSSFSheet sheet = wb.getSheetAt(0);
            Iterator<Row> itr = sheet.iterator();

            String cnpj = null;
            boolean rubrica = false;

            while (itr.hasNext()) { //While de Linhas do Excel
                Row row = itr.next();

                if (row.getCell(0) != null) {

                    if (row.getCell(0).getStringCellValue().contains("CNPJ/CPF/CEI")) {
                        cnpj = row.getCell(7).getStringCellValue();
                    }

                    if (row.getCell(0).getStringCellValue().equals("")) {
                        rubrica = false;
                    }

                    if (rubrica) {
                        Rubrica r = new Rubrica();
                        r.setCnpj(cnpj);
                        r.setCodigo(row.getCell(0).getStringCellValue());
                        r.setDescricao(row.getCell(3).getStringCellValue());
                        r.setTipo(row.getCell(11).getStringCellValue());
                        lista.add(r);
                    }

                    if (row.getCell(0).getStringCellValue().equals("Código")) {
                        rubrica = true;
                    }
                }
            }
        }
        return lista;
    }

    //Versão XLS
    public ArrayList retornarRubricasExcel(ArrayList<String> arquivosExcel, String caminho) throws IOException, BiffException {
        ArrayList<Rubrica> rubricas = new ArrayList<>();

        for (String arqExcel : arquivosExcel) {
            WorkbookSettings ws = new WorkbookSettings();
            ws.setEncoding("Cp1252");
            Workbook wrk = Workbook.getWorkbook(new File(caminho + "\\" + arqExcel), ws);
            Sheet sheet = wrk.getSheet(0);
            int linhas = sheet.getRows();
            //String cnpj = arqExcel.replaceAll(".xls", "");
            String cnpj = null;

            for (int i = 1; i < linhas; i++) {
                Rubrica r = new Rubrica();
                Cell col1row1 = sheet.getCell(0, i); //Codigo
                Cell col1row3 = sheet.getCell(3, i); //Descrição
                Cell col1row12 = sheet.getCell(12, i); //Tipo

                if ("CNPJ/CPF/CEI ".equals(col1row1.getContents())) {
                    Cell col1row7 = sheet.getCell(7, i); //CNPJ
                    cnpj = col1row7.getContents();
                }

                if (!"".equals(col1row1.getContents()) && col1row1.getContents().matches("[+-]?\\d*(\\.\\d+)?")) {
                    r.setCnpj(cnpj);
                    r.setCodigo(col1row1.getContents());
                    r.setDescricao(col1row3.getContents());
                    r.setTipo(col1row12.getContents());
                    rubricas.add(r);
                }
            }

        }

        return rubricas;
    }
    
    public ArrayList<RubricaConfiguracao> retornaConfiguracaoRubrica(int idConversao) {
        RubricaDAO rDAO = new RubricaDAO();
        EmpresaDAO eDAO = new EmpresaDAO();
        RubricaUtilizadaDAO ruDAO = new RubricaUtilizadaDAO();
        
        //Classes
        RubricaNaoLocalizadaDAO rnlDAO = new RubricaNaoLocalizadaDAO();
        
        //Rubricas Não Localizada
        ArrayList<RubricaNaoLocalizada> listaRNL = new ArrayList<>();
        
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
                    
                    //Não gravar quando nao houver Rubrica
                    if(r.getTipo() != null) {
                        if(r.getTipo().equals("D")) { //Desconto
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
                        } else if(r.getTipo().equals("P") && !r.getDescricao().toUpperCase().contains("Hora Extra".toUpperCase())) { //Provento
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
                        } else if(r.getTipo().equals("P") && r.getDescricao().toUpperCase().contains("Hora Extra ".toUpperCase())) { //Rubrica de Horas Extras
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
