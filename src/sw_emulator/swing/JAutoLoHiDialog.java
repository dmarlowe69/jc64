/**
 * @(#)JAutoLoHiDialog.java 2021/08/06
 *
 * ICE Team free software group
 *
 * This file is part of JIIT64 Java Ice Team Tracker 64
 * See README for copyright notice.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 */
package sw_emulator.swing;

import java.text.ParseException;
import javax.swing.JFormattedTextField.AbstractFormatter; 
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.text.DefaultFormatter; 
import javax.swing.text.DefaultFormatterFactory; 
import sw_emulator.software.MemoryDasm;
import static sw_emulator.software.MemoryDasm.TYPE_MAJOR;
import static sw_emulator.software.MemoryDasm.TYPE_MINOR;
import static sw_emulator.software.MemoryDasm.TYPE_PLUS;
import static sw_emulator.software.MemoryDasm.TYPE_PLUS_MAJOR;
import static sw_emulator.software.MemoryDasm.TYPE_PLUS_MINOR;

/**
 * Dialog for low/high table
 * 
 * @author stefano_tognon
 */
public class JAutoLoHiDialog extends javax.swing.JDialog {   
  /** Memory dasm */
  private MemoryDasm[] memories;
  
  /** The table with selection */
  private JTable jTable;
    
  private static class HexFormatterFactory extends DefaultFormatterFactory { 
        @Override
        public AbstractFormatter getDefaultFormatter() { 
           return new HexFormatter(); 
       } 
  } 

  private static class HexFormatter extends DefaultFormatter { 
      @Override
      public Object stringToValue(String text) throws ParseException { 
         try { 
            return Long.valueOf(text, 16); 
         } catch (NumberFormatException nfe) { 
            throw new ParseException(text,0); 
         } 
     } 

      @Override
     public String valueToString(Object value) throws ParseException { 
        if (value instanceof Long)
             return Long.toHexString( 
              ((Long)value).intValue()).toUpperCase();  
        else return Integer.toHexString( 
              ((Integer)value).intValue()).toUpperCase(); 
     } 
 } 


