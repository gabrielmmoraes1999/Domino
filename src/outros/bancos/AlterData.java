package outros.bancos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import modelo.Empresa;
import modelo.Lancamento;
import modelo.Layout;
import util.Sistema;

/**
 *
 * @author Gabriel Moraes
 */
public class AlterData {
    public ArrayList buscaEmpresas(String origem) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        ArrayList<String> lista = new ArrayList<>();
        boolean competencia = false;
        int countCompetencia = 0;
        String cnpj = null;
        
        BufferedReader lerArq = new BufferedReader(new InputStreamReader(new FileInputStream(origem), "Cp1252"));
        String linha = lerArq.readLine();
        
        while(linha != null) {
            if(linha.contains("Cód. Descrição Referência")) {
                competencia = true;
            }
            
            if(competencia) {
                countCompetencia++;
            }
            
            if(countCompetencia == 4) {
                String[] split = linha.split(" ");
                cnpj = split[split.length - 1].replaceAll("a", "");
                cnpj = cnpj.replaceAll("-", "");
                cnpj = cnpj.replaceAll("/", "");
                cnpj = cnpj.replaceAll("\\.", "");
            }
            
            if(linha.contains("Demonstrativo")){
                competencia = false;
                countCompetencia = 0;
            }
            
            if(!lista.contains(cnpj) && cnpj != null) {
                lista.add(cnpj);
            }
            
            linha = lerArq.readLine();
        }
        lerArq.close();
        return lista;
    }
    
    public ArrayList<Lancamento> retornarLancamentos(ArrayList<Empresa> listaEmpresa, String arquivoOrigem) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        ArrayList<Lancamento> lancList = new ArrayList<>();
        BufferedReader lerArq = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoOrigem), "Cp1252"));
        
        String linha = lerArq.readLine();
        
        boolean primeiraLinha = true;
        boolean competencia = false;
        int countCompetencia = 0;
        boolean lanc = false;
        boolean campEmpregado = false;
        
        ArrayList<Layout> list = new ArrayList<>();
        
        String codEmpregado = null;
        String competenciaTemp = null;
        String cnpj = null;
        
        while(linha != null) {
            //Inicio Codigo do Empregado
            if(primeiraLinha) {
                codEmpregado = linha.replaceAll("[^0-9]", "");
            }
            
            if(linha.equals("DATA ASSINATURA DO FUNCIONÁRIO")){
                campEmpregado = true;
            }
            
            if(campEmpregado) {
                if(!linha.equals("DATA ASSINATURA DO FUNCIONÁRIO")) {
                    if(!linha.replaceAll(",", "").matches("^\\d+$")){
                        if(!linha.contains("Cód. Descrição Referência")){
                            codEmpregado = linha.replaceAll("[^0-9]", "");
                        }
                    }
                }
            }
            //Fim Codigo do Empregado
            
            //Inicio Competencia e CNPJ
            if(linha.contains("Cód. Descrição Referência")) {
                campEmpregado = false;
                competencia = true;
            }
            
            if(competencia) {
                countCompetencia++;
            }
            
            if(countCompetencia == 4) {
                String[] split = linha.split(" ");
                String comCNPJ = linha;
                competenciaTemp = comCNPJ.substring(3, 10);
                cnpj = split[split.length - 1].replaceAll("a", "");
                cnpj = cnpj.replaceAll("-", "");
                cnpj = cnpj.replaceAll("/", "");
                cnpj = cnpj.replaceAll("\\.", "");
            }
            
            if(linha.contains("Demonstrativo")){
                competencia = false;
                countCompetencia = 0;
            }
            //Fim Competencia e CNPJ
            
            
            
            //Inicia Rubricas
            if(linha.contains("Demonstrativo de Pagamento")){
                lanc = true;
            }
            
            if(lanc) {
                String temp = linha;
                temp = temp.replaceAll(",", "");
                temp = temp.replaceAll("\\.", "");
                temp = temp.replaceAll(" ", "");
                lanc = !temp.matches("^\\d+$");
            }
            
            if(linha.equals("Valor Líquido")) {
                //lanc = false;
            }
            
            if(lanc) {
                if(!linha.contains("Demonstrativo de Pagamento")){
                    String[] split = linha.split(" ");
                    Layout l = new Layout();
                    l.setEmpresa(cnpj);
                    l.setEmpregado(codEmpregado);
                    l.setCompetencia(competenciaTemp);
                    l.setRubrica(new Sistema().completeToLeft(split[0], '0', 4));
                    l.setValor(split[split.length - 1]);
                    list.add(l);
                }
            }
            //Fim Rubricas
            
            primeiraLinha = false;
            linha = lerArq.readLine();
        }
        
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
                        la.setValor(l.getValor());
                        lancList.add(la);
                    }
                }
            }
        }
        
        return lancList;
    }
}
