/**
 * @(#)JFrameDisassembler.java 2019/12/01
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

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Utilities;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;
import sw_emulator.software.Disassembly;
import sw_emulator.software.MemoryDasm;
import sw_emulator.software.memory.memoryState;
import sw_emulator.swing.main.FileManager;
import sw_emulator.swing.main.MPR;
import sw_emulator.swing.main.Option;
import sw_emulator.swing.main.Project;
import sw_emulator.swing.main.userAction;
import static sw_emulator.swing.main.userAction.SOURCE_FINDD;
import sw_emulator.swing.table.DataTableModelMemory;
import sw_emulator.swing.table.MemoryTableCellRenderer;

/**
 * Main frame for C64 disassembler
 * 
 * @author ice
 */
public class JDisassemblerFrame extends javax.swing.JFrame implements userAction {
  /** Option to use */
  Option option=new Option();
  
  /** Project to use */
  Project project;
  
  /** Project file name */
  File projectFile;
  
  /** Last saved project values */
  Project savedProject;
  
  /** Data table for memory */
  DataTableModelMemory dataTableModelMemory=new DataTableModelMemory();
  
  /** Disassembly engine  */
  Disassembly disassembly=new Disassembly();
  
  /** Option dialog */
  JOptionDialog jOptionDialog=new JOptionDialog(this, true);
  
  /** Project dialog */
  JProjectDialog jProjectDialog=new JProjectDialog(this, true);
  
  /** Project chhoser file dialog*/
  JFileChooser projectChooserFile=new JFileChooser();
  
  /** Export as file chooser */
  JFileChooser exportAsChooserFile=new JFileChooser();
  
  /** Load MPR as file chooser */
  JFileChooser optionMPRLoadChooserFile=new JFileChooser();
  
  /** Save MPR as file chooser */
  JFileChooser optionMPRSaveChooserFile=new JFileChooser();
  
  /** Memory cell renderer for table */
  MemoryTableCellRenderer memoryTableCellRenderer=new MemoryTableCellRenderer();
  
  /** License dialog */
  JLicenseDialog jLicenseDialog=new JLicenseDialog(this, true);
  
  /** Credit dialog */
  JCreditsDialog jCreditsDialog=new JCreditsDialog(this, true);
  
  /** About dialog */
  JAboutDialog jAboutDialog=new JAboutDialog(this, true);
  
  /** Help dialog */
  JHelpFrame jHelpFrame=new JHelpFrame();
  
  /** Find dialog for source */
  FindDialog findDialogSource;
  
  /** Find dialog for disassembly */
  FindDialog findDialogDis;
  
  
    /**
     * Creates new form JFrameDisassembler
     */
    public JDisassemblerFrame() {        
        initComponents();
        Shared.framesList.add(this);
        Shared.framesList.add(projectChooserFile);
        Shared.framesList.add(exportAsChooserFile);
        Shared.framesList.add(optionMPRLoadChooserFile);
        Shared.framesList.add(optionMPRSaveChooserFile);
        Shared.framesList.add(findDialogDis);
        Shared.framesList.add(findDialogSource);
        findDialogDis.setSearchString(" ");
        findDialogSource.setSearchString(" ");
        
        FileManager.instance.readOptionFile(FileManager.optionFile, option);
        
        if (option.getLafName().equals("SYNTH")) Option.useLookAndFeel(option.getFlatLaf());
        else Option.useLookAndFeel(option.getLafName(), option.getMethalTheme());
        
        jOptionDialog.useOption(option);
        
        projectChooserFile.addChoosableFileFilter(new FileNameExtensionFilter("JC64Dis (*.dis)", "dis"));
        exportAsChooserFile.addChoosableFileFilter(new FileNameExtensionFilter("Source (*.txt)","txt"));
        optionMPRLoadChooserFile.addChoosableFileFilter(new FileNameExtensionFilter("PRG C64 program (prg, bin)", "prg", "bin"));
        optionMPRLoadChooserFile.setMultiSelectionEnabled(true);
        optionMPRLoadChooserFile.setDialogTitle("Select all PRG to include into the MPR");    
        optionMPRSaveChooserFile.addChoosableFileFilter(new FileNameExtensionFilter("Multi PRG C64 program (mpr)", "mpr"));
        optionMPRSaveChooserFile.setDialogTitle("Select the MPR file to save");          
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar = new javax.swing.JToolBar();
        jButtonNewProject = new javax.swing.JButton();
        jButtonOpenProject = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jButtonSaveProject = new javax.swing.JButton();
        jButtonSaveProjectAs = new javax.swing.JButton();
        jButtonMPR = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jSeparatorButton1 = new javax.swing.JToolBar.Separator();
        jButtonClearDMem = new javax.swing.JButton();
        jButtonClearUMem = new javax.swing.JButton();
        jButtonClearDLabel = new javax.swing.JButton();
        jButtonAddUserComm = new javax.swing.JButton();
        jButtonAddUserBlock = new javax.swing.JButton();
        jButtonAddUserLabel = new javax.swing.JButton();
        jButtonAddUserLabelOp = new javax.swing.JButton();
        jButtonMarkCode = new javax.swing.JButton();
        jButtonMarkData = new javax.swing.JButton();
        jButtonMarkGarbage = new javax.swing.JButton();
        jButtonMarkPlus = new javax.swing.JButton();
        jButtonMarkMinus = new javax.swing.JButton();
        jButtonMarkLow = new javax.swing.JButton();
        jButtonMarkMax = new javax.swing.JButton();
        jSeparatorButton3 = new javax.swing.JToolBar.Separator();
        jButtonConfigure = new javax.swing.JButton();
        jButtonSIDLD = new javax.swing.JButton();
        jButtonViewProject = new javax.swing.JButton();
        jSeparatorButton2 = new javax.swing.JToolBar.Separator();
        jButtonFindMem = new javax.swing.JButton();
        jButtonDisassemble = new javax.swing.JButton();
        jButtonFindDis = new javax.swing.JButton();
        jButtonExportAsDiss = new javax.swing.JButton();
        jButtonFindSource = new javax.swing.JButton();
        jButtonExportAsSource = new javax.swing.JButton();
        jSplitPaneExternal = new javax.swing.JSplitPane();
        jSplitPaneInternal = new javax.swing.JSplitPane();
        jScrollPaneLeft = new javax.swing.JScrollPane();
        rSyntaxTextAreaDis = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
        jScrollPaneRight = new javax.swing.JScrollPane();
        rSyntaxTextAreaSource = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
        jScrollPaneMemory = new javax.swing.JScrollPane();
        jTableMemory = new javax.swing.JTable() {

            //Implement table cell tool tips.
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                int realColumnIndex = convertColumnIndexToModel(colIndex);

                try {
                    MemoryDasm memory=dataTableModelMemory.getData()[rowIndex];
                    switch (dataTableModelMemory.columns[realColumnIndex]) {
                        case ID:
                        if (!memory.isInside) tip="Memory outside of the program";
                        else if(memory.isCode) tip="Memory marked as code";
                        else if (memory.isData) tip="Memory marked as data";
                        else tip="Memory not marked as code or data";
                        break;
                        case DC:
                        if ((Boolean)getValueAt(rowIndex, colIndex)) tip=memory.dasmComment;
                        break;
                        case UC:
                        if ((Boolean)getValueAt(rowIndex, colIndex)) tip=memory.userComment;
                        break;
                        case DL:
                        if ((Boolean)getValueAt(rowIndex, colIndex)) tip=memory.dasmLocation;
                        break;
                        case UL:
                        if ((Boolean)getValueAt(rowIndex, colIndex)) tip=memory.userLocation;
                        break;
                        case UB:
                        if ((Boolean)getValueAt(rowIndex, colIndex)) tip="<html>"+memory.userBlockComment.replace("\n", "<br>")+"</html>";
                        break;
                        case RE:
                        if (memory.type!=' ') {
                            MemoryDasm mem=dataTableModelMemory.getData()[memory.related];
                            if (memory.type=='+') {
                                if (mem.userLocation!=null && !"".equals(mem.userLocation)) tip=mem.userLocation+"+"+(memory.address-memory.related);
                                else if (mem.dasmLocation!=null && !"".equals(mem.dasmLocation)) tip=mem.dasmLocation+"+"+(memory.address-memory.related);
                                else tip="$"+ShortToExe(mem.address)+"+"+(memory.address-memory.related);
                            } else {
                                if (mem.userLocation!=null && !"".equals(mem.userLocation)) tip="#"+memory.type+mem.userLocation;
                                else if (mem.dasmLocation!=null && !"".equals(mem.dasmLocation)) tip="#"+memory.type+mem.dasmLocation;
                                else tip="#"+memory.type+"$"+ShortToExe(mem.address);
                            }
                        }
                        break;
                    }
                } catch (RuntimeException e1) {
                    //catch null pointer exception if mouse is over an empty line
                }
                return tip;
            }

        };
        jMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemNewProject = new javax.swing.JMenuItem();
        jSeparatorProject1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemOpenProject = new javax.swing.JMenuItem();
        jMenuItemCloseProject = new javax.swing.JMenuItem();
        jSeparatorProject2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemSaveProject = new javax.swing.JMenuItem();
        jMenuItemSaveAsProject = new javax.swing.JMenuItem();
        jSeparatorProject3 = new javax.swing.JPopupMenu.Separator();
        jMenuItemMPR = new javax.swing.JMenuItem();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuMarkGarbage = new javax.swing.JMenu();
        jMenuItemClearDMem = new javax.swing.JMenuItem();
        jMenuItemClearUMem = new javax.swing.JMenuItem();
        jMenuItemClearDLabel = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItemAddComment = new javax.swing.JMenuItem();
        jMenuItemAddBlock = new javax.swing.JMenuItem();
        jMenuItemUserLabel = new javax.swing.JMenuItem();
        jMenuItemUserLabelOp = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItemMarkCode = new javax.swing.JMenuItem();
        jMenuItemMarkData = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItemPlus = new javax.swing.JMenuItem();
        jMenuItemMinus = new javax.swing.JMenuItem();
        jMenuItemMemLow = new javax.swing.JMenuItem();
        jMenuItemMemHigh = new javax.swing.JMenuItem();
        jMenuOption = new javax.swing.JMenu();
        jMenuItemConfigure = new javax.swing.JMenuItem();
        jMenuItemSIDLD = new javax.swing.JMenuItem();
        jSeparatorOption = new javax.swing.JPopupMenu.Separator();
        jMenuItemViewProject = new javax.swing.JMenuItem();
        jMenuSource = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItemDiss = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemFindDis = new javax.swing.JMenuItem();
        jMenuItemDissSaveAs = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemFindSource = new javax.swing.JMenuItem();
        jMenuItemSourceSaveAs = new javax.swing.JMenuItem();
        jMenuHelpContents = new javax.swing.JMenu();
        jMenuItemContents = new javax.swing.JMenuItem();
        jSeparatorHelp1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemLicense = new javax.swing.JMenuItem();
        jMenuItemCredits = new javax.swing.JMenuItem();
        jMenuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("JC64Dis");

