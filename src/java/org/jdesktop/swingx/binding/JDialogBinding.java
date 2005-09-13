/*
 * JLabelBinding.java
 *
 * Created on August 24, 2005, 5:14 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx.binding;
import javax.swing.JDialog;
import org.jdesktop.binding.ScalarBinding;



/**
 *
 * @author Richard
 */
public class JDialogBinding extends ScalarBinding {
    private String oldValue;
    
    public JDialogBinding(JDialog dlg) {
        super(dlg, String.class);
    }
    
    public JDialogBinding(JDialog dlg, String fieldName) {
        super(dlg, fieldName, String.class);
    }

    protected void initialize() {
        oldValue = getComponent().getTitle();
    }

    public void release() {
        getComponent().setTitle(oldValue);
    }

    protected void setComponentValue(Object value) {
        getComponent().setTitle(value == null ? "" : (String)value);
    }
    
    protected String getComponentValue() {
        //in reality, this should never be called since
        //label components are always read only!!!
        assert false : "getComponentValue was called although the JDialogBinding is read only";
        return null;
    }
    
    public JDialog getComponent() {
        return (JDialog)super.getComponent();
    }
}
