/**
 * @(#)DataTableModelMemory.java 2019/12/21
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
package sw_emulator.swing.table;

import javax.swing.table.AbstractTableModel;
import sw_emulator.software.MemoryDasm;
import sw_emulator.swing.Shared;
import sw_emulator.swing.main.Option;

/**
 * DataTableModel for memory dasm
 * 
 * @author ice
 */
public class DataTableModelMemory extends AbstractTableModel {    
  // mode for show memory address  
  public static final byte MOD_HEX=1;
  public static final byte MOD_CHAR=2;  
  
  Option option;
    
  /** Table data */  
  MemoryDasm[] data;  

  public DataTableModelMemory(Option option) {
    this.option=option;
  }
  
  public enum COLUMNS {
    ID("Memory location", Integer.class),
    DC("Dasm comment", Boolean.class),
    UC("User comment", Boolean.class),
    DL("Dasm location", Boolean.class),
    UL("User location", Boolean.class),
    UB("User block comment", Boolean.class),
    RE("Related address", String.class),
    VL("Value in memory", Integer.class);
      
    String columnsTip;
    Class type;
    
    COLUMNS(String tip, Class type) {
      columnsTip=tip;  
      this.type=type;
    }
    
    };  
  
  public static COLUMNS[] columns=COLUMNS.values();   
  
  /**
   * Set the memory data to use
   * 
   * @param data the memory data
   */
  public void setData(MemoryDasm[] data) {
    this.data=data;  
  }
  
  /**
   * Get the actual data in table 
   * 
   * @return  the data
   */
  public MemoryDasm[] getData() {
    return data;  
  }
    
  /**
   * Get the number of columns
   * 
   * @return the number of columns
   */
  @Override
  public int getColumnCount() {
    return columns.length;
  }
  
  /**
   * Get the number of rows
   * 
   * @return the number of rows
   */
  @Override
  public int getRowCount() {
    if (data!=null) return data.length;
    else return 0;
  }

  @Override
  public Class<?> getColumnClass(int i) {
    return columns[i].type;
  } 
  
  /**
   * Get the value at the given position
   * 
   * @param rowIndex the row index
   * @param columnIndex the column index
   * @return the value in cell
   */
  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    MemoryDasm memory=data[rowIndex];
      
    switch (columns[columnIndex]) {
        case ID:
          return Shared.ShortToExe(memory.address);   
        case VL:
          if (option.memoryValue==MOD_HEX) return Shared.ByteToExe(memory.copy & 0xFF); 
          else return (char)memory.copy;  
        case DC:
          return memory.dasmComment!=null;            
        case UC:
          return memory.userComment!=null;  
        case DL:
          return memory.dasmLocation!=null;            
        case UL:
          return memory.userLocation!=null;      
        case UB:
          return memory.userBlockComment!=null;     
        case RE:
          String val="";  
          if (memory.index!=-1) {
            switch (memory.index) {
              case 10:  
                val="!";  
                break;
              case 11:  
                val="\"";  
                break; 
              case 12:  
                val="£";  
                break;
              case 13:  
                val="$";  
                break;  
              case 14:  
                val="%";  
                break;
              case 15:  
                val="&";  
                break;
              case 16:  
                val="/";  
                break;
              case 17:  
                val="(";  
                break;
              case 18:  
                val=")";  
                break;
              case 19:  
                val="=";  
                break;  
              default:  
                val=""+memory.index;
                break;
            }  
              
          } 
            
          if (memory.type!=' ') return ""+memory.dataType.getChar()+memory.type+val;
          else return ""+memory.dataType.getChar()+val;
                 
    }  
    return "";
  }

  /**
   * Get the name of the column
   * 
   * @param column the column index
   * @return the column name
   * @Override
   */    
  @Override
  public String getColumnName(int column) {
      return columns[column].name();
  }    
}
