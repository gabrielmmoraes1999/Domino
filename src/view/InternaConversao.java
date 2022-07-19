package view;

import database.Sybase;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import jxl.read.biff.BiffException;
import modelo.Conversao;
import modelo.ConversaoDAO;
import modelo.EmpregadoDAO;
import modelo.Empresa;
import modelo.EmpresaDAO;
import modelo.Lancamento;
import modelo.LancamentoDAO;
import modelo.LayoutDominioDAO;
import modelo.Rubrica;
import modelo.RubricaConfiguracaoDAO;
import modelo.RubricaDAO;
import modelo.RubricaNaoLocalizadaDAO;
import outros.bancos.*;
import relatorio.Completo;
import relatorio.Simples;
import util.Config;

import util.PosicaoJanela;
import util.Sistema;
import static view.TelaPrincipal.jPainelPrincipal;

/**
 *
 * @author Gabriel Moraes
 */
public final class InternaConversao extends javax.swing.JInternalFrame {
    
    String property = System.getProperty("user.dir");
    
    boolean verficarConexao = false;
    
    private final String host = "localhost";
    private final String porta = "2638";
    private final String nomeBanco = "Contabil";
    private final String usuario = "";
    private final String senha = "";
    
    int idConversao;
    
    String arquivoRubricas = null;
    String arquivoOrigem = null;
    String arquivoConfig = null;
    String arquivoEmpresas = null;
    
    ConversaoDAO cDAO = new ConversaoDAO();
    PosicaoJanela pj = new PosicaoJanela();
    InternaAvisoDeProcesso iap = null;
    EmpregadoDAO eDAO = new EmpregadoDAO();
    Properties config = new Properties();
    Sistema s = new Sistema();
    TelaCarregamento tc;

    public InternaConversao() {
        initComponents();
        jButton12.setVisible(false);
        jButton13.setVisible(false);
        setFrameIcon(new javax.swing.ImageIcon(new Config().getIcon()));
        
        if(new ConversaoDAO().lerTodos().isEmpty()) {
            novaConversao();
        } else {
            if(Config.conversao.getId() != 0) {
                conversaoAtual(Config.conversao.getId());
            } else {
                novaConversao();
            }
        }
    }
    
    public void conversaoAtual(int id) {
        Conversao c = cDAO.carregarPorID(id);
        Config.sistema = c.getSistema();
        
        //tNConversao.setText(Config.nConversao);
        tNConversao.setText(Config.conversao.getNumero());
        tNConversao.setEnabled(false);
        tSistema.setSelectedIndex(Config.sistema);
        tSistema.setEnabled(false);
        tCaminho.setEnabled(false);
        tCaminho.setText("C:\\");
        jButton1.setEnabled(false);
        tHost.setText(c.getHost());
        tPorta.setText(c.getPorta());
        tNomeBanco.setText(c.getNome());
        tUsuario.setText(c.getUsuario());
        tSenha.setText(c.getSenha());
        tHost.setEnabled(false);
        tPorta.setEnabled(false);
        tNomeBanco.setEnabled(false);
        tUsuario.setEnabled(false);
        tSenha.setEnabled(false);
        jButton2.setEnabled(true);
        jButton3.setEnabled(true);
        jButton4.setEnabled(true);
        jButton5.setEnabled(true);
        jButton6.setEnabled(true);
        jButton7.setEnabled(true);
        jButton8.setEnabled(true);
        jButton8.setText("Novo");
        jButton9.setText("Editar");
        jButton9.setEnabled(true);
        jButton10.setEnabled(false);
        jButton11.setEnabled(true);
    }
    
