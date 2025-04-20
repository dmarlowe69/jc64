/**
 * @(#)CpuDasm.java 2022/02/04
 *
 * ICE Team Free Software Group
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
package sw_emulator.software.cpu;

import java.util.Locale;
import sw_emulator.software.Assembler;
import sw_emulator.software.MemoryDasm;
import static sw_emulator.software.MemoryDasm.TYPE_MAJOR;
import static sw_emulator.software.MemoryDasm.TYPE_MINOR;
import static sw_emulator.software.MemoryDasm.TYPE_MINUS;
import static sw_emulator.software.MemoryDasm.TYPE_MINUS_MAJOR;
import static sw_emulator.software.MemoryDasm.TYPE_MINUS_MINOR;
import static sw_emulator.software.MemoryDasm.TYPE_PLUS;
import static sw_emulator.software.MemoryDasm.TYPE_PLUS_MAJOR;
import static sw_emulator.software.MemoryDasm.TYPE_PLUS_MINOR;
import static sw_emulator.software.cpu.M6510Dasm.A_NUL;
import static sw_emulator.software.cpu.M6510Dasm.M_JAM;
import sw_emulator.swing.main.Constant;
import sw_emulator.swing.main.Option;

/**
 * Generic base Cpu disassembler
 * 
 * @author ice
 */
public class CpuDasm implements disassembler {
/** Type of instruction (used to create comment) */
  protected int iType=M_JAM;

  /** Type of addressing used by instruction (used to create comment) */
  protected int aType=A_NUL;

  /** Value of address (used to create comment) */
  protected long addr=0;

  /** Value of operation (used to create comment) */
  protected long value=0;

  /** Last position pointer in buffer */
  protected int pos=0;

  /** Last program counter value */
  protected long pc=0;
  
  /** Memory dasm to use */
  MemoryDasm[] memory;
  
  /** Assembler manager */
  protected Assembler assembler=new Assembler();
  
  /** Option to use */
  protected Option option;    
  
  /** Constant to use */
  protected Constant constant;  
  
  /** Actual case to use for text */
  public boolean upperCase=true;
  
  /** Default mode for Hex ($)*/
  protected boolean defaultMode=true;
  
  /** String builder global to reduce GC call */
  final StringBuilder result=new StringBuilder ("");     
  
  /**
   * Set the memory dasm to use
   * 
   * @param memory the memory dasm
   */
  public void setMemory(MemoryDasm[] memory) {
    this.memory=memory;  
  }
  
  /**
   * Set the constant to use
   * 
   * @param constant the constants
   */
  public void setConstant(Constant constant) {
    this.constant=constant;  
  }
  
  /**
   * Set the option to use
   * 
   * @param option the option to use
   * @param assembler the assembler to use
   */
  public void setOption(Option option, Assembler assembler) {
    this.option=option;
    this.assembler=assembler;        
  }    
  
