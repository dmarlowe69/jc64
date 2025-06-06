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

import java.awt.AWTException;
import java.awt.Font;
import java.awt.Image;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import static java.awt.event.InputEvent.ALT_DOWN_MASK;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.prefs.Preferences;
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
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.JTableHeader;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;
import sw_emulator.software.Assembler.Name;
import sw_emulator.software.BasicDetokenize.BasicType;
import sw_emulator.software.asm.Compiler;
import sw_emulator.software.Disassembly;
import sw_emulator.software.MemoryDasm;
import static sw_emulator.software.MemoryDasm.TYPE_EMPTY;
import static sw_emulator.software.MemoryDasm.TYPE_MAJOR;
import static sw_emulator.software.MemoryDasm.TYPE_MINOR;
import static sw_emulator.software.MemoryDasm.TYPE_MINUS;
import static sw_emulator.software.MemoryDasm.TYPE_MINUS_MAJOR;
import static sw_emulator.software.MemoryDasm.TYPE_MINUS_MINOR;
import static sw_emulator.software.MemoryDasm.TYPE_PLUS;
import static sw_emulator.software.MemoryDasm.TYPE_PLUS_MAJOR;
import static sw_emulator.software.MemoryDasm.TYPE_PLUS_MINOR;
import sw_emulator.software.cpu.M6510Dasm;
import sw_emulator.software.cpu.Z80Dasm;
import sw_emulator.software.memory.memoryState;
import sw_emulator.swing.main.Carets;
import sw_emulator.swing.main.Constant;
import sw_emulator.swing.main.DataType;
import sw_emulator.swing.main.FileManager;
import sw_emulator.swing.main.FileType;
import sw_emulator.swing.main.KeyProject;
import sw_emulator.swing.main.MPR;
import sw_emulator.swing.main.Option;
import sw_emulator.swing.main.Patch;
import sw_emulator.swing.main.Project;
import sw_emulator.swing.main.RecentItems;
import sw_emulator.swing.main.Serial;
import sw_emulator.swing.main.UndoManager;
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
  
  /** Icon images */
  List<Image> iconImages = new ArrayList<>();
  
  /** Project to use */
  Project project;
  
  /** Project file name */
  File projectFile;
  
  /** Last saved project values */
  Project savedProject;
  
  /** Recent items */
  RecentItems recentFile=new RecentItems();
  
  /** Undo manager */
  UndoManager undo=new UndoManager();
  
  /** Data table for memory */
  DataTableModelMemory dataTableModelMemory=new DataTableModelMemory(option);
  
  /** Disassembly engine  */
  Disassembly disassembly=new Disassembly();
  
  /** Option dialog */
  JOptionDialog jOptionDialog;
  
  /** Project dialog */
  JProjectDialog jProjectDialog=new JProjectDialog(this, true);
  
  /** Project chooser file dialog*/
  JFileChooser projectChooserFile=new JFileChooser();
  
  /** Project merge file dialog*/
  JFileChooser projectMergeFile=new JFileChooser();  
  
  /** Export as file chooser */
  JFileChooser exportAsChooserFile=new JFileChooser();
  
  /** Load MPR as file chooser */
  JFileChooser optionMPRLoadChooserFile=new JFileChooser();
  
  /** Save MPR as file chooser */
  JFileChooser optionMPRSaveChooserFile=new JFileChooser();
  
  /** Import labels from dasm */
  JFileChooser importLabelsChooserFile=new JFileChooser();
  
  /** Memory cell renderer for table */
  MemoryTableCellRenderer memoryTableCellRenderer=new MemoryTableCellRenderer();
  
  /** License dialog */
  JLicenseDialog jLicenseDialog=new JLicenseDialog(this, true);
  
  /** Credit dialog */
  JCreditsDialog jCreditsDialog=new JCreditsDialog(this, true);
  
  /** About dialog */
  JAboutDialog jAboutDialog=new JAboutDialog(this, true);
  
  /** Labels dialog */
  JLabelsDialog jLabelsDialog=new JLabelsDialog(this, true, option);
  
  /** Auto low/hi dialog */
  JAutoLoHiDialog jAutoLoHiDialog=new JAutoLoHiDialog(this, true);
  
  /** JWizard dialog */
  JWizardDialog jWizardDialog=new JWizardDialog(this, true, option);
  
  /** Help dialog */
  JHelpFrame jHelpFrame=new JHelpFrame();
  
  /** Freeze source frame */
  JFreezeFrame jFreezeFrame=new JFreezeFrame();
  
  /** Hex dialog */
  JHexDialog jHexDialog=new JHexDialog(this, true);
  
  /** Block dialog */
  JBlockDialog jBlockDialog=new JBlockDialog(this, true);  
  
  /** Player dialog */
  JPlayerDialog jPlayerDialog=new JPlayerDialog(this, false);
  
  /** Find dialog for source */
  FindDialog findDialogSource;
  
  /** Find dialog for disassembly */
  FindDialog findDialogDis;
  
  /** Compiler */
  Compiler compiler=new Compiler();  
  
  /** Dialog for Easter eggs */
  FadeDialog fadeDialog;//=new FadeDialog();
  
  /** Stack for call of jumps */
  Stack<Integer> callStack = new Stack();
  
  /** Last directory for saving project  */
  public final static String LAST_DIR_PROJECT = "last.dir.project";  
     
  /** Preference system file */
  private Preferences m_prefNode=Preferences.userRoot().node(this.getClass().getName());
  
  /** Last hex string searched*/
  private String lastSearch="";
  
  
    /**
   * Creates new form JFrameDisassembler
   */
  public JDisassemblerFrame() {        
    try {
      iconImages.add(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/images/icon128.png")).getImage());
      iconImages.add(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/images/icon64.png")).getImage());
      iconImages.add(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/images/icon32.png")).getImage());
    } catch (Exception e) {
      System.err.println(e);
    }

    initComponents();

    jOptionDialog=new JOptionDialog(this, true, dataTableModelMemory, this);

    Shared.framesList.add(this);
    Shared.framesList.add(projectChooserFile);
    Shared.framesList.add(projectMergeFile);
    Shared.framesList.add(exportAsChooserFile);
    Shared.framesList.add(optionMPRLoadChooserFile);
    Shared.framesList.add(optionMPRSaveChooserFile);
    Shared.framesList.add(findDialogDis);
    Shared.framesList.add(findDialogSource);
    Shared.framesList.add(importLabelsChooserFile);
 
    Shared.syntaxList.add(rSyntaxTextAreaDis);
    Shared.syntaxList.add(rSyntaxTextAreaSource);
    Shared.syntaxList.add(rSyntaxTextAreaDisMin);
    Shared.syntaxList.add(rSyntaxTextAreaSourceMin);
    
    findDialogDis.setSearchString(" ");
    findDialogSource.setSearchString(" ");

    FileManager.instance.readOptionFile(FileManager.OPTION_FILE, option);
    //  if (option.getLafName().equals("SYNTH")) Option.useLookAndFeel(option.getFlatLaf());
    //  else Option.useLookAndFeel(option.getLafName(), option.getMethalTheme());

    jOptionDialog.useOption(option);
    
    option.useSyntaxTheme(option.syntaxTheme);

    projectChooserFile.addChoosableFileFilter(new FileNameExtensionFilter("JC64Dis (*.dis)", "dis"));
    projectChooserFile.setAcceptAllFileFilterUsed(false);
    projectChooserFile.setCurrentDirectory(new File(m_prefNode.get(LAST_DIR_PROJECT, "")));
    projectChooserFile.setFileView(new ProjectFileView(option));

    projectMergeFile.addChoosableFileFilter(new FileNameExtensionFilter("JC64Dis (*.dis)", "dis"));
    exportAsChooserFile.addChoosableFileFilter(new FileNameExtensionFilter("Source (*.txt)", "txt"));
    optionMPRLoadChooserFile.addChoosableFileFilter(new FileNameExtensionFilter("PRG C64 program (prg, bin)", "prg", "bin"));
    optionMPRLoadChooserFile.setMultiSelectionEnabled(true);
    optionMPRLoadChooserFile.setDialogTitle("Select all PRG to include into the MPR");
    optionMPRSaveChooserFile.addChoosableFileFilter(new FileNameExtensionFilter("Multi PRG C64 program (mpr)", "mpr"));
    optionMPRSaveChooserFile.setDialogTitle("Select the MPR file to save");
    importLabelsChooserFile.setDialogTitle("Select a memory label dump file from DASM");
    compiler.setOption(option);

    jTableMemory.addMouseListener(new java.awt.event.MouseAdapter() {
      MouseEvent last;

      private Timer timer = new Timer(300, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // timer has gone off, so treat as a single click
          singleClick();
          timer.stop();
        }
      });

      @Override
      public void mouseClicked(MouseEvent e) {
        last = e;
        // Check if timer is running 
        // to know if there was an earlier click
        if (timer.isRunning()) {
          // There was an earlier click so we'll treat it as a double click
          timer.stop();
          doubleClick();
        } else {
          // (Re)start the timer and wait for 2nd click      	
          timer.restart();
        }
      }

      /**
       * Single click on table
       */
      protected void singleClick() {
        int row = jTableMemory.rowAtPoint(last.getPoint());
        int col = jTableMemory.columnAtPoint(last.getPoint());

        switch (DataTableModelMemory.columns[jTableMemory.convertColumnIndexToModel(col)]) {
          case UC:    // user comment
            if (option.clickUcEdit) {
              addComment(row);
              if (option.forceCompilation) {
                disassembly(true);
              }
            }
            break;
          case UL:     // user label
            if (option.clickUlEdit) {
              addLabel(row);
              if (option.forceCompilation) {
                disassembly(true);
              }
            }
            break;
          case UB:     // user global comment
            if (option.clickUbEdit) {
              addBlock(row);
              if (option.forceCompilation) {
                disassembly(true);
              }
            }
            break;
          case DC:     // automatic comment
            if (option.clickDcErase) {
              MemoryDasm mem = project.memory[row];
              if (mem.dasmComment != null && mem.userComment == null) {
                mem.userComment = "";
              }
              dataTableModelMemory.fireTableDataChanged();
              if (option.forceCompilation) {
                disassembly(true);
              }
            }
            break;
          case DL:     // automatic label
            if (option.clickDlErase) {
              MemoryDasm mem = project.memory[row];
              if (mem.dasmLocation != null) {
                mem.dasmLocation = null;
              }
              dataTableModelMemory.fireTableDataChanged();
              if (option.forceCompilation) {
                disassembly(true);
              }
            }
            break;
          case VL:    // add patch
            if (option.clickVlPatch) {
              MemoryDasm mem = project.memory[row];
              String value = JOptionPane.showInputDialog(null, "Insert the new value (in hex) for this location");
              if (value != null) {
                Patch patch = new Patch();
                patch.value = Integer.parseInt(value, 16);

                if (!patch.isValidRange()) {
                  JOptionPane.showMessageDialog(null, "Invalid address or value", "Error", JOptionPane.ERROR_MESSAGE);
                  break;
                }

                patch.address = mem.address;

                int size = 0;
                if (project.patches != null) {
                  size = project.patches.length;
                }

                // copy the value in the list
                Patch[] patches2 = new Patch[size + 1];
                if (size > 0) {
                  System.arraycopy(project.patches, 0, patches2, 0, project.patches.length);
                }
                patches2[size] = patch;

                project.patches = patches2;

                dataTableModelMemory.fireTableDataChanged();
                if (option.forceCompilation) {
                  disassembly(true);
                }
              }

            }
            break;
        }
      }

      /**
       * Double click on table
       */
      protected void doubleClick() {
        int actual;

        // get the address in hex format
        int addr = jTableMemory.getSelectedRow();
        int pos = 0;

        // try with carets
        if (addr >= 0) {
          pos = disassembly.caretsPreview.getPosition(project.memory[addr]);

          if (pos != -1) {
            rSyntaxTextAreaDis.setCaretPosition(pos);
            rSyntaxTextAreaDis.requestFocusInWindow();
            return;
          } else {
            pos = 0;
          }
        }

        // scan all lines for the memory location
        try {
          String preview = rSyntaxTextAreaDis.getText();
          String lines[] = preview.split("\\r?\\n");
          for (String line : lines) {
            actual = searchAddress(line.substring(0, Math.min(line.length(), option.maxLabelLength)));
            if (actual == addr) {
              // set preview in the find position  
              rSyntaxTextAreaDis.setCaretPosition(pos);
              rSyntaxTextAreaDis.requestFocusInWindow();
              break;
            } else {
              pos += line.length() + 1;
            }
          }
        } catch (Exception e) {
          System.err.println();
        }
      }
    });
   
    jScrollPaneLeftMin.setVisible(option.showMiniature);
    jScrollPaneRightMin.setVisible(option.showMiniature);
    pack();
    
    fadeDialog=new FadeDialog(option, this);
  }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPopupMenuData = new javax.swing.JPopupMenu();
    jMenuBasic = new javax.swing.JMenu();
    jMenuItemBasicNone = new javax.swing.JMenuItem();
    jSeparatorStandard = new javax.swing.JPopupMenu.Separator();
    jMenuItemBasicV2_0 = new javax.swing.JMenuItem();
    jMenuItemBasicV3_5 = new javax.swing.JMenuItem();
    jMenuItemBasicV4_0 = new javax.swing.JMenuItem();
    jMenuItemBasicV7_0 = new javax.swing.JMenuItem();
    jSeparatorV2 = new javax.swing.JPopupMenu.Separator();
    jMenuItemSimon = new javax.swing.JMenuItem();
    jMenuItemAndreFachat = new javax.swing.JMenuItem();
    jMenuItemSpeech = new javax.swing.JMenuItem();
    jMenuItemFinalCart3 = new javax.swing.JMenuItem();
    jMenuItemUltrabasic = new javax.swing.JMenuItem();
    jMenuItemGraphics = new javax.swing.JMenuItem();
    jMenuItemWS = new javax.swing.JMenuItem();
    jMenuItemPegasus = new javax.swing.JMenuItem();
    jMenuItemXbasic = new javax.swing.JMenuItem();
    jMenuItemDrago = new javax.swing.JMenuItem();
    jMenuItemReu = new javax.swing.JMenuItem();
    jMenuItemLightninig = new javax.swing.JMenuItem();
    jMenuItemMagic = new javax.swing.JMenuItem();
    jMenuItemBlark = new javax.swing.JMenuItem();
    jMenuItemWSFinal = new javax.swing.JMenuItem();
    jMenuItemGame = new javax.swing.JMenuItem();
    jMenuItemBasex = new javax.swing.JMenuItem();
    jMenuItemSuper = new javax.swing.JMenuItem();
    jMenuItemExpanded = new javax.swing.JMenuItem();
    jMenuItemSuperExpanderChip = new javax.swing.JMenuItem();
    jMenuItemWarsaw = new javax.swing.JMenuItem();
    jMenuItemDbs = new javax.swing.JMenuItem();
    jMenuItemKipper = new javax.swing.JMenuItem();
    jMenuItemBails = new javax.swing.JMenuItem();
    jMenuItemEve = new javax.swing.JMenuItem();
    jMenuItemTool = new javax.swing.JMenuItem();
    jSeparatorV128 = new javax.swing.JPopupMenu.Separator();
    jMenuItemV8 = new javax.swing.JMenuItem();
    jSeparatorV20 = new javax.swing.JPopupMenu.Separator();
    jMenuItemSuperExpander = new javax.swing.JMenuItem();
    jMenuItemTurtle = new javax.swing.JMenuItem();
    jMenuItemEasy = new javax.swing.JMenuItem();
    jMenuItemV4 = new javax.swing.JMenuItem();
    jMenuItemV5 = new javax.swing.JMenuItem();
    jMenuItemExpandedV20 = new javax.swing.JMenuItem();
    jMenuItemHandy = new javax.swing.JMenuItem();
    jMenuItemByteHex = new javax.swing.JMenuItem();
    jMenuItemByteDec = new javax.swing.JMenuItem();
    jMenuItemByteBin = new javax.swing.JMenuItem();
    jMenuItemByteChar = new javax.swing.JMenuItem();
    jSeparatorByte = new javax.swing.JPopupMenu.Separator();
    jMenuItemWord = new javax.swing.JMenuItem();
    jMenuItemWordSwapped = new javax.swing.JMenuItem();
    jSeparatorWord = new javax.swing.JPopupMenu.Separator();
    jMenuItemTribyte = new javax.swing.JMenuItem();
    jMenuItemLong = new javax.swing.JMenuItem();
    jSeparatorPopUpMenu0 = new javax.swing.JPopupMenu.Separator();
    jMenuItemAddress = new javax.swing.JMenuItem();
    jMenuItemStackWord = new javax.swing.JMenuItem();
    jSeparatorPopUpMenu1 = new javax.swing.JPopupMenu.Separator();
    jMenuItemSpriteMono = new javax.swing.JMenuItem();
    jMenuItemSpriteMulti = new javax.swing.JMenuItem();
    jSeparatorSprite = new javax.swing.JPopupMenu.Separator();
    jMenuItemText = new javax.swing.JMenuItem();
    jMenuItemNumText = new javax.swing.JMenuItem();
    jMenuItemTextZero = new javax.swing.JMenuItem();
    jMenuItemtextHighOne = new javax.swing.JMenuItem();
    jMenuItemtextShifted = new javax.swing.JMenuItem();
    jMenuItemTextScreen = new javax.swing.JMenuItem();
    jMenuItemTextPetascii = new javax.swing.JMenuItem();
    jPopupMenuConstant = new javax.swing.JPopupMenu();
    jMenuItemConstant0 = new javax.swing.JMenuItem();
    jMenuItemConstant1 = new javax.swing.JMenuItem();
    jMenuItemConstant2 = new javax.swing.JMenuItem();
    jMenuItemConstant3 = new javax.swing.JMenuItem();
    jMenuItemConstant4 = new javax.swing.JMenuItem();
    jMenuItemConstant5 = new javax.swing.JMenuItem();
    jMenuItemConstant6 = new javax.swing.JMenuItem();
    jMenuItemConstant7 = new javax.swing.JMenuItem();
    jMenuItemConstant8 = new javax.swing.JMenuItem();
    jMenuItemConstant9 = new javax.swing.JMenuItem();
    jSeparatorConstant = new javax.swing.JPopupMenu.Separator();
    jMenuItemConstantClear_ = new javax.swing.JMenuItem();
    jSeparatorConstant2 = new javax.swing.JPopupMenu.Separator();
    jMenuItemConstant10 = new javax.swing.JMenuItem();
    jMenuItemConstant11 = new javax.swing.JMenuItem();
    jMenuItemConstant12 = new javax.swing.JMenuItem();
    jMenuItemConstant13 = new javax.swing.JMenuItem();
    jMenuItemConstant14 = new javax.swing.JMenuItem();
    jMenuItemConstant15 = new javax.swing.JMenuItem();
    jMenuItemConstant16 = new javax.swing.JMenuItem();
    jMenuItemConstant17 = new javax.swing.JMenuItem();
    jMenuItemConstant18 = new javax.swing.JMenuItem();
    jMenuItemConstant19 = new javax.swing.JMenuItem();
    jPopupMenuSaveAs = new javax.swing.JPopupMenu();
    jMenuItemSaveAsDasm = new javax.swing.JMenuItem();
    jMenuItemSaveAsTmpx = new javax.swing.JMenuItem();
    jMenuItemSaveAsCa65 = new javax.swing.JMenuItem();
    jMenuItemSaveAsAcme = new javax.swing.JMenuItem();
    jMenuItemSaveAsKickAssembler = new javax.swing.JMenuItem();
    jMenuItemSaveAsTass64 = new javax.swing.JMenuItem();
    jMenuItemSaveAsGlass = new javax.swing.JMenuItem();
    jMenuItemSaveAsAS = new javax.swing.JMenuItem();
    jPopupMenuMemory = new javax.swing.JPopupMenu();
    jMenuItemCopy = new javax.swing.JMenuItem();
    jMenuItemPaste = new javax.swing.JMenuItem();
    jSeparatorMem1 = new javax.swing.JPopupMenu.Separator();
    jMenuItemFindAddr = new javax.swing.JMenuItem();
    jMenuItemFindSeq = new javax.swing.JMenuItem();
    jSeparatorMem2 = new javax.swing.JPopupMenu.Separator();
    jMenuItemCode = new javax.swing.JMenuItem();
    jMenuItemData = new javax.swing.JMenuItem();
    jMenuDataSel = new javax.swing.JMenu();
    jMenuItemByteHex2 = new javax.swing.JMenuItem();
    jMenuItemByteDec2 = new javax.swing.JMenuItem();
    jMenuItemByteBin2 = new javax.swing.JMenuItem();
    jMenuItemByteChar2 = new javax.swing.JMenuItem();
    jSeparatorByte2 = new javax.swing.JPopupMenu.Separator();
    jMenuItemWord2 = new javax.swing.JMenuItem();
    jMenuItemWordSwapped2 = new javax.swing.JMenuItem();
    jSeparatorWord2 = new javax.swing.JPopupMenu.Separator();
    jMenuItemTribyte2 = new javax.swing.JMenuItem();
    jMenuItemLong2 = new javax.swing.JMenuItem();
    jSeparatorPopUpMenu2 = new javax.swing.JPopupMenu.Separator();
    jMenuItemAddress2 = new javax.swing.JMenuItem();
    jMenuItemStackWord2 = new javax.swing.JMenuItem();
    jSeparatorPopUpMenu3 = new javax.swing.JPopupMenu.Separator();
    jMenuItemSpriteMono2 = new javax.swing.JMenuItem();
    jMenuItemSpriteMulti2 = new javax.swing.JMenuItem();
    jSeparatorSprite2 = new javax.swing.JPopupMenu.Separator();
    jMenuItemText2 = new javax.swing.JMenuItem();
    jMenuItemNumText2 = new javax.swing.JMenuItem();
    jMenuItemTextZero2 = new javax.swing.JMenuItem();
    jMenuItemtextHighOne2 = new javax.swing.JMenuItem();
    jMenuItemtextShifted2 = new javax.swing.JMenuItem();
    jMenuItemTextScreen2 = new javax.swing.JMenuItem();
    jMenuItemTextPetascii2 = new javax.swing.JMenuItem();
    jMenuItemGarbage = new javax.swing.JMenuItem();
    jMenuItemUnmark = new javax.swing.JMenuItem();
    jSeparatorMem3 = new javax.swing.JPopupMenu.Separator();
    jMenuItemHex = new javax.swing.JMenuItem();
    jMenuItemBlockLabel = new javax.swing.JMenuItem();
    jPanelToolBar = new javax.swing.JPanel();
    jToolBarFile = new javax.swing.JToolBar();
    jButtonNewProject = new javax.swing.JButton();
    jButtonOpenProject = new javax.swing.JButton();
    jButtonClose = new javax.swing.JButton();
    jButtonSaveProject = new javax.swing.JButton();
    jButtonSaveProjectAs = new javax.swing.JButton();
    jButtonMPR = new javax.swing.JButton();
    jButtonMerge = new javax.swing.JButton();
    jButtonExit = new javax.swing.JButton();
    jToolBarMemory = new javax.swing.JToolBar();
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
    jButtonMarkConstant = new javax.swing.JButton();
    jButtonMarkPlus = new javax.swing.JButton();
    jButtonMarkMinus = new javax.swing.JButton();
    jButtonMarkLow = new javax.swing.JButton();
    jButtonMarkLowHigh = new javax.swing.JButton();
    jButtonMarkBoth = new javax.swing.JButton();
    jButtonMarkHighLow = new javax.swing.JButton();
    jButtonMarkMax = new javax.swing.JButton();
    jButtonWizard = new javax.swing.JButton();
    jButtonJumpBack = new javax.swing.JButton();
    jButtonJumpFollow = new javax.swing.JButton();
    jToolBarOption = new javax.swing.JToolBar();
    jButtonConfigure = new javax.swing.JButton();
    jButtonSIDLD = new javax.swing.JButton();
    jButtonViewProject = new javax.swing.JButton();
    jButtonViewLabels = new javax.swing.JButton();
    jToolBarSource = new javax.swing.JToolBar();
    jButtonFindMem = new javax.swing.JButton();
    jButtonDisassemble = new javax.swing.JButton();
    jButtonAssemble = new javax.swing.JButton();
    jButtonFindDis = new javax.swing.JButton();
    jButtonExportAsDiss = new javax.swing.JButton();
    jButtonFindSource = new javax.swing.JButton();
    jButtonExportAsSource = new javax.swing.JButton();
    jButtonExportAsSource1 = new javax.swing.JButton();
    jToolBarPerformance = new javax.swing.JToolBar();
    heapView = new sw_emulator.swing.HeapView();
    jToolBarPerc = new javax.swing.JToolBar();
    jPanelPerc = new sw_emulator.swing.JPanelPerc();
    jSplitPaneExternal = new javax.swing.JSplitPane();
    jSplitPaneInternal = new javax.swing.JSplitPane();
    jPanelLeft = new javax.swing.JPanel();
    jScrollPaneLeft = new javax.swing.JScrollPane();
    rSyntaxTextAreaDis = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
    jScrollPaneLeftMin = new javax.swing.JScrollPane();
    rSyntaxTextAreaDisMin = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
    jPanelRight = new javax.swing.JPanel();
    jScrollPaneRight = new javax.swing.JScrollPane();
    rSyntaxTextAreaSource = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
    jScrollPaneRightMin = new javax.swing.JScrollPane();
    rSyntaxTextAreaSourceMin = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
    jScrollPaneMemory = new javax.swing.JScrollPane();
    jTableMemory = new javax.swing.JTable() {
      String[] hh={"Memory address location in Hex",
        "Disassembler automatic comment",
        "User manual comment",
        "Disassembler automatic label",
        "User manual label",
        "User global comment",
        "Related location and data type",
        "Value in memory"};
      @Override protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(columnModel) {
          @Override public String getToolTipText(MouseEvent e) {
            //int col = columnAtPoint(e.getPoint());
            int index = columnModel.getColumnIndexAtX(e.getPoint().x);
            int realIndex = columnModel.getColumn(index).getModelIndex();
            return hh[realIndex];
          }
        };
      }

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
            else if(memory.isCode) tip="Memory marked as code ("+getAreaCodeSize(memory.address)+"/"+Shared.ShortToExe(jTableMemory.getSelectedRowCount())+")";
            else if (memory.isData) tip="Memory marked as data ("+getAreaDataSize(memory.address)+"/"+Shared.ShortToExe(jTableMemory.getSelectedRowCount())+")";
            else if (memory.isGarbage) tip="Memory marked as garbage ("+getAreaGarbageSize(memory.address)+"/"+Shared.ShortToExe(jTableMemory.getSelectedRowCount())+")";
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
            if (memory.type!=TYPE_EMPTY) {
              MemoryDasm mem=dataTableModelMemory.getData()[memory.related];
              switch (memory.type) {
                case TYPE_PLUS:
                if (mem.userLocation!=null && !"".equals(mem.userLocation)) tip=mem.userLocation+"+"+(memory.address-memory.related);
                else if (mem.dasmLocation!=null && !"".equals(mem.dasmLocation)) tip=mem.dasmLocation+"+"+(memory.address-memory.related);
                else tip="$"+Shared.ShortToExe(mem.address)+"+"+(memory.address-memory.related);
                break;
                case TYPE_MINUS:
                if (mem.userLocation!=null && !"".equals(mem.userLocation)) tip=mem.userLocation+(memory.address-memory.related);
                else if (mem.dasmLocation!=null && !"".equals(mem.dasmLocation)) tip=mem.dasmLocation+(memory.address-memory.related);
                else tip="$"+Shared.ShortToExe(mem.address)+(memory.address-memory.related);
                break;
                default:
                if (mem.userLocation!=null && !"".equals(mem.userLocation)) tip="#"+memory.type+mem.userLocation;
                else if (mem.dasmLocation!=null && !"".equals(mem.dasmLocation)) tip="#"+memory.type+mem.dasmLocation;
                else tip="#"+memory.type+"$"+Shared.ShortToExe(mem.address);
              }
            } else if (memory.dataType!=null) {
              tip=memory.dataType.getDescription();
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
    jMenuRecent = new javax.swing.JMenu();
    jMenuItemRecent1 = new javax.swing.JMenuItem();
    jMenuItemRecent2 = new javax.swing.JMenuItem();
    jMenuItemRecent3 = new javax.swing.JMenuItem();
    jMenuItemRecent4 = new javax.swing.JMenuItem();
    jMenuItemRecent5 = new javax.swing.JMenuItem();
    jMenuItemRecent6 = new javax.swing.JMenuItem();
    jMenuItemRecent7 = new javax.swing.JMenuItem();
    jMenuItemRecent8 = new javax.swing.JMenuItem();
    jMenuItemRecent9 = new javax.swing.JMenuItem();
    jMenuItemCloseProject = new javax.swing.JMenuItem();
    jSeparatorProject2 = new javax.swing.JPopupMenu.Separator();
    jMenuItemSaveProject = new javax.swing.JMenuItem();
    jMenuItemSaveAsProject = new javax.swing.JMenuItem();
    jSeparatorProject3 = new javax.swing.JPopupMenu.Separator();
    jMenuItemMPR = new javax.swing.JMenuItem();
    jMenuItem2 = new javax.swing.JMenuItem();
    jMenuItemExit = new javax.swing.JMenuItem();
    jMenuMemory = new javax.swing.JMenu();
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
    jSubMenu = new javax.swing.JMenu();
    jMenuBasic1 = new javax.swing.JMenu();
    jMenuItemBasicNone1 = new javax.swing.JMenuItem();
    jSeparatorStandard1 = new javax.swing.JPopupMenu.Separator();
    jMenuItemBasicV2_1 = new javax.swing.JMenuItem();
    jMenuItemBasicV3_5_1 = new javax.swing.JMenuItem();
    jMenuItemBasicV4_0_1 = new javax.swing.JMenuItem();
    jMenuItemBasicV7_0_1 = new javax.swing.JMenuItem();
    jSeparatorV3 = new javax.swing.JPopupMenu.Separator();
    jMenuItemSimon1 = new javax.swing.JMenuItem();
    jMenuItemAndreFachat1 = new javax.swing.JMenuItem();
    jMenuItemSpeech1 = new javax.swing.JMenuItem();
    jMenuItemFinalCart4 = new javax.swing.JMenuItem();
    jMenuItemUltrabasic1 = new javax.swing.JMenuItem();
    jMenuItemGraphics1 = new javax.swing.JMenuItem();
    jMenuItemWS1 = new javax.swing.JMenuItem();
    jMenuItemPegasus1 = new javax.swing.JMenuItem();
    jMenuItemXbasic1 = new javax.swing.JMenuItem();
    jMenuItemDrago1 = new javax.swing.JMenuItem();
    jMenuItemReu1 = new javax.swing.JMenuItem();
    jMenuItemLightninig1 = new javax.swing.JMenuItem();
    jMenuItemMagic1 = new javax.swing.JMenuItem();
    jMenuItemBlark1 = new javax.swing.JMenuItem();
    jMenuItemWSFinal1 = new javax.swing.JMenuItem();
    jMenuItemGame1 = new javax.swing.JMenuItem();
    jMenuItemBasex1 = new javax.swing.JMenuItem();
    jMenuItemSuper1 = new javax.swing.JMenuItem();
    jMenuItemExpanded1 = new javax.swing.JMenuItem();
    jMenuItemSuperExpanderChip1 = new javax.swing.JMenuItem();
    jMenuItemWarsaw1 = new javax.swing.JMenuItem();
    jMenuItemDbs1 = new javax.swing.JMenuItem();
    jMenuItemKipper1 = new javax.swing.JMenuItem();
    jMenuItemBails1 = new javax.swing.JMenuItem();
    jMenuItemEve1 = new javax.swing.JMenuItem();
    jMenuItemTool1 = new javax.swing.JMenuItem();
    jSeparatorV129 = new javax.swing.JPopupMenu.Separator();
    jMenuItemV9 = new javax.swing.JMenuItem();
    jSeparatorV21 = new javax.swing.JPopupMenu.Separator();
    jMenuItemSuperExpander1 = new javax.swing.JMenuItem();
    jMenuItemTurtle1 = new javax.swing.JMenuItem();
    jMenuItemEasy1 = new javax.swing.JMenuItem();
    jMenuItemV6 = new javax.swing.JMenuItem();
    jMenuItemV7 = new javax.swing.JMenuItem();
    jMenuItemExpandedV21 = new javax.swing.JMenuItem();
    jMenuItemHandy1 = new javax.swing.JMenuItem();
    jSeparator7 = new javax.swing.JPopupMenu.Separator();
    jMenuItemByteHex1 = new javax.swing.JMenuItem();
    jMenuItemByteDec1 = new javax.swing.JMenuItem();
    jMenuItemByteBin1 = new javax.swing.JMenuItem();
    jMenuItemByteChar1 = new javax.swing.JMenuItem();
    jSeparatorByte1 = new javax.swing.JPopupMenu.Separator();
    jMenuItemWord1 = new javax.swing.JMenuItem();
    jMenuItemWordSwapped1 = new javax.swing.JMenuItem();
    jSeparatorWord1 = new javax.swing.JPopupMenu.Separator();
    jMenuItemTribyte1 = new javax.swing.JMenuItem();
    jMenuItemLong1 = new javax.swing.JMenuItem();
    jSeparator5 = new javax.swing.JPopupMenu.Separator();
    jMenuItemAddress1 = new javax.swing.JMenuItem();
    jMenuItemStackWord1 = new javax.swing.JMenuItem();
    jSeparator6 = new javax.swing.JPopupMenu.Separator();
    jMenuItemSpriteMono1 = new javax.swing.JMenuItem();
    jMenuItemSpriteMulti1 = new javax.swing.JMenuItem();
    jSeparatorSprite1 = new javax.swing.JPopupMenu.Separator();
    jMenuItemText1 = new javax.swing.JMenuItem();
    jMenuItemNumText1 = new javax.swing.JMenuItem();
    jMenuItemTextZero1 = new javax.swing.JMenuItem();
    jMenuItemtextHighOne1 = new javax.swing.JMenuItem();
    jMenuItemtextShifted1 = new javax.swing.JMenuItem();
    jMenuItemTextScreen1 = new javax.swing.JMenuItem();
    jMenuItemTextPetascii1 = new javax.swing.JMenuItem();
    jMenuItemMarkGarbage = new javax.swing.JMenuItem();
    jSubMenuC = new javax.swing.JMenu();
    jMenuItemConstant0b = new javax.swing.JMenuItem();
    jMenuItemConstant9b = new javax.swing.JMenuItem();
    jMenuItemConstant1b = new javax.swing.JMenuItem();
    jMenuItemConstant2b = new javax.swing.JMenuItem();
    jMenuItemConstant3b = new javax.swing.JMenuItem();
    jMenuItemConstant4b = new javax.swing.JMenuItem();
    jMenuItemConstant5b = new javax.swing.JMenuItem();
    jMenuItemConstant6b = new javax.swing.JMenuItem();
    jMenuItemConstant7b = new javax.swing.JMenuItem();
    jMenuItemConstant8b = new javax.swing.JMenuItem();
    jSeparatorConstant3 = new javax.swing.JPopupMenu.Separator();
    jMenuItemConstantClear = new javax.swing.JMenuItem();
    jSeparatorConstant4 = new javax.swing.JPopupMenu.Separator();
    jMenuItemConstant0b1 = new javax.swing.JMenuItem();
    jMenuItemConstant1b1 = new javax.swing.JMenuItem();
    jMenuItemConstant2b1 = new javax.swing.JMenuItem();
    jMenuItemConstant3b1 = new javax.swing.JMenuItem();
    jMenuItemConstant4b1 = new javax.swing.JMenuItem();
    jMenuItemConstant5b1 = new javax.swing.JMenuItem();
    jMenuItemConstant6b1 = new javax.swing.JMenuItem();
    jMenuItemConstant7b1 = new javax.swing.JMenuItem();
    jMenuItemConstant8b1 = new javax.swing.JMenuItem();
    jMenuItemConstant9b1 = new javax.swing.JMenuItem();
    jMenuItemPlus = new javax.swing.JMenuItem();
    jMenuItemMinus = new javax.swing.JMenuItem();
    jMenuItemMemLow = new javax.swing.JMenuItem();
    jMenuItemMemLowHigh = new javax.swing.JMenuItem();
    jMenuItemMemBoth = new javax.swing.JMenuItem();
    jMenuItemMemHighLow = new javax.swing.JMenuItem();
    jMenuItemMemHigh = new javax.swing.JMenuItem();
    jMenuItemWizard = new javax.swing.JMenuItem();
    jMenuOption = new javax.swing.JMenu();
    jMenuItemConfigure = new javax.swing.JMenuItem();
    jMenuItemSIDLD = new javax.swing.JMenuItem();
    jSeparatorOption = new javax.swing.JPopupMenu.Separator();
    jMenuItemViewProject = new javax.swing.JMenuItem();
    jMenuItemViewLabels = new javax.swing.JMenuItem();
    jMenuSource = new javax.swing.JMenu();
    jMenuItem1 = new javax.swing.JMenuItem();
    jMenuItemDiss = new javax.swing.JMenuItem();
    jMenuItemAssembly = new javax.swing.JMenuItem();
    jSeparator1 = new javax.swing.JPopupMenu.Separator();
    jMenuItemFindDis = new javax.swing.JMenuItem();
    jMenuItemDissSaveAs = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JPopupMenu.Separator();
    jMenuItemFindSource = new javax.swing.JMenuItem();
    jMenuItemSourceSaveAs = new javax.swing.JMenuItem();
    jMenuSub = new javax.swing.JMenu();
    jMenuItemSaveAsDasm1 = new javax.swing.JMenuItem();
    jMenuItemSaveAsTmpx1 = new javax.swing.JMenuItem();
    jMenuItemSaveAsCa65_1 = new javax.swing.JMenuItem();
    jMenuItemSaveAsAcme1 = new javax.swing.JMenuItem();
    jMenuItemSaveAsKickAssembler1 = new javax.swing.JMenuItem();
    jMenuItemSaveAsTass64_1 = new javax.swing.JMenuItem();
    jMenuItemSaveAsGlass1 = new javax.swing.JMenuItem();
    jMenuItemSaveAsAS1 = new javax.swing.JMenuItem();
    jMenuItem3 = new javax.swing.JMenuItem();
    jMenuHelpContents = new javax.swing.JMenu();
    jMenuItemContents = new javax.swing.JMenuItem();
    jSeparatorHelp1 = new javax.swing.JPopupMenu.Separator();
    jMenuItemLicense = new javax.swing.JMenuItem();
    jMenuItemCredits = new javax.swing.JMenuItem();
    jMenuItemAbout = new javax.swing.JMenuItem();
    jSeparatorHelp2 = new javax.swing.JPopupMenu.Separator();
    jMenuItemImportLabels = new javax.swing.JMenuItem();
    jMenuItemRefactorLabels = new javax.swing.JMenuItem();
    jMenuItemAutComment = new javax.swing.JMenuItem();
    jMenuItemAutLabel = new javax.swing.JMenuItem();
    jMenuItemSidldPlayer = new javax.swing.JMenuItem();
    jSeparatorHelp3 = new javax.swing.JPopupMenu.Separator();
    jMenuUndo = new javax.swing.JMenu();
    jMenuItemUndo1 = new javax.swing.JMenuItem();
    jMenuItemUndo2 = new javax.swing.JMenuItem();
    jMenuItemUndo3 = new javax.swing.JMenuItem();
    jMenuItemUndo4 = new javax.swing.JMenuItem();
    jMenuItemUndo5 = new javax.swing.JMenuItem();
    jMenuItemUndo6 = new javax.swing.JMenuItem();
    jMenuItemUndo7 = new javax.swing.JMenuItem();
    jMenuItemUndo8 = new javax.swing.JMenuItem();
    jMenuItemUndo9 = new javax.swing.JMenuItem();

    jMenuBasic.setText("Data as Basic");

    jMenuItemBasicNone.setText("no BASIC");
    jMenuItemBasicNone.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemBasicNoneActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemBasicNone);
    jMenuBasic.add(jSeparatorStandard);

    jMenuItemBasicV2_0.setText(BasicType.BASIC_V2_0.getName());
    jMenuItemBasicV2_0.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemBasicV2_0ActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemBasicV2_0);

    jMenuItemBasicV3_5.setText(BasicType.BASIC_V3_5.getName());
    jMenuItemBasicV3_5.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemBasicV3_5ActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemBasicV3_5);

    jMenuItemBasicV4_0.setText(BasicType.BASIC_V4_0.getName());
    jMenuItemBasicV4_0.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemBasicV4_0ActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemBasicV4_0);

    jMenuItemBasicV7_0.setText(BasicType.BASIC_V7_0.getName());
    jMenuItemBasicV7_0.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemBasicV7_0ActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemBasicV7_0);
    jMenuBasic.add(jSeparatorV2);

    jMenuItemSimon.setText(BasicType.BASIC_SIMON.getName());
    jMenuItemSimon.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemSimonActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemSimon);

    jMenuItemAndreFachat.setText(BasicType.BASIC_ANDRE_FACHAT.getName());
    jMenuItemAndreFachat.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemAndreFachatActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemAndreFachat);

    jMenuItemSpeech.setText(BasicType.BASIC_SPEECH.getName());
    jMenuItemSpeech.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemSpeechActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemSpeech);

    jMenuItemFinalCart3.setText(BasicType.BASIC_FINAL_CART3.getName());
    jMenuItemFinalCart3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemFinalCart3ActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemFinalCart3);

    jMenuItemUltrabasic.setText(BasicType.BASIC_ULTRABASIC.getName());
    jMenuItemUltrabasic.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemUltrabasicActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemUltrabasic);

    jMenuItemGraphics.setText(BasicType.BASIC_GRAPHICS.getName());
    jMenuItemGraphics.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemGraphicsActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemGraphics);

    jMenuItemWS.setText(BasicType.BASIC_WS.getName());
    jMenuItemWS.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemWSActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemWS);

    jMenuItemPegasus.setText(BasicType.BASIC_PEGASUS.getName());
    jMenuItemPegasus.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemPegasusActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemPegasus);

    jMenuItemXbasic.setText(BasicType.BASIC_XBASIC.getName());
    jMenuItemXbasic.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemXbasicActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemXbasic);

    jMenuItemDrago.setText(BasicType.BASIC_DRAGO.getName());
    jMenuItemDrago.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemDragoActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemDrago);

    jMenuItemReu.setText(BasicType.BASIC_REU.getName());
    jMenuItemReu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemReuActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemReu);

    jMenuItemLightninig.setText(BasicType.BASIC_LIGHTNING.getName());
    jMenuItemLightninig.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemLightninigActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemLightninig);

    jMenuItemMagic.setText(BasicType.BASIC_MAGIC.getName());
    jMenuItemMagic.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemMagicActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemMagic);

    jMenuItemBlark.setText(BasicType.BASIC_BLARG.getName());
    jMenuItemBlark.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemBlarkActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemBlark);

    jMenuItemWSFinal.setText(BasicType.BASIC_WS_FINAL.getName());
    jMenuItemWSFinal.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemWSFinalActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemWSFinal);

    jMenuItemGame.setText(BasicType.BASIC_GAME.getName());
    jMenuItemGame.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemGameActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemGame);

    jMenuItemBasex.setText(BasicType.BASIC_BASEX.getName());
    jMenuItemBasex.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemBasexActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemBasex);

    jMenuItemSuper.setText(BasicType.BASIC_SUPER.getName());
    jMenuItemSuper.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemSuperActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemSuper);

    jMenuItemExpanded.setText(BasicType.BASIC_EXPANDED.getName());
    jMenuItemExpanded.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemExpandedActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemExpanded);

    jMenuItemSuperExpanderChip.setText(BasicType.BASIC_SUPER_EXPANDER_CHIP.getName());
    jMenuItemSuperExpanderChip.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemSuperExpanderChipActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemSuperExpanderChip);

    jMenuItemWarsaw.setText(BasicType.BASIC_WARSAW.getName());
    jMenuItemWarsaw.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemWarsawActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemWarsaw);

    jMenuItemDbs.setText(BasicType.BASIC_DBS.getName());
    jMenuItemDbs.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemDbsActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemDbs);

    jMenuItemKipper.setText(BasicType.BASIC_KIPPER.getName());
    jMenuItemKipper.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemKipperActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemKipper);

    jMenuItemBails.setText(BasicType.BASIC_BAILS.getName());
    jMenuItemBails.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemBailsActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemBails);

    jMenuItemEve.setText(BasicType.BASIC_EVE.getName());
    jMenuItemEve.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemEveActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemEve);

    jMenuItemTool.setText(BasicType.BASIC_TOOL.getName());
    jMenuItemTool.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemToolActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemTool);
    jMenuBasic.add(jSeparatorV128);

    jMenuItemV8.setText(BasicType.BASIC_V8.getName());
    jMenuItemV8.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemV8ActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemV8);
    jMenuBasic.add(jSeparatorV20);

    jMenuItemSuperExpander.setText(BasicType.BASIC_SUPER_EXPANDER.getName());
    jMenuItemSuperExpander.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemSuperExpanderActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemSuperExpander);

    jMenuItemTurtle.setText(BasicType.BASIC_TURTLE.getName());
    jMenuItemTurtle.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemTurtleActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemTurtle);

    jMenuItemEasy.setText(BasicType.BASIC_EASY.getName());
    jMenuItemEasy.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemEasyActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemEasy);

    jMenuItemV4.setText(BasicType.BASIC_V4.getName());
    jMenuItemV4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemV4ActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemV4);

    jMenuItemV5.setText(BasicType.BASIC_V5.getName());
    jMenuItemV5.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemV5ActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemV5);

    jMenuItemExpandedV20.setText(BasicType.BASIC_EXPANDED_V20.getName());
    jMenuItemExpandedV20.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemExpandedV20ActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemExpandedV20);

    jMenuItemHandy.setText(BasicType.BASIC_HANDY.getName());
    jMenuItemHandy.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemHandyActionPerformed(evt);
      }
    });
    jMenuBasic.add(jMenuItemHandy);

    jPopupMenuData.add(jMenuBasic);

    jMenuItemByteHex.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/B.png"))); // NOI18N
    jMenuItemByteHex.setText("(B) Mark data as Byte (HEX)");
    jMenuItemByteHex.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemByteHexActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemByteHex);

    jMenuItemByteDec.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/D.png"))); // NOI18N
    jMenuItemByteDec.setText("(D) Mark data as Byte (DEC)");
    jMenuItemByteDec.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemByteDecActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemByteDec);

    jMenuItemByteBin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/Y.png"))); // NOI18N
    jMenuItemByteBin.setText("(Y) Mark data as Byte (BIN)");
    jMenuItemByteBin.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemByteBinActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemByteBin);

    jMenuItemByteChar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/R.png"))); // NOI18N
    jMenuItemByteChar.setText("(R) Mark data as Byte (CHAR)");
    jMenuItemByteChar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemByteCharActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemByteChar);
    jPopupMenuData.add(jSeparatorByte);

    jMenuItemWord.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/W.png"))); // NOI18N
    jMenuItemWord.setText("(W) Mark data as Word");
    jMenuItemWord.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemWordActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemWord);

    jMenuItemWordSwapped.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/P.png"))); // NOI18N
    jMenuItemWordSwapped.setText("(P) Mark data as Word Swapped");
    jMenuItemWordSwapped.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemWordSwappedActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemWordSwapped);
    jPopupMenuData.add(jSeparatorWord);

    jMenuItemTribyte.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/E.png"))); // NOI18N
    jMenuItemTribyte.setText("(E) Mark data as Tribyte");
    jMenuItemTribyte.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemTribyteActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemTribyte);

    jMenuItemLong.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/L.png"))); // NOI18N
    jMenuItemLong.setText("(L) Mark data as Long");
    jMenuItemLong.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemLongActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemLong);
    jPopupMenuData.add(jSeparatorPopUpMenu0);

    jMenuItemAddress.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/A.png"))); // NOI18N
    jMenuItemAddress.setText("(A) Mark data as Address");
    jMenuItemAddress.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemAddressActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemAddress);

    jMenuItemStackWord.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/S.png"))); // NOI18N
    jMenuItemStackWord.setText("(S) Mark data as Stack Word");
    jMenuItemStackWord.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemStackWordActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemStackWord);
    jPopupMenuData.add(jSeparatorPopUpMenu1);

    jMenuItemSpriteMono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/O.png"))); // NOI18N
    jMenuItemSpriteMono.setText("(O) Mark data as Monocromatic Sprite definitions");
    jMenuItemSpriteMono.setToolTipText("");
    jMenuItemSpriteMono.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemSpriteMonoActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemSpriteMono);

    jMenuItemSpriteMulti.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/F.png"))); // NOI18N
    jMenuItemSpriteMulti.setText("(F) Mark data as Multicolor Sprite definitions");
    jMenuItemSpriteMulti.setToolTipText("");
    jMenuItemSpriteMulti.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemSpriteMultiActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemSpriteMulti);
    jPopupMenuData.add(jSeparatorSprite);

    jMenuItemText.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/T.png"))); // NOI18N
    jMenuItemText.setText("(T) Mark data as Text");
    jMenuItemText.setToolTipText("");
    jMenuItemText.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemTextActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemText);

    jMenuItemNumText.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/N.png"))); // NOI18N
    jMenuItemNumText.setText("(N) Mark data as Text with # chars before");
    jMenuItemNumText.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemNumTextActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemNumText);

    jMenuItemTextZero.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/Z.png"))); // NOI18N
    jMenuItemTextZero.setText("(Z) Mark data as text terminated with 0");
    jMenuItemTextZero.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemTextZeroActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemTextZero);

    jMenuItemtextHighOne.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/M.png"))); // NOI18N
    jMenuItemtextHighOne.setText("(M) Mark data as Text with high bit 1");
    jMenuItemtextHighOne.setToolTipText("");
    jMenuItemtextHighOne.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemtextHighOneActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemtextHighOne);

    jMenuItemtextShifted.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/H.png"))); // NOI18N
    jMenuItemtextShifted.setText("(H) Mark data as Text left shifted");
    jMenuItemtextShifted.setToolTipText("");
    jMenuItemtextShifted.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemtextShiftedActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemtextShifted);

    jMenuItemTextScreen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/C.png"))); // NOI18N
    jMenuItemTextScreen.setText("(C) Mark data as Text converted to screen code");
    jMenuItemTextScreen.setToolTipText("");
    jMenuItemTextScreen.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemTextScreenActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemTextScreen);

    jMenuItemTextPetascii.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/I.png"))); // NOI18N
    jMenuItemTextPetascii.setText("(I) Mark data as Text converted to petAscii code");
    jMenuItemTextPetascii.setToolTipText("");
    jMenuItemTextPetascii.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemTextPetasciiActionPerformed(evt);
      }
    });
    jPopupMenuData.add(jMenuItemTextPetascii);

    jMenuItemConstant0.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/0.png"))); // NOI18N
    jMenuItemConstant0.setText("Sub-mark cell as of constant 0");
    jMenuItemConstant0.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant0ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant0);

    jMenuItemConstant1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/1.png"))); // NOI18N
    jMenuItemConstant1.setText("Sub-mark cell as of constant 1");
    jMenuItemConstant1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant1ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant1);

    jMenuItemConstant2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/2.png"))); // NOI18N
    jMenuItemConstant2.setText("Sub-mark cell as of constant 2");
    jMenuItemConstant2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant2ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant2);

    jMenuItemConstant3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/3.png"))); // NOI18N
    jMenuItemConstant3.setText("Sub-mark cell as of constant 3");
    jMenuItemConstant3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant3ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant3);

    jMenuItemConstant4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/4.png"))); // NOI18N
    jMenuItemConstant4.setText("Sub-mark cell as of constant 4");
    jMenuItemConstant4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant4ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant4);

    jMenuItemConstant5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/5.png"))); // NOI18N
    jMenuItemConstant5.setText("Sub-mark cell as of constant 5");
    jMenuItemConstant5.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant5ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant5);

    jMenuItemConstant6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/6.png"))); // NOI18N
    jMenuItemConstant6.setText("Sub-mark cell as of constant 6");
    jMenuItemConstant6.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant6ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant6);

    jMenuItemConstant7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/7.png"))); // NOI18N
    jMenuItemConstant7.setText("Sub-mark cell as of constant 7");
    jMenuItemConstant7.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant7ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant7);

    jMenuItemConstant8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/8.png"))); // NOI18N
    jMenuItemConstant8.setText("Sub-mark cell as of constant 8");
    jMenuItemConstant8.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant8ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant8);

    jMenuItemConstant9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/9.png"))); // NOI18N
    jMenuItemConstant9.setText("Sub-mark cell as of constant 9");
    jMenuItemConstant9.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant9ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant9);
    jPopupMenuConstant.add(jSeparatorConstant);

    jMenuItemConstantClear_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/0-9.png"))); // NOI18N
    jMenuItemConstantClear_.setText("Sub-mark cell as of constant none");
    jMenuItemConstantClear_.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstantClear_ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstantClear_);
    jPopupMenuConstant.add(jSeparatorConstant2);

    jMenuItemConstant10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/0.png"))); // NOI18N
    jMenuItemConstant10.setText("Sub-mark cell as of constant !");
    jMenuItemConstant10.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant10ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant10);

    jMenuItemConstant11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/1.png"))); // NOI18N
    jMenuItemConstant11.setText("Sub-mark cell as of constant \"");
    jMenuItemConstant11.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant11ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant11);

    jMenuItemConstant12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/2.png"))); // NOI18N
    jMenuItemConstant12.setText("Sub-mark cell as of constant £");
    jMenuItemConstant12.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant12ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant12);

    jMenuItemConstant13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/3.png"))); // NOI18N
    jMenuItemConstant13.setText("Sub-mark cell as of constant $");
    jMenuItemConstant13.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant13ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant13);

    jMenuItemConstant14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/4.png"))); // NOI18N
    jMenuItemConstant14.setText("Sub-mark cell as of constant %");
    jMenuItemConstant14.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant14ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant14);

    jMenuItemConstant15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/5.png"))); // NOI18N
    jMenuItemConstant15.setText("Sub-mark cell as of constant &");
    jMenuItemConstant15.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant15ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant15);

    jMenuItemConstant16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/6.png"))); // NOI18N
    jMenuItemConstant16.setText("Sub-mark cell as of constant /");
    jMenuItemConstant16.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant16ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant16);

    jMenuItemConstant17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/7.png"))); // NOI18N
    jMenuItemConstant17.setText("Sub-mark cell as of constant (");
    jMenuItemConstant17.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant17ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant17);

    jMenuItemConstant18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/8.png"))); // NOI18N
    jMenuItemConstant18.setText("Sub-mark cell as of constant )");
    jMenuItemConstant18.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant18ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant18);

    jMenuItemConstant19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/9.png"))); // NOI18N
    jMenuItemConstant19.setText("Sub-mark cell as of constant =");
    jMenuItemConstant19.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemConstant19ActionPerformed(evt);
      }
    });
    jPopupMenuConstant.add(jMenuItemConstant19);

    jMenuItemSaveAsDasm.setText("Save in Dasm format");
    jMenuItemSaveAsDasm.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemSaveAsDasmActionPerformed(evt);
      }
    });
    jPopupMenuSaveAs.add(jMenuItemSaveAsDasm);

    jMenuItemSaveAsTmpx.setText("Save in TMPx format");
    jMenuItemSaveAsTmpx.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemSaveAsTmpxActionPerformed(evt);
      }
    });
    jPopupMenuSaveAs.add(jMenuItemSaveAsTmpx);

    jMenuItemSaveAsCa65.setText("Save in CA65 format");
    jMenuItemSaveAsCa65.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemSaveAsCa65ActionPerformed(evt);
      }
    });
    jPopupMenuSaveAs.add(jMenuItemSaveAsCa65);

    jMenuItemSaveAsAcme.setText("Save in Acme format");
    jMenuItemSaveAsAcme.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemSaveAsAcmeActionPerformed(evt);
      }
    });
    jPopupMenuSaveAs.add(jMenuItemSaveAsAcme);

    jMenuItemSaveAsKickAssembler.setText("Save in KickAssembler format");
    jMenuItemSaveAsKickAssembler.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemSaveAsKickAssemblerActionPerformed(evt);
      }
    });
    jPopupMenuSaveAs.add(jMenuItemSaveAsKickAssembler);

    jMenuItemSaveAsTass64.setText("Save in Tass64 format");
    jMenuItemSaveAsTass64.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemSaveAsTass64ActionPerformed(evt);
      }
    });
    jPopupMenuSaveAs.add(jMenuItemSaveAsTass64);

    jMenuItemSaveAsGlass.setText("Save in Glass format");
    jMenuItemSaveAsGlass.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemSaveAsGlassActionPerformed(evt);
      }
    });
    jPopupMenuSaveAs.add(jMenuItemSaveAsGlass);
    jMenuItemSaveAsGlass.getAccessibleContext().setAccessibleName("Save in Glass format");

    jMenuItemSaveAsAS.setText("Save in AS format");
    jMenuItemSaveAsAS.setToolTipText("");
    jMenuItemSaveAsAS.setActionCommand("Save in AS format");
    jMenuItemSaveAsAS.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemSaveAsASActionPerformed(evt);
      }
    });
    jPopupMenuSaveAs.add(jMenuItemSaveAsAS);
    jMenuItemSaveAsAS.getAccessibleContext().setAccessibleName("Save in AS format");

    jMenuItemCopy.setText("Copy into this instance");
    jMenuItemCopy.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemCopyActionPerformed(evt);
      }
    });
    jPopupMenuMemory.add(jMenuItemCopy);

    jMenuItemPaste.setText("Paste from another instance");
    jMenuItemPaste.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemPasteActionPerformed(evt);
      }
    });
    jPopupMenuMemory.add(jMenuItemPaste);
    jPopupMenuMemory.add(jSeparatorMem1);

    jMenuItemFindAddr.setText("Find address");
    jMenuItemFindAddr.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemFindAddrActionPerformed(evt);
      }
    });
    jPopupMenuMemory.add(jMenuItemFindAddr);

    jMenuItemFindSeq.setText("Find sequences of bytes");
    jMenuItemFindSeq.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemFindSeqActionPerformed(evt);
      }
    });
    jPopupMenuMemory.add(jMenuItemFindSeq);
    jPopupMenuMemory.add(jSeparatorMem2);

    jMenuItemCode.setText("Mark as code");
    jMenuItemCode.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemCodeActionPerformed(evt);
      }
    });
    jPopupMenuMemory.add(jMenuItemCode);

    jMenuItemData.setText("Mark as data");
    jMenuItemData.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemDataActionPerformed(evt);
      }
    });
    jPopupMenuMemory.add(jMenuItemData);

    jMenuDataSel.setText("More specific data selection:");

    jMenuItemByteHex2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/B.png"))); // NOI18N
    jMenuItemByteHex2.setText("(B) Mark data as Byte (HEX)");
    jMenuItemByteHex2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemByteHex2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemByteHex2);

    jMenuItemByteDec2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/D.png"))); // NOI18N
    jMenuItemByteDec2.setText("(D) Mark data as Byte (DEC)");
    jMenuItemByteDec2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemByteDec2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemByteDec2);

    jMenuItemByteBin2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/Y.png"))); // NOI18N
    jMenuItemByteBin2.setText("(Y) Mark data as Byte (BIN)");
    jMenuItemByteBin2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemByteBin2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemByteBin2);

    jMenuItemByteChar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/R.png"))); // NOI18N
    jMenuItemByteChar2.setText("(R) Mark data as Byte (CHAR)");
    jMenuItemByteChar2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemByteChar2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemByteChar2);
    jMenuDataSel.add(jSeparatorByte2);

    jMenuItemWord2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/W.png"))); // NOI18N
    jMenuItemWord2.setText("(W) Mark data as Word");
    jMenuItemWord2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemWord2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemWord2);

    jMenuItemWordSwapped2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/P.png"))); // NOI18N
    jMenuItemWordSwapped2.setText("(P) Mark data as Word Swapped");
    jMenuItemWordSwapped2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemWordSwapped2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemWordSwapped2);
    jMenuDataSel.add(jSeparatorWord2);

    jMenuItemTribyte2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/E.png"))); // NOI18N
    jMenuItemTribyte2.setText("(E) Mark data as Tribyte");
    jMenuItemTribyte2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemTribyte2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemTribyte2);

    jMenuItemLong2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/L.png"))); // NOI18N
    jMenuItemLong2.setText("(L) Mark data as Long");
    jMenuItemLong2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemLong2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemLong2);
    jMenuDataSel.add(jSeparatorPopUpMenu2);

    jMenuItemAddress2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/A.png"))); // NOI18N
    jMenuItemAddress2.setText("(A) Mark data as Address");
    jMenuItemAddress2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemAddress2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemAddress2);

    jMenuItemStackWord2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/S.png"))); // NOI18N
    jMenuItemStackWord2.setText("(S) Mark data as Stack Word");
    jMenuItemStackWord2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemStackWord2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemStackWord2);
    jMenuDataSel.add(jSeparatorPopUpMenu3);

    jMenuItemSpriteMono2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/O.png"))); // NOI18N
    jMenuItemSpriteMono2.setText("(O) Mark data as Monocromatic Sprite definitions");
    jMenuItemSpriteMono2.setToolTipText("");
    jMenuItemSpriteMono2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemSpriteMono2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemSpriteMono2);

    jMenuItemSpriteMulti2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/F.png"))); // NOI18N
    jMenuItemSpriteMulti2.setText("(F) Mark data as Multicolor Sprite definitions");
    jMenuItemSpriteMulti2.setToolTipText("");
    jMenuItemSpriteMulti2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemSpriteMulti2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemSpriteMulti2);
    jMenuDataSel.add(jSeparatorSprite2);

    jMenuItemText2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/T.png"))); // NOI18N
    jMenuItemText2.setText("(T) Mark data as Text");
    jMenuItemText2.setToolTipText("");
    jMenuItemText2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemText2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemText2);

    jMenuItemNumText2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/N.png"))); // NOI18N
    jMenuItemNumText2.setText("(N) Mark data as Text with # chars before");
    jMenuItemNumText2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemNumText2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemNumText2);

    jMenuItemTextZero2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/Z.png"))); // NOI18N
    jMenuItemTextZero2.setText("(Z) Mark data as text terminated with 0");
    jMenuItemTextZero2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemTextZero2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemTextZero2);

    jMenuItemtextHighOne2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/M.png"))); // NOI18N
    jMenuItemtextHighOne2.setText("(M) Mark data as Text with high bit 1");
    jMenuItemtextHighOne2.setToolTipText("");
    jMenuItemtextHighOne2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemtextHighOne2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemtextHighOne2);

    jMenuItemtextShifted2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/H.png"))); // NOI18N
    jMenuItemtextShifted2.setText("(H) Mark data as Text left shifted");
    jMenuItemtextShifted2.setToolTipText("");
    jMenuItemtextShifted2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemtextShifted2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemtextShifted2);

    jMenuItemTextScreen2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/C.png"))); // NOI18N
    jMenuItemTextScreen2.setText("(C) Mark data as Text converted to screen code");
    jMenuItemTextScreen2.setToolTipText("");
    jMenuItemTextScreen2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemTextScreen2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemTextScreen2);

    jMenuItemTextPetascii2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/I.png"))); // NOI18N
    jMenuItemTextPetascii2.setText("(I) Mark data as Text converted to petAscii code");
    jMenuItemTextPetascii2.setToolTipText("");
    jMenuItemTextPetascii2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemTextPetascii2ActionPerformed(evt);
      }
    });
    jMenuDataSel.add(jMenuItemTextPetascii2);

    jPopupMenuMemory.add(jMenuDataSel);

    jMenuItemGarbage.setText("Mark as garbage");
    jMenuItemGarbage.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemGarbageActionPerformed(evt);
      }
    });
    jPopupMenuMemory.add(jMenuItemGarbage);

    jMenuItemUnmark.setText("Un-Mark area");
    jMenuItemUnmark.setToolTipText("");
    jMenuItemUnmark.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemUnmarkActionPerformed(evt);
      }
    });
    jPopupMenuMemory.add(jMenuItemUnmark);
    jPopupMenuMemory.add(jSeparatorMem3);

    jMenuItemHex.setText("Show hex/text/char view");
    jMenuItemHex.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemHexActionPerformed(evt);
      }
    });
    jPopupMenuMemory.add(jMenuItemHex);

    jMenuItemBlockLabel.setText("Sequential labeled blocks");
    jMenuItemBlockLabel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemBlockLabelActionPerformed(evt);
      }
    });
    jPopupMenuMemory.add(jMenuItemBlockLabel);

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    setTitle("JC64Dis");
    setIconImages(iconImages);
    addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        formMouseEntered(evt);
      }
    });
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
    });

    jPanelToolBar.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        jPanelToolBarMouseEntered(evt);
      }
    });
    jPanelToolBar.setLayout(new javax.swing.BoxLayout(jPanelToolBar, javax.swing.BoxLayout.LINE_AXIS));

    jToolBarFile.setRollover(true);

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
    jToolBarFile.add(jButtonNewProject);

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
    jToolBarFile.add(jButtonOpenProject);

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
    jToolBarFile.add(jButtonClose);

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
    jToolBarFile.add(jButtonSaveProject);

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
    jToolBarFile.add(jButtonSaveProjectAs);

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
    jToolBarFile.add(jButtonMPR);

    jButtonMerge.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/merge.png"))); // NOI18N
    jButtonMerge.setToolTipText("Collaborative merge");
    jButtonMerge.setFocusable(false);
    jButtonMerge.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonMerge.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonMerge.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonMergeActionPerformed(evt);
      }
    });
    jToolBarFile.add(jButtonMerge);

    jButtonExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/exit.png"))); // NOI18N
    jButtonExit.setToolTipText("Exit application");
    jButtonExit.setFocusable(false);
    jButtonExit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonExit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonExit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonExitActionPerformed(evt);
      }
    });
    jToolBarFile.add(jButtonExit);

    jPanelToolBar.add(jToolBarFile);

    jToolBarMemory.setRollover(true);

    jButtonClearDMem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/eraser.png"))); // NOI18N
    jButtonClearDMem.setToolTipText("Erase daisassembly automatic comment");
    jButtonClearDMem.setFocusable(false);
    jButtonClearDMem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonClearDMem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonClearDMem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonClearDMemActionPerformed(evt);
      }
    });
    jToolBarMemory.add(jButtonClearDMem);

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
    jToolBarMemory.add(jButtonClearUMem);

    jButtonClearDLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/eraser2.png"))); // NOI18N
    jButtonClearDLabel.setToolTipText("Erase disassembly automatic label");
    jButtonClearDLabel.setFocusable(false);
    jButtonClearDLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonClearDLabel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonClearDLabel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonClearDLabelActionPerformed(evt);
      }
    });
    jToolBarMemory.add(jButtonClearDLabel);

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
    jToolBarMemory.add(jButtonAddUserComm);

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
    jToolBarMemory.add(jButtonAddUserBlock);

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
    jToolBarMemory.add(jButtonAddUserLabel);

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
    jToolBarMemory.add(jButtonAddUserLabelOp);

    jButtonMarkCode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/code.png"))); // NOI18N
    jButtonMarkCode.setToolTipText("Mark the selected addresses as code");
    jButtonMarkCode.setFocusable(false);
    jButtonMarkCode.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonMarkCode.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonMarkCode.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        jButtonMarkCodeMouseEntered(evt);
      }
    });
    jButtonMarkCode.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonMarkCodeActionPerformed(evt);
      }
    });
    jToolBarMemory.add(jButtonMarkCode);

    jButtonMarkData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/data.png"))); // NOI18N
    jButtonMarkData.setToolTipText("Mark the selected addresses as data");
    jButtonMarkData.setFocusable(false);
    jButtonMarkData.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonMarkData.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonMarkData.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        jButtonMarkDataMouseEntered(evt);
      }
    });
    jButtonMarkData.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonMarkDataActionPerformed(evt);
      }
    });
    jToolBarMemory.add(jButtonMarkData);

    jButtonMarkGarbage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/garb.png"))); // NOI18N
    jButtonMarkGarbage.setToolTipText("Mark the selected addresses as garbage");
    jButtonMarkGarbage.setFocusable(false);
    jButtonMarkGarbage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonMarkGarbage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonMarkGarbage.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        jButtonMarkGarbageMouseEntered(evt);
      }
    });
    jButtonMarkGarbage.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonMarkGarbageActionPerformed(evt);
      }
    });
    jToolBarMemory.add(jButtonMarkGarbage);

    jButtonMarkConstant.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/const.png"))); // NOI18N
    jButtonMarkConstant.setToolTipText("Mark the selected addresses as constant");
    jButtonMarkConstant.setFocusable(false);
    jButtonMarkConstant.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonMarkConstant.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonMarkConstant.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        jButtonMarkConstantMouseEntered(evt);
      }
    });
    jButtonMarkConstant.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonMarkConstantActionPerformed(evt);
      }
    });
    jToolBarMemory.add(jButtonMarkConstant);

    jButtonMarkPlus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/plus.png"))); // NOI18N
    jButtonMarkPlus.setToolTipText("Mark the selected addresses as +");
    jButtonMarkPlus.setFocusable(false);
    jButtonMarkPlus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonMarkPlus.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonMarkPlus.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        jButtonMarkPlusMouseEntered(evt);
      }
    });
    jButtonMarkPlus.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonMarkPlusActionPerformed(evt);
      }
    });
    jToolBarMemory.add(jButtonMarkPlus);

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
    jToolBarMemory.add(jButtonMarkMinus);

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
    jToolBarMemory.add(jButtonMarkLow);

    jButtonMarkLowHigh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/minmax.png"))); // NOI18N
    jButtonMarkLowHigh.setToolTipText("Assign the 2 selected addresses as #<>");
    jButtonMarkLowHigh.setFocusable(false);
    jButtonMarkLowHigh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonMarkLowHigh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonMarkLowHigh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonMarkLowHighActionPerformed(evt);
      }
    });
    jToolBarMemory.add(jButtonMarkLowHigh);

    jButtonMarkBoth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/both.png"))); // NOI18N
    jButtonMarkBoth.setToolTipText("Assign the 2 tables as #<>");
    jButtonMarkBoth.setFocusable(false);
    jButtonMarkBoth.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonMarkBoth.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonMarkBoth.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonMarkBothActionPerformed(evt);
      }
    });
    jToolBarMemory.add(jButtonMarkBoth);

    jButtonMarkHighLow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/maxmin.png"))); // NOI18N
    jButtonMarkHighLow.setToolTipText("Assign the 2 selected addresses as #<>");
    jButtonMarkHighLow.setFocusable(false);
    jButtonMarkHighLow.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonMarkHighLow.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonMarkHighLow.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonMarkHighLowActionPerformed(evt);
      }
    });
    jToolBarMemory.add(jButtonMarkHighLow);

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
    jToolBarMemory.add(jButtonMarkMax);

    jButtonWizard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/wizard.png"))); // NOI18N
    jButtonWizard.setToolTipText("Assign using a wizard");
    jButtonWizard.setFocusable(false);
    jButtonWizard.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonWizard.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonWizard.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonWizardActionPerformed(evt);
      }
    });
    jToolBarMemory.add(jButtonWizard);

    jButtonJumpBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/back.png"))); // NOI18N
    jButtonJumpBack.setToolTipText("Jump back position");
    jButtonJumpBack.setFocusable(false);
    jButtonJumpBack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonJumpBack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonJumpBack.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonJumpBackActionPerformed(evt);
      }
    });
    jToolBarMemory.add(jButtonJumpBack);

    jButtonJumpFollow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/forward.png"))); // NOI18N
    jButtonJumpFollow.setToolTipText("Jump follow position");
    jButtonJumpFollow.setFocusable(false);
    jButtonJumpFollow.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonJumpFollow.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonJumpFollow.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonJumpFollowActionPerformed(evt);
      }
    });
    jToolBarMemory.add(jButtonJumpFollow);

    jPanelToolBar.add(jToolBarMemory);

    jToolBarOption.setRollover(true);

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
    jToolBarOption.add(jButtonConfigure);

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
    jToolBarOption.add(jButtonSIDLD);

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
    jToolBarOption.add(jButtonViewProject);

    jButtonViewLabels.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/view_label.png"))); // NOI18N
    jButtonViewLabels.setToolTipText("View labels");
    jButtonViewLabels.setFocusable(false);
    jButtonViewLabels.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonViewLabels.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonViewLabels.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonViewLabelsActionPerformed(evt);
      }
    });
    jToolBarOption.add(jButtonViewLabels);

    jPanelToolBar.add(jToolBarOption);

    jToolBarSource.setRollover(true);

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
    jToolBarSource.add(jButtonFindMem);

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
    jToolBarSource.add(jButtonDisassemble);

    jButtonAssemble.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/assembler.png"))); // NOI18N
    jButtonAssemble.setToolTipText("Assemble");
    jButtonAssemble.setFocusable(false);
    jButtonAssemble.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonAssemble.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonAssemble.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonAssembleActionPerformed(evt);
      }
    });
    jToolBarSource.add(jButtonAssemble);

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
    jToolBarSource.add(jButtonFindDis);

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
    jToolBarSource.add(jButtonExportAsDiss);

    jButtonFindSource.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/finds.png"))); // NOI18N
    jButtonFindSource.setToolTipText("Find a text in source");
    jButtonFindSource.setFocusable(false);
    jButtonFindSource.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonFindSource.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonFindSource.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        jButtonFindSourceMouseEntered(evt);
      }
    });
    jButtonFindSource.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonFindSourceActionPerformed(evt);
      }
    });
    jToolBarSource.add(jButtonFindSource);

    jButtonExportAsSource.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/exportas2.png"))); // NOI18N
    jButtonExportAsSource.setToolTipText("Save source file");
    jButtonExportAsSource.setFocusable(false);
    jButtonExportAsSource.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonExportAsSource.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonExportAsSource.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        jButtonExportAsSourceMouseEntered(evt);
      }
    });
    jButtonExportAsSource.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonExportAsSourceActionPerformed(evt);
      }
    });
    jToolBarSource.add(jButtonExportAsSource);

    jButtonExportAsSource1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/freeze.png"))); // NOI18N
    jButtonExportAsSource1.setToolTipText("Freeze the source");
    jButtonExportAsSource1.setFocusable(false);
    jButtonExportAsSource1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonExportAsSource1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonExportAsSource1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        jButtonExportAsSource1MouseEntered(evt);
      }
    });
    jButtonExportAsSource1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonExportAsSource1ActionPerformed(evt);
      }
    });
    jToolBarSource.add(jButtonExportAsSource1);

    jPanelToolBar.add(jToolBarSource);

    jToolBarPerformance.setRollover(true);
    jToolBarPerformance.setAlignmentY(0.5F);
    jToolBarPerformance.setMaximumSize(new java.awt.Dimension(128, 38));
    jToolBarPerformance.setMinimumSize(new java.awt.Dimension(128, 38));
    jToolBarPerformance.setPreferredSize(new java.awt.Dimension(128, 38));
    jToolBarPerformance.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        jToolBarPerformanceMouseEntered(evt);
      }
    });

    heapView.setMinimumSize(new java.awt.Dimension(128, 38));
    heapView.setName(""); // NOI18N
    heapView.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        heapViewMouseClicked(evt);
      }
    });
    jToolBarPerformance.add(heapView);

    jPanelToolBar.add(jToolBarPerformance);

    jToolBarPerc.setRollover(true);
    jToolBarPerc.setMaximumSize(new java.awt.Dimension(92, 38));
    jToolBarPerc.setMinimumSize(new java.awt.Dimension(92, 38));
    jToolBarPerc.setName(""); // NOI18N
    jToolBarPerc.setPreferredSize(new java.awt.Dimension(92, 38));

    jPanelPerc.setMaximumSize(new java.awt.Dimension(74, 36));
    jPanelPerc.setMinimumSize(new java.awt.Dimension(74, 36));
    jPanelPerc.setPreferredSize(new java.awt.Dimension(74, 36));
    jToolBarPerc.add(jPanelPerc);

    jPanelToolBar.add(jToolBarPerc);

    jSplitPaneExternal.setToolTipText("");

    jSplitPaneInternal.setResizeWeight(0.5);
    jSplitPaneInternal.setToolTipText("");

    jPanelLeft.setLayout(new java.awt.BorderLayout());

    rSyntaxTextAreaDis.setEditable(false);
    rSyntaxTextAreaDis.setColumns(20);
    rSyntaxTextAreaDis.setRows(5);
    rSyntaxTextAreaDis.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
    rSyntaxTextAreaDis.setName("Dis"); // NOI18N
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

  // force menu key not working otherwise
  rSyntaxTextAreaDis.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, InputEvent.CTRL_MASK),
    new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        execute(MEM_PLUS);
      }
    }
  );

  // force menu key not working otherwise
  rSyntaxTextAreaDis.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, InputEvent.CTRL_MASK),
    new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        execute(MEM_MINUS);
      }
    }
  );
  rSyntaxTextAreaDis.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent evt) {
      rSyntaxTextAreaDisMouseClicked(evt);
    }
    public void mouseEntered(java.awt.event.MouseEvent evt) {
      rSyntaxTextAreaDisMouseEntered(evt);
    }
    public void mouseReleased(java.awt.event.MouseEvent evt) {
      rSyntaxTextAreaDisMouseReleased(evt);
    }
  });
  rSyntaxTextAreaDis.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyReleased(java.awt.event.KeyEvent evt) {
      rSyntaxTextAreaDisKeyReleased(evt);
    }
  });
  jScrollPaneLeft.setViewportView(rSyntaxTextAreaDis);

  jPanelLeft.add(jScrollPaneLeft, java.awt.BorderLayout.CENTER);

  jScrollPaneLeftMin.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
  jScrollPaneLeftMin.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

  rSyntaxTextAreaDisMin.setEditable(false);
  rSyntaxTextAreaDisMin.setColumns(40);
  rSyntaxTextAreaDisMin.setRows(5);
  rSyntaxTextAreaDisMin.setFont(new java.awt.Font("Monospaced", 0, 3)); // NOI18N
  rSyntaxTextAreaDisMin.setMinimumSize(new java.awt.Dimension(671, 1000));
  rSyntaxTextAreaDisMin.setName("Miniature"); // NOI18N
  rSyntaxTextAreaDisMin.setSyntaxEditingStyle("text/asm6502");
  rSyntaxTextAreaDisMin.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK),
    new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        execute(SOURCE_FINDD);
      }
    }
  );
  rSyntaxTextAreaDisMin.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent evt) {
      rSyntaxTextAreaDisMinMouseClicked(evt);
    }
    public void mouseReleased(java.awt.event.MouseEvent evt) {
      rSyntaxTextAreaDisMinMouseReleased(evt);
    }
  });
  jScrollPaneLeftMin.setViewportView(rSyntaxTextAreaDisMin);

  jPanelLeft.add(jScrollPaneLeftMin, java.awt.BorderLayout.EAST);

  jSplitPaneInternal.setLeftComponent(jPanelLeft);

  jPanelRight.setLayout(new java.awt.BorderLayout());

  rSyntaxTextAreaSource.setEditable(false);
  rSyntaxTextAreaSource.setColumns(25);
  rSyntaxTextAreaSource.setRows(5);
  rSyntaxTextAreaSource.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
  rSyntaxTextAreaSource.setName("Source"); // NOI18N
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
    public void mouseEntered(java.awt.event.MouseEvent evt) {
      rSyntaxTextAreaSourceMouseEntered(evt);
    }
    public void mouseReleased(java.awt.event.MouseEvent evt) {
      rSyntaxTextAreaSourceMouseReleased(evt);
    }
  });
  jScrollPaneRight.setViewportView(rSyntaxTextAreaSource);

  jPanelRight.add(jScrollPaneRight, java.awt.BorderLayout.CENTER);

  jScrollPaneRightMin.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
  jScrollPaneRightMin.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

  rSyntaxTextAreaSourceMin.setEditable(false);
  rSyntaxTextAreaSourceMin.setColumns(35);
  rSyntaxTextAreaSourceMin.setRows(5);
  rSyntaxTextAreaSourceMin.setFont(new java.awt.Font("Monospaced", 0, 3)); // NOI18N
  rSyntaxTextAreaSourceMin.setMinimumSize(new java.awt.Dimension(671, 1000));
  rSyntaxTextAreaSourceMin.setName("Miniature"); // NOI18N
  rSyntaxTextAreaSourceMin.setSyntaxEditingStyle("text/asm6502");
  rSyntaxTextAreaSourceMin.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK),
    new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        execute(SOURCE_FINDS);
      }
    }
  );
  rSyntaxTextAreaSourceMin.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent evt) {
      rSyntaxTextAreaSourceMinMouseClicked(evt);
    }
    public void mouseReleased(java.awt.event.MouseEvent evt) {
      rSyntaxTextAreaSourceMinMouseReleased(evt);
    }
  });
  jScrollPaneRightMin.setViewportView(rSyntaxTextAreaSourceMin);

  jPanelRight.add(jScrollPaneRightMin, java.awt.BorderLayout.EAST);

  jSplitPaneInternal.setRightComponent(jPanelRight);

  jSplitPaneExternal.setRightComponent(jSplitPaneInternal);

  jScrollPaneMemory.setPreferredSize(new java.awt.Dimension(200, 403));

  jTableMemory.setModel(dataTableModelMemory);
  jTableMemory.setDefaultRenderer(Integer.class, memoryTableCellRenderer);
  jTableMemory.getColumnModel().getColumn(0).setPreferredWidth(310);

  InputMap im = this.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  ActionMap am = this.getRootPane().getActionMap();

  //add custom action
  im.put(KeyStroke.getKeyStroke("control F"), "find");
  am.put("find", new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent ae) {
      execute(SOURCE_FINDA);
    }
  });

  ((InputMap)UIManager.get("Table.ancestorInputMap")).put(KeyStroke.getKeyStroke("control F"), "none");

  //add custom action
  im.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, InputEvent.ALT_DOWN_MASK), "home");
  am.put("home", new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent ae) {
      altHome();
    }
  });
  im.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, InputEvent.ALT_DOWN_MASK), "end");
  am.put("end", new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent ae) {
      altEnd();
    }
  });

  jTableMemory.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mousePressed(java.awt.event.MouseEvent evt) {
      jTableMemoryMousePressed(evt);
    }
    public void mouseReleased(java.awt.event.MouseEvent evt) {
      jTableMemoryMouseReleased(evt);
    }
  });
  jScrollPaneMemory.setViewportView(jTableMemory);

  jSplitPaneExternal.setLeftComponent(jScrollPaneMemory);

  jMenuBar.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseEntered(java.awt.event.MouseEvent evt) {
      jMenuBarMouseEntered(evt);
    }
  });

  jMenuFile.setText("File");

  jMenuItemNewProject.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemNewProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/filenew.png"))); // NOI18N
  jMenuItemNewProject.setMnemonic('n');
  jMenuItemNewProject.setText("New Project");
  jMenuItemNewProject.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemNewProjectActionPerformed(evt);
    }
  });
  jMenuFile.add(jMenuItemNewProject);
  jMenuFile.add(jSeparatorProject1);

  jMenuItemOpenProject.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemOpenProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/fileopen.png"))); // NOI18N
  jMenuItemOpenProject.setMnemonic('o');
  jMenuItemOpenProject.setText("Open Project");
  jMenuItemOpenProject.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemOpenProjectActionPerformed(evt);
    }
  });
  jMenuFile.add(jMenuItemOpenProject);

  jMenuRecent.setText("Open Recent Project");
  jMenuRecent.addMenuListener(new javax.swing.event.MenuListener() {
    public void menuCanceled(javax.swing.event.MenuEvent evt) {
    }
    public void menuDeselected(javax.swing.event.MenuEvent evt) {
    }
    public void menuSelected(javax.swing.event.MenuEvent evt) {
      jMenuRecentMenuSelected(evt);
    }
  });

  jMenuItemRecent1.setEnabled(false);
  jMenuItemRecent1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemRecent1ActionPerformed(evt);
    }
  });
  jMenuRecent.add(jMenuItemRecent1);

  jMenuItemRecent2.setEnabled(false);
  jMenuItemRecent2.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemRecent2ActionPerformed(evt);
    }
  });
  jMenuRecent.add(jMenuItemRecent2);

  jMenuItemRecent3.setEnabled(false);
  jMenuItemRecent3.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemRecent3ActionPerformed(evt);
    }
  });
  jMenuRecent.add(jMenuItemRecent3);

  jMenuItemRecent4.setEnabled(false);
  jMenuItemRecent4.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemRecent4ActionPerformed(evt);
    }
  });
  jMenuRecent.add(jMenuItemRecent4);

  jMenuItemRecent5.setEnabled(false);
  jMenuItemRecent5.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemRecent5ActionPerformed(evt);
    }
  });
  jMenuRecent.add(jMenuItemRecent5);

  jMenuItemRecent6.setEnabled(false);
  jMenuItemRecent6.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemRecent6ActionPerformed(evt);
    }
  });
  jMenuRecent.add(jMenuItemRecent6);

  jMenuItemRecent7.setEnabled(false);
  jMenuItemRecent7.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemRecent7ActionPerformed(evt);
    }
  });
  jMenuRecent.add(jMenuItemRecent7);

  jMenuItemRecent8.setEnabled(false);
  jMenuItemRecent8.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemRecent8ActionPerformed(evt);
    }
  });
  jMenuRecent.add(jMenuItemRecent8);

  jMenuItemRecent9.setEnabled(false);
  jMenuItemRecent9.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemRecent9ActionPerformed(evt);
    }
  });
  jMenuRecent.add(jMenuItemRecent9);

  jMenuFile.add(jMenuRecent);

  jMenuItemCloseProject.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemCloseProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/close.png"))); // NOI18N
  jMenuItemCloseProject.setMnemonic('c');
  jMenuItemCloseProject.setText("Close Project");
  jMenuItemCloseProject.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemCloseProjectActionPerformed(evt);
    }
  });
  jMenuFile.add(jMenuItemCloseProject);
  jMenuFile.add(jSeparatorProject2);

  jMenuItemSaveProject.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemSaveProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/filesave.png"))); // NOI18N
  jMenuItemSaveProject.setMnemonic('s');
  jMenuItemSaveProject.setText("Save Project");
  jMenuItemSaveProject.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSaveProjectActionPerformed(evt);
    }
  });
  jMenuFile.add(jMenuItemSaveProject);

  jMenuItemSaveAsProject.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
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

  jMenuItemMPR.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemMPR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/create.png"))); // NOI18N
  jMenuItemMPR.setText("Create a MPR archive");
  jMenuItemMPR.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemMPRActionPerformed(evt);
    }
  });
  jMenuFile.add(jMenuItemMPR);

  jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/merge.png"))); // NOI18N
  jMenuItem2.setMnemonic('r');
  jMenuItem2.setText("Collaborative merge");
  jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItem2ActionPerformed(evt);
    }
  });
  jMenuFile.add(jMenuItem2);

  jMenuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_DOWN_MASK));
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

  jMenuMemory.setText("Memory");

  jMenuItemClearDMem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemClearDMem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/eraser.png"))); // NOI18N
  jMenuItemClearDMem.setMnemonic('a');
  jMenuItemClearDMem.setText("Clear disassembly automatic comment");
  jMenuItemClearDMem.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemClearDMemActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemClearDMem);

  jMenuItemClearUMem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemClearUMem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/eraser1.png"))); // NOI18N
  jMenuItemClearUMem.setMnemonic('m');
  jMenuItemClearUMem.setText("Clear user comment");
  jMenuItemClearUMem.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemClearUMemActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemClearUMem);

  jMenuItemClearDLabel.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemClearDLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/eraser2.png"))); // NOI18N
  jMenuItemClearDLabel.setMnemonic('r');
  jMenuItemClearDLabel.setText("Clear disassembly automatic label");
  jMenuItemClearDLabel.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemClearDLabelActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemClearDLabel);
  jMenuMemory.add(jSeparator4);

  jMenuItemAddComment.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SEMICOLON, 0));
  jMenuItemAddComment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/comm.png"))); // NOI18N
  jMenuItemAddComment.setMnemonic('u');
  jMenuItemAddComment.setText("Add user comment");
  jMenuItemAddComment.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemAddCommentActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemAddComment);

  jMenuItemAddBlock.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SEMICOLON, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemAddBlock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/block.png"))); // NOI18N
  jMenuItemAddBlock.setMnemonic('b');
  jMenuItemAddBlock.setText("Add user block comment");
  jMenuItemAddBlock.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemAddBlockActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemAddBlock);

  jMenuItemUserLabel.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemUserLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/mem2.png"))); // NOI18N
  jMenuItemUserLabel.setMnemonic('l');
  jMenuItemUserLabel.setText("Add user label");
  jMenuItemUserLabel.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemUserLabelActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemUserLabel);

  jMenuItemUserLabelOp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemUserLabelOp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/mem3.png"))); // NOI18N
  jMenuItemUserLabelOp.setMnemonic('n');
  jMenuItemUserLabelOp.setText("Add user label on next address");
  jMenuItemUserLabelOp.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemUserLabelOpActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemUserLabelOp);
  jMenuMemory.add(jSeparator3);

  jMenuItemMarkCode.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemMarkCode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/code.png"))); // NOI18N
  jMenuItemMarkCode.setMnemonic('c');
  jMenuItemMarkCode.setText("Mark as code");
  jMenuItemMarkCode.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemMarkCodeActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemMarkCode);

  jMenuItemMarkData.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemMarkData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/data.png"))); // NOI18N
  jMenuItemMarkData.setMnemonic('d');
  jMenuItemMarkData.setText("Mark as data");
  jMenuItemMarkData.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemMarkDataActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemMarkData);

  jSubMenu.setText("(more specific data selection)");

  jMenuBasic1.setText("Data as Basic");

  jMenuItemBasicNone1.setText("no BASIC");
  jMenuItemBasicNone1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemBasicNone1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemBasicNone1);
  jMenuBasic1.add(jSeparatorStandard1);

  jMenuItemBasicV2_1.setText(BasicType.BASIC_V2_0.getName());
  jMenuItemBasicV2_1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemBasicV2_1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemBasicV2_1);

  jMenuItemBasicV3_5_1.setText(BasicType.BASIC_V3_5.getName());
  jMenuItemBasicV3_5_1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemBasicV3_5_1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemBasicV3_5_1);

  jMenuItemBasicV4_0_1.setText(BasicType.BASIC_V4_0.getName());
  jMenuItemBasicV4_0_1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemBasicV4_0_1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemBasicV4_0_1);

  jMenuItemBasicV7_0_1.setText(BasicType.BASIC_V7_0.getName());
  jMenuItemBasicV7_0_1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemBasicV7_0_1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemBasicV7_0_1);
  jMenuBasic1.add(jSeparatorV3);

  jMenuItemSimon1.setText(BasicType.BASIC_SIMON.getName());
  jMenuItemSimon1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSimon1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemSimon1);

  jMenuItemAndreFachat1.setText(BasicType.BASIC_ANDRE_FACHAT.getName());
  jMenuItemAndreFachat1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemAndreFachat1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemAndreFachat1);

  jMenuItemSpeech1.setText(BasicType.BASIC_SPEECH.getName());
  jMenuItemSpeech1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSpeech1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemSpeech1);

  jMenuItemFinalCart4.setText(BasicType.BASIC_FINAL_CART3.getName());
  jMenuItemFinalCart4.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemFinalCart4ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemFinalCart4);

  jMenuItemUltrabasic1.setText(BasicType.BASIC_ULTRABASIC.getName());
  jMenuItemUltrabasic1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemUltrabasic1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemUltrabasic1);

  jMenuItemGraphics1.setText(BasicType.BASIC_GRAPHICS.getName());
  jMenuItemGraphics1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemGraphics1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemGraphics1);

  jMenuItemWS1.setText(BasicType.BASIC_WS.getName());
  jMenuItemWS1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemWS1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemWS1);

  jMenuItemPegasus1.setText(BasicType.BASIC_PEGASUS.getName());
  jMenuItemPegasus1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemPegasus1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemPegasus1);

  jMenuItemXbasic1.setText(BasicType.BASIC_XBASIC.getName());
  jMenuItemXbasic1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemXbasic1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemXbasic1);

  jMenuItemDrago1.setText(BasicType.BASIC_DRAGO.getName());
  jMenuItemDrago1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemDrago1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemDrago1);

  jMenuItemReu1.setText(BasicType.BASIC_REU.getName());
  jMenuItemReu1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemReu1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemReu1);

  jMenuItemLightninig1.setText(BasicType.BASIC_LIGHTNING.getName());
  jMenuItemLightninig1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemLightninig1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemLightninig1);

  jMenuItemMagic1.setText(BasicType.BASIC_MAGIC.getName());
  jMenuItemMagic1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemMagic1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemMagic1);

  jMenuItemBlark1.setText(BasicType.BASIC_BLARG.getName());
  jMenuItemBlark1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemBlark1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemBlark1);

  jMenuItemWSFinal1.setText(BasicType.BASIC_WS_FINAL.getName());
  jMenuItemWSFinal1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemWSFinal1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemWSFinal1);

  jMenuItemGame1.setText(BasicType.BASIC_GAME.getName());
  jMenuItemGame1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemGame1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemGame1);

  jMenuItemBasex1.setText(BasicType.BASIC_BASEX.getName());
  jMenuItemBasex1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemBasex1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemBasex1);

  jMenuItemSuper1.setText(BasicType.BASIC_SUPER.getName());
  jMenuItemSuper1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSuper1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemSuper1);

  jMenuItemExpanded1.setText(BasicType.BASIC_EXPANDED.getName());
  jMenuItemExpanded1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemExpanded1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemExpanded1);

  jMenuItemSuperExpanderChip1.setText(BasicType.BASIC_SUPER_EXPANDER_CHIP.getName());
  jMenuItemSuperExpanderChip1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSuperExpanderChip1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemSuperExpanderChip1);

  jMenuItemWarsaw1.setText(BasicType.BASIC_WARSAW.getName());
  jMenuItemWarsaw1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemWarsaw1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemWarsaw1);

  jMenuItemDbs1.setText(BasicType.BASIC_DBS.getName());
  jMenuItemDbs1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemDbs1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemDbs1);

  jMenuItemKipper1.setText(BasicType.BASIC_KIPPER.getName());
  jMenuItemKipper1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemKipper1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemKipper1);

  jMenuItemBails1.setText(BasicType.BASIC_BAILS.getName());
  jMenuItemBails1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemBails1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemBails1);

  jMenuItemEve1.setText(BasicType.BASIC_EVE.getName());
  jMenuItemEve1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemEve1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemEve1);

  jMenuItemTool1.setText(BasicType.BASIC_TOOL.getName());
  jMenuItemTool1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemTool1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemTool1);
  jMenuBasic1.add(jSeparatorV129);

  jMenuItemV9.setText(BasicType.BASIC_V8.getName());
  jMenuItemV9.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemV9ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemV9);
  jMenuBasic1.add(jSeparatorV21);

  jMenuItemSuperExpander1.setText(BasicType.BASIC_SUPER_EXPANDER.getName());
  jMenuItemSuperExpander1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSuperExpander1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemSuperExpander1);

  jMenuItemTurtle1.setText(BasicType.BASIC_TURTLE.getName());
  jMenuItemTurtle1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemTurtle1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemTurtle1);

  jMenuItemEasy1.setText(BasicType.BASIC_EASY.getName());
  jMenuItemEasy1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemEasy1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemEasy1);

  jMenuItemV6.setText(BasicType.BASIC_V4.getName());
  jMenuItemV6.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemV6ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemV6);

  jMenuItemV7.setText(BasicType.BASIC_V5.getName());
  jMenuItemV7.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemV7ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemV7);

  jMenuItemExpandedV21.setText(BasicType.BASIC_EXPANDED_V20.getName());
  jMenuItemExpandedV21.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemExpandedV21ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemExpandedV21);

  jMenuItemHandy1.setText(BasicType.BASIC_HANDY.getName());
  jMenuItemHandy1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemHandy1ActionPerformed(evt);
    }
  });
  jMenuBasic1.add(jMenuItemHandy1);

  jSubMenu.add(jMenuBasic1);
  jSubMenu.add(jSeparator7);

  jMenuItemByteHex1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemByteHex1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/B.png"))); // NOI18N
  jMenuItemByteHex1.setMnemonic('b');
  jMenuItemByteHex1.setText("(B) Mark data as Byte (HEX)");
  jMenuItemByteHex1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemByteHex1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemByteHex1);

  jMenuItemByteDec1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemByteDec1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/D.png"))); // NOI18N
  jMenuItemByteDec1.setMnemonic('d');
  jMenuItemByteDec1.setText("(D) Mark data as Byte (DEC)");
  jMenuItemByteDec1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemByteDec1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemByteDec1);

  jMenuItemByteBin1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemByteBin1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/Y.png"))); // NOI18N
  jMenuItemByteBin1.setMnemonic('y');
  jMenuItemByteBin1.setText("(Y) Mark data as Byte (BIN)");
  jMenuItemByteBin1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemByteBin1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemByteBin1);

  jMenuItemByteChar1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemByteChar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/R.png"))); // NOI18N
  jMenuItemByteChar1.setMnemonic('r');
  jMenuItemByteChar1.setText("(R) Mark data as Byte (CHAR)");
  jMenuItemByteChar1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemByteChar1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemByteChar1);
  jSubMenu.add(jSeparatorByte1);

  jMenuItemWord1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemWord1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/W.png"))); // NOI18N
  jMenuItemWord1.setMnemonic('w');
  jMenuItemWord1.setText("(W) Mark data as Word");
  jMenuItemWord1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemWord1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemWord1);

  jMenuItemWordSwapped1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemWordSwapped1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/P.png"))); // NOI18N
  jMenuItemWordSwapped1.setMnemonic('p');
  jMenuItemWordSwapped1.setText("(P) Mark data as Word Swapped");
  jMenuItemWordSwapped1.setToolTipText("");
  jMenuItemWordSwapped1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemWordSwapped1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemWordSwapped1);
  jSubMenu.add(jSeparatorWord1);

  jMenuItemTribyte1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemTribyte1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/E.png"))); // NOI18N
  jMenuItemTribyte1.setText("(E) Mark data as Tribyte");
  jMenuItemTribyte1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemTribyte1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemTribyte1);

  jMenuItemLong1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemLong1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/L.png"))); // NOI18N
  jMenuItemLong1.setMnemonic('l');
  jMenuItemLong1.setText("(L) Mark data as Long");
  jMenuItemLong1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemLong1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemLong1);
  jSubMenu.add(jSeparator5);

  jMenuItemAddress1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemAddress1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/A.png"))); // NOI18N
  jMenuItemAddress1.setMnemonic('a');
  jMenuItemAddress1.setText("(A) Mark data as Address");
  jMenuItemAddress1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemAddress1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemAddress1);

  jMenuItemStackWord1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemStackWord1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/S.png"))); // NOI18N
  jMenuItemStackWord1.setMnemonic('s');
  jMenuItemStackWord1.setText("(S) Mark data as Stack Word");
  jMenuItemStackWord1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemStackWord1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemStackWord1);
  jSubMenu.add(jSeparator6);

  jMenuItemSpriteMono1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemSpriteMono1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/O.png"))); // NOI18N
  jMenuItemSpriteMono1.setMnemonic('o');
  jMenuItemSpriteMono1.setText("(O) Mark data as Monocromatic Sprite definitions");
  jMenuItemSpriteMono1.setToolTipText("");
  jMenuItemSpriteMono1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSpriteMono1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemSpriteMono1);

  jMenuItemSpriteMulti1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemSpriteMulti1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/F.png"))); // NOI18N
  jMenuItemSpriteMulti1.setMnemonic('f');
  jMenuItemSpriteMulti1.setText("(F) Mark data as Multicolor Sprite definitions");
  jMenuItemSpriteMulti1.setToolTipText("");
  jMenuItemSpriteMulti1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSpriteMulti1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemSpriteMulti1);
  jSubMenu.add(jSeparatorSprite1);

  jMenuItemText1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemText1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/T.png"))); // NOI18N
  jMenuItemText1.setMnemonic('t');
  jMenuItemText1.setText("(T) Mark data as Text");
  jMenuItemText1.setToolTipText("");
  jMenuItemText1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemText1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemText1);

  jMenuItemNumText1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemNumText1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/N.png"))); // NOI18N
  jMenuItemNumText1.setMnemonic('n');
  jMenuItemNumText1.setText("(N) Mark data as Text with # chars before");
  jMenuItemNumText1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemNumText1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemNumText1);

  jMenuItemTextZero1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemTextZero1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/Z.png"))); // NOI18N
  jMenuItemTextZero1.setMnemonic('z');
  jMenuItemTextZero1.setText("(Z) Mark data as text terminated with 0");
  jMenuItemTextZero1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemTextZero1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemTextZero1);

  jMenuItemtextHighOne1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemtextHighOne1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/M.png"))); // NOI18N
  jMenuItemtextHighOne1.setMnemonic('m');
  jMenuItemtextHighOne1.setText("(M) Mark data as Text with high bit 1");
  jMenuItemtextHighOne1.setToolTipText("");
  jMenuItemtextHighOne1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemtextHighOne1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemtextHighOne1);

  jMenuItemtextShifted1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemtextShifted1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/H.png"))); // NOI18N
  jMenuItemtextShifted1.setMnemonic('h');
  jMenuItemtextShifted1.setText("(H) Mark data as Text left shifted");
  jMenuItemtextShifted1.setToolTipText("");
  jMenuItemtextShifted1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemtextShifted1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemtextShifted1);

  jMenuItemTextScreen1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemTextScreen1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/C.png"))); // NOI18N
  jMenuItemTextScreen1.setMnemonic('c');
  jMenuItemTextScreen1.setText("(C) Mark data as Text converted to screen code");
  jMenuItemTextScreen1.setToolTipText("");
  jMenuItemTextScreen1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemTextScreen1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemTextScreen1);

  jMenuItemTextPetascii1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemTextPetascii1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/I.png"))); // NOI18N
  jMenuItemTextPetascii1.setMnemonic('i');
  jMenuItemTextPetascii1.setText("(I) Mark data as Text converted to petAscii code");
  jMenuItemTextPetascii1.setToolTipText("");
  jMenuItemTextPetascii1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemTextPetascii1ActionPerformed(evt);
    }
  });
  jSubMenu.add(jMenuItemTextPetascii1);

  jMenuMemory.add(jSubMenu);

  jMenuItemMarkGarbage.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemMarkGarbage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/garb.png"))); // NOI18N
  jMenuItemMarkGarbage.setMnemonic('g');
  jMenuItemMarkGarbage.setText("Mark as garbage");
  jMenuItemMarkGarbage.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemMarkGarbageActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemMarkGarbage);

  jSubMenuC.setText("Sub-mark a cell");

  jMenuItemConstant0b.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemConstant0b.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/0.png"))); // NOI18N
  jMenuItemConstant0b.setText("as of constant 0");
  jMenuItemConstant0b.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant0bActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant0b);

  jMenuItemConstant9b.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_9, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemConstant9b.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/9.png"))); // NOI18N
  jMenuItemConstant9b.setText("as of constant 9");
  jMenuItemConstant9b.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant9bActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant9b);

  jMenuItemConstant1b.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemConstant1b.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/1.png"))); // NOI18N
  jMenuItemConstant1b.setText("as of constant 1");
  jMenuItemConstant1b.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant1bActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant1b);

  jMenuItemConstant2b.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemConstant2b.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/2.png"))); // NOI18N
  jMenuItemConstant2b.setText("as of constant 2");
  jMenuItemConstant2b.setToolTipText("");
  jMenuItemConstant2b.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant2bActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant2b);

  jMenuItemConstant3b.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemConstant3b.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/3.png"))); // NOI18N
  jMenuItemConstant3b.setText("as of constant 3");
  jMenuItemConstant3b.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant3bActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant3b);

  jMenuItemConstant4b.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemConstant4b.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/4.png"))); // NOI18N
  jMenuItemConstant4b.setText("as of constant 4");
  jMenuItemConstant4b.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant4bActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant4b);

  jMenuItemConstant5b.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_5, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemConstant5b.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/5.png"))); // NOI18N
  jMenuItemConstant5b.setText("as of constant 5");
  jMenuItemConstant5b.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant5bActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant5b);

  jMenuItemConstant6b.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_6, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemConstant6b.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/6.png"))); // NOI18N
  jMenuItemConstant6b.setText("as of constant 6");
  jMenuItemConstant6b.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant6bActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant6b);

  jMenuItemConstant7b.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_7, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemConstant7b.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/7.png"))); // NOI18N
  jMenuItemConstant7b.setText("as of constant 7");
  jMenuItemConstant7b.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant7bActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant7b);

  jMenuItemConstant8b.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_8, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemConstant8b.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/8.png"))); // NOI18N
  jMenuItemConstant8b.setText("as of constant 8");
  jMenuItemConstant8b.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant8bActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant8b);
  jSubMenuC.add(jSeparatorConstant3);

  jMenuItemConstantClear.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_BACK_SPACE, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemConstantClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/0-9.png"))); // NOI18N
  jMenuItemConstantClear.setText("reset to none");
  jMenuItemConstantClear.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstantClearActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstantClear);
  jSubMenuC.add(jSeparatorConstant4);

  jMenuItemConstant0b1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/0.png"))); // NOI18N
  jMenuItemConstant0b1.setText("as of constant !");
  jMenuItemConstant0b1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant0b1ActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant0b1);

  jMenuItemConstant1b1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/1.png"))); // NOI18N
  jMenuItemConstant1b1.setText("as of constant \"");
  jMenuItemConstant1b1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant1b1ActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant1b1);

  jMenuItemConstant2b1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/2.png"))); // NOI18N
  jMenuItemConstant2b1.setText("as of constant £");
  jMenuItemConstant2b1.setToolTipText("");
  jMenuItemConstant2b1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant2b1ActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant2b1);

  jMenuItemConstant3b1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/3.png"))); // NOI18N
  jMenuItemConstant3b1.setText("as of constant $");
  jMenuItemConstant3b1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant3b1ActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant3b1);

  jMenuItemConstant4b1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/4.png"))); // NOI18N
  jMenuItemConstant4b1.setText("as of constant %");
  jMenuItemConstant4b1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant4b1ActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant4b1);

  jMenuItemConstant5b1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/5.png"))); // NOI18N
  jMenuItemConstant5b1.setText("as of constant &");
  jMenuItemConstant5b1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant5b1ActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant5b1);

  jMenuItemConstant6b1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/6.png"))); // NOI18N
  jMenuItemConstant6b1.setText("as of constant /");
  jMenuItemConstant6b1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant6b1ActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant6b1);

  jMenuItemConstant7b1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/7.png"))); // NOI18N
  jMenuItemConstant7b1.setText("as of constant (");
  jMenuItemConstant7b1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant7b1ActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant7b1);

  jMenuItemConstant8b1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/8.png"))); // NOI18N
  jMenuItemConstant8b1.setText("as of constant )");
  jMenuItemConstant8b1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant8b1ActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant8b1);

  jMenuItemConstant9b1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/9.png"))); // NOI18N
  jMenuItemConstant9b1.setText("as of constant =");
  jMenuItemConstant9b1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConstant9b1ActionPerformed(evt);
    }
  });
  jSubMenuC.add(jMenuItemConstant9b1);

  jMenuMemory.add(jSubMenuC);

  jMenuItemPlus.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ADD, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemPlus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/plus.png"))); // NOI18N
  jMenuItemPlus.setText("Assign the selected address as +");
  jMenuItemPlus.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemPlusActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemPlus);

  jMenuItemMinus.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SUBTRACT, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemMinus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/minus.png"))); // NOI18N
  jMenuItemMinus.setText("Assign the selected address as -");
  jMenuItemMinus.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemMinusActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemMinus);

  jMenuItemMemLow.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemMemLow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/min.png"))); // NOI18N
  jMenuItemMemLow.setText("Assign the selected address as #<");
  jMenuItemMemLow.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemMemLowActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemMemLow);

  jMenuItemMemLowHigh.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemMemLowHigh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/minmax.png"))); // NOI18N
  jMenuItemMemLowHigh.setText("Assign the 2 selected addresses as #<>");
  jMenuItemMemLowHigh.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemMemLowHighActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemMemLowHigh);

  jMenuItemMemBoth.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemMemBoth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/minmax.png"))); // NOI18N
  jMenuItemMemBoth.setText("Assign the 2 tables as #<>");
  jMenuItemMemBoth.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemMemBothActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemMemBoth);

  jMenuItemMemHighLow.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemMemHighLow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/maxmin.png"))); // NOI18N
  jMenuItemMemHighLow.setText("Assign the 2 selected addresses as #><");
  jMenuItemMemHighLow.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemMemHighLowActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemMemHighLow);

  jMenuItemMemHigh.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemMemHigh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/max.png"))); // NOI18N
  jMenuItemMemHigh.setText("Assign the selected address as #>");
  jMenuItemMemHigh.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemMemHighActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemMemHigh);

  jMenuItemWizard.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemWizard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/wizard.png"))); // NOI18N
  jMenuItemWizard.setText("Assign using wizard");
  jMenuItemWizard.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemWizardActionPerformed(evt);
    }
  });
  jMenuMemory.add(jMenuItemWizard);

  jMenuBar.add(jMenuMemory);

  jMenuOption.setText("Option");
  jMenuOption.setToolTipText("");

  jMenuItemConfigure.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemConfigure.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/configure.png"))); // NOI18N
  jMenuItemConfigure.setMnemonic('o');
  jMenuItemConfigure.setText("General Option");
  jMenuItemConfigure.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemConfigureActionPerformed(evt);
    }
  });
  jMenuOption.add(jMenuItemConfigure);

  jMenuItemSIDLD.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemSIDLD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/mem.png"))); // NOI18N
  jMenuItemSIDLD.setMnemonic('e');
  jMenuItemSIDLD.setText("Apply SIDLD flags to memory");
  jMenuItemSIDLD.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSIDLDActionPerformed(evt);
    }
  });
  jMenuOption.add(jMenuItemSIDLD);
  jMenuOption.add(jSeparatorOption);

  jMenuItemViewProject.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_J, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemViewProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/view_detailed.png"))); // NOI18N
  jMenuItemViewProject.setMnemonic('j');
  jMenuItemViewProject.setText("View Project");
  jMenuItemViewProject.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemViewProjectActionPerformed(evt);
    }
  });
  jMenuOption.add(jMenuItemViewProject);

  jMenuItemViewLabels.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemViewLabels.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/view_label.png"))); // NOI18N
  jMenuItemViewLabels.setMnemonic('b');
  jMenuItemViewLabels.setText("View labels");
  jMenuItemViewLabels.setToolTipText("");
  jMenuItemViewLabels.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemViewLabelsActionPerformed(evt);
    }
  });
  jMenuOption.add(jMenuItemViewLabels);

  jMenuBar.add(jMenuOption);

  jMenuSource.setText("Source");

  jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/finda.png"))); // NOI18N
  jMenuItem1.setText("Find memory address");
  jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItem1ActionPerformed(evt);
    }
  });
  jMenuSource.add(jMenuItem1);

  jMenuItemDiss.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemDiss.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/exec.png"))); // NOI18N
  jMenuItemDiss.setMnemonic('e');
  jMenuItemDiss.setText("Disassemble");
  jMenuItemDiss.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemDissActionPerformed(evt);
    }
  });
  jMenuSource.add(jMenuItemDiss);

  jMenuItemAssembly.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemAssembly.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/assembler.png"))); // NOI18N
  jMenuItemAssembly.setMnemonic('t');
  jMenuItemAssembly.setText("Assemble");
  jMenuItemAssembly.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemAssemblyActionPerformed(evt);
    }
  });
  jMenuSource.add(jMenuItemAssembly);
  jMenuSource.add(jSeparator1);

  jMenuItemFindDis.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemFindDis.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/findd.png"))); // NOI18N
  jMenuItemFindDis.setText("Find text in preview");
  jMenuItemFindDis.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemFindDisActionPerformed(evt);
    }
  });
  jMenuSource.add(jMenuItemFindDis);

  jMenuItemDissSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemDissSaveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/exportas1.png"))); // NOI18N
  jMenuItemDissSaveAs.setMnemonic('p');
  jMenuItemDissSaveAs.setText("Export As of preview");
  jMenuItemDissSaveAs.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemDissSaveAsActionPerformed(evt);
    }
  });
  jMenuSource.add(jMenuItemDissSaveAs);
  jMenuSource.add(jSeparator2);

  jMenuItemFindSource.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemFindSource.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/finds.png"))); // NOI18N
  jMenuItemFindSource.setText("Find text in source");
  jMenuItemFindSource.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemFindSourceActionPerformed(evt);
    }
  });
  jMenuSource.add(jMenuItemFindSource);

  jMenuItemSourceSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemSourceSaveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/exportas2.png"))); // NOI18N
  jMenuItemSourceSaveAs.setMnemonic('s');
  jMenuItemSourceSaveAs.setText("Export As of source");
  jMenuItemSourceSaveAs.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSourceSaveAsActionPerformed(evt);
    }
  });
  jMenuSource.add(jMenuItemSourceSaveAs);

  jMenuSub.setText("(more specific export)");

  jMenuItemSaveAsDasm1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0, java.awt.event.InputEvent.ALT_DOWN_MASK));
  jMenuItemSaveAsDasm1.setText("Save in Dasm format");
  jMenuItemSaveAsDasm1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSaveAsDasm1ActionPerformed(evt);
    }
  });
  jMenuSub.add(jMenuItemSaveAsDasm1);

  jMenuItemSaveAsTmpx1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.ALT_DOWN_MASK));
  jMenuItemSaveAsTmpx1.setText("Save in TMPx format");
  jMenuItemSaveAsTmpx1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSaveAsTmpx1ActionPerformed(evt);
    }
  });
  jMenuSub.add(jMenuItemSaveAsTmpx1);

  jMenuItemSaveAsCa65_1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.ALT_DOWN_MASK));
  jMenuItemSaveAsCa65_1.setText("Save in CA65 format");
  jMenuItemSaveAsCa65_1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSaveAsCa65_1ActionPerformed(evt);
    }
  });
  jMenuSub.add(jMenuItemSaveAsCa65_1);

  jMenuItemSaveAsAcme1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.ALT_DOWN_MASK));
  jMenuItemSaveAsAcme1.setText("Save in Acme format");
  jMenuItemSaveAsAcme1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSaveAsAcme1ActionPerformed(evt);
    }
  });
  jMenuSub.add(jMenuItemSaveAsAcme1);

  jMenuItemSaveAsKickAssembler1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.ALT_DOWN_MASK));
  jMenuItemSaveAsKickAssembler1.setText("Save in KickAssembler format");
  jMenuItemSaveAsKickAssembler1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSaveAsKickAssembler1ActionPerformed(evt);
    }
  });
  jMenuSub.add(jMenuItemSaveAsKickAssembler1);

  jMenuItemSaveAsTass64_1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_5, java.awt.event.InputEvent.ALT_DOWN_MASK));
  jMenuItemSaveAsTass64_1.setText("Save in Tass64 format");
  jMenuItemSaveAsTass64_1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSaveAsTass64_1ActionPerformed(evt);
    }
  });
  jMenuSub.add(jMenuItemSaveAsTass64_1);

  jMenuItemSaveAsGlass1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_6, java.awt.event.InputEvent.ALT_DOWN_MASK));
  jMenuItemSaveAsGlass1.setText("Save in Glass format");
  jMenuItemSaveAsGlass1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSaveAsGlass1ActionPerformed(evt);
    }
  });
  jMenuSub.add(jMenuItemSaveAsGlass1);

  jMenuItemSaveAsAS1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_7, java.awt.event.InputEvent.ALT_DOWN_MASK));
  jMenuItemSaveAsAS1.setText("Save in AS format");
  jMenuItemSaveAsAS1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSaveAsAS1ActionPerformed(evt);
    }
  });
  jMenuSub.add(jMenuItemSaveAsAS1);

  jMenuSource.add(jMenuSub);

  jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/freeze.png"))); // NOI18N
  jMenuItem3.setText("Freeze source");
  jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItem3ActionPerformed(evt);
    }
  });
  jMenuSource.add(jMenuItem3);

  jMenuBar.add(jMenuSource);

  jMenuHelpContents.setText("Help");

  jMenuItemContents.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
  jMenuItemContents.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sw_emulator/swing/icons/mini/help_index.png"))); // NOI18N
  jMenuItemContents.setMnemonic('h');
  jMenuItemContents.setText("Help contents");
  jMenuItemContents.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemContentsActionPerformed(evt);
    }
  });
  jMenuHelpContents.add(jMenuItemContents);
  jMenuHelpContents.add(jSeparatorHelp1);

  jMenuItemLicense.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemLicense.setMnemonic('l');
  jMenuItemLicense.setText("License");
  jMenuItemLicense.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemLicenseActionPerformed(evt);
    }
  });
  jMenuHelpContents.add(jMenuItemLicense);

  jMenuItemCredits.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemCredits.setMnemonic('d');
  jMenuItemCredits.setText("Credits");
  jMenuItemCredits.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemCreditsActionPerformed(evt);
    }
  });
  jMenuHelpContents.add(jMenuItemCredits);

  jMenuItemAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemAbout.setMnemonic('a');
  jMenuItemAbout.setText("About");
  jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemAboutActionPerformed(evt);
    }
  });
  jMenuHelpContents.add(jMenuItemAbout);
  jMenuHelpContents.add(jSeparatorHelp2);

  jMenuItemImportLabels.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemImportLabels.setMnemonic('i');
  jMenuItemImportLabels.setText("Import labels");
  jMenuItemImportLabels.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemImportLabelsActionPerformed(evt);
    }
  });
  jMenuHelpContents.add(jMenuItemImportLabels);

  jMenuItemRefactorLabels.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemRefactorLabels.setMnemonic('t');
  jMenuItemRefactorLabels.setText("Refactor labels");
  jMenuItemRefactorLabels.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemRefactorLabelsActionPerformed(evt);
    }
  });
  jMenuHelpContents.add(jMenuItemRefactorLabels);

  jMenuItemAutComment.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemAutComment.setText("Clear automatic  comment");
  jMenuItemAutComment.setActionCommand("Clear automatic comment");
  jMenuItemAutComment.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemAutCommentActionPerformed(evt);
    }
  });
  jMenuHelpContents.add(jMenuItemAutComment);

  jMenuItemAutLabel.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
  jMenuItemAutLabel.setText("Clear automatic label");
  jMenuItemAutLabel.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemAutLabelActionPerformed(evt);
    }
  });
  jMenuHelpContents.add(jMenuItemAutLabel);

  jMenuItemSidldPlayer.setText("SIDld player");
  jMenuItemSidldPlayer.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemSidldPlayerActionPerformed(evt);
    }
  });
  jMenuHelpContents.add(jMenuItemSidldPlayer);
  jMenuHelpContents.add(jSeparatorHelp3);

  jMenuUndo.setText("Undo");
  jMenuUndo.addMenuListener(new javax.swing.event.MenuListener() {
    public void menuCanceled(javax.swing.event.MenuEvent evt) {
    }
    public void menuDeselected(javax.swing.event.MenuEvent evt) {
    }
    public void menuSelected(javax.swing.event.MenuEvent evt) {
      jMenuUndoMenuSelected(evt);
    }
  });

  jMenuItemUndo1.setEnabled(false);
  jMenuItemUndo1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemUndo1ActionPerformed(evt);
    }
  });
  jMenuUndo.add(jMenuItemUndo1);

  jMenuItemUndo2.setToolTipText("");
  jMenuItemUndo2.setEnabled(false);
  jMenuItemUndo2.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemUndo2ActionPerformed(evt);
    }
  });
  jMenuUndo.add(jMenuItemUndo2);

  jMenuItemUndo3.setEnabled(false);
  jMenuItemUndo3.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemUndo3ActionPerformed(evt);
    }
  });
  jMenuUndo.add(jMenuItemUndo3);

  jMenuItemUndo4.setEnabled(false);
  jMenuItemUndo4.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemUndo4ActionPerformed(evt);
    }
  });
  jMenuUndo.add(jMenuItemUndo4);

  jMenuItemUndo5.setToolTipText("");
  jMenuItemUndo5.setEnabled(false);
  jMenuItemUndo5.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemUndo5ActionPerformed(evt);
    }
  });
  jMenuUndo.add(jMenuItemUndo5);

  jMenuItemUndo6.setToolTipText("");
  jMenuItemUndo6.setEnabled(false);
  jMenuItemUndo6.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemUndo6ActionPerformed(evt);
    }
  });
  jMenuUndo.add(jMenuItemUndo6);

  jMenuItemUndo7.setEnabled(false);
  jMenuItemUndo7.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemUndo7ActionPerformed(evt);
    }
  });
  jMenuUndo.add(jMenuItemUndo7);

  jMenuItemUndo8.setEnabled(false);
  jMenuItemUndo8.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemUndo8ActionPerformed(evt);
    }
  });
  jMenuUndo.add(jMenuItemUndo8);

  jMenuItemUndo9.setToolTipText("");
  jMenuItemUndo9.setEnabled(false);
  jMenuItemUndo9.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      jMenuItemUndo9ActionPerformed(evt);
    }
  });
  jMenuUndo.add(jMenuItemUndo9);

  jMenuHelpContents.add(jMenuUndo);

  jMenuBar.add(jMenuHelpContents);

  setJMenuBar(jMenuBar);

  javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
  getContentPane().setLayout(layout);
  layout.setHorizontalGroup(
    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addComponent(jSplitPaneExternal)
    .addComponent(jPanelToolBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
  );
  layout.setVerticalGroup(
    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addGroup(layout.createSequentialGroup()
      .addComponent(jPanelToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
      .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
      .addComponent(jSplitPaneExternal, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE))
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
      if (option.forceCompilation) disassembly(true);
    }//GEN-LAST:event_jButtonSIDLDActionPerformed

    private void jMenuItemSIDLDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSIDLDActionPerformed
      execute(OPTION_SIDLD);
      if (option.forceCompilation) disassembly(true);
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
      gotoMem(rSyntaxTextAreaDis, disassembly.caretsPreview, evt.getModifiersEx());
      
      if (evt.getClickCount() == 2 && !evt.isConsumed() && 
          ((evt.getModifiersEx() & ALT_DOWN_MASK) == ALT_DOWN_MASK)) {
        this.gotoLabelOp();        
        return;
      }      
      
      if (evt.getClickCount() == 2 && !evt.isConsumed() && 
          ((evt.getModifiersEx() & CTRL_DOWN_MASK) == CTRL_DOWN_MASK)) {
        manageAction(disassembly.caretsPreview.getType(rSyntaxTextAreaDis.getCaretPosition()));
      }
      rSyntaxTextAreaDisMin.setCaretPosition(rSyntaxTextAreaDis.getCaretPosition());
      
      // scroll to miniature position
      rSyntaxTextAreaDisMin.requestFocusInWindow();    
      
      // scrolla back to area otherwise focus stay on miniature
      rSyntaxTextAreaDis.requestFocusInWindow();    
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
      gotoMemSource(evt);
      if (evt.getClickCount() == 2 && !evt.isConsumed() && 
          ((evt.getModifiersEx() & CTRL_DOWN_MASK) == CTRL_DOWN_MASK)) {
        manageAction(disassembly.caretsSource.getType(rSyntaxTextAreaSource.getCaretPosition()));
      }
      rSyntaxTextAreaSourceMin.setCaretPosition(rSyntaxTextAreaSource.getCaretPosition());
      
      // scroll to miniature position
      rSyntaxTextAreaSourceMin.requestFocusInWindow(); 
            
      // scrolla back to area otherwise focus stay on miniature
      rSyntaxTextAreaSource.requestFocusInWindow(); 
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

    private void jMenuItemMarkGarbageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMarkGarbageActionPerformed
      execute(MEM_MARKGARB);
    }//GEN-LAST:event_jMenuItemMarkGarbageActionPerformed

    private void rSyntaxTextAreaDisMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rSyntaxTextAreaDisMouseReleased
      String selected=rSyntaxTextAreaDis.getSelectedText();
      
      int actual;
      
      int min=0xffff+1;  // min find address
      int max=-1;        // max find address
      
      // avoid no selected text
      if (selected==null) return;
      
      try {
        String lines[] = selected.split("\\r?\\n");
        for (String line: lines) {
          actual=searchAddress(line.substring(0, Math.min(line.length(), option.maxLabelLength)));  
          if (actual==-1) continue;
        
          if (actual<min) min=actual;
          if (actual>max) max=actual;
        }
      
        // if max is not -1 we find a range
        if (max==-1) return;        
        
        // try with carets if first line were partially covered (only with a selection of more lines)
        MemoryDasm mem= disassembly.caretsPreview.getMemory(rSyntaxTextAreaDis.getCaretPosition()-selected.length());
        if (mem != null && mem.address<min && selected.contains("\n")) min=mem.address;        
      
        //scroll to that point
        ///jTableMemory.scrollRectToVisible(jTableMemory.getCellRect(min,0, true)); 
        Shared.scrollToCenter(jTableMemory, min, 0);
        
        // select those rows
        jTableMemory.setRowSelectionInterval(min, max);       
        
      } catch (Exception e) {
          System.err.println(e);
        }  
    }//GEN-LAST:event_rSyntaxTextAreaDisMouseReleased

    private void rSyntaxTextAreaSourceMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rSyntaxTextAreaSourceMouseReleased
      String selected=rSyntaxTextAreaSource.getSelectedText();
      
      int actual;
      
      int min=0xffff+1;  // min find address
      int max=-1;        // max find address
      
      // avoid no selected text
      if (selected==null) return;
      
      try {
        String lines[] = selected.split("\\r?\\n");
        for (String line: lines) {
          actual=searchAddress(line.substring(0, Math.min(line.length(), option.maxLabelLength)));  
          if (actual==-1) continue;
        
          if (actual<min) min=actual;
          if (actual>max) max=actual;
        }
      
        // if max is not -1 we find a range
        if (max==-1) return;
      
        //scroll to that point
        ///jTableMemory.scrollRectToVisible(jTableMemory.getCellRect(min,0, true)); 
        Shared.scrollToCenter(jTableMemory, min, 0);
        
        // select this rows
        jTableMemory.setRowSelectionInterval(min, max);        
      } catch (Exception e) {
          System.err.println(e);;
        }  
    }//GEN-LAST:event_rSyntaxTextAreaSourceMouseReleased

    private void jButtonMergeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMergeActionPerformed
      execute(PROJ_MERGE);
    }//GEN-LAST:event_jButtonMergeActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
      execute(PROJ_MERGE);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jButtonMarkDataMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonMarkDataMouseEntered
      jPopupMenuData.show((JComponent)evt.getSource(), 0, ((JComponent)evt.getSource()).getHeight());
    }//GEN-LAST:event_jButtonMarkDataMouseEntered

    private void jButtonMarkCodeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonMarkCodeMouseEntered
      if (jPopupMenuData.isShowing()) jPopupMenuData.setVisible(false);
    }//GEN-LAST:event_jButtonMarkCodeMouseEntered

    private void jButtonMarkGarbageMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonMarkGarbageMouseEntered
      if (jPopupMenuData.isShowing()) jPopupMenuData.setVisible(false);
      if (jPopupMenuConstant.isShowing()) jPopupMenuConstant.setVisible(false);
      if (jPopupMenuSaveAs.isShowing()) jPopupMenuSaveAs.setVisible(false);     
    }//GEN-LAST:event_jButtonMarkGarbageMouseEntered

    private void jMenuBarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuBarMouseEntered
      if (jPopupMenuData.isShowing()) jPopupMenuData.setVisible(false);
      if (jPopupMenuConstant.isShowing()) jPopupMenuConstant.setVisible(false);
      if (jPopupMenuSaveAs.isShowing()) jPopupMenuSaveAs.setVisible(false);     
    }//GEN-LAST:event_jMenuBarMouseEntered

    private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered
      if (jPopupMenuData.isShowing()) jPopupMenuData.setVisible(false);
      if (jPopupMenuConstant.isShowing()) jPopupMenuConstant.setVisible(false);
      if (jPopupMenuSaveAs.isShowing()) jPopupMenuSaveAs.setVisible(false);     
    }//GEN-LAST:event_formMouseEntered

    private void rSyntaxTextAreaDisMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rSyntaxTextAreaDisMouseEntered
      if (jPopupMenuData.isShowing()) jPopupMenuData.setVisible(false);
      if (jPopupMenuConstant.isShowing()) jPopupMenuConstant.setVisible(false);
      if (jPopupMenuSaveAs.isShowing()) jPopupMenuSaveAs.setVisible(false);     
    }//GEN-LAST:event_rSyntaxTextAreaDisMouseEntered

    private void rSyntaxTextAreaSourceMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rSyntaxTextAreaSourceMouseEntered
      if (jPopupMenuData.isShowing()) jPopupMenuData.setVisible(false);
      if (jPopupMenuConstant.isShowing()) jPopupMenuConstant.setVisible(false);
      if (jPopupMenuSaveAs.isShowing()) jPopupMenuSaveAs.setVisible(false);  
    }//GEN-LAST:event_rSyntaxTextAreaSourceMouseEntered

    private void jMenuItemByteHexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemByteHexActionPerformed
      execute(MEM_MARKDATA_B);
    }//GEN-LAST:event_jMenuItemByteHexActionPerformed

    private void jMenuItemWordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWordActionPerformed
      execute(MEM_MARKDATA_W);     
    }//GEN-LAST:event_jMenuItemWordActionPerformed

    private void jMenuItemLongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLongActionPerformed
      execute(MEM_MARKDATA_L); 
    }//GEN-LAST:event_jMenuItemLongActionPerformed

    private void jMenuItemAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddressActionPerformed
      execute(MEM_MARKDATA_A);
    }//GEN-LAST:event_jMenuItemAddressActionPerformed

    private void jMenuItemStackWordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStackWordActionPerformed
      execute(MEM_MARKDATA_S);
    }//GEN-LAST:event_jMenuItemStackWordActionPerformed

    private void jMenuItemTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTextActionPerformed
      execute(MEM_MARKDATA_T);
    }//GEN-LAST:event_jMenuItemTextActionPerformed

    private void jMenuItemNumTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNumTextActionPerformed
      execute(MEM_MARKDATA_N);
    }//GEN-LAST:event_jMenuItemNumTextActionPerformed

    private void jMenuItemTextZeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTextZeroActionPerformed
      execute(MEM_MARKDATA_Z);
    }//GEN-LAST:event_jMenuItemTextZeroActionPerformed

    private void jMenuItemtextHighOneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemtextHighOneActionPerformed
      execute(MEM_MARKDATA_M);
    }//GEN-LAST:event_jMenuItemtextHighOneActionPerformed

    private void jMenuItemtextShiftedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemtextShiftedActionPerformed
      execute(MEM_MARKDATA_H);
    }//GEN-LAST:event_jMenuItemtextShiftedActionPerformed

    private void jMenuItemTextScreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTextScreenActionPerformed
      execute(MEM_MARKDATA_C); 
    }//GEN-LAST:event_jMenuItemTextScreenActionPerformed

    private void jMenuItemByteHex1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemByteHex1ActionPerformed
      execute(MEM_MARKDATA_B);
    }//GEN-LAST:event_jMenuItemByteHex1ActionPerformed

    private void jMenuItemWord1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWord1ActionPerformed
      execute(MEM_MARKDATA_W);      
    }//GEN-LAST:event_jMenuItemWord1ActionPerformed

    private void jMenuItemLong1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLong1ActionPerformed
      execute(MEM_MARKDATA_L);
    }//GEN-LAST:event_jMenuItemLong1ActionPerformed

    private void jMenuItemAddress1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddress1ActionPerformed
      execute(MEM_MARKDATA_A);       
    }//GEN-LAST:event_jMenuItemAddress1ActionPerformed

    private void jMenuItemStackWord1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStackWord1ActionPerformed
      execute(MEM_MARKDATA_S);
    }//GEN-LAST:event_jMenuItemStackWord1ActionPerformed

    private void jMenuItemText1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemText1ActionPerformed
      execute(MEM_MARKDATA_T);
    }//GEN-LAST:event_jMenuItemText1ActionPerformed

    private void jMenuItemNumText1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNumText1ActionPerformed
      execute(MEM_MARKDATA_N);
    }//GEN-LAST:event_jMenuItemNumText1ActionPerformed

    private void jMenuItemTextZero1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTextZero1ActionPerformed
      execute(MEM_MARKDATA_Z);
    }//GEN-LAST:event_jMenuItemTextZero1ActionPerformed

    private void jMenuItemtextHighOne1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemtextHighOne1ActionPerformed
      execute(MEM_MARKDATA_M);
    }//GEN-LAST:event_jMenuItemtextHighOne1ActionPerformed

    private void jMenuItemtextShifted1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemtextShifted1ActionPerformed
      execute(MEM_MARKDATA_H);
    }//GEN-LAST:event_jMenuItemtextShifted1ActionPerformed

    private void jMenuItemTextScreen1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTextScreen1ActionPerformed
      execute(MEM_MARKDATA_C);
    }//GEN-LAST:event_jMenuItemTextScreen1ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
      execute(APP_EXIT);
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItemWordSwappedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWordSwappedActionPerformed
      execute(MEM_MARKDATA_P);  
    }//GEN-LAST:event_jMenuItemWordSwappedActionPerformed

    private void jMenuItemWordSwapped1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWordSwapped1ActionPerformed
      execute(MEM_MARKDATA_P);  
    }//GEN-LAST:event_jMenuItemWordSwapped1ActionPerformed

    private void jMenuItemByteDecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemByteDecActionPerformed
      execute(MEM_MARKDATA_D);
    }//GEN-LAST:event_jMenuItemByteDecActionPerformed

    private void jMenuItemByteBinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemByteBinActionPerformed
      execute(MEM_MARKDATA_Y);  
    }//GEN-LAST:event_jMenuItemByteBinActionPerformed

    private void jMenuItemByteCharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemByteCharActionPerformed
      execute(MEM_MARKDATA_R);  
    }//GEN-LAST:event_jMenuItemByteCharActionPerformed

    private void jMenuItemByteDec1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemByteDec1ActionPerformed
      execute(MEM_MARKDATA_D); 
    }//GEN-LAST:event_jMenuItemByteDec1ActionPerformed

    private void jMenuItemByteBin1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemByteBin1ActionPerformed
      execute(MEM_MARKDATA_Y);  
    }//GEN-LAST:event_jMenuItemByteBin1ActionPerformed

    private void jMenuItemByteChar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemByteChar1ActionPerformed
      execute(MEM_MARKDATA_R);  
    }//GEN-LAST:event_jMenuItemByteChar1ActionPerformed

    private void jMenuItemSpriteMonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSpriteMonoActionPerformed
      execute(MEM_MARKDATA_O);  
    }//GEN-LAST:event_jMenuItemSpriteMonoActionPerformed

    private void jMenuItemSpriteMultiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSpriteMultiActionPerformed
      execute(MEM_MARKDATA_F); 
    }//GEN-LAST:event_jMenuItemSpriteMultiActionPerformed

    private void jMenuItemTextPetascii1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTextPetascii1ActionPerformed
       execute(MEM_MARKDATA_I);  
    }//GEN-LAST:event_jMenuItemTextPetascii1ActionPerformed

    private void jMenuItemTextPetasciiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTextPetasciiActionPerformed
      execute(MEM_MARKDATA_I); 
    }//GEN-LAST:event_jMenuItemTextPetasciiActionPerformed

    private void jMenuItemTribyteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTribyteActionPerformed
      execute(MEM_MARKDATA_E);  
    }//GEN-LAST:event_jMenuItemTribyteActionPerformed

    private void jMenuItemTribyte1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTribyte1ActionPerformed
      execute(MEM_MARKDATA_E); 
    }//GEN-LAST:event_jMenuItemTribyte1ActionPerformed

    private void jMenuItemSpriteMono1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSpriteMono1ActionPerformed
      execute(MEM_MARKDATA_O); 
    }//GEN-LAST:event_jMenuItemSpriteMono1ActionPerformed

    private void jMenuItemSpriteMulti1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSpriteMulti1ActionPerformed
      execute(MEM_MARKDATA_F);
    }//GEN-LAST:event_jMenuItemSpriteMulti1ActionPerformed

    private void jButtonAssembleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAssembleActionPerformed
      execute(SOURCE_ASS);
    }//GEN-LAST:event_jButtonAssembleActionPerformed

    private void jMenuItemAssemblyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAssemblyActionPerformed
      execute(SOURCE_ASS);
    }//GEN-LAST:event_jMenuItemAssemblyActionPerformed

    private void jButtonMarkLowHighActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMarkLowHighActionPerformed
      execute(MEM_LOWHIGH);
    }//GEN-LAST:event_jButtonMarkLowHighActionPerformed

    private void jMenuItemMemLowHighActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMemLowHighActionPerformed
      execute(MEM_LOWHIGH);
    }//GEN-LAST:event_jMenuItemMemLowHighActionPerformed

    private void jMenuItemMemHighLowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMemHighLowActionPerformed
      execute(MEM_HIGHLOW);
    }//GEN-LAST:event_jMenuItemMemHighLowActionPerformed

    private void jButtonMarkHighLowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMarkHighLowActionPerformed
      execute(MEM_HIGHLOW);
    }//GEN-LAST:event_jButtonMarkHighLowActionPerformed

    private void jButtonMarkConstantMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonMarkConstantMouseEntered
      jPopupMenuConstant.show((JComponent)evt.getSource(), 0, ((JComponent)evt.getSource()).getHeight());
    }//GEN-LAST:event_jButtonMarkConstantMouseEntered

    private void jButtonMarkConstantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMarkConstantActionPerformed
      execute(MEM_SUB_CLEAR);
    }//GEN-LAST:event_jButtonMarkConstantActionPerformed

    private void jButtonMarkPlusMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonMarkPlusMouseEntered
      if (jPopupMenuConstant.isShowing()) jPopupMenuConstant.setVisible(false);  
    }//GEN-LAST:event_jButtonMarkPlusMouseEntered

    private void jMenuItemConstant0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant0ActionPerformed
      execute(MEM_SUB_0);  
    }//GEN-LAST:event_jMenuItemConstant0ActionPerformed

    private void jMenuItemConstant1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant1ActionPerformed
      execute(MEM_SUB_1);  
    }//GEN-LAST:event_jMenuItemConstant1ActionPerformed

    private void jMenuItemConstant2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant2ActionPerformed
      execute(MEM_SUB_2); 
    }//GEN-LAST:event_jMenuItemConstant2ActionPerformed

    private void jMenuItemConstant3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant3ActionPerformed
      execute(MEM_SUB_3); 
    }//GEN-LAST:event_jMenuItemConstant3ActionPerformed

    private void jMenuItemConstant4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant4ActionPerformed
      execute(MEM_SUB_4);  
    }//GEN-LAST:event_jMenuItemConstant4ActionPerformed

    private void jMenuItemConstant5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant5ActionPerformed
      execute(MEM_SUB_5); 
    }//GEN-LAST:event_jMenuItemConstant5ActionPerformed

    private void jMenuItemConstant6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant6ActionPerformed
      execute(MEM_SUB_6);  
    }//GEN-LAST:event_jMenuItemConstant6ActionPerformed

    private void jMenuItemConstant7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant7ActionPerformed
      execute(MEM_SUB_7);   
    }//GEN-LAST:event_jMenuItemConstant7ActionPerformed

    private void jMenuItemConstant8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant8ActionPerformed
      execute(MEM_SUB_8);   
    }//GEN-LAST:event_jMenuItemConstant8ActionPerformed

    private void jMenuItemConstant9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant9ActionPerformed
      execute(MEM_SUB_9);
    }//GEN-LAST:event_jMenuItemConstant9ActionPerformed

    private void jMenuItemConstant0bActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant0bActionPerformed
      execute(MEM_SUB_0); 
    }//GEN-LAST:event_jMenuItemConstant0bActionPerformed

    private void jMenuItemConstant1bActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant1bActionPerformed
      execute(MEM_SUB_1);  
    }//GEN-LAST:event_jMenuItemConstant1bActionPerformed

    private void jMenuItemConstant2bActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant2bActionPerformed
      execute(MEM_SUB_2);
    }//GEN-LAST:event_jMenuItemConstant2bActionPerformed

    private void jMenuItemConstant3bActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant3bActionPerformed
      execute(MEM_SUB_3);
    }//GEN-LAST:event_jMenuItemConstant3bActionPerformed

    private void jMenuItemConstant4bActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant4bActionPerformed
      execute(MEM_SUB_4);
    }//GEN-LAST:event_jMenuItemConstant4bActionPerformed

    private void jMenuItemConstant5bActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant5bActionPerformed
      execute(MEM_SUB_5);
    }//GEN-LAST:event_jMenuItemConstant5bActionPerformed

    private void jMenuItemConstant6bActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant6bActionPerformed
      execute(MEM_SUB_6);
    }//GEN-LAST:event_jMenuItemConstant6bActionPerformed

    private void jMenuItemConstant7bActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant7bActionPerformed
      execute(MEM_SUB_7);
    }//GEN-LAST:event_jMenuItemConstant7bActionPerformed

    private void jMenuItemConstant8bActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant8bActionPerformed
      execute(MEM_SUB_8); 
    }//GEN-LAST:event_jMenuItemConstant8bActionPerformed

    private void jMenuItemConstant9bActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant9bActionPerformed
      execute(MEM_SUB_9);   
    }//GEN-LAST:event_jMenuItemConstant9bActionPerformed

    private void jMenuItemConstantClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstantClearActionPerformed
      execute(MEM_SUB_CLEAR); 
    }//GEN-LAST:event_jMenuItemConstantClearActionPerformed

    private void jMenuItemConstantClear_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstantClear_ActionPerformed
      execute(MEM_SUB_CLEAR); 
    }//GEN-LAST:event_jMenuItemConstantClear_ActionPerformed

    private void rSyntaxTextAreaDisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rSyntaxTextAreaDisKeyReleased
        
      // test for down/up without shift
      if ((evt.getKeyCode()==40 || evt.getKeyCode()==38)) {
        if (!evt.isShiftDown()) {
          try {  
            int pos=Utilities.getRowStart(rSyntaxTextAreaDis, rSyntaxTextAreaDis.getCaretPosition());
       
            int addr=searchAddress(rSyntaxTextAreaDis.getDocument().getText(pos,option.maxLabelLength));
        
            if (addr==-1) return;
                
            //scroll to that point
            ///jTableMemory.scrollRectToVisible(jTableMemory.getCellRect(addr,0, true)); 
            Shared.scrollToCenter(jTableMemory, addr, 0);
        
            // select this row
            jTableMemory.setRowSelectionInterval(addr, addr); 
         } catch (Exception e) {
             System.err.println(e);
           }
        } else {
            // this is a selection
            rSyntaxTextAreaDisMouseReleased(null);
          }  
      }
      
    }//GEN-LAST:event_rSyntaxTextAreaDisKeyReleased

    private void jButtonViewLabelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonViewLabelsActionPerformed
      execute(OPTION_LABELS);
    }//GEN-LAST:event_jButtonViewLabelsActionPerformed

    private void jMenuItemViewLabelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemViewLabelsActionPerformed
      execute(OPTION_LABELS);
    }//GEN-LAST:event_jMenuItemViewLabelsActionPerformed

    private void jButtonMarkBothActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMarkBothActionPerformed
      execute(MEM_BOTH);
    }//GEN-LAST:event_jButtonMarkBothActionPerformed

    private void jMenuItemMemBothActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMemBothActionPerformed
      execute(MEM_BOTH);
    }//GEN-LAST:event_jMenuItemMemBothActionPerformed

    private void jButtonWizardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonWizardActionPerformed
      execute(MEM_WIZARD);       
    }//GEN-LAST:event_jButtonWizardActionPerformed

    private void jMenuItemWizardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWizardActionPerformed
      execute(MEM_WIZARD);  
    }//GEN-LAST:event_jMenuItemWizardActionPerformed

    private void jMenuItemSaveAsDasmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsDasmActionPerformed
      execute(SOURCE_DASM);
    }//GEN-LAST:event_jMenuItemSaveAsDasmActionPerformed

    private void jMenuItemSaveAsTmpxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsTmpxActionPerformed
      execute(SOURCE_TMPX);
    }//GEN-LAST:event_jMenuItemSaveAsTmpxActionPerformed

    private void jMenuItemSaveAsCa65ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsCa65ActionPerformed
      execute(SOURCE_CA65);
    }//GEN-LAST:event_jMenuItemSaveAsCa65ActionPerformed

    private void jMenuItemSaveAsAcmeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsAcmeActionPerformed
      execute(SOURCE_ACME);
    }//GEN-LAST:event_jMenuItemSaveAsAcmeActionPerformed

    private void jMenuItemSaveAsKickAssemblerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsKickAssemblerActionPerformed
      execute(SOURCE_KICK);
    }//GEN-LAST:event_jMenuItemSaveAsKickAssemblerActionPerformed

    private void jMenuItemSaveAsTass64ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsTass64ActionPerformed
      execute(SOURCE_TASS64);
    }//GEN-LAST:event_jMenuItemSaveAsTass64ActionPerformed

    private void jButtonExportAsSourceMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonExportAsSourceMouseEntered
      jPopupMenuSaveAs.show((JComponent)evt.getSource(), 0, ((JComponent)evt.getSource()).getHeight());
    }//GEN-LAST:event_jButtonExportAsSourceMouseEntered

    private void jButtonFindSourceMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonFindSourceMouseEntered
      if (jPopupMenuSaveAs.isShowing()) jPopupMenuSaveAs.setVisible(false);  
    }//GEN-LAST:event_jButtonFindSourceMouseEntered

    private void jToolBarPerformanceMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToolBarPerformanceMouseEntered
      if (jPopupMenuSaveAs.isShowing()) jPopupMenuSaveAs.setVisible(false);  
    }//GEN-LAST:event_jToolBarPerformanceMouseEntered

    private void jPanelToolBarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelToolBarMouseEntered
      if (jPopupMenuSaveAs.isShowing()) jPopupMenuSaveAs.setVisible(false);  
    }//GEN-LAST:event_jPanelToolBarMouseEntered

    private void jMenuItemSaveAsDasm1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsDasm1ActionPerformed
      execute(SOURCE_DASM);
    }//GEN-LAST:event_jMenuItemSaveAsDasm1ActionPerformed

    private void jMenuItemSaveAsTmpx1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsTmpx1ActionPerformed
      execute(SOURCE_TMPX);
    }//GEN-LAST:event_jMenuItemSaveAsTmpx1ActionPerformed

    private void jMenuItemSaveAsCa65_1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsCa65_1ActionPerformed
      execute(SOURCE_CA65);
    }//GEN-LAST:event_jMenuItemSaveAsCa65_1ActionPerformed

    private void jMenuItemSaveAsAcme1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsAcme1ActionPerformed
      execute(SOURCE_ACME);
    }//GEN-LAST:event_jMenuItemSaveAsAcme1ActionPerformed

    private void jMenuItemSaveAsKickAssembler1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsKickAssembler1ActionPerformed
      execute(SOURCE_KICK);
    }//GEN-LAST:event_jMenuItemSaveAsKickAssembler1ActionPerformed

    private void jMenuItemSaveAsTass64_1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsTass64_1ActionPerformed
      execute(SOURCE_TASS64);
    }//GEN-LAST:event_jMenuItemSaveAsTass64_1ActionPerformed

    private void jMenuItemImportLabelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemImportLabelsActionPerformed
      execute(HELP_IMPORT);
    }//GEN-LAST:event_jMenuItemImportLabelsActionPerformed

    private void jMenuItemRefactorLabelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRefactorLabelsActionPerformed
      execute(HELP_REFACTOR);
    }//GEN-LAST:event_jMenuItemRefactorLabelsActionPerformed

    private void jMenuRecentMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenuRecentMenuSelected
      execute(PROJ_RECENT);
    }//GEN-LAST:event_jMenuRecentMenuSelected

    private void jMenuItemRecent1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRecent1ActionPerformed
      recent(0);
    }//GEN-LAST:event_jMenuItemRecent1ActionPerformed

    private void jMenuItemRecent2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRecent2ActionPerformed
      recent(1); 
    }//GEN-LAST:event_jMenuItemRecent2ActionPerformed

    private void jMenuItemRecent9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRecent9ActionPerformed
      recent(8);
    }//GEN-LAST:event_jMenuItemRecent9ActionPerformed

    private void jMenuItemRecent4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRecent4ActionPerformed
      recent(3);
    }//GEN-LAST:event_jMenuItemRecent4ActionPerformed

    private void jMenuItemRecent5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRecent5ActionPerformed
      recent(4);
    }//GEN-LAST:event_jMenuItemRecent5ActionPerformed

    private void jMenuItemRecent6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRecent6ActionPerformed
      recent(5);
    }//GEN-LAST:event_jMenuItemRecent6ActionPerformed

    private void jMenuItemRecent7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRecent7ActionPerformed
      recent(6);
    }//GEN-LAST:event_jMenuItemRecent7ActionPerformed

    private void jMenuItemRecent8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRecent8ActionPerformed
      recent(7);
    }//GEN-LAST:event_jMenuItemRecent8ActionPerformed

    private void jMenuItemRecent3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRecent3ActionPerformed
      recent(2);  
    }//GEN-LAST:event_jMenuItemRecent3ActionPerformed

    private void jMenuItemAutLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAutLabelActionPerformed
      execute(HELP_CLEARLAB);
    }//GEN-LAST:event_jMenuItemAutLabelActionPerformed

    private void jMenuItemCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCopyActionPerformed
      execute(APP_COPY);
    }//GEN-LAST:event_jMenuItemCopyActionPerformed

    private void jMenuItemPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPasteActionPerformed
      execute(APP_PASTE);
    }//GEN-LAST:event_jMenuItemPasteActionPerformed

    private void jTableMemoryMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMemoryMousePressed
      if (evt.isPopupTrigger()) jPopupMenuMemory.show(evt.getComponent(),evt.getX(), evt.getY());
    }//GEN-LAST:event_jTableMemoryMousePressed

    private void jTableMemoryMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMemoryMouseReleased
      if (evt.isPopupTrigger()) jPopupMenuMemory.show(evt.getComponent(),evt.getX(), evt.getY());
    }//GEN-LAST:event_jTableMemoryMouseReleased

    private void jMenuItemUndo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUndo1ActionPerformed
      undo(0);
    }//GEN-LAST:event_jMenuItemUndo1ActionPerformed

    private void jMenuItemUndo2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUndo2ActionPerformed
      undo(1);
    }//GEN-LAST:event_jMenuItemUndo2ActionPerformed

    private void jMenuItemUndo3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUndo3ActionPerformed
      undo(2);
    }//GEN-LAST:event_jMenuItemUndo3ActionPerformed

    private void jMenuItemUndo4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUndo4ActionPerformed
      undo(3);
    }//GEN-LAST:event_jMenuItemUndo4ActionPerformed

    private void jMenuItemUndo5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUndo5ActionPerformed
      undo(4);
    }//GEN-LAST:event_jMenuItemUndo5ActionPerformed

    private void jMenuItemUndo6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUndo6ActionPerformed
      undo(5);
    }//GEN-LAST:event_jMenuItemUndo6ActionPerformed

    private void jMenuItemUndo7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUndo7ActionPerformed
      undo(6);
    }//GEN-LAST:event_jMenuItemUndo7ActionPerformed

    private void jMenuItemUndo8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUndo8ActionPerformed
      undo(7);
    }//GEN-LAST:event_jMenuItemUndo8ActionPerformed

    private void jMenuItemUndo9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUndo9ActionPerformed
      undo(8);
    }//GEN-LAST:event_jMenuItemUndo9ActionPerformed

    private void jMenuUndoMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenuUndoMenuSelected
      execute(HELP_UNDO);
    }//GEN-LAST:event_jMenuUndoMenuSelected

    private void heapViewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_heapViewMouseClicked
        // Trigger a gc
            new Runnable() {
            @Override
                public void run() {
                    System.gc();
                    System.runFinalization();
                    System.gc();
                }
            }.run();
    }//GEN-LAST:event_heapViewMouseClicked

    private void jMenuItemAutCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAutCommentActionPerformed
      execute(HELP_CLEARCOM);
    }//GEN-LAST:event_jMenuItemAutCommentActionPerformed

    private void jMenuItemFindAddrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFindAddrActionPerformed
      execute(SOURCE_FINDA);
    }//GEN-LAST:event_jMenuItemFindAddrActionPerformed

    private void jMenuItemFindSeqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFindSeqActionPerformed
      execute(SOURCE_FINDX); 
    }//GEN-LAST:event_jMenuItemFindSeqActionPerformed

    private void jMenuItemCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCodeActionPerformed
      execute(MEM_MARKCODE);
    }//GEN-LAST:event_jMenuItemCodeActionPerformed

    private void jMenuItemDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDataActionPerformed
      execute(MEM_MARKDATA);
    }//GEN-LAST:event_jMenuItemDataActionPerformed

    private void jMenuItemGarbageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGarbageActionPerformed
      execute(MEM_MARKGARB);
    }//GEN-LAST:event_jMenuItemGarbageActionPerformed

    private void jMenuItemByteHex2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemByteHex2ActionPerformed
      execute(MEM_MARKDATA_B);        
    }//GEN-LAST:event_jMenuItemByteHex2ActionPerformed

    private void jMenuItemByteDec2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemByteDec2ActionPerformed
      execute(MEM_MARKDATA_D);
    }//GEN-LAST:event_jMenuItemByteDec2ActionPerformed

    private void jMenuItemByteBin2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemByteBin2ActionPerformed
      execute(MEM_MARKDATA_Y);  
    }//GEN-LAST:event_jMenuItemByteBin2ActionPerformed

    private void jMenuItemByteChar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemByteChar2ActionPerformed
      execute(MEM_MARKDATA_R);          
    }//GEN-LAST:event_jMenuItemByteChar2ActionPerformed

    private void jMenuItemWord2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWord2ActionPerformed
      execute(MEM_MARKDATA_W);        
    }//GEN-LAST:event_jMenuItemWord2ActionPerformed

    private void jMenuItemWordSwapped2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWordSwapped2ActionPerformed
      execute(MEM_MARKDATA_P);          
    }//GEN-LAST:event_jMenuItemWordSwapped2ActionPerformed

    private void jMenuItemTribyte2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTribyte2ActionPerformed
      execute(MEM_MARKDATA_E);         
    }//GEN-LAST:event_jMenuItemTribyte2ActionPerformed

    private void jMenuItemLong2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLong2ActionPerformed
      execute(MEM_MARKDATA_L);        
    }//GEN-LAST:event_jMenuItemLong2ActionPerformed

    private void jMenuItemAddress2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddress2ActionPerformed
      execute(MEM_MARKDATA_A);        
    }//GEN-LAST:event_jMenuItemAddress2ActionPerformed

    private void jMenuItemStackWord2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStackWord2ActionPerformed
      execute(MEM_MARKDATA_S);        
    }//GEN-LAST:event_jMenuItemStackWord2ActionPerformed

    private void jMenuItemSpriteMono2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSpriteMono2ActionPerformed
      execute(MEM_MARKDATA_O);        
    }//GEN-LAST:event_jMenuItemSpriteMono2ActionPerformed

    private void jMenuItemSpriteMulti2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSpriteMulti2ActionPerformed
      execute(MEM_MARKDATA_F);        
    }//GEN-LAST:event_jMenuItemSpriteMulti2ActionPerformed

    private void jMenuItemText2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemText2ActionPerformed
      execute(MEM_MARKDATA_T);   
    }//GEN-LAST:event_jMenuItemText2ActionPerformed

    private void jMenuItemNumText2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNumText2ActionPerformed
      execute(MEM_MARKDATA_N);    
    }//GEN-LAST:event_jMenuItemNumText2ActionPerformed

    private void jMenuItemTextZero2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTextZero2ActionPerformed
      execute(MEM_MARKDATA_Z);        
    }//GEN-LAST:event_jMenuItemTextZero2ActionPerformed

    private void jMenuItemtextHighOne2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemtextHighOne2ActionPerformed
      execute(MEM_MARKDATA_M);        
    }//GEN-LAST:event_jMenuItemtextHighOne2ActionPerformed

    private void jMenuItemtextShifted2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemtextShifted2ActionPerformed
      execute(MEM_MARKDATA_H);        
    }//GEN-LAST:event_jMenuItemtextShifted2ActionPerformed

    private void jMenuItemTextScreen2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTextScreen2ActionPerformed
      execute(MEM_MARKDATA_C);         
    }//GEN-LAST:event_jMenuItemTextScreen2ActionPerformed

    private void jMenuItemTextPetascii2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTextPetascii2ActionPerformed
      execute(MEM_MARKDATA_I);         
    }//GEN-LAST:event_jMenuItemTextPetascii2ActionPerformed

    private void jButtonExportAsSource1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonExportAsSource1MouseEntered
      if (jPopupMenuSaveAs.isShowing()) jPopupMenuSaveAs.setVisible(false); 
    }//GEN-LAST:event_jButtonExportAsSource1MouseEntered

    private void jButtonExportAsSource1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportAsSource1ActionPerformed
      execute(SOURCE_FREEZE);
    }//GEN-LAST:event_jButtonExportAsSource1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
      execute(SOURCE_FREEZE);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItemHexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHexActionPerformed
      execute(MEM_HEX);
    }//GEN-LAST:event_jMenuItemHexActionPerformed

    private void jMenuItemConstant10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant10ActionPerformed
      execute(MEM_SUB_10);
    }//GEN-LAST:event_jMenuItemConstant10ActionPerformed

    private void jMenuItemConstant11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant11ActionPerformed
      execute(MEM_SUB_11);
    }//GEN-LAST:event_jMenuItemConstant11ActionPerformed

    private void jMenuItemConstant12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant12ActionPerformed
      execute(MEM_SUB_12);
    }//GEN-LAST:event_jMenuItemConstant12ActionPerformed

    private void jMenuItemConstant13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant13ActionPerformed
      execute(MEM_SUB_13);
    }//GEN-LAST:event_jMenuItemConstant13ActionPerformed

    private void jMenuItemConstant14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant14ActionPerformed
      execute(MEM_SUB_14);
    }//GEN-LAST:event_jMenuItemConstant14ActionPerformed

    private void jMenuItemConstant15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant15ActionPerformed
      execute(MEM_SUB_15);
    }//GEN-LAST:event_jMenuItemConstant15ActionPerformed

    private void jMenuItemConstant16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant16ActionPerformed
      execute(MEM_SUB_16);
    }//GEN-LAST:event_jMenuItemConstant16ActionPerformed

    private void jMenuItemConstant17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant17ActionPerformed
      execute(MEM_SUB_17);
    }//GEN-LAST:event_jMenuItemConstant17ActionPerformed

    private void jMenuItemConstant18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant18ActionPerformed
      execute(MEM_SUB_18);   
    }//GEN-LAST:event_jMenuItemConstant18ActionPerformed

    private void jMenuItemConstant19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant19ActionPerformed
      execute(MEM_SUB_19);   
    }//GEN-LAST:event_jMenuItemConstant19ActionPerformed

    private void jMenuItemConstant0b1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant0b1ActionPerformed
      execute(MEM_SUB_10);
    }//GEN-LAST:event_jMenuItemConstant0b1ActionPerformed

    private void jMenuItemConstant1b1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant1b1ActionPerformed
      execute(MEM_SUB_11);
    }//GEN-LAST:event_jMenuItemConstant1b1ActionPerformed

    private void jMenuItemConstant2b1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant2b1ActionPerformed
      execute(MEM_SUB_12);
    }//GEN-LAST:event_jMenuItemConstant2b1ActionPerformed

    private void jMenuItemConstant3b1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant3b1ActionPerformed
      execute(MEM_SUB_13);
    }//GEN-LAST:event_jMenuItemConstant3b1ActionPerformed

    private void jMenuItemConstant4b1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant4b1ActionPerformed
      execute(MEM_SUB_14);
    }//GEN-LAST:event_jMenuItemConstant4b1ActionPerformed

    private void jMenuItemConstant5b1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant5b1ActionPerformed
      execute(MEM_SUB_15);
    }//GEN-LAST:event_jMenuItemConstant5b1ActionPerformed

    private void jMenuItemConstant6b1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant6b1ActionPerformed
      execute(MEM_SUB_16);
    }//GEN-LAST:event_jMenuItemConstant6b1ActionPerformed

    private void jMenuItemConstant7b1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant7b1ActionPerformed
      execute(MEM_SUB_17);
    }//GEN-LAST:event_jMenuItemConstant7b1ActionPerformed

    private void jMenuItemConstant8b1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant8b1ActionPerformed
      execute(MEM_SUB_18);
    }//GEN-LAST:event_jMenuItemConstant8b1ActionPerformed

    private void jMenuItemConstant9b1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConstant9b1ActionPerformed
      execute(MEM_SUB_19);
    }//GEN-LAST:event_jMenuItemConstant9b1ActionPerformed

  private void jMenuItemSidldPlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSidldPlayerActionPerformed
     jPlayerDialog.setup(option, project);
     jPlayerDialog.setVisible(true);
  }//GEN-LAST:event_jMenuItemSidldPlayerActionPerformed

  private void rSyntaxTextAreaDisMinMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rSyntaxTextAreaDisMinMouseClicked
    gotoMem(rSyntaxTextAreaDisMin, disassembly.caretsPreview, evt.getModifiersEx());
    rSyntaxTextAreaDis.setCaretPosition(rSyntaxTextAreaDisMin.getCaretPosition());
    rSyntaxTextAreaDis.requestFocusInWindow();    
  }//GEN-LAST:event_rSyntaxTextAreaDisMinMouseClicked

  private void rSyntaxTextAreaDisMinMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rSyntaxTextAreaDisMinMouseReleased
      String selected=rSyntaxTextAreaDisMin.getSelectedText();
      
      int actual;
      
      int min=0xffff+1;  // min find address
      int max=-1;        // max find address
      
      // avoid no selected text
      if (selected==null) return;
      
      try {
        String lines[] = selected.split("\\r?\\n");
        for (String line: lines) {
          actual=searchAddress(line.substring(0, Math.min(line.length(), option.maxLabelLength)));  
          if (actual==-1) continue;
        
          if (actual<min) min=actual;
          if (actual>max) max=actual;
        }
      
        // if max is not -1 we find a range
        if (max==-1) return;
      
        //scroll to that point
        ///jTableMemory.scrollRectToVisible(jTableMemory.getCellRect(min,0, true)); 
        Shared.scrollToCenter(jTableMemory, min, 0);
        
        // select those rows
        jTableMemory.setRowSelectionInterval(min, max);       
        
      } catch (Exception e) {
          System.err.println(e);;
        }  
  }//GEN-LAST:event_rSyntaxTextAreaDisMinMouseReleased

  private void rSyntaxTextAreaSourceMinMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rSyntaxTextAreaSourceMinMouseClicked
    gotoMem(rSyntaxTextAreaSourceMin, disassembly.caretsSource, evt.getModifiersEx());
    rSyntaxTextAreaSource.setCaretPosition(rSyntaxTextAreaSourceMin.getCaretPosition());
    rSyntaxTextAreaSource.requestFocusInWindow();   
  }//GEN-LAST:event_rSyntaxTextAreaSourceMinMouseClicked

  private void rSyntaxTextAreaSourceMinMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rSyntaxTextAreaSourceMinMouseReleased
      String selected=rSyntaxTextAreaSourceMin.getSelectedText();
      
      int actual;
      
      int min=0xffff+1;  // min find address
      int max=-1;        // max find address
      
      // avoid no selected text
      if (selected==null) return;
      
      try {
        String lines[] = selected.split("\\r?\\n");
        for (String line: lines) {
          actual=searchAddress(line.substring(0, Math.min(line.length(), option.maxLabelLength)));  
          if (actual==-1) continue;
        
          if (actual<min) min=actual;
          if (actual>max) max=actual;
        }
      
        // if max is not -1 we find a range
        if (max==-1) return;
      
        //scroll to that point
        ///jTableMemory.scrollRectToVisible(jTableMemory.getCellRect(min,0, true)); 
        Shared.scrollToCenter(jTableMemory, min, 0);
        
        // select this rows
        jTableMemory.setRowSelectionInterval(min, max);        
      } catch (Exception e) {
          System.err.println(e);;
        }  
  }//GEN-LAST:event_rSyntaxTextAreaSourceMinMouseReleased

  private void jMenuItemUnmarkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUnmarkActionPerformed
    execute(MEM_UNMARK);
  }//GEN-LAST:event_jMenuItemUnmarkActionPerformed

  private void jMenuItemBasicNoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBasicNoneActionPerformed
    execute(MEM_BASIC_NONE);
  }//GEN-LAST:event_jMenuItemBasicNoneActionPerformed

  private void jMenuItemBasicV2_0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBasicV2_0ActionPerformed
    execute(MEM_BASIC_V2_0);
  }//GEN-LAST:event_jMenuItemBasicV2_0ActionPerformed

  private void jMenuItemBasicV3_5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBasicV3_5ActionPerformed
    execute(MEM_BASIC_V3_5);
  }//GEN-LAST:event_jMenuItemBasicV3_5ActionPerformed

  private void jMenuItemBasicV4_0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBasicV4_0ActionPerformed
    execute(MEM_BASIC_V4_0);
  }//GEN-LAST:event_jMenuItemBasicV4_0ActionPerformed

  private void jMenuItemBasicV7_0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBasicV7_0ActionPerformed
    execute(MEM_BASIC_V7_0);
  }//GEN-LAST:event_jMenuItemBasicV7_0ActionPerformed

  private void jMenuItemSimonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSimonActionPerformed
    execute(MEM_BASIC_SIMON);
  }//GEN-LAST:event_jMenuItemSimonActionPerformed

  private void jMenuItemAndreFachatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAndreFachatActionPerformed
    execute(MEM_BASIC_ANDRE_FACHAT);
  }//GEN-LAST:event_jMenuItemAndreFachatActionPerformed

  private void jMenuItemSpeechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSpeechActionPerformed
    execute(MEM_BASIC_SPEECH);
  }//GEN-LAST:event_jMenuItemSpeechActionPerformed

  private void jMenuItemFinalCart3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFinalCart3ActionPerformed
    execute(MEM_BASIC_FINAL_CART3);
  }//GEN-LAST:event_jMenuItemFinalCart3ActionPerformed

  private void jMenuItemUltrabasicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUltrabasicActionPerformed
    execute(MEM_BASIC_ULTRABASIC);
  }//GEN-LAST:event_jMenuItemUltrabasicActionPerformed

  private void jMenuItemGraphicsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGraphicsActionPerformed
    execute(MEM_BASIC_GRAPHICS);
  }//GEN-LAST:event_jMenuItemGraphicsActionPerformed
    
  private void jMenuItemWSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWSActionPerformed
    execute(MEM_BASIC_WS);
  }//GEN-LAST:event_jMenuItemWSActionPerformed

  private void jMenuItemPegasusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPegasusActionPerformed
    execute(MEM_BASIC_PEGASUS);
  }//GEN-LAST:event_jMenuItemPegasusActionPerformed

  private void jMenuItemXbasicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemXbasicActionPerformed
    execute(MEM_BASIC_XBASIC);
  }//GEN-LAST:event_jMenuItemXbasicActionPerformed

  private void jMenuItemDragoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDragoActionPerformed
    execute(MEM_BASIC_DRAGO);
  }//GEN-LAST:event_jMenuItemDragoActionPerformed

  private void jMenuItemReuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemReuActionPerformed
    execute(MEM_BASIC_REU);
  }//GEN-LAST:event_jMenuItemReuActionPerformed

  private void jMenuItemLightninigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLightninigActionPerformed
    execute(MEM_BASIC_LIGHTNING);
  }//GEN-LAST:event_jMenuItemLightninigActionPerformed

  private void jMenuItemMagicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMagicActionPerformed
    execute(MEM_BASIC_MAGIC);
  }//GEN-LAST:event_jMenuItemMagicActionPerformed

  private void jMenuItemBlarkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBlarkActionPerformed
    execute(MEM_BASIC_BLARG);
  }//GEN-LAST:event_jMenuItemBlarkActionPerformed

  private void jMenuItemWSFinalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWSFinalActionPerformed
    execute(MEM_BASIC_WS_FINAL);
  }//GEN-LAST:event_jMenuItemWSFinalActionPerformed

  private void jMenuItemGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGameActionPerformed
    execute(MEM_BASIC_GAME);
  }//GEN-LAST:event_jMenuItemGameActionPerformed

  private void jMenuItemBasexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBasexActionPerformed
    execute(MEM_BASIC_BASEX);
  }//GEN-LAST:event_jMenuItemBasexActionPerformed

  private void jMenuItemSuperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSuperActionPerformed
    execute(MEM_BASIC_SUPER);
  }//GEN-LAST:event_jMenuItemSuperActionPerformed

  private void jMenuItemExpandedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExpandedActionPerformed
    execute(MEM_BASIC_EXPANDED);
  }//GEN-LAST:event_jMenuItemExpandedActionPerformed

  private void jMenuItemSuperExpanderChipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSuperExpanderChipActionPerformed
    execute(MEM_BASIC_SUPER_EXPANDER_CHIP);
  }//GEN-LAST:event_jMenuItemSuperExpanderChipActionPerformed

  private void jMenuItemWarsawActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWarsawActionPerformed
    execute(MEM_BASIC_WARSAW);
  }//GEN-LAST:event_jMenuItemWarsawActionPerformed

  private void jMenuItemDbsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDbsActionPerformed
    execute(MEM_BASIC_DBS);
  }//GEN-LAST:event_jMenuItemDbsActionPerformed

  private void jMenuItemKipperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemKipperActionPerformed
    execute(MEM_BASIC_KIPPER);
  }//GEN-LAST:event_jMenuItemKipperActionPerformed

  private void jMenuItemBailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBailsActionPerformed
    execute(MEM_BASIC_BAILS);
  }//GEN-LAST:event_jMenuItemBailsActionPerformed

  private void jMenuItemEveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEveActionPerformed
    execute(MEM_BASIC_EVE);
  }//GEN-LAST:event_jMenuItemEveActionPerformed

  private void jMenuItemToolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemToolActionPerformed
    execute(MEM_BASIC_TOOL);
  }//GEN-LAST:event_jMenuItemToolActionPerformed

  private void jMenuItemSuperExpanderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSuperExpanderActionPerformed
    execute(MEM_BASIC_SUPER_EXPANDER);
  }//GEN-LAST:event_jMenuItemSuperExpanderActionPerformed

  private void jMenuItemTurtleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTurtleActionPerformed
    execute(MEM_BASIC_TURTLE);
  }//GEN-LAST:event_jMenuItemTurtleActionPerformed

  private void jMenuItemEasyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEasyActionPerformed
    execute(MEM_BASIC_EASY);
  }//GEN-LAST:event_jMenuItemEasyActionPerformed

  private void jMenuItemV4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemV4ActionPerformed
    execute(MEM_BASIC_V4);
  }//GEN-LAST:event_jMenuItemV4ActionPerformed

  private void jMenuItemV5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemV5ActionPerformed
    execute(MEM_BASIC_V5);
  }//GEN-LAST:event_jMenuItemV5ActionPerformed

  private void jMenuItemExpandedV20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExpandedV20ActionPerformed
    execute(MEM_BASIC_EXPANDED_V20);
  }//GEN-LAST:event_jMenuItemExpandedV20ActionPerformed

  private void jMenuItemHandyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHandyActionPerformed
    execute(MEM_BASIC_HANDY);
  }//GEN-LAST:event_jMenuItemHandyActionPerformed

  private void jMenuItemBasicNone1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBasicNone1ActionPerformed
    execute(MEM_BASIC_NONE);
  }//GEN-LAST:event_jMenuItemBasicNone1ActionPerformed

  private void jMenuItemBasicV2_1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBasicV2_1ActionPerformed
    execute(MEM_BASIC_V2_0);
  }//GEN-LAST:event_jMenuItemBasicV2_1ActionPerformed

  private void jMenuItemBasicV3_5_1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBasicV3_5_1ActionPerformed
   execute(MEM_BASIC_V3_5);
  }//GEN-LAST:event_jMenuItemBasicV3_5_1ActionPerformed

  private void jMenuItemBasicV4_0_1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBasicV4_0_1ActionPerformed
    execute(MEM_BASIC_V4_0);
  }//GEN-LAST:event_jMenuItemBasicV4_0_1ActionPerformed

  private void jMenuItemBasicV7_0_1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBasicV7_0_1ActionPerformed
    execute(MEM_BASIC_V7_0);
  }//GEN-LAST:event_jMenuItemBasicV7_0_1ActionPerformed

  private void jMenuItemSimon1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSimon1ActionPerformed
    execute(MEM_BASIC_SIMON);
  }//GEN-LAST:event_jMenuItemSimon1ActionPerformed

  private void jMenuItemAndreFachat1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAndreFachat1ActionPerformed
     execute(MEM_BASIC_ANDRE_FACHAT);
  }//GEN-LAST:event_jMenuItemAndreFachat1ActionPerformed

  private void jMenuItemSpeech1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSpeech1ActionPerformed
    execute(MEM_BASIC_SPEECH);    
  }//GEN-LAST:event_jMenuItemSpeech1ActionPerformed

  private void jMenuItemFinalCart4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFinalCart4ActionPerformed
    execute(MEM_BASIC_FINAL_CART3); 
  }//GEN-LAST:event_jMenuItemFinalCart4ActionPerformed

  private void jMenuItemUltrabasic1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUltrabasic1ActionPerformed
    execute(MEM_BASIC_ULTRABASIC);    
  }//GEN-LAST:event_jMenuItemUltrabasic1ActionPerformed

  private void jMenuItemGraphics1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGraphics1ActionPerformed
    execute(MEM_BASIC_GRAPHICS);    
  }//GEN-LAST:event_jMenuItemGraphics1ActionPerformed

  private void jMenuItemWS1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWS1ActionPerformed
    execute(MEM_BASIC_WS);  
  }//GEN-LAST:event_jMenuItemWS1ActionPerformed

  private void jMenuItemPegasus1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPegasus1ActionPerformed
    execute(MEM_BASIC_PEGASUS); 
  }//GEN-LAST:event_jMenuItemPegasus1ActionPerformed

  private void jMenuItemXbasic1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemXbasic1ActionPerformed
    execute(MEM_BASIC_XBASIC);  
  }//GEN-LAST:event_jMenuItemXbasic1ActionPerformed

  private void jMenuItemDrago1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDrago1ActionPerformed
    execute(MEM_BASIC_DRAGO); 
  }//GEN-LAST:event_jMenuItemDrago1ActionPerformed

  private void jMenuItemReu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemReu1ActionPerformed
    execute(MEM_BASIC_REU);      
  }//GEN-LAST:event_jMenuItemReu1ActionPerformed

  private void jMenuItemLightninig1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLightninig1ActionPerformed
    execute(MEM_BASIC_LIGHTNING);     
  }//GEN-LAST:event_jMenuItemLightninig1ActionPerformed

  private void jMenuItemMagic1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMagic1ActionPerformed
    execute(MEM_BASIC_MAGIC);  
  }//GEN-LAST:event_jMenuItemMagic1ActionPerformed

  private void jMenuItemBlark1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBlark1ActionPerformed
    execute(MEM_BASIC_BLARG);    
  }//GEN-LAST:event_jMenuItemBlark1ActionPerformed

  private void jMenuItemWSFinal1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWSFinal1ActionPerformed
     execute(MEM_BASIC_WS_FINAL);
  }//GEN-LAST:event_jMenuItemWSFinal1ActionPerformed

  private void jMenuItemGame1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGame1ActionPerformed
     execute(MEM_BASIC_GAME);    
  }//GEN-LAST:event_jMenuItemGame1ActionPerformed

  private void jMenuItemBasex1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBasex1ActionPerformed
    execute(MEM_BASIC_BASEX);  
  }//GEN-LAST:event_jMenuItemBasex1ActionPerformed

  private void jMenuItemSuper1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSuper1ActionPerformed
     execute(MEM_BASIC_SUPER);  
  }//GEN-LAST:event_jMenuItemSuper1ActionPerformed

  private void jMenuItemExpanded1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExpanded1ActionPerformed
     execute(MEM_BASIC_EXPANDED);   
  }//GEN-LAST:event_jMenuItemExpanded1ActionPerformed

  private void jMenuItemSuperExpanderChip1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSuperExpanderChip1ActionPerformed
   execute(MEM_BASIC_SUPER_EXPANDER_CHIP);    
  }//GEN-LAST:event_jMenuItemSuperExpanderChip1ActionPerformed

  private void jMenuItemWarsaw1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWarsaw1ActionPerformed
     execute(MEM_BASIC_WARSAW);   
  }//GEN-LAST:event_jMenuItemWarsaw1ActionPerformed

  private void jMenuItemDbs1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDbs1ActionPerformed
    execute(MEM_BASIC_DBS);    
  }//GEN-LAST:event_jMenuItemDbs1ActionPerformed

  private void jMenuItemKipper1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemKipper1ActionPerformed
    execute(MEM_BASIC_KIPPER); 
  }//GEN-LAST:event_jMenuItemKipper1ActionPerformed

  private void jMenuItemBails1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBails1ActionPerformed
     execute(MEM_BASIC_BAILS);
  }//GEN-LAST:event_jMenuItemBails1ActionPerformed

  private void jMenuItemEve1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEve1ActionPerformed
    execute(MEM_BASIC_EVE);   
  }//GEN-LAST:event_jMenuItemEve1ActionPerformed

  private void jMenuItemTool1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTool1ActionPerformed
    execute(MEM_BASIC_TOOL);     
  }//GEN-LAST:event_jMenuItemTool1ActionPerformed

  private void jMenuItemSuperExpander1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSuperExpander1ActionPerformed
    execute(MEM_BASIC_SUPER_EXPANDER);   
  }//GEN-LAST:event_jMenuItemSuperExpander1ActionPerformed

  private void jMenuItemTurtle1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTurtle1ActionPerformed
    execute(MEM_BASIC_TURTLE);     
  }//GEN-LAST:event_jMenuItemTurtle1ActionPerformed

  private void jMenuItemEasy1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEasy1ActionPerformed
    execute(MEM_BASIC_EASY);    
  }//GEN-LAST:event_jMenuItemEasy1ActionPerformed

  private void jMenuItemV6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemV6ActionPerformed
    execute(MEM_BASIC_V4);   
  }//GEN-LAST:event_jMenuItemV6ActionPerformed

  private void jMenuItemV7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemV7ActionPerformed
    execute(MEM_BASIC_V5);    
  }//GEN-LAST:event_jMenuItemV7ActionPerformed

  private void jMenuItemExpandedV21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExpandedV21ActionPerformed
    execute(MEM_BASIC_EXPANDED_V20);  
  }//GEN-LAST:event_jMenuItemExpandedV21ActionPerformed

  private void jMenuItemHandy1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHandy1ActionPerformed
    execute(MEM_BASIC_HANDY);    
  }//GEN-LAST:event_jMenuItemHandy1ActionPerformed

  private void jMenuItemV8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemV8ActionPerformed
    execute(MEM_BASIC_V8);    
  }//GEN-LAST:event_jMenuItemV8ActionPerformed

  private void jMenuItemV9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemV9ActionPerformed
    execute(MEM_BASIC_V8);
  }//GEN-LAST:event_jMenuItemV9ActionPerformed

  private void jMenuItemBlockLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBlockLabelActionPerformed
    execute(MEM_BLOCKLABELS);
  }//GEN-LAST:event_jMenuItemBlockLabelActionPerformed

  private void jButtonJumpBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonJumpBackActionPerformed
    execute(MEM_JUMPBACK);
  }//GEN-LAST:event_jButtonJumpBackActionPerformed

  private void jButtonJumpFollowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonJumpFollowActionPerformed
    execute(MEM_JUMPFOLLOW);
  }//GEN-LAST:event_jButtonJumpFollowActionPerformed

  private void jMenuItemSaveAsGlassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsGlassActionPerformed
    execute(SOURCE_GLASS);
  }//GEN-LAST:event_jMenuItemSaveAsGlassActionPerformed

  private void jMenuItemSaveAsASActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsASActionPerformed
    execute(SOURCE_AS);
  }//GEN-LAST:event_jMenuItemSaveAsASActionPerformed

  private void jMenuItemSaveAsGlass1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsGlass1ActionPerformed
    execute(SOURCE_GLASS);
  }//GEN-LAST:event_jMenuItemSaveAsGlass1ActionPerformed

  private void jMenuItemSaveAsAS1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsAS1ActionPerformed
    execute(SOURCE_AS);
  }//GEN-LAST:event_jMenuItemSaveAsAS1ActionPerformed

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
  private sw_emulator.swing.HeapView heapView;
  private javax.swing.JButton jButtonAddUserBlock;
  private javax.swing.JButton jButtonAddUserComm;
  private javax.swing.JButton jButtonAddUserLabel;
  private javax.swing.JButton jButtonAddUserLabelOp;
  private javax.swing.JButton jButtonAssemble;
  private javax.swing.JButton jButtonClearDLabel;
  private javax.swing.JButton jButtonClearDMem;
  private javax.swing.JButton jButtonClearUMem;
  private javax.swing.JButton jButtonClose;
  private javax.swing.JButton jButtonConfigure;
  private javax.swing.JButton jButtonDisassemble;
  private javax.swing.JButton jButtonExit;
  private javax.swing.JButton jButtonExportAsDiss;
  private javax.swing.JButton jButtonExportAsSource;
  private javax.swing.JButton jButtonExportAsSource1;
  private javax.swing.JButton jButtonFindDis;
  private javax.swing.JButton jButtonFindMem;
  private javax.swing.JButton jButtonFindSource;
  private javax.swing.JButton jButtonJumpBack;
  private javax.swing.JButton jButtonJumpFollow;
  private javax.swing.JButton jButtonMPR;
  private javax.swing.JButton jButtonMarkBoth;
  private javax.swing.JButton jButtonMarkCode;
  private javax.swing.JButton jButtonMarkConstant;
  private javax.swing.JButton jButtonMarkData;
  private javax.swing.JButton jButtonMarkGarbage;
  private javax.swing.JButton jButtonMarkHighLow;
  private javax.swing.JButton jButtonMarkLow;
  private javax.swing.JButton jButtonMarkLowHigh;
  private javax.swing.JButton jButtonMarkMax;
  private javax.swing.JButton jButtonMarkMinus;
  private javax.swing.JButton jButtonMarkPlus;
  private javax.swing.JButton jButtonMerge;
  private javax.swing.JButton jButtonNewProject;
  private javax.swing.JButton jButtonOpenProject;
  private javax.swing.JButton jButtonSIDLD;
  private javax.swing.JButton jButtonSaveProject;
  private javax.swing.JButton jButtonSaveProjectAs;
  private javax.swing.JButton jButtonViewLabels;
  private javax.swing.JButton jButtonViewProject;
  private javax.swing.JButton jButtonWizard;
  private javax.swing.JMenuBar jMenuBar;
  private javax.swing.JMenu jMenuBasic;
  private javax.swing.JMenu jMenuBasic1;
  private javax.swing.JMenu jMenuDataSel;
  private javax.swing.JMenu jMenuFile;
  private javax.swing.JMenu jMenuHelpContents;
  private javax.swing.JMenuItem jMenuItem1;
  private javax.swing.JMenuItem jMenuItem2;
  private javax.swing.JMenuItem jMenuItem3;
  private javax.swing.JMenuItem jMenuItemAbout;
  private javax.swing.JMenuItem jMenuItemAddBlock;
  private javax.swing.JMenuItem jMenuItemAddComment;
  private javax.swing.JMenuItem jMenuItemAddress;
  private javax.swing.JMenuItem jMenuItemAddress1;
  private javax.swing.JMenuItem jMenuItemAddress2;
  private javax.swing.JMenuItem jMenuItemAndreFachat;
  private javax.swing.JMenuItem jMenuItemAndreFachat1;
  private javax.swing.JMenuItem jMenuItemAssembly;
  private javax.swing.JMenuItem jMenuItemAutComment;
  private javax.swing.JMenuItem jMenuItemAutLabel;
  private javax.swing.JMenuItem jMenuItemBails;
  private javax.swing.JMenuItem jMenuItemBails1;
  private javax.swing.JMenuItem jMenuItemBasex;
  private javax.swing.JMenuItem jMenuItemBasex1;
  private javax.swing.JMenuItem jMenuItemBasicNone;
  private javax.swing.JMenuItem jMenuItemBasicNone1;
  private javax.swing.JMenuItem jMenuItemBasicV2_0;
  private javax.swing.JMenuItem jMenuItemBasicV2_1;
  private javax.swing.JMenuItem jMenuItemBasicV3_5;
  private javax.swing.JMenuItem jMenuItemBasicV3_5_1;
  private javax.swing.JMenuItem jMenuItemBasicV4_0;
  private javax.swing.JMenuItem jMenuItemBasicV4_0_1;
  private javax.swing.JMenuItem jMenuItemBasicV7_0;
  private javax.swing.JMenuItem jMenuItemBasicV7_0_1;
  private javax.swing.JMenuItem jMenuItemBlark;
  private javax.swing.JMenuItem jMenuItemBlark1;
  private javax.swing.JMenuItem jMenuItemBlockLabel;
  private javax.swing.JMenuItem jMenuItemByteBin;
  private javax.swing.JMenuItem jMenuItemByteBin1;
  private javax.swing.JMenuItem jMenuItemByteBin2;
  private javax.swing.JMenuItem jMenuItemByteChar;
  private javax.swing.JMenuItem jMenuItemByteChar1;
  private javax.swing.JMenuItem jMenuItemByteChar2;
  private javax.swing.JMenuItem jMenuItemByteDec;
  private javax.swing.JMenuItem jMenuItemByteDec1;
  private javax.swing.JMenuItem jMenuItemByteDec2;
  private javax.swing.JMenuItem jMenuItemByteHex;
  private javax.swing.JMenuItem jMenuItemByteHex1;
  private javax.swing.JMenuItem jMenuItemByteHex2;
  private javax.swing.JMenuItem jMenuItemClearDLabel;
  private javax.swing.JMenuItem jMenuItemClearDMem;
  private javax.swing.JMenuItem jMenuItemClearUMem;
  private javax.swing.JMenuItem jMenuItemCloseProject;
  private javax.swing.JMenuItem jMenuItemCode;
  private javax.swing.JMenuItem jMenuItemConfigure;
  private javax.swing.JMenuItem jMenuItemConstant0;
  private javax.swing.JMenuItem jMenuItemConstant0b;
  private javax.swing.JMenuItem jMenuItemConstant0b1;
  private javax.swing.JMenuItem jMenuItemConstant1;
  private javax.swing.JMenuItem jMenuItemConstant10;
  private javax.swing.JMenuItem jMenuItemConstant11;
  private javax.swing.JMenuItem jMenuItemConstant12;
  private javax.swing.JMenuItem jMenuItemConstant13;
  private javax.swing.JMenuItem jMenuItemConstant14;
  private javax.swing.JMenuItem jMenuItemConstant15;
  private javax.swing.JMenuItem jMenuItemConstant16;
  private javax.swing.JMenuItem jMenuItemConstant17;
  private javax.swing.JMenuItem jMenuItemConstant18;
  private javax.swing.JMenuItem jMenuItemConstant19;
  private javax.swing.JMenuItem jMenuItemConstant1b;
  private javax.swing.JMenuItem jMenuItemConstant1b1;
  private javax.swing.JMenuItem jMenuItemConstant2;
  private javax.swing.JMenuItem jMenuItemConstant2b;
  private javax.swing.JMenuItem jMenuItemConstant2b1;
  private javax.swing.JMenuItem jMenuItemConstant3;
  private javax.swing.JMenuItem jMenuItemConstant3b;
  private javax.swing.JMenuItem jMenuItemConstant3b1;
  private javax.swing.JMenuItem jMenuItemConstant4;
  private javax.swing.JMenuItem jMenuItemConstant4b;
  private javax.swing.JMenuItem jMenuItemConstant4b1;
  private javax.swing.JMenuItem jMenuItemConstant5;
  private javax.swing.JMenuItem jMenuItemConstant5b;
  private javax.swing.JMenuItem jMenuItemConstant5b1;
  private javax.swing.JMenuItem jMenuItemConstant6;
  private javax.swing.JMenuItem jMenuItemConstant6b;
  private javax.swing.JMenuItem jMenuItemConstant6b1;
  private javax.swing.JMenuItem jMenuItemConstant7;
  private javax.swing.JMenuItem jMenuItemConstant7b;
  private javax.swing.JMenuItem jMenuItemConstant7b1;
  private javax.swing.JMenuItem jMenuItemConstant8;
  private javax.swing.JMenuItem jMenuItemConstant8b;
  private javax.swing.JMenuItem jMenuItemConstant8b1;
  private javax.swing.JMenuItem jMenuItemConstant9;
  private javax.swing.JMenuItem jMenuItemConstant9b;
  private javax.swing.JMenuItem jMenuItemConstant9b1;
  private javax.swing.JMenuItem jMenuItemConstantClear;
  private javax.swing.JMenuItem jMenuItemConstantClear_;
  private javax.swing.JMenuItem jMenuItemContents;
  private javax.swing.JMenuItem jMenuItemCopy;
  private javax.swing.JMenuItem jMenuItemCredits;
  private javax.swing.JMenuItem jMenuItemData;
  private javax.swing.JMenuItem jMenuItemDbs;
  private javax.swing.JMenuItem jMenuItemDbs1;
  private javax.swing.JMenuItem jMenuItemDiss;
  private javax.swing.JMenuItem jMenuItemDissSaveAs;
  private javax.swing.JMenuItem jMenuItemDrago;
  private javax.swing.JMenuItem jMenuItemDrago1;
  private javax.swing.JMenuItem jMenuItemEasy;
  private javax.swing.JMenuItem jMenuItemEasy1;
  private javax.swing.JMenuItem jMenuItemEve;
  private javax.swing.JMenuItem jMenuItemEve1;
  private javax.swing.JMenuItem jMenuItemExit;
  private javax.swing.JMenuItem jMenuItemExpanded;
  private javax.swing.JMenuItem jMenuItemExpanded1;
  private javax.swing.JMenuItem jMenuItemExpandedV20;
  private javax.swing.JMenuItem jMenuItemExpandedV21;
  private javax.swing.JMenuItem jMenuItemFinalCart3;
  private javax.swing.JMenuItem jMenuItemFinalCart4;
  private javax.swing.JMenuItem jMenuItemFindAddr;
  private javax.swing.JMenuItem jMenuItemFindDis;
  private javax.swing.JMenuItem jMenuItemFindSeq;
  private javax.swing.JMenuItem jMenuItemFindSource;
  private javax.swing.JMenuItem jMenuItemGame;
  private javax.swing.JMenuItem jMenuItemGame1;
  private javax.swing.JMenuItem jMenuItemGarbage;
  private javax.swing.JMenuItem jMenuItemGraphics;
  private javax.swing.JMenuItem jMenuItemGraphics1;
  private javax.swing.JMenuItem jMenuItemHandy;
  private javax.swing.JMenuItem jMenuItemHandy1;
  private javax.swing.JMenuItem jMenuItemHex;
  private javax.swing.JMenuItem jMenuItemImportLabels;
  private javax.swing.JMenuItem jMenuItemKipper;
  private javax.swing.JMenuItem jMenuItemKipper1;
  private javax.swing.JMenuItem jMenuItemLicense;
  private javax.swing.JMenuItem jMenuItemLightninig;
  private javax.swing.JMenuItem jMenuItemLightninig1;
  private javax.swing.JMenuItem jMenuItemLong;
  private javax.swing.JMenuItem jMenuItemLong1;
  private javax.swing.JMenuItem jMenuItemLong2;
  private javax.swing.JMenuItem jMenuItemMPR;
  private javax.swing.JMenuItem jMenuItemMagic;
  private javax.swing.JMenuItem jMenuItemMagic1;
  private javax.swing.JMenuItem jMenuItemMarkCode;
  private javax.swing.JMenuItem jMenuItemMarkData;
  private javax.swing.JMenuItem jMenuItemMarkGarbage;
  private javax.swing.JMenuItem jMenuItemMemBoth;
  private javax.swing.JMenuItem jMenuItemMemHigh;
  private javax.swing.JMenuItem jMenuItemMemHighLow;
  private javax.swing.JMenuItem jMenuItemMemLow;
  private javax.swing.JMenuItem jMenuItemMemLowHigh;
  private javax.swing.JMenuItem jMenuItemMinus;
  private javax.swing.JMenuItem jMenuItemNewProject;
  private javax.swing.JMenuItem jMenuItemNumText;
  private javax.swing.JMenuItem jMenuItemNumText1;
  private javax.swing.JMenuItem jMenuItemNumText2;
  private javax.swing.JMenuItem jMenuItemOpenProject;
  private javax.swing.JMenuItem jMenuItemPaste;
  private javax.swing.JMenuItem jMenuItemPegasus;
  private javax.swing.JMenuItem jMenuItemPegasus1;
  private javax.swing.JMenuItem jMenuItemPlus;
  private javax.swing.JMenuItem jMenuItemRecent1;
  private javax.swing.JMenuItem jMenuItemRecent2;
  private javax.swing.JMenuItem jMenuItemRecent3;
  private javax.swing.JMenuItem jMenuItemRecent4;
  private javax.swing.JMenuItem jMenuItemRecent5;
  private javax.swing.JMenuItem jMenuItemRecent6;
  private javax.swing.JMenuItem jMenuItemRecent7;
  private javax.swing.JMenuItem jMenuItemRecent8;
  private javax.swing.JMenuItem jMenuItemRecent9;
  private javax.swing.JMenuItem jMenuItemRefactorLabels;
  private javax.swing.JMenuItem jMenuItemReu;
  private javax.swing.JMenuItem jMenuItemReu1;
  private javax.swing.JMenuItem jMenuItemSIDLD;
  private javax.swing.JMenuItem jMenuItemSaveAsAS;
  private javax.swing.JMenuItem jMenuItemSaveAsAS1;
  private javax.swing.JMenuItem jMenuItemSaveAsAcme;
  private javax.swing.JMenuItem jMenuItemSaveAsAcme1;
  private javax.swing.JMenuItem jMenuItemSaveAsCa65;
  private javax.swing.JMenuItem jMenuItemSaveAsCa65_1;
  private javax.swing.JMenuItem jMenuItemSaveAsDasm;
  private javax.swing.JMenuItem jMenuItemSaveAsDasm1;
  private javax.swing.JMenuItem jMenuItemSaveAsGlass;
  private javax.swing.JMenuItem jMenuItemSaveAsGlass1;
  private javax.swing.JMenuItem jMenuItemSaveAsKickAssembler;
  private javax.swing.JMenuItem jMenuItemSaveAsKickAssembler1;
  private javax.swing.JMenuItem jMenuItemSaveAsProject;
  private javax.swing.JMenuItem jMenuItemSaveAsTass64;
  private javax.swing.JMenuItem jMenuItemSaveAsTass64_1;
  private javax.swing.JMenuItem jMenuItemSaveAsTmpx;
  private javax.swing.JMenuItem jMenuItemSaveAsTmpx1;
  private javax.swing.JMenuItem jMenuItemSaveProject;
  private javax.swing.JMenuItem jMenuItemSidldPlayer;
  private javax.swing.JMenuItem jMenuItemSimon;
  private javax.swing.JMenuItem jMenuItemSimon1;
  private javax.swing.JMenuItem jMenuItemSourceSaveAs;
  private javax.swing.JMenuItem jMenuItemSpeech;
  private javax.swing.JMenuItem jMenuItemSpeech1;
  private javax.swing.JMenuItem jMenuItemSpriteMono;
  private javax.swing.JMenuItem jMenuItemSpriteMono1;
  private javax.swing.JMenuItem jMenuItemSpriteMono2;
  private javax.swing.JMenuItem jMenuItemSpriteMulti;
  private javax.swing.JMenuItem jMenuItemSpriteMulti1;
  private javax.swing.JMenuItem jMenuItemSpriteMulti2;
  private javax.swing.JMenuItem jMenuItemStackWord;
  private javax.swing.JMenuItem jMenuItemStackWord1;
  private javax.swing.JMenuItem jMenuItemStackWord2;
  private javax.swing.JMenuItem jMenuItemSuper;
  private javax.swing.JMenuItem jMenuItemSuper1;
  private javax.swing.JMenuItem jMenuItemSuperExpander;
  private javax.swing.JMenuItem jMenuItemSuperExpander1;
  private javax.swing.JMenuItem jMenuItemSuperExpanderChip;
  private javax.swing.JMenuItem jMenuItemSuperExpanderChip1;
  private javax.swing.JMenuItem jMenuItemText;
  private javax.swing.JMenuItem jMenuItemText1;
  private javax.swing.JMenuItem jMenuItemText2;
  private javax.swing.JMenuItem jMenuItemTextPetascii;
  private javax.swing.JMenuItem jMenuItemTextPetascii1;
  private javax.swing.JMenuItem jMenuItemTextPetascii2;
  private javax.swing.JMenuItem jMenuItemTextScreen;
  private javax.swing.JMenuItem jMenuItemTextScreen1;
  private javax.swing.JMenuItem jMenuItemTextScreen2;
  private javax.swing.JMenuItem jMenuItemTextZero;
  private javax.swing.JMenuItem jMenuItemTextZero1;
  private javax.swing.JMenuItem jMenuItemTextZero2;
  private javax.swing.JMenuItem jMenuItemTool;
  private javax.swing.JMenuItem jMenuItemTool1;
  private javax.swing.JMenuItem jMenuItemTribyte;
  private javax.swing.JMenuItem jMenuItemTribyte1;
  private javax.swing.JMenuItem jMenuItemTribyte2;
  private javax.swing.JMenuItem jMenuItemTurtle;
  private javax.swing.JMenuItem jMenuItemTurtle1;
  private javax.swing.JMenuItem jMenuItemUltrabasic;
  private javax.swing.JMenuItem jMenuItemUltrabasic1;
  private javax.swing.JMenuItem jMenuItemUndo1;
  private javax.swing.JMenuItem jMenuItemUndo2;
  private javax.swing.JMenuItem jMenuItemUndo3;
  private javax.swing.JMenuItem jMenuItemUndo4;
  private javax.swing.JMenuItem jMenuItemUndo5;
  private javax.swing.JMenuItem jMenuItemUndo6;
  private javax.swing.JMenuItem jMenuItemUndo7;
  private javax.swing.JMenuItem jMenuItemUndo8;
  private javax.swing.JMenuItem jMenuItemUndo9;
  private javax.swing.JMenuItem jMenuItemUnmark;
  private javax.swing.JMenuItem jMenuItemUserLabel;
  private javax.swing.JMenuItem jMenuItemUserLabelOp;
  private javax.swing.JMenuItem jMenuItemV4;
  private javax.swing.JMenuItem jMenuItemV5;
  private javax.swing.JMenuItem jMenuItemV6;
  private javax.swing.JMenuItem jMenuItemV7;
  private javax.swing.JMenuItem jMenuItemV8;
  private javax.swing.JMenuItem jMenuItemV9;
  private javax.swing.JMenuItem jMenuItemViewLabels;
  private javax.swing.JMenuItem jMenuItemViewProject;
  private javax.swing.JMenuItem jMenuItemWS;
  private javax.swing.JMenuItem jMenuItemWS1;
  private javax.swing.JMenuItem jMenuItemWSFinal;
  private javax.swing.JMenuItem jMenuItemWSFinal1;
  private javax.swing.JMenuItem jMenuItemWarsaw;
  private javax.swing.JMenuItem jMenuItemWarsaw1;
  private javax.swing.JMenuItem jMenuItemWizard;
  private javax.swing.JMenuItem jMenuItemWord;
  private javax.swing.JMenuItem jMenuItemWord1;
  private javax.swing.JMenuItem jMenuItemWord2;
  private javax.swing.JMenuItem jMenuItemWordSwapped;
  private javax.swing.JMenuItem jMenuItemWordSwapped1;
  private javax.swing.JMenuItem jMenuItemWordSwapped2;
  private javax.swing.JMenuItem jMenuItemXbasic;
  private javax.swing.JMenuItem jMenuItemXbasic1;
  private javax.swing.JMenuItem jMenuItemtextHighOne;
  private javax.swing.JMenuItem jMenuItemtextHighOne1;
  private javax.swing.JMenuItem jMenuItemtextHighOne2;
  private javax.swing.JMenuItem jMenuItemtextShifted;
  private javax.swing.JMenuItem jMenuItemtextShifted1;
  private javax.swing.JMenuItem jMenuItemtextShifted2;
  private javax.swing.JMenu jMenuMemory;
  private javax.swing.JMenu jMenuOption;
  private javax.swing.JMenu jMenuRecent;
  private javax.swing.JMenu jMenuSource;
  private javax.swing.JMenu jMenuSub;
  private javax.swing.JMenu jMenuUndo;
  private javax.swing.JPanel jPanelLeft;
  private sw_emulator.swing.JPanelPerc jPanelPerc;
  private javax.swing.JPanel jPanelRight;
  private javax.swing.JPanel jPanelToolBar;
  private javax.swing.JPopupMenu jPopupMenuConstant;
  private javax.swing.JPopupMenu jPopupMenuData;
  private javax.swing.JPopupMenu jPopupMenuMemory;
  private javax.swing.JPopupMenu jPopupMenuSaveAs;
  private javax.swing.JScrollPane jScrollPaneLeft;
  protected javax.swing.JScrollPane jScrollPaneLeftMin;
  private javax.swing.JScrollPane jScrollPaneMemory;
  private javax.swing.JScrollPane jScrollPaneRight;
  protected javax.swing.JScrollPane jScrollPaneRightMin;
  private javax.swing.JPopupMenu.Separator jSeparator1;
  private javax.swing.JPopupMenu.Separator jSeparator2;
  private javax.swing.JPopupMenu.Separator jSeparator3;
  private javax.swing.JPopupMenu.Separator jSeparator4;
  private javax.swing.JPopupMenu.Separator jSeparator5;
  private javax.swing.JPopupMenu.Separator jSeparator6;
  private javax.swing.JPopupMenu.Separator jSeparator7;
  private javax.swing.JPopupMenu.Separator jSeparatorByte;
  private javax.swing.JPopupMenu.Separator jSeparatorByte1;
  private javax.swing.JPopupMenu.Separator jSeparatorByte2;
  private javax.swing.JPopupMenu.Separator jSeparatorConstant;
  private javax.swing.JPopupMenu.Separator jSeparatorConstant2;
  private javax.swing.JPopupMenu.Separator jSeparatorConstant3;
  private javax.swing.JPopupMenu.Separator jSeparatorConstant4;
  private javax.swing.JPopupMenu.Separator jSeparatorHelp1;
  private javax.swing.JPopupMenu.Separator jSeparatorHelp2;
  private javax.swing.JPopupMenu.Separator jSeparatorHelp3;
  private javax.swing.JPopupMenu.Separator jSeparatorMem1;
  private javax.swing.JPopupMenu.Separator jSeparatorMem2;
  private javax.swing.JPopupMenu.Separator jSeparatorMem3;
  private javax.swing.JPopupMenu.Separator jSeparatorOption;
  private javax.swing.JPopupMenu.Separator jSeparatorPopUpMenu0;
  private javax.swing.JPopupMenu.Separator jSeparatorPopUpMenu1;
  private javax.swing.JPopupMenu.Separator jSeparatorPopUpMenu2;
  private javax.swing.JPopupMenu.Separator jSeparatorPopUpMenu3;
  private javax.swing.JPopupMenu.Separator jSeparatorProject1;
  private javax.swing.JPopupMenu.Separator jSeparatorProject2;
  private javax.swing.JPopupMenu.Separator jSeparatorProject3;
  private javax.swing.JPopupMenu.Separator jSeparatorSprite;
  private javax.swing.JPopupMenu.Separator jSeparatorSprite1;
  private javax.swing.JPopupMenu.Separator jSeparatorSprite2;
  private javax.swing.JPopupMenu.Separator jSeparatorStandard;
  private javax.swing.JPopupMenu.Separator jSeparatorStandard1;
  private javax.swing.JPopupMenu.Separator jSeparatorV128;
  private javax.swing.JPopupMenu.Separator jSeparatorV129;
  private javax.swing.JPopupMenu.Separator jSeparatorV2;
  private javax.swing.JPopupMenu.Separator jSeparatorV20;
  private javax.swing.JPopupMenu.Separator jSeparatorV21;
  private javax.swing.JPopupMenu.Separator jSeparatorV3;
  private javax.swing.JPopupMenu.Separator jSeparatorWord;
  private javax.swing.JPopupMenu.Separator jSeparatorWord1;
  private javax.swing.JPopupMenu.Separator jSeparatorWord2;
  private javax.swing.JSplitPane jSplitPaneExternal;
  private javax.swing.JSplitPane jSplitPaneInternal;
  private javax.swing.JMenu jSubMenu;
  private javax.swing.JMenu jSubMenuC;
  private javax.swing.JTable jTableMemory;
  private javax.swing.JToolBar jToolBarFile;
  private javax.swing.JToolBar jToolBarMemory;
  private javax.swing.JToolBar jToolBarOption;
  private javax.swing.JToolBar jToolBarPerc;
  private javax.swing.JToolBar jToolBarPerformance;
  private javax.swing.JToolBar jToolBarSource;
  private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea rSyntaxTextAreaDis;
  protected org.fife.ui.rsyntaxtextarea.RSyntaxTextArea rSyntaxTextAreaDisMin;
  private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea rSyntaxTextAreaSource;
  protected org.fife.ui.rsyntaxtextarea.RSyntaxTextArea rSyntaxTextAreaSourceMin;
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
      case PROJ_RECENT:
        recentFile();  
        break;  
      case PROJ_SAVE:
        projectSave(); 
        break;        
      case PROJ_SAVEAS:
        projectSaveAs();
        break;        
      case PROJ_MERGE:
        projectMerge();
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
      case OPTION_LABELS:
        optionLabels();
        break;
      case SOURCE_DISASS:
        disassembly(true);
        break;        
      case SOURCE_ASS:
        assembly();
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
      case SOURCE_FINDX:
        findSeqHex();
        break;    
      case SOURCE_FINDD:
        findDialogDis.setVisible(true);
        break;
      case SOURCE_FINDS:
        findDialogSource.setVisible(true);
        break;        
      case SOURCE_FREEZE:
        if (project==null)  {
          JOptionPane.showMessageDialog(this, "Needs an active project for this function");
          return;
        }
        jFreezeFrame.setup(project, disassembly, option);
        jFreezeFrame.setVisible(true);
        break;
      case APP_EXIT:
        exit();
        break;        
      case MEM_CLEARDCOM:
        clearDasmComment();
        if (option.forceCompilation) disassembly(true);
        break;
       case MEM_CLEARUCOM:
        clearUserComment();
        if (option.forceCompilation) disassembly(true);
        break;    
       case MEM_ADDCOMM:
         addComment();
         if (option.forceCompilation) disassembly(true);
         break;
       case MEM_ADDLABEL:
         addLabel();
         if (option.forceCompilation) disassembly(true);
         break;      
       case MEM_ADDLABELOP:
         addLabelOp();
         if (option.forceCompilation) disassembly(true);
         break;                   
       case MEM_MARKCODE:
         markAsCode();  
         if (option.forceCompilation) disassembly(true);
         break;
       case MEM_MARKDATA:
         markAsData(DataType.NONE);  
         if (option.forceCompilation) disassembly(true);
         break;
       case MEM_MARKDATA_B:
         markAsData(DataType.BYTE_HEX);      
         if (option.forceCompilation) disassembly(true);
         break;      
       case MEM_MARKDATA_D:
         markAsData(DataType.BYTE_DEC);      
         if (option.forceCompilation) disassembly(true);
         break;           
       case MEM_MARKDATA_Y:
         markAsData(DataType.BYTE_BIN);      
         if (option.forceCompilation) disassembly(true);
         break; 
       case MEM_MARKDATA_R:
         markAsData(DataType.BYTE_CHAR);      
         if (option.forceCompilation) disassembly(true);
         break;          
       case MEM_MARKDATA_W:  
         markAsData(DataType.WORD);  
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_MARKDATA_P:  
         markAsData(DataType.SWAPPED);  
         if (option.forceCompilation) disassembly(true);
         break;      
       case MEM_MARKDATA_E:  
         markAsData(DataType.TRIBYTE);   
         if (option.forceCompilation) disassembly(true);
         break;         
       case MEM_MARKDATA_L:  
         markAsData(DataType.LONG);   
         if (option.forceCompilation) disassembly(true);
         break;          
       case MEM_MARKDATA_A:  
         markAsData(DataType.ADDRESS);     
         if (option.forceCompilation) disassembly(true);
         break;         
       case MEM_MARKDATA_S:  
         markAsData(DataType.STACK);      
         if (option.forceCompilation) disassembly(true);
         break;           
       case MEM_MARKDATA_T: 
         markAsData(DataType.TEXT);  
         if (option.forceCompilation) disassembly(true);
         break;           
       case MEM_MARKDATA_N:  
         markAsData(DataType.NUM_TEXT);    
         if (option.forceCompilation) disassembly(true);
         break;           
       case MEM_MARKDATA_Z: 
         markAsData(DataType.ZERO_TEXT);   
         if (option.forceCompilation) disassembly(true);
         break;   
       case MEM_MARKDATA_M:  
         markAsData(DataType.HIGH_TEXT);   
         if (option.forceCompilation) disassembly(true);
         break;            
       case MEM_MARKDATA_H:  
         markAsData(DataType.SHIFT_TEXT);   
         if (option.forceCompilation) disassembly(true);
         break;           
       case MEM_MARKDATA_C:  
         markAsData(DataType.SCREEN_TEXT);  
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_MARKDATA_I:  
         markAsData(DataType.PETASCII_TEXT);  
         if (option.forceCompilation) disassembly(true);
         break;     
       case MEM_MARKDATA_O:  
         markAsData(DataType.MONO_SPRITE);  
         if (option.forceCompilation) disassembly(true);
         break;        
       case MEM_MARKDATA_F:  
         markAsData(DataType.MULTI_SPRITE);  
         if (option.forceCompilation) disassembly(true);
         break;          
       case MEM_MARKGARB:
         markAsGarbage();  
         if (option.forceCompilation) disassembly(true);
         break;   
       case MEM_UNMARK:
         unmark();  
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_ADDBLOCK:
         addBlock();
         if (option.forceCompilation) disassembly(true);
         break;
       case MEM_CLEARDLABEL:
         clearDLabel();  
         if (option.forceCompilation) disassembly(true);
         break;
       case MEM_LOW:
         memLow();  
         if (option.forceCompilation) disassembly(true);
         break;
       case MEM_LOWHIGH:          
         memLowHigh();  
         if (option.forceCompilation) disassembly(true);
         break;
       case MEM_BOTH:          
         memAutoLoHi();          
         if (option.forceCompilation) disassembly(true); 
         break;
       case MEM_HIGHLOW:
         memHighLow();  
         if (option.forceCompilation) disassembly(true);
         break;         
       case MEM_HIGH:
         memHigh();  
         if (option.forceCompilation) disassembly(true);
         break;         
       case MEM_PLUS:
         memPlus();  
         if (option.forceCompilation) disassembly(true);
         break;
       case MEM_MINUS:
         memMinus();  
         if (option.forceCompilation) disassembly(true);
         break;    
       case MEM_SUB_0:
         subAssign(0);  
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_SUB_1:  
         subAssign(1);
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_SUB_2:  
         subAssign(2); 
         if (option.forceCompilation) disassembly(true);
         break;
       case MEM_SUB_3:  
         subAssign(3); 
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_SUB_4:  
         subAssign(4); 
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_SUB_5:  
         subAssign(5);  
         if (option.forceCompilation) disassembly(true);
         break;
       case MEM_SUB_6:  
         subAssign(6);  
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_SUB_7:  
         subAssign(7);  
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_SUB_8:  
         subAssign(8); 
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_SUB_9:  
         subAssign(9);  
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_SUB_10:
         subAssign(10);  
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_SUB_11:  
         subAssign(11);
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_SUB_12:  
         subAssign(12); 
         if (option.forceCompilation) disassembly(true);
         break;
       case MEM_SUB_13:  
         subAssign(13); 
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_SUB_14:  
         subAssign(14); 
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_SUB_15:  
         subAssign(15);  
         if (option.forceCompilation) disassembly(true);
         break;
       case MEM_SUB_16:  
         subAssign(16);  
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_SUB_17:  
         subAssign(17);  
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_SUB_18:  
         subAssign(18); 
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_SUB_19:  
         subAssign(19);  
         if (option.forceCompilation) disassembly(true);
         break;   
       case MEM_SUB_CLEAR:  
         subAssign(-1); 
         if (option.forceCompilation) disassembly(true);
         break;  
       case MEM_WIZARD:
         wizard();  
         if (option.forceCompilation) disassembly(true); 
         break;
       case MEM_HEX:
         showHex();  
         break;
       case MEM_BLOCKLABELS:
         createBlockLabels();
         break;
       case MEM_JUMPBACK:
         jumpBack();
         break;
       case MEM_JUMPFOLLOW:
         this.gotoLabelOp();
         break;         
         
         
       case MEM_BASIC_NONE:     
         markAsBasicData(BasicType.NONE);      
         if (option.forceCompilation) disassembly(true);
         break;          
       case MEM_BASIC_V2_0:     
         markAsBasicData(BasicType.BASIC_V2_0);      
         if (option.forceCompilation) disassembly(true);
         break;     
       case MEM_BASIC_V3_5:    
         markAsBasicData(BasicType.BASIC_V3_5);      
         if (option.forceCompilation) disassembly(true);
         break;        
       case MEM_BASIC_V4_0:     
         markAsBasicData(BasicType.BASIC_V4_0);      
         if (option.forceCompilation) disassembly(true);
         break;
       case MEM_BASIC_V7_0:      
         markAsBasicData(BasicType.BASIC_V7_0);      
         if (option.forceCompilation) disassembly(true);
         break;        
       case MEM_BASIC_SIMON:   
         markAsBasicData(BasicType.BASIC_SIMON);      
         if (option.forceCompilation) disassembly(true);
         break;     
       case MEM_BASIC_ANDRE_FACHAT:   
         markAsBasicData(BasicType.BASIC_ANDRE_FACHAT);      
         if (option.forceCompilation) disassembly(true);
         break;          
       case MEM_BASIC_SPEECH:   
         markAsBasicData(BasicType.BASIC_SPEECH);      
         if (option.forceCompilation) disassembly(true);
         break;              
       case MEM_BASIC_FINAL_CART3:   
         markAsBasicData(BasicType.BASIC_FINAL_CART3);      
         if (option.forceCompilation) disassembly(true);
         break;         
       case MEM_BASIC_ULTRABASIC:   
         markAsBasicData(BasicType.BASIC_ULTRABASIC);      
         if (option.forceCompilation) disassembly(true);
         break;         
       case MEM_BASIC_GRAPHICS:   
         markAsBasicData(BasicType.BASIC_GRAPHICS);      
         if (option.forceCompilation) disassembly(true);
         break;            
       case MEM_BASIC_WS:   
         markAsBasicData(BasicType.BASIC_WS);      
         if (option.forceCompilation) disassembly(true);
         break;          
       case MEM_BASIC_PEGASUS:   
         markAsBasicData(BasicType.BASIC_PEGASUS);      
         if (option.forceCompilation) disassembly(true);
         break;      
       case MEM_BASIC_XBASIC:   
         markAsBasicData(BasicType.BASIC_XBASIC);      
         if (option.forceCompilation) disassembly(true);
         break;       
       case MEM_BASIC_DRAGO:   
         markAsBasicData(BasicType.BASIC_DRAGO);      
         if (option.forceCompilation) disassembly(true);
         break;         
       case MEM_BASIC_REU:   
         markAsBasicData(BasicType.BASIC_REU);      
         if (option.forceCompilation) disassembly(true);
         break;      
       case MEM_BASIC_LIGHTNING:   
         markAsBasicData(BasicType.BASIC_LIGHTNING);      
         if (option.forceCompilation) disassembly(true);
         break;          
       case MEM_BASIC_MAGIC:   
         markAsBasicData(BasicType.BASIC_MAGIC);      
         if (option.forceCompilation) disassembly(true);
         break;       
       case MEM_BASIC_BLARG:   
         markAsBasicData(BasicType.BASIC_BLARG);      
         if (option.forceCompilation) disassembly(true);
         break;    
       case MEM_BASIC_WS_FINAL:   
         markAsBasicData(BasicType.BASIC_WS_FINAL);      
         if (option.forceCompilation) disassembly(true);
         break;    
       case MEM_BASIC_GAME:   
         markAsBasicData(BasicType.BASIC_GAME);      
         if (option.forceCompilation) disassembly(true);
         break;      
       case MEM_BASIC_BASEX:   
         markAsBasicData(BasicType.BASIC_BASEX);      
         if (option.forceCompilation) disassembly(true);
         break;           
       case MEM_BASIC_SUPER:   
         markAsBasicData(BasicType.BASIC_SUPER);      
         if (option.forceCompilation) disassembly(true);
         break;          
       case MEM_BASIC_EXPANDED:   
         markAsBasicData(BasicType.BASIC_EXPANDED);      
         if (option.forceCompilation) disassembly(true);
         break;           
       case MEM_BASIC_SUPER_EXPANDER_CHIP:   
         markAsBasicData(BasicType.BASIC_SUPER_EXPANDER_CHIP);      
         if (option.forceCompilation) disassembly(true);
         break;         
       case MEM_BASIC_WARSAW:   
         markAsBasicData(BasicType.BASIC_WARSAW);      
         if (option.forceCompilation) disassembly(true);
         break;           
       case MEM_BASIC_DBS:   
         markAsBasicData(BasicType.BASIC_DBS);      
         if (option.forceCompilation) disassembly(true);
         break;            
       case MEM_BASIC_KIPPER:   
         markAsBasicData(BasicType.BASIC_KIPPER);      
         if (option.forceCompilation) disassembly(true);
         break;        
       case MEM_BASIC_BAILS:   
         markAsBasicData(BasicType.BASIC_BAILS);      
         if (option.forceCompilation) disassembly(true);
         break;     
       case MEM_BASIC_EVE:   
         markAsBasicData(BasicType.BASIC_EVE);      
         if (option.forceCompilation) disassembly(true);
         break;      
       case MEM_BASIC_TOOL:   
         markAsBasicData(BasicType.BASIC_TOOL);      
         if (option.forceCompilation) disassembly(true);
         break;      
       case MEM_BASIC_SUPER_EXPANDER:   
         markAsBasicData(BasicType.BASIC_SUPER_EXPANDER);      
         if (option.forceCompilation) disassembly(true);
         break;     
       case MEM_BASIC_TURTLE:   
         markAsBasicData(BasicType.BASIC_TURTLE);      
         if (option.forceCompilation) disassembly(true);
         break;        
       case MEM_BASIC_EASY:   
         markAsBasicData(BasicType.BASIC_EASY);      
         if (option.forceCompilation) disassembly(true);
         break;    
       case MEM_BASIC_V4:   
         markAsBasicData(BasicType.BASIC_V4);      
         if (option.forceCompilation) disassembly(true);
         break; 
       case MEM_BASIC_V5:   
         markAsBasicData(BasicType.BASIC_V5);      
         if (option.forceCompilation) disassembly(true);
         break;    
       case MEM_BASIC_EXPANDED_V20:   
         markAsBasicData(BasicType.BASIC_EXPANDED_V20);      
         if (option.forceCompilation) disassembly(true);
         break;     
       case MEM_BASIC_HANDY:   
         markAsBasicData(BasicType.BASIC_HANDY);      
         if (option.forceCompilation) disassembly(true);
         break;           
       case MEM_BASIC_V8:   
         markAsBasicData(BasicType.BASIC_V8);      
         if (option.forceCompilation) disassembly(true);
         break;          
         
         
       case SOURCE_DASM:
         export(Name.DASM);
         break;         
       case SOURCE_TMPX:  
         export(Name.TMPX);
         break;         
       case SOURCE_CA65:
         export(Name.CA65);  
         break;         
       case SOURCE_ACME:
         export(Name.ACME);  
         break;         
       case SOURCE_KICK:
         export(Name.KICK);  
         break;
       case SOURCE_TASS64:
         export(Name.TASS64);
         break;      
       case SOURCE_GLASS:
         export(Name.GLASS);
         break;     
       case SOURCE_AS:
         export(Name.AS);
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
       case HELP_IMPORT:
         importLabels();
         if (option.forceCompilation) disassembly(true);   
         break;  
       case HELP_REFACTOR:  
         refactor();  
         if (option.forceCompilation) disassembly(true); 
         break;
       case HELP_CLEARLAB:  
         clearLab();
         if (option.forceCompilation) disassembly(true); 
         break;  
       case HELP_CLEARCOM:  
         clearCom();
         if (option.forceCompilation) disassembly(true); 
         break;          
       case HELP_UNDO:
         undo();
         break;
       case APP_COPY:
         appCopy();  
         break;
       case APP_PASTE:
         appPaste();  
         if (option.forceCompilation) disassembly(true);   
         break;
    }
        
  }
  
  /**
   * Project new user action
   */
  private void projectNew() {
    if (project != null && !project.equals(savedProject)) {
      int input = JOptionPane.showConfirmDialog(this, "Project not saved. Save it? (No=not save it)", "Information", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE); 
      if (input==JOptionPane.CANCEL_OPTION) return;
      else if (input==JOptionPane.OK_OPTION) projectSave(); 
    } 
    
    project=new Project();
    savedProject=project.clone();
    projectFile=null;
    jProjectDialog.setUp(project);            
    jProjectDialog.setVisible(true);
    setTitle("JC64dis (<new>)");
    jPanelPerc.setPerc(-1);

    if (project.file==null || "".equals(project.file)) {
      project=null;
      savedProject=null;       
      rSyntaxTextAreaSource.setText("");
      rSyntaxTextAreaDis.setText("");
      rSyntaxTextAreaDisMin.setText("");
      rSyntaxTextAreaSourceMin.setText("");
      dataTableModelMemory.setData(null);
      dataTableModelMemory.fireTableDataChanged();
    } else {
      dataTableModelMemory.setData(project.memory);
      dataTableModelMemory.fireTableDataChanged();
      execute(SOURCE_DISASS);
    }
          
  }
  
  /**
   * Project close user action
   */
  private void projectClose() {
    if (project == null) return;
    
    if (!project.equals(savedProject)) {
      int input = JOptionPane.showConfirmDialog(this, "Project not saved. Save it? (No=not save it)", "Information", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE); 
      if (input==JOptionPane.CANCEL_OPTION) return;
      else if (input==JOptionPane.OK_OPTION) projectSave();               
    }       
    
    undo.clear();  // clearLab all previous undo action
  
    setTitle("JC64dis");
    project=null;
    savedProject=null;
    projectFile=null;
    rSyntaxTextAreaSource.setText("");
    rSyntaxTextAreaDis.setText("");
    rSyntaxTextAreaDisMin.setText("");
    rSyntaxTextAreaSourceMin.setText("");
    dataTableModelMemory.setData(null);
    dataTableModelMemory.fireTableDataChanged();
    jPanelPerc.setPerc(-1);
  }
  
  /**
   * Project open user action
   */
  private void projectOpen() {     
    if (project != null && !project.equals(savedProject)) {
      int input = JOptionPane.showConfirmDialog(this, "Project not saved. Save it? (No=not save it)", "Information", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE); 
      if (input==JOptionPane.CANCEL_OPTION) return;
      else if (input==JOptionPane.OK_OPTION) projectSave();      
    } 
    
    int retVal=projectChooserFile.showOpenDialog(this);
    if (retVal == JFileChooser.APPROVE_OPTION) {
      projectFile=projectChooserFile.getSelectedFile();

      m_prefNode.put(LAST_DIR_PROJECT, projectFile.getPath());

      project=new Project();
      setTitle("JC64dis ("+projectFile.getName()+")");
      if (!FileManager.instance.readProjectFile(projectFile , project)) {
          JOptionPane.showMessageDialog(this, "Error reading project file", "Error", JOptionPane.ERROR_MESSAGE);
      } else {
          if (option.pedantic) JOptionPane.showMessageDialog(this, "File read", "Information", JOptionPane.INFORMATION_MESSAGE);
          execute(SOURCE_DISASS);
        }
        savedProject=project.clone();
        dataTableModelMemory.setData(project.memory);
        dataTableModelMemory.fireTableDataChanged();
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
              if (option.pedantic) JOptionPane.showMessageDialog(this, "File saved", "Information", JOptionPane.INFORMATION_MESSAGE);
              savedProject=project.clone();
            }  
          recentFile.push(projectFile.getPath());
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
          
          if (!projectFile.getName().toLowerCase().endsWith(".dis")) {
            try {  
              projectFile = new File(projectFile.getCanonicalPath() +".dis");
            } catch (Exception e) {
                System.err.println(e);
              }  
          }
          
          m_prefNode.put(LAST_DIR_PROJECT, projectFile.getPath());
          
          projectChooserFile.setCurrentDirectory(new File(m_prefNode.get(LAST_DIR_PROJECT, "")));
          
          setTitle("JC64dis ("+projectFile.getName()+")");
          if (!FileManager.instance.writeProjectFile(projectFile , project)) {
            JOptionPane.showMessageDialog(this, "Error writing project file", "Error", JOptionPane.ERROR_MESSAGE);
          } else {
              if (option.pedantic) JOptionPane.showMessageDialog(this, "File saved", "Information", JOptionPane.INFORMATION_MESSAGE);
              savedProject=project.clone();
            }
          
          recentFile.push(projectFile.getPath());
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
         dataTableModelMemory.setData(project.memory);
         dataTableModelMemory.fireTableDataChanged();
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
                  if (option.pedantic) JOptionPane.showMessageDialog(this, "File saved", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            }
          }   
  }
 
  /**
   * Exit from program
   */
  private void exit() {
    if (project != null && !project.equals(savedProject)) {      
      int input = JOptionPane.showConfirmDialog(this, "Project not saved. Save it? (No=not save it)", "Information", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE); 
      if (input==JOptionPane.CANCEL_OPTION) return;
      if (input==JOptionPane.OK_OPTION) projectSave();
      System.exit(0);   
    } else System.exit(0);
  }
  
  /**
   * Clear the dasm label by adding a user null label
   */
  private void clearDasmComment() {
    MemoryDasm mem;   
      
    int rows[]=jTableMemory.getSelectedRows();
        
    for (int i=0; i<rows.length; i++) {
      mem=project.memory[rows[i]];
      
      // in data no automatic comments are added, so we can erase it directly
      if (mem.isData && mem.dasmComment!=null) {
        mem.dasmComment=null;
        // for compatibility, we erase a user "" comment
        if ("".equals(mem.userComment)) mem.userComment=null;
      } else if (mem.dasmComment!=null && mem.userComment==null) mem.userComment="";
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
   * Un-mark user selection
   */
  private void unmark() {
    MemoryDasm mem;   
      
    int rows[]=jTableMemory.getSelectedRows();
        
    for (int i=0; i<rows.length; i++) {
      mem= project.memory[rows[i]];
      mem.isCode=false;
      mem.isData=false;
      mem.isGarbage=false;
      mem.dataType=DataType.NONE;
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
      mem.dataType=DataType.NONE;
    }
    
    dataTableModelMemory.fireTableDataChanged();  
    jTableMemory.clearSelection();
    for (int i=0; i<rows.length; i++) {
      jTableMemory.addRowSelectionInterval(rows[i], rows[i]);  
    }
  }
  
  /**
   * Mark user selection as data
   * 
   * @param dataType the type of date
   */
  private void markAsData(DataType dataType) {
    MemoryDasm mem;   
      
    int rows[]=jTableMemory.getSelectedRows();

    switch (dataType) {
      case ZERO_TEXT:
        if (rows.length<=1) {
          JOptionPane.showMessageDialog(this, "Too few elements", "Warning", JOptionPane.WARNING_MESSAGE);
          break;   
        }   
        // we must find area that terminate with a 0
        int pos;
        boolean fount=false;
        for (pos=1; pos<rows.length; pos++) {
          if (project.memory[rows[pos]].copy==0) {
            fount=true;  
            break;
          }    
        }
        
        // look if the area is crossing a zero terminated one
        if (!fount && rows[pos-1]<0xFFFF && project.memory[rows[pos-1]+1].dataType==DataType.ZERO_TEXT) {
          pos--;  
          fount=true;
        }

        if (fount) {
          int lastRow=rows[pos];  
          for (int i=pos; i>=0; i--) {
            if (lastRow-rows[i]>1) break;
            lastRow=rows[i];
          
            setMem(project.memory[rows[i]], dataType, rows[i]);  
          }  
          
          pos++;
          while (pos>0 && pos<rows.length) {
            pos=searchMoreZero(rows, pos);
          }
        } else JOptionPane.showMessageDialog(this, "This area is not zero terminated", "Warning", JOptionPane.WARNING_MESSAGE); 
        break;
      case NUM_TEXT:
        if (rows.length==0) break;
        
        boolean over=false;
        boolean few=false;
        int num=project.memory[rows[0]].copy;
        
        if (num>=rows.length) few=true;        
        if (project.memory[rows[0]].dataType==DataType.NUM_TEXT) over=true;

        int lastRow=rows[0];
        if (num>0 && !few) {
          for (pos=1; pos<num; pos++) {            
            if (project.memory[rows[pos]].dataType==DataType.NUM_TEXT) {
              over=true;
              break;
            }  
            if (rows[pos]-lastRow>1) {
              few=true;  
              break;
            }
            lastRow=rows[pos];
          }
        } else pos=0;
        
        if (over) {
          JOptionPane.showMessageDialog(this, "Overlapping area for text definition", "Warning", JOptionPane.WARNING_MESSAGE);
          break;
        }  
        
        if (few) {
          JOptionPane.showMessageDialog(this, "Too few elements for a string of "+num+" chars", "Warning", JOptionPane.WARNING_MESSAGE);
          break;  
        }
        
        if (pos>0) {
          for (int i=0; i<=pos; i++) {
            setMem(project.memory[rows[i]], dataType, rows[i]); 
          }  
        }        
        break;  
      case HIGH_TEXT:      
        if (rows.length<=1) {
          JOptionPane.showMessageDialog(this, "Too few elements", "Warning", JOptionPane.WARNING_MESSAGE);
          break;   
        }  
          
        if ((project.memory[rows[0]].copy & 0x80)!=0) {
          JOptionPane.showMessageDialog(this, "Area can not start with high bit", "Warning", JOptionPane.WARNING_MESSAGE);
          break;
        }
        
        fount=false;
        for (pos=1; pos<rows.length; pos++) {
          if ((project.memory[rows[pos]].copy & 0x80)!=0) {
            fount=true;
            break;
          }    
        }
        
        // look if the area is crossing a high terminated one
        if (!fount && rows[pos-1]<0xFFFF && project.memory[rows[pos-1]+1].dataType==DataType.HIGH_TEXT) {
          pos--;  
          fount=true;
        }

        if (fount) {
          lastRow=rows[pos];  
          for (int i=pos; i>=0; i--) {
            if (lastRow-rows[i]>1) break;
            lastRow=rows[i];
          
            setMem(project.memory[rows[i]], dataType, rows[i]);  
          }          
        } else JOptionPane.showMessageDialog(this, "This area is not high bit terminated", "Warning", JOptionPane.WARNING_MESSAGE); 
        break; 
      case SHIFT_TEXT:
        // we must find area that are not with bit 0 at 1
        for (pos=0; pos<rows.length; pos++) {
          if ((project.memory[rows[pos]].copy & 0x01)!=0) break;    
        }
        
        if (pos==0)  {
          JOptionPane.showMessageDialog(this, "This area start with wrong low bit", "Warning", JOptionPane.WARNING_MESSAGE);  
        } else {
        
          lastRow=rows[pos-1];  
          for (int i=pos-1; i>=0; i--) {
            if (lastRow-rows[i]>1) break;
            lastRow=rows[i];
          
            setMem(project.memory[rows[i]], dataType, rows[i]); 
          }          
        }
        break;  
      default:              
        for (int i=0; i<rows.length; i++) {
          setMem(project.memory[rows[i]], dataType, rows[i]);
        }
       break;
    }
    
    dataTableModelMemory.fireTableDataChanged();  
    jTableMemory.clearSelection();
    for (int i=0; i<rows.length; i++) {
      jTableMemory.addRowSelectionInterval(rows[i], rows[i]);  
    }
  }
  
   /**
   * Mark user selection as basic type data
   * 
   * @param basicType the type of basic data
   */
  private void markAsBasicData(BasicType basicType) {
    MemoryDasm mem;   
      
    int rows[]=jTableMemory.getSelectedRows();
    
    for (int i=0; i<rows.length; i++) {
      mem=project.memory[rows[i]];
      mem.basicType=basicType;
      mem.isData=true;
      mem.isCode=false;
      mem.isGarbage=false;
      // force byte data type
      if (mem.dataType!=DataType.BYTE_BIN && 
          mem.dataType!=DataType.BYTE_CHAR && 
          mem.dataType!=DataType.BYTE_DEC && 
          mem.dataType!=DataType.BYTE_HEX) mem.dataType=DataType.BYTE_HEX;
      jTableMemory.addRowSelectionInterval(rows[i], rows[i]);  
    }
  }
  
  /**
   * Search for more zero text area
   * @param rows the rows selected
   * @param initial the initial position
   * @return the found position or -1
   */
  private int searchMoreZero(int rows[], int initial) {
    boolean fount=false;
    int pos;

    for (pos=initial; pos<rows.length; pos++) {
      if (project.memory[rows[pos]].copy==0) {
        fount=true;  
        break;
      }    
    }
        
    // look if the area is crossing a zero terminated one
    if (!fount && rows[pos-1]<0xFFFF && project.memory[rows[pos-1]+1].dataType==DataType.ZERO_TEXT) {
      pos--;  
      fount=true;
    }

    if (fount) {
      int lastRow=rows[pos];  
      for (int i=pos; i>=0; i--) {
        if (lastRow-rows[i]>1) break;
        lastRow=rows[i];
          
        setMem(project.memory[rows[i]], DataType.ZERO_TEXT, rows[i]);  
      }  
      return pos+1;          
    } else return -1;
  }
  
  /**
   * Set the memory with the given type
   * 
   * @param mem the memory location to set
   * @param dataType the new datatype to set
   * @param pos the position where it is in memory
   */
  private void setMem(MemoryDasm mem, DataType dataType, int pos) {
    mem.isData=true;
    mem.isCode=false;
    mem.isGarbage=false;
    if (mem.dataType!=dataType && mem.dataType==DataType.ZERO_TEXT) removeBelowType(pos, DataType.ZERO_TEXT);
    if (mem.dataType!=dataType && mem.dataType==DataType.NUM_TEXT) removeType(pos, DataType.NUM_TEXT);
    if (mem.dataType!=dataType && mem.dataType==DataType.HIGH_TEXT) removeBelowType(pos, DataType.HIGH_TEXT);
    mem.dataType=dataType;
    if (option.eraseDComm) mem.dasmComment=null;
    if (option.erasePlus) {
      switch (mem.type) {
        case TYPE_PLUS:
        case TYPE_MINUS:  
          mem.related=-1;
          mem.type=TYPE_EMPTY;
          break;
        case TYPE_PLUS_MAJOR:
        case TYPE_MINUS_MAJOR:  
          mem.related&=0xFFFF;
          mem.type=TYPE_MAJOR;
          break;
        case TYPE_PLUS_MINOR:
        case TYPE_MINUS_MINOR:  
          mem.related&=0xFFFF; 
          mem.type=TYPE_MINOR;
          break;
      }
    }
  }
  
  /**
   * Remove text type that are below this point
   * 
   * @param pos the position
   * @param type the type to remove
   */
  private void removeBelowType(int pos, DataType type) {
    if (pos==0) return;
    for (int i=pos-1; i>=0; i--) {
      if (project.memory[i].dataType==DataType.ZERO_TEXT && project.memory[i].copy==0 ) break;
      if (project.memory[i].dataType==type) project.memory[i].dataType=DataType.NONE;
      else break;
    }                
  }
  
  /**
   * Remove text type that are above or below this point
   * 
   * @param pos the position
   * @param type the type to remove
   */
  private void removeType(int pos, DataType type) {
    if (pos==0) return;
    for (int i=pos-1; i>=0; i--) {
      if (project.memory[i].dataType==type) project.memory[i].dataType=DataType.NONE;
      else break;
    }
    
    if (pos==0xFFFF) return;
    for (int i=pos+1; i<0xFFFF; i++) {
      if (project.memory[i].dataType==type) project.memory[i].dataType=DataType.NONE;
      else break;  
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
      mem.dasmLocation=null;
      if (option.eraseDComm) mem.dasmComment=null;
      if (option.erasePlus) {         
        switch (mem.type) {
          case TYPE_PLUS:
          case TYPE_MINUS:
            mem.related=-1;
            mem.type=TYPE_EMPTY;
            break;
          case TYPE_PLUS_MAJOR:
          case TYPE_MINUS_MAJOR:
            mem.related&=0xFFFF;
            mem.type=TYPE_MAJOR;
            break;
          case TYPE_PLUS_MINOR:
          case TYPE_MINUS_MINOR:  
            mem.related&=0xFFFF;
            mem.type=TYPE_MINOR;
            break;
        }
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
               
               // code execution has priority over data access
               if (project.memory[i].isCode) project.memory[i].isData=false;
           }
           if (option.pedantic) JOptionPane.showMessageDialog(this, "Operation done.", "Info", JOptionPane.INFORMATION_MESSAGE);  
           
           dataTableModelMemory.fireTableDataChanged();            
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
    
   if (option.pedantic) JOptionPane.showMessageDialog(this, mpr.getDescription(), "Information on saved file", JOptionPane.INFORMATION_MESSAGE);
  }
  
  /**
   * Option for show labels
   */
  private void optionLabels() {
    if (project==null) {
      JOptionPane.showMessageDialog(this,"No project. Open a project first", "Warning", JOptionPane.WARNING_MESSAGE); 
      return;
    }
    jLabelsDialog.setUp(project.memory, jTableMemory, rSyntaxTextAreaDis, project);
    jLabelsDialog.setVisible(true);
  }
  
  /**
   * Use font size in option
   */
  private void useFont() {
    Font font;
       
    font=rSyntaxTextAreaDis.getFont();
    if (font.getSize() != option.sizePreviewFont) rSyntaxTextAreaDis.setFont(font.deriveFont(font.getStyle(), option.sizePreviewFont));  
    
    font=rSyntaxTextAreaSource.getFont();
    if (font.getSize() != option.sizeSourceFont) rSyntaxTextAreaSource.setFont(font.deriveFont(font.getStyle(), option.sizeSourceFont));  
  }
  
  /**
   * Disassembly the memory
   * 
   * @param storeUndo true if we store for the undo the compiled project
   */
  private void disassembly(boolean storeUndo) {
    useFont();  
      
    if (project==null) {
      disassembly.source="";
      disassembly.disassembly="";
    } else {
        disassembly.dissassembly(project.fileType, project.inB, option, 
                                 project.memory, project.constant, project.mpr, 
                                 project.relocates, project.patches, 
                                 project.chip, project.binAddress, 
                                 project.targetType, false);
        disassembly.dissassembly(project.fileType, project.inB, option,
                                 project.memory, project.constant, project.mpr,
                                 project.relocates, project.patches, 
                                 project.chip, project.binAddress,
                                 project.targetType, true);
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
    if (option.showMiniature) rSyntaxTextAreaSourceMin.setText(disassembly.source); 
    else rSyntaxTextAreaSourceMin.setText("");
    try {
      rSyntaxTextAreaSource.setCaretPosition(rSyntaxTextAreaSource.getDocument()
                        .getDefaultRootElement().getElement(lineS)
                        .getStartOffset());
      rSyntaxTextAreaSource.requestFocusInWindow();
    } catch (Exception ex) {
        System.err.println(ex);
    }
    
    rSyntaxTextAreaDis.setText(disassembly.disassembly);
    if (option.showMiniature) rSyntaxTextAreaDisMin.setText(disassembly.disassembly);
    else rSyntaxTextAreaDisMin.setText("");
    try {
      rSyntaxTextAreaDis.setCaretPosition(rSyntaxTextAreaDis.getDocument()
                        .getDefaultRootElement().getElement(lineD)
                        .getStartOffset());
      rSyntaxTextAreaDis.requestFocusInWindow();
    } catch (Exception ex) {
        System.err.println(ex);
    }       
    
    memoryTableCellRenderer.setDisassembly(disassembly);
    
    // repositionate in memory if option is on
    if (option.repositionate) gotoMem(rSyntaxTextAreaDis, disassembly.caretsPreview, 0);
    
    DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");    
    if (storeUndo) undo.store(df.format(new Date()), project);
    
    // update perc based onto the work done by renaming the labels
    int total=0;
    int done=0;
    for (MemoryDasm mem:project.memory) {
      if (!mem.isInside || mem.isGarbage) continue;
      
      // look for relative locations
      if (mem.type==TYPE_PLUS || mem.type==TYPE_MINUS) {
        MemoryDasm memr=project.memory[mem.related];
        if (memr.userLocation!=null && !"".equals(memr.userLocation)) {
          total++;
          done++;
        }                   
        continue;
      }  
      
      if (mem.type==TYPE_PLUS_MAJOR || mem.type==TYPE_PLUS_MINOR ||
          mem.type==TYPE_MINUS_MAJOR || mem.type==TYPE_MINUS_MINOR) {
        MemoryDasm memr=project.memory[(mem.related>>16) & 0xFFFF];
        if (memr.userLocation!=null && !"".equals(memr.userLocation)) {
          total++;
          done++;
        }                   
        continue;
      }  
      
      // look for locations
      if (mem.userLocation!=null && !"".equals(mem.userLocation)) {
        total++;
        done++;
      } else {
          if (mem.dasmLocation!=null && !"".equals(mem.dasmLocation)) {
            total++;  
          }
        }  
    }
    if (total!=0) jPanelPerc.setPerc((float)done/(float)total);
    else jPanelPerc.setPerc(0);
  }

  /**
   * Add a user label to the selected memory address
   */
  private void addComment() {
    int row=jTableMemory.getSelectedRow();
    addComment(row);
  }
  
   /**
   * Add a user label to the given row
   * 
   * @param row the row to use
   */
  private void addComment(int row) {
    if (row<0) {
      JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
    }
      
    MemoryDasm mem= project.memory[row];
    String comment=JOptionPane.showInputDialog(this, "Insert the comment for the selected memory location", mem.userComment);
    
    if (comment!=null) mem.userComment=comment;  
    
    dataTableModelMemory.fireTableDataChanged(); 
    jTableMemory.setRowSelectionInterval(row, row); 
  } 

  /**
   * Add a user label to the selected memory address
   */
  private void addLabel() {
    int row=jTableMemory.getSelectedRow();
    
    addLabel(row);
  }
  
  /**
   * Add a user label to the selected memory address
   * 
   * @param row the row to use
   */
  private void addLabel(int row) {
    if (row<0) {
      JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
    }
    
    MemoryDasm mem= project.memory[row];
    addLabel(mem);
    dataTableModelMemory.fireTableDataChanged(); 
    jTableMemory.setRowSelectionInterval(row, row);    
  }
  
  /**
   * Add a user label to the next word address of selected memory address
   */
  private void addLabelOp() {
    int row = jTableMemory.getSelectedRow();
    if (row < 0) {
      JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);
      return;
    }

    // avoid to read over the end
    if (row >= 0xFFFE) {
      JOptionPane.showMessageDialog(this, "Address locate over $FFFF boundary", "Warning", JOptionPane.WARNING_MESSAGE);
      return;
    }

    MemoryDasm mem = null;

    switch (project.targetType) {
      case C128:
      case C64:
      case C1541:
      case PLUS4:
      case VIC20:
      case ATARI:
        // determine if it is of page zero or 16 bit
        switch (M6510Dasm.tableSize[project.memory[row].copy & 0xFF]) {
          case 1:
            JOptionPane.showMessageDialog(this, "Instruction without operand. Skip action", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
          case 2:
            // avoid to use not defined bytes
            if (!project.memory[row + 1].isInside) {
              JOptionPane.showMessageDialog(this, "Address is incomplete. Skip action", "Warning", JOptionPane.WARNING_MESSAGE);
              return;
            }

            // get next address
            mem = project.memory[project.memory[row + 1].copy & 0xFF];
            break;
          case 3:
            // avoid to use not defined bytes
            if (!project.memory[row + 1].isInside || !project.memory[row + 2].isInside) {
              JOptionPane.showMessageDialog(this, "Address is incomplete. Skip action", "Warning", JOptionPane.WARNING_MESSAGE);
              return;
            }

            // get next address
            mem = project.memory[(project.memory[row + 2].copy & 0xFF) * 256 + (project.memory[row + 1].copy & 0xFF)];
            break;
        }
        break;
      case C128Z:
        int op,
         iType,
         steps;
        op = project.memory[row].copy & 0xFF;

        iType = Z80Dasm.tableMnemonics[op];

        switch (iType) {
          case Z80Dasm.T_CB:
            op = project.memory[row + 1].copy & 0xFF;
            steps = Z80Dasm.tableSizeCB[op];
            break;
          case Z80Dasm.T_DD:
            op = project.memory[row + 1].copy & 0xFF;
            steps = Z80Dasm.tableSizeDD[op];

            if (Z80Dasm.tableMnemonicsDD[op] == Z80Dasm.T_DDCB) {
              // there are an extra table  
              op = project.memory[row + 2].copy & 0xFF;
              steps = Z80Dasm.tableSizeDDCB[op];
            }
            break;
          case Z80Dasm.T_ED:
            op = project.memory[row + 1].copy & 0xFF;
            steps = Z80Dasm.tableSizeED[op];
            break;
          case Z80Dasm.T_FD:
            op = project.memory[row + 1].copy & 0xFF;
            steps = Z80Dasm.tableSizeFD[op];

            if (Z80Dasm.tableMnemonicsFD[op] == Z80Dasm.T_FDCB) {
              op = project.memory[row + 2].copy & 0xFF;
              steps = Z80Dasm.tableSizeFDCB[op];
            }
            break;
          default:

            steps = Z80Dasm.tableSize[op];
            break;
        }

        switch (steps) {
          case 1:
            JOptionPane.showMessageDialog(this, "Instruction without operand. Skip action", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
          case 3:
            // avoid to use not defined bytes
            if (!project.memory[row + 1].isInside || !project.memory[row + 2].isInside) {
              JOptionPane.showMessageDialog(this, "Address is incomplete. Skip action", "Warning", JOptionPane.WARNING_MESSAGE);
              return;
            }

            // get next address
            mem = project.memory[(project.memory[row + 2].copy & 0xFF) * 256 + (project.memory[row + 1].copy & 0xFF)];
            break;
          default:
            JOptionPane.showMessageDialog(this, "Not jet implemented for this combination of bytes. Skip action", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        break;
    }

    addLabel(mem);
    dataTableModelMemory.fireTableDataChanged();
    jTableMemory.setRowSelectionInterval(row, row);
  }
  
  /**
   * Add the label in given memory
   * 
   * @param mem the mem to use 
   */
  private void addLabel(MemoryDasm mem) {
    String initial="";
    if (mem.userLocation!=null) initial=mem.userLocation;
    else if (mem.dasmLocation!=null && !"".equals(mem.dasmLocation)) initial=mem.dasmLocation;
    
    String label=JOptionPane.showInputDialog(this, "Insert the label for the selected memory location", initial);  
    if (label!=null) {
      if ("".equals(label)) {
        if (option.pedantic) JOptionPane.showMessageDialog(this, "User label erased", "Information", JOptionPane.INFORMATION_MESSAGE);   
        mem.userLocation=null;
        return;
      }  
      
        String error=errorLabel(label);
        if (error!=null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);   
            return;
        }
      
      mem.userLocation=label;
    }  
  }
  
  /**
   * Go to the label onto the instructions (like JMP, ...)
   */
  private void gotoLabelOp() {
    int row = jTableMemory.getSelectedRow();
    if (row < 0 || row >= 0xFFFE) return;  // avoid invalid address
    
    MemoryDasm mem = null;

    switch (project.targetType) {
      case C128:
      case C64:
      case C1541:
      case PLUS4:
      case VIC20:
      case ATARI:
        // determine if it is of page zero or 16 bit
        switch (M6510Dasm.tableSize[project.memory[row].copy & 0xFF]) {
          case 2:
            // avoid to use not defined bytes
            if (!project.memory[row + 1].isInside)  return;

            // get next address
            mem = project.memory[project.memory[row + 1].copy & 0xFF];
            break;
          case 3:
            // avoid to use not defined bytes
            if (!project.memory[row + 1].isInside || !project.memory[row + 2].isInside) return;

            // get next address
            mem = project.memory[(project.memory[row + 2].copy & 0xFF) * 256 + (project.memory[row + 1].copy & 0xFF)];
            break;
        }
        break;
      case C128Z:
        int op,
         iType,
         steps;
        op = project.memory[row].copy & 0xFF;

        iType = Z80Dasm.tableMnemonics[op];

        switch (iType) {
          case Z80Dasm.T_CB:
            op = project.memory[row + 1].copy & 0xFF;
            steps = Z80Dasm.tableSizeCB[op];
            break;
          case Z80Dasm.T_DD:
            op = project.memory[row + 1].copy & 0xFF;
            steps = Z80Dasm.tableSizeDD[op];

            if (Z80Dasm.tableMnemonicsDD[op] == Z80Dasm.T_DDCB) {
              // there are an extra table  
              op = project.memory[row + 2].copy & 0xFF;
              steps = Z80Dasm.tableSizeDDCB[op];
            }
            break;
          case Z80Dasm.T_ED:
            op = project.memory[row + 1].copy & 0xFF;
            steps = Z80Dasm.tableSizeED[op];
            break;
          case Z80Dasm.T_FD:
            op = project.memory[row + 1].copy & 0xFF;
            steps = Z80Dasm.tableSizeFD[op];

            if (Z80Dasm.tableMnemonicsFD[op] == Z80Dasm.T_FDCB) {
              op = project.memory[row + 2].copy & 0xFF;
              steps = Z80Dasm.tableSizeFDCB[op];
            }
            break;
          default:

            steps = Z80Dasm.tableSize[op];
            break;
        }

        switch (steps) {
          case 3:
            // avoid to use not defined bytes
            if (!project.memory[row + 1].isInside || !project.memory[row + 2].isInside) return;

            // get next address
            mem = project.memory[(project.memory[row + 2].copy & 0xFF) * 256 + (project.memory[row + 1].copy & 0xFF)];
            break;
          default:
            return;
        }

        break;
    }

    // avoid invlaid memory detected
    if (mem==null) return;
    
    int actual;

    // get the address in hex format
    int addr = mem.address;
    int pos = 0;
    
    // save current memory position
    callStack.push(project.memory[row].address);

    // try with carets
    if (addr >= 0) {
      pos = disassembly.caretsPreview.getPosition(project.memory[addr]);

      if (pos != -1) {
        rSyntaxTextAreaDis.setCaretPosition(pos);
        rSyntaxTextAreaDis.requestFocusInWindow();
        return;
      } else {
        pos = 0;
      }
    }

    // scan all lines for the memory location
    try {
      String preview = rSyntaxTextAreaDis.getText();
      String lines[] = preview.split("\\r?\\n");
      for (String line : lines) {
        actual = searchAddress(line.substring(0, Math.min(line.length(), option.maxLabelLength)));
        if (actual == addr) {
          // set preview in the find position  
          rSyntaxTextAreaDis.setCaretPosition(pos);
          rSyntaxTextAreaDis.requestFocusInWindow();
          break;
        } else {
          pos += line.length() + 1;
        }
      }
    } catch (Exception e) {
      System.err.println();
    }
    
    dataTableModelMemory.fireTableDataChanged();
    jTableMemory.setRowSelectionInterval(row, row);
  }
  
  /**
   * Jump back in stacked flow
   */
  private void jumpBack() {
    if (callStack.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Empty calling stack", "Warning", JOptionPane.WARNING_MESSAGE); 
      return;
    }
    
    int actual;
    
    int addr=callStack.pop();
    int pos=0;

    // try with carets
    if (addr >= 0) {
      pos = disassembly.caretsPreview.getPosition(project.memory[addr]);

      if (pos != -1) {
        rSyntaxTextAreaDis.setCaretPosition(pos);
        rSyntaxTextAreaDis.requestFocusInWindow();
        return;
      } else {
        pos = 0;
      }
    }

    // scan all lines for the memory location
    try {
      String preview = rSyntaxTextAreaDis.getText();
      String lines[] = preview.split("\\r?\\n");
      for (String line : lines) {
        actual = searchAddress(line.substring(0, Math.min(line.length(), option.maxLabelLength)));
        if (actual == addr) {
          // set preview in the find position  
          rSyntaxTextAreaDis.setCaretPosition(pos);
          rSyntaxTextAreaDis.requestFocusInWindow();
          break;
        } else {
          pos += line.length() + 1;
        }
      }
    } catch (Exception e) {
      System.err.println();
    }
  }

  /**
   * Add a block for comment
   */
  private void addBlock() {
    int row=jTableMemory.getSelectedRow();
        
    addBlock(row);
  }
  
  /**
   * Add a block for comment
   * 
   * @param row the row to use
   */
  private void addBlock(int row) {
    if (row<0) {
      JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
    }
      
    MemoryDasm mem= project.memory[row];
    JTextArea area=new JTextArea(20,80);
    area.setText(mem.userBlockComment);
    area.setFont(new Font("monospaced", Font.PLAIN, 12)); 

    JScrollPane scrollPane = new JScrollPane(area);  
    
 /*   area.addAncestorListener(new AncestorListener() {
    @Override
    public void ancestorRemoved(AncestorEvent event) {}

    @Override
    public void ancestorMoved(AncestorEvent event) {}

    @Override
    public void ancestorAdded(AncestorEvent event) {
       System.err.println(event.getComponent().requestFocusInWindow());
    }
    });*/
         

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          Robot robot = new Robot();
          int i = 2;
          while (i-- > 0) {
            robot.keyPress(KeyEvent.VK_TAB);
            robot.delay(100);
            robot.keyRelease(KeyEvent.VK_TAB);
          }
        } catch (AWTException e) {
            System.out.println("Failed to use Robot, got exception: " + e.getMessage());
          }
      }
    });
    
    if (JOptionPane.showConfirmDialog(null, scrollPane, "Add a multi lines block comment", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
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
    
    // erase all if no row selected
    if (rows.length==0) {
      clearLab();
      return;
    }
        
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
   * Assign a reference to memory as #< and manage + or - if present
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
    cols.add("REL");
        
    Vector rows=new Vector();
    Vector data;
    
    int value=project.memory[row].copy & 0xFF;
    
    for (MemoryDasm memory : project.memory) {    
      if ((memory.address & 0xFF)==value) {
        if (!memory.isInside && memory.userLocation==null) continue;
          
        data=new Vector();
          
        data.add(Shared.ShortToExe(memory.address));
        data.add(memory.dasmLocation);
        data.add(memory.userLocation);
        
        String res="";
        switch (memory.type) {
          case TYPE_PLUS:
            {
              /// this is a memory in table label
              int pos=memory.address-memory.related;
              MemoryDasm mem2=project.memory[memory.related];
              if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) res=mem2.userLocation+"+"+pos;  
              else if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) res=mem2.dasmLocation+"+"+pos;
              else res="$"+Shared.ShortToExe((int)memory.related)+"+"+pos;
              break;
            }
          case TYPE_PLUS_MAJOR:
          case TYPE_PLUS_MINOR:
            {
              /// this is a memory in table label
              int rel=(memory.related>>16) &0xFFFF;
              int pos=memory.address-rel;
              MemoryDasm mem2=project.memory[rel];
              if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) res=mem2.userLocation+"+"+pos;
              else if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) res=mem2.dasmLocation+"+"+pos;
              else res="$"+Shared.ShortToExe(rel)+"+"+pos;
              break;
            }
          case TYPE_MINUS:
            {
              /// this is a memory in table label
              int pos=memory.address-memory.related;
              MemoryDasm mem2=project.memory[memory.related];
              if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) res=mem2.userLocation+pos;
              else if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) res=mem2.dasmLocation+pos;
              else res="$"+Shared.ShortToExe((int)memory.related)+pos;
              break;
            }
          case TYPE_MINUS_MAJOR:
          case TYPE_MINUS_MINOR:
            {
              /// this is a memory in table label
              int rel=(memory.related>>16) &0xFFFF;
              int pos=memory.address-rel;
              MemoryDasm mem2=project.memory[rel];
              if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) res=mem2.userLocation+pos;
              else if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) res=mem2.dasmLocation+pos;
              else res="$"+Shared.ShortToExe(rel)+"+"+pos;
              break;
            }            
        }
        
        data.add(res);          
        rows.add(data);
      }
    }

    JTable table = new JTable(rows, cols);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.getColumnModel().getColumn(0).setPreferredWidth(18);
    
    if (JOptionPane.showConfirmDialog(null, new JScrollPane(table), 
            "Select the address to use as #<", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
      
      project.memory[row].relatedAddressBase=0;
      project.memory[row].relatedAddressDest=0;
        
      int rowS=table.getSelectedRow();
      if (rowS<0) {
        if (project.memory[row].type==TYPE_MAJOR || 
            project.memory[row].type==TYPE_MINOR || 
            project.memory[row].type==TYPE_PLUS_MAJOR || 
            project.memory[row].type==TYPE_PLUS_MINOR || 
            project.memory[row].type==TYPE_MINUS_MAJOR || 
            project.memory[row].type==TYPE_MINUS_MINOR) {
          if (JOptionPane.showConfirmDialog(this, "Did you want to delete the current address association?", "No selection were done, so:", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            // + and <, or - ans < ?
              switch (project.memory[row].type) { 
                case TYPE_PLUS_MAJOR:
                  project.memory[row].type=TYPE_PLUS;
                  project.memory[row].related>>=8;
                  break;
                case TYPE_MINUS_MAJOR:
                  project.memory[row].type=TYPE_MINUS;
                  project.memory[row].related>>=8;
                  break;
                default:
                  project.memory[row].type=TYPE_EMPTY;
                  project.memory[row].related=-1;
                  break;
              }
          }
        } else JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
        return;
      } else {                                
          switch (project.memory[row].type) {
            case TYPE_PLUS:
              project.memory[row].type=TYPE_PLUS_MINOR;
              project.memory[row].related=(project.memory[row].related<<16)+Integer.parseInt((String)table.getValueAt(rowS, 0),16);
              break;
            case TYPE_MINUS:
              project.memory[row].type=TYPE_MINUS_MINOR;
              project.memory[row].related=(project.memory[row].related<<16)+Integer.parseInt((String)table.getValueAt(rowS, 0),16);
              break;
            default:
              project.memory[row].type=TYPE_MINOR;
              project.memory[row].related=Integer.parseInt((String)table.getValueAt(rowS, 0),16);
              break;
          }
        }
       
      dataTableModelMemory.fireTableDataChanged();      
      jTableMemory.setRowSelectionInterval(row, row); 
    }       
  }

  /**
   * Assign two references to memory as #< and #>
   * In presence of + or - it uses both definitions over < and >
   */
  private void memLowHigh() {
    int row=jTableMemory.getSelectedRow();
    if (row<0) {
      JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
    }
    
    int rows[]=jTableMemory.getSelectedRows();
    if (rows.length==1) {
      JOptionPane.showMessageDialog(this, "At least two rows must be selected", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
    }    
    
    MemoryDasm low, high;
    int address;
    
    for (int i=0; i<rows.length; i+=2) {
            
      if (i==rows.length-1) break;
      low=project.memory[rows[i]];
      high=project.memory[rows[i+1]];
    
      address=(low.copy & 0xFF) + ((high.copy & 0xFF)<<8);
    
      low.related=address;          
      switch (low.type) {
        case TYPE_PLUS:
          high.type=TYPE_PLUS_MINOR;      
          break;
        case TYPE_MINUS:
          high.type=TYPE_MINUS_MINOR;
          break;
        default:
          low.type=TYPE_MINOR;
          break;
      }
      
      high.related=address;
      switch (high.type) {
        case TYPE_PLUS:
          high.type=TYPE_PLUS_MAJOR;     
          break;
        case TYPE_MINUS:
          high.type=TYPE_MINUS_MAJOR;
          break;
        default:
              high.type=TYPE_MAJOR;
          break;
      }
      
      low.relatedAddressBase=0;
      low.relatedAddressDest=0;
      high.relatedAddressBase=0;
      high.relatedAddressDest=0;
    }
      
    dataTableModelMemory.fireTableDataChanged();      
    jTableMemory.setRowSelectionInterval(rows[0], rows[rows.length-1]);        
  }  
  
/**
   * Assign two references to memory as #> and #< 
   * In presence of + or - it uses both definitions over < and >
   */
  private void memHighLow() {
    int row=jTableMemory.getSelectedRow();
    if (row<0) {
      JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
    }
    
    int rows[]=jTableMemory.getSelectedRows();
    if (rows.length==1) {
      JOptionPane.showMessageDialog(this, "Two rows must be selected", "Warning", JOptionPane.WARNING_MESSAGE);  
      return;
    }    
    
    MemoryDasm low, high;
    int address; 
    
    for (int i=0; i<rows.length; i+=2) {
            
      if (i==rows.length-1) break;
      low=project.memory[rows[i+1]];
      high=project.memory[rows[i]];
    
      address=(low.copy & 0xFF) + ((high.copy & 0xFF)<<8);
    
      low.related=address;          
      switch (low.type) {
        case TYPE_PLUS:
          low.type=TYPE_PLUS_MINOR;
          break;
        case TYPE_MINUS:
          low.type=TYPE_MINUS_MINOR;
          break;
        default:
          low.type=TYPE_MINOR;
          break;
      }
            
      high.related=address;
      switch (high.type) {
        case TYPE_PLUS:
          high.type=TYPE_PLUS_MAJOR;    
          break;
        case TYPE_MINUS:
          high.type=TYPE_MINUS_MAJOR;
          break;
        default:
          high.type=TYPE_MAJOR;
          break;
      }
      
      low.relatedAddressBase=0;
      low.relatedAddressDest=0;
      high.relatedAddressBase=0;
      high.relatedAddressDest=0;
    }     
      
    dataTableModelMemory.fireTableDataChanged();      
    jTableMemory.setRowSelectionInterval(rows[0], rows[rows.length-1]);        
  }    
  
  /**
   * Assign a reference to memory as #> and manage + or - if present
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
    cols.add("REL");
        
    Vector rows=new Vector();
    Vector data;
    
    int value=project.memory[row].copy & 0xFF;
    
    for (MemoryDasm memory : project.memory) {      
      if (((memory.address>>8) & 0xFF)==value) {
        if (!memory.isInside && memory.userLocation==null) continue;  
          
        data=new Vector();
          
        data.add(Shared.ShortToExe(memory.address));
        data.add(memory.dasmLocation);
        data.add(memory.userLocation);
        
        String res="";
        switch (memory.type) {
          case TYPE_PLUS:
            {
              /// this is a memory in table label
              int pos=memory.address-memory.related;
              MemoryDasm mem2=project.memory[memory.related];
              if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) res=mem2.userLocation+"+"+pos;  
              else if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) res=mem2.dasmLocation+"+"+pos;
              else res="$"+Shared.ShortToExe((int)memory.related)+"+"+pos;
              break;
            }
          case TYPE_PLUS_MAJOR:
          case TYPE_PLUS_MINOR:
            {
              /// this is a memory in table label
              int rel=(memory.related>>16) &0xFFFF;
              int pos=memory.address-rel;
              MemoryDasm mem2=project.memory[rel];
              if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) res=mem2.userLocation+"+"+pos;
              else if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) res=mem2.dasmLocation+"+"+pos;
              else res="$"+Shared.ShortToExe(rel)+"+"+pos;
              break;
            }
          case TYPE_MINUS:
            {
              /// this is a memory in table label
              int pos=memory.address-memory.related;
              MemoryDasm mem2=project.memory[memory.related];
              if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) res=mem2.userLocation+pos;
              else if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) res=mem2.dasmLocation+pos;
              else res="$"+Shared.ShortToExe((int)memory.related)+pos;
              break;
            }
          case TYPE_MINUS_MAJOR:
          case TYPE_MINUS_MINOR:
            {
              /// this is a memory in table label
              int rel=(memory.related>>16) &0xFFFF;
              int pos=memory.address-rel;
              MemoryDasm mem2=project.memory[rel];
              if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) res=mem2.userLocation+pos;
              else if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) res=mem2.dasmLocation+pos;
              else res="$"+Shared.ShortToExe(rel)+pos;
              break;
            }            
        }
        
        data.add(res);         
        rows.add(data);
      }
    }
    
    JTable table = new JTable(rows, cols);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.getColumnModel().getColumn(0).setPreferredWidth(18);
    
    if (JOptionPane.showConfirmDialog(null, new JScrollPane(table), 
           "Select the address to use as #>", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
        
      project.memory[row].relatedAddressBase=0;
      project.memory[row].relatedAddressDest=0;
      
       int rowS=table.getSelectedRow();
       if (rowS<0) {
         if (project.memory[row].type==TYPE_MAJOR || 
             project.memory[row].type==TYPE_MINOR || 
             project.memory[row].type==TYPE_PLUS_MAJOR || 
             project.memory[row].type==TYPE_PLUS_MINOR || 
             project.memory[row].type==TYPE_MINUS_MAJOR || 
             project.memory[row].type==TYPE_MINUS_MINOR) {
            if (JOptionPane.showConfirmDialog(this, "Did you want to delete the current address association?", "No selection were done, so:", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
              // + and <, or - ans < ?
              switch (project.memory[row].type) { 
                case TYPE_PLUS_MAJOR:
                  project.memory[row].type=TYPE_PLUS;
                  project.memory[row].related>>=8;
                  break;
                case TYPE_MINUS_MAJOR:
                  project.memory[row].type=TYPE_MINUS;
                  project.memory[row].related>>=8;
                  break;
                default:
                  project.memory[row].type=TYPE_EMPTY;
                  project.memory[row].related=-1;
                  break;
              }
            }
         } else JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
         return;
       } else {                  
           switch (project.memory[row].type) {
             case TYPE_PLUS:
               project.memory[row].type=TYPE_PLUS_MAJOR;
               project.memory[row].related=(project.memory[row].related<<16)+Integer.parseInt((String)table.getValueAt(rowS, 0),16);
               break;
             case TYPE_MINUS:
               project.memory[row].type=TYPE_MINUS_MAJOR;
               project.memory[row].related=(project.memory[row].related<<16)+Integer.parseInt((String)table.getValueAt(rowS, 0),16);
               break;
             default:
               project.memory[row].type=TYPE_MAJOR;
               project.memory[row].related=Integer.parseInt((String)table.getValueAt(rowS, 0),16);
               break;
        }
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
      ///jTableMemory.scrollRectToVisible(new Rectangle(jTableMemory.getCellRect(pos, 0, true)));
      Shared.scrollToCenter(jTableMemory, pos, 0);
    } catch (Exception e) {
      }
  }
  
  /**
   * Find a sequences of hex number from current position
   */
  private void findSeqHex() { 
    String addr=JOptionPane.showInputDialog(this, "Search for a sequences of hex number (it starts from the next selected position)", lastSearch);  
    if (addr==null) return;
    
    addr=addr.replaceAll("0x", "");
    addr=addr.replaceAll("0X", "");
    addr=addr.replaceAll("$", "");
    addr=addr.replaceAll(",", "");
    addr=addr.replaceAll(";", "");
    addr=addr.replaceAll(":", "");
    addr=addr.replaceAll(" ", "");

    if ("".equals(addr)) return;
    
    char[] data=addr.toCharArray();
    if ((data.length % 2)!=0) {        
      JOptionPane.showMessageDialog(this, "Invalid sequences of bytes. Bytes must be hex with 2 chars, like: 0x01 $02 03 ", "Warning", JOptionPane.WARNING_MESSAGE);
      return;
    }
    
    int pos=jTableMemory.getSelectedRow()+1;
    // be dure to restart if this is the case
    if (pos>0xFFFF-data.length/2) pos=0;
    
    // generate the list of bytes to search
    ArrayList<Byte> list=new ArrayList();
    int i=0;
    String val;
    try {
      while (i<data.length) {
        val=""+data[i]+data[i+1];
        i+=2;
        list.add((byte)((Integer.parseInt(val, 16)) &0xFF));
      }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Invalid sequences of bytes. Bytes must be hex with 2 chars, like: 0x01 $02 03 ", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
      }  
    
    lastSearch=addr;
            
    // search from current position
    boolean find;
    for (i=pos; i<0xFFFF-list.size(); i++) {
      find=true;  
      for (int j=0; j<list.size(); j++) {
        if (list.get(j) != project.memory[i+j].copy) {
          find=false; 
          break;
        }  
      }
      
      if (find) {
        Shared.scrollToCenter(jTableMemory, i, 0);  
        jTableMemory.getSelectionModel().setSelectionInterval(i, i+list.size()-1);
        return;
      }
    }
    
    // search from beginning to current
    for (i=0; i<pos; i++) {
      if (i+list.size()>0xFFFF) break;
      
      find=true;  
      for (int j=0; j<list.size(); j++) {
        if (list.get(j) != project.memory[i+j].copy) {
          find=false; 
          break;
        }  
      }
      
      if (find) {
        Shared.scrollToCenter(jTableMemory, i, 0);  
        jTableMemory.getSelectionModel().setSelectionInterval(i, i+list.size()-1);
        return;
      }
    }
    
    JOptionPane.showMessageDialog(this, "Sequences not fount");
  }

  /**
   * Mark the memory as address +
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
    for (int i=1; i<256; i++) {
      addr=mem.address-i;
      if (addr<0) continue;
      
      memory=project.memory[addr];
      
      data=new Vector();
          
      data.add(Shared.ShortToExe(memory.address));
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
        if (project.memory[row].type==TYPE_PLUS || 
            project.memory[row].type==TYPE_PLUS_MAJOR || 
            project.memory[row].type==TYPE_PLUS_MINOR) {
          if (JOptionPane.showConfirmDialog(this, "Did you want to delete the current address association?", "No selection were done, so:", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
              // + and < ?
              switch (project.memory[row].type) {
                  case TYPE_PLUS_MAJOR:
                      project.memory[row].type=TYPE_MAJOR;
                      project.memory[row].related&=0xFFFF;
                      break;
                  case TYPE_PLUS_MINOR:
                      project.memory[row].type=TYPE_MINOR;
                      project.memory[row].related&=0xFFFF;
                      break;
                  default:   
                      project.memory[row].type=TYPE_EMPTY;
                      project.memory[row].related=-1;
                      break;
              }
              
              // delete an automatic label if present, otherwise in code instruction it will be recreated if label is no more used
              project.memory[row].dasmLocation=null;
          }
        } else JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
        return;
      } else {         
          switch (project.memory[row].type) {
              case TYPE_MAJOR:
                  project.memory[row].type=TYPE_PLUS_MAJOR;
                  project.memory[row].related=project.memory[row].related+(Integer.parseInt((String)table.getValueAt(rowS, 0),16)<<16);
                  break;
              case TYPE_MINOR:
                  project.memory[row].type=TYPE_PLUS_MINOR;
                  project.memory[row].related=project.memory[row].related+(Integer.parseInt((String)table.getValueAt(rowS, 0),16)<<16);
                  break;
              default:
                  project.memory[row].type=TYPE_PLUS;
                  project.memory[row].related=Integer.parseInt((String)table.getValueAt(rowS, 0),16);
                  break;
          }
        }
       
      dataTableModelMemory.fireTableDataChanged();
      jTableMemory.setRowSelectionInterval(row, row);
    }
  }
  
  /**
   * Collaborative merge of another user contribution
   */
  private void projectMerge() {
    if (project == null) {
      JOptionPane.showMessageDialog(this, "You must be inside a project to merge it with another!", "Information", JOptionPane.WARNING_MESSAGE);
      return;
    } 
 
    int retVal=projectMergeFile.showOpenDialog(this);
    if (retVal == JFileChooser.APPROVE_OPTION) {
       File mergeFile=projectMergeFile.getSelectedFile();
       
       Project mergeProject=new Project();
       if (!FileManager.instance.readProjectFile(mergeFile , mergeProject)) {
         JOptionPane.showMessageDialog(this, "Error reading project file", "Error", JOptionPane.ERROR_MESSAGE);
         return;  
       } 
                   
       // test that the projects are of the same
       if ((Arrays.hashCode(mergeProject.inB) != Arrays.hashCode(project.inB))) {
         JOptionPane.showMessageDialog(this, "Byte data of the projects are different: they are not of the same source", "Error", JOptionPane.ERROR_MESSAGE);
         return;            
       }
       
       if (project.fileType==FileType.CRT && project.chip != mergeProject.chip) {
         JOptionPane.showMessageDialog(this, "You are disassembe different chip inside a CRT image", "Error", JOptionPane.ERROR_MESSAGE);
         return;    
       }       
       
       if (MPR.hashCode(mergeProject.mpr) != MPR.hashCode(project.mpr)) {
         JOptionPane.showMessageDialog(this, "Byte data of MPR in projects are different: they are not of the same source", "Error", JOptionPane.ERROR_MESSAGE);  
         return;  
       }
       
       // take name/description from secondary only if not present in primary
       if (project.name==null || "".equals(project.name)) project.name=mergeProject.name;       
       if (project.description==null || "".equals(project.description)) project.description=mergeProject.description;
       
       // copy constant
       for (int i=0; i<Constant.COLS; i++) {
         for (int j=0; i<Constant.ROWS; j++) {
           if (project.constant.table[i][j]==null || "".equals(project.constant.table[i][j])) project.constant.table[i][j]=mergeProject.constant.table[i][j];
         }  
       }      
       
       MemoryDasm memProject;
       MemoryDasm memMerge;
       
       // scan all memory locations
       for (int i=0; i<project.memory.length; i++) {
          memProject=project.memory[i];
          memMerge=mergeProject.memory[i];
          
          // apply secondary information if primary are not defined
          if (memProject.userBlockComment==null || "".equals(memProject.userBlockComment)) memProject.userBlockComment=memMerge.userBlockComment;
          if (memProject.userComment==null || "".equals(memProject.userComment)) memProject.userComment=memMerge.userComment;
          if (memProject.userLocation==null || "".equals(memProject.userLocation)) memProject.userLocation=memMerge.userLocation;
          
          if (memProject.isInside) {
            if (!memProject.isCode && !memProject.isData && !memProject.isGarbage) {
              memProject.isCode=memMerge.isCode;
              memProject.isData=memMerge.isData;
              memProject.isGarbage=memMerge.isGarbage;
            }  
            
            if (memProject.related==-1) {
              memProject.related=memMerge.related;
              memProject.type=memMerge.type;
            }
            
            if (memProject.dataType!=DataType.NONE) memProject.dataType=memMerge.dataType;
            
            if (memProject.index==-1) memProject.index=memMerge.index;
            if (memProject.relatedAddressBase==0) memProject.relatedAddressBase=memMerge.relatedAddressBase;
            if (memProject.relatedAddressDest==0) memProject.relatedAddressDest=memMerge.relatedAddressDest;
          }
       }
       
       // check relocate
       if (project.relocates==null) project.relocates=mergeProject.relocates;       
       
       // check patch
       if (project.patches==null) project.patches=mergeProject.patches;
       else if (mergeProject.patches!=null) {          
          // copy the value in the list
          Patch[] patches2=new Patch[project.patches.length+mergeProject.patches.length];
          System.arraycopy(project.patches, 0, patches2, 0, project.patches.length);
          for (int i=0; i<mergeProject.patches.length; i++) {
            patches2[project.patches.length+i]=mergeProject.patches[i];
          }
          project.patches=patches2;
       }            
                        
       dataTableModelMemory.fireTableDataChanged();
    }      
  }
  
  /**
   * Mark the memory as address -
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
    for (int i=1; i<256; i++) {
      addr=mem.address+i;
      if (addr>0xFFFF) continue;
      
      memory=project.memory[addr];
      
      data=new Vector();
          
      data.add(Shared.ShortToExe(memory.address));
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
        if (project.memory[row].type==TYPE_MINUS) {
          if (JOptionPane.showConfirmDialog(this, "Did you want to delete the current address association?", "No selection were done, so:", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            // - and <,> ?
            switch (project.memory[row].type) {
                  case TYPE_MINUS_MAJOR:
                      project.memory[row].type=TYPE_MAJOR;
                      project.memory[row].related&=0xFFFF;
                      break;
                  case TYPE_MINUS_MINOR:
                      project.memory[row].type=TYPE_MINOR;
                      project.memory[row].related&=0xFFFF;
                      break;
                  default:   
                      project.memory[row].type=TYPE_EMPTY;
                      project.memory[row].related=-1;
                      break;
            }
          }
        } else JOptionPane.showMessageDialog(this, "No row selected", "Warning", JOptionPane.WARNING_MESSAGE);  
        return;
      } else {         
           mem.related=Integer.parseInt((String)table.getValueAt(rowS, 0),16);          
           mem.type=TYPE_MINUS;
        }
       
      dataTableModelMemory.fireTableDataChanged();
      jTableMemory.setRowSelectionInterval(row, row);
    }
  }              
  
  /**
   * Search for a memory address (even as label) from the initial passed string
   * 
   * @param initial the initial buffer to search for
   * @return the address or -1 if not find
   */
  protected int searchAddress(String initial) {
    int addr=-1;
    
    try {
      // get the first word of the string
      String str=initial;
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
    } catch (Exception e)  {
        System.err.println(e);   
      }    
    
    return addr;
  }

  /**
   * Assemble back the source to binary
   */
  private void assembly() {
    File inputFile=new File(option.tmpPath+File.separator+"input.s");
    File outputFile=new File(option.tmpPath+File.separator+"output.prg");        
    
    if (disassembly.source==null || "".equals(disassembly.source)) {
       JOptionPane.showMessageDialog(this, "There is no source to assemble",
               "Warning", JOptionPane.WARNING_MESSAGE);
       return;
    }    
    
    if (option.tmpPath==null || "".equals(option.tmpPath)) {
       JOptionPane.showMessageDialog(this, "Select a temporary path for the assembler outputs", "Warning",
        JOptionPane.WARNING_MESSAGE);
       return;            
     }
    
    try {
       PrintWriter out=new PrintWriter(inputFile);
       out.write(disassembly.source);
       out.flush();
       out.close();
    } catch (Exception e) {
        System.err.println(e);
      }
      
    String res=compiler.compile(inputFile, outputFile);
    
    JTextArea textArea = new JTextArea(50, 50);
    textArea.setText(res);
    textArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(textArea);
    JOptionPane.showMessageDialog(this, scrollPane, "Result of "+option.assembler.getName()+" compilatation", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Assing the given index in table to memory
   * 
   * @param key the key table to assign
   */
  private void subAssign(int index) {
    MemoryDasm mem;   
      
    int rows[]=jTableMemory.getSelectedRows();
        
    for (int i=0; i<rows.length; i++) {
      mem= project.memory[rows[i]];
      mem.index=(byte)index;
    }
    
    dataTableModelMemory.fireTableDataChanged();  
    jTableMemory.clearSelection();
    for (int i=0; i<rows.length; i++) {
      jTableMemory.addRowSelectionInterval(rows[i], rows[i]);  
    } 
  }
  
  /**
   * Automatic hi/lo table assigment
   */
  private void memAutoLoHi() {
    if (project==null) return;
    jAutoLoHiDialog.setUp(project.memory, jTableMemory);
    jAutoLoHiDialog.setVisible(true);
  }
  
  /**
   * Export the source with the given 
   * 
   * @param name the assembler name
   */
  private void export(Name name) {
    if (project==null) return;
    
    Name actual=option.assembler;    
    option.assembler=name;
    Disassembly dis=new Disassembly();        
    dis.dissassembly(project.fileType, project.inB, option, project.memory,
                     project.constant, project.mpr, project.relocates, 
                     project.patches, project.chip, project.binAddress,
                     project.targetType, true);
     option.assembler=actual;
    
    exportAs(dis.source);     
  }
  
  /**
   * Run wizard for memory 
   */
  private void wizard() {
    if (project==null)  return;
    
    jWizardDialog.setUp(project.memory, disassembly, project, jTableMemory.getSelectedRow());
    jWizardDialog.setVisible(true);
  }
  
  /**
   * Refactor labels
   */
  private void refactor() {      
    if (project==null) {
      JOptionPane.showMessageDialog(this, "Open a project before using this function");
      return;
    }
    
    String oldPrefix=JOptionPane.showInputDialog(this, "Insert the prefix of labels to refactor:");
    if (oldPrefix==null || "".equals(oldPrefix)) {
      JOptionPane.showMessageDialog(this, "No prefix: abort operation");
      return;
    }
    
    String newPrefix=JOptionPane.showInputDialog(this, "Insert the prefix of labels to have:");           
    if (newPrefix==null || "".equals(newPrefix)) {
      JOptionPane.showMessageDialog(this, "No prefix: abort operation");
      return;
    }    
    
    String result;
    String error;
      
    for (MemoryDasm mem:project.memory) {
      if (mem.userLocation!=null && !"".equals(mem.userLocation) && mem.userLocation.startsWith(oldPrefix)) {
        result=mem.userLocation.replaceFirst(oldPrefix, newPrefix);
          
        error=errorLabel(result);
        if (error!=null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);   
            return;
        }
        
        mem.userLocation=result;
      }
    }
  }
  
  /**
   * Return if label gives error
   * 
   * @param label the label to check
   * @return null if ok or the error message
   */
  private String errorLabel(String label) {
      if (label.contains(" ")) return "Label must not contain spaces";      
      if (label.length()>option.maxLabelLength) return "Label too long. Max allowed="+option.maxLabelLength;            
      if (label.length()<2) return "Label too short. Min allowed=2";
            
      // see if the label is already defined
      for (MemoryDasm memory : project.memory) {
        if (label.equals(memory.dasmLocation) || label.equals(memory.userLocation)) {
          return "This label is already used into the source at: "+Shared.ShortToExe(memory.address);
       }
      }
      
      // see if label is as constant
      for (int i=0; i<Constant.COLS; i++) {
        for (int j=0; j<Constant.ROWS; j++) {
          if (label.equals(project.constant.table[i][j])) {
             return "This label is already used as constant ("+i+"/"+j+")";
           } 
         }
      }
      
      String tmp=label.toUpperCase();
      for (String val: M6510Dasm.mnemonics) {
        if (tmp.equals(val)) {
          return "This label is an opcode, cannot be defined";
        }
      }
      
      if (label.startsWith("W") && label.length()==5) {
         return "Label cannot be like Wxxxx as they are reserved";
      }
      
      return null;        
  }
  
  /**
   * Return true if label gives error without giving messages
   * 
   * @param label the label to check
   * @return true if error
   */
  private boolean silentErrorLabel(String label) {
      if (label.contains(" ")) return true;     
      if (label.length()>option.maxLabelLength) return true;      
      if (label.length()<2) return true ;
            
      // see if the label is already defined
      for (MemoryDasm memory : project.memory) {
        if (label.equals(memory.dasmLocation) || label.equals(memory.userLocation)) {       
          return true;
       }
      }
      
      // see if label is as constant
      for (int i=0; i<Constant.COLS; i++) {
        for (int j=0; j<Constant.ROWS; j++) {
          if (label.equals(project.constant.table[i][j])) {
            return true;  
           } 
         }
      }
      
      String tmp=label.toUpperCase();
      for (String val: M6510Dasm.mnemonics) {
        if (tmp.equals(val)) {
          return true;  
        }
      }
      
      if (label.startsWith("W") && label.length()==5) {
         return true;
      }
      
      return false;        
  }
  
  /**
   * Import lables from dasm file 
   */
  private void importLabels() {           
     File file;
     
     int retVal=importLabelsChooserFile.showOpenDialog(this);
     if (retVal != JFileChooser.APPROVE_OPTION) return;
          
     file=importLabelsChooserFile.getSelectedFile();
     
     try {
       BufferedReader br=new BufferedReader(new FileReader(file));
 
       String text;
       StringTokenizer st;
       String label;
       String pos;
       int address;
       MemoryDasm mem;
       
       int done=0;
       int processed=0;
       
       while ((text = br.readLine()) != null) {
         if (text.startsWith("---")) continue;
         processed++;
         
         st=new StringTokenizer(text);
         label=st.nextToken();
         pos=st.nextToken();                  
         
         address=Integer.parseInt(pos, 16);
         
         if (silentErrorLabel(label)) continue;
         
         mem=project.memory[address];
         // avoid to create a label like the automatic one
         if (mem.dasmLocation!=null && mem.dasmLocation.equals(mem.userLocation)) continue;
         
         mem.userLocation=label;
         done++;
       }
         
           
       br.close();
       
       JOptionPane.showMessageDialog(this, "Created "+done+" lables, out of "+processed+" entries");
       
     } catch (Exception e) {
         System.err.println(e);
         JOptionPane.showMessageDialog(this, "Error reading file", "Error", JOptionPane.ERROR_MESSAGE);
       }
         
  }
  
  /**
   * Popolate recent file menu
   */
  private void recentFile() {
    String path;  
      
    recentFile.reload();       
    
    for (int i=0; i<recentFile.size(); i++) {
      path=new File(recentFile.get(i)).getName();            
      
      switch (i) {
        case 0:
          jMenuItemRecent1.setText(path);
          jMenuItemRecent1.setEnabled(true);
          break;
        case 1:
          jMenuItemRecent2.setText(path);
          jMenuItemRecent2.setEnabled(true);
          break;  
        case 2:
          jMenuItemRecent3.setText(path);
          jMenuItemRecent3.setEnabled(true);
          break;  
        case 3:
          jMenuItemRecent4.setText(path);
          jMenuItemRecent4.setEnabled(true);
          break;    
        case 4:
          jMenuItemRecent5.setText(path);
          jMenuItemRecent5.setEnabled(true);
          break;  
        case 5:
          jMenuItemRecent6.setText(path);
          jMenuItemRecent6.setEnabled(true);
          break;  
        case 6:
          jMenuItemRecent7.setText(path);
          jMenuItemRecent7.setEnabled(true);
          break;  
        case 7:
          jMenuItemRecent8.setText(path);
          jMenuItemRecent8.setEnabled(true);
          break;    
        case 8:
          jMenuItemRecent9.setText(path);
          jMenuItemRecent9.setEnabled(true);
          break;              
      }
    }
  }
  
  /**
   * Load a recent file 
   * 
   * @param pos the position i nrecent
   */
  private void recent(int pos) {
    if (project != null && !project.equals(savedProject)) {
      int input = JOptionPane.showConfirmDialog(this, "Project not saved. Save it? (No=not save it)", "Information", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE); 
      if (input==JOptionPane.CANCEL_OPTION) return;
      else if (input==JOptionPane.OK_OPTION) projectSave();     
    } 
    
    projectFile=new File(recentFile.get(pos));
    project=new Project();
    setTitle("JC64dis ("+projectFile.getName()+")");
    if (!FileManager.instance.readProjectFile(projectFile , project)) {
        JOptionPane.showMessageDialog(this, "Error reading project file", "Error", JOptionPane.ERROR_MESSAGE);
    } else {
        if (option.pedantic) JOptionPane.showMessageDialog(this, "File read", "Information", JOptionPane.INFORMATION_MESSAGE);
        execute(SOURCE_DISASS);
      }
    savedProject=project.clone();
    dataTableModelMemory.setData(project.memory);
    dataTableModelMemory.fireTableDataChanged();
                  
  }
  
  /**
   * Clear the automatic label
   */
  private void clearLab() {
    for (MemoryDasm mem: project.memory) {
      mem.dasmLocation=null;  
    }  
    dataTableModelMemory.fireTableDataChanged();
  }
  
  /**
   * Clear the automatic comment
   */
  private void clearCom() {
    for (MemoryDasm mem: project.memory) {
      mem.dasmComment=null;  
      if (mem.isData && "".equals(mem.userComment)) mem.userComment=null; 
    }  
    dataTableModelMemory.fireTableDataChanged();
  }  
  
  /**
   * Got memory from preview
   * 
   * @param rSyntaxTextArea syntax text area 
   * @param caret cater used for disassembly
   * @param modifier the key modifier of mouse click
   */
  private void gotoMem(org.fife.ui.rsyntaxtextarea.RSyntaxTextArea rSyntaxTextArea, 
                       Carets caret, int modifier) {
    try {  
      int pos;
      int addr=-1;  
        
      // try with carets
      MemoryDasm mem=caret.getMemory(rSyntaxTextArea.getCaretPosition());
          
      if (mem==null) {
        // get starting position of clicked point  
        pos=Utilities.getRowStart(rSyntaxTextArea, rSyntaxTextArea.getCaretPosition());
        addr=searchAddress(rSyntaxTextArea.getDocument().getText(pos,option.maxLabelLength));
      } else addr=mem.address;
        
      if (addr==-1) return;
                
      //scroll to that point
      ///jTableMemory.scrollRectToVisible(jTableMemory.getCellRect(addr,0, true)); 
      Shared.scrollToCenter(jTableMemory, addr, 0);
      
      if ((modifier & InputEvent.SHIFT_DOWN_MASK)==InputEvent.SHIFT_DOWN_MASK) {
        int row=jTableMemory.getSelectedRow();
        if (row==-1) jTableMemory.setRowSelectionInterval(addr, addr);
        else {
          if (row<addr) {
            mem=project.memory[addr];
            if (mem.isCode) jTableMemory.setRowSelectionInterval(row, addr+M6510Dasm.tableSize[mem.copy & 0xff]-1); // go to end of instruction
            else jTableMemory.setRowSelectionInterval(row, addr);
          } 
          else jTableMemory.setRowSelectionInterval(addr, row); 
        }
      } else jTableMemory.setRowSelectionInterval(addr, addr);       // select this row          
    } catch (Exception e) {
        System.err.println(e);
      }  
  }
  
  /**
   * Goto mem form source 
   * 
   * @param evt the mouse event
   */
  private void gotoMemSource(java.awt.event.MouseEvent evt) {
    try {
        int pos;
        int addr=-1;
        
        // try with carets
        MemoryDasm mem=disassembly.caretsSource.getMemory(rSyntaxTextAreaSource.getCaretPosition());
        
        if (mem==null) {
          // get starting position of clicked point  
          pos=Utilities.getRowStart(rSyntaxTextAreaSource, rSyntaxTextAreaSource.getCaretPosition());
        
          addr=searchAddress(rSyntaxTextAreaSource.getDocument().getText(pos,option.maxLabelLength));
        } else addr=mem.address;                
        
        if (addr==-1) return;
                
        //scroll to that point
        ///jTableMemory.scrollRectToVisible(jTableMemory.getCellRect(addr,0, true)); 
        Shared.scrollToCenter(jTableMemory, addr, 0);
        
        // select this row
        jTableMemory.setRowSelectionInterval(addr, addr);     

        // ctrl + click must move source area too the same position
        if (evt.isControlDown()) {
          int actual;  
        
          // get the address in hex format
          addr=jTableMemory.getSelectedRow();
          pos=0;        

          // scan all lines for the memory location
          try {
            String preview=rSyntaxTextAreaDis.getText();
            String lines[] = preview.split("\\r?\\n");
            for (String line: lines) {
              actual=searchAddress(line.substring(0, Math.min(line.length(), option.maxLabelLength)));   
              if (actual==addr) {      
                // set preview in the find position  
                rSyntaxTextAreaDis.setCaretPosition(pos);
                rSyntaxTextAreaDis.requestFocusInWindow();
                break;
              } else {
                  pos+=line.length()+1;
                }
            }
          } catch (Exception e) {
              System.err.println();  
            } 
        }
    } catch (Exception e) {
          System.err.println(e);
      }    
  }
  
  /**
   * Copy action
   */
  private void appCopy() {
    // prepare the data to copy in clipboard  
    Serial serial=new Serial();      
    serial.uuid=Shared.uuid;
    serial.selected=jTableMemory.getSelectedRows();
    serial.memory=project.memory;
    
    if (serial.selected==null) {
      JOptionPane.showMessageDialog(this, "No memory addresses to copy are selected. Abort copy.");
      return;   
    }
    
    String result="";
      try {
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          ObjectOutputStream os = new ObjectOutputStream(bos);
          os.writeObject(serial);
          result= Base64.getEncoder().encodeToString(bos.toByteArray());
          os.close();      
      } catch (IOException ex) {
          JOptionPane.showMessageDialog(this, "Error in copy objects in memory!");
          return;
      }
      
    StringSelection st=new StringSelection(result);      
    Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();      
    cb.setContents(st, st);
  }
  
  /**
   * Paste action
   */
  private void appPaste() {
    Serial serial=null;  
    String result="";
    
    Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
      
    try {
      Transferable t = cb.getContents(null);
      if (t.isDataFlavorSupported(DataFlavor.stringFlavor))
        result=(String)t.getTransferData(DataFlavor.stringFlavor);  
   
      ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(result));
      ObjectInputStream oInputStream = new ObjectInputStream(bis);
      serial=(Serial)oInputStream.readObject();                   
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error getting JC64dis objects from clipboard!");
        return;
      }
    
    if (serial==null) {
      JOptionPane.showMessageDialog(this, "Nothing to do");
      return;  
    }
    
    if (serial.selected==null) {
      JOptionPane.showMessageDialog(this, "There was no rows copied to paste.");
      return;    
    }
    
    int row=jTableMemory.getSelectedRow();
      
    if (serial.uuid.equals(Shared.uuid)) {
      JOptionPane.showMessageDialog(this, "Can not paste from same instance.");
      return;    
    }
    
    if (row==-1) {
      JOptionPane.showMessageDialog(this, "Select the row where to copy.");
      return;      
    }
    
    MemoryDasm memC;
    MemoryDasm memP;
    
    try {
      int pos=serial.selected[0];
    
      // scan all the selected memory addresses
      for (int val: serial.selected) {
        memC=serial.memory[val];  // copy from
        memP=project.memory[row+val-pos]; // paste to
      
        memP.userComment=memC.userComment;
        memP.userLocation=memC.userLocation;
        memP.userBlockComment=memC.userBlockComment;
        memP.type=memC.type;
        if (memC.related>=0) {
          if (memC.related>0xFFFF) {
              memP.related=(memP.address-memC.address+(memC.related>>16))<<16;
          } else {
              memP.related=memP.address-memC.address+memC.related;
            }
        } else memP.related=memC.related;
        memP.isCode=memC.isCode;
        memP.isData=memC.isData;
        memP.isGarbage=memC.isGarbage;
        memP.index=memC.index;
        memP.dataType=memC.dataType;
        memP.relatedAddressBase=memC.relatedAddressBase;
        memP.relatedAddressDest=memC.relatedAddressDest;
      }
    } catch (Exception e) {
        System.err.println(e);
      }      
   }
  
  /**
   * Popolate menus
   */
  private void undo() {
    KeyProject obj;  
      
    Iterator<KeyProject> iter=undo.getAll();
    int i=0;
    while (iter.hasNext()) {
      obj=iter.next();
      
      switch (i) {
        case 0:
          jMenuItemUndo1.setText(obj.key);
          jMenuItemUndo1.setEnabled(true);
          break;
        case 1:
          jMenuItemUndo2.setText(obj.key);
          jMenuItemUndo2.setEnabled(true);
          break;  
        case 2:
          jMenuItemUndo3.setText(obj.key);
          jMenuItemUndo3.setEnabled(true);
          break;  
        case 3:
          jMenuItemUndo4.setText(obj.key);
          jMenuItemUndo4.setEnabled(true);
          break;    
        case 4:
          jMenuItemUndo5.setText(obj.key);
          jMenuItemUndo5.setEnabled(true);
          break;  
        case 5:
          jMenuItemUndo6.setText(obj.key);
          jMenuItemUndo6.setEnabled(true);
          break;  
        case 6:
          jMenuItemUndo7.setText(obj.key);
          jMenuItemUndo7.setEnabled(true);
          break;  
        case 7:
          jMenuItemUndo8.setText(obj.key);
          jMenuItemUndo8.setEnabled(true);
          break;    
        case 8:
          jMenuItemUndo9.setText(obj.key);
          jMenuItemUndo9.setEnabled(true);
          break;   
      }   
      i++;
    }
  }
  
  /**
   * Undo project at given index 
   * 
   * @param index the index
   */
  private void undo(int index) {
    String key=null;
    
    switch (index) {
      case 0:
        key=jMenuItemUndo1.getText();
        break;
      case 1:
        key=jMenuItemUndo2.getText();
        break;   
      case 2:
        key=jMenuItemUndo3.getText();
        break;     
      case 3:
        key=jMenuItemUndo4.getText();
        break;     
      case 4:
        key=jMenuItemUndo5.getText();
        break;     
      case 5:
        key=jMenuItemUndo6.getText();
        break;     
      case 6:
        key=jMenuItemUndo7.getText();
        break;     
      case 7:
        key=jMenuItemUndo8.getText();
        break;     
      case 8:
        key=jMenuItemUndo9.getText();
        break;     
    }  
      
    Project search=undo.retrieve(key);
    
    if (search!=null) {
      project=search;  
      dataTableModelMemory.setData(project.memory);
      dataTableModelMemory.fireTableDataChanged();
      if (option.forceCompilation) disassembly(false);   
    }
  }
  
  /**
   * Get the hex size of the are around the given code memory location
   * 
   * @param address the address to check
   * @return the hex number string
   */
  private String getAreaCodeSize(int address) {
    MemoryDasm mem=project.memory[address];
    
    int size=1;
    
    int i=address-1;
    while (i>=0) {
      if (project.memory[i].isCode) size++;
      else break;
      i--;
    }
    
    i=address+1;
    while (i<=0xFFFF) {
      if (project.memory[i].isCode) size++;
      else break;  
      i++;
    }
    
    return Shared.ShortToExe(size);
  }  
  
  /**
   * Get the hex size of the are around the given date memory location
   * 
   * @param address the address to check
   * @return the hex number string
   */
  private String getAreaDataSize(int address) {
    MemoryDasm mem=project.memory[address];
    
    int size=1;
    
    int i=address-1;
    while (i>=0) {
      if (project.memory[i].isData) size++;
      else break;
      i--;
    }
    
    i=address+1;
    while (i<=0xFFFF) {
      if (project.memory[i].isData) size++;
      else break;   
      i++;
    }
    
    return Shared.ShortToExe(size);
  }   
  
    /**
   * Get the hex size of the are around the given garbage memory location
   * 
   * @param address the address to check
   * @return the hex number string
   */
  private String getAreaGarbageSize(int address) {
    MemoryDasm mem=project.memory[address];
    
    int size=1;
    
    int i=address-1;
    while (i>=0) {
      if (project.memory[i].isGarbage) size++;
      else break;
      i--;
    }
    
    i=address+1;
    while (i<=0xFFFF) {
      if (project.memory[i].isGarbage) size++;
      else break;      
      i++;
    }
    
    return Shared.ShortToExe(size);
  }  
  
  /**
   * Go to home of the block kind where is the memory location
   */
  private void altHome() {
    int row=jTableMemory.getSelectedRow();
    if (row==-1) return;
    
    
    int i=row-1;
    
    MemoryDasm mem2;
    MemoryDasm mem=project.memory[row];
    
    if ((mem.isCode || (!mem.isData && option.useAsCode)) && mem.isInside && !mem.isGarbage) {
       
      while (i>=0) {
        mem2=project.memory[i];
        
        if ((mem2.isCode || (!mem2.isData && option.useAsCode)) && mem2.isInside && !mem2.isGarbage) i--;
        else break;      
      }  
      i++;
      
      //scroll to that point
      ///jTableMemory.scrollRectToVisible(jTableMemory.getCellRect(i, 0, true)); 
      Shared.scrollToCenter(jTableMemory, i, 0);      
      //jTableMemory.setRowSelectionInterval(i, i);
    } else 
    if ((mem.isData || (!mem.isCode && !option.useAsCode)) && mem.isInside && !mem.isGarbage)  {
        
      while (i>=0) {
        mem2=project.memory[i];
        
        if ((mem2.isData || (!mem2.isCode && !option.useAsCode)) && mem2.isInside && !mem2.isGarbage)  i--;
        else break;      
      }  
      i++;
      
      //scroll to that point
      ///jTableMemory.scrollRectToVisible(jTableMemory.getCellRect(i, 0, true)); 
      Shared.scrollToCenter(jTableMemory, i, 0);
      //jTableMemory.setRowSelectionInterval(i, i);  
        
    }
    else 
    if (mem.isGarbage) {
      while (i>=0) {
        mem2=project.memory[i];
        
        if (mem2.isGarbage)  i--;
        else break;      
      }  
      i++;
      
      //scroll to that point
      ///jTableMemory.scrollRectToVisible(jTableMemory.getCellRect(i, 0, true)); 
      Shared.scrollToCenter(jTableMemory, i, 0);
      //jTableMemory.setRowSelectionInterval(i, i);    
    } 
  }
  
  /**
   * Go to end of the block kind where is the memory location
   */
  private void altEnd() {
    int row=jTableMemory.getSelectedRow();
    if (row==-1) return;
    
    
    int i=row+1;
    
    MemoryDasm mem2;
    MemoryDasm mem=project.memory[row];
    
    if ((mem.isCode || (!mem.isData && option.useAsCode)) && mem.isInside && !mem.isGarbage) {
       
      while (i<=0xFFFF) {
        mem2=project.memory[i];
        
        if ((mem2.isCode || (!mem2.isData && option.useAsCode)) && mem2.isInside && !mem2.isGarbage) i++;
        else break;      
      }  
      i--;
      
      //scroll to that point
      ///jTableMemory.scrollRectToVisible(jTableMemory.getCellRect(i, 0, true)); 
      Shared.scrollToCenter(jTableMemory, i, 0);
      //jTableMemory.setRowSelectionInterval(i, i);
    } else 
    if ((mem.isData || (!mem.isCode && !option.useAsCode)) && mem.isInside && !mem.isGarbage)  {
        
      while (i<=0xFFFF) {
        mem2=project.memory[i];
        
        if ((mem2.isData || (!mem2.isCode && !option.useAsCode)) && mem2.isInside && !mem2.isGarbage)  i++;
        else break;      
      }  
      i--;
      
      //scroll to that point
      ///jTableMemory.scrollRectToVisible(jTableMemory.getCellRect(i, 0, true)); 
      Shared.scrollToCenter(jTableMemory, i, 0);
      //jTableMemory.setRowSelectionInterval(i, i);  
        
    }
    else 
    if (mem.isGarbage) {
      while (i<=0XFFFF) {
        mem2=project.memory[i];
        
        if (mem2.isGarbage)  i++;
        else break;      
      }  
      i--;
      
      //scroll to that point
      ///jTableMemory.scrollRectToVisible(jTableMemory.getCellRect(i, 0, true)); 
      Shared.scrollToCenter(jTableMemory, i, 0);
      //jTableMemory.setRowSelectionInterval(i, i);    
    }    
  }    

  /**
   * Manage the given type action in current memory cell selected
   * 
   * @param type the action type
   */
    private void manageAction(Carets.Type type) {
      if (type==null) return;
      
      switch (type) {
        case COMMENT:
          execute(MEM_ADDCOMM);
          break;
        case BLOCK_COMMENT:
          execute(MEM_ADDBLOCK);  
          break; 
        case LABEL:
          execute(MEM_ADDLABEL);   
          break;
        case LABEL_REL:
          execute(MEM_ADDLABELOP);
          break;  
      }
    }
    
    /**
     * Show hex dialog
     */
    private void showHex() {
      int row=jTableMemory.getSelectedRow();
      if (row==-1) return;  
      
      int end;
      
      for (end=row+1; end<=0xFFFF; end++) {
        if (!project.memory[end].isInside) break;  
      }      
      
      jHexDialog.setUp(project.memory, row, end-1);
      jHexDialog.setVisible(true);
    }

    /**
     * Create sequential labeled blocks
     */
    private void createBlockLabels() {
      int row=jTableMemory.getSelectedRow();
      if (row==-1) return;
      
      int[] rows=jTableMemory.getSelectedRows();
      
      jBlockDialog.setUp(project.memory, rows[0], rows[rows.length-1]);
      jBlockDialog.setVisible(true);
    }
}