    public void carregarUltimaConversao() {
        Conversao c = cDAO.carregarPorID(cDAO.retornaUltimoID());
        
        Config.sistema = c.getSistema();
        
        tNConversao.setText(c.getNumero());
        tNConversao.setEnabled(false);
        tSistema.setSelectedIndex(Config.sistema);
        tSistema.setEnabled(false);
        tCaminho.setEnabled(false);
        tCaminho.setText("C:\\");
        jButton1.setEnabled(false);
        tHost.setText(c.getHost());
        tPorta.setText(c.getPorta());
        tNomeBanco.setText(c.getNome());
        tUsuario.setText(c.getUsuario());
        tSenha.setText(c.getSenha());
        tHost.setEnabled(false);
        tPorta.setEnabled(false);
        tNomeBanco.setEnabled(false);
        tUsuario.setEnabled(false);
        tSenha.setEnabled(false);
        jButton2.setEnabled(true);
        jButton3.setEnabled(true);
        jButton4.setEnabled(true);
        jButton5.setEnabled(true);
        jButton6.setEnabled(true);
        jButton7.setEnabled(true);
        jButton8.setEnabled(true);
        jButton8.setText("Novo");
        jButton9.setText("Editar");
        jButton9.setEnabled(true);
        jButton10.setEnabled(false);
        jButton11.setEnabled(true);
    }
    
    public void novaConversao() {
        Conversao c = cDAO.carregarPorID(cDAO.retornaUltimoID());
        int numero = 0;
        
        if(c.getNumero() != null) {
            numero = Integer.valueOf(c.getNumero());
        }
        
        numero++;
        tNConversao.setText(s.completeToLeft(String.valueOf(numero), '0', 3));
        //tNConversao.setEnabled(true); Desativado na Atualização 4.0
        tSistema.setEnabled(true);
        tSistema.setSelectedIndex(0);
        tCaminho.setText("C:\\");
        tCaminho.setEnabled(true);
        jButton1.setEnabled(true);
        tHost.setText("localhost");
        tPorta.setText("2638");
        tNomeBanco.setText("Contabil");
        tUsuario.setText("");
        tSenha.setText("");
        tHost.setEnabled(true);
        tPorta.setEnabled(true);
        tNomeBanco.setEnabled(true);
        tUsuario.setEnabled(true);
        tSenha.setEnabled(true);
        jButton2.setEnabled(false);
        jButton3.setEnabled(true);
        jButton4.setEnabled(false);
        jButton5.setEnabled(false);
        jButton6.setEnabled(false);
        jButton7.setEnabled(false);
        jButton8.setText("Cancelar");
        jButton9.setEnabled(false);
        jButton10.setEnabled(true);
        jButton11.setEnabled(false);
    }
    
    public void cancelarConversao() {
        //tNConversao.setText(s.consultarConversoes());
        tNConversao.setEnabled(false);
        tSistema.setEnabled(false);
        tCaminho.setEnabled(false);
        jButton1.setEnabled(false);
        tHost.setText("localhost");
        tPorta.setText("2638");
        tNomeBanco.setText("Contabil");
        tUsuario.setText("");
        tSenha.setText("");
        tHost.setEnabled(false);
        tPorta.setEnabled(false);
        tNomeBanco.setEnabled(false);
        tUsuario.setEnabled(false);
        tSenha.setEnabled(false);
        jButton2.setEnabled(true);
        jButton3.setEnabled(true);
        jButton4.setEnabled(true);
        jButton5.setEnabled(true);
        jButton6.setEnabled(true);
        jButton7.setEnabled(true);
        jButton8.setEnabled(true);
        jButton8.setText("Novo");
        jButton9.setText("Editar");
        jButton9.setEnabled(true);
        jButton10.setEnabled(false);
        jButton11.setEnabled(true);
    }
    
    public void editarConversao() {
        tNConversao.setText("");
        //tNConversao.setEnabled(true);
        tSistema.setEnabled(true);
        tCaminho.setEnabled(true);
        tCaminho.setText("");
        jButton1.setEnabled(false);
        tHost.setText("");
        tPorta.setText("");
        tNomeBanco.setText("");
        tUsuario.setText("");
        tSenha.setText("");
        tHost.setEnabled(true);
        tPorta.setEnabled(true);
        tNomeBanco.setEnabled(true);
        tUsuario.setEnabled(true);
        tSenha.setEnabled(true);
        jButton2.setEnabled(false);
        jButton3.setEnabled(false);
        jButton4.setEnabled(false);
        jButton5.setEnabled(false);
        jButton6.setEnabled(false);
        jButton7.setEnabled(false);
        jButton8.setEnabled(false);
        jButton9.setText("Cancelar");
    }
    
