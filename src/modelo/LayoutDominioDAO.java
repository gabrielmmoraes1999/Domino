package modelo;

import java.util.ArrayList;
import java.util.Calendar;
import util.Config;
import util.Sistema;

/**
 *
 * @author Gabriel Moraes
 */
public class LayoutDominioDAO {
    
    public ArrayList<String> zeramento(String dataInicio, String dataFim) {
        ArrayList<String> linhas = new ArrayList<>();
        
        EmpregadoDAO eDAO = new EmpregadoDAO();
        Sistema s = new Sistema();
        
        int countInicio = 0;
        Calendar cInicio = Calendar.getInstance();
        cInicio.set(Calendar.DAY_OF_MONTH, 01);
        for(String c:dataInicio.split("/")) {
            if(countInicio == 0) {
                cInicio.set(Calendar.MONTH, Integer.parseInt(c));
            } else {
                cInicio.set(Calendar.YEAR, Integer.parseInt(c));
            }
            countInicio++;
        }
        
        int countFim = 0;
        Calendar cFim = Calendar.getInstance();
        cFim.set(Calendar.DAY_OF_MONTH, 01);
        for(String c:dataFim.split("/")) {
            if(countFim == 0) {
                cFim.set(Calendar.MONTH, Integer.parseInt(c));
            } else {
                cFim.set(Calendar.YEAR, Integer.parseInt(c));
            }
            countFim++;
        }
        
        int difMes = cFim.get(Calendar.MONTH) - cInicio.get(Calendar.MONTH);  
        int difAno = ((cFim.get(Calendar.YEAR) - cInicio.get(Calendar.YEAR))*12);  
        
        int total = difAno+difMes;
        
        int mesCont = cInicio.get(Calendar.MONTH);
        int ano = cInicio.get(Calendar.YEAR);
        
        for(int i = 0; i <= total; i++) {
            if(mesCont == 13) {
                ano++;
                mesCont = 01;
            }
            
            //For de 
            for(Empregado e : eDAO.carregarPorConversao(Config.conversao.getId())) {
                linhas.add("10"+s.completeToLeft(String.valueOf(e.getCodigo()), '0', 10)+ano+s.completeToLeft(String.valueOf(mesCont), '0', 2)+"000111000000000"+s.completeToLeft(String.valueOf(e.getCodEmpresa()), '0', 10));
                linhas.add("10"+s.completeToLeft(String.valueOf(e.getCodigo()), '0', 10)+ano+s.completeToLeft(String.valueOf(mesCont), '0', 2)+"878111000000000"+s.completeToLeft(String.valueOf(e.getCodEmpresa()), '0', 10));
                linhas.add("10"+s.completeToLeft(String.valueOf(e.getCodigo()), '0', 10)+ano+s.completeToLeft(String.valueOf(mesCont), '0', 2)+"099511000000000"+s.completeToLeft(String.valueOf(e.getCodEmpresa()), '0', 10));
                linhas.add("10"+s.completeToLeft(String.valueOf(e.getCodigo()), '0', 10)+ano+s.completeToLeft(String.valueOf(mesCont), '0', 2)+"938011000000000"+s.completeToLeft(String.valueOf(e.getCodEmpresa()), '0', 10));
            }
            
            mesCont++;
        }
        
        return linhas;
    }
    
    
    public ArrayList<String> retornaLancamentosPreparados(ArrayList<LayoutDominio> ldList) {
        ArrayList<String> linhas = new ArrayList<>();
        
        Sistema s = new Sistema();
        
        ldList.forEach((ld) -> {
            String competencia = ld.getCompetencia();
            competencia = competencia.replaceAll("/", "");
            competencia = competencia.replaceAll("-", "");
            String mes = competencia.substring(0, 2);
            String ano = competencia.substring(2, 6);
            
            String valor = ld.getValor();
            valor = valor.replaceAll(",", "");
            valor = valor.replaceAll("\\.", "");
            
            linhas.add("10"+s.completeToLeft(String.valueOf(ld.getCodEmpregado()), '0', 10)+ano+mes+s.completeToLeft(ld.getCodRubrica(), '0', 4)+"11"+s.completeToLeft(valor, '0', 9)+s.completeToLeft(String.valueOf(ld.getCodEmpresa()), '0', 10));
        });
        
        return linhas;
    }
}
