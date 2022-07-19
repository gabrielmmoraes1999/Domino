package util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import modelo.Update;
import org.apache.commons.io.FileUtils;
import view.TelaCarregamento;
import view.TelaErro;

/**
 *
 * @author Gabriel Moraes
 */
public class Sistema {
    static String IV = "AAAAAAAAAAAAAAAA";
    static String chaveencriptacao = "dominoprinty1515";
    //static String chaveencriptacao = "0123456789abcdef";
    
    public static byte[] encrypt(String texto) throws Exception {
        Cipher encripta = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(chaveencriptacao.getBytes("UTF-8"), "AES");
        encripta.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
        return encripta.doFinal(texto.getBytes("UTF-8"));
    }

    public static String decrypt(byte[] textoencriptado) throws Exception{
        Cipher decripta = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(chaveencriptacao.getBytes("UTF-8"), "AES");
        decripta.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
        return new String(decripta.doFinal(textoencriptado),"UTF-8");
    }
    
    public static float checkSimilarity(String sString1, String sString2) throws Exception {

        // Se as strings têm tamanho distinto, obtêm a similaridade de todas as
        // combinações em que tantos caracteres quanto a diferença entre elas são
        // inseridos na string de menor tamanho. Retorna a similaridade máxima
        // entre todas as combinações, descontando um percentual que representa
        // a diferença em número de caracteres.
        if(sString1.length() != sString2.length()) {
            int iDiff = Math.abs(sString1.length() - sString2.length());
            int iLen = Math.max(sString1.length(), sString2.length());
            String sBigger, sSmaller, sAux;

            if(iLen == sString1.length()) {
                sBigger = sString1;
                sSmaller = sString2;
            }
            else {
                sBigger = sString2;
                sSmaller = sString1;
            }

            float fSim, fMaxSimilarity = Float.MIN_VALUE;
            for(int i = 0; i <= sSmaller.length(); i++) {
                sAux = sSmaller.substring(0, i) + sBigger.substring(i, i+iDiff) + sSmaller.substring(i);
                fSim = checkSimilaritySameSize(sBigger,  sAux);
                if(fSim > fMaxSimilarity)
                    fMaxSimilarity = fSim;
            }
            return fMaxSimilarity - (1f * iDiff) / iLen;

        // Se as strings têm o mesmo tamanho, simplesmente compara-as caractere
        // a caractere. A similaridade advém das diferenças em cada posição.
        } else
            return checkSimilaritySameSize(sString1, sString2);
    }

    public static float checkSimilaritySameSize(String sString1, String sString2) throws Exception {

        if(sString1.length() != sString2.length())
            throw new Exception("Strings devem ter o mesmo tamanho!");

        int iLen = sString1.length();
        int iDiffs = 0;

        // Conta as diferenças entre as strings
        for(int i = 0; i < iLen; i++)
            if(sString1.charAt(i) != sString2.charAt(i))
                iDiffs++;

        // Calcula um percentual entre 0 e 1, sendo 0 completamente diferente e
        // 1 completamente igual
        return 1f - (float) iDiffs / iLen;
    }
    