    //Novo Para gravar no banco de dados firebird
    public void gravarConversao() {
        if(tSistema.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um sistema", "Aviso", JOptionPane.WARNING_MESSAGE);
        } else if((tSistema.getSelectedIndex() == 2 || tSistema.getSelectedIndex() == 4 || tSistema.getSelectedIndex() == 6 || tSistema.getSelectedIndex() == 7) && !Config.developer) {
            // Para sistemas nao liberados!
            JOptionPane.showMessageDialog(this, "Sistema selecionado esta em fase de desenvolvimento!", "Aviso", JOptionPane.WARNING_MESSAGE);
        } else {
            tc = new TelaCarregamento();
            //Registrar Tabela Conversão
            Conversao c = new Conversao();
            c.setNumero(tNConversao.getText());
            c.setSistema(tSistema.getSelectedIndex());
            c.setHost(tHost.getText());
            c.setPorta(tPorta.getText());
            c.setNome(tNomeBanco.getText());
            c.setUsuario(tUsuario.getText());
            c.setSenha(tSenha.getText());
            idConversao = cDAO.inserir(c);
            //Atualizar ID da conversao
            c.setId(idConversao);
            Config.conversao = c;
            
            
            //Usando Thread para Janela realizar o procedimento de criar arquivo de origem
            tc.setVisible(true);
            new Thread(inicial).start();
        }
    }
    
