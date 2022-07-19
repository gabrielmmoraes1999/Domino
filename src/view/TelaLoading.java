package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JWindow;

/**
 *
 * @author Gabriel Moraes
 */
public class TelaLoading extends JWindow {
    
    private final String imagem;
    
    /*public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        TelaLoading tl = new TelaLoading("C:\\Domino\\Imagens\\Logo.png");
        //SwingUtilities.invokeLater(() -> new TelaLoading("C:\\Domino\\Imagens\\Logo.png"));
        tl.setVisible(true);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }
        tl.setVisible(false);
    }*/

    public TelaLoading(String imagem) {
        this.imagem = imagem;
        setBackground(new Color(0, 0, 0, 0));
        setContentPane(new Pane());
        pack();
        setVisible(false);
        setLocationRelativeTo(null);
    }
    
    class Pane extends JPanel {
        private BufferedImage leaf;

        public Pane() {
            JPanel borderPainel = new JPanel();
            borderPainel.setLayout(new BorderLayout());
            JPanel gridPainel = new JPanel();
            gridPainel.setLayout(new GridLayout(2, 1));
            
            borderPainel.add(gridPainel, BorderLayout.SOUTH);
            add(borderPainel);

            try {
                leaf = ImageIO.read(new File(imagem)); //Arquivo da Imagem
            } catch (IOException ex) {
                new TelaErro(3, ex.getStackTrace()).setVisible(true);
            }

            gridPainel.setOpaque(false);
            borderPainel.setOpaque(false);
            setOpaque(false);

        }

        @Override
        public Dimension getPreferredSize() {
            return leaf == null ? new Dimension(200, 200) : new Dimension(leaf.getWidth(), leaf.getHeight());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (leaf != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.drawImage(leaf, 0, 0, this);
                g2d.dispose();
            }
        }
    }

}
