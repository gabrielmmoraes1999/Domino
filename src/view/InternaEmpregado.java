package view;

import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;
import modelo.Empregado;
import modelo.EmpregadoDAO;
import util.Config;

/**
 *
 * @author Gabriel Moraes
 */
public class InternaEmpregado extends javax.swing.JInternalFrame {
    
    TableRowSorter trs;
    DefaultTableModel modelo;
    EmpregadoDAO eDAO = new EmpregadoDAO();
    
    ArrayList<Empregado> listOriginal = new ArrayList<>();

    public InternaEmpregado() {
        setFrameIcon(new javax.swing.ImageIcon(new Config().getIcon()));
        initComponents();
        lerDeParaEmpregados();
    }
    
    private void lerDeParaEmpregados() {
        try {
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            maskCPF.setValueContainsLiteralCharacters(false);
            
            modelo = (DefaultTableModel) tbEmpregados.getModel();
            modelo.setNumRows(0);
            
            listOriginal = eDAO.carregarPorConversao(Config.conversao.getId());
            
            for(Empregado e : listOriginal) {
                modelo.addRow(new Object[] {
                    e.getCodEmpresa(), e.getCodigo(), e.getCodEsocial(), e.getNome(), maskCPF.valueToString(e.getCpf())
                });
            }
        } catch (ParseException ex) {
            new TelaErro(7, ex.getStackTrace()).setVisible(true);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tEmpresa = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbEmpregados = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Empregado");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtro"));

        jLabel1.setText("Empresa:");

        tEmpresa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tEmpresaKeyTyped(evt);
            }
        });

        jButton1.setText("Salvar");
        jButton1.setFocusable(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tbEmpregados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Empresa", "Código", "Cód eSocial", "Nome", "CPF"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbEmpregados.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tbEmpregados);
        if (tbEmpregados.getColumnModel().getColumnCount() > 0) {
            tbEmpregados.getColumnModel().getColumn(0).setMinWidth(0);
            tbEmpregados.getColumnModel().getColumn(0).setMaxWidth(100);
            tbEmpregados.getColumnModel().getColumn(1).setMinWidth(0);
            tbEmpregados.getColumnModel().getColumn(1).setMaxWidth(50);
            tbEmpregados.getColumnModel().getColumn(2).setMinWidth(150);
            tbEmpregados.getColumnModel().getColumn(2).setMaxWidth(250);
            tbEmpregados.getColumnModel().getColumn(4).setMinWidth(200);
            tbEmpregados.getColumnModel().getColumn(4).setMaxWidth(200);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        ArrayList<Empregado> list = new ArrayList<Empregado>();
        
        for(int i = 0; i < tbEmpregados.getModel().getRowCount(); i++) {
            Empregado e = new Empregado();
            for(int j = 0; j < tbEmpregados.getModel().getColumnCount(); j++) {
                switch(j) {
                    case 0:
                        e.setCodEmpresa((Integer) tbEmpregados.getModel().getValueAt(i, j));
                        break;
                    case 1:
                        e.setCodigo((Integer) tbEmpregados.getModel().getValueAt(i, j));
                        break;
                    case 2:
                        e.setCodEsocial((String) tbEmpregados.getModel().getValueAt(i, j));
                        break;
                    case 3:
                        e.setNome((String) tbEmpregados.getModel().getValueAt(i, j));
                        break;
                    case 4:
                        e.setCpf((String) tbEmpregados.getModel().getValueAt(i, j));
                        break;
                }
            }
            
            list.add(e);
        }
        
        ArrayList<Empregado> listAlterados = new ArrayList<Empregado>();
        
        for(Empregado e : list) {
            for(Empregado ee : listOriginal) {
                if(e.getCodEmpresa() == ee.getCodEmpresa() && e.getCodigo() == ee.getCodigo() && !e.getCodEsocial().equals(ee.getCodEsocial())) {
                    listAlterados.add(e);
                }
            }
        }
        
        eDAO.alterarVarios(listAlterados, Config.conversao.getId());
        JOptionPane.showMessageDialog(InternaEmpregado.this, "Alterações gravadas com sucesso!","Sucesso!", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tEmpresaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tEmpresaKeyTyped
        trs = new TableRowSorter(modelo);
        trs.setRowFilter(RowFilter.regexFilter(tEmpresa.getText(), 0));
        tbEmpregados.setRowSorter(trs);
    }//GEN-LAST:event_tEmpresaKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField tEmpresa;
    private javax.swing.JTable tbEmpregados;
    // End of variables declaration//GEN-END:variables
}