    Runnable inicial = new Runnable() {
        @Override
        public void run() {
            try {
                File diretorio = new File(property + "\\Conversoes\\" + tNConversao.getText());
                diretorio.mkdirs();
                arquivoOrigem = diretorio + "\\origem.txt";
            
                //Criar arquivo de Origem
                if(tSistema.getSelectedIndex() != 6) {
                    s.criarArquivoOrigem(tCaminho.getText(), arquivoOrigem);
                }
                
                TelaCarregamento.porcentagem = 0;
                
                Config.sistema = tSistema.getSelectedIndex();
                
                ArrayList<String> listaCnpj = new ArrayList<>();
                
                switch(tSistema.getSelectedIndex()) {
                    case 1:
                        Nasajon nj = new Nasajon();
                        listaCnpj = nj.buscaEmpresas(arquivoOrigem);
                        break;
                    case 2:
                        MasterMaq ng = new MasterMaq();
                        listaCnpj = ng.buscaEmpresas(arquivoOrigem);
                        break;
                    case 3:
                        SCI sci = new SCI();
                        listaCnpj = sci.buscaEmpresas(arquivoOrigem);
                        break;
                    case 4:
                        AlterData ad = new AlterData();
                        listaCnpj = ad.buscaEmpresas(arquivoOrigem);
                        break;
                    case 5:
                        Ledware lw = new Ledware();
                        listaCnpj = lw.buscaEmpresas(arquivoOrigem);
                        break;
                    case 6:
                        eSocial es = new eSocial();
                        listaCnpj = es.buscaEmpresas(tCaminho.getText()); //Ler os xml do caminho
                        break;
                    case 7:
                        Fortes f = new Fortes();
                        listaCnpj = f.buscarEmpresas(arquivoOrigem);
                        break;
                    case 8:
                        Tron t = new Tron();
                        listaCnpj = t.buscarEmpresas(arquivoOrigem);
                        break;
                }
                
                //Registrar tabela de Rubricas
                ArrayList<Rubrica> rub = new ArrayList<>();
                switch(tSistema.getSelectedIndex()) {
                    case 1:
                        Nasajon n = new Nasajon();
                        rub = n.retornarRubricasExcel(s.filtrarArquivos(tCaminho.getText(), ".xls"), tCaminho.getText());
                        break;
                    case 2:
                        //Ainda a desenvolver
                        break;
                    case 3:
                        SCI sci = new SCI();
                        rub = sci.retornarRubricasExcel(s.filtrarArquivos(tCaminho.getText(), ".xls"), tCaminho.getText());
                        break;
                    case 4:
                        //Ainda a desenvolver
                        break;
                    case 5:
                        Ledware lw = new Ledware();
                        rub = lw.retornarRubricasXLSX(s.filtrarArquivos(tCaminho.getText(), ".xlsx"), tCaminho.getText());
                        break;
                    case 6:
                        eSocial es = new eSocial();
                        rub = es.retornarRubricas(tCaminho.getText());
                        break;
                    case 7:
                        Fortes f = new Fortes();
                        rub = f.retornarRubricasCSV(s.filtrarArquivos(tCaminho.getText(), ".csv"), tCaminho.getText());
                        break;
                    case 8:
                        Tron t = new Tron();
                        rub = t.retornarRubricasXLS(s.filtrarArquivos(tCaminho.getText(), ".xls"), tCaminho.getText());
                        break;
                }
                
                //Fechar tela de carregamento
                tc.dispose();
                
                //Abrir janela de Aguardo
                iap = new InternaAvisoDeProcesso();
                pj.centralizarJanela(iap, jPainelPrincipal);
                
                RubricaDAO rDAO = new RubricaDAO();
                rDAO.inserir(rub, idConversao);
                
                //Iniciar conexão banco Dominio
                Sybase sb = new Sybase();
                Sybase.host = tHost.getText();
                Sybase.porta = tPorta.getText();
                Sybase.nome = tNomeBanco.getText();
                Sybase.usuario = tUsuario.getText();
                Sybase.senha = tSenha.getText();
                sb.connect();
                
                //Buscar no banco Dominio e Registrar tabela de Empresa
                EmpresaDAO eDAO = new EmpresaDAO();
                eDAO.inserir(eDAO.buscarEmpresasDominio(listaCnpj), idConversao);
                
                //CarregarEmpresas
                ArrayList<Empresa> empList = eDAO.carregarPorConversao(idConversao);
                
                //Consultar no banco Dominio e Registrar tabela de Empregado
                EmpregadoDAO eeDAO = new EmpregadoDAO();
                eeDAO.inserir(eeDAO.empregadosDominio(empList), idConversao);
                
                //Finalizar conexão com banco Dominio
                sb.disconnect();
                
                //Registrar tabela de Lancamento
                LancamentoDAO lDAO = new LancamentoDAO();
                
                switch(Config.sistema) {
                    case 1:
                        Nasajon nj = new Nasajon();
                        lDAO.inserir(nj.retornarLancamentos(empList, arquivoOrigem), idConversao);
                        break;
                    case 2:
                        MasterMaq ng = new MasterMaq();
                        lDAO.inserir(ng.retornarLancamentos(empList, arquivoOrigem), idConversao);
                        break;
                    case 3:
                        SCI sci = new SCI();
                        lDAO.inserir(sci.retornarLancamentos(empList, arquivoOrigem), idConversao);
                        break;
                    case 4:
                        AlterData ad = new AlterData();
                        lDAO.inserir(ad.retornarLancamentos(empList, arquivoOrigem), idConversao);
                        break;
                    case 5:
                        Ledware lw = new Ledware();
                        lDAO.inserir(lw.retornarLancamentos(empList, arquivoOrigem, idConversao), idConversao);
                        break;
                    case 6:
                        eSocial es = new eSocial();
                        lDAO.inserir(es.retornarLancamentos(idConversao, tCaminho.getText()), idConversao); //Caminho para ler os xmls
                        break;
                    case 7:
                        Fortes f = new Fortes();
                        lDAO.inserir(f.retornarLancamentos(empList, arquivoOrigem), idConversao);
                        break;
                    case 8:
                        Tron t = new Tron();
                        lDAO.inserir(t.retornarLancamentos(empList, eeDAO.carregarPorConversao(idConversao), arquivoOrigem), idConversao);
                        break;
                }
                
                //Posição antida - Registrar tabela de Empregado
                
                //Registrar tabela de configuracao de Rubricas
                RubricaConfiguracaoDAO rcDAO = new RubricaConfiguracaoDAO();
                
                switch(Config.sistema) {
                    case 1:
                        Nasajon nj = new Nasajon();
                        rcDAO.inserir(nj.retornaConfiguracaoRubrica(idConversao), idConversao);
                        break;
                    case 2:
                        MasterMaq ng = new MasterMaq();
                        //Ah desenvolver
                        break;
                    case 3:
                        SCI sci = new SCI();
                        rcDAO.inserir(sci.retornaConfiguracaoRubrica(idConversao), idConversao);
                        break;
                    case 4:
                        AlterData ad = new AlterData();
                        //Ah desenvolver
                        break;
                    case 5:
                        Ledware ld = new Ledware();
                        rcDAO.inserir(ld.retornaConfiguracaoRubrica(idConversao), idConversao);
                        break;
                    case 6:
                        eSocial es = new eSocial();
                        rcDAO.inserir(es.retornaConfiguracaoRubrica(idConversao), idConversao); //Ainda verificando
                        break;
                    case 7:
                        Fortes f = new Fortes();
                        rcDAO.inserir(f.retornaConfiguracaoRubrica(idConversao), idConversao); //Fase de teste
                        break;
                    case 8:
                        Tron t = new Tron();
                        rcDAO.inserir(t.retornaConfiguracaoRubrica(idConversao), idConversao);
                        break;
                }
                
                if(!Config.developer) {
                    //Apagar pasta Conversão
                    s.excluirPasta(new File(property+"\\Conversoes"));
                }
                
            } catch (IOException ex) {
                new TelaErro(3, ex.getStackTrace()).setVisible(true);
            } catch (InterruptedException ex) {
                new TelaErro(4, ex.getStackTrace()).setVisible(true);
            } catch (BiffException ex) {
                new TelaErro(5, ex.getStackTrace()).setVisible(true);
            } catch (Exception ex) {
                new TelaErro(1, ex.getStackTrace()).setVisible(true);
            }
            
            iap.dispose();
            
            //Antigo fechar tela de carregamento
            carregarUltimaConversao();
        }
    };
    