    public static void backup() {
        try {
            SimpleDateFormat formato = new SimpleDateFormat("yyyyMMdd_HHmm");
            //String firebird = System.getenv("ProgramFiles")+"\\Firebird\\Firebird_3_0\\gbak.exe";
            String firebird = System.getProperty("user.dir")+"\\Firebird\\Firebird_3_0\\gbak.exe";
            String comando = "-user SYSDBA -pas masterkey";
            String bancoDeDados = System.getProperty("user.dir")+"\\Dados\\DOMINO.FDB";
            String backup = System.getProperty("user.dir")+"\\Backup\\"+formato.format(new Date())+".fbk";
            
            if(!new File(System.getProperty("user.dir")+"\\Backup").exists()) {
                new File(System.getProperty("user.dir")+"\\Backup").mkdirs();
            }
            
            if(new File(firebird).exists()) {
                Scanner scanner = new Scanner(Runtime.getRuntime().exec(firebird+" "+comando+"  "+bancoDeDados+" "+backup).getInputStream());
            } else {
                JOptionPane.showMessageDialog(null, "Firebird não encontrado","Erro!", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }
    
    public static void restaurar(String arquivoBackup) {
        try {
            //SimpleDateFormat formato = new SimpleDateFormat("yyyyMMdd_hhmm");
            //String firebird = System.getenv("ProgramFiles")+"\\Firebird\\Firebird_3_0\\gbak.exe";
            String firebird = System.getProperty("user.dir")+"\\Firebird\\Firebird_3_0\\gbak.exe";
            String comando = "-user SYSDBA -pas masterkey -r -p 4096 -o";
            String bancoDeDados = System.getProperty("user.dir")+"\\Dados\\DOMINO.FDB";
            
            //Deletar Banco
            if(new File(bancoDeDados).exists()) {
                new File(bancoDeDados).delete();
            }
            
            if(new File(firebird).exists()) {
                Scanner scanner = new Scanner(Runtime.getRuntime().exec(firebird+" "+comando+"  "+arquivoBackup+" "+bancoDeDados).getInputStream());
            } else {
                JOptionPane.showMessageDialog(null, "Firebird não encontrado","Erro!", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            new TelaErro(3, ex.getStackTrace()).setVisible(true);
        }
    }
    
    public static void criarArqRelatorio(String caminho, String arquivo, Object conteudo) throws FileNotFoundException, UnsupportedEncodingException {
        File pasta = new File(caminho);
        
        if(!pasta.exists()) {
            pasta.mkdirs();
        }
        
        try ( PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(caminho + "\\" + arquivo), "Cp1252"))) {
            pw.print(conteudo);
        }
    }
    
    public void gravarArquivo(ArrayList<String> linhas, String arquivo) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter (new OutputStreamWriter (new FileOutputStream (arquivo), "Cp1252"));
            for(String l : linhas) {
                pw.println(l);
            }
        } catch (UnsupportedEncodingException ex) {
            new TelaErro(6, ex.getStackTrace()).setVisible(true);
        } catch (FileNotFoundException ex) {
            new TelaErro(2, ex.getStackTrace()).setVisible(true);
        } finally {
            pw.close();
        }
    }
    
    public String baixarAtualizacao(String httpFile, Update u) {
        String pastaRaiz = System.getProperty("user.dir");
        File pastaDownload = new File(pastaRaiz+"\\Download");
        String arquivo = pastaDownload+"\\domino.zip";
        
        if(Config.codPublic < u.getCodPublic()) {
            //Criar a pasta de não existir
            if(!pastaDownload.exists()) {
                pastaDownload.mkdirs();
            } else {
                File arq = new File(arquivo);
                if(arq.exists()) {
                    arq.exists();
                }
            }
            
            //Baixar arquivo
            try {
                URL website = new URL(httpFile);
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                FileOutputStream fos = new FileOutputStream(arquivo);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                fos.close();
                rbc.close();
            } catch (MalformedURLException ex) {
                new TelaErro(11, ex.getStackTrace()).setVisible(true);
            } catch (IOException ex) {
                new TelaErro(3, ex.getStackTrace()).setVisible(true);
            }
        }
        return arquivo;
    }
    
    public void descompactaAtualizacao(String arquivo, Update u) {
        if(Config.codPublic < u.getCodPublic()) {
            ByteArrayInputStream bis = null;
            try {
                File file = new File(arquivo);
                bis = new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
                ZipInputStream zipStream = new ZipInputStream(bis);
                ZipEntry ze;
                byte buff[] = new byte[16384];
                int readBytes;
                while((ze = zipStream.getNextEntry()) != null) {
                    String fileName = file.getParentFile()+"\\" + ze.getName();
                    if(ze.isDirectory()) {
                        Files.createDirectories(Paths.get(fileName));
                        continue;
                    }
                    //zipStream.readAllBytes();
                    FileOutputStream outFile = new FileOutputStream(fileName);
                    while((readBytes = zipStream.read(buff)) != -1) {
                        outFile.write(buff, 0, readBytes);
                    }
                    outFile.close();
                }
            } catch (IOException ex) {
                new TelaErro(3, ex.getStackTrace()).setVisible(true);
            } finally {
                try {
                    bis.close();
                } catch (IOException ex) {
                    new TelaErro(3, ex.getStackTrace()).setVisible(true);
                }
            }
            //Apagar arquivo zip
            new File(arquivo).delete();
        }
    }
    
    public void criarArquivoOrigem(String pastaDados, String arquivoOrigem) throws IOException{
        ArrayList<String> lista = new ArrayList<>();
        File arquivos[];
        File diretorio = new File(pastaDados);
        arquivos = diretorio.listFiles();
        
        int qtdArquivoPDF = 0;
        for (File arquivo : arquivos) {
            if(arquivo.getPath().endsWith(".pdf") || arquivo.getPath().endsWith(".PDF")) {
                qtdArquivoPDF++;
            }
        }
        
        int countPDF = 0;
        for (File arquivo : arquivos) {
            if(arquivo.getPath().endsWith(".pdf") || arquivo.getPath().endsWith(".PDF")) {
                String arq = arquivo.getAbsolutePath();
                PDFClass pdf = new PDFClass();
                pdf.setEnderecoRecurso(arq);
                lista.add(pdf.getConteudo());
                countPDF++;
                TelaCarregamento.porcentagem = (100*countPDF)/qtdArquivoPDF;
            }
        }
        
        PrintWriter pw = new PrintWriter (new OutputStreamWriter (new FileOutputStream (arquivoOrigem), "Cp1252"));
        
        String umPorTodos = "";
        
        for(String texto : lista){
            umPorTodos = umPorTodos+texto;
        }
        
        pw.write(umPorTodos);
        pw.close();
    }
    
    public ArrayList<String> filtrarArquivos(String pastaDados, String tipo) {
        ArrayList<String> lista = new ArrayList<>();
        File baseFolder = new File(pastaDados);
        File[] files = baseFolder.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.getPath().endsWith(tipo)) {
                lista.add(file.getName());
            }
        }
        return lista;
    }
    
    public void logo() throws IOException, InterruptedException {
        System.out.println("\n" +
                "        ±±±±±±±   ±±±±±±  ±±±    ±±± ±± ±±±   ±±  ±±±±±± \n" +
                "        ±±ßßßß±± ±±ßßßß±± ±±±±  ±±±± ßß ±±±±  ±± ±±ßßßß±±\n" +
                "        ±±    ±± ±±    ±± ±±ß±±±±ß±± ±± ±±ß±± ±± ±±    ±±\n" +
                "        ±±    ±± ±±    ±± ±± ß±±ß ±± ±± ±± ß±±±± ±±    ±±\n" +
                "        ±±±±±±±ß ß±±±±±±ß ±±  ßß  ±± ±± ±±  ß±±± ß±±±±±±ß\n" +
                "        ßßßßßßß   ßßßßßß  ßß      ßß ßß ßß   ßßß  ßßßßßß \n" +
                "                                                   FOLHA\n");
    }
    
    public String descricaoEventos(String linha) {
        String descricao = "";
        for(String palavra:linha.split(" ")){
            String withAccent = "äáâàãéêëèíîïìöóôòõüúûùçÇ";
            String withoutAccent = "aaaaaeeeeiiiiooooouuuucC";
            for (int i = 0; i < withAccent.length(); i++) {
                palavra = palavra.replace(withAccent.charAt(i), withoutAccent.charAt(i));
            }
            if(palavra.matches("^[a-zA-Z0-9[.][_][-]]+")){
                descricao = descricao.concat(palavra+" ");
            }
        }
        return descricao;
    }
    
    public String completeToLeft(String value, char c, int size) {
        String result = value;
        
        while (result.length() < size) {
            result = c + result;
        }
        return result;
    }
    
    public void excluirPasta (File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; ++i) {
                excluirPasta(files[i]);
            }
        }
        f.delete();
    }
}
