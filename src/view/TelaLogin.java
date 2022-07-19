package view;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import modelo.Computer;
import static modelo.ComputerDAO.*;
import static util.Config.visual;
import modelo.User;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import util.Config;

/**
 *
 * @author Gabriel Moraes
 */
public class TelaLogin extends javax.swing.JFrame {

    //Configurações Computador
    String manufacturer;
    String name;
    String serialNumber;
    String version;
    String serialNumberCpu;
    String nameCpu;

    //TelaAguarde ta = new TelaAguarde();
    static TelaLoading tl = new TelaLoading(System.getProperty("user.dir")+"\\Imagens\\Logo.png");
    PrintStream saida;
    ObjectInputStream entrada;

    /**
     * Creates new form TelaLogin
     */
    public TelaLogin() {
        tl.setVisible(true);
        
        try {
            this.saida = new PrintStream(Config.socket.getOutputStream());
            this.entrada = new ObjectInputStream(Config.socket.getInputStream());
        } catch (IOException ex) {
            new TelaErro(3, ex.getStackTrace()).setVisible(true);
        }
        
        initComponents();
        setIconImage(new Config().getIcon());
        manufacturer = getMotherboardManufacturer();
        name = getMotherboardName();
        serialNumber = getMotherboardSerialNumber();
        version = getMotherboardVersion();
        serialNumberCpu = getCPUSerial();
        nameCpu = getCPUName();
        tl.setVisible(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tLogin = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tSenha = new javax.swing.JPasswordField();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dominó Folha - Login");
        setResizable(false);

        jLabel1.setText("Login");

        tLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tLoginActionPerformed(evt);
            }
        });

        jLabel2.setText("Senha");

        tSenha.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tSenhaKeyPressed(evt);
            }
        });

        jButton1.setText("Entrar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tSenha, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tLogin))
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(216, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconLogin.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(122, 122, 122))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tLoginActionPerformed

    }//GEN-LAST:event_tLoginActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.setVisible(false);
        try {
            Thread.sleep(500);
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        tl.setVisible(true);
        try {
            Thread.sleep(2000);
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        new Thread(login).start();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tSenhaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tSenhaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.setVisible(false);
            try {
                Thread.sleep(500);
            } catch (Exception ex) {
                new TelaErro(1, ex.getStackTrace()).setVisible(true);
            }
            tl.setVisible(true);
            try {
                Thread.sleep(2000);
            } catch (Exception ex) {
                new TelaErro(1, ex.getStackTrace()).setVisible(true);
            }
            new Thread(login).start();
        }
    }//GEN-LAST:event_tSenhaKeyPressed

    public void tornaVisivel() {
        this.setVisible(true);
    }
    
    Runnable login = new Runnable() {
        @Override
        public void run() {

            try {
                User uu = new User();
                Computer cc = new Computer();

                //Envia mensagem ao servidor
                String comandos[] = {"1", "2"};
                for (String comando : comandos) {
                    saida.println(comando);

                    switch (comando) {
                        case "1": //User
                            saida.println(tLogin.getText()+"|"+new String(tSenha.getPassword()));
                            String retorno = (String) entrada.readObject();
                            StringTokenizer st = new StringTokenizer(retorno, "|");
                            uu.setId(Integer.valueOf(st.nextToken()));
                            uu.setFullName(st.nextToken());
                            uu.setUsername(st.nextToken());
                            uu.setEmail(st.nextToken());
                            uu.setStatus(Boolean.valueOf(st.nextToken()));
                            String validade = st.nextToken();
                            
                            if(!validade.equals("null") && validade != null) {
                                uu.setExpirationDate(new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(validade).getTime()));
                            }
                            
                            uu.setConnection(Integer.valueOf(st.nextToken()));
                            break;
                        case "2": //Enviar computer
                            saida.println(uu.getId()
                                    + "|" + getMotherboardManufacturer()
                                    + "|" + getMotherboardName()
                                    + "|" + getMotherboardSerialNumber()
                                    + "|" + getCPUSerial());

                            //Receber Computador
                            StringTokenizer stC = new StringTokenizer((String) entrada.readObject(), "|");
                            cc.setId(Integer.valueOf(stC.nextToken()));
                            cc.setManufacturer(stC.nextToken());
                            cc.setName(stC.nextToken());
                            cc.setSerialNumber(stC.nextToken());
                            cc.setSerialNumberCpu(stC.nextToken());
                            break;
                    }

                }

                //User u = Config.user = uDAO.login(tLogin.getText(), new String(tSenha.getPassword()));
                User u = Config.user = uu;

                //IF verificar usuario
                if (u.getUsername() != null && !"null".equals(u.getUsername())) {
                    //IF verificar data
                    NTPUDPClient timeClient = new NTPUDPClient();
                    InetAddress inetAddress = InetAddress.getByName(Config.cosultTime);
                    TimeInfo timeInfo = timeClient.getTime(inetAddress);
                    long returnTime = timeInfo.getReturnTime();
                    Date time = new Date(returnTime);
                    if (!time.before(u.getExpirationDate())) {
                        JOptionPane.showMessageDialog(TelaLogin.this, "Sua Licença expirou: " + new SimpleDateFormat("dd/MM/yyyy").format(u.getExpirationDate()) + "\nPara ajustar a situação, por favor entrar em contato com a administração.", "Aviso", JOptionPane.WARNING_MESSAGE);
                        System.exit(0);
                    }
                    
                    Computer c = new Computer();
                    //c = cDAO.verifiedComputer(u.getId(), manufacturer, name, serialNumber, serialNumberCpu);
                    c = cc;

                    if (c.getManufacturer().equals("null") && c.getName().equals("null") && c.getSerialNumber().equals("null")) {
                        //Enviar para Servidor
                        saida.println("4");
                        saida.println(u.getId());
                        if (u.getConnection() <= (Integer) entrada.readObject()) {
                            JOptionPane.showMessageDialog(TelaLogin.this, "Limite de licença!\nNúmero de Liçenca: " + u.getConnection(), "Aviso", JOptionPane.WARNING_MESSAGE);
                            System.exit(0);
                        }

                        int jop;
                        jop = JOptionPane.showConfirmDialog(TelaLogin.this, "Detectamos que esta acessando de uma nova maquina!\n\nDeseja confirmar o cadastro?");
                        switch (jop) {
                            case JOptionPane.YES_OPTION:
                                saida.println("3");
                                saida.println(uu.getId()
                                    + "|" + getMotherboardManufacturer()
                                    + "|" + getMotherboardName()
                                    + "|" + getMotherboardSerialNumber()
                                    + "|" + getMotherboardVersion()
                                    + "|" + getCPUSerial()
                                    + "|" + getCPUName());
                                
                                if((Integer) entrada.readObject() > 0) {
                                    JOptionPane.showMessageDialog(TelaLogin.this, "Nova maquina cadastrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                                }
                                break;
                            case JOptionPane.NO_OPTION:
                                System.exit(0);
                            default:
                                System.exit(0);
                        }
                    }

                    saida.close();
                    entrada.close();
                    dispose();
                    new TelaPrincipal().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(TelaLogin.this, "Usuario e Senha Inválido!", "Erro (-100)", JOptionPane.ERROR_MESSAGE);
                    tornaVisivel();
                }
            } catch (Exception ex) {
                new TelaErro(1, ex.getStackTrace()).setVisible(true);
            }
            tl.dispose();
        }
    };
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if (visual.equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (String a : args) {
                    if (a.equals("dvp")) {
                        Config.developer = true;
                        Config.titulo = "(Modo Desenvolvedor)";
                    }
                }
                
                try {
                    Config.socket = new Socket(Config.url, Config.porta);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Sem conexão com a internet!\nVerifique sua conexão com a internet", "Aviso!", JOptionPane.WARNING_MESSAGE);
                    tl.dispose();
                    System.exit(0);
                }
                
                new TelaLogin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tLogin;
    private javax.swing.JPasswordField tSenha;
    // End of variables declaration//GEN-END:variables

}
