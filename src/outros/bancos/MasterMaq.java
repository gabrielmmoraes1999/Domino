package outros.bancos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import modelo.Empresa;
import modelo.Lancamento;
import modelo.Layout;

/**
 *
 * @author Gabriel Moraes
 */
public class MasterMaq {
    public ArrayList buscaEmpresas(String origem) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        ArrayList<String> listaCnpj = new ArrayList<String>();
        BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(origem), "Cp1252"));
        String linha = buffRead.readLine();
        
        //Adicionar CNPJ na lista
        boolean ifCnpj = false;
        int count = 0;
        int countCnpj = 0;
        
        while (linha != null) {
            count++;
            //Inicio CNPJ
            if(linha.contains("Total: Total:")) {
                ifCnpj = true;
            }
            
            if(ifCnpj || count == 2) {
                countCnpj++;
                if(countCnpj == 3 || count == 2) {
                    linha = linha.replaceAll("-", "");
                    linha = linha.replaceAll("/", "");
                    linha = linha.replaceAll("\\.", "");
                    if(!listaCnpj.contains(linha)) {
                        listaCnpj.add(linha);
                    }
                }
            }
            
            if(linha.equals("Depto.:")) {
                countCnpj = 0;
                ifCnpj = false;
            }
            //Fim CNPJ
            linha = buffRead.readLine();
        }
        
        buffRead.close();
        return listaCnpj;
    }
    
    public ArrayList<Lancamento> retornarLancamentos(ArrayList<Empresa> listaEmpresa, String arquivoOrigem) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        ArrayList<Lancamento> lancList = new ArrayList<Lancamento>();
        BufferedReader lerArq = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoOrigem), "Cp1252"));
        String linha = lerArq.readLine();
        boolean inicioFim = false;
        boolean codEmpregadoConf = false;
        boolean confCompetencia = false;
        int countEmpregado = 0;
        int contComp = 0;
        String empregadoWhile = null;
        String competenciaWhile = null;
        
        ArrayList<Layout> list = new ArrayList<Layout>();
        
        //Adicionar CNPJ na lista
        boolean ifCnpj = false;
        int count = 0;
        int countCnpj = 0;
        String cnpj = null;
        
        while (linha != null) {
            count++;
            //Inicio CNPJ
            if(linha.contains("Total: Total:")) {
                ifCnpj = true;
            }
            
            if(ifCnpj || count == 2) {
                countCnpj++;
                if(countCnpj == 3 || count == 2) {
                    linha = linha.replaceAll("-", "");
                    linha = linha.replaceAll("/", "");
                    linha = linha.replaceAll("\\.", "");
                    cnpj = linha;
                    //System.out.println(linha);
                    //if(!listaCnpj.contains(linha)) {
                        //listaCnpj.add(linha);
                    //}
                }
            }
            
            if(linha.equals("Depto.:")) {
                countCnpj = 0;
                ifCnpj = false;
            }
            //Fim CNPJ
            
            //Inicio Competencia
            if(linha.toLowerCase().contains("Cargo:".toLowerCase())){
                confCompetencia = true;
            }

            if(confCompetencia){
                contComp++;
            }

            if(contComp == 2){
                competenciaWhile = linha;
            }
                
            if(linha.toLowerCase().contains("Referência Vencimentos".toLowerCase())){
                contComp = 0;
                confCompetencia = false;
            }
            //Fim Competencia
            
            //Inicio Funcionario
            if(linha.toLowerCase().contains("Depto.:".toLowerCase())){
                codEmpregadoConf = true;
            }

            if(codEmpregadoConf){
                countEmpregado++;
            }

            if(countEmpregado == 2){
                linha = linha.substring(0,linha.indexOf(" ")+1).replaceAll(" ", "");
                empregadoWhile = linha;
            }
                
            if(linha.toLowerCase().contains("Func.:".toLowerCase())){
                countEmpregado= 0;
                codEmpregadoConf = false;
            }
            //Fim Funcionario
            
            // Inicio Rubricas
            if(linha.toLowerCase().contains("Verbas".toLowerCase())){
                inicioFim = true;
                linha = linha.replaceAll("Verbas", "");
            } else if(linha.toLowerCase().contains("Valor Líquido".toLowerCase())) {
                inicioFim = false;
            }
            
            if(inicioFim){
                //int indexu = linha.lastIndexOf(" ");
		//int indexp = linha.indexOf(" ");
                if(!"".equals(linha)) {
                    //empresaList.add(codEmpresa);
                    //System.out.println(cnpj+"|"+empregadoWhile+"|"+competenciaWhile+"|"+linha.substring(0,indexp+1).replaceAll(" ", "")+"|"+linha.substring(indexu+1));
                    String[] split = linha.split(" ");
                    
                    Layout l = new Layout();
                    l.setEmpresa(cnpj);
                    l.setEmpregado(empregadoWhile);
                    l.setCompetencia(competenciaWhile);
                    l.setRubrica(split[0]);
                    if(linha.contains("Hora Extra ")) {
                        l.setValor(split[split.length - 2]);
                    } else {
                        l.setValor(split[split.length - 1]);
                    }
                    list.add(l);
                }
            }
            //Fim Rubrica
            linha = lerArq.readLine();
        }
        
        ArrayList<String> temp = new ArrayList<String>();
        
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
                        la.setValor(l.getValor());
                        lancList.add(la);
                        //System.out.println(e.getCodigo()+"|"+l.getEmpregado()+"|"+l.getCompetencia()+"|"+l.getRubrica()+"|"+l.getValor());
                    }
                }
            }
        }
        
        lerArq.close();
        return lancList;
    }
    
    public ArrayList<Layout> preparaLancamentos(String arquivo, String codEmpresa) throws IOException{
        ArrayList<Layout> listL = new ArrayList<Layout>();
        ArrayList<String> empresaList = new ArrayList<String>();
        ArrayList<String> empregadoList = new ArrayList<String>();
        ArrayList<String> competenciaList = new ArrayList<String>();
        ArrayList<String> rubricaList = new ArrayList<String>();
        ArrayList<String> valorList = new ArrayList<String>();
        
        FileReader arq = new FileReader(arquivo);
        BufferedReader lerArq = new BufferedReader(arq);
        String linha = lerArq.readLine();
        boolean inicioFim = false;
        boolean codEmpregadoConf = false;
        boolean confCompetencia = false;
        int countEmpregado = 0;
        int contComp = 0;
        String empregadoWhile = null;
        String competenciaWhile = null;
        
        while (linha != null) {
            //Inicio Competencia
            if(linha.toLowerCase().contains("Cargo:".toLowerCase())){
                confCompetencia = true;
            }

            if(confCompetencia){
                contComp++;
            }

            if(contComp == 2){
                competenciaWhile = linha;
            }
                
            if(linha.toLowerCase().contains("Referência Vencimentos".toLowerCase())){
                contComp = 0;
                confCompetencia = false;
            }
            //Fim Competencia
            
            //Inicio Funcionario
            if(linha.toLowerCase().contains("Depto.:".toLowerCase())){
                codEmpregadoConf = true;
            }

            if(codEmpregadoConf){
                countEmpregado++;
            }

            if(countEmpregado == 2){
                linha = linha.substring(0,linha.indexOf(" ")+1).replaceAll(" ", "");
                empregadoWhile = linha;
            }
                
            if(linha.toLowerCase().contains("Func.:".toLowerCase())){
                countEmpregado= 0;
                codEmpregadoConf = false;
            }
            //Fim Funcionario
            
            // Inicio Rubricas
            if(linha.toLowerCase().contains("Verbas".toLowerCase())){
                inicioFim = true;
                linha = linha.replaceAll("Verbas", "");
            } else if(linha.toLowerCase().contains("Valor Líquido".toLowerCase())) {
                inicioFim = false;
            }
            
            if(inicioFim){
                int indexu = linha.lastIndexOf(" ");
		int indexp = linha.indexOf(" ");
                if(!"".equals(linha)){
                    empresaList.add(codEmpresa);
                    empregadoList.add(empregadoWhile);
                    competenciaList.add(competenciaWhile);
                    rubricaList.add(linha.substring(0,indexp+1).replaceAll(" ", ""));
                    valorList.add(linha.substring(indexu+1));
                }
            }
            //Fim Rubrica
            linha = lerArq.readLine();
        }
        
        arq.close();
        lerArq.close();
        
        int dsf = 0;
        for(String r: rubricaList){
            Layout l = new Layout();
            l.setEmpresa(empresaList.get(dsf));
            l.setEmpregado(empregadoList.get(dsf));
            l.setCompetencia(competenciaList.get(dsf));
            l.setRubrica(r);
            l.setValor(valorList.get(dsf));
            listL.add(l);
            dsf++;
        }
        return listL;
    }
    
    public void gravarTemp(String arquivo, String destinoTemp, String codEmpresa) throws IOException{
        String separador = "|";
        ArrayList<Layout> list = new ArrayList<Layout>();
        FileWriter arq = new FileWriter(destinoTemp);
        PrintWriter gravarArq = new PrintWriter(arq);
        list = this.preparaLancamentos(arquivo,codEmpresa);
        
        for(Layout paFor: list){
            gravarArq.printf(paFor.getEmpresa()+separador+paFor.getEmpregado()+separador+paFor.getCompetencia()+separador+paFor.getRubrica()+separador+paFor.getValor()+"%n");
        }
        
        arq.close();
    }
    
    public void gravarTempEmpresas(ArrayList<Empresa> listaEmpresa, String arquivoOrigem, String destinoTemp) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<Layout> lancamento = new ArrayList<Layout>();
        
        BufferedReader lerArq = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoOrigem), "Cp1252"));
        String linha = lerArq.readLine();
        boolean inicioFim = false;
        boolean codEmpregadoConf = false;
        boolean confCompetencia = false;
        boolean ifCnpj = false;
        int count = 0;
        int countCnpj = 0;
        int countEmpregado = 0;
        int contComp = 0;
        String cnpj = null;
        String empregadoWhile = null;
        String competenciaWhile = null;
        
        while (linha != null) {
            count++;
            //Inicio CNPJ
            if(linha.contains("Total: Total:")) {
                ifCnpj = true;
            }
            
            if(ifCnpj || count == 2) {
                countCnpj++;
                if(countCnpj == 3 || count == 2) {
                    linha = linha.replaceAll("-", "");
                    linha = linha.replaceAll("/", "");
                    linha = linha.replaceAll("\\.", "");
                    cnpj = linha;
                }
            }
            
            if(linha.equals("Depto.:")) {
                countCnpj = 0;
                ifCnpj = false;
            }
            //Fim CNPJ
            
            //Inicio Competencia
            if(linha.toLowerCase().contains("Cargo".toLowerCase())){
                confCompetencia = true;
            }

            if(confCompetencia) {
                contComp++;
            }

            if(contComp == 2) {
                competenciaWhile = linha;
            }
                
            if(linha.toLowerCase().contains("Referência Vencimentos".toLowerCase())){
                contComp = 0;
                confCompetencia = false;
            }
            //Fim Competencia
            
            //Inicio Funcionario
            if(linha.toLowerCase().contains("Depto.:".toLowerCase())){
                codEmpregadoConf = true;
            }

            if(codEmpregadoConf){
                countEmpregado++;
            }

            if(countEmpregado == 2){
                linha = linha.substring(0,linha.indexOf(" ")+1).replaceAll(" ", "");
                empregadoWhile = linha;
            }
                
            if(linha.toLowerCase().contains("Func.:".toLowerCase())){
                countEmpregado= 0;
                codEmpregadoConf = false;
            }
            //Fim Funcionario
            
            // Inicio Rubricas
            if(linha.toLowerCase().contains("Verbas".toLowerCase())){
                inicioFim = true;
                linha = linha.replaceAll("Verbas", "");
            } else if(linha.toLowerCase().contains("Valor Líquido".toLowerCase())) {
                inicioFim = false;
            }
            
            if(inicioFim){
                int indexu = linha.lastIndexOf(" ");
		int indexp = linha.indexOf(" ");
                if(!"".equals(linha)){
                    Layout l = new Layout();
                    l.setEmpresa(cnpj);
                    l.setEmpregado(empregadoWhile);
                    l.setCompetencia(competenciaWhile);
                    String codRubrica = linha.substring(0,indexp+1).replaceAll(" ", "");
                    l.setRubrica(codRubrica);
                    String valor = linha.substring(indexu+1);
                    l.setValor(valor);
                    lancamento.add(l);
                }
            }
            //Fim Rubrica
            linha = lerArq.readLine();
        }
        lerArq.close();
        
        for(Layout l : lancamento) {
            for(Empresa e : listaEmpresa) {
                if(l.getEmpresa().equals(e.getCnpj())) {
                    if(!list.contains(e.getCodigo()+"|"+l.getEmpregado()+"|"+l.getCompetencia()+"|"+l.getRubrica()+"|"+l.getValor())) {
                        list.add(e.getCodigo()+"|"+l.getEmpregado()+"|"+l.getCompetencia()+"|"+l.getRubrica()+"|"+l.getValor());
                    }
                }
            }
        }
        
        FileWriter arqW = new FileWriter(destinoTemp);
        PrintWriter gravarArq = new PrintWriter(arqW);
        
        for(String l : list){
            gravarArq.println(l);
        }
        
        arqW.close();
    }
}