        jToolBar.setFloatable(false);
        jToolBar.setRollover(true);

        jButtonNewProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/filenew.png"))); // NOI18N
        jButtonNewProject.setToolTipText("New project");
        jButtonNewProject.setFocusable(false);
        jButtonNewProject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonNewProject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonNewProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewProjectActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonNewProject);

        jButtonOpenProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/fileopen.png"))); // NOI18N
        jButtonOpenProject.setToolTipText("Open project");
        jButtonOpenProject.setFocusable(false);
        jButtonOpenProject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonOpenProject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonOpenProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenProjectActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonOpenProject);

        jButtonClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/close.png"))); // NOI18N
        jButtonClose.setToolTipText("Close the project");
        jButtonClose.setFocusable(false);
        jButtonClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonClose);

        jButtonSaveProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/filesave.png"))); // NOI18N
        jButtonSaveProject.setToolTipText("Save project");
        jButtonSaveProject.setFocusable(false);
        jButtonSaveProject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSaveProject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSaveProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveProjectActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonSaveProject);

        jButtonSaveProjectAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/filesaveas.png"))); // NOI18N
        jButtonSaveProjectAs.setToolTipText("Save project as");
        jButtonSaveProjectAs.setFocusable(false);
        jButtonSaveProjectAs.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSaveProjectAs.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSaveProjectAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveProjectAsActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonSaveProjectAs);

        jButtonMPR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/create.png"))); // NOI18N
        jButtonMPR.setToolTipText("Create a MRP archive");
        jButtonMPR.setFocusable(false);
        jButtonMPR.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonMPR.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonMPR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMPRActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonMPR);

        jButtonExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/exit.png"))); // NOI18N
        jButtonExit.setToolTipText("Save project as");
        jButtonExit.setFocusable(false);
        jButtonExit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonExit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonExit);
        jToolBar.add(jSeparatorButton1);

        jButtonClearDMem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/eraser.png"))); // NOI18N
        jButtonClearDMem.setToolTipText("Erase dasm automatic comment");
        jButtonClearDMem.setFocusable(false);
        jButtonClearDMem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonClearDMem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonClearDMem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearDMemActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonClearDMem);

        jButtonClearUMem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/eraser1.png"))); // NOI18N
        jButtonClearUMem.setToolTipText("Erase user comment");
        jButtonClearUMem.setFocusable(false);
        jButtonClearUMem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonClearUMem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonClearUMem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearUMemActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonClearUMem);

        jButtonClearDLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/eraser2.png"))); // NOI18N
        jButtonClearDLabel.setToolTipText("Erase dasm automatic label");
        jButtonClearDLabel.setFocusable(false);
        jButtonClearDLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonClearDLabel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonClearDLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearDLabelActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonClearDLabel);

        jButtonAddUserComm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/comm.png"))); // NOI18N
        jButtonAddUserComm.setToolTipText("Add user comment");
        jButtonAddUserComm.setFocusable(false);
        jButtonAddUserComm.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAddUserComm.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAddUserComm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddUserCommActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonAddUserComm);

        jButtonAddUserBlock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/block.png"))); // NOI18N
        jButtonAddUserBlock.setToolTipText("Add a block user comment");
        jButtonAddUserBlock.setFocusable(false);
        jButtonAddUserBlock.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAddUserBlock.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAddUserBlock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddUserBlockActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonAddUserBlock);

        jButtonAddUserLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mem2.png"))); // NOI18N
        jButtonAddUserLabel.setToolTipText("Add user label");
        jButtonAddUserLabel.setFocusable(false);
        jButtonAddUserLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAddUserLabel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAddUserLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddUserLabelActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonAddUserLabel);

        jButtonAddUserLabelOp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mem3.png"))); // NOI18N
        jButtonAddUserLabelOp.setToolTipText("Add user label on next word address");
        jButtonAddUserLabelOp.setFocusable(false);
        jButtonAddUserLabelOp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAddUserLabelOp.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAddUserLabelOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddUserLabelOpActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonAddUserLabelOp);

        jButtonMarkCode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/code.png"))); // NOI18N
        jButtonMarkCode.setToolTipText("Mark the selected addresses as code");
        jButtonMarkCode.setFocusable(false);
        jButtonMarkCode.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonMarkCode.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonMarkCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMarkCodeActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonMarkCode);

        jButtonMarkData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/data.png"))); // NOI18N
        jButtonMarkData.setToolTipText("Mark the selected addresses as data");
        jButtonMarkData.setFocusable(false);
        jButtonMarkData.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonMarkData.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonMarkData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMarkDataActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonMarkData);

        jButtonMarkGarbage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/garb.png"))); // NOI18N
        jButtonMarkGarbage.setToolTipText("Mark the selected addresses as garbage");
        jButtonMarkGarbage.setFocusable(false);
        jButtonMarkGarbage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonMarkGarbage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonMarkGarbage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMarkGarbageActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonMarkGarbage);

        jButtonMarkPlus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/plus.png"))); // NOI18N
        jButtonMarkPlus.setToolTipText("Mark the selected addresses as +");
        jButtonMarkPlus.setFocusable(false);
        jButtonMarkPlus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonMarkPlus.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonMarkPlus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMarkPlusActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonMarkPlus);

        jButtonMarkMinus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/minus.png"))); // NOI18N
        jButtonMarkMinus.setToolTipText("Mark the selected addresses as -");
        jButtonMarkMinus.setFocusable(false);
        jButtonMarkMinus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonMarkMinus.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonMarkMinus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMarkMinusActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonMarkMinus);

        jButtonMarkLow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/min.png"))); // NOI18N
        jButtonMarkLow.setToolTipText("Assign the selected address as #<");
        jButtonMarkLow.setFocusable(false);
        jButtonMarkLow.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonMarkLow.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonMarkLow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMarkLowActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonMarkLow);

        jButtonMarkMax.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/max.png"))); // NOI18N
        jButtonMarkMax.setToolTipText("Assign the selected address as #>");
        jButtonMarkMax.setFocusable(false);
        jButtonMarkMax.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonMarkMax.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonMarkMax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMarkMaxActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonMarkMax);
        jToolBar.add(jSeparatorButton3);

        jButtonConfigure.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/configure.png"))); // NOI18N
        jButtonConfigure.setToolTipText("Set general option");
        jButtonConfigure.setFocusable(false);
        jButtonConfigure.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonConfigure.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonConfigure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConfigureActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonConfigure);

        jButtonSIDLD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mem.png"))); // NOI18N
        jButtonSIDLD.setToolTipText("Apply SIDLD flags to memory");
        jButtonSIDLD.setFocusable(false);
        jButtonSIDLD.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSIDLD.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSIDLD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSIDLDActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonSIDLD);

        jButtonViewProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/view_detailed.png"))); // NOI18N
        jButtonViewProject.setToolTipText("View project");
        jButtonViewProject.setFocusable(false);
        jButtonViewProject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonViewProject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonViewProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonViewProjectActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonViewProject);
        jToolBar.add(jSeparatorButton2);

        jButtonFindMem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/finda.png"))); // NOI18N
        jButtonFindMem.setToolTipText("Find a memory address");
        jButtonFindMem.setFocusable(false);
        jButtonFindMem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonFindMem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonFindMem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindMemActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonFindMem);

        jButtonDisassemble.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/exec.png"))); // NOI18N
        jButtonDisassemble.setToolTipText("Disassemble");
        jButtonDisassemble.setFocusable(false);
        jButtonDisassemble.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDisassemble.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDisassemble.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDisassembleActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonDisassemble);

        jButtonFindDis.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/findd.png"))); // NOI18N
        jButtonFindDis.setToolTipText("Find a text in preview");
        jButtonFindDis.setFocusable(false);
        jButtonFindDis.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonFindDis.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonFindDis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindDisActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonFindDis);

        jButtonExportAsDiss.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/exportas1.png"))); // NOI18N
        jButtonExportAsDiss.setToolTipText("Save preview file");
        jButtonExportAsDiss.setFocusable(false);
        jButtonExportAsDiss.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonExportAsDiss.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonExportAsDiss.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportAsDissActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonExportAsDiss);

        jButtonFindSource.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/finds.png"))); // NOI18N
        jButtonFindSource.setToolTipText("Find a text in source");
        jButtonFindSource.setFocusable(false);
        jButtonFindSource.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonFindSource.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonFindSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindSourceActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonFindSource);

        jButtonExportAsSource.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/exportas2.png"))); // NOI18N
        jButtonExportAsSource.setToolTipText("Save source file");
        jButtonExportAsSource.setFocusable(false);
        jButtonExportAsSource.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonExportAsSource.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonExportAsSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportAsSourceActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonExportAsSource);

        jSplitPaneExternal.setToolTipText("");

        jSplitPaneInternal.setResizeWeight(0.5);
        jSplitPaneInternal.setToolTipText("");

        rSyntaxTextAreaDis.setEditable(false);
        rSyntaxTextAreaDis.setColumns(20);
        rSyntaxTextAreaDis.setRows(5);
        rSyntaxTextAreaDis.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        rSyntaxTextAreaDis.setSyntaxEditingStyle("text/asm6502");
        findDialogDis=new FindDialog(this, new SearchListener() {
            @Override
            public void searchEvent(SearchEvent e) {
                SearchEvent.Type type = e.getType();
                SearchContext context = e.getSearchContext();
                SearchResult result;

                switch (type) {
                    default: // Prevent FindBugs warning later
                    case MARK_ALL:
                    result = SearchEngine.markAll(rSyntaxTextAreaDis, context);
                    break;
                    case FIND:
                    result = SearchEngine.find(rSyntaxTextAreaDis, context);
                    if (!result.wasFound()) {
                        UIManager.getLookAndFeel().provideErrorFeedback(rSyntaxTextAreaDis);
                    }
                    break;
                }

            }

            @Override
            public String getSelectedText() {
                return rSyntaxTextAreaDis.getSelectedText();
            }
        }
    );

    rSyntaxTextAreaDis.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK),
        new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                execute(SOURCE_FINDD);
            }
        }
    );
    rSyntaxTextAreaDis.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            rSyntaxTextAreaDisMouseClicked(evt);
        }
    });
    jScrollPaneLeft.setViewportView(rSyntaxTextAreaDis);

    jSplitPaneInternal.setLeftComponent(jScrollPaneLeft);

    rSyntaxTextAreaSource.setEditable(false);
    rSyntaxTextAreaSource.setColumns(20);
    rSyntaxTextAreaSource.setRows(5);
    rSyntaxTextAreaSource.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
    rSyntaxTextAreaSource.setSyntaxEditingStyle("text/asm6502");
    findDialogSource=new FindDialog(this, new SearchListener() {
        @Override
        public void searchEvent(SearchEvent e) {
            SearchEvent.Type type = e.getType();
            SearchContext context = e.getSearchContext();
            SearchResult result;

            switch (type) {
                default: // Prevent FindBugs warning later
                case MARK_ALL:
                result = SearchEngine.markAll(rSyntaxTextAreaSource, context);
                break;
                case FIND:
                result = SearchEngine.find(rSyntaxTextAreaSource, context);
                if (!result.wasFound()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(rSyntaxTextAreaSource);
                }
                break;
            }

        }

        @Override
        public String getSelectedText() {
            return rSyntaxTextAreaSource.getSelectedText();
        }
    }
    );

    rSyntaxTextAreaSource.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK),
        new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                execute(SOURCE_FINDS);
            }
        }
    );
    rSyntaxTextAreaSource.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            rSyntaxTextAreaSourceMouseClicked(evt);
        }
    });
    jScrollPaneRight.setViewportView(rSyntaxTextAreaSource);

    jSplitPaneInternal.setRightComponent(jScrollPaneRight);

    jSplitPaneExternal.setRightComponent(jSplitPaneInternal);

    jScrollPaneMemory.setPreferredSize(new java.awt.Dimension(170, 403));

    jTableMemory.setModel(dataTableModelMemory);
    jTableMemory.setDefaultRenderer(Integer.class, memoryTableCellRenderer);
    jTableMemory.getColumnModel().getColumn(0).setPreferredWidth(310);

    InputMap im = this.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    ActionMap am = this.getRootPane().getActionMap();

    //add custom action
    im.put(KeyStroke.getKeyStroke("control F"), "save");
    am.put("save", new AbstractAction(){
        @Override
        public void actionPerformed(ActionEvent ae) {
            execute(SOURCE_FINDA);
        }
    });

    ((InputMap)UIManager.get("Table.ancestorInputMap")).put(KeyStroke.getKeyStroke("control F"), "none");

    jScrollPaneMemory.setViewportView(jTableMemory);

    jSplitPaneExternal.setLeftComponent(jScrollPaneMemory);

    jMenuFile.setText("File");

    jMenuItemNewProject.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
    jMenuItemNewProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/filenew.png"))); // NOI18N
    jMenuItemNewProject.setMnemonic('w');
    jMenuItemNewProject.setText("New Project");
    jMenuItemNewProject.setToolTipText("");
    jMenuItemNewProject.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemNewProjectActionPerformed(evt);
        }
    });
    jMenuFile.add(jMenuItemNewProject);
    jMenuFile.add(jSeparatorProject1);

    jMenuItemOpenProject.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
    jMenuItemOpenProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/fileopen.png"))); // NOI18N
    jMenuItemOpenProject.setMnemonic('e');
    jMenuItemOpenProject.setText("Open Project");
    jMenuItemOpenProject.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemOpenProjectActionPerformed(evt);
        }
    });
    jMenuFile.add(jMenuItemOpenProject);

    jMenuItemCloseProject.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
    jMenuItemCloseProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/close.png"))); // NOI18N
    jMenuItemCloseProject.setMnemonic('c');
    jMenuItemCloseProject.setText("Close Project");
    jMenuItemCloseProject.setToolTipText("");
    jMenuItemCloseProject.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemCloseProjectActionPerformed(evt);
        }
    });
    jMenuFile.add(jMenuItemCloseProject);
    jMenuFile.add(jSeparatorProject2);

    jMenuItemSaveProject.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    jMenuItemSaveProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/filesave.png"))); // NOI18N
    jMenuItemSaveProject.setMnemonic('s');
    jMenuItemSaveProject.setText("Save Project");
    jMenuItemSaveProject.setToolTipText("");
    jMenuItemSaveProject.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemSaveProjectActionPerformed(evt);
        }
    });
    jMenuFile.add(jMenuItemSaveProject);

    jMenuItemSaveAsProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/filesaveas.png"))); // NOI18N
    jMenuItemSaveAsProject.setMnemonic('v');
    jMenuItemSaveAsProject.setText("Save Project As");
    jMenuItemSaveAsProject.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemSaveAsProjectActionPerformed(evt);
        }
    });
    jMenuFile.add(jMenuItemSaveAsProject);
    jMenuFile.add(jSeparatorProject3);

    jMenuItemMPR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/create.png"))); // NOI18N
    jMenuItemMPR.setText("Create a MPR archive");
    jMenuItemMPR.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemMPRActionPerformed(evt);
        }
    });
    jMenuFile.add(jMenuItemMPR);

    jMenuItemExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/exit.png"))); // NOI18N
    jMenuItemExit.setMnemonic('x');
    jMenuItemExit.setText("Exit");
    jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemExitActionPerformed(evt);
        }
    });
    jMenuFile.add(jMenuItemExit);

    jMenuBar.add(jMenuFile);

    jMenuMarkGarbage.setText("Memory");

    jMenuItemClearDMem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/eraser.png"))); // NOI18N
    jMenuItemClearDMem.setText("Clear dasm automatic comment");
    jMenuItemClearDMem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemClearDMemActionPerformed(evt);
        }
    });
    jMenuMarkGarbage.add(jMenuItemClearDMem);

    jMenuItemClearUMem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/eraser1.png"))); // NOI18N
    jMenuItemClearUMem.setText("Clear user comment");
    jMenuItemClearUMem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemClearUMemActionPerformed(evt);
        }
    });
    jMenuMarkGarbage.add(jMenuItemClearUMem);

    jMenuItemClearDLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/eraser2.png"))); // NOI18N
    jMenuItemClearDLabel.setText("Clear dasm automatic label");
    jMenuItemClearDLabel.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemClearDLabelActionPerformed(evt);
        }
    });
    jMenuMarkGarbage.add(jMenuItemClearDLabel);
    jMenuMarkGarbage.add(jSeparator4);

    jMenuItemAddComment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/comm.png"))); // NOI18N
    jMenuItemAddComment.setText("Add user comment");
    jMenuItemAddComment.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemAddCommentActionPerformed(evt);
        }
    });
    jMenuMarkGarbage.add(jMenuItemAddComment);

    jMenuItemAddBlock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/block.png"))); // NOI18N
    jMenuItemAddBlock.setText("Add user block comment");
    jMenuItemAddBlock.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemAddBlockActionPerformed(evt);
        }
    });
    jMenuMarkGarbage.add(jMenuItemAddBlock);

    jMenuItemUserLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/mem2.png"))); // NOI18N
    jMenuItemUserLabel.setText("Add user label");
    jMenuItemUserLabel.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemUserLabelActionPerformed(evt);
        }
    });
    jMenuMarkGarbage.add(jMenuItemUserLabel);

    jMenuItemUserLabelOp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/mem3.png"))); // NOI18N
    jMenuItemUserLabelOp.setText("Add user label on next address");
    jMenuItemUserLabelOp.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemUserLabelOpActionPerformed(evt);
        }
    });
    jMenuMarkGarbage.add(jMenuItemUserLabelOp);
    jMenuMarkGarbage.add(jSeparator3);

    jMenuItemMarkCode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/code.png"))); // NOI18N
    jMenuItemMarkCode.setText("Mark as code");
    jMenuItemMarkCode.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemMarkCodeActionPerformed(evt);
        }
    });
    jMenuMarkGarbage.add(jMenuItemMarkCode);

    jMenuItemMarkData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/data.png"))); // NOI18N
    jMenuItemMarkData.setText("Mark as data");
    jMenuItemMarkData.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemMarkDataActionPerformed(evt);
        }
    });
    jMenuMarkGarbage.add(jMenuItemMarkData);

    jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/garb.png"))); // NOI18N
    jMenuItem2.setText("Mark as garbage");
    jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItem2ActionPerformed(evt);
        }
    });
    jMenuMarkGarbage.add(jMenuItem2);

    jMenuItemPlus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/plus.png"))); // NOI18N
    jMenuItemPlus.setText("Assign the selected address as +");
    jMenuItemPlus.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemPlusActionPerformed(evt);
        }
    });
    jMenuMarkGarbage.add(jMenuItemPlus);

    jMenuItemMinus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/minus.png"))); // NOI18N
    jMenuItemMinus.setText("Assign the selected address as -");
    jMenuItemMinus.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemMinusActionPerformed(evt);
        }
    });
    jMenuMarkGarbage.add(jMenuItemMinus);

    jMenuItemMemLow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/min.png"))); // NOI18N
    jMenuItemMemLow.setText("Assign the selected address as #<");
    jMenuItemMemLow.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemMemLowActionPerformed(evt);
        }
    });
    jMenuMarkGarbage.add(jMenuItemMemLow);

    jMenuItemMemHigh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/max.png"))); // NOI18N
    jMenuItemMemHigh.setText("Assign the selected address as #>");
    jMenuItemMemHigh.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemMemHighActionPerformed(evt);
        }
    });
    jMenuMarkGarbage.add(jMenuItemMemHigh);

    jMenuBar.add(jMenuMarkGarbage);

    jMenuOption.setText("Option");
    jMenuOption.setToolTipText("");

    jMenuItemConfigure.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/configure.png"))); // NOI18N
    jMenuItemConfigure.setMnemonic('o');
    jMenuItemConfigure.setText("General Option");
    jMenuItemConfigure.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemConfigureActionPerformed(evt);
        }
    });
    jMenuOption.add(jMenuItemConfigure);

    jMenuItemSIDLD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/mem.png"))); // NOI18N
    jMenuItemSIDLD.setText("Apply SIDLD flags to memory");
    jMenuItemSIDLD.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemSIDLDActionPerformed(evt);
        }
    });
    jMenuOption.add(jMenuItemSIDLD);
    jMenuOption.add(jSeparatorOption);

    jMenuItemViewProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/view_detailed.png"))); // NOI18N
    jMenuItemViewProject.setMnemonic('v');
    jMenuItemViewProject.setText("View Project");
    jMenuItemViewProject.setToolTipText("");
    jMenuItemViewProject.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemViewProjectActionPerformed(evt);
        }
    });
    jMenuOption.add(jMenuItemViewProject);

    jMenuBar.add(jMenuOption);

    jMenuSource.setText("Source");

    jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/finda.png"))); // NOI18N
    jMenuItem1.setText("Find memory address");
    jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItem1ActionPerformed(evt);
        }
    });
    jMenuSource.add(jMenuItem1);

    jMenuItemDiss.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/exec.png"))); // NOI18N
    jMenuItemDiss.setText("Disassemble");
    jMenuItemDiss.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemDissActionPerformed(evt);
        }
    });
    jMenuSource.add(jMenuItemDiss);
    jMenuSource.add(jSeparator1);

    jMenuItemFindDis.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/findd.png"))); // NOI18N
    jMenuItemFindDis.setText("Find text in preview");
    jMenuItemFindDis.setToolTipText("");
    jMenuItemFindDis.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemFindDisActionPerformed(evt);
        }
    });
    jMenuSource.add(jMenuItemFindDis);

    jMenuItemDissSaveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/exportas1.png"))); // NOI18N
    jMenuItemDissSaveAs.setText("Export As of preview");
    jMenuItemDissSaveAs.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemDissSaveAsActionPerformed(evt);
        }
    });
    jMenuSource.add(jMenuItemDissSaveAs);
    jMenuSource.add(jSeparator2);

    jMenuItemFindSource.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/finds.png"))); // NOI18N
    jMenuItemFindSource.setText("Find text in source");
    jMenuItemFindSource.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemFindSourceActionPerformed(evt);
        }
    });
    jMenuSource.add(jMenuItemFindSource);

    jMenuItemSourceSaveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/exportas2.png"))); // NOI18N
    jMenuItemSourceSaveAs.setText("Export As of source");
    jMenuItemSourceSaveAs.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemSourceSaveAsActionPerformed(evt);
        }
    });
    jMenuSource.add(jMenuItemSourceSaveAs);

    jMenuBar.add(jMenuSource);

    jMenuHelpContents.setText("Help");

    jMenuItemContents.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/help_index.png"))); // NOI18N
    jMenuItemContents.setText("Help contents");
    jMenuItemContents.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemContentsActionPerformed(evt);
        }
    });
    jMenuHelpContents.add(jMenuItemContents);
    jMenuHelpContents.add(jSeparatorHelp1);

    jMenuItemLicense.setText("License");
    jMenuItemLicense.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemLicenseActionPerformed(evt);
        }
    });
    jMenuHelpContents.add(jMenuItemLicense);

    jMenuItemCredits.setText("Credits");
    jMenuItemCredits.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemCreditsActionPerformed(evt);
        }
    });
    jMenuHelpContents.add(jMenuItemCredits);

    jMenuItemAbout.setText("About");
    jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemAboutActionPerformed(evt);
        }
    });
    jMenuHelpContents.add(jMenuItemAbout);

    jMenuBar.add(jMenuHelpContents);

    setJMenuBar(jMenuBar);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(jSplitPaneExternal)
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addComponent(jToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jSplitPaneExternal, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE))
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
      execute(APP_EXIT);
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    private void jMenuItemNewProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNewProjectActionPerformed
      execute(PROJ_NEW);
    }//GEN-LAST:event_jMenuItemNewProjectActionPerformed

    private void jMenuItemOpenProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenProjectActionPerformed
     execute(PROJ_OPEN);
    }//GEN-LAST:event_jMenuItemOpenProjectActionPerformed

    private void jMenuItemCloseProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCloseProjectActionPerformed
      execute(PROJ_CLOSE);
    }//GEN-LAST:event_jMenuItemCloseProjectActionPerformed

    private void jMenuItemSaveProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveProjectActionPerformed
      execute(PROJ_SAVE);
    }//GEN-LAST:event_jMenuItemSaveProjectActionPerformed

    private void jMenuItemSaveAsProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsProjectActionPerformed
      execute(PROJ_SAVEAS);
    }//GEN-LAST:event_jMenuItemSaveAsProjectActionPerformed

    private void jButtonNewProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewProjectActionPerformed
      execute(PROJ_NEW);
    }//GEN-LAST:event_jButtonNewProjectActionPerformed

    private void jButtonOpenProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenProjectActionPerformed
      execute(PROJ_OPEN);
    }//GEN-LAST:event_jButtonOpenProjectActionPerformed

    private void jButtonSaveProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveProjectActionPerformed
      execute(PROJ_SAVE);
    }//GEN-LAST:event_jButtonSaveProjectActionPerformed

    private void jButtonSaveProjectAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveProjectAsActionPerformed
      execute(PROJ_SAVEAS);
    }//GEN-LAST:event_jButtonSaveProjectAsActionPerformed

    private void jButtonConfigureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConfigureActionPerformed
      execute(OPTION_CONFIGURE);
    }//GEN-LAST:event_jButtonConfigureActionPerformed

    private void jMenuItemConfigureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConfigureActionPerformed
      execute(OPTION_CONFIGURE);
    }//GEN-LAST:event_jMenuItemConfigureActionPerformed

    private void jButtonDisassembleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDisassembleActionPerformed
      execute(SOURCE_DISASS);
    }//GEN-LAST:event_jButtonDisassembleActionPerformed

    private void jMenuItemDissActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDissActionPerformed
      execute(SOURCE_DISASS);
    }//GEN-LAST:event_jMenuItemDissActionPerformed

    private void jMenuItemViewProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemViewProjectActionPerformed
      execute(OPTION_VIEWPRJ); 
    }//GEN-LAST:event_jMenuItemViewProjectActionPerformed

    private void jMenuItemDissSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDissSaveAsActionPerformed
      execute(SOURCE_EXPASDIS); 
    }//GEN-LAST:event_jMenuItemDissSaveAsActionPerformed

    private void jButtonExportAsDissActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportAsDissActionPerformed
      execute(SOURCE_EXPASDIS); 
    }//GEN-LAST:event_jButtonExportAsDissActionPerformed

    private void jButtonExportAsSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportAsSourceActionPerformed
      execute(SOURCE_EXPASSOURCE); 
    }//GEN-LAST:event_jButtonExportAsSourceActionPerformed

    private void jMenuItemSourceSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSourceSaveAsActionPerformed
      execute(SOURCE_EXPASSOURCE);
    }//GEN-LAST:event_jMenuItemSourceSaveAsActionPerformed

    private void jButtonViewProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonViewProjectActionPerformed
      execute(OPTION_VIEWPRJ); 
    }//GEN-LAST:event_jButtonViewProjectActionPerformed

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
      execute(APP_EXIT); 
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void jMenuItemClearDMemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemClearDMemActionPerformed
      execute(MEM_CLEARDCOM);
    }//GEN-LAST:event_jMenuItemClearDMemActionPerformed

    private void jButtonClearDMemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearDMemActionPerformed
       execute(MEM_CLEARDCOM);
    }//GEN-LAST:event_jButtonClearDMemActionPerformed

    private void jButtonClearUMemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearUMemActionPerformed
      execute(MEM_CLEARUCOM);
    }//GEN-LAST:event_jButtonClearUMemActionPerformed

    private void jMenuItemClearUMemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemClearUMemActionPerformed
      execute(MEM_CLEARUCOM);
    }//GEN-LAST:event_jMenuItemClearUMemActionPerformed

    private void jButtonMarkCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMarkCodeActionPerformed
      execute(MEM_MARKCODE);
    }//GEN-LAST:event_jButtonMarkCodeActionPerformed

    private void jButtonMarkDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMarkDataActionPerformed
      execute(MEM_MARKDATA);
    }//GEN-LAST:event_jButtonMarkDataActionPerformed

    private void jMenuItemMarkCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMarkCodeActionPerformed
      execute(MEM_MARKCODE);
    }//GEN-LAST:event_jMenuItemMarkCodeActionPerformed

    private void jMenuItemMarkDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMarkDataActionPerformed
      execute(MEM_MARKDATA);  
    }//GEN-LAST:event_jMenuItemMarkDataActionPerformed

    private void jButtonSIDLDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSIDLDActionPerformed
      execute(OPTION_SIDLD);
    }//GEN-LAST:event_jButtonSIDLDActionPerformed

    private void jMenuItemSIDLDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSIDLDActionPerformed
      execute(OPTION_SIDLD);
    }//GEN-LAST:event_jMenuItemSIDLDActionPerformed

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
      execute(PROJ_CLOSE);
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jMenuItemContentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemContentsActionPerformed
      execute(HELP_CONTENTS);
    }//GEN-LAST:event_jMenuItemContentsActionPerformed

    private void jMenuItemLicenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLicenseActionPerformed
      execute(HELP_LICENSE);
    }//GEN-LAST:event_jMenuItemLicenseActionPerformed

    private void jMenuItemCreditsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCreditsActionPerformed
      execute(HELP_CREDITS);
    }//GEN-LAST:event_jMenuItemCreditsActionPerformed

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
      execute(HELP_ABOUT);
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    private void rSyntaxTextAreaDisMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rSyntaxTextAreaDisMouseClicked
      try {
        int addr=-1;  
        // get starting position of clicked point  
        int pos=Utilities.getRowStart(rSyntaxTextAreaDis, rSyntaxTextAreaDis.getCaretPosition());
        
        // get the first word of the string
        String str=rSyntaxTextAreaDis.getDocument().getText(pos,option.maxLabelLength);
        str=str.contains(" ") ? str.split(" ")[0] : str;  
 
        if (str.length()==4) addr=Integer.decode("0x"+str);
        else {
          str=str.contains(":") ? str.split(":")[0] : str;  
          for (MemoryDasm memory : project.memory) {
            if (str.equals(memory.dasmLocation) || str.equals(memory.userLocation)) {
                addr=memory.address;
                break;      
            }    
          }  
        }
        
        if (addr==-1) return;
                
        //scroll to that point
        jTableMemory.scrollRectToVisible(jTableMemory.getCellRect(addr,0, true)); 
        
        // select this row
        jTableMemory.setRowSelectionInterval(addr, addr);
      } catch (Exception e) {
          System.err.println(e);
      }
    }//GEN-LAST:event_rSyntaxTextAreaDisMouseClicked

    private void jMenuItemFindDisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFindDisActionPerformed
      execute(SOURCE_FINDD);
    }//GEN-LAST:event_jMenuItemFindDisActionPerformed

    private void jMenuItemFindSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFindSourceActionPerformed
      execute(SOURCE_FINDS);
    }//GEN-LAST:event_jMenuItemFindSourceActionPerformed

    private void jButtonFindDisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindDisActionPerformed
      execute(SOURCE_FINDD);
    }//GEN-LAST:event_jButtonFindDisActionPerformed

    private void jButtonFindSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindSourceActionPerformed
      execute(SOURCE_FINDS);
    }//GEN-LAST:event_jButtonFindSourceActionPerformed

    private void jButtonAddUserCommActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddUserCommActionPerformed
      execute(MEM_ADDCOMM);
    }//GEN-LAST:event_jButtonAddUserCommActionPerformed

    private void jMenuItemAddCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddCommentActionPerformed
      execute(MEM_ADDCOMM);
    }//GEN-LAST:event_jMenuItemAddCommentActionPerformed

    private void jButtonAddUserLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddUserLabelActionPerformed
      execute(MEM_ADDLABEL);
    }//GEN-LAST:event_jButtonAddUserLabelActionPerformed

    private void jMenuItemUserLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUserLabelActionPerformed
      execute(MEM_ADDLABEL);
    }//GEN-LAST:event_jMenuItemUserLabelActionPerformed

    private void jButtonAddUserBlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddUserBlockActionPerformed
      execute(MEM_ADDBLOCK);
    }//GEN-LAST:event_jButtonAddUserBlockActionPerformed

    private void jMenuItemAddBlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddBlockActionPerformed
      execute(MEM_ADDBLOCK);
    }//GEN-LAST:event_jMenuItemAddBlockActionPerformed

    private void rSyntaxTextAreaSourceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rSyntaxTextAreaSourceMouseClicked
      try {
        int addr=-1;  
        // get starting position of clicked point  
        int pos=Utilities.getRowStart(rSyntaxTextAreaSource, rSyntaxTextAreaSource.getCaretPosition());
        
        // get the first word of the string
        String str=rSyntaxTextAreaSource.getDocument().getText(pos,option.maxLabelLength);
        str=str.contains(" ") ? str.split(" ")[0] : str; 
 
        if (str.length()==4) addr=Integer.decode("0x"+str);
        else {
          str=str.contains(":") ? str.split(":")[0] : str;  
          for (MemoryDasm memory : project.memory) {
            if (str.equals(memory.dasmLocation) || str.equals(memory.userLocation)) {
                addr=memory.address;
                break;      
            }    
          }  
        }
        
        if (addr==-1) return;
                
        //scroll to that point
        jTableMemory.scrollRectToVisible(jTableMemory.getCellRect(addr,0, true)); 
        
        // select this row
        jTableMemory.setRowSelectionInterval(addr, addr);
      } catch (Exception e) {
          System.err.println(e);
      }
    }//GEN-LAST:event_rSyntaxTextAreaSourceMouseClicked

    private void jMenuItemClearDLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemClearDLabelActionPerformed
      execute(MEM_CLEARDLABEL);  
    }//GEN-LAST:event_jMenuItemClearDLabelActionPerformed

    private void jButtonClearDLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearDLabelActionPerformed
      execute(MEM_CLEARDLABEL);  
    }//GEN-LAST:event_jButtonClearDLabelActionPerformed

    private void jButtonMarkLowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMarkLowActionPerformed
      execute(MEM_LOW);
    }//GEN-LAST:event_jButtonMarkLowActionPerformed

    private void jButtonMarkMaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMarkMaxActionPerformed
      execute(MEM_HIGH);
    }//GEN-LAST:event_jButtonMarkMaxActionPerformed

    private void jMenuItemMemLowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMemLowActionPerformed
      execute(MEM_LOW);
    }//GEN-LAST:event_jMenuItemMemLowActionPerformed

    private void jMenuItemMemHighActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMemHighActionPerformed
      execute(MEM_HIGH);
    }//GEN-LAST:event_jMenuItemMemHighActionPerformed

    private void jButtonFindMemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindMemActionPerformed
      execute(SOURCE_FINDA);
    }//GEN-LAST:event_jButtonFindMemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
      execute(SOURCE_FINDA);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButtonMarkPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMarkPlusActionPerformed
      execute(MEM_PLUS);
    }//GEN-LAST:event_jButtonMarkPlusActionPerformed

    private void jMenuItemPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPlusActionPerformed
      execute(MEM_PLUS);
    }//GEN-LAST:event_jMenuItemPlusActionPerformed

    private void jButtonMarkMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMarkMinusActionPerformed
      execute(MEM_MINUS);
    }//GEN-LAST:event_jButtonMarkMinusActionPerformed

    private void jMenuItemMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMinusActionPerformed
      execute(MEM_MINUS);
    }//GEN-LAST:event_jMenuItemMinusActionPerformed

    private void jMenuItemMPRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMPRActionPerformed
      execute(OPTION_MPR);
    }//GEN-LAST:event_jMenuItemMPRActionPerformed

    private void jButtonMPRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMPRActionPerformed
      execute(OPTION_MPR);
    }//GEN-LAST:event_jButtonMPRActionPerformed

    private void jButtonAddUserLabelOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddUserLabelOpActionPerformed
      execute(MEM_ADDLABELOP);
    }//GEN-LAST:event_jButtonAddUserLabelOpActionPerformed

    private void jMenuItemUserLabelOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUserLabelOpActionPerformed
      execute(MEM_ADDLABELOP);
    }//GEN-LAST:event_jMenuItemUserLabelOpActionPerformed

    private void jButtonMarkGarbageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMarkGarbageActionPerformed
      execute(MEM_MARKGARB);
    }//GEN-LAST:event_jButtonMarkGarbageActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
      execute(MEM_MARKGARB);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

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
            java.util.logging.Logger.getLogger(JDisassemblerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDisassemblerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDisassemblerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDisassemblerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        Option.installLook();
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JDisassemblerFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddUserBlock;
    private javax.swing.JButton jButtonAddUserComm;
    private javax.swing.JButton jButtonAddUserLabel;
    private javax.swing.JButton jButtonAddUserLabelOp;
    private javax.swing.JButton jButtonClearDLabel;
    private javax.swing.JButton jButtonClearDMem;
    private javax.swing.JButton jButtonClearUMem;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonConfigure;
    private javax.swing.JButton jButtonDisassemble;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonExportAsDiss;
    private javax.swing.JButton jButtonExportAsSource;
    private javax.swing.JButton jButtonFindDis;
    private javax.swing.JButton jButtonFindMem;
    private javax.swing.JButton jButtonFindSource;
    private javax.swing.JButton jButtonMPR;
    private javax.swing.JButton jButtonMarkCode;
    private javax.swing.JButton jButtonMarkData;
    private javax.swing.JButton jButtonMarkGarbage;
    private javax.swing.JButton jButtonMarkLow;
    private javax.swing.JButton jButtonMarkMax;
    private javax.swing.JButton jButtonMarkMinus;
    private javax.swing.JButton jButtonMarkPlus;
    private javax.swing.JButton jButtonNewProject;
    private javax.swing.JButton jButtonOpenProject;
    private javax.swing.JButton jButtonSIDLD;
    private javax.swing.JButton jButtonSaveProject;
    private javax.swing.JButton jButtonSaveProjectAs;
    private javax.swing.JButton jButtonViewProject;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelpContents;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemAddBlock;
    private javax.swing.JMenuItem jMenuItemAddComment;
    private javax.swing.JMenuItem jMenuItemClearDLabel;
    private javax.swing.JMenuItem jMenuItemClearDMem;
    private javax.swing.JMenuItem jMenuItemClearUMem;
    private javax.swing.JMenuItem jMenuItemCloseProject;
    private javax.swing.JMenuItem jMenuItemConfigure;
    private javax.swing.JMenuItem jMenuItemContents;
    private javax.swing.JMenuItem jMenuItemCredits;
    private javax.swing.JMenuItem jMenuItemDiss;
    private javax.swing.JMenuItem jMenuItemDissSaveAs;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemFindDis;
    private javax.swing.JMenuItem jMenuItemFindSource;
    private javax.swing.JMenuItem jMenuItemLicense;
    private javax.swing.JMenuItem jMenuItemMPR;
    private javax.swing.JMenuItem jMenuItemMarkCode;
    private javax.swing.JMenuItem jMenuItemMarkData;
    private javax.swing.JMenuItem jMenuItemMemHigh;
    private javax.swing.JMenuItem jMenuItemMemLow;
    private javax.swing.JMenuItem jMenuItemMinus;
    private javax.swing.JMenuItem jMenuItemNewProject;
    private javax.swing.JMenuItem jMenuItemOpenProject;
    private javax.swing.JMenuItem jMenuItemPlus;
    private javax.swing.JMenuItem jMenuItemSIDLD;
    private javax.swing.JMenuItem jMenuItemSaveAsProject;
    private javax.swing.JMenuItem jMenuItemSaveProject;
    private javax.swing.JMenuItem jMenuItemSourceSaveAs;
    private javax.swing.JMenuItem jMenuItemUserLabel;
    private javax.swing.JMenuItem jMenuItemUserLabelOp;
    private javax.swing.JMenuItem jMenuItemViewProject;
    private javax.swing.JMenu jMenuMarkGarbage;
    private javax.swing.JMenu jMenuOption;
    private javax.swing.JMenu jMenuSource;
    private javax.swing.JScrollPane jScrollPaneLeft;
    private javax.swing.JScrollPane jScrollPaneMemory;
    private javax.swing.JScrollPane jScrollPaneRight;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparatorButton1;
    private javax.swing.JToolBar.Separator jSeparatorButton2;
    private javax.swing.JToolBar.Separator jSeparatorButton3;
    private javax.swing.JPopupMenu.Separator jSeparatorHelp1;
    private javax.swing.JPopupMenu.Separator jSeparatorOption;
    private javax.swing.JPopupMenu.Separator jSeparatorProject1;
    private javax.swing.JPopupMenu.Separator jSeparatorProject2;
    private javax.swing.JPopupMenu.Separator jSeparatorProject3;
    private javax.swing.JSplitPane jSplitPaneExternal;
    private javax.swing.JSplitPane jSplitPaneInternal;
    private javax.swing.JTable jTableMemory;
    private javax.swing.JToolBar jToolBar;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea rSyntaxTextAreaDis;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea rSyntaxTextAreaSource;
    // End of variables declaration//GEN-END:variables

  @Override
  public void execute(int type) {
    switch (type) {
      case PROJ_NEW:
        projectNew();
        break;
      case PROJ_CLOSE:  
        projectClose(); 
        break;  
      case PROJ_OPEN:
        projectOpen();
        break;
      case PROJ_SAVE:
        projectSave(); 
        break;        
      case PROJ_SAVEAS:
        projectSaveAs();
        break;        
      case OPTION_CONFIGURE:
        jOptionDialog.setVisible(true);
        break;
      case OPTION_VIEWPRJ:
        projectView();
        break;   
      case OPTION_SIDLD:
        optionSIDLD();  
        break;        
      case OPTION_MPR:
        optionMPR();
        break;
      case SOURCE_DISASS:
        disassembly();
        break;            
      case SOURCE_EXPASDIS:
        exportAs(rSyntaxTextAreaDis.getText());  
        break;        
      case SOURCE_EXPASSOURCE:
        exportAs(rSyntaxTextAreaSource.getText());    
        break;
      case SOURCE_FINDA:
        findAddress();
        break;   
      case SOURCE_FINDD:
        findDialogDis.setVisible(true);
        break;
      case SOURCE_FINDS:
        findDialogSource.setVisible(true);
        break;        
      case APP_EXIT:
        exit();
        break;        
      case MEM_CLEARDCOM:
        clearDasmComment();
        break;
       case MEM_CLEARUCOM:
        clearUserComment();
        break;    
       case MEM_ADDCOMM:
         addComment();
         break;
       case MEM_ADDLABEL:
         addLabel();
         break;      
       case MEM_ADDLABELOP:
         addLabelOp();
         break;                   
       case MEM_MARKCODE:
         markAsCode();  
         break;
       case MEM_MARKDATA:
         markAsData();  
         break;
       case MEM_MARKGARB:
         markAsGarbage();  
         break;        
       case MEM_ADDBLOCK:
         addBlock();
         break;
       case MEM_CLEARDLABEL:
         clearDLabel();  
         break;
       case MEM_LOW:
         memLow();  
         break;
       case MEM_HIGH:
         memHigh();  
         break;
       case MEM_PLUS:
         memPlus();  
         break;
       case MEM_MINUS:
         memMinus();  
         break;         
         
       case HELP_CONTENTS: 
         jHelpFrame.setVisible(true);
         break;
       case HELP_LICENSE:
         jLicenseDialog.setVisible(true);  
         break;
       case HELP_CREDITS:
         jCreditsDialog.setVisible(true);
         break;  
       case HELP_ABOUT:
         jAboutDialog.setVisible(true);
         break;
    }
        
  }
  
  /**
   * Project new user action
   */
  private void projectNew() {
    if (project != null && !project.equals(savedProject)) {
      JOptionPane.showMessageDialog(this, "Project not saved. Close it, then create a new one.", "Information", JOptionPane.WARNING_MESSAGE);
    } else {
        project=new Project();
        savedProject=project.clone();
        projectFile=null;
        jProjectDialog.setUp(project);            
        jProjectDialog.setVisible(true);
        
        if (project.file==null || "".equals(project.file)) {
          project=null;
          savedProject=null;          
        } else {
          dataTableModelMemory.setData(project.memory);
          dataTableModelMemory.fireTableDataChanged();
          execute(SOURCE_DISASS);
        }
      }    
  }
  
  /**
   * Project close user action
   */
  private void projectClose() {
    if (project == null) return;
    
    if (!project.equals(savedProject)) {
      int res=JOptionPane.showConfirmDialog(this, "Project not saved. Close it anywere?", "Information", JOptionPane.YES_NO_OPTION);
      if (res!=JOptionPane.YES_OPTION) return;              
    }       
  
    project=null;
    savedProject=null;
    projectFile=null;
    rSyntaxTextAreaSource.setText("");
    rSyntaxTextAreaDis.setText("");
    dataTableModelMemory.setData(null);
    dataTableModelMemory.fireTableDataChanged();
  }
  
  /**
   * Project open user action
   */
  private void projectOpen() {
   if (savedProject!=null)   System.err.println("###"+savedProject.memory[0].userComment);  

    if (project != null && !project.equals(savedProject)) {
      JOptionPane.showMessageDialog(this, "Project not saved. Close it, then create a new one.", "Information", JOptionPane.WARNING_MESSAGE);
    } else {
        int retVal=projectChooserFile.showOpenDialog(this);
        if (retVal == JFileChooser.APPROVE_OPTION) {
          projectFile=projectChooserFile.getSelectedFile();
          project=new Project();
          if (!FileManager.instance.readProjectFile(projectFile , project)) {
              JOptionPane.showMessageDialog(this, "Error reading project file", "Error", JOptionPane.ERROR_MESSAGE);
          } else {
              JOptionPane.showMessageDialog(this, "File read", "Information", JOptionPane.INFORMATION_MESSAGE);
              execute(SOURCE_DISASS);
            }
            savedProject=project.clone();
            dataTableModelMemory.setData(project.memory);
            dataTableModelMemory.fireTableDataChanged();
          }
       }         
  }
  
  /**
   * Project save user action
   */
  private void projectSave() {
    if (project==null) {
      JOptionPane.showMessageDialog(this, "There is nothing to save", "Information", JOptionPane.INFORMATION_MESSAGE);  
    } else {
        if (projectFile==null) execute (PROJ_SAVEAS);
        else {
          if (!FileManager.instance.writeProjectFile(projectFile , project)) {
            JOptionPane.showMessageDialog(this, "Error writing project file", "Error", JOptionPane.ERROR_MESSAGE);
          } else {
              JOptionPane.showMessageDialog(this, "File saved", "Information", JOptionPane.INFORMATION_MESSAGE);
              savedProject=project.clone();
            }  
        }
      }      
  } 
  
  /**
   * Project save as user action
   */
  private void projectSaveAs() {
    if (project==null) {
      JOptionPane.showMessageDialog(this, "There is nothing to save", "Information", JOptionPane.INFORMATION_MESSAGE);  
    } else {
        int retVal=projectChooserFile.showSaveDialog(this);
        if (retVal == JFileChooser.APPROVE_OPTION) {
          projectFile=projectChooserFile.getSelectedFile();
          if (!FileManager.instance.writeProjectFile(projectFile , project)) {
            JOptionPane.showMessageDialog(this, "Error writing project file", "Error", JOptionPane.ERROR_MESSAGE);
          } else {
              JOptionPane.showMessageDialog(this, "File saved", "Information", JOptionPane.INFORMATION_MESSAGE);
              savedProject=project.clone();
            }
        }
     }      
  }
  
  /**
   * Project view user action
   */
  private void projectView() {
    if (project==null) {
      JOptionPane.showMessageDialog(this, "No project are actually being used.", "Warning", JOptionPane.WARNING_MESSAGE);   
    }  else {
         jProjectDialog.setUp(project);
         jProjectDialog.setVisible(true);
         execute(SOURCE_DISASS);
        }      
  }

  /**
   * Export as of the select text
   * 
   * @param text the text to export
   */
  private void exportAs(String text) {
     File file; 
      
     if (text==null || "".equals(text)) {
          JOptionPane.showMessageDialog(this, "There is nothing to save", "Information", JOptionPane.INFORMATION_MESSAGE);  
        } else {
            int retVal=exportAsChooserFile.showSaveDialog(this);
            if (retVal == JFileChooser.APPROVE_OPTION) {
              file=exportAsChooserFile.getSelectedFile();
              if (!FileManager.instance.writeTxtFile(file , text)) {
                JOptionPane.showMessageDialog(this, "Error writing txt file", "Error", JOptionPane.ERROR_MESSAGE);
              } else {
                  JOptionPane.showMessageDialog(this, "File saved", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            }
          }   
  }
 
  /**
   * Exit from program
   */
  private void exit() {
    if (project != null && !project.equals(savedProject)) {
      int res=JOptionPane.showConfirmDialog(this, "Project not saved. Exit anywere?", "Information", JOptionPane.YES_NO_OPTION);
      if (res==JOptionPane.YES_OPTION) {      
        System.exit(0);
      }
    } else System.exit(0);
  }
  
  /**
   * Clear the dasm label by adding a user null label
   */
  private void clearDasmComment() {
    MemoryDasm mem;   
      
    int rows[]=jTableMemory.getSelectedRows();
        
    for (int i=0; i<rows.length; i++) {
      mem= project.memory[rows[i]];
      if (mem.dasmComment!=null && mem.userComment==null) mem.userComment="";
    }
    
    dataTableModelMemory.fireTableDataChanged();
    jTableMemory.clearSelection();
    for (int i=0; i<rows.length; i++) {
      jTableMemory.addRowSelectionInterval(rows[i], rows[i]);  
    }
  }
  
  /**
   * Clear the dasm label by adding a user null label
   */
  private void clearUserComment() {
    MemoryDasm mem;   
      
    int rows[]=jTableMemory.getSelectedRows();
        
    for (int i=0; i<rows.length; i++) {
      mem= project.memory[rows[i]];
      if (mem.userComment!=null) mem.userComment=null;
    }
    
    dataTableModelMemory.fireTableDataChanged();
    jTableMemory.clearSelection();
    for (int i=0; i<rows.length; i++) {
      jTableMemory.addRowSelectionInterval(rows[i], rows[i]);  
    }
  } 
  
  /**
   * Mark user selection as code
   */
  private void markAsCode() {
    MemoryDasm mem;   
      
    int rows[]=jTableMemory.getSelectedRows();
        
    for (int i=0; i<rows.length; i++) {
      mem= project.memory[rows[i]];
      mem.isCode=true;
      mem.isData=false;
      mem.isGarbage=false;
    }
    
    dataTableModelMemory.fireTableDataChanged();  
    jTableMemory.clearSelection();
    for (int i=0; i<rows.length; i++) {
      jTableMemory.addRowSelectionInterval(rows[i], rows[i]);  
    }
  }
  
  /**
   * Mark user selection as data
   */
  private void markAsData() {
    MemoryDasm mem;   
      
    int rows[]=jTableMemory.getSelectedRows();
        
    for (int i=0; i<rows.length; i++) {
      mem= project.memory[rows[i]];
      mem.isData=true;
      mem.isCode=false;
      mem.isGarbage=false;
      if (option.eraseDComm) mem.dasmComment=null;
      if (option.erasePlus && mem.type=='+') {
        mem.related=-1;
        mem.type=' ';
      }
    }
    
    dataTableModelMemory.fireTableDataChanged();  
    jTableMemory.clearSelection();
    for (int i=0; i<rows.length; i++) {
      jTableMemory.addRowSelectionInterval(rows[i], rows[i]);  
    }
  }

  /**
   * Mark user selection as garbage
   */
  private void markAsGarbage() {
    MemoryDasm mem;   
      
    int rows[]=jTableMemory.getSelectedRows();
        
    for (int i=0; i<rows.length; i++) {
      mem= project.memory[rows[i]];
      mem.isData=false;
      mem.isCode=false;
      mem.isGarbage=true;
      if (option.eraseDComm) mem.dasmComment=null;
      if (option.erasePlus && mem.type=='+') {
        mem.related=-1;
        mem.type=' ';
      }
    }
    
    dataTableModelMemory.fireTableDataChanged();  
    jTableMemory.clearSelection();
    for (int i=0; i<rows.length; i++) {
      jTableMemory.addRowSelectionInterval(rows[i], rows[i]);  
    }
  }  
  
  /**
   * Apply SIDLD flags to memory
   */
  private void optionSIDLD() {
    if (project==null) {
      JOptionPane.showMessageDialog(this, "No project are actually being used.", "Warning", JOptionPane.WARNING_MESSAGE);   
    }  else {  
         int res=JOptionPane.showConfirmDialog(this, "Confirm to apply SIDLD memory flags to code/data in table?", "Information", JOptionPane.YES_NO_OPTION);
         if (res == JFileChooser.APPROVE_OPTION) {
             
           for (int i=0; i<project.memoryFlags.length; i++) {        
               project.memory[i].isData = (project.memoryFlags[i] & 
                       (memoryState.MEM_READ | memoryState.MEM_READ_FIRST |
                       memoryState.MEM_WRITE | memoryState.MEM_WRITE_FIRST |
                       memoryState.MEM_SAMPLE)) !=0;
      
               project.memory[i].isCode = (project.memoryFlags[i] & 
                       (memoryState.MEM_EXECUTE | memoryState.MEM_EXECUTE_FIRST)) !=0;                                             
           }
           JOptionPane.showMessageDialog(this, "Operation done.", "Info", JOptionPane.INFORMATION_MESSAGE);  
         }                  
       }  
  }
  
  /**
   * MPR create option
   */
  private void optionMPR() {
    JOptionPane.showMessageDialog(this,"A MPR is a group of PRG files saved together\n"+
                                       "Selelect all PRG the files with a multi selection (use CTRL+click) in the next dialog\n"+
                                       "Then you had to choose the output MPR file name in the last dialog", "Create a multi PRG archive", JOptionPane.INFORMATION_MESSAGE);
      
    optionMPRLoadChooserFile.showOpenDialog(this);
    File[] files = optionMPRLoadChooserFile.getSelectedFiles();  
    if (files.length==0) {
      JOptionPane.showMessageDialog(this,"Aborting creation due to not files selected", "Warning", JOptionPane.WARNING_MESSAGE);
      return;
    }
    
    MPR mpr=new MPR();
    if (!mpr.setElements(files)) {
      JOptionPane.showMessageDialog(this,"I/O error in reading the files", "Error", JOptionPane.ERROR_MESSAGE);  
      return;
    }
    
    if (optionMPRSaveChooserFile.showSaveDialog(this)==JFileChooser.APPROVE_OPTION) {
       if (!mpr.saveFile(optionMPRSaveChooserFile.getSelectedFile())) {
         JOptionPane.showMessageDialog(this,"Error saving the file", "Error", JOptionPane.ERROR_MESSAGE);  
         return;
       }
    } else {
        JOptionPane.showMessageDialog(this,"No file selected", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
      }
    
   JOptionPane.showMessageDialog(this, mpr.getDescription(), "Information on saved file", JOptionPane.INFORMATION_MESSAGE);
  }
  
  /**
   * Disassembly the memory
   */
  private void disassembly() {
    if (project==null) {
      disassembly.source="";
      disassembly.disassembly="";
    } else {
        disassembly.dissassembly(project.fileType, project.inB, option, project.memory, project.mpr, false);
        disassembly.dissassembly(project.fileType, project.inB, option, project.memory, project.mpr, true);
      }  
    int lineS=0;
    int lineD=0;
    try {
      lineS=rSyntaxTextAreaSource.getLineOfOffset(rSyntaxTextAreaSource.getCaretPosition());        
      lineD=rSyntaxTextAreaDis.getLineOfOffset(rSyntaxTextAreaDis.getCaretPosition());        
    } catch (BadLocationException ex) {
        System.err.println(ex);
    }
    rSyntaxTextAreaSource.setText(disassembly.source);    
    try {
      rSyntaxTextAreaSource.setCaretPosition(rSyntaxTextAreaSource.getDocument()
                        .getDefaultRootElement().getElement(lineS)
                        .getStartOffset());
      rSyntaxTextAreaSource.requestFocusInWindow();
    } catch (Exception ex) {
        System.err.println(ex);
    }
    
    rSyntaxTextAreaDis.setText(disassembly.disassembly);
    try {
      rSyntaxTextAreaDis.setCaretPosition(rSyntaxTextAreaDis.getDocument()
                        .getDefaultRootElement().getElement(lineD)
                        .getStartOffset());
      rSyntaxTextAreaDis.requestFocusInWindow();
    } catch (Exception ex) {
        System.err.println(ex);
    }       
    
    memoryTableCellRenderer.setDisassembly(disassembly);      
  }

  /**
   * Add a user label to the selected memory address
   */
  private void addComment() {
    int row=jTableMemory.getSelectedRow();
    if (row<0) {
      JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
    }
      
    MemoryDasm mem= project.memory[row];
    String comment=JOptionPane.showInputDialog(this, "Insert the comment for the selected memory location", mem.userComment);
    
    System.err.println("###"+project.memory[0].userComment+"###"+savedProject.memory[0].userComment);
    
    if (comment!=null) mem.userComment=comment;  
    
      System.err.println("###"+project.memory[0].userComment+"###"+savedProject.memory[0].userComment);
    
    dataTableModelMemory.fireTableDataChanged(); 
    jTableMemory.setRowSelectionInterval(row, row); 
  }

  /**
   * Add a user label to the selected memory address
   */
  private void addLabel() {
    int row=jTableMemory.getSelectedRow();
    if (row<0) {
      JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
    }
    
    MemoryDasm mem= project.memory[row];
    String initial="";
    if (mem.userLocation!=null) initial=mem.userLocation;
    else if (mem.dasmLocation!=null && !"".equals(mem.dasmLocation)) initial=mem.dasmLocation;
    
    String label=JOptionPane.showInputDialog(this, "Insert the label for the selected memory location", initial);  
    if (label!=null) {
      if ("".equals(label)) {
        JOptionPane.showMessageDialog(this, "User label erased", "Information", JOptionPane.INFORMATION_MESSAGE);   
        mem.userLocation=null;
        return;
      }  
      
      if (label.contains(" ")) {
        JOptionPane.showMessageDialog(this, "Label must not contain spaces", "Error", JOptionPane.ERROR_MESSAGE);   
        return;
      }
      
      if (label.length()>option.maxLabelLength) {
        JOptionPane.showMessageDialog(this, "Label too long. Max allowed="+option.maxLabelLength, "Error", JOptionPane.ERROR_MESSAGE);     
        return;
      }
        
      if (label.length()<6) {
        JOptionPane.showMessageDialog(this, "Label too short. Min allowed=6", "Error", JOptionPane.ERROR_MESSAGE);     
        return;
      }    
            
      // see if the label is already defined
      for (MemoryDasm memory : project.memory) {
        if (label.equals(memory.dasmLocation) || label.equals(memory.userLocation)) {
          JOptionPane.showMessageDialog(this, "This label is already used into the source", "Error", JOptionPane.ERROR_MESSAGE);  
          return;
       }
      }
      
      mem.userLocation=label;
      dataTableModelMemory.fireTableDataChanged(); 
      jTableMemory.setRowSelectionInterval(row, row); 
    }
  }
  
  /**
   * Add a user label to the next word address of selected memory address
   */
  private void addLabelOp() {
    int row=jTableMemory.getSelectedRow();
    if (row<0) {
      JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
    }
    
    // avoid to read over the end
    if (row>=0xFFFE) {
      JOptionPane.showMessageDialog(this, "Address locate over $FFFF boundary", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;        
    }
    
    // avoid to use not defined bytes
    if (!project.memory[row+1].isInside ||!project.memory[row+2].isInside) {
      JOptionPane.showMessageDialog(this, "Address is incomplete. Skip action", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;         
    }
    
    // get next address
    MemoryDasm mem= project.memory[(project.memory[row+2].copy & 0xFF)*256+(project.memory[row+1].copy & 0xFF)];
    
    String initial="";
    if (mem.userLocation!=null) initial=mem.userLocation;
    else if (mem.dasmLocation!=null && !"".equals(mem.dasmLocation)) initial=mem.dasmLocation;
    
    String label=JOptionPane.showInputDialog(this, "Insert the label for the address of operation", initial);  
    if (label!=null) {
      if ("".equals(label)) {
        JOptionPane.showMessageDialog(this, "User label erased", "Information", JOptionPane.INFORMATION_MESSAGE);   
        mem.userLocation=null;
        return;
      }  
      
      if (label.contains(" ")) {
        JOptionPane.showMessageDialog(this, "Label must not contain spaces", "Error", JOptionPane.ERROR_MESSAGE);   
        return;
      }
      
      if (label.length()>option.maxLabelLength) {
        JOptionPane.showMessageDialog(this, "Label too long. Max allowed="+option.maxLabelLength, "Error", JOptionPane.ERROR_MESSAGE);     
        return;
      }
        
      if (label.length()<6) {
        JOptionPane.showMessageDialog(this, "Label too short. Min allowed=6", "Error", JOptionPane.ERROR_MESSAGE);     
        return;
      }    
            
      // see if the label is already defined
      for (MemoryDasm memory : project.memory) {
        if (label.equals(memory.dasmLocation) || label.equals(memory.userLocation)) {
          JOptionPane.showMessageDialog(this, "This label is already used into the source", "Error", JOptionPane.ERROR_MESSAGE);  
          return;
       }
      }
      
      mem.userLocation=label;
      dataTableModelMemory.fireTableDataChanged(); 
      jTableMemory.setRowSelectionInterval(row, row); 
    }
  }

  /**
   * Add a block for comment
   */
  private void addBlock() {
    int row=jTableMemory.getSelectedRow();
    if (row<0) {
      JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
    }
      
    MemoryDasm mem= project.memory[row];
    JTextArea area=new JTextArea(20,20);
    area.setText(mem.userBlockComment);
    area.setFont(new Font("monospaced", Font.PLAIN, 12));

    JScrollPane scrollPane = new JScrollPane(area);
    
    if (JOptionPane.showConfirmDialog(null, scrollPane, "Add a multi-lines block comment", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
      mem.userBlockComment=area.getText();
      if ("".equals(mem.userBlockComment)) mem.userBlockComment=null;
      dataTableModelMemory.fireTableDataChanged();  
      jTableMemory.setRowSelectionInterval(row, row); 
    }              
  }

  /**
   * Clear dasm label (can be regenerated next time)
   */
  private void clearDLabel() {
    MemoryDasm mem;   
      
    int rows[]=jTableMemory.getSelectedRows();
        
    for (int i=0; i<rows.length; i++) {
      mem= project.memory[rows[i]];
      if (mem.dasmLocation!=null) mem.dasmLocation=null;
    }
    
    dataTableModelMemory.fireTableDataChanged();    
    jTableMemory.clearSelection();
    for (int i=0; i<rows.length; i++) {
      jTableMemory.addRowSelectionInterval(rows[i], rows[i]);  
    }
  }  

  /**
   * Assign a reference to memory as #<
   */
  private void memLow() {
    int row=jTableMemory.getSelectedRow();
    if (row<0) {
      JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
    }
    
    Vector cols=new Vector();
    cols.add("ADDR");
    cols.add("DASM");
    cols.add("USER");
        
    Vector rows=new Vector();
    Vector data;
    
    int value=project.memory[row].copy & 0xFF;
    
    for (MemoryDasm memory : project.memory) {    
      if ((memory.address & 0xFF)==value) {
        if (!memory.isInside && memory.userLocation==null) continue;
          
        data=new Vector();
          
        data.add(ShortToExe(memory.address));
        data.add(memory.dasmLocation);
        data.add(memory.userLocation);
          
        rows.add(data);
      }
    }

    JTable table = new JTable(rows, cols);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    if (JOptionPane.showConfirmDialog(null, new JScrollPane(table), 
            "Select the address to use as #<", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
        
      int rowS=table.getSelectedRow();
      if (rowS<0) {
        if (project.memory[row].type=='>' || project.memory[row].type=='<') {
          if (JOptionPane.showConfirmDialog(this, "Did you want to delete the current address association?", "No selection were done, so:", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            project.memory[row].type=' ';
            project.memory[row].related=-1;
          }
        } else JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
        return;
      } else {         
          project.memory[row].related=Integer.parseInt((String)table.getValueAt(rowS, 0),16);          
          project.memory[row].type='<';
        }
       
      dataTableModelMemory.fireTableDataChanged();      
      jTableMemory.setRowSelectionInterval(row, row); 
    }       
  }

  /**
   * Assign a reference to memory as #>
   */
  private void memHigh() {
    int row=jTableMemory.getSelectedRow();
    if (row<0) {
      JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
    }
    
    Vector cols=new Vector();
    cols.add("ADDR");
    cols.add("DASM");
    cols.add("USER");
        
    Vector rows=new Vector();
    Vector data;
    
    int value=project.memory[row].copy & 0xFF;
    
    for (MemoryDasm memory : project.memory) {      
      if (((memory.address>>8) & 0xFF)==value) {
        if (!memory.isInside && memory.userLocation==null) continue;  
          
        data=new Vector();
          
        data.add(ShortToExe(memory.address));
        data.add(memory.dasmLocation);
        data.add(memory.userLocation);
          
        rows.add(data);
      }
    }
    
    JTable table = new JTable(rows, cols);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    if (JOptionPane.showConfirmDialog(null, new JScrollPane(table), 
           "Select the address to use as #>", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
        
       int rowS=table.getSelectedRow();
       if (rowS<0) {
         if (project.memory[row].type=='>' || project.memory[row].type=='<') {
            if (JOptionPane.showConfirmDialog(this, "Did you want to delete the current address association?", "No selection were done, so:", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
              project.memory[row].type=' ';
              project.memory[row].related=-1;
            }
         } else JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
         return;
       } else {         
           project.memory[row].related=Integer.parseInt((String)table.getValueAt(rowS, 0),16);          
           project.memory[row].type='>';
         }
       
       dataTableModelMemory.fireTableDataChanged();
       jTableMemory.setRowSelectionInterval(row, row);
    }   
  }
 
  /**
   * Find a memory address
   */
  private void findAddress() {
    String addr=JOptionPane.showInputDialog(this, "Search and go to a given HEX memory address");
    if (addr==null) return;
    
    try {
      int pos=Integer.parseInt(addr,16);
      if (pos<0 || pos>0xFFFF) return;
    
      jTableMemory.getSelectionModel().setSelectionInterval(pos, pos);
      jTableMemory.scrollRectToVisible(new Rectangle(jTableMemory.getCellRect(pos, 0, true)));
    } catch (Exception e) {
      }
  }

  /**
   * Mark the memroy as address +
   */
  private void memPlus() {
    int row=jTableMemory.getSelectedRow();
    if (row<0) {
      JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
    }
    
    Vector cols=new Vector();
    cols.add("ADDR");
    cols.add("DASM");
    cols.add("USER");
        
    Vector rows=new Vector();
    Vector data;
    
    int value=project.memory[row].copy & 0xFF;
    
    MemoryDasm mem=project.memory[row];
    MemoryDasm memory;
    
    int addr;
    for (int i=1; i<32; i++) {
      addr=mem.address-i;
      if (i<0) continue;
      
      memory=project.memory[addr];
      
      data=new Vector();
          
      data.add(ShortToExe(memory.address));
      data.add(memory.dasmLocation);
      data.add(memory.userLocation);
          
      rows.add(data);
    }
    
    JTable table = new JTable(rows, cols);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  
    
    if (JOptionPane.showConfirmDialog(null, new JScrollPane(table), 
           "Select the address to use as + in table", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
        
      int rowS=table.getSelectedRow();
      if (rowS<0) {
        if (project.memory[row].type=='+') {
          if (JOptionPane.showConfirmDialog(this, "Did you want to delete the current address association?", "No selection were done, so:", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            project.memory[row].type=' ';
            project.memory[row].related=-1;
          }
        } else JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
        return;
      } else {         
           mem.related=Integer.parseInt((String)table.getValueAt(rowS, 0),16);          
           mem.type='+';
        }
       
      dataTableModelMemory.fireTableDataChanged();
      jTableMemory.setRowSelectionInterval(row, row);
    }
  }
  
/**
   * Mark the memroy as address -
   */
  private void memMinus() {
    int row=jTableMemory.getSelectedRow();
    if (row<0) {
      JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
    }
    
    Vector cols=new Vector();
    cols.add("ADDR");
    cols.add("DASM");
    cols.add("USER");
        
    Vector rows=new Vector();
    Vector data;
    
    int value=project.memory[row].copy & 0xFF;
    
    MemoryDasm mem=project.memory[row];
    MemoryDasm memory;
    
    int addr;
    for (int i=1; i<32; i++) {
      addr=mem.address+i;
      if (i<0) continue;
      
      memory=project.memory[addr];
      
      data=new Vector();
          
      data.add(ShortToExe(memory.address));
      data.add(memory.dasmLocation);
      data.add(memory.userLocation);
          
      rows.add(data);
    }
    
    JTable table = new JTable(rows, cols);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  
    
    if (JOptionPane.showConfirmDialog(null, new JScrollPane(table), 
           "Select the address to use as - in table", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
        
      int rowS=table.getSelectedRow();
      if (rowS<0) {
        if (project.memory[row].type=='-') {
          if (JOptionPane.showConfirmDialog(this, "Did you want to delete the current address association?", "No selection were done, so:", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            project.memory[row].type=' ';
            project.memory[row].related=-1;
          }
        } else JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
        return;
      } else {         
           mem.related=Integer.parseInt((String)table.getValueAt(rowS, 0),16);          
           mem.type='-';
        }
       
      dataTableModelMemory.fireTableDataChanged();
      jTableMemory.setRowSelectionInterval(row, row);
    }
  }  
    
  /**
   * Convert a unsigned short (containing in a int) to Exe upper case 4 chars
   *
   * @param value the short value to convert
   * @return the exe string rapresentation of byte
   */
  protected String ShortToExe(int value) {
    int tmp=value;

    if (value<0) return "????";
    
    String ret=Integer.toHexString(tmp);
    int len=ret.length();
    switch (len) {
      case 1:
        ret="000"+ret;
        break;
     case 2:
        ret="00"+ret;
        break;
     case 3:
        ret="0"+ret;
        break;
    }
    return ret.toUpperCase(Locale.ENGLISH);
  }      
}
