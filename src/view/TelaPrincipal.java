package view;

import desenvolvedor.InternaGerarArquivoBancoDados;
import desenvolvedor.InternaSQL;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import modelo.Backup;
import modelo.BackupDAO;
import modelo.Conversao;
import modelo.ConversaoDAO;
import modelo.EmpregadoDAO;
import modelo.NovidadeDAO;
import modelo.RubricaConfiguracaoDAO;
import modelo.Update;
import modelo.VersaoBancoDAO;
import relatorio.Completo;
import util.Config;
import static util.Config.visual;
import util.PosicaoJanela;
import util.Sistema;

/**
 *
 * @author Gabriel Moraes
 */
public final class TelaPrincipal extends javax.swing.JFrame {

    /**
     * Creates new form TelaPrincial
     */
    boolean parabens = false;
    
    Color color = null;
    Update u = null;
    PosicaoJanela pj = new PosicaoJanela();
    Sistema s = new Sistema();
    EmpregadoDAO eDAO = new EmpregadoDAO();
    InternaAvisoDeProcesso iap = null;
    RubricaConfiguracaoDAO rcDAO = new RubricaConfiguracaoDAO();

    public TelaPrincipal() {
        if (primeiroAcesso() != 0) {
            ultimaConversaoAberta();
        } else {
            Config.conversao = new Conversao();
        }
        
        initComponents();
        setIconImage(new Config().getIcon());
        listaConversao.setVisible(false);
        listaDeModulos.setVisible(false);
        setExtendedState(MAXIMIZED_BOTH);
        corInicial();
        jButton1.setVisible(false);

        //Abrir JANELA APLICA SQL
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == e.KEY_RELEASED && e.getKeyCode() == KeyEvent.VK_F7 && Config.developer) {
                    InternaSQL iSQL = new InternaSQL();
                    pj.centralizarJanela(iSQL, jPainelPrincipal);
                    return true;
                } else if (e.getID() == e.KEY_RELEASED && e.getKeyCode() == KeyEvent.VK_F8 && Config.developer) {
                    InternaGerarArquivoBancoDados iGABD = new InternaGerarArquivoBancoDados();
                    pj.centralizarJanela(iGABD, jPainelPrincipal);
                    return true;
                } else if (e.getID() == e.KEY_RELEASED && e.getKeyCode() == KeyEvent.VK_F10) {
                    InternaAplicaSQL iASQL = new InternaAplicaSQL();
                    pj.centralizarJanela(iASQL, jPainelPrincipal);
                    return true;
                }
                return false;
            }
        });

        //Verificar atualização do banco de dados
        new Thread(atualizacao).start();
        
        //Verificar se a atualização disponivel depois de 6 segundos
        new Thread(verificarAtualizacao).start();
    }

    public void corInicial() {
        color = new Color(0, 63, 75);
        bConversao.setBackground(color);
        bCompany.setBackground(color);
        bRubrica.setBackground(color);
        jButton1.setBackground(color);
        jButton2.setBackground(color);
        jButton6.setBackground(color);
        jButton9.setBackground(color);
        jButton10.setBackground(color);
        jButton11.setBackground(color);
        bConversoes.setBackground(new Color(0, 85, 102));
        bUpdate.setBackground(color);
    }

    public Color passaMouse() {
        return new Color(0, 50, 60);
    }

    public String retornaNumeroConversao() {
        if(Config.conversao.getNumero() != null) {
            return Config.conversao.getNumero();
        } else {
            return "";
        }
    }
    
    public DefaultListModel retornaConversoes() {
        DefaultListModel model = new DefaultListModel();
        ConversaoDAO cDAO = new ConversaoDAO();

        for (Conversao c : cDAO.lerTodos()) {
            model.addElement((String) c.getNumero());
        }

        return model;
    }

    public void ultimaConversaoAberta() {
        try {
            ConversaoDAO cDAO = new ConversaoDAO();
            
            if (new File(System.getProperty("user.dir") + "\\config.ini").exists()) {
                Properties config = new Properties();
                config.load(new FileInputStream(System.getProperty("user.dir") + "\\config.ini"));
                String idString = config.getProperty("[conversao]");
                
                if(idString != null && !idString.equals("")) {
                    int id = Integer.valueOf(idString);
                    Config.conversao = cDAO.carregarPorID(id);
                    
                    //Se o ID for 0, retorna com a ultima conversao
                    if(id == 0 || Config.conversao.getId() == 0) {
                        Config.conversao = cDAO.carregarPorID(cDAO.retornaUltimoID());
                    }
                } else {
                    Config.conversao = cDAO.carregarPorID(cDAO.retornaUltimoID());
                }
            } else {
                Config.conversao = cDAO.carregarPorID(cDAO.retornaUltimoID());
            }
        } catch (FileNotFoundException ex) {
            new TelaErro(2, ex.getStackTrace()).setVisible(true);
        } catch (IOException ex) {
            new TelaErro(3, ex.getStackTrace()).setVisible(true);
        }
    }

    public int primeiroAcesso() {
        int totalDeConversoes = new ConversaoDAO().lerTodos().size();
        return totalDeConversoes;
    }

    public void verificarAtualizacao() {
        u = new Update();
        try {
            //u = uDAO.returnLast();
            if (Config.codPublic < u.getCodPublic()) {
                if (JOptionPane.showConfirmDialog(this, "Ei detectamos nova atualização disponivel!\nDeseja iniciar o download agora?", "Atualização", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    new Thread(t1).start();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Parabens!\nSeu sistema esta atualizado!", "Aviso!", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }

    Runnable verificarAtualizacao = new Runnable() {
        @Override
        public void run() {
            try {
                if(!parabens) {
                    sleep(6000);
                }
                Config.socket = new Socket(Config.url, Config.porta);
                PrintStream saida = new PrintStream(Config.socket.getOutputStream());
                ObjectInputStream entrada = new ObjectInputStream(Config.socket.getInputStream());

                saida.println("5");
                StringTokenizer st = new StringTokenizer((String) entrada.readObject(), "|");
                u = new Update();
                u.setCodPublic(Integer.valueOf(st.nextToken()));
                u.setVersion(st.nextToken());
                u.setLink(st.nextToken());
                
                saida.close();
                entrada.close();

                if (Config.codPublic < u.getCodPublic()) {
                    if (JOptionPane.showConfirmDialog(TelaPrincipal.this, "Ei detectamos nova atualização disponivel!\nDeseja iniciar o download agora?", "Atualização", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                        new Thread(t1).start();
                    }
                } else {
                    if(parabens) {
                        JOptionPane.showMessageDialog(TelaPrincipal.this, "Parabens!\nSeu sistema esta atualizado!", "Aviso!", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (IOException ex) {
                new TelaErro(3, ex.getStackTrace()).setVisible(true);
            } catch (ClassNotFoundException ex) {
                new TelaErro(12, ex.getStackTrace()).setVisible(true);
            } catch (InterruptedException ex) {
                new TelaErro(4, ex.getStackTrace()).setVisible(true);
            }
        }
    };

    Runnable t1 = new Runnable() {
        @Override
        public void run() {
            Sistema s = new Sistema();
            String arquivo = s.baixarAtualizacao(u.getLink(), u);
            s.descompactaAtualizacao(arquivo, u);
            if (JOptionPane.showConfirmDialog(TelaPrincipal.this, "Download Concluído!\nDeseja atualizar agora?", "Atualizar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                File arqInstala = new File(Config.arquivoAtualiza);
                try {
                    Process p = Runtime.getRuntime().exec("cmd.exe /c \"" + arqInstala.getAbsolutePath() + "\"");
                } catch (IOException ex) {
                    new TelaErro(3, ex.getStackTrace()).setVisible(true);
                }
                System.exit(0);
            }
        }
    };

    Runnable atualizacao = new Runnable() {
        @Override
        public void run() {
            try {
                sleep(1000);
                if (Config.versaoBancoDados > new VersaoBancoDAO().retornaVersaoAtualBanco()) {
                    InternaAtualizacaoBanco iab = new InternaAtualizacaoBanco();
                    pj.centralizarJanela(iab, jPainelPrincipal);
                }
            } catch (InterruptedException ex) {
                new TelaErro(4, ex.getStackTrace()).setVisible(true);
            }
        }
    };
    
    Runnable relatorioCompleto = new Runnable() {
        @Override
        public void run() {
            try {
                String caminho = System.getProperty("user.home")+"\\Documents\\Domino\\Conversoes\\"+Config.conversao.getNumero();
                String arquivo = "info.html";
                
                Sistema.criarArqRelatorio(caminho, arquivo, new Completo(Config.conversao.getId()));
                
                iap.dispose();
                Desktop.getDesktop().browse(new URI(new File(caminho + "\\" + arquivo).toURI().toString()));
            } catch (FileNotFoundException ex) {
                new TelaErro(2, ex.getStackTrace()).setVisible(true);
            } catch (UnsupportedEncodingException ex) {
                new TelaErro(6, ex.getStackTrace()).setVisible(true);
            } catch (URISyntaxException ex) {
                new TelaErro(8, ex.getStackTrace()).setVisible(true);
            } catch (IOException ex) {
                new TelaErro(3, ex.getStackTrace()).setVisible(true);
            }
        }
    };

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton6 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        bConversoes = new javax.swing.JButton();
        listaConversao = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        bConversao = new javax.swing.JButton();
        bCompany = new javax.swing.JButton();
        bRubrica = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        bUpdate = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPainelPrincipal = new javax.swing.JDesktopPane();
        listaDeModulos = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Dominó Folha Versão: "+Config.ver+" "+Config.titulo);
        setMinimumSize(new java.awt.Dimension(800, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(0, 85, 102));

        jPanel1.setBackground(new java.awt.Color(0, 85, 102));

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconUser32.png"))); // NOI18N
        jButton6.setBorderPainted(false);
        jButton6.setContentAreaFilled(false);
        jButton6.setFocusable(false);
        jButton6.setOpaque(true);
        jButton6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton6MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton6MouseExited(evt);
            }
        });
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconBackup32.png"))); // NOI18N
        jButton9.setContentAreaFilled(false);
        jButton9.setDefaultCapable(false);
        jButton9.setFocusable(false);
        jButton9.setOpaque(true);
        jButton9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton9MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton9MouseExited(evt);
            }
        });
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconRestore32.png"))); // NOI18N
        jButton10.setContentAreaFilled(false);
        jButton10.setDefaultCapable(false);
        jButton10.setFocusable(false);
        jButton10.setOpaque(true);
        jButton10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton10MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton10MouseExited(evt);
            }
        });
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        bConversoes.setForeground(new java.awt.Color(255, 255, 255));
        bConversoes.setText("Conversão: "+retornaNumeroConversao());
        bConversoes.setBorderPainted(false);
        bConversoes.setContentAreaFilled(false);
        bConversoes.setFocusable(false);
        bConversoes.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        bConversoes.setOpaque(true);
        bConversoes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                bConversoesMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                bConversoesMouseExited(evt);
            }
        });
        bConversoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bConversoesActionPerformed(evt);
            }
        });

        jList1.setModel(retornaConversoes());
        jList1.setFocusable(false);
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout listaConversaoLayout = new javax.swing.GroupLayout(listaConversao);
        listaConversao.setLayout(listaConversaoLayout);
        listaConversaoLayout.setHorizontalGroup(
            listaConversaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        listaConversaoLayout.setVerticalGroup(
            listaConversaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
        );

        bConversao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconConversao32.png"))); // NOI18N
        bConversao.setBorderPainted(false);
        bConversao.setContentAreaFilled(false);
        bConversao.setFocusable(false);
        bConversao.setOpaque(true);
        bConversao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                bConversaoMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                bConversaoMouseExited(evt);
            }
        });
        bConversao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bConversaoActionPerformed(evt);
            }
        });

        bCompany.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconCompany32.png"))); // NOI18N
        bCompany.setBorderPainted(false);
        bCompany.setContentAreaFilled(false);
        bCompany.setFocusable(false);
        bCompany.setOpaque(true);
        bCompany.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                bCompanyMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                bCompanyMouseExited(evt);
            }
        });
        bCompany.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCompanyActionPerformed(evt);
            }
        });

        bRubrica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconRubrica32.png"))); // NOI18N
        bRubrica.setBorderPainted(false);
        bRubrica.setContentAreaFilled(false);
        bRubrica.setFocusable(false);
        bRubrica.setOpaque(true);
        bRubrica.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                bRubricaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                bRubricaMouseExited(evt);
            }
        });
        bRubrica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRubricaActionPerformed(evt);
            }
        });

        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconHelp32.png"))); // NOI18N
        jButton11.setBorderPainted(false);
        jButton11.setContentAreaFilled(false);
        jButton11.setFocusable(false);
        jButton11.setOpaque(true);
        jButton11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton11MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton11MouseExited(evt);
            }
        });
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        bUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconUpdate32.png"))); // NOI18N
        bUpdate.setBorderPainted(false);
        bUpdate.setContentAreaFilled(false);
        bUpdate.setFocusable(false);
        bUpdate.setOpaque(true);
        bUpdate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                bUpdateMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                bUpdateMouseExited(evt);
            }
        });
        bUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bUpdateActionPerformed(evt);
            }
        });

        jButton12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton12.setForeground(new java.awt.Color(255, 255, 255));
        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-domino-24.png"))); // NOI18N
        jButton12.setText("Dominó Folha");
        jButton12.setBorderPainted(false);
        jButton12.setContentAreaFilled(false);
        jButton12.setDefaultCapable(false);
        jButton12.setFocusable(false);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-domino-96.png"))); // NOI18N
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setFocusable(false);
        jButton1.setOpaque(true);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconDiagnosis32.png"))); // NOI18N
        jButton2.setContentAreaFilled(false);
        jButton2.setFocusable(false);
        jButton2.setOpaque(true);
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton2MouseExited(evt);
            }
        });
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton12)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(bConversao, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bCompany, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bRubrica, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 216, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(294, 294, 294)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(bConversoes, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(listaConversao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(bConversao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bCompany, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bRubrica, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton9)
                            .addComponent(jButton10)
                            .addComponent(bUpdate)
                            .addComponent(jButton11)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(bConversoes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(listaConversao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPainelPrincipal.setBackground(new java.awt.Color(0, 90, 102));

        listaDeModulos.setBackground(new java.awt.Color(0, 100, 102));

        jButton3.setBackground(new java.awt.Color(204, 51, 0));
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leaf_16.png"))); // NOI18N
        jButton3.setText("Folha");
        jButton3.setBorderPainted(false);
        jButton3.setContentAreaFilled(false);

        jButton4.setBackground(new java.awt.Color(0, 153, 102));
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/document_icon_16.png"))); // NOI18N
        jButton4.setText("NF-e");
        jButton4.setBorderPainted(false);
        jButton4.setContentAreaFilled(false);

        jButton5.setBackground(new java.awt.Color(51, 51, 255));
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accounting_icon_16.png"))); // NOI18N
        jButton5.setText("Contabilidade");
        jButton5.setBorderPainted(false);
        jButton5.setContentAreaFilled(false);

        javax.swing.GroupLayout listaDeModulosLayout = new javax.swing.GroupLayout(listaDeModulos);
        listaDeModulos.setLayout(listaDeModulosLayout);
        listaDeModulosLayout.setHorizontalGroup(
            listaDeModulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        listaDeModulosLayout.setVerticalGroup(
            listaDeModulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(listaDeModulosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4)
                .addContainerGap(409, Short.MAX_VALUE))
        );

        jPainelPrincipal.setLayer(listaDeModulos, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jPainelPrincipalLayout = new javax.swing.GroupLayout(jPainelPrincipal);
        jPainelPrincipal.setLayout(jPainelPrincipalLayout);
        jPainelPrincipalLayout.setHorizontalGroup(
            jPainelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPainelPrincipalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(listaDeModulos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPainelPrincipalLayout.setVerticalGroup(
            jPainelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(listaDeModulos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText(Config.user.getFullName());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPainelPrincipal)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPainelPrincipal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(6, 6, 6))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (JOptionPane.showConfirmDialog(this, "Tem certeza de que deseja fechar esta janela?", "Sair", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            try {
                Config.criarConfig(String.valueOf(Config.conversao.getId()));
                Config.socket.close();
            } catch (IOException ex) {
                new TelaErro(3, ex.getStackTrace()).setVisible(true);
            }
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        /*if(listaDeModulos.getVisibleRect().width == 0) {
            listaDeModulos.setVisible(true);
        } else {
            listaDeModulos.setVisible(false);
        }*/
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if (!eDAO.carregarPorConversao(Config.conversao.getId()).isEmpty()) {
            InternaEmpregado ip = new InternaEmpregado();
            pj.centralizarJanela(ip, jPainelPrincipal);
        } else {
            JOptionPane.showMessageDialog(this, "Nenhum empregado não foi localizado!", "Aviso!", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        if (JOptionPane.showConfirmDialog(this, "Deseja realizar um backup agora?", "Backup", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            Sistema.backup();
            Backup b = new Backup();
            BackupDAO bDAO = new BackupDAO();
            Timestamp ts = new Timestamp(new Date().getTime());
            b.setData(ts);
            bDAO.inserir(b);
            JOptionPane.showMessageDialog(this, "Backup realizado com sucesso!","Sucesso!", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        InternaRestauraBackup irb = new InternaRestauraBackup();
        //if(irb.isVisible() == true) {
        //jPainelPrincipal.moveToFront(irb);
        //} else {
        //pj.centralizarJanela(irb, jPainelPrincipal);
        //}
        pj.centralizarJanela(irb, jPainelPrincipal);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton6MouseEntered
        jButton6.setBackground(passaMouse());
        jButton6.setToolTipText("Empregado");
    }//GEN-LAST:event_jButton6MouseEntered

    private void jButton9MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton9MouseEntered
        jButton9.setBackground(passaMouse());
        jButton9.setToolTipText("Backup");
    }//GEN-LAST:event_jButton9MouseEntered

    private void jButton10MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton10MouseEntered
        jButton10.setBackground(passaMouse());
        jButton10.setToolTipText("Restaurar");
    }//GEN-LAST:event_jButton10MouseEntered

    private void jButton6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton6MouseExited
        jButton6.setBackground(color);
    }//GEN-LAST:event_jButton6MouseExited

    private void jButton9MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton9MouseExited
        jButton9.setBackground(color);
    }//GEN-LAST:event_jButton9MouseExited

    private void jButton10MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton10MouseExited
        jButton10.setBackground(color);
    }//GEN-LAST:event_jButton10MouseExited

    private void jButton11MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton11MouseEntered
        jButton11.setBackground(passaMouse());
        jButton11.setToolTipText("Sobre");
    }//GEN-LAST:event_jButton11MouseEntered

    private void jButton11MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton11MouseExited
        jButton11.setBackground(color);
    }//GEN-LAST:event_jButton11MouseExited

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        TelaSobre ts = new TelaSobre();
        ts.setVisible(true);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void bConversoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bConversoesActionPerformed
        if (listaConversao.getVisibleRect().width == 0) {
            jList1.setModel(retornaConversoes());
            listaConversao.setVisible(true);
        } else {
            listaConversao.setVisible(false);
        }
    }//GEN-LAST:event_bConversoesActionPerformed

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        ConversaoDAO cDAO = new ConversaoDAO();
        
        if (!Config.conversao.getNumero().equals((String) jList1.getSelectedValue())) {
            if (jPainelPrincipal.getAllFrames().length == 0) {
                Config.conversao = cDAO.carregarPorNumero((String) jList1.getSelectedValue());
                
                bConversoes.setText("Conversão: " + Config.conversao.getNumero());
                listaConversao.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Para trocar de conversão terá que fechar todas as janelas abertas.", "Atenção!", JOptionPane.WARNING_MESSAGE);
            }
        }
        listaConversao.setVisible(false);
    }//GEN-LAST:event_jList1MouseClicked

    private void bConversoesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bConversoesMouseEntered
        bConversoes.setBackground(passaMouse());
    }//GEN-LAST:event_bConversoesMouseEntered

    private void bConversoesMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bConversoesMouseExited
        bConversoes.setBackground(new Color(0, 85, 102));
    }//GEN-LAST:event_bConversoesMouseExited

    private void bConversaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bConversaoActionPerformed
        if (primeiroAcesso() == 0) {
            JOptionPane.showMessageDialog(this, "Nenhuma conversão localizada. Entrando em modo de inclusão!", "Aviso!", JOptionPane.INFORMATION_MESSAGE);
        }

        InternaConversao ic = new InternaConversao();
        pj.centralizarJanela(ic, jPainelPrincipal);
    }//GEN-LAST:event_bConversaoActionPerformed

    private void bConversaoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bConversaoMouseEntered
        bConversao.setBackground(passaMouse());
        bConversao.setToolTipText("Conversão");
    }//GEN-LAST:event_bConversaoMouseEntered

    private void bConversaoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bConversaoMouseExited
        bConversao.setBackground(color);
    }//GEN-LAST:event_bConversaoMouseExited

    private void bCompanyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCompanyActionPerformed
        InternaEmpresa ie = new InternaEmpresa();
        pj.centralizarJanela(ie, jPainelPrincipal);
    }//GEN-LAST:event_bCompanyActionPerformed

    private void bUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bUpdateActionPerformed
        //verificarAtualizacao();
        parabens = true;
        new Thread(verificarAtualizacao).start();
    }//GEN-LAST:event_bUpdateActionPerformed

    private void bUpdateMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bUpdateMouseEntered
        bUpdate.setBackground(passaMouse());
        bUpdate.setToolTipText("Verificar Atualizações");
    }//GEN-LAST:event_bUpdateMouseEntered

    private void bUpdateMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bUpdateMouseExited
        bUpdate.setBackground(color);
    }//GEN-LAST:event_bUpdateMouseExited

    private void bCompanyMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bCompanyMouseEntered
        bCompany.setBackground(passaMouse());
        bCompany.setToolTipText("Empresa");
    }//GEN-LAST:event_bCompanyMouseEntered

    private void bCompanyMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bCompanyMouseExited
        bCompany.setBackground(color);
    }//GEN-LAST:event_bCompanyMouseExited

    private void bRubricaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRubricaActionPerformed
        if (rcDAO.retornaIDPrimeiraRubricaPorConversao(Config.conversao.getId()) != 0) {
            InternaRubrica ir = new InternaRubrica();
            pj.centralizarJanela(ir, jPainelPrincipal);
        } else if(Config.conversao.getId() == 0) {
            JOptionPane.showMessageDialog(this, "Nenhuma conversão aberta!", "Aviso!", JOptionPane.INFORMATION_MESSAGE);
        }else {
            JOptionPane.showMessageDialog(this, "Nenhuma rubrica encontrada!", "Aviso!", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_bRubricaActionPerformed

    private void bRubricaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bRubricaMouseEntered
        bRubrica.setBackground(passaMouse());
        bRubrica.setToolTipText("Rubrica");
    }//GEN-LAST:event_bRubricaMouseEntered

    private void bRubricaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bRubricaMouseExited
        bRubrica.setBackground(color);
    }//GEN-LAST:event_bRubricaMouseExited

    private void jButton2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseEntered
        jButton2.setBackground(passaMouse());
        jButton2.setToolTipText("Diagnóstico Completo");
    }//GEN-LAST:event_jButton2MouseEntered

    private void jButton2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseExited
        jButton2.setBackground(color);
    }//GEN-LAST:event_jButton2MouseExited

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(Config.conversao != null) {
            iap = new InternaAvisoDeProcesso();
            pj.centralizarJanela(iap, jPainelPrincipal);
            new Thread(relatorioCompleto).start();
        } else {
            JOptionPane.showMessageDialog(this, "Nenhuma conversão aberta!", "Aviso!", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new TelaPrincipal().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCompany;
    private javax.swing.JButton bConversao;
    public static javax.swing.JButton bConversoes;
    private javax.swing.JButton bRubrica;
    private javax.swing.JButton bUpdate;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList<String> jList1;
    public static javax.swing.JDesktopPane jPainelPrincipal;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel listaConversao;
    private javax.swing.JPanel listaDeModulos;
    // End of variables declaration//GEN-END:variables
}
