package util;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import modelo.Conversao;
import modelo.User;

/**
 *
 * @author Gabriel Moraes
 */
public class Config {
    //Variaveis de Atualização
    public static String ver = "4.2";
    public static int codPublic = 15;
    public static int versaoBancoDados = 13;
    //Fim
    
    //Servidor
    public static Socket socket;
    public static String url = "domino-server.ddns.net";
    public static int porta = 25865;
    
    public static boolean developer = false;
    public static String titulo = "";
    public static Conversao conversao;
    public static String visual = "Windows";
    public static User user;
    public static String cosultTime = "a.st1.ntp.br";
    //public static String nConversao = "";
    public static String pastaPronto = "";
    public static int sistema = 0;
    public static String arquivoAtualiza = System.getProperty("user.dir")+"\\Download\\Atualiza.exe";
    //Nimbus, Windows
    
    public Image getIcon() {
        return Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/icon.png"));
    }
    
    public static void criarConfig(String conversao) throws IOException {
        try (FileWriter arq = new FileWriter(System.getProperty("user.dir")+"\\config.ini")) {
            PrintWriter gravarArq = new PrintWriter(arq);
            gravarArq.println("[conversao] = "+conversao);
        }
    }
}