  /**
   * Convert a unsigned byte (containing in a int) to Exe upper case 2 chars
   *
   * @param value the byte value to convert
   * @return the exe string rapresentation of byte
   */
  protected String ByteToExe(int value) {
    int tmp=value;
    
    if (value<0) return "??";
    
    String ret=Integer.toHexString(tmp);
    if (ret.length()==1) ret="0"+ret;
    return ret.toUpperCase(Locale.ENGLISH);
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
  
  /**
   * Return the hex number string with appropriate prefix/suffix ($ of h)
   * 
   * @param value the string value of the hex number
   * @param defaultMode the mode $ as default
   * @return the hex string with prefix
   */
  protected static String HexNum(String value, boolean defaultMode) {
    if (defaultMode) return "$"+value;
    else if (!Character.isDigit(value.charAt(0))) return "0"+value+"h";
         return value+"h";
  } 
  
  /**
   * Get notmalized type (<,>)
   * 
   * @param type 
   * @return the normalized type
   */
  private char getNormType(char type) {
    switch (type) {
       case TYPE_PLUS_MINOR:
       case TYPE_MINUS_MINOR:
       case TYPE_MINOR:  
         return TYPE_MINOR;
       case TYPE_PLUS_MAJOR:
       case TYPE_MINUS_MAJOR:  
       case TYPE_MAJOR:  
         return TYPE_MAJOR;
       default:  
         return type;
    }
  }
  
  /**
   * Get the major/minor representation of the isntruction 
   * 
   * @param type the type of major/minor
   * @param value the address value
   * @return the formatted string
   */
  private String getMajorMinor(char type, String value) {
    type=getNormType(type);  // get only < or >
    
    if (option.isMajorMinorSyntaxSupported()) return type+value;
    else if (type=='<') {
      if (defaultMode) return "("+value+" & $FF)";
      else return "("+value+" & 0FFh)";
    } else return  "("+value+" >> 8)";
  }
  
  /**
   * Get the label of immediate value
   * 
   * @param addr the address of the value
   * @param value in that location
   * @return the label of location
   */
  protected String getLabelImm(long addr, long value) {
    if (addr<0 || addr>0xffff) return HexNum("??", defaultMode); 
    
    char type=memory[(int)addr].type;
    
    // this is a data declaration            
    if (type==TYPE_MINOR || 
        type==TYPE_MAJOR || 
        type==TYPE_PLUS_MAJOR || 
        type==TYPE_PLUS_MINOR || 
        type==TYPE_MINUS_MAJOR || 
        type==TYPE_MINUS_MINOR) {    
      MemoryDasm memRel;
      // the byte is a reference
      if (type==TYPE_PLUS_MAJOR || type==TYPE_PLUS_MINOR || type==TYPE_MINUS_MAJOR || type==TYPE_MINUS_MINOR) memRel=memory[memory[(int)addr].related & 0xFFFF];   
      else memRel=memory[memory[(int)addr].related & 0xFFFF];   
              
      if (memRel.userLocation!=null && !"".equals(memRel.userLocation)) return getMajorMinor(type,memRel.userLocation);
      else if (memRel.dasmLocation!=null && !"".equals(memRel.dasmLocation)) return getMajorMinor(type,memRel.dasmLocation);
           else {   
              switch (memRel.type) {
                case TYPE_PLUS:
                  /// this is a memory in table label
                  int pos=memRel.address-memRel.related;
                  MemoryDasm mem2=memory[memRel.related];
                  if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) return getMajorMinor(type, mem2.userLocation+"+"+pos);
                  if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) return getMajorMinor(type, mem2.dasmLocation+"+"+pos);
                  return getNormType(type)+HexNum(ByteToExe((int)memRel.related), defaultMode)+"+"+pos;  
                  
                case TYPE_MINUS:
                  /// this is a memory in table label
                  pos=memRel.address-memRel.related;
                  mem2=memory[memRel.related];
                  if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) return getMajorMinor(type, mem2.userLocation+pos);
                  if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) return getMajorMinor(type, mem2.dasmLocation+pos);
                  return getMajorMinor(type, HexNum(ByteToExe((int)memRel.related), defaultMode)+pos);    
                default: return getMajorMinor(memory[(int)addr].type, HexNum(ShortToExe(memRel.address), defaultMode));
              }
           }
    } else {        
        if (memory[(int)addr].index!=-1) {
          String res=constant.table[memory[(int)addr].index][(int)value];  
          if (res!=null && !"".equals(res)) return res;
        }            
        return HexNum(ByteToExe((int)value), defaultMode);
      }
  }
  
  
  /**
   * Get the label or memory location ($) of zero page
   * 
   * @param addr the address of the label
   * @return the label or memory location ($)
   */
  protected String getLabelZero(long addr) {
    if (addr<0 || addr>0xffff) return HexNum("??", defaultMode);
      
    MemoryDasm mem=memory[(int)addr];          
    
    if (mem.type==TYPE_PLUS) {
      /// this is a memory in table label
      int pos=mem.address-mem.related;
      MemoryDasm mem2=memory[mem.related];
      if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) return mem2.userLocation+"+"+pos;
      if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) return mem2.dasmLocation+"+"+pos;
      return HexNum(ByteToExe((int)mem.related), defaultMode)+"+"+pos;  
    }
    
    if (mem.type==TYPE_PLUS_MAJOR || mem.type==TYPE_PLUS_MINOR) {
      /// this is a memory in table label
      int rel=mem.related>>16;
      int pos=mem.address-rel;
      MemoryDasm mem2=memory[rel];
      if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) return mem2.userLocation+"+"+pos;
      if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) return mem2.dasmLocation+"+"+pos;
      return HexNum(ByteToExe(rel), defaultMode)+"+"+pos;  
    }    
    
    if (mem.type==TYPE_MINUS_MAJOR || mem.type==TYPE_MINUS_MINOR) {
      /// this is a memory in table label
      int rel=mem.related>>16;
      int pos=mem.address-rel;
      MemoryDasm mem2=memory[rel];
      if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) return mem2.userLocation+pos;
      if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) return mem2.dasmLocation+pos;
      return HexNum(ByteToExe(rel), defaultMode)+pos;  
    }      
    
    if (mem.type==TYPE_MINUS) {
      /// this is a memory in table label
      int pos=mem.address-mem.related;
      MemoryDasm mem2=memory[mem.related];
      if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) return mem2.userLocation+pos;
      if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) return mem2.dasmLocation+pos;
      return HexNum(ByteToExe((int)mem.related), defaultMode)+pos;  
    }     
     
    if (mem.userLocation!=null && !"".equals(mem.userLocation)) return mem.userLocation;
    if (mem.dasmLocation!=null && !"".equals(mem.dasmLocation)) return mem.dasmLocation;
    return HexNum(ByteToExe((int)addr), defaultMode);        
  } 

  /**
   * Get the label or memory location ($)
   * 
   * @param addr the address of the label
   * @return the label or memory location ($)
   */
  protected String getLabel(long addr) {
    if (addr<0 || addr>0xffff) return HexNum("????", defaultMode);  
      
    MemoryDasm mem=memory[(int)addr];

    try {    
        if (mem.type==TYPE_PLUS) {
          /// this is a memory in table label
          int pos=mem.address-mem.related;
          MemoryDasm mem2=memory[mem.related];
          if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) return mem2.userLocation+"+"+pos;
          if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) return mem2.dasmLocation+"+"+pos;
          return HexNum(ShortToExe((int)mem.related), defaultMode)+"+"+pos;  
        }

        if (mem.type==TYPE_PLUS_MAJOR || mem.type==TYPE_PLUS_MINOR) {
          /// this is a memory in table label
          int rel=(mem.related>>16)&0xFFFF;
          int pos=mem.address-rel;
          MemoryDasm mem2=memory[rel];
          if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) return mem2.userLocation+"+"+pos;
          if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) return mem2.dasmLocation+"+"+pos;
          return HexNum(ShortToExe(rel), defaultMode)+"+"+pos;  
        }    
        
        if (mem.type==TYPE_MINUS_MAJOR || mem.type==TYPE_MINUS_MINOR) {
          /// this is a memory in table label
          int rel=(mem.related>>16)&0xFFFF;
          int pos=mem.address-rel;
          MemoryDasm mem2=memory[rel];
          if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) return mem2.userLocation+pos;
          if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) return mem2.dasmLocation+pos;
          return HexNum(ShortToExe(rel), defaultMode)+pos;  
        }          

        if (mem.type==TYPE_MINUS) {
          /// this is a memory in table label
          int pos=mem.address-mem.related;
          MemoryDasm mem2=memory[mem.related];
          if (mem2.userLocation!=null && !"".equals(mem2.userLocation)) return mem2.userLocation+pos;
          if (mem2.dasmLocation!=null && !"".equals(mem2.dasmLocation)) return mem2.dasmLocation+pos;
          return HexNum(ShortToExe((int)mem.related), defaultMode)+pos;  
        } 
    } catch (Exception e) {
        return HexNum("xxxx", defaultMode);
      }
    

     
    if (mem.userLocation!=null && !"".equals(mem.userLocation)) return mem.userLocation;
    if (mem.dasmLocation!=null && !"".equals(mem.dasmLocation)) return mem.dasmLocation;
    return HexNum(ShortToExe((int)addr), defaultMode);        
  } 
  
  /**
   * Set the dasm label
   * 
   * @param addr the address to add as label
   */
  protected void setLabel(long addr) {
    if (addr<0 || addr>0xffff) return;
    
    MemoryDasm mem=memory[(int)addr];
           
    if (mem.isInside && !mem.isGarbage) {
        switch (mem.type) {
            case TYPE_PLUS:
            case TYPE_MINUS:
                memory[mem.related].dasmLocation="W"+ShortToExe(mem.related);
                break;
            case TYPE_PLUS_MAJOR:
            case TYPE_PLUS_MINOR: 
            case TYPE_MINUS_MAJOR:
            case TYPE_MINUS_MINOR:   
                memory[mem.related & 0xFFFF].dasmLocation="W"+ShortToExe(mem.related & 0xFFFF);
                break;
            default:
                mem.dasmLocation="W"+ShortToExe((int)addr); // create dasm location only if there is not a related one
                break;
        }
    }
  }
  
  /**
   * Set the label for plus relative address if this is the case
   * 
   * @param addr tha address where to point
   * @param offset the offset to add
   */
  protected void setLabelPlus(long addr, int offset) {
    if (addr<0 || addr+offset>0xffff) return;
    
    MemoryDasm mem=memory[(int)addr+offset];
    
    if (mem.isInside) {
      // set as relative + unless it is already set (even by user)
      if (mem.dasmLocation!=null && (mem.type!=TYPE_PLUS) && (mem.type!=TYPE_MINUS)) {
        mem.type=TYPE_PLUS;
        mem.related=(int)addr;
        setLabel(addr);
      } 
    }
  }
  
  /**
   * Set the label for minus relative address if this is the case
   * 
   * @param addr tha address where to point
   * @param offset the offset to sub
   */
  protected void setLabelMinus(long addr, int offset) {
    if (addr<0 || addr+offset>0xffff) return;
    
    MemoryDasm mem=memory[(int)addr-offset];
    
    if (mem.isInside && mem.type!=TYPE_PLUS) {
      if (mem.dasmLocation!=null) {
        mem.type=TYPE_MINUS;
        mem.related=(int)addr;
        setLabel(addr);
      } 
    }
  }  
  
  private static final String SPACES="                                                                               "; 
  private static final String TABS="\t\t\t\t\t\t\t\t\t\t";
  
  /**
   * Return spaces/tabs to use in start of instruction
   * 
   * @param mem the memory of this line
   * @return the spaces/tabs
   */
  protected String getInstrSpacesTabs(MemoryDasm mem) {
    if (!option.labelOnSepLine) {
      int num=0;  
      if (mem.userLocation!=null && !"".equals(mem.userLocation)) num=mem.userLocation.length()+1;
      else if (mem.dasmLocation!=null && !"".equals(mem.dasmLocation)) num=mem.dasmLocation.length()+1;
      return SPACES.substring(0, (option.maxLabelLength-num<0 ? 1: option.maxLabelLength-num))+SPACES.substring(0, option.numInstrSpaces)+TABS.substring(0, option.numInstrTabs);
    } else return SPACES.substring(0, option.numInstrSpaces)+TABS.substring(0, option.numInstrTabs);
  }    
  
  /**
   * Return spaces/tabs to use in comment after instruction
   * 
   * @param skip amount to skip
   * @return the spaces/tabs
   */
  protected String getInstrCSpacesTabs(int skip) {
    return SPACES.substring(0, (option.numInstrCSpaces-skip<0 ? 1:option.numInstrCSpaces-skip))+TABS.substring(0, option.numInstrCTabs);
  } 
  
  /**
   * Return spaces/tabs to use for separate opcode from operand
   * 
   * @return the spaces/tabs
   */
  protected String getSpacesTabsOp() {
    return SPACES.substring(0, (option.numSpacesOp))+TABS.substring(0, option.numTabsOp);  
  }

  /**
   * Return the mnemonic assembler instruction rapresent by passed code bytes,
   * using last position an program counter.
   *
   * @param buffer the buffer containg the data 
   * @return a string menemonic rapresentation of instruction
   */
  public String dasm(byte[] buffer) {
    return dasm(buffer, pos, pc);
  }
  
  @Override
  public String dasm(byte[] buffer, int pos, long pc) {
    return "";
  }

  @Override
  public String dcom(int iType, int aType, long addr, long value) {
     return "";
  }

  @Override
  public String cdasm(byte[] buffer, int start, int end, long pc) {
    return "";
  }

  @Override
  public String csdasm(byte[] buffer, int start, int end, long pc) {
    return "";
  }
}
