package relatorio;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.MaskFormatter;
import modelo.Empregado;
import modelo.EmpregadoDAO;
import modelo.Empresa;
import modelo.EmpresaDAO;
import modelo.Lancamento;
import modelo.LancamentoDAO;
import modelo.RubricaConfiguracaoDAO;

/**
 *
 * @author Gabriel Moraes
 */
public class Completo {

    int idConversao;

    public Completo(int idConversao) {
        this.idConversao = idConversao;
    }

    @Override
    public String toString() {
        String html = null;
        try {
            EmpresaDAO empDAO = new EmpresaDAO();
            EmpregadoDAO eDAO = new EmpregadoDAO();
            LancamentoDAO lDAO = new LancamentoDAO();

            RubricaConfiguracaoDAO rcDAO = new RubricaConfiguracaoDAO();

            MaskFormatter maskCNPJ = new MaskFormatter("##.###.###/####-##");
            maskCNPJ.setValueContainsLiteralCharacters(false);
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            maskCPF.setValueContainsLiteralCharacters(false);

            ArrayList<Empresa> listaEmpresa = empDAO.carregarPorConversaoOrdenarPorCodEmpresa(idConversao);
            ArrayList<Empregado> listaEmpregado = eDAO.carregarPorConversao(idConversao);

            int totalEmpresa = listaEmpresa.size();
            int totalEmpregado = 0;
            int totalRubrica = rcDAO.carregarPorConversao(idConversao).size();
            int totalLancamento = lDAO.carregarPorConversao(idConversao).size();

            //Remover este for, pois retorna o total de empresagos
            for (Empresa e : empDAO.carregarPorConversao(idConversao)) {
                totalEmpregado = totalEmpregado + eDAO.retornaQuantidadeEmpregadoPorConversaoPorEmpresa(idConversao, e.getCodigo());
            }

            float totalEmpregadoFloat = totalEmpregado;
            int critica = 0;
            int advertencia = 0;

            ArrayList<String> empresaEmpregadoList = new ArrayList<>();
            ArrayList<String> empresaEmpregadoTemList = new ArrayList<>();

            for (Lancamento l : lDAO.carregarPorConversao(idConversao)) {
                if (!empresaEmpregadoList.contains(l.getCodEmpresa() + "|" + l.getCodEmpregado())) {
                    empresaEmpregadoList.add(l.getCodEmpresa() + "|" + l.getCodEmpregado());
                }
            }

            for (String l : empresaEmpregadoList) {
                StringTokenizer st = new StringTokenizer(l, "|");
                String empresa = st.nextToken();
                String codEmpregado = st.nextToken();
                for (Empregado e : listaEmpregado) {
                    if (empresa.equals(String.valueOf(e.getCodEmpresa())) && codEmpregado.equals(e.getCodEsocial())) {
                        empresaEmpregadoTemList.add(empresa + "|" + codEmpregado);
                    }
                }
            }

            ArrayList<String> empregadosNaoLocalizados = new ArrayList<>();

            for (String l : empresaEmpregadoList) {
                if (!empresaEmpregadoTemList.contains(l)) {
                    empregadosNaoLocalizados.add(l);
                    critica++;
                }
            }

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            //Erro Empresa
            int errosE = 0;
            float totalEmpresaFloat = listaEmpresa.size();
            String errosCPEmpresa = "-";

            for (Empresa e : listaEmpresa) {
                if (e.getCodigo() == 0) {
                    errosE++;
                }
            }

            String errosEmpresa = "-";
            if (errosE != 0) {
                errosEmpresa = String.valueOf(errosE);
            }

            if (!df.format((errosE * 100) / totalEmpresaFloat).equals("0")) {
                errosCPEmpresa = df.format((errosE * 100) / totalEmpresaFloat) + " %";
            }

            //Inconsistencia Empresas
            String errosAdvertenciasEmpresa = "Nenhuma inconsistência detectada";
            if (errosE != 0) {
                errosAdvertenciasEmpresa = errosE + " crítica(s)";
            }

            //Erro Empregado
            int totalErro = critica + advertencia;

            //Colunas html
            String errosC;
            String advertenciasC;
            String errosCP;
            String advertenciasCP;

            if (totalErro != 0) {
                errosC = totalErro + " (" + critica + ")";
            } else {
                errosC = "-";
            }

            if (advertencia != 0) {
                advertenciasC = String.valueOf(advertencia);
            } else {
                advertenciasC = "-";
            }

            if (!df.format((critica * 100) / totalEmpregadoFloat).equals("0")) {
                errosCP = df.format((critica * 100) / totalEmpregadoFloat) + " %";
            } else {
                errosCP = "-";
            }

            if (!df.format((advertencia * 100) / totalEmpregadoFloat).equals("0")) {
                advertenciasCP = df.format((advertencia * 100) / totalEmpregadoFloat) + " %";
            } else {
                advertenciasCP = "-";
            }

            //Inconsistencia Empregados
            String errosAdvertenciasEmpregado;
            if (critica + advertencia != 0) {
                errosAdvertenciasEmpregado = (critica + advertencia) + " (" + critica + " crítica(s) e " + advertencia + " advertência(s))";
            } else {
                errosAdvertenciasEmpregado = "Nenhuma inconsistência detectada";
            }

            html = "<html>\n"
                    + "	<head>\n"
                    + "		<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n"
                    + "		<title>Relatório completo da conversão de dados</title>\n"
                    + "		\n"
                    + "	</head>\n"
                    + "	<style TYPE=\"text/css\">\n"
                    + "	  <!--\n"
                    + "		body { color: #000000; background-color: #E1E1E1; margin: 10px; padding: 0px; }\n"
                    + "		colval { font-family: Arial, Helvetica, sans-serif; font-size: 9pt; font-style: italic; color: rgb(51,51,51); font-weight: bold; }\n"
                    + "		coldesc { font-family: Arial, Helvetica, sans-serif; font-size: 9pt; font-weight: bold; }\n"
                    + "		colname { font-family: Arial, Helvetica, sans-serif; font-size: 9pt; font-weight: bold; }\n"
                    + "		table td.warningIcon {\n"
                    + "		  background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAClklEQVR42qWTbUhTURjH/9e9OZevW5Ny1pytkRLZi1l9qChFPyThNx1BX4xyrCxfMpOMiqjAZkRd2MwFzje0pSCBiKBI6AgqK8NcalEhaHeuDza8d/fudhzkl9CMHng+nOf8z++c8z/nofCfQa004Wx0UrHxceXdXd3vXK6mPlIS/w3gdJr1er55dlbpt1ov7PH5mE9rBtjt9nUajXZ8X2aNzu8/ANoe2UrTD06SKX5NgJaW1psm48TlHem3wAai4Rl9JJw+U5E9NTU1+FeAw+FITTXo3msTqhUDQzqolBwOZWnQ1Z85UllRdoRIFlcF9PQ8e5pu7C3w+wbg6syFhGJQaxnG9PxtlJTWFXs8I40rAhoaGrL3Z6X06TUWyjutQB2dAYWUQX3VJJh5PcZ81q9mc9HOQCDg+wNA07TEZNr2dmuSIy0hahQTH4GyG0kwJAuoK52AwC5iDldRaxu643a7q38/6zKgqcllzUjj72+OuUYFAxwmJ3nkFQeRd3gTbCVjEIn/C4F4fIltXigyn9jLMMz4MoDsrjEYUj6Yos+rI/GNiEV8Z4CcklgU5qeg6vhwGBASIjArPYWWIVXnvXqbeelZw4C2tvaHBvVzS7LSSUoiEQugeB6Dr1QwJApIigtAFCRhAMvK8FnzOHS2/Equ1+vtp8in2b5BG/V6V0KNRAIi5EPhxXN+AfkXtSg8ZsS5ox6yWAZRpMiRRcxwuzE4k/PyUlXlQaqj40nvFvVArl49TXYRQYWIN6FQ2KL+FxR062UwbhRIKSJ84QhKhCAIePOjANfvNlsom83miYtWZohCEDKpFHKFAgqScrmcjCVhq3lyIo7lsMiyIsdxCJKUKFTodHe1LnkQQzJxtcZaqRNJ/PwFcRkU2Y5v7+4AAAAASUVORK5CYII=');\n"
                    + "		  background-repeat: no-repeat;\n"
                    + "		  background-position: center;\n"
                    + "		  width: 16px;\n"
                    + "		}\n"
                    + "		table td.errorIcon {\n"
                    + "		  background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADd0lEQVR42mVTbUhbZxg9bxJtEk22aGLTaSZpLf1US0un/SWl3WalQict6xxkP9TKZmFgKNg2LcNpgmA6qSxjsIF0uhS7dlIY67bO/ahzmbgf0SI2S11kuY3OBOPMx/Xm3tw9KazY7YXDvT+ec97nnPd5GDYdu91eaDQamyus1jNGxg7J0agBRUWrMcZ+Cy4s3IrGYl+63e7EZg7798fhcFTW1dR8sU8U9ycnJ5XSygqkVArpRAKCSoXCo0elJ1brw4npaVtPT8/McwJErjpz7Ng3ep+vDHNzQDyObDYLMSshI2UhSBLSggC+uBj68+fDY+PjDb1O5+xTgVzbr9fVTezw+6uzU1PQV1UjGfoD6eW/iCiSgATly+WQIGNtfh6SpQyi7R3/Tw8eHHFfu5ZmLperrdlq/SR1545Sb7FgW18fpGgMcx3vIRmJQLV9B7T2TqID4QsXkOCeoKy9XRoOPGr92OMZYrdu3vyh4v6Px5WPg5DJ6/b+fuTt2oVEZAmhgY+QZ7OBF0WsTU8jcOUqCklKu3cv1my2e23nzjWw+15vTDs8XKQi3yK1K5GIxekCv7UE6xQgT0Em/X4EurrwIuWSZQzSFjVKPJ5I49k3y9m3g4Oy9vZtsFQSwoaAJMdhy4EDMLhcSK6vI58EA+3tUC8uPnuyjEKBl4aG0sdPny5h342MRKXBwWIFFSeoSF1ZiWLKQZBliMkkUtSVgQhxux2ZUAhKEhC0Gug/+zz+WlNTORv1er9XXb/+amZ2FnkVFTAPDECgNtMzMwh1d8Pa/SFMNa9gq1KJ3xsbIS8vQ3H4MHwnTvzcdflyPevt7W2pWV39NOzxKBUmE7a5+7ER5hB0OGCm29UaDXbfuAGNXo/gG6egI5v5ly7J7969e/UXn68vNwfautrayZUrjuqNP8NgOh1SFJ6JAtOQjYIc1GqaGAYTvcbf1GWgpSXU2tnZIAjC/NNc3m5u3t/W1HRv3uEoFcNh5BMpRy4kER199blRJgsJqxW83R5/6+LF9zmOGyUq/2wXjtTWVtk7OryGYHAPNzbGpIUFFJFAPgVYUFqKgvp6+fHOnZFOp/ODMMd9RZTV55Ypd8xm8wuHDh5sPXXy5FmL0bhPkcloZIWCX1xaWhwd+3p84tepEZ7nc4u0/r9t/M+GkmkYCDqCgpAixAm5VZY2F/8DhNuOUj4xifMAAAAASUVORK5CYII='); \n"
                    + "		  background-repeat: no-repeat;\n"
                    + "		  background-position: center;\n"
                    + "		  width: 16px;\n"
                    + "		}\n"
                    + "		table td.informationIcon {\n"
                    + "		  background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAC0UlEQVR42qWTWWyMURTHf9/s801naWu0pbZop9opHSGU2EIsESkSERIkHkQmEaJEYo8lJKJFSCzhgdjCA2JreZBaG6oaNGXQ0R1tdbQzo1oznzuD0fDoJic3OfecX875n3Ml/jr5q/OniWu6sLHCMn+5q4Q9FFZcuK/wVs94qUfiQHGtSOxld/dN7S8nJacQb0uIvrX5PvPxQxMN9bXB1pbmw8J1SIDexwC/knekOzIXpWVk42218rhKRfWH76B8J8nahStNwpn6jTpvFW88VadF/OYI5Ddgr0hek+4czdXHesrfwUSXiVFZcaglher6di7feU9yfIi8MUY+N76MQAoEYK0U6VmUfSl37CT5xjMb9ytDJJpVnNyaRoJZQaXWRgu9V9aIe1cpOYMNLJxi5nn5g0g7cyOAgmGuEfmSJYdjN0MooS6MOomsASpeez+R0tvKwXXD0WslZrqvU93YztJZfehvaeZ5xdPCCODR1Bmzcq+WxVHxTkEJdxMSEH/gKy2+r6TYFC7tGUlnSEXeymI6gmFGO6245yZxu+haaQTgm79giXXjiRY6OjWEQ934/UHa2gOYDGE2LU5mnMvOnjNerpQ0YDZqsJg0HFk/ggvnT32JAdYfraMtoMEnEts7AgSCXex2pzNnfAIFZzwUlbYhy3pUQnazrGbvKmcMEG3hYkknt5/48Ac7o1XoNLB9eRY2k8S24x4scSbh0yJkxdFPy/zJxlgLURH9SjIbDnuE3gqSFELWqxnjtGE06vHUKsgGWUxDIzSSyJugI9FQHxMxOsZRuePlA+deU1LRIWYPOUPi2enORqeXKTjZQE2jCp1OhyM1zLwpasrL7v4cY89FGpSezcGzL3jyKkDGIDtblg3Fnmhh434vTc0GchwSsydraap59meR/lllRyavagKUVX7hY2s3GpUBe3wCrsw4MgZ0431b+e8q//dn+p/v/APW103cqcextAAAAABJRU5ErkJggg=='); \n"
                    + "		  background-repeat: no-repeat;\n"
                    + "		  background-position: center;\n"
                    + "		  width: 16px;\n"
                    + "		}\n"
                    + "		table { color: #000000; font-family: Arial, Helvetica, sans-serif; font-size: 9pt; text-decoration: none; background-color: #FFFFFF; border: 1px; border-color: #2192CC; border-spacing: 1px; border-style: none; padding: 0px 0px 15px 0px; }\n"
                    + "		td.title { background-color: rgb(12,50,71); font-size: 12pt; font-weight: bold; text-align: center; text-transform: uppercase; color: #FFFFFF; padding: 5px 5px 5px 5px; width: 100%; vertical-align: middle; cursor: default; }\n"
                    + "		table.infoTable { font-family: Arial, Helvetica, sans-serif; font-size: 9pt; text-decoration: none; border: 1px; border-color: rgb(21,91,130); background-color: #FFFFFF; border-spacing: 1px; border-style: solid; padding: 2px; }\n"
                    + "		table td.infoTableLabel { font-size: 10pt; padding: 2px 2px 2px 5px; color: #FFFFFF; border: 0px; background-color: rgb(21,91,130); text-align: left; vertical-align: center; font-weight: bold; white-space: nowrap; cursor: default; }\n"
                    + "		.infoTable td.infoTableField{ font-family: Arial, Helvetica, sans-serif; font-size: 9pt; text-decoration: none; background-color: #FFFFFF; padding: 2px 2px 2px 5px; }\n"
                    + "		.infoTableLabel a { color: #FFFFFF; text-decoration: none; }\n"
                    + "		.infoTableLabel a:hover { color: #CCCCCC; text-decoration: underline; font-weight: bold; }\n"
                    + "		table.detailTable { color: #000000; font-family: Arial, Helvetica, sans-serif; font-size: 9pt; text-decoration: none; background-color: #FFFFFF; border: 0px; border-spacing: 1px; border-style: solid; padding: 0px; }\n"
                    + "		.detailTable thead th { font-family: Arial, Helvetica, sans-serif; color: #FFFFFF; background-color: rgb(27,117,165); height: 21px; padding: 0px; border: 0px; text-align: center; cursor: default; }\n"
                    + "		.detailTable tr.subtitle { font-family: Arial, Helvetica, sans-serif; color: rgb(51,51,51); background-color: rgb(76,183,239); height: 21px; padding: 0px; border: 0px; text-align: center; font-weight: bold; }\n"
                    + "		.detailTable tr.subtitle:hover { background-color: rgb(146,210,245); }\n"
                    + "		.detailTable tbody tr { height: 21px; border: 0px; padding-left: 1px; padding-right: 1px; background-color: #FFFFFF; text-decoration: none; }\n"
                    + "		.detailTable tbody tr:nth-child(even) { background-color: #D7EBFF; }\n"
                    + "		.detailTable tbody tr:hover { background-color: #91C8FF; }\n"
                    + "		.quick-navigation { color: rgb(0,65,150); text-decoration: none; }\n"
                    + "		.quick-navigation:hover { text-decoration: underline; }\n"
                    + "		/**\n"
                    + "		 * HINT- A CSS tooltip library\n"
                    + "		 */\n"
                    + "		.hint { position: relative; display: inline-block; }\n"
                    + "		.hint:before, .hint:after { position: absolute; opacity: 0; z-index: 1000000; -webkit-transition: 0.3s ease; -moz-transition: 0.3s ease; pointer-events: none; }\n"
                    + "		.hint:hover:before, .hint:hover:after { opacity: 1; }\n"
                    + "		.hint:before { content: ''; position: absolute; background: transparent; border: 6px solid transparent; position: absolute; }\n"
                    + "		.hint:after { content: attr(data-hint); background: rgba(0, 0, 0, 0.8); color: white; padding: 8px 10px; font-size: 12px; white-space: nowrap; box-shadow: 4px 4px 8px rgba(0, 0, 0, 0.3); }\n"
                    + "		/* top */\n"
                    + "		.hint--top:before { bottom: 100%; left: 50%; margin: 0 0 -18px 0; border-top-color: rgba(0, 0, 0, 0.8); } \n"
                    + "		.hint--top:after { bottom: 100%; left: 50%; margin: 0 0 -6px -10px; }\n"
                    + "		.hint--top:hover:before { margin-bottom: -10px; }\n"
                    + "		.hint--top:hover:after { margin-bottom: 2px; }\n"
                    + "		/* default: bottom */\n"
                    + "		.hint--bottom:before { top: 100%; left: 50%; margin: -14px 0 0 0; border-bottom-color: rgba(0, 0, 0, 0.8); }\n"
                    + "		.hint--bottom:after { top: 100%; left: 50%; margin: -2px 0 0 -10px; }\n"
                    + "		.hint--bottom:hover:before { margin-top: -6px; }\n"
                    + "		.hint--bottom:hover:after { margin-top: 6px; }\n"
                    + "		/* right */\n"
                    + "		.hint--right:before { left: 100%; bottom: 50%; margin: 0 0 -4px -8px; border-right-color: rgba(0,0,0,0.8); }\n"
                    + "		.hint--right:after { left: 100%; bottom: 50%; margin: 0 0 -13px 4px; }\n"
                    + "		.hint--right:hover:before { margin: 0 0 -4px -0; }\n"
                    + "		.hint--right:hover:after { margin: 0 0 -13px 12px; }\n"
                    + "		/* left */\n"
                    + "		.hint--left:before { right: 100%; bottom: 50%; margin: 0 -8px -4px 0; border-left-color: rgba(0,0,0,0.8); }\n"
                    + "		.hint--left:after { right: 100%; bottom: 50%; margin: 0 4px -13px 0; }\n"
                    + "		.hint--left:hover:before { margin: 0 0 -4px 0; }\n"
                    + "		.hint--left:hover:after { margin: 0 12px -13px 0; }\n"
                    + "	  -->\n"
                    + "	</style>\n"
                    + "  <script type='text/javascript'>\n"
                    + "    function showRow(rowId,atrib) {\n"
                    + "      var rowObject = document.getElementsByName(rowId);\n"
                    + "      for (var e=0; e<rowObject.length; e++) {\n"
                    + "        if (atrib == 'hide') rowObject[e].style.display = 'none';\n"
                    + "        else rowObject[e].style.display = '';\n"
                    + "      }\n"
                    + "    }\n"
                    + "  </script>\n"
                    + "	<table width='95%' align='center' border='0' cellspacing='0' cellpadding='0'>\n"
                    + "		<tr>\n"
                    + "			<td class='title'>RELATÓRIO SIMPLIFICADO</td>\n"
                    + "		</tr>\n"
                    + "		<tr>\n"
                    + "			<td height='4'></td>\n"
                    + "		</tr>\n"
                    + "		<tr><td>\n"
                    + "		<table class='infoTable' width='95%' align='center' cellspacing='1'>\n"
                    + "			<tr>\n"
                    + "				<td class='infoTableLabel'>Informações convertidas</td>\n"
                    + "			</tr>\n"
                    + "			<tr>\n"
                    + "				<td>\n"
                    + "					<table class='detailTable' width='100%' align='center'>\n"
                    + "						<thead>\n"
                    + "							<tr>\n"
                    + "								<th>Nome</th>\n"
                    + "								<th>Total convertido</th>\n"
                    + "								<th>Erros</th>\n"
                    + "								<th>Advertências</th>\n"
                    + "								<th>Informações</th>\n"
                    + "								<th>% Erros</th>\n"
                    + "								<th>% Advertências</th>\n"
                    + "								<th>% Informações</th>\n"
                    + "							</tr>\n"
                    + "						</thead>\n"
                    + "						<tbody>\n"
                    + "							<tr>\n"
                    + "								<td><a name=\"quick-navigation\" class=\"quick-navigation\" href=\"#\">Empresas</a></td>\n"
                    + "								<td align=\"center\">" + totalEmpresa + "</td>\n"
                    + "								<td align=\"center\">" + errosEmpresa + "</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">" + errosCPEmpresa + "</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "							</tr>\n"
                    + "							<tr>\n"
                    + "								<td><a name=\"quick-navigation\" class=\"quick-navigation\" href=\"#\">Empregados</a></td>\n"
                    + "								<td align=\"center\">" + totalEmpregado + "</td>\n"
                    + "								<td align=\"center\">" + errosC + "</td>\n"
                    + "								<td align=\"center\">" + advertenciasC + "</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">" + errosCP + "</td>\n"
                    + "								<td align=\"center\">" + advertenciasCP + "</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "							</tr>\n"
                    + "							<tr>\n"
                    + "								<td><a name=\"quick-navigation\" class=\"quick-navigation\" href=\"#\">Lançamentos</a></td>\n"
                    + "								<td align=\"center\">" + totalLancamento + "</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "							</tr>\n"
                    + "							<tr>\n"
                    + "								<td><a name=\"quick-navigation\" class=\"quick-navigation\" href=\"#\">Rubricas Normais</a></td>\n"
                    + "								<td align=\"center\">" + totalRubrica + "</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "							</tr>\n"
                    + "							<tr>\n"
                    + "								<td><a name=\"quick-navigation\" class=\"quick-navigation\" href=\"#\">Rubricas Horas</a></td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "								<td align=\"center\">-</td>\n"
                    + "							</tr>\n"
                    + "						</tbody>\n"
                    + "					</table>\n"
                    + "		</table>\n"
                    + "		<br>\n"
                    + "				</td>\n"
                    + "			</tr>\n"
                    + "		<tr>\n"
                    + "			<td class='title'>RELATÓRIO DETALHADO</td>\n"
                    + "		</tr>\n"
                    + "		<tr>\n"
                    + "			<td height='4'></td>\n"
                    + "		</tr>\n"
                    + "		<tr>\n"
                    + "			<td>\n"
                    + "				<table class='infoTable' width='95%' align='center' cellspacing='1'>\n"
                    + "					<tr>\n"
                    + "						<td class='infoTableLabel'>Empresas</td>\n"
                    + "					</tr>\n"
                    + "					<tr>\n"
                    + "						<td class='infoTableField'>\n"
                    + "							<b>Total:</b> " + totalEmpresa + "\n"
                    + "						</td>\n"
                    + "					</tr>\n"
                    + "					<tr>\n"
                    + "						<td class='infoTableField'>\n"
                    + "							<b>Inconsistências:</b> " + errosAdvertenciasEmpresa + "\n"
                    + "						</td>\n"
                    + "					</tr>\n"
                    + "					<tr>\n"
                    + "						<td>\n"
                    + "							<table class='detailTable' width='100%' align='center'>\n"
                    + "								<thead>\n"
                    + "									<tr>\n"
                    + "										<th colspan='3'>Empresa</th>\n"
                    + "										<th colspan='3'>\n"
                    + "											<a class='hint hint--top' data-hint='Exibição: De dados da folha'>Folha</a>\n"
                    + "										</th>\n"
                    + "									</tr>\n"
                    + "									<tr>\n"
                    + "										<th>Código</th>\n"
                    + "										<th>Razão Social</th>\n"
                    + "										<th>Inscrição</th>\n"
                    + "										<th>Empregados</th>\n"
                    + "										<th>Rubricas</th>\n"
                    + "										<th>Lancamentos</th>\n"
                    + "									</tr>\n"
                    + "								</thead>\n"
                    + "								<tbody>\n";
            boolean empresaNaoEncontrada = false;

            for (Empresa e : listaEmpresa) {
                if (e.getCodigo() != 0) {
                    int qtdEmpregado = eDAO.retornaQuantidadeEmpregadoPorConversaoPorEmpresa(idConversao, e.getCodigo());
                    int qtdRubrica = rcDAO.retornaQuantidadeRubricaPorConveraoPorEmpresa(idConversao, e.getCodigo());
                    int qtdLancamento = lDAO.retornaQuantidadeLancamentoPorConversaoPorEmpresa(idConversao, e.getCodigo());

                    switch(e.getCnpj().length()) {
                        case 11:
                            html = html
                                    + "									<tr>\n"
                                    + "										<td align='center'>" + e.getCodigo() + "</td>\n"
                                    + "										<td align='center'>" + e.getRazaoSocial() + "</td>\n"
                                    + "										<td align='center'>" + maskCPF.valueToString(e.getCnpj()) + "</td>\n"
                                    + "										<td align='center'>" + qtdEmpregado + "</td>\n"
                                    + "										<td align='center'>" + qtdRubrica + "</td>\n"
                                    + "										<td align='center'>" + qtdLancamento + "</td>\n"
                                    + "									</tr>\n";
                            break;
                        case 14:
                            html = html
                                    + "									<tr>\n"
                                    + "										<td align='center'>" + e.getCodigo() + "</td>\n"
                                    + "										<td align='center'>" + e.getRazaoSocial() + "</td>\n"
                                    + "										<td align='center'>" + maskCNPJ.valueToString(e.getCnpj()) + "</td>\n"
                                    + "										<td align='center'>" + qtdEmpregado + "</td>\n"
                                    + "										<td align='center'>" + qtdRubrica + "</td>\n"
                                    + "										<td align='center'>" + qtdLancamento + "</td>\n"
                                    + "									</tr>\n";
                            break;
                        default:
                            html = html
                                    + "									<tr>\n"
                                    + "										<td align='center'>" + e.getCodigo() + "</td>\n"
                                    + "										<td align='center'>" + e.getRazaoSocial() + "</td>\n"
                                    + "										<td align='center'>" + e.getCnpj() + "</td>\n"
                                    + "										<td align='center'>" + qtdEmpregado + "</td>\n"
                                    + "										<td align='center'>" + qtdRubrica + "</td>\n"
                                    + "										<td align='center'>" + qtdLancamento + "</td>\n"
                                    + "									</tr>\n";
                            break;
                    }
                } else {
                    empresaNaoEncontrada = true;
                }
            }
            html = html
                    + "									<tr class='subtitle'>\n"
                    + "										<td>TOTAL</td>\n"
                    + "										<td colspan='2'>" + totalEmpresa + " empresa(s)</td>\n"
                    + "										<td>" + totalEmpregado + "</td>\n"
                    + "										<td>" + totalRubrica + "</td>\n"
                    + "										<td>" + totalLancamento + "</td>\n"
                    + "									</tr>\n"
                    + "								</tbody>\n"
                    + "							</table>\n"
                    + "						</td>\n"
                    + "					</tr>\n"
                    + "					<tr><td height='4'></td></tr>\n";
            //Empresa com erros
            if (empresaNaoEncontrada) {
                html = html
                        + "<tr>\n"
                        + "	<td>\n"
                        + "		<table class=\"detailTable\" width=\"100%\" align=\"center\">\n"
                        + "			<thead>\n"
                        + "				<tr>\n"
                        + "					<th width=\"60\">Tipo</th>\n"
                        + "					<th>Empresa</th>\n"
                        + "					<th>Mensagem</th>\n"
                        + "				</tr>\n"
                        + "			</thead>\n"
                        + "			<tbody>";
                for (Empresa e : listaEmpresa) {
                    if (e.getCodigo() == 0) {
                        html = html
                                + "<tr>\n"
                                + "					<td class=\"errorIcon\"></td>\n"
                                + "					<td align=\"center\">" + e.getCodigo() + "</td>\n"
                                + "					<td>A empresa com o Inscrição '<b>" + maskCNPJ.valueToString(e.getCnpj()) + "</b>' não foi encontrada!</td>\n"
                                + "				</tr>";
                    }
                }
                
                html = html
                        + "</tbody>\n"
                        + "		</table>\n"
                        + "	</td>\n"
                        + "</tr>";
            }
            
            html = html
                    + "				</table>\n"
                    + "				<br>\n"
                    + "				<table class='infoTable' width='95%' align='center' cellspacing='1'>\n"
                    + "					<tr>\n"
                    + "						<td class='infoTableLabel'>Empregados</td>\n"
                    + "					</tr>\n"
                    + "					<tr>\n"
                    + "						<td class='infoTableField'><b>Total:</b> " + totalEmpregado + "</td>\n"
                    + "					</tr>\n"
                    + "					<tr>\n"
                    + "						<td class='infoTableField'><b>Inconsistências:</b> " + errosAdvertenciasEmpregado + "</td>\n"
                    + "					</tr>\n";
            if (critica + advertencia != 0) {
                html = html
                        + "					<tr>\n"
                        + "						<td height='4'></td>\n"
                        + "					</tr>\n"
                        + "					<tr>\n"
                        + "						<td>\n"
                        + "							<table class='detailTable' width='100%' align='center'>\n"
                        + "								<thead>\n"
                        + "									<th width='60'>Tipo</th>\n"
                        + "									<th>Empresa</th>\n"
                        + "									<th>Empregado</th>\n"
                        + "									<th>Mensagem</th>\n"
                        + "								</thead>\n"
                        + "								<tbody>\n";
            }
            for (String t : empregadosNaoLocalizados) {
                StringTokenizer st = new StringTokenizer(t, "|");
                html = html
                        + "									<tr>\n"
                        + "										<td class=\"errorIcon\"></td>\n"
                        + "										<td align=\"center\">" + st.nextToken() + "</td>\n"
                        + "										<td align=\"center\">" + st.nextToken() + "</td>\n"
                        + "										<td><coldesc>Código eSocial</coldesc> não encontrado.</td>\n"
                        + "									</tr>\n";
            }
            /*for(String d : demitidos) {
            StringTokenizer st = new StringTokenizer(d, "|");
            html = html +
            "									<tr>\n" +
            "										<td class=\"warningIcon\"></td>\n" +
            "										<td align=\"center\">"+st.nextToken()+"</td>\n" +
            "										<td align=\"center\">"+st.nextToken()+"</td>\n" +
            "										<td><coldesc>Código eSocial</coldesc> é igual código do Empregado.</td>\n" +
            "									</tr>\n";
            }*/

            if (critica + advertencia != 0) {
                html = html
                        + "								</tbody>\n"
                        + "							</table>\n"
                        + "						</td>\n"
                        + "					</tr>\n";
            }
            
            html = html
                    + "				</table>\n"
                    + "				<br>\n"
                    + "				<table class='infoTable' width='95%' align='center' cellspacing='1'>\n"
                    + "					<tr>\n"
                    + "						<td class='infoTableLabel'>Lançamentos</td>\n"
                    + "					</tr>\n"
                    + "					<tr>\n"
                    + "						<td class='infoTableField'><b>Total:</b> " + totalLancamento + "</td>\n"
                    + "					</tr>\n"
                    + "					<tr>\n"
                    + "						<td class='infoTableField'>\n"
                    + "							<b>Inconsistências:</b> Nenhuma inconsistência detectada.\n"
                    + "						</td>\n"
                    + "					</tr>\n"
                    + "				</table>\n"
                    + "				<br>\n"
                    + "				<table class='infoTable' width='95%' align='center' cellspacing='1'>\n"
                    + "					<tr>\n"
                    + "						<td class='infoTableLabel'>Rubricas Normais</td>\n"
                    + "					</tr>\n"
                    + "					<tr>\n"
                    + "						<td class='infoTableField'><b>Total:</b> " + totalRubrica + "</td>\n"
                    + "					</tr>\n"
                    + "					<tr>\n"
                    + "						<td class='infoTableField'>\n"
                    + "							<b>Inconsistências:</b> Nenhuma inconsistência detectada.\n"
                    + "						</td>\n"
                    + "					</tr>\n"
                    + "				</table>\n"
                    + "				<br>\n"
                    + "				<table class='infoTable' width='95%' align='center' cellspacing='1'>\n"
                    + "					<tr>\n"
                    + "						<td class='infoTableLabel'>Rubricas Horas</td>\n"
                    + "					</tr>\n"
                    + "					<tr>\n"
                    + "						<td class='infoTableField'>\n"
                    + "							<b>Total:</b> 0\n"
                    + "						</td>\n"
                    + "					</tr>\n"
                    + "					<tr>\n"
                    + "						<td class='infoTableField'>\n"
                    + "							<b>Inconsistências:</b> Nenhuma inconsistência detectada.\n"
                    + "						</td>\n"
                    + "					</tr>\n"
                    + "				</table>\n"
                    + "			</td>\n"
                    + "		</tr>\n"
                    + "	</table>\n"
                    + "<br>\n"
                    + "</html>";
        } catch (ParseException ex) {
            Logger.getLogger(Completo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return html;
    }

}
