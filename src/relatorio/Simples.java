package relatorio;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.MaskFormatter;
import modelo.EmpregadoDAO;
import modelo.Empresa;
import modelo.EmpresaDAO;
import modelo.LancamentoDAO;
import modelo.RubricaConfiguracaoDAO;

/**
 *
 * @author Gabriel Moraes
 */
public class Simples {

    int idConversao;

    public Simples(int idConversao) {
        this.idConversao = idConversao;
    }

    @Override
    public String toString() {
        EmpresaDAO empDAO = new EmpresaDAO();
        EmpregadoDAO eDAO = new EmpregadoDAO();
        LancamentoDAO lDAO = new LancamentoDAO();
        RubricaConfiguracaoDAO rcDAO = new RubricaConfiguracaoDAO();

        int totalEmpresa = 0;
        int totalEmpregado = 0;
        int totalRubrica = 0;
        int totalLancamento = 0;
        
        String html = null;
        
        try {
        html = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "	<head>\n"
                + "		<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n"
                + "		<title>Relação de Empresas</title>\n"
                + "		<script type=\"text/javascript\">\n"
                + "			window.onload=function() {\n"
                + "				for (i=0; i<document.getElementsByName(\"quick-navigation\").length; i++) {\n"
                + "					var element = document.getElementsByName(\"quick-navigation\")[i];\n"
                + "					element.setAttribute(\"id\",\"quick-\"+(i+1));\n"
                + "					element.setAttribute(\"onclick\",\"javascript:navigation_goItem(); return false;\");\n"
                + "				}\n"
                + "				\n"
                + "				for (i=0; i<document.getElementsByName(\"navigation-item\").length; i++) {\n"
                + "					var tag_a = document.createElement(\"a\");\n"
                + "					var tag_sup = document.createElement(\"sup\");\n"
                + "					var navigationTag = document.getElementsByName(\"navigation-item\")[i];\n"
                + "					navigationTag.setAttribute(\"id\",i+1);\n"
                + "					\n"
                + "					// Item anterior\n"
                + "					if (i > 0) {\n"
                + "						tag_sup.innerHTML = \" [\"\n"
                + "						tag_a.innerHTML = \"anterior\";\n"
                + "						tag_a.setAttribute(\"href\",\"#\");\n"
                + "						tag_a.setAttribute(\"title\",\"Item anterior\");\n"
                + "						tag_a.setAttribute(\"onclick\",\"javascript:navigation('previous'); return false;\");\n"
                + "						tag_sup.appendChild(tag_a);\n"
                + "						tag_sup.innerHTML += \"]\"\n"
                + "						navigationTag.appendChild(tag_sup);\n"
                + "					}\n"
                + "					// Próximo item\n"
                + "					if (i < document.getElementsByName(\"navigation-item\").length-1) {\n"
                + "						tag_sup.innerHTML += \" [\"\n"
                + "						tag_a = document.createElement(\"a\");\n"
                + "						tag_a.innerHTML = \"próximo\";\n"
                + "						tag_a.setAttribute(\"href\",\"#\");\n"
                + "						tag_a.setAttribute(\"title\",\"Próximo item\");\n"
                + "						tag_a.setAttribute(\"onclick\",\"javascript:navigation('next'); return false;\");\n"
                + "						tag_sup.appendChild(tag_a);\n"
                + "						tag_sup.innerHTML += \"]\"\n"
                + "						navigationTag.appendChild(tag_sup);\n"
                + "					}\n"
                + "				}\n"
                + "			};\n"
                + "			function navigation_goItem() {\n"
                + "				var current = document.activeElement.getAttribute(\"id\");\n"
                + "				current = current.substring(6);\n"
                + "				window.location.href = \"#\" + current;\n"
                + "			}\n"
                + "			function navigation(direction) {\n"
                + "				var currentItem = document.activeElement.parentNode.parentNode;\n"
                + "				var currentId = Number(currentItem.getAttribute(\"id\"));\n"
                + "				if (direction == \"next\") window.location.href = \"#\" + (currentId+1);\n"
                + "				if (direction == \"previous\") window.location.href = \"#\" + (currentId-1);\n"
                + "			}\n"
                + "			function getCurrentItem() {\n"
                + "				current = document.activeElement.parentNode.parentNode.getAttribute(\"id\");\n"
                + "				return Number(current);\n"
                + "			}\n"
                + "		</script>\n"
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
                + "	<script type='text/javascript'>\n"
                + "		function showRow(rowId,atrib) {\n"
                + "		  var rowObject = document.getElementsByName(rowId);\n"
                + "		  for (var e=0; e<rowObject.length; e++) {\n"
                + "			if (atrib == 'hide') rowObject[e].style.display = 'none';\n"
                + "			else rowObject[e].style.display = '';\n"
                + "		  }\n"
                + "		}\n"
                + "	</script>\n"
                + "	<table width='95%' align='center' border='0' cellspacing='0' cellpadding='0'>\n"
                + "	<tr><td>\n"
                + "	<tr>\n"
                + "		<td class='title'>Relação de Empresas</td>\n"
                + "	</tr>\n"
                + "	<tr>\n"
                + "		<td height='4'></td>\n"
                + "	</tr>\n"
                + "		<tr>\n"
                + "			<td>\n"
                + "				<table class='infoTable' width='95%' align='center' cellspacing='1'>\n"
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
                + "								<tbody>";

        for (Empresa e : empDAO.carregarPorConversaoOrdenarPorCodEmpresa(idConversao)) {
            totalEmpresa++;
            //Ler Empregado e lançamentos
            int qtdEmpregado = eDAO.retornaQuantidadeEmpregadoPorConversaoPorEmpresa(idConversao, e.getCodigo());
            int qtdLancamentos = lDAO.retornaQuantidadeLancamentoPorConversaoPorEmpresa(idConversao, e.getCodigo());
            int qtdRubrica = rcDAO.retornaQuantidadeRubricaPorConveraoPorEmpresa(idConversao, e.getCodigo());

            MaskFormatter maskCNPJ = new MaskFormatter("##.###.###/####-##");
            maskCNPJ.setValueContainsLiteralCharacters(false);
            
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            maskCPF.setValueContainsLiteralCharacters(false);
            
            switch(e.getCnpj().length()) {
                case 11:
                    html = html + "<tr>\n"
                            + "										<td align='center'>" + e.getCodigo() + "</td>\n"
                            + "										<td align='center'>" + e.getRazaoSocial() + "</td>\n"
                            + "										<td align='center'>" + maskCPF.valueToString(e.getCnpj()) + "</td>\n"
                            + "										<td align='center'>" + qtdEmpregado + "</td>\n"
                            + "										<td align='center'>" + qtdRubrica + "</td>\n"
                            + "										<td align='center'>" + qtdLancamentos + "</td>\n"
                            + "									</tr>";
                    break;
                case 14:
                    html = html + "<tr>\n"
                            + "										<td align='center'>" + e.getCodigo() + "</td>\n"
                            + "										<td align='center'>" + e.getRazaoSocial() + "</td>\n"
                            + "										<td align='center'>" + maskCNPJ.valueToString(e.getCnpj()) + "</td>\n"
                            + "										<td align='center'>" + qtdEmpregado + "</td>\n"
                            + "										<td align='center'>" + qtdRubrica + "</td>\n"
                            + "										<td align='center'>" + qtdLancamentos + "</td>\n"
                            + "									</tr>";
                    break;
                default:
                    html = html + "<tr>\n"
                            + "										<td align='center'>" + e.getCodigo() + "</td>\n"
                            + "										<td align='center'>" + e.getRazaoSocial() + "</td>\n"
                            + "										<td align='center'>" + e.getCnpj() + "</td>\n"
                            + "										<td align='center'>" + qtdEmpregado + "</td>\n"
                            + "										<td align='center'>" + qtdRubrica + "</td>\n"
                            + "										<td align='center'>" + qtdLancamentos + "</td>\n"
                            + "									</tr>";
                    break;
            }
            
            totalEmpregado = totalEmpregado + qtdEmpregado;
            totalRubrica = totalRubrica + qtdRubrica;
            totalLancamento = totalLancamento + qtdLancamentos;
        }

        html = html + "<tr class='subtitle'>\n"
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
                + "				</table>\n"
                + "			</td>\n"
                + "		</tr>\n"
                + "	</table>\n"
                + "</html>";
        } catch (ParseException ex) {
            Logger.getLogger(Simples.class.getName()).log(Level.SEVERE, null, ex);
        }
        return html;
    }

}