  /**
   * Set memory dasm
   * 
   * @param memories the memories dasm to use
   * @param jTable the table with selection
   */
  public void setUp(MemoryDasm[] memories, JTable jTable) {
    this.memories=memories; 
    this.jTable=jTable;
  }
    /**
     * Creates new form JDialogAutoLoHi
     */
    public JAutoLoHiDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        Shared.framesList.add(this);
        
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)jSpinnerStartLow.getEditor(); 
        editor.getTextField().setFormatterFactory(new HexFormatterFactory());  
        
        editor = (JSpinner.DefaultEditor)jSpinnerStartHigh.getEditor(); 
        editor.getTextField().setFormatterFactory(new HexFormatterFactory());
        
        editor = (JSpinner.DefaultEditor)jSpinnerEndLow.getEditor(); 
        editor.getTextField().setFormatterFactory(new HexFormatterFactory());
        
        editor = (JSpinner.DefaultEditor)jSpinnerEndHigh.getEditor(); 
        editor.getTextField().setFormatterFactory(new HexFormatterFactory());
        
        editor = (JSpinner.DefaultEditor)jSpinnerBase.getEditor(); 
        editor.getTextField().setFormatterFactory(new HexFormatterFactory());
        
        editor = (JSpinner.DefaultEditor)jSpinnerDest.getEditor(); 
        editor.getTextField().setFormatterFactory(new HexFormatterFactory());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButtonCancel = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jSpinnerStartLow = new javax.swing.JSpinner();
        jLabelStartLo = new javax.swing.JLabel();
        jLabelStartHi = new javax.swing.JLabel();
        jSpinnerStartHigh = new javax.swing.JSpinner();
        jLabelEndLo = new javax.swing.JLabel();
        jSpinnerEndLow = new javax.swing.JSpinner();
        jLabelEndHigh = new javax.swing.JLabel();
        jSpinnerEndHigh = new javax.swing.JSpinner();
        jSeparator1 = new javax.swing.JSeparator();
        jButtonOk = new javax.swing.JButton();
        jLabelStartLo2 = new javax.swing.JLabel();
        jSpinnerStartLow2 = new javax.swing.JSpinner();
        jLabelStartHi2 = new javax.swing.JLabel();
        jSpinnerStartHigh2 = new javax.swing.JSpinner();
        jButtonOk2 = new javax.swing.JButton();
        jLabelSize2 = new javax.swing.JLabel();
        jSpinnerSize2 = new javax.swing.JSpinner();
        jLabelPrefix2 = new javax.swing.JLabel();
        jTextFieldPrefix2 = new javax.swing.JTextField();
        jLabelDigit2 = new javax.swing.JLabel();
        jSpinnerDigit2 = new javax.swing.JSpinner();
        jSpinnerStart2 = new javax.swing.JSpinner();
        jCheckBoxUpper2 = new javax.swing.JCheckBox();
        jLabelStart2 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabelRelated = new javax.swing.JLabel();
        jLabelBaseAddress = new javax.swing.JLabel();
        jSpinnerBase = new javax.swing.JSpinner();
        jLabelDestinationAddress = new javax.swing.JLabel();
        jSpinnerDest = new javax.swing.JSpinner();
        jButtonRelative = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tables automatic assigment");

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setText("Automatic assign low/high pointer from two tables");
        jPanel2.add(jLabel1);

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonCancel);

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_END);

        jSpinnerStartLow.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(0L), Long.valueOf(0L), Long.valueOf(65535L), Long.valueOf(1L)));

        jLabelStartLo.setText("Starting of low values:");

        jLabelStartHi.setText("Starting of high values:");

        jSpinnerStartHigh.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(0L), Long.valueOf(0L), Long.valueOf(65535L), Long.valueOf(1L)));

        jLabelEndLo.setText("Ending of low values:");

        jSpinnerEndLow.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(0L), Long.valueOf(0L), Long.valueOf(65535L), Long.valueOf(1L)));

        jLabelEndHigh.setText("Ending of high values:");

        jSpinnerEndHigh.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(0L), Long.valueOf(0L), Long.valueOf(65535L), Long.valueOf(1L)));

        jButtonOk.setText("OK");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });

        jLabelStartLo2.setText("Starting of low values:");

        jSpinnerStartLow2.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(0L), Long.valueOf(0L), Long.valueOf(65535L), Long.valueOf(1L)));

        jLabelStartHi2.setText("Starting of high values:");

        jSpinnerStartHigh2.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(0L), Long.valueOf(0L), Long.valueOf(65535L), Long.valueOf(1L)));

        jButtonOk2.setText("OK");
        jButtonOk2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOk2ActionPerformed(evt);
            }
        });

        jLabelSize2.setText("Table size:");

        jSpinnerSize2.setModel(new javax.swing.SpinnerNumberModel(2, 1, 256, 1));

        jLabelPrefix2.setText("Prefix:");

        jLabelDigit2.setText("Digit:");

        jSpinnerDigit2.setModel(new javax.swing.SpinnerNumberModel(1, 1, 2, 1));
        jSpinnerDigit2.setToolTipText("Min number of digits to use (can increase automatically)");

        jSpinnerStart2.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));

        jCheckBoxUpper2.setText("Uppercase");

        jLabelStart2.setText("Start:");

        jLabelRelated.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelRelated.setText("Assign <> related relative address");
        jLabelRelated.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabelBaseAddress.setText("Base (real) address:");

        jSpinnerBase.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(0L), Long.valueOf(0L), Long.valueOf(65535L), Long.valueOf(1L)));

        jLabelDestinationAddress.setText("Destination address:");

        jSpinnerDest.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(0L), Long.valueOf(0L), Long.valueOf(65535L), Long.valueOf(1L)));

        jButtonRelative.setText("OK");
        jButtonRelative.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRelativeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelRelated, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(226, 226, 226)
                                .addComponent(jButtonOk))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 478, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabelStartLo)
                                            .addComponent(jLabelEndLo, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jSpinnerEndLow, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                            .addComponent(jSpinnerStartLow, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(51, 51, 51)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabelEndHigh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabelStartHi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jSpinnerStartHigh, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jSpinnerEndHigh, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(212, 212, 212)
                                .addComponent(jButtonOk2)))
                        .addGap(0, 20, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabelPrefix2, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextFieldPrefix2))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabelStartLo2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabelSize2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jSpinnerStartLow2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jSpinnerSize2)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabelStart2, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSpinnerStart2)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabelStartHi2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(55, 55, 55)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jCheckBoxUpper2)
                                            .addComponent(jLabelDigit2))
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jSpinnerDigit2)
                                    .addComponent(jSpinnerStartHigh2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jSeparator2)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabelBaseAddress)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinnerBase, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabelDestinationAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinnerDest, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(212, 212, 212)
                .addComponent(jButtonRelative)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSpinnerStartLow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelStartLo)
                    .addComponent(jSpinnerStartHigh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelStartHi))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSpinnerEndLow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelEndLo)
                    .addComponent(jSpinnerEndHigh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelEndHigh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonOk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSpinnerStartLow2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelStartLo2)
                    .addComponent(jSpinnerStartHigh2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelStartHi2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSize2)
                    .addComponent(jSpinnerSize2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPrefix2)
                    .addComponent(jTextFieldPrefix2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelDigit2)
                    .addComponent(jSpinnerDigit2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxUpper2)
                    .addComponent(jSpinnerStart2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelStart2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonOk2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelRelated, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSpinnerBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelBaseAddress)
                    .addComponent(jSpinnerDest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelDestinationAddress))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRelative)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
      if (memories==null) return;
      
      long valStartLow=(Long)jSpinnerStartLow.getValue();
      long valStartHigh=(Long)jSpinnerStartHigh.getValue();
      long valEndLow=(Long)jSpinnerEndLow.getValue();
      long valEndHigh=(Long)jSpinnerEndHigh.getValue();
      
      if (valStartLow>valEndLow) {
        JOptionPane.showMessageDialog(this, "Low end position must be after low start position");
        return;
      }
      
      if (valStartHigh>valEndHigh) {
        JOptionPane.showMessageDialog(this, "High end position must be after high start position");
        return;
      } 
      
      if (valEndLow-valStartLow != valEndHigh-valStartHigh) {
        JOptionPane.showMessageDialog(this, "The size area from low and high must be equal");
        return;
      }
      
      if (valStartLow<valStartHigh && valEndLow>valStartHigh) {
        JOptionPane.showMessageDialog(this, "Starting high position cannot be inside low area");
        return;
      }
      
      if (valStartLow<valEndHigh && valEndLow>valEndHigh) {
        JOptionPane.showMessageDialog(this, "Ending high position cannot be inside low area");
        return;
      }      
      
      MemoryDasm low;
      MemoryDasm high;
      int position;
      
      // make the action
      for (long i=0; i<=valEndLow-valStartLow; i++) {
        low=memories[(int)(i+valStartLow)];
        high=memories[(int)(i+valStartHigh)];
        
        position=(low.copy & 0xFF)+((high.copy &0xFF)<<8);
        
        low.type=TYPE_MINOR;
        low.related=position;
        
        high.type=TYPE_MAJOR;
        high.related=position;
      }
      
      setVisible(false);
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
     setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOk2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOk2ActionPerformed
      if (memories==null) return;
      
      long valStartLow=(Long)jSpinnerStartLow2.getValue();
      long valStartHigh=(Long)jSpinnerStartHigh2.getValue();
      int size=(Integer)jSpinnerSize2.getValue();

      if ((valStartLow!=valStartHigh+1) && (valStartLow!=valStartHigh-1)) {
        JOptionPane.showMessageDialog(this, "High star position must be before or after low starting position by 1");
        return; 
      }
      
      MemoryDasm low;
      MemoryDasm high;
      int position;
      
      String prefix=jTextFieldPrefix2.getText();
      int start=(Integer)jSpinnerStart2.getValue();
      boolean uppercase=jCheckBoxUpper2.isSelected();
      int digit=(Integer)jSpinnerDigit2.getValue();
      String label;
      
      for (int i=0; i<size; i++) {
        low=memories[(int)(i*2+valStartLow)];
        high=memories[(int)(i*2+valStartHigh)];  
        
        position=(low.copy & 0xFF)+((high.copy &0xFF)<<8);
        low.type=TYPE_MINOR;
        low.related=position;
        
        high.type=TYPE_MAJOR;
        high.related=position;  
                
        
        if (prefix!=null && !"".equals(prefix)) { 
          label=Integer.toHexString(i+start);
          if (label.length()==1 && digit==2) label="0"+label;
          if (uppercase) label=label.toUpperCase();
          else label=label.toLowerCase();
          label=prefix+label;        
          memories[position].userLocation=label;
        }  
      }
    }//GEN-LAST:event_jButtonOk2ActionPerformed

  private void jButtonRelativeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRelativeActionPerformed
    // assign << >> values to memories location 
    
    long base;
    long dest;
    
    try {
      base=(Long)jSpinnerBase.getValue();
      dest=(long)jSpinnerDest.getValue();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Invalid number for address", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;        
    }
    
    int row=jTable.getSelectedRow();
    if (row<0) {
      JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
    }
    
    int rows[]=jTable.getSelectedRows();
    if (rows.length==1) {
      JOptionPane.showMessageDialog(this, "At least two rows must be selected", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
    }    
    
    MemoryDasm low, high;
    int address;
    
    for (int i=0; i<rows.length; i+=2) {
            
      if (i==rows.length-1) break;
      low=memories[rows[i]];
      high=memories[rows[i+1]];
      
      if (low.address<base) {
        JOptionPane.showMessageDialog(this, "Selected address must be after or equal to base address", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
      }
    
      // the address is not the one inserted, but a relocated one
      address=(int)((low.copy & 0xFF) + ((high.copy & 0xFF)<<8)-base+dest);
    
      low.related=address;          
      if (low.type==TYPE_PLUS) high.type=TYPE_PLUS_MINOR;
      else low.type=TYPE_MINOR;      
      
      high.related=address;
      if (high.type==TYPE_PLUS) high.type=TYPE_PLUS_MAJOR;
      else high.type=TYPE_MAJOR;     
      
      low.relatedAddressBase=(int)base;
      low.relatedAddressDest=(int)dest;
      
      high.relatedAddressBase=(int)base;
      high.relatedAddressDest=(int)dest;
    }
   
    jTable.setRowSelectionInterval(rows[0], rows[rows.length-1]);  
    setVisible(false);
  }//GEN-LAST:event_jButtonRelativeActionPerformed

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
            java.util.logging.Logger.getLogger(JAutoLoHiDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JAutoLoHiDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JAutoLoHiDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JAutoLoHiDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JAutoLoHiDialog dialog = new JAutoLoHiDialog(new javax.swing.JFrame(), true);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JButton jButtonOk2;
    private javax.swing.JButton jButtonRelative;
    private javax.swing.JCheckBox jCheckBoxUpper2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelBaseAddress;
    private javax.swing.JLabel jLabelDestinationAddress;
    private javax.swing.JLabel jLabelDigit2;
    private javax.swing.JLabel jLabelEndHigh;
    private javax.swing.JLabel jLabelEndLo;
    private javax.swing.JLabel jLabelPrefix2;
    private javax.swing.JLabel jLabelRelated;
    private javax.swing.JLabel jLabelSize2;
    private javax.swing.JLabel jLabelStart2;
    private javax.swing.JLabel jLabelStartHi;
    private javax.swing.JLabel jLabelStartHi2;
    private javax.swing.JLabel jLabelStartLo;
    private javax.swing.JLabel jLabelStartLo2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSpinner jSpinnerBase;
    private javax.swing.JSpinner jSpinnerDest;
    private javax.swing.JSpinner jSpinnerDigit2;
    private javax.swing.JSpinner jSpinnerEndHigh;
    private javax.swing.JSpinner jSpinnerEndLow;
    private javax.swing.JSpinner jSpinnerSize2;
    private javax.swing.JSpinner jSpinnerStart2;
    private javax.swing.JSpinner jSpinnerStartHigh;
    private javax.swing.JSpinner jSpinnerStartHigh2;
    private javax.swing.JSpinner jSpinnerStartLow;
    private javax.swing.JSpinner jSpinnerStartLow2;
    private javax.swing.JTextField jTextFieldPrefix2;
    // End of variables declaration//GEN-END:variables
}
