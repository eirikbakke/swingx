/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jdesktop.swingx;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;

import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.Scrollable;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.JXComponent;
import org.jdesktop.swingx.painter.PainterSupport;

/**
 * A simple JPanel extension that adds translucency support.
 * This component and all of its content will be displayed with the specified
 * &quot;alpha&quot; transluscency property value. It also supports the
 * Painter API.
 *
 * @author rbair
 */
public class JXPanel extends JPanel implements Scrollable, JXComponent {
    private boolean scrollableTracksViewportHeight;
    private boolean scrollableTracksViewportWidth;
    protected PainterSupport painterSupport;
    
    /**
     * The alpha level for this component.
     */
    private float alpha = 1.0f;
    /**
     * If the old alpha value was 1.0, I keep track of the opaque setting because
     * a translucent component is not opaque, but I want to be able to restore
     * opacity to its default setting if the alpha is 1.0. Honestly, I don't know
     * if this is necessary or not, but it sounded good on paper :)
     * <p>TODO: Check whether this variable is necessary or not</p>
     */
    private boolean oldOpaque;
    /**
     * Indicates whether this component should inherit its parent alpha value
     */
    private boolean inheritAlpha = true;
    /**
     * Specifies the Painter to use for painting the background of this panel.
     * If no painter is specified, the normal painting routine for JPanel
     * is called. Old behavior is also honored for the time being if no
     * backgroundPainter is specified
     */
    private Painter backgroundPainter;
    private Painter foregroundPainter;
    /**
     * Keeps track of the old dimensions so that if the dimensions change, the
     * saved gradient image can be thrown out and re-rendered. This size is
     * AFTER applying the insets!
     */
    private Dimension oldSize;
    
    
    /**
     * Creates a new instance of JXPanel
     */
    public JXPanel() {
        initPainterSupport();
    }
    
