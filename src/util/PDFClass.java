package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import view.TelaErro;

/**
 *
 * @author Gabriel Moraes
 */
public class PDFClass {
    private String enderecoRecurso;

    public void setEnderecoRecurso(String enderecoRecurso) {
        this.enderecoRecurso = enderecoRecurso; // endereço dos ficheiros
    }
    
    public String getConteudo() {
        File f = new File(this.enderecoRecurso);
        FileInputStream is;
        
        try {
            is = new FileInputStream(f);
        } catch (IOException ex) {
            new TelaErro(3, ex.getStackTrace()).setVisible(true);
            return null;
        }
        
        PDDocument pdfDocument = null;
        
        try {
            PDFParser parser = new PDFParser(is);
            parser.parse();
            pdfDocument = parser.getPDDocument();
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(pdfDocument);
	} catch (IOException ex) {
            new TelaErro(3, ex.getStackTrace()).setVisible(true);
            return "ERRO: Não é possível abrir a stream" + ex;
	} catch (Throwable ex) {
            // Fazemos um catch, uma vez que precisamos de fechar o recurso
            new TelaErro(10, ex.getStackTrace()).setVisible(true);
            return "ERRO: Um erro ocorreu enquanto tentava obter o conteúdo do PDF" + ex;
        } finally {
            if (pdfDocument != null) {
                try {
                    pdfDocument.close();
                } catch (IOException ex) {
                    new TelaErro(3, ex.getStackTrace()).setVisible(true);
                    return "ERRO: Não foi possível fechar o PDF." + ex;
                }
            }
        }
    }
}
