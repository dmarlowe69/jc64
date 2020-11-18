/**
 * @(#)Assembler 2020/11/01
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
package sw_emulator.software;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import sw_emulator.math.Unsigned;
import sw_emulator.swing.main.DataType;
import sw_emulator.swing.main.Option;

/**
 * Assembler definitions:
 * ->Name
 * ->Label
 * ->Byte
 * ->Word
 * 
 * @author ice
 */
public class Assembler {
   private static final String SPACES="                                        ";  
   
/**
   * Convert a unsigned byte (containing in a int) to Exe upper case 2 chars
   *
   * @param value the byte value to convert
   * @return the exe string rapresentation of byte
   */
  protected static String ByteToExe(int value) {
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
  protected static String ShortToExe(int value) {
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
    * Action type
    */ 
   public interface ActionType {
     /**
      * Flush the actual data to the output stream
      * 
      * @param str the output stream
      */   
      void flush(StringBuilder str);
   } 
    
   /** 
    * Name of the assembler
    */ 
   public enum Name {
     DASM {
       @Override
       public String getName() {
         return "Dasm";
       }
     },     
     TMPX {
       @Override
       public String getName() {
         return "TMPx";
       }
     },     
     CA65 {
       @Override
       public String getName() {
         return "CA65";
       }
     },     
     ACME {
       @Override
       public String getName() {
         return "ACME";
       }
     },     
     KICK {
       @Override
       public String getName() {
         return "KickAssembler";
       }
     };        
    
     /**
      * Get the char of this data type
      *  -> ORG $xxyy
      *  -> .ORG $xxyy
      *  -> * = $xxyy
      *  -> .pc $xxyy
      * @return the char
      */
     public abstract String getName();
   }
   
   /**
    * Starting declaration
    *  -> processor 6502
    *  -> .cpu "6502"
    *  -> .cpu 6502
    *  -> .setcpu 6502x
    *  -> .p02
    *  -> !CPU 6510
    */
   public enum Starting implements ActionType {
      PROC,             // processor 6502
      DOT_CPU_A,        // .cpu "6502"
      DOT_CPU,          // .cpu 6502
      DOT_SETCPU,       // .setcpu 6502x
      DOT_P02,          // .p02
      MARK_CPU;         // !cpu 6510
       
       
      @Override
      public void flush(StringBuilder str) {
        switch (aStarting) {
          case PROC:
            str.append("  processor 6502\n\n");
            break;
          case DOT_CPU_A:
            str.append("  .cpu \"6502\"\n\n");  
            break;
          case DOT_CPU:
            str.append("  .cpu 6502\n\n");
            break;
          case DOT_SETCPU:
            str.append("  .cpu 6502\n\n");
            break; 
          case DOT_P02:
            str.append("  .p02\n\n");
            break;    
          case MARK_CPU:
            str.append("  !cpu 6510\n\n");
            break;     
        }  
      }
   } 
   
   /**
    * Origin declaration
    *  -> org $xxyy
    *  -> .org $xxyy
    *  -> *=$xxyy
    *  -> .pc $xxyy
    */
   public enum Origin implements ActionType {
      ORG,              //  org $xxyy
      DOT_ORG,          // .org $xxyy
      ASTERISK,         //   *= $xxyy
      DOT_PC;           //  .pc $xxyy
           
      @Override
      public void flush(StringBuilder str) {
        switch (aOrigin) {
          case ORG:
              str.append("  org $").append(ShortToExe(lastPC)).append("\n\n");
            break;
          case DOT_ORG:
              str.append("  .org $").append(ShortToExe(lastPC)).append("\n\n");
            break;
          case ASTERISK:
              str.append("  *=").append(ShortToExe(lastPC)).append("\n\n");
            break;
          case DOT_PC:
              str.append("  .pc $").append(ShortToExe(lastPC)).append("\n\n");
            break;
        }    
      }
   }
   
   /**
    * Label declaration type
    *  -> xxxx
    *  -> xxxx:
    */
   public enum Label implements ActionType {
      NAME,               // xxxx
      NAME_COLON;         // xxxx:
    
      @Override
      public void flush(StringBuilder str) {
        // add the label if it was declared by dasm or user   
        String label=null;
        if (lastMem.userLocation!=null && !"".equals(lastMem.userLocation)) label=lastMem.userLocation;
        else if (lastMem.dasmLocation!=null && !"".equals(lastMem.dasmLocation)) label=lastMem.dasmLocation;
          
        switch (aLabel) {
          case NAME:
            str.append(label);  
            break; 
          case NAME_COLON:
            str.append(label).append(":");
            break;
        }
      }
    }    
   
   /**
    * Line comment
    * 
    *  -> ; xxx
    *  -> /* xxx *\/
    *  -> // xxx
    */
   public enum Comment implements ActionType {
      SEMICOLON,       // ; xxxx
      CSTYLE,          // /* xxx */
      DOUBLE_BAR;      // // xxx 
    
      @Override
      public void flush(StringBuilder str) {
        String comment=lastMem.dasmComment;
        if (lastMem.userComment != null && !"".equals(lastMem.userComment)) comment=lastMem.userComment;
        
        switch (aComment) {
          case SEMICOLON:
            str.append("; ").append(comment).append("\n");
            break;
          case CSTYLE:
            str.append("/* ").append(comment).append(" */\n");  
            break;  
          case DOUBLE_BAR:
            str.append("// ").append(comment).append("\n");
            break;
        }
      }
    }  
   
   /**
    * Block comment
    * 
    *  -> ; xxxx
    *  -> /\* xxx *\/
    *  -> if 0 xxx endif
    *  -> .if 0 xxx .endif
    *  -> .comment xxx .endc
    */
   public enum BlockComment implements ActionType {
      SEMICOLON,       // ; xxxx
      CSTYLE,          // /* xxx */ 
      IF,              // if 0 xxx endif
      DOT_IF,          // .if 0 xxx .endif
      MARK_IF,         // !if 0 { xxx }
      COMMENT;         // .comment xxx .endc
    
      @Override
      public void flush(StringBuilder str) {
        // split by new line
        String[] lines = lastMem.userBlockComment.split("\\r?\\n");  
     
        switch (aBlockComment) {
          case SEMICOLON:    
            for (String line : lines) {
              if ("".equals(line) || " ".equals(line)) str.append("\n");
              else str.append(";").append(line).append("\n");   
            }                      
            break;         
          case CSTYLE:
            boolean isOpen=false;
            for (String line : lines) {
              if ("".equals(line) || " ".equals(line)) {
                if (isOpen) str.append("/*\n\n");
                else {
                 str.append("\n*\\n\n");
                 isOpen=false;
                }
              } else {
                  if (!isOpen) {
                    isOpen=true;
                    str.append("/*\n");
                  }
                  str.append(";").append(line).append("\n");
                }   
            }        
            if (!isOpen) str.append("\n*\\n");         
            break;          
          case IF:
            isOpen=false;
            for (String line : lines) {
              if ("".equals(line) || " ".equals(line)) {
                if (isOpen) str.append("endif\n\n");
                else {
                 str.append("if 0\n");
                 isOpen=false;
                }
              } else {
                  if (!isOpen) {
                    isOpen=true;
                    str.append("if 0\n");
                  }
                  str.append(";").append(line).append("\n");
                }   
            }        
            if (!isOpen) str.append("endif\n");   
            break;
          case DOT_IF:
            isOpen=false;
            for (String line : lines) {
              if ("".equals(line) || " ".equals(line)) {
                if (isOpen) str.append(".endif\n\n");
                else {
                 str.append(".if 0\n");
                 isOpen=false;
                }
              } else {
                  if (!isOpen) {
                    isOpen=true;
                    str.append(".if 0\n");
                  }
                  str.append(";").append(line).append("\n");
                }   
            }        
            if (!isOpen) str.append(".endif\n");    
            break;  
          case MARK_IF:
            isOpen=false;
            for (String line : lines) {
              if ("".equals(line) || " ".equals(line)) {
                if (isOpen) str.append("}\n\n");
                else {
                 str.append("!if 0 {\n\n");
                 isOpen=false;
                }
              } else {
                  if (!isOpen) {
                    isOpen=true;
                    str.append("!if 0 {\n");
                  }
                  str.append(";").append(line).append("\n");
                }   
            }        
            if (!isOpen) str.append("}\n");    
            break;  
          case COMMENT:
            isOpen=false;
            for (String line : lines) {
              if ("".equals(line) || " ".equals(line)) {
                if (isOpen) str.append(".comment\n\n");
                else {
                 str.append(".endc\n\n");
                 isOpen=false;
                }
              } else {
                  if (!isOpen) {
                    isOpen=true;
                    str.append(".comment\n");
                  }
                  str.append(";").append(line).append("\n");
                }   
            }        
            if (!isOpen) str.append(".endc\n");   
            break;  
        } 
      }
    }    
      
   /**
    * Byte declaration type
    *  -> .byte $xx
    *  -> byte $xx
    *  -> dc $xx
    *  -> dc.b $xx
    *  -> -byt $xx
    *  -> !byte $xx
    *  -> !8 $xx
    */
   public enum Byte implements ActionType {
      DOT_BYTE,           // .byte $xx
      BYTE,               //  byte $xx
      DC_BYTE,            //    dc $xx
      DC_B_BYTE,          //  dc.b $xx
      DOT_BYT_BYTE,       //  .byt $xx
      MARK_BYTE,          // !byte $xx 
      EIGHT_BYTE;         //    !8 $xx     
      
      @Override
      public void flush(StringBuilder str) {
        if (list.isEmpty()) return; 
        
        MemoryDasm mem;
        MemoryDasm memRel;
        
        // create starting command according to the kind of byte
        switch (aByte) {
          case DOT_BYTE:
            str.append(("  .byte "));
            break;
          case BYTE:
            str.append(("  byte "));
            break;
          case DC_BYTE:
           str.append(("  dc "));   
            break;
          case DC_B_BYTE:
            str.append(("  dc.b "));
            break;
          case DOT_BYT_BYTE:
            str.append(("  .byt.b "));  
            break;
          case MARK_BYTE:
            str.append(("  !byte "));   
            break;  
          case EIGHT_BYTE:
            str.append(("  !8 "));  
            break;  
        }
          
        Iterator<MemoryDasm> iter=list.iterator();
        while (iter.hasNext()) {
          // accodate each bytes in the format choosed
          mem=iter.next();
          memRel=listRel.pop();
          
          if (mem.type=='<' || mem.type=='>') {           
            if (memRel.userLocation!=null && !"".equals(memRel.userLocation)) str.append(mem.type).append(memRel.userLocation);
            else if (memRel.dasmLocation!=null && !"".equals(memRel.dasmLocation)) str.append(mem.type).append(memRel.dasmLocation);
                 else str.append(mem.type).append("$").append(ShortToExe(memRel.address));
          } else str.append(getByteType(mem.dataType, mem.copy));
          if (listRel.size()>0) str.append(", ");  
          else str.append("\n");
        }
        list.clear();
      }  
      
      /**
       * Return the byte represented as by the given type
       * 
       * @param dataType the type to use 
       * @param value the byte value
       * @return the converted string
       */
      private String getByteType(DataType dataType, byte value) {
        switch (dataType)   {
          case BYTE_DEC:
            return ""+Unsigned.done(value);
          case BYTE_BIN:
            return "%"+Integer.toBinaryString((value & 0xFF) + 0x100).substring(1);
          case BYTE_CHAR:
            //return "\""+(char)Unsigned.done(value)+"\"";
            return "'"+(char)Unsigned.done(value);
          case BYTE_HEX:
          default:
            return "$"+ByteToExe(Unsigned.done(value));
        }
      }
   }    
   
   /**
    * Word declaration type
    *  -> .word $xxyy
    *  -> word $xxyy
    *  -> dc.w $xxyy
    *  -> .dbyte $xxyy
    *  -> !word $xxyy
    *  -> !16 $xxyy
    */
   public enum Word implements ActionType {
     DOT_WORD,            //  .word $xxyy
     WORD,                //   word $xxyy
     DC_W_WORD,           //   dc.w $xxyy
     DOT_DBYTE,           // .dbyte $xxyy
     MARK_WORD,           //  !word $xxyy
     SIXTEEN_WORD;        //    !16 $xxyy
     
     @Override
     public void flush(StringBuilder str) {         
       if (list.isEmpty()) return; 
       
       MemoryDasm memLow;
       MemoryDasm memHigh;
       MemoryDasm memRelLow;
       MemoryDasm memRelHigh;
        
       // create starting command according to the kind of byte
       switch (aWord) {
         case DOT_WORD:
           str.append(("  .word "));  
           break;
         case WORD:
           str.append(("  word "));   
           break;
         case DC_W_WORD:
           str.append(("  dc.w "));  
           break;
         case DOT_DBYTE:
           str.append(("  .dbyte "));   
           break;
         case MARK_WORD:
           str.append(("  !word "));   
           break;
         case SIXTEEN_WORD:
           str.append(("  !16 "));  
           break;  
       }
       
       while (!list.isEmpty()) {
         // if only 1 byte left, use byte coding
         if (list.size()==1) aByte.flush(str);
         else {
           memLow=list.pop();
           memRelLow=listRel.pop();
           memHigh=list.pop();
           memRelHigh=listRel.pop();           
           
           if (memLow.type=='<' && memHigh.type=='>' && memLow.related==memHigh.related) {
             if (memRelLow.userLocation!=null && !"".equals(memRelLow.userLocation)) str.append(memRelLow.userLocation);
            else if (memRelLow.dasmLocation!=null && !"".equals(memRelLow.dasmLocation)) str.append(memRelLow.dasmLocation);
                 else str.append("$").append(ShortToExe(memRelLow.address));  
           } else {
             // if cannot make a word with relative locations, force all to be of byte type
             if (memLow.type=='<' || memLow.type=='>' || memHigh.type=='>' || memHigh.type=='<')  {
               list.addFirst(memHigh);
               list.addFirst(memLow);
               listRel.addFirst(memRelHigh);
               listRel.addFirst(memRelLow);
               aByte.flush(str);
             }
             else str.append("$").append(ByteToExe(Unsigned.done(memHigh.copy))).append(ByteToExe(Unsigned.done(memLow.copy)));  
             
             
           }
           if (list.size()>=2) str.append(", ");
           else str.append("\n");
         }
       }
     } 
   }
   
   /**
    * Tribyte declaration type
    */
   public enum Tribyte implements ActionType {

        ;
     @Override
     public void flush(StringBuilder str) {
      
     } 
   }   
   
   /**
    * Long declaration type
    */
   public enum Long implements ActionType  {
        ;      
       @Override
      public void flush(StringBuilder str) {
      
      } 
   }    
   
   /** Fifo list  of memory locations */
   protected static LinkedList<MemoryDasm> list=new LinkedList();
   
   /** Fifo list of related memory locations */
   protected static LinkedList<MemoryDasm> listRel=new LinkedList();   
   
   /** Option to use */
   protected static Option option;
         
   /** Last used memory dasm */
   protected static MemoryDasm lastMem=null;
   
   /** Last program counter */
   protected static int lastPC=0;
   
   
   /** Assembler starting to use */
   protected static Assembler.Starting aStarting; 
   
   /** Assembler origin to use */
   protected static Assembler.Origin aOrigin; 
   
   /** Assembler label to use */
   protected static Assembler.Label aLabel;
   
   /** Assembler block comment to use */
   protected static Assembler.BlockComment aBlockComment;  
   
   /** Assembler comment to use */
   protected static Assembler.Comment aComment;  
  
   /** Assembler byte type */
   protected static Assembler.Byte aByte;
  
   /** Assembler word type */
   protected static Assembler.Word aWord;
   
   /** Actual type being processed */
   ActionType actualType=null;

  
  
   /**
    * Set the option to use
    * 
    * @param option the option to use
    * @param aStarting the starting type to use 
    * @param aOrigin the origin type to use
    * @param aLabel the label type to use
    * @param aComment the comment type to use
    * @param aBlockComment the comment type to use
    * @param aByte the byte type to use
    * @param aWord the word type tp use
    * 
    */
   public void setOption(Option option, 
                         Assembler.Starting aStarting,
                         Assembler.Origin aOrigin,
                         Assembler.Label aLabel,  
                         Assembler.Comment aComment, 
                         Assembler.BlockComment aBlockComment,
                         Assembler.Byte aByte, 
                         Assembler.Word aWord) {
     Assembler.aStarting=aStarting;  
     Assembler.option=option;
     Assembler.aOrigin=aOrigin;
     Assembler.aLabel=aLabel;
     Assembler.aComment=aComment;
     Assembler.aBlockComment=aBlockComment;
     Assembler.aByte=aByte;
     Assembler.aWord=aWord;
   } 
   
   /**
    * Put the value into the buffer and manage 
    * 
    * @param str the string builder where put result
    * @param mem the memory being processed
    * @param memRel eventual memory related
    */
   public void putValue(StringBuilder str, MemoryDasm mem, MemoryDasm memRel) {
     ActionType type=actualType;  
     lastMem=mem;
     
     // if there is a block comments use it
     if (mem.userBlockComment!=null && !"".equals(mem.userBlockComment)) {
       type=actualType;
       actualType=aBlockComment;
       flush(str);
       actualType=type;
     }     
       
     // if this is a label then the type will change  
     if (!(lastMem.type=='+' || lastMem.type=='-')) {
       if (mem.userLocation!=null && !"".equals(mem.userLocation)) type=aLabel;
       else if (mem.dasmLocation!=null && !"".equals(mem.dasmLocation)) type=aLabel;  
     }
      
     // test if it change type  
     if (type!=actualType) {
       flush(str);              // write back previous data
       
       // we can have only one label in a memory row, so process it if this is the case
       if (type==aLabel) {
         int size=str.length();         
         type.flush(str);             // write back the label         
       
         // check if there is a comment for the label
         if (lastMem.userComment!=null) {
           size=str.length()-size;      // get number of chars used  
           str.append(SPACES.substring(0, SPACES.length()-size));
           type=aComment;
           type.flush(str);
         } else str.append("\n");  // close label by going in a new line
       }   
              
       actualType=getType(mem);
     }  
          
     
     list.add(mem);
     listRel.add(memRel);
     
     // we are processing bytes?
     if (actualType instanceof Byte) {
       // look if it is time to aggregate data
       if (list.size()==option.maxByteAggregate) actualType.flush(str);         
     } else
     // we are processing word?    
     if (actualType instanceof Word) {
       // look if it is time to aggregate data
       if (list.size()==option.maxWordAggregate*2) actualType.flush(str);         
     } else
     // we are processing tribyte?    
     if (actualType instanceof Tribyte) {
       // look if it is time to aggregate data
       if (list.size()==option.maxTribyteAggregate*3) actualType.flush(str);         
     } else
     // we are processing long?    
     if (actualType instanceof Long) {
       // look if it is time to aggregate data
       if (list.size()==option.maxLongAggregate*4) actualType.flush(str);         
     }         
     
   }
   
   /**
    * Put the starting string
    * 
    * @param str the steam for output
    */
   public void setStarting(StringBuilder str) {
     aStarting.flush(str);
   }   
   
   /**
    * Put the origin of PC
    * 
    * @param str the steam for output
    * @param pc the program counter to set
    */
   public void setOrg(StringBuilder str, int pc) {
     lastPC=pc;
      aOrigin.flush(str);
   }
   
   /**
    * Set a word and put to ouptput steam (it deletes anything that threre are in queue)
    * 
    * @param str the output stream
    * @param low the low byte
    * @param high the hight byte
    * @param comment eventual comment to add
    */
   public void setWord(StringBuilder str, byte low, byte high, String comment) {
     MemoryDasm lowMem=new MemoryDasm();
     MemoryDasm highMem=new MemoryDasm();
     
     lowMem.copy=low;
     lowMem.dataType=DataType.WORD;
     lowMem.type='W';
     lowMem.related=-1;
     highMem.copy=high;
     highMem.dataType=DataType.WORD;
     highMem.type='W';
     highMem.related=-1;
     
     list.clear();
     listRel.clear();
     list.add(lowMem);
     listRel.add(null);
     list.add(highMem);
     listRel.add(null);
     
     int size=0;
     
     if (comment!=null) {
       highMem.userComment=comment;
       lastMem=highMem;
       size=str.length();
     }
     
     actualType=aWord;
     flush(str);
     actualType=null;
     
     if (comment!=null) {
       str.deleteCharAt(str.length()-1);              // remove \n
       size=str.length()-size;                      // get number of chars used  
       str.append(SPACES.substring(0, SPACES.length()-size));
       aComment.flush(str);  
     }
   }
   
   /**
    * Flush the actual data to the output stream
    * 
    * @param str the output stream
    */
   public void flush(StringBuilder str) {
     if (actualType!=null) actualType.flush(str);
   }
   
   /**
    * Get the tyoe for this location
    * @param mem
    * @return 
    */
   private ActionType getType(MemoryDasm mem) {
     if (!mem.isData) return null;
     
     switch (mem.dataType) {
       case BYTE_HEX:
       case BYTE_DEC:
       case BYTE_BIN:
       case BYTE_CHAR:
         return aByte;
       case WORD:
         return aWord;
           
     }
     
     // default is of Byte type
     return aByte;
   }   
}
               

