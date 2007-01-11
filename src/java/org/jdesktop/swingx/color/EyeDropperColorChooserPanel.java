/*
 * EyeDropperColorChooserPanel.java
 *
 * Created on February 28, 2006, 11:52 AM
 */

package org.jdesktop.swingx.color;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.MouseInputAdapter;
import org.jdesktop.swingx.JXColorSelectionButton;

/**
 * <p>EyeDropperColorChooserPanel is a pluggable panel for the 
 * {@link JColorChooser} which allows the user to grab any 
 * color from the screen using a magnifying glass.</p>
 *
 * <p>Example usage:</p>
 * <pre><code>
 *    public static void main(String ... args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JColorChooser chooser = new JColorChooser();
                chooser.addChooserPanel(new EyeDropperColorChooserPanel());
                JFrame frame = new JFrame();
                frame.add(chooser);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
 * </code></pre>
 *
 * @author joshua@marinacci.org
 */
public class EyeDropperColorChooserPanel extends AbstractColorChooserPanel {
    private Color oldColor;
    
    /**
     * Example usage
     */
    public static void main(String ... args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JColorChooser chooser = new JColorChooser();
                chooser.addChooserPanel(new EyeDropperColorChooserPanel());
                JFrame frame = new JFrame();
                frame.add(chooser);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
    
    /**
     * Creates new EyeDropperColorChooserPanel
     */
    public EyeDropperColorChooserPanel() {
        initComponents();
        MouseInputAdapter mia = new MouseInputAdapter() {
            public void mousePressed(MouseEvent evt) {
            }
            public void mouseDragged(MouseEvent evt) {
                Point pt = evt.getPoint();
                SwingUtilities.convertPointToScreen(pt,evt.getComponent());
                ((MagnifyingPanel)magPanel).setMagPoint(pt);
            }
            public void mouseReleased(MouseEvent evt) {
                Color newColor = new Color(((MagnifyingPanel)magPanel).activeColor);
                oldColor = newColor;
                getColorSelectionModel().setSelectedColor(oldColor);
            }
        };
        eyeDropper.addMouseListener(mia);
        eyeDropper.addMouseMotionListener(mia);
        try {
            eyeDropper.setIcon(new ImageIcon(getClass().getResource("mag.png")));
            eyeDropper.setText("");
        } catch (Exception ex) {
            
        }
        
        magPanel.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Color color = new Color(((MagnifyingPanel)magPanel).activeColor);
                activeColor.setBackground(color);
                hexColor.setText(ColorUtil.toHexString(color).substring(1));
                rgbColor.setText(color.getRed() +"," + color.getGreen() + "," + color.getBlue());
            }
        });
    }
    
    private class MagnifyingPanel extends JPanel {
        private Point2D point;
        private int activeColor;
        public void setMagPoint(Point2D point) {
            this.point = point;
            repaint();
        }
        public void paintComponent(Graphics g) {
            if(point != null) {
                Rectangle rect = new Rectangle((int)point.getX()-10,(int)point.getY()-10,20,20);
                try {
                    BufferedImage img =new Robot().createScreenCapture(rect);
                    g.drawImage(img,0,0,getWidth(),getHeight(),null);
                    int oldColor = activeColor;
                    activeColor = img.getRGB(img.getWidth()/2,img.getHeight()/2);
                    firePropertyChange("activeColor", oldColor, activeColor);
                } catch (AWTException ex) {
                    ex.printStackTrace();
                }
            }
            g.setColor(Color.black);
            g.drawRect(getWidth()/2 - 5, getHeight()/2 -5, 10,10);
        }
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        eyeDropper = new javax.swing.JButton();
        magPanel = new MagnifyingPanel();
        activeColor = new JXColorSelectionButton();
        hexColor = new javax.swing.JTextField();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        rgbColor = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        eyeDropper.setText("eye");

        magPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        org.jdesktop.layout.GroupLayout magPanelLayout = new org.jdesktop.layout.GroupLayout(magPanel);
        magPanel.setLayout(magPanelLayout);
        magPanelLayout.setHorizontalGroup(
            magPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );
        magPanelLayout.setVerticalGroup(
            magPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        activeColor.setEnabled(false);
        activeColor.setPreferredSize(new java.awt.Dimension(40, 40));

        hexColor.setEditable(false);

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Drag the magnifying glass to select a color from the screen.");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setOpaque(false);

        jLabel1.setText("#");

        rgbColor.setEditable(false);
        rgbColor.setText("255,255,255");

        jLabel2.setText("RGB");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(magPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, hexColor, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, rgbColor, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                            .add(activeColor, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(eyeDropper)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextArea1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTextArea1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 52, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(eyeDropper))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel2)
                                    .add(rgbColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(hexColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 14, Short.MAX_VALUE)
                        .add(activeColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(magPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton activeColor;
    private javax.swing.JButton eyeDropper;
    private javax.swing.JTextField hexColor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel magPanel;
    private javax.swing.JTextField rgbColor;
    // End of variables declaration//GEN-END:variables
    
    /**
     * {@inheritDoc}
     */
    public void updateChooser() {
    }
    
    /**
     * {@inheritDoc}
     */
    protected void buildChooser() {
    }
    
    /**
     * {@inheritDoc}
     */
    public String getDisplayName() {
        return "Grab from Screen";
    }
    
    /**
     * {@inheritDoc}
     */
    public Icon getSmallDisplayIcon() {
        return new ImageIcon();
    }
    
    /**
     * {@inheritDoc}
     */
    public Icon getLargeDisplayIcon() {
        return new ImageIcon();
    }
}