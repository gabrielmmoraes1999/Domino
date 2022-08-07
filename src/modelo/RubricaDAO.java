package modelo;

import database.Firebird;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import view.TelaErro;

/**
 *
 * @author Gabriel Moraes
 */
public class RubricaDAO {
    public void inserir(ArrayList<Rubrica> rub, int idConversao) {
        try {
            String sql = "INSERT INTO RUBRICA (ID_CONVERSAO, CNPJ, CODIGO, DESCRICAO, NATUREZA, TIPO) VALUES(?,?,?,?,?,?)";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            
            for(Rubrica r : rub) {
                pstm.setInt(1, idConversao);
                pstm.setString(2, r.getCnpj());
                pstm.setString(3, r.getCodigo());
                pstm.setString(4, r.getDescricao());
                pstm.setString(5, r.getNatureza());
                pstm.setString(6, r.getTipo());
                pstm.execute();
            }
            
            Firebird.conn.commit();
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }
    
    public ArrayList<Rubrica> carregarPorConversao(int idConversao) {
        ArrayList<Rubrica> rub = new ArrayList<>();
        try {
            String sql = "SELECT * FROM RUBRICA WHERE ID_CONVERSAO = ? ORDER BY ID ASC";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            ResultSet rs = pstm.executeQuery();
            
            while (rs.next()) {
                Rubrica r = new Rubrica();
                r.setId(rs.getInt("ID"));
                r.setCnpj(rs.getString("CNPJ"));
                r.setCodigo(rs.getString("CODIGO"));
                r.setDescricao(rs.getString("DESCRICAO"));
                r.setNatureza(rs.getString("NATUREZA"));
                r.setTipo(rs.getString("TIPO"));
                rub.add(r);
            }
            
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return rub;
    }
    
    public Rubrica carregarPorConversaoEmpresaCodigo(String cnpj, String codigo, int idConversao) {
        Rubrica r = new Rubrica();
        try {
            String sql = "SELECT * FROM RUBRICA WHERE CNPJ = ? AND CODIGO = ? AND ID_CONVERSAO = ? ORDER BY ID ASC";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setString(1, cnpj);
            pstm.setString(2, codigo);
            pstm.setInt(3, idConversao);
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                r.setId(rs.getInt("ID"));
                r.setCnpj(rs.getString("CNPJ"));
                r.setCodigo(rs.getString("CODIGO"));
                r.setDescricao(rs.getString("DESCRICAO"));
                r.setNatureza(rs.getString("NATUREZA"));
                r.setTipo(rs.getString("TIPO"));
            }
            
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return r;
    }
    
    public void deletar(int idConversao) {
        try {
            String sql = "DELETE FROM RUBRICA WHERE ID_CONVERSAO = ?";
            Firebird fb = new Firebird();
            fb.connect();
            PreparedStatement pstm = Firebird.conn.prepareStatement(sql);
            pstm.setInt(1, idConversao);
            pstm.execute();
            Firebird.conn.commit();
            fb.disconnect();
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }
    
    //Fim Banco de Dados
    
    public ArrayList<DeParaRubrica> retornaDeParaRubrica(ArrayList<RubricaUtilizada> ruList) {
        ArrayList<DeParaRubrica> dpRubrica = new ArrayList<>();
        
        int count = 0;
        int codEmpresa = 0;
        
        for(RubricaUtilizada ru : ruList) {
            DeParaRubrica dpr = new DeParaRubrica();
            
            if(codEmpresa != ru.getCodEmpresa()) {
                count = 300;
                codEmpresa = ru.getCodEmpresa();
            }
            
            dpr.setCodEmpresa(ru.getCodEmpresa());
            dpr.setOrigem(ru.getCodRubrica());
            dpr.setDestino(String.valueOf(count));
            dpRubrica.add(dpr);
            count++;
        }
        
        return dpRubrica;
    }
    
    public ArrayList<String> gerarRubrica(ArrayList<RubricaConfiguracao> rcList) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        ArrayList<String> linhas = new ArrayList<>();
        
        for(RubricaConfiguracao rc : rcList) {
            String tipo = null;
            String unidade;
            String classificacao = null;
            String numeroNatureza;
            
            String somar13Media;
            String feriasMedia;
            String taxa;
            int ordemCalculo;
            String somaMediaQuandoEmpregadoAfastado;
            String afastamentoAuxMaternidade;
            int codBase;
            int gerarHomolognet;
            String rubrica;
            String codiRubrica;
            String campoRelacionamentoTermoRescisao = null;
            String licencaMaternidade;
            int avisoPrevioMedias;
            int compoeValorCampo23RemuneracaoMesAnterior;
            int saldoSalarioMedias;
            int tipoConsideracaoCampo23ReciboRescisao;
            int calcularDiferencaAlteracaoPisoSalarial;
            int codIncidendiaIRRF;
            int codIncidendiaINSS;
            String mediaAfastamentoAbortoNaoCriminoso;
            int codIncidendiaFGTS;
            int codIncidenciaSindicaleSocial;
            int usareSocialDomentico;
            String codigoeSocialDomestico;
            
            switch(rc.getTipo()) {
                //Desconto
                case 0:
                    tipo = "D";
                    classificacao = "11";
                    campoRelacionamentoTermoRescisao = "115";
                    break;
                //Provento
                case 1:
                    tipo = "P";
                    classificacao = "2";
                    campoRelacionamentoTermoRescisao = "95";
                    break;
            }
            
            //If para avaliar Valor ou Horas
            if(rc.isHoras()) {
                if(rc.getDescricao().contains("Adicional") && rc.getDescricao().contains("Noturno")) {
                    //Adicional Noturno
                    unidade = "H";
                    somar13Media = "S";
                    feriasMedia = "S";
                    taxa = "200000";
                    ordemCalculo = 77;
                    classificacao = "22";
                    somaMediaQuandoEmpregadoAfastado = "S";
                    afastamentoAuxMaternidade = "S";
                    codBase = 1;
                    gerarHomolognet = 1;
                    rubrica = "12";
                    codiRubrica = "12";
                    campoRelacionamentoTermoRescisao = "55";
                    licencaMaternidade = "S";
                    avisoPrevioMedias = 1;
                    compoeValorCampo23RemuneracaoMesAnterior = 1;
                    saldoSalarioMedias = 1;
                    tipoConsideracaoCampo23ReciboRescisao = 1;
                    numeroNatureza = "1205";
                    calcularDiferencaAlteracaoPisoSalarial = 0;
                    codIncidendiaIRRF = 11;
                    codIncidendiaINSS = 11;
                    mediaAfastamentoAbortoNaoCriminoso = "S";
                    codIncidendiaFGTS = 11;
                    codIncidenciaSindicaleSocial = 11;
                    usareSocialDomentico = 1;
                    codigoeSocialDomestico = "'eSocial1130'";
                } else {
                    //Horas Extras
                    unidade = "H";
                    somar13Media = "S";
                    feriasMedia = "S";
                    //Valor
                    String valor = rc.getDescricao();
                    valor = valor.replaceAll("%", "");
                    valor = valor.replaceAll("[^0-9]", "");
                    
                    //System.out.println(valor);
                    
                    int valorInt = 100;
                    
                    if(rc.getDescricao().contains("Normal")) {
                        valorInt = 150;
                    } else if (rc.getDescricao().contains("NOTURNA")) {
                        valorInt = valorInt + Integer.parseInt(valor) + 20; //Adicionando os 20 para o sistema Tron 
                    }else {
                        valorInt = valorInt + Integer.parseInt(valor);
                    }
                    
                    
                    taxa = valorInt+"0000";
                    ordemCalculo = 490;
                    classificacao = "3";
                    somaMediaQuandoEmpregadoAfastado = "S";
                    afastamentoAuxMaternidade = "S";
                    codBase = 7;
                    gerarHomolognet = 1;
                    rubrica = "4";
                    codiRubrica = "4";
                    campoRelacionamentoTermoRescisao = "56";
                    licencaMaternidade = "S";
                    avisoPrevioMedias = 1;
                    compoeValorCampo23RemuneracaoMesAnterior = 1;
                    saldoSalarioMedias = 1;
                    tipoConsideracaoCampo23ReciboRescisao = 1;
                    numeroNatureza = "1003";
                    calcularDiferencaAlteracaoPisoSalarial = 0;
                    codIncidendiaIRRF = 11;
                    codIncidendiaINSS = 11;
                    mediaAfastamentoAbortoNaoCriminoso = "S";
                    codIncidendiaFGTS = 11;
                    codIncidenciaSindicaleSocial = 0;
                    usareSocialDomentico = 1;
                    codigoeSocialDomestico = "'eSocial1100'";
                }
            } else {
                unidade = "V";
                numeroNatureza = "9989";
                
                somar13Media = "N";
                feriasMedia = "N";
                taxa = "0";
                ordemCalculo = 0;
                somaMediaQuandoEmpregadoAfastado = "N";
                afastamentoAuxMaternidade = "N";
                codBase = -24;
                gerarHomolognet = 0;
                rubrica = "NULO";
                codiRubrica = "NULO";
                //campoRelacionamentoTermoRescisao = "115";
                licencaMaternidade = "N";
                avisoPrevioMedias = 0;
                compoeValorCampo23RemuneracaoMesAnterior = 0;
                saldoSalarioMedias = 0;
                tipoConsideracaoCampo23ReciboRescisao = 0;
                calcularDiferencaAlteracaoPisoSalarial = 1;
                codIncidendiaIRRF = 0;
                codIncidendiaINSS = 0;
                mediaAfastamentoAbortoNaoCriminoso = "N";
                codIncidendiaFGTS = 0;
                codIncidenciaSindicaleSocial = 0;
                usareSocialDomentico = 0;
                codigoeSocialDomestico = "NULO";
            }
            
            //Situação da Rubrica Ativa ou Inativa
            int situacao = 1;
            String dataInativacao = "NULO";
            if(!rc.isSituacao()) {
                dataInativacao = formato.format(rc.getSituacaoData());
                situacao = 0;
            }
            
            String fichaFinanceira;
            if(rc.isFichaFinanceira()) {
                fichaFinanceira = "'S'";
            } else {
                fichaFinanceira = "'N'";
            }
            
            String dirf;
            if(rc.isDirf()) {
                dirf = "'S'";
            } else {
                dirf = "'N'";
            }
            
            String rais;
            if(rc.isRais()) {
                rais = "'S'";
            } else {
                rais = "'N'";
            }
            
            //Experimental Medias
            int avisoPrevioAdicional;
            if(rc.isAvisoPrevio()) {
                avisoPrevioAdicional = 1;
            } else {
                avisoPrevioAdicional = 0;
            }
            
            String salarioFerias;
            if(rc.isSalarioFerias()) {
                salarioFerias = "'S'";
            } else {
                salarioFerias = "'N'";
            }
            
            int licencaPremioAdicional;
            if(rc.isLicencaPremio()) {
                licencaPremioAdicional = 1;
            } else {
                licencaPremioAdicional = 0;
            }
            
            if(rc.isAvisoPrevioMedias()) {
                avisoPrevioMedias = 1;
            } else {
                avisoPrevioMedias = 0;
            }
            
            if(rc.isSalarioMedias()) {
                somar13Media = "S";
            } else {
                somar13Media = "N";
            }
            
            if(rc.isFeriasMedias()) {
                feriasMedia = "S";
            } else {
                feriasMedia = "N";
            }
            
            int licencaPremioMedia;
            
            if(rc.isLicencaPremioMedias()) {
                licencaPremioMedia = 1;
            } else {
                licencaPremioMedia = 0;
            }
            
            if(rc.isSalarioMedias()) {
                saldoSalarioMedias = 1;
            } else {
                saldoSalarioMedias = 0;
            }
            
            
            linhas.add(rc.getCodEmpresa()+"	"+rc.getCodigo()+"	'"+rc.getDescricao()+"'	'"+tipo+"'	'"+unidade+"'	NULO	NULO	NULO	NULO	NULO	NULO	NULO	NULO	NULO	NULO	'N'	'"+somar13Media+"'	'"+feriasMedia+"'	"+salarioFerias+"	"+fichaFinanceira+"	"+rais+"	"+dirf+"	'N'	'S'	'N'	"+taxa+"	"+ordemCalculo+"	"+classificacao+"	1	0	0	'N'	NULO	'N'	NULO	0	'"+somaMediaQuandoEmpregadoAfastado+"'	'N'	'N'	'N'	'"+afastamentoAuxMaternidade+"'	'N'	'N'	'N'	'N'	'N'	"+codBase+"	'N'	'N'	NULO	'N'	'N'	'N'	'N'	'N'	'N'	'N'	'N'	'N'	0	NULO	0	0	0	NULO	"+gerarHomolognet+"	"+rubrica+"	"+codiRubrica+"	1	"+campoRelacionamentoTermoRescisao+"	NULO	NULO	'"+licencaMaternidade+"'	'N'	0	2	0	0	NULO	0	"+avisoPrevioMedias+"	"+avisoPrevioAdicional+"	'N'	"+compoeValorCampo23RemuneracaoMesAnterior+"	"+saldoSalarioMedias+"	0	NULO	0	1	"+tipoConsideracaoCampo23ReciboRescisao+"	'N'	"+formato.format(rc.getInicioData())+"	"+situacao+"	"+dataInativacao+"	"+numeroNatureza+"	NULO	0	0	NULO	0	NULO	0	NULO	0	NULO	0	NULO	1	1	"+rc.getCodigo()+"	1	0	NULO	"+calcularDiferencaAlteracaoPisoSalarial+"	'N'	NULO	0	NULO	0	0	'N'	'N'	0	0	NULO	'N'	'N'	0	NULO	"+codIncidendiaIRRF+"	"+codIncidendiaINSS+"	'"+mediaAfastamentoAbortoNaoCriminoso+"'	"+licencaPremioMedia+"	"+licencaPremioAdicional+"	'N'	1	1	1	"+codIncidendiaFGTS+"	'N'	"+codIncidenciaSindicaleSocial+"	'00000000-0000-0000-0000-000000000000'	'00000000-0000-0000-0000-000000000000'	NULO	'N'	'N'	'N'	'N'	"+usareSocialDomentico+"	"+codigoeSocialDomestico+"	'00000000-0000-0000-0000-000000000000'	'S'");
        }
        return linhas;
    }
}
