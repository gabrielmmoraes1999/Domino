package view;

import database.Sybase;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;
import modelo.Empresa;
import modelo.EmpresaDAO;
import util.Config;

/**
 *
 * @author Gabriel Moraes
 */
public final class InternaEmpresa extends javax.swing.JInternalFrame {

    TableRowSorter trs;
    DefaultTableModel modelo;
    EmpresaDAO eDAO = new EmpresaDAO();
    
    public InternaEmpresa() {
        if(!eDAO.carregarPorConversao(Config.conversao.getId()).isEmpty()) {
            initComponents();
            setFrameIcon(new javax.swing.ImageIcon(new Config().getIcon()));
            carregarEmpresas();
        } else if(Config.conversao.getId() == 0) {
            JOptionPane.showMessageDialog(null,"Nenhuma conversão aberta!", "Aviso!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,"Nenhuma empresa encontrada!", "Aviso!", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public boolean verificarConexao() {
        boolean verificar = false;
        try {
            Sybase sb = new Sybase();
            Sybase.host = Config.conversao.getHost();
            Sybase.porta = Config.conversao.getPorta();
            Sybase.nome = Config.conversao.getNome();
            Sybase.usuario = Config.conversao.getUsuario();
            Sybase.senha = Config.conversao.getSenha();
            sb.connect();
            sb.disconnect();
            verificar = true;
        } catch (SQLException ee) {
            
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
        return verificar;
    }
    
    public void carregarEmpresas() {
        try {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            MaskFormatter maskCNPJ = new MaskFormatter("##.###.###/####-##");
            maskCNPJ.setValueContainsLiteralCharacters(false);
            
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            maskCPF.setValueContainsLiteralCharacters(false);
            
            modelo = (DefaultTableModel) tEmpresas.getModel();
            modelo.setNumRows(0);
            
            boolean verificar = verificarConexao();

            for(Empresa e : eDAO.carregarPorConversaoOrdenarPorCodEmpresa(Config.conversao.getId())) {
                String configurada;
                Sybase.host = Config.conversao.getHost();
                Sybase.porta = Config.conversao.getPorta();
                Sybase.nome = Config.conversao.getNome();
                Sybase.usuario = Config.conversao.getUsuario();
                Sybase.senha = Config.conversao.getSenha();
                
                if(verificar) {
                    if(eDAO.verificarRubricasDaEmpresa(String.valueOf(e.getCodigo()))) {
                        configurada = "Sim";
                    } else {
                        configurada = "Não";
                    }
                } else {
                    configurada = "-";
                }
                
                if(e.getCodigo() != 0) {
                    String cnpj;
                    
                    switch(e.getCnpj().length()) {
                        case 11:
                            cnpj = maskCPF.valueToString(e.getCnpj());
                            break;
                        case 14:
                            cnpj = maskCNPJ.valueToString(e.getCnpj());
                            break;
                        default:
                            cnpj = e.getCnpj();
                            break;
                    }
                    
                    modelo.addRow(
                            new Object[] {
                                e.getCodigo(), e.getRazaoSocial(), cnpj, formato.format(e.getInicioDasAtividades()), configurada
                            }
                    );
                }
            }
        } catch (ParseException ex) {
            new TelaErro(7, ex.getStackTrace()).setVisible(true);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tEmpresas = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Empresa");

        tEmpresas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Código", "Nome", "CNPJ", "Inicio das Atividades", "Configurada"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tEmpresas.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tEmpresas);
        if (tEmpresas.getColumnModel().getColumnCount() > 0) {
            tEmpresas.getColumnModel().getColumn(0).setMinWidth(0);
            tEmpresas.getColumnModel().getColumn(0).setMaxWidth(100);
            tEmpresas.getColumnModel().getColumn(1).setMinWidth(250);
            tEmpresas.getColumnModel().getColumn(2).setMinWidth(150);
            tEmpresas.getColumnModel().getColumn(2).setMaxWidth(250);
            tEmpresas.getColumnModel().getColumn(3).setMinWidth(150);
            tEmpresas.getColumnModel().getColumn(3).setMaxWidth(200);
            tEmpresas.getColumnModel().getColumn(4).setMinWidth(100);
            tEmpresas.getColumnModel().getColumn(4).setMaxWidth(250);
        }

        jButton1.setText("Atualizar");
        jButton1.setFocusable(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        carregarEmpresas();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTable tEmpresas;
    // End of variables declaration//GEN-END:variables
}
