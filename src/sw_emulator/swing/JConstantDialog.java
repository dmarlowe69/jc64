/**
 * @(#)JCostantDialog.java 2021/05/01
 *
 * ICE Team free software group
 *
 * This file is part of C64 Java Software Emulator.
 * See README for copyright notice.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */
package sw_emulator.swing;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import sw_emulator.software.MemoryDasm;
import sw_emulator.swing.main.Constant;
import sw_emulator.swing.main.FileManager;
import sw_emulator.swing.table.ConstantCellEditor;
import sw_emulator.swing.table.DataTableModelConstant;

/**
 * Dialog for constant
 * 
 * @author ice
 */
public class JConstantDialog extends javax.swing.JDialog {
    /** Constant of tables value */
    Constant constant=new Constant();
    
    /** Data model */
    DataTableModelConstant dataModel=new DataTableModelConstant(constant);
    
    /** Constant cell editor */
    ConstantCellEditor constantCellEditor=new ConstantCellEditor(new JTextField());
    
    /** Project chooser file dialog*/
    JFileChooser constantChooserFile=new JFileChooser();

    /**
     * Creates new form JConstantDialog
     */
    public JConstantDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        Shared.framesList.add(this);
        //Shared.framesList.add(constantCellEditor);
    }
    
    /**
     * Set up the dialog with the project to use 
     * 
     * @param constant constant 
     * @param memories the memory
     */
    public void setUp(Constant constant, MemoryDasm[] memories) {
      this.constant=constant;  
      dataModel=new DataTableModelConstant(constant);
      jTableConstant.setModel(dataModel);    
      constantCellEditor.setCostant(constant, memories);
    }  

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenuConstant = new javax.swing.JPopupMenu();
        jMenuItemLoad = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemGoto = new javax.swing.JMenuItem();
        jScrollPaneTable = new javax.swing.JScrollPane();
        jTableConstant = new javax.swing.JTable();
        jTableConstant.setDefaultEditor(String.class, constantCellEditor);
        jTableConstant.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        jPanelDn = new javax.swing.JPanel();
        jButtonClose = new javax.swing.JButton();

        jMenuItemLoad.setText("Load from file");
        jMenuItemLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLoadActionPerformed(evt);
            }
        });
        jPopupMenuConstant.add(jMenuItemLoad);

        jMenuItemSave.setText("Save to file");
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jPopupMenuConstant.add(jMenuItemSave);
        jPopupMenuConstant.add(jSeparator1);

        jMenuItemGoto.setText("Goto hex address ");
        jMenuItemGoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGotoActionPerformed(evt);
            }
        });
        jPopupMenuConstant.add(jMenuItemGoto);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Constants definitions");

        jTableConstant.setModel(dataModel);
        InputMap im = this.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = this.getRootPane().getActionMap();

        //add custom action
        im.put(KeyStroke.getKeyStroke("control F"), "find");
        am.put("find", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                find();
            }
        });

        ((InputMap)UIManager.get("Table.ancestorInputMap")).put(KeyStroke.getKeyStroke("control F"), "none");
        jTableConstant.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableConstantMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableConstantMouseReleased(evt);
            }
        });
        jScrollPaneTable.setViewportView(jTableConstant);

        getContentPane().add(jScrollPaneTable, java.awt.BorderLayout.CENTER);

        jButtonClose.setText("Close");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jPanelDn.add(jButtonClose);

        getContentPane().add(jPanelDn, java.awt.BorderLayout.PAGE_END);

        setBounds(0, 0, 763, 545);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
      setVisible(false);
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jMenuItemLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLoadActionPerformed
      load(); 
    }//GEN-LAST:event_jMenuItemLoadActionPerformed

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
      save();
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private void jMenuItemGotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGotoActionPerformed
      find();
    }//GEN-LAST:event_jMenuItemGotoActionPerformed

    private void jTableConstantMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableConstantMousePressed
      if (evt.isPopupTrigger()) jPopupMenuConstant.show(evt.getComponent(),evt.getX(), evt.getY());
    }//GEN-LAST:event_jTableConstantMousePressed

    private void jTableConstantMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableConstantMouseReleased
      if (evt.isPopupTrigger()) jPopupMenuConstant.show(evt.getComponent(),evt.getX(), evt.getY());
    }//GEN-LAST:event_jTableConstantMouseReleased

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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JConstantDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JConstantDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JConstantDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JConstantDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JConstantDialog dialog = new JConstantDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    /** Find the given address */
    private void find() {
      String addr=JOptionPane.showInputDialog(this, "Search and go to a given HEX memory address");
      if (addr==null) return;
    
      try {
        int pos=Integer.parseInt(addr,16);
        if (pos<0 || pos>0xFFFF) return;
    
        jTableConstant.getSelectionModel().setSelectionInterval(pos, pos);
        Shared.scrollToCenter(jTableConstant, pos, 0);
      } catch (Exception e) {
        }  
    }
    
    /**
     * Load the constant from file
     */
    private void load() {
      int col=jTableConstant.getSelectedColumn()-1;
      
      if (col<0) {
        JOptionPane.showMessageDialog(this, "Select a column to load the file in");
        return;
      }    
      
      if (constantChooserFile.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {

        if (!FileManager.instance.readConstantFile(constantChooserFile.getSelectedFile(), constant, col)) {
          JOptionPane.showMessageDialog(this, "Error loading the file", "Error", JOptionPane.ERROR_MESSAGE);
        } else dataModel.fireTableDataChanged();
      }  
    }
    
    /**
     * Save the constant from file
     */
    private void save() {
      int col=jTableConstant.getSelectedColumn()-1;
      
      if (col<0) {
        JOptionPane.showMessageDialog(this, "Select a column to save on file");
        return;
      }
      
      if (constantChooserFile.showSaveDialog(this)==JFileChooser.APPROVE_OPTION) {

        if (!FileManager.instance.writeConstantFile(constantChooserFile.getSelectedFile(), constant, col)) {
          JOptionPane.showMessageDialog(this, "Error saving the file", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }       
    }    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JMenuItem jMenuItemGoto;
    private javax.swing.JMenuItem jMenuItemLoad;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JPanel jPanelDn;
    private javax.swing.JPopupMenu jPopupMenuConstant;
    private javax.swing.JScrollPane jScrollPaneTable;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTable jTableConstant;
    // End of variables declaration//GEN-END:variables
}
