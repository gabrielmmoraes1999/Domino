package view;

import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JOptionPane;
import modelo.EmpregadoDAO;
import modelo.LayoutDominioDAO;
import util.Config;
import util.Sistema;

/**
 *
 * @author Gabriel Moraes
 */
public class InternaZeramento extends javax.swing.JInternalFrame {
    
    Sistema s = new Sistema();
    EmpregadoDAO eDAO = new EmpregadoDAO();
    LayoutDominioDAO ldDAO = new LayoutDominioDAO();

    public InternaZeramento() {
        setFrameIcon(new javax.swing.ImageIcon(new Config().getIcon()));
        initComponents();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        bInicial = new javax.swing.JButton();
        tCompInicial = new javax.swing.JFormattedTextField();
        tCompFinal = new javax.swing.JFormattedTextField();

        setClosable(true);
        setTitle("Zeramento");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Período"));

        jLabel1.setText("Competência Inicial:");

        jLabel2.setText("Competência Final:");

        bInicial.setText("Salvar");
        bInicial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bInicialActionPerformed(evt);
            }
        });

        try {
            tCompInicial.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        tCompInicial.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tCompInicial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tCompInicialActionPerformed(evt);
            }
        });
        tCompInicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tCompInicialKeyPressed(evt);
            }
        });

        try {
            tCompFinal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        tCompFinal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tCompFinal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tCompFinalKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(bInicial)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tCompFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tCompInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tCompInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tCompFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bInicial)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bInicialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bInicialActionPerformed
        File file = new File(System.getProperty("user.home")+"\\Documents\\Domino\\Conversoes\\"+Config.conversao.getNumero());
        
        if(!file.exists()) {
            file.mkdirs();
        }
        String arquivo = System.getProperty("user.home")+"\\Documents\\Domino\\Conversoes\\"+Config.conversao.getNumero()+"\\Zeramento.txt";
        s.gravarArquivo(ldDAO.zeramento(tCompInicial.getText(), tCompFinal.getText()), arquivo);
        JOptionPane.showMessageDialog(this, "Arquivo criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }//GEN-LAST:event_bInicialActionPerformed

    private void tCompInicialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tCompInicialActionPerformed
        
    }//GEN-LAST:event_tCompInicialActionPerformed

    private void tCompInicialKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tCompInicialKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tCompFinal.requestFocus();
        }
    }//GEN-LAST:event_tCompInicialKeyPressed

    private void tCompFinalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tCompFinalKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            bInicial.requestFocus();
        }
    }//GEN-LAST:event_tCompFinalKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bInicial;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JFormattedTextField tCompFinal;
    private javax.swing.JFormattedTextField tCompInicial;
    // End of variables declaration//GEN-END:variables
}