    Runnable relacaoDeEmpresa = new Runnable() {
        @Override
        public void run() {
            try {
                String caminho = System.getProperty("user.home")+"\\Documents\\Domino\\Conversoes\\"+Config.conversao.getNumero();
                String arquivo = "relacao_empresas.html";
                
                Sistema.criarArqRelatorio(caminho, arquivo, new Simples(Config.conversao.getId()));
                
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
    
    
    Runnable criarArquivoConversao = new Runnable() {
        @Override
        public void run() {
            Lancamento l = new Lancamento();
            LancamentoDAO lDAO = new LancamentoDAO();
            LayoutDominioDAO ldDAO = new LayoutDominioDAO();
            
            String arquivo = System.getProperty("user.home")+"\\Documents\\Domino\\Conversoes\\"+Config.conversao.getNumero()+"\\Conversao.txt";
            
            File pasta = new File(System.getProperty("user.home")+"\\Documents\\Domino\\Conversoes\\"+Config.conversao.getNumero());
            
            if(!pasta.exists()) {
                pasta.mkdirs();
            }
            
            //Sistema eSocial
            if(Config.sistema == 6) {
                //Antigo
                //s.gravarArquivo(ldDAO.retornaLancamentosPreparados(l.preparaLancamentoESocial(cDAO.carregarPorNumero(Config.conversao.getNumero()).getId())), arquivo);
                s.gravarArquivo(ldDAO.retornaLancamentosPreparados(l.preparaLancamentoESocial(Config.conversao.getId())), arquivo);
            } else {
                boolean realizarDeParaEmpregado = JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, "Deseja aplicar o DePara Empregados?");
            
                //Realizar preparação e DePara
                s.gravarArquivo(ldDAO.retornaLancamentosPreparados(lDAO.dePara(realizarDeParaEmpregado)), arquivo);
            }
            
            iap.dispose();
            JOptionPane.showMessageDialog(InternaConversao.this, "Converção realizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    };
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tNConversao = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tSistema = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        tCaminho = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        tHost = new javax.swing.JTextField();
        tPorta = new javax.swing.JTextField();
        tNomeBanco = new javax.swing.JTextField();
        tUsuario = new javax.swing.JTextField();
        tSenha = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle("Conversão");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados"));

        jLabel1.setText("Numero:");

        tNConversao.setEnabled(false);

        jLabel2.setText("Sistema:");

        tSistema.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecione", "Nasajon", "MasterMaq", "SCI", "AlterData", "Ledware", "eSocial - XML", "Fortes", "Tron" }));
        tSistema.setEnabled(false);
        tSistema.setFocusable(false);

        jLabel3.setText("Caminho:");

        tCaminho.setText("C:\\");
            tCaminho.setEnabled(false);

            jButton1.setText("...");
            jButton1.setEnabled(false);
            jButton1.setFocusable(false);
            jButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });

            jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Conexão Dominio"));

            tHost.setText(this.host);
            tHost.setEnabled(false);

            tPorta.setText(this.porta);
            tPorta.setEnabled(false);

            tNomeBanco.setText(this.nomeBanco);
            tNomeBanco.setEnabled(false);

            tUsuario.setText(this.usuario);
            tUsuario.setEnabled(false);

            tSenha.setText(this.senha);
            tSenha.setEnabled(false);

            jLabel4.setText("Host:");

            jLabel5.setText("Porta:");

            jLabel6.setText("Nome:");

            jLabel7.setText("Usuário:");

            jLabel8.setText("Senha:");

            jButton3.setText("Testar Conexão");
            jButton3.setFocusable(false);
            jButton3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jButton3)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4)
                                .addComponent(jLabel5)
                                .addComponent(jLabel8)
                                .addComponent(jLabel7)
                                .addComponent(jLabel6))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(tPorta, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tNomeBanco)
                                .addComponent(tUsuario, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tSenha, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tHost, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE))))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tPorta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(tNomeBanco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3)
                    .addContainerGap())
            );

            jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Relatórios"));

            jButton2.setText("Relação de Empresas");
            jButton2.setEnabled(false);
            jButton2.setFocusable(false);
            jButton2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });

            jButton4.setText("Diagnóstico Completo");
            jButton4.setEnabled(false);
            jButton4.setFocusable(false);
            jButton4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton4)
                        .addComponent(jButton2))
                    .addContainerGap(58, Short.MAX_VALUE))
            );
            jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(jButton2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton4)
                    .addContainerGap(10, Short.MAX_VALUE))
            );

            jButton12.setText("<");
            jButton12.setFocusable(false);
            jButton12.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton12ActionPerformed(evt);
                }
            });

            jButton13.setText(">");
            jButton13.setFocusable(false);
            jButton13.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton13ActionPerformed(evt);
                }
            });

            jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Processo"));

            jButton5.setText("Zeramento de Horas Normais...");
            jButton5.setFocusable(false);
            jButton5.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton5ActionPerformed(evt);
                }
            });

            jButton7.setText("Iniciar conversão");
            jButton7.setFocusable(false);
            jButton7.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton7ActionPerformed(evt);
                }
            });

            jButton6.setText("Exportar Rubricas");
            jButton6.setFocusable(false);
            jButton6.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton6ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
            jPanel4.setLayout(jPanel4Layout);
            jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton7)
                        .addComponent(jButton5)
                        .addComponent(jButton6))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addComponent(jButton6)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton5)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton7)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(tCaminho, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jButton1))
                                .addComponent(tSistema, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(tNConversao, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jButton12)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jButton13)))))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tNConversao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(jButton12)
                        .addComponent(jButton13))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tCaminho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(jButton1))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            jButton8.setText("Novo");
            jButton8.setFocusable(false);
            jButton8.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton8ActionPerformed(evt);
                }
            });

            jButton9.setText("Editar");
            jButton9.setFocusable(false);
            jButton9.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton9ActionPerformed(evt);
                }
            });

            jButton10.setText("Gravar");
            jButton10.setToolTipText("");
            jButton10.setEnabled(false);
            jButton10.setFocusable(false);
            jButton10.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton10ActionPerformed(evt);
                }
            });

            jButton11.setText("Excluir");
            jButton11.setFocusable(false);
            jButton11.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton11ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(10, 10, 10)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jButton8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton9)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton10)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton11)))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            pack();
        }// </editor-fold>//GEN-END:initComponents

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        if("Novo".equals(jButton8.getText())) {
            novaConversao();
        } else if ("Cancelar".equals(jButton8.getText())) {
            if(Config.conversao.getId() != 0) {
                conversaoAtual(Config.conversao.getId());
            } else {
                dispose();
            }
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        if("Editar".equals(jButton9.getText())) {
            editarConversao();
            tNConversao.setFocusable(true);
        } else if ("Cancelar".equals(jButton9.getText())) {
            conversaoAtual(Config.conversao.getId());
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Selecionar pasta");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retorno = fc.showOpenDialog(this);
        if(retorno == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            tCaminho.setText(f.getPath());
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            Sybase sb = new Sybase();
            Sybase.host = tHost.getText();
            Sybase.porta = tPorta.getText();
            Sybase.nome = tNomeBanco.getText();
            Sybase.usuario = tUsuario.getText();
            Sybase.senha = tSenha.getText();
            sb.connect();
            JOptionPane.showMessageDialog(this, "Conexão realizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            sb.disconnect();
            verficarConexao = true;
        } catch (SQLException ee) {
            JOptionPane.showMessageDialog(this, "Conexão invalida!", "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            new TelaErro(1, ex.getStackTrace()).setVisible(true);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        if(verficarConexao || Config.developer) {
            gravarConversao();
            TelaPrincipal.bConversoes.setText("Conversão: "+Config.conversao.getNumero());
        } else {
            JOptionPane.showMessageDialog(InternaConversao.this, "Conexao não verificada ou inválida!", "Atenção!", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        iap = new InternaAvisoDeProcesso();
        pj.centralizarJanela(iap, jPainelPrincipal);
        new Thread(relacaoDeEmpresa).start();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        iap = new InternaAvisoDeProcesso();
        pj.centralizarJanela(iap, jPainelPrincipal);
        new Thread(relatorioCompleto).start();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        InternaZeramento iz = new InternaZeramento();
        pj.centralizarJanela(iz, TelaPrincipal.jPainelPrincipal);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        iap = new InternaAvisoDeProcesso();
        pj.centralizarJanela(iap, jPainelPrincipal);
        new Thread(criarArquivoConversao).start();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(InternaConversao.this, "Tem certeza que deseja excluir esta conversão?")) {
            new RubricaNaoLocalizadaDAO().deletar(Config.conversao.getId());
            new RubricaConfiguracaoDAO().deletar(Config.conversao.getId());
            new RubricaDAO().deletar(Config.conversao.getId());
            new LancamentoDAO().deletar(Config.conversao.getId());
            new EmpregadoDAO().deletar(Config.conversao.getId());
            new EmpresaDAO().deletar(Config.conversao.getId());
            new ConversaoDAO().deletar(Config.conversao.getId());
            
            JOptionPane.showMessageDialog(InternaConversao.this, "Conversao excluida com sucesso!","Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            //Atualizar para Ultima Conversao apos a Exclusão
            Config.conversao = cDAO.carregarPorID(cDAO.retornaUltimoID());
            
            if(Config.conversao.getNumero() == null) {
                TelaPrincipal.bConversoes.setText("Conversão: ");
            } else {
                TelaPrincipal.bConversoes.setText("Conversão: " + Config.conversao.getNumero());
            }
            
            this.dispose();
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        RubricaDAO rDAO = new RubricaDAO();
        RubricaConfiguracaoDAO rcDAO = new RubricaConfiguracaoDAO();
        
        File FOEVENTOS = new File(System.getProperty("user.home")+"\\Documents\\Domino\\Conversoes\\"+Config.conversao.getNumero());
        
        if(FOEVENTOS.exists()) {
            FOEVENTOS.mkdirs();
        }
        
        String arquivo = System.getProperty("user.home")+"\\Documents\\Domino\\Conversoes\\"+Config.conversao.getNumero()+"\\FOEVENTOS.txt";
        s.gravarArquivo(rDAO.gerarRubrica(rcDAO.carregarPorConversao(Config.conversao.getId())), arquivo);
        JOptionPane.showMessageDialog(this, "Rubricas Exportadas com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton6ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField tCaminho;
    private javax.swing.JTextField tHost;
    private javax.swing.JTextField tNConversao;
    private javax.swing.JTextField tNomeBanco;
    private javax.swing.JTextField tPorta;
    private javax.swing.JTextField tSenha;
    private javax.swing.JComboBox<String> tSistema;
    private javax.swing.JTextField tUsuario;
    // End of variables declaration//GEN-END:variables
}