    /**
     * @param isDoubleBuffered
     */
    public JXPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        initPainterSupport();
    }
    
    /**
     * @param layout
     */
    public JXPanel(LayoutManager layout) {
        super(layout);
        initPainterSupport();
    }
    
    /**
     * @param layout
     * @param isDoubleBuffered
     */
    public JXPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        initPainterSupport();
    }
    
    private void initPainterSupport() {
        painterSupport = new PainterSupport();
        painterSupport.setPainter(JXComponent.COMPONENT_LAYER,
                new AbstractPainter<JXPanel>() {
            protected void paintBackground(Graphics2D g, JXPanel component, int width, int height) {
                JXPanel.super.paintComponent(g);
            }
        });
    }
    
    /**
     * Set the alpha transparency level for this component. This automatically
     * causes a repaint of the component.
     *
     * <p>TODO add support for animated changes in translucency</p>
     *
     * @param alpha must be a value between 0 and 1 inclusive.
     */
    public void setAlpha(float alpha) {
        if (this.alpha != alpha) {
            assert alpha >= 0 && alpha <= 1.0;
            float oldAlpha = this.alpha;
            this.alpha = alpha;
            if (alpha > 0f && alpha < 1f) {
                if (oldAlpha == 1) {
                    //it used to be 1, but now is not. Save the oldOpaque
                    oldOpaque = isOpaque();
                    setOpaque(false);
                }
                RepaintManager manager = RepaintManager.currentManager(this);
                if (!manager.getClass().isAnnotationPresent(TranslucentRepaintManager.class)) {
                    RepaintManager.setCurrentManager(new RepaintManagerX());
                }
            } else if (alpha == 1) {
                //restore the oldOpaque if it was true (since opaque is false now)
                if (oldOpaque) {
                    setOpaque(true);
                }
            }
            firePropertyChange("alpha", oldAlpha, alpha);
            repaint();
        }
    }
    
    /**
     * @return the alpha translucency level for this component. This will be
     * a value between 0 and 1, inclusive.
     */
    public float getAlpha() {
        return alpha;
    }
    
    /**
     * Unlike other properties, alpha can be set on a component, or on one of
     * its parents. If the alpha of a parent component is .4, and the alpha on
     * this component is .5, effectively the alpha for this component is .4
     * because the lowest alpha in the heirarchy &quot;wins&quot;
     */
    public float getEffectiveAlpha() {
        if (inheritAlpha) {
            float a = alpha;
            Component c = this;
            while ((c = c.getParent()) != null) {
                if (c instanceof JXPanel) {
                    a = Math.min(((JXPanel)c).getAlpha(), a);
                }
            }
            return a;
        } else {
            return alpha;
        }
    }
    
    public boolean isInheritAlpha() {
        return inheritAlpha;
    }
    
    public void setInheritAlpha(boolean val) {
        if (inheritAlpha != val) {
            inheritAlpha = val;
            firePropertyChange("inheritAlpha", !inheritAlpha, inheritAlpha);
        }
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
     */
    public boolean getScrollableTracksViewportHeight() {
        return scrollableTracksViewportHeight;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
     */
    public boolean getScrollableTracksViewportWidth() {
        return scrollableTracksViewportWidth;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
     */
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }
    /**
     * @param scrollableTracksViewportHeight The scrollableTracksViewportHeight to set.
     */
    public void setScrollableTracksViewportHeight(boolean scrollableTracksViewportHeight) {
        this.scrollableTracksViewportHeight = scrollableTracksViewportHeight;
    }
    /**
     * @param scrollableTracksViewportWidth The scrollableTracksViewportWidth to set.
     */
    public void setScrollableTracksViewportWidth(boolean scrollableTracksViewportWidth) {
        this.scrollableTracksViewportWidth = scrollableTracksViewportWidth;
    }
    
    
    /**
     * Specifies a Painter to use to paint the background of this JXPanel.
     * If <code>p</code> is not null, then setOpaque(false) will be called
     * as a side effect. A component should not be opaque if painters are
     * being used, because Painters may paint transparent pixels or not
     * paint certain pixels, such as around the border insets.
     */
    public void setBackgroundPainter(Painter p) {
        Painter old = getBackgroundPainter();
        
        painterSupport.setBackgroundPainter(p);
        
        if (p != null) {
            setOpaque(false);
        }
        
        firePropertyChange("backgroundPainter", old, getBackgroundPainter());
        repaint();
    }
    
    public Painter getBackgroundPainter() {
        return painterSupport.getBackgroundPainter();
    }
    
    /**
     * Specifies a Painter to use to paint the background of this JXPanel.
     * If <code>p</code> is not null, then setOpaque(false) will be called
     * as a side effect. A component should not be opaque if painters are
     * being used, because Painters may paint transparent pixels or not
     * paint certain pixels, such as around the border insets.
     */
    public void setForegroundPainter(Painter p) {
        Painter old = getForegroundPainter();
        //this.foregroundPainter = p;
        painterSupport.setForegroundPainter(p);
        //painterSupport.setPainter(p, FOREGROUND_LAYER);
        
        if (p != null) {
            setOpaque(false);
        }
        
        firePropertyChange("foregroundPainter", old, getForegroundPainter());
        repaint();
    }
    
    public Painter getForegroundPainter() {
        return painterSupport.getForegroundPainter();
    }
    
    /**
     * Overriden paint method to take into account the alpha setting
     */
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        Composite oldComp = g2d.getComposite();
        float alpha = getEffectiveAlpha();
        Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alphaComp);
        super.paint(g2d);
        g2d.setComposite(oldComp);
        g2d.dispose();
    }
    
    /**
     * overridden to provide gradient painting
     *
     * TODO: Chris says that in OGL we actually suffer here by caching the
     * gradient paint since the OGL pipeline will render gradients on
     * hardware for us. The decision to use cacheing is based on my experience
     * with gradient title borders slowing down repaints -- this could use more
     * extensive analysis.
     */
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g.create();
        //Insets ins = this.getInsets();
        //g2.translate(ins.left, ins.top);
        //painterSupport.paint(g2, this,
        //        this.getWidth()  - ins.left - ins.right,
        //        this.getHeight() - ins.top  - ins.bottom);
        painterSupport.paint(g2, this, this.getWidth(), this.getHeight());
        g2.dispose();
    }


    public void setPainters(Map<Integer, List<Painter>> painters) {
        painterSupport.setPainters(painters);
    }

    public Map<Integer, List<Painter>> getPainters() {
        return painterSupport.getPainters();
    }

    public void setPainter(Integer layer, Painter painter) {
        painterSupport.setPainter(layer,painter);
    }

    public Painter getPainter(Integer layer) {
        return painterSupport.getPainter(layer);
    }
}
