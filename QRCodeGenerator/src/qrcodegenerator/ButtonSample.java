
package qrcodegenerator;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;


class RoundBorder extends AbstractBorder {
    private int rad;
    public RoundBorder(int rad){
        this.rad = rad;
    }
    
    @Override
    public void paintBorder(Component c,Graphics g,int x,int y,int wid,int hei){
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setColor(Color.GRAY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.draw(new RoundRectangle2D.Double(x,y,wid-1,hei-1,rad,rad));
        g2.dispose();
    }
    
    @Override
    public Insets getBorderInsets(Component c){
        return new Insets(8,8,8,8);
    }
    
    @Override
    public Insets getBorderInsets(Component c,Insets insets){
        insets.set(8,8,8,8);
        return insets;
    }
    
}

public class ButtonSample{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            JFrame f = new JFrame("Round UI Example");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(400,300);
            f.setLocationRelativeTo(null);
            
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
            panel.setBackground(new Color(240,240,240));
            panel.setBorder(new RoundBorder(30));
            
            JButton button = new JButton("Round Button");
            button.setBorder(new RoundBorder(30));
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setBackground(Color.ORANGE);
            button.setForeground(Color.WHITE);
            
            JTextField text = new JTextField(20);
            text.setBorder(new RoundBorder(20));
            text.setBackground(Color.WHITE);
            text.setOpaque(true);
            
            panel.add(text);
            panel.add(button);
            f.add(panel);
            f.setVisible(true);
        });
    }
}
