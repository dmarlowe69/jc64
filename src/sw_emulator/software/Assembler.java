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
      * 
      * @return the char
      */
     public abstract String getName();
   }
   
   /**
    * Label declaration type
    *  -> xxxx
    *  -> xxxx:
    */
   public enum Label implements ActionType {
      NAME,               // xxxx
      NAME_COLON;          // xxxx:
    
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
                 str.append("if\n\n");
                 isOpen=false;
                }
              } else {
                  if (!isOpen) {
                    isOpen=true;
                    str.append("if\n");
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
                 str.append(".if\n\n");
                 isOpen=false;
                }
              } else {
                  if (!isOpen) {
                    isOpen=true;
                    str.append(".if\n");
                  }
                  str.append(";").append(line).append("\n");
                }   
            }        
            if (!isOpen) str.append(".endif\n");    
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
      BYT_BYTE,           //  .byt $xx
      MARK_BYTE,          // !byte $xx 
      EIGHT_BYTE;         //    !8 $xx     
      
      @Override
      public void flush(StringBuilder str) {
        MemoryDasm mem;
        MemoryDasm memRel;
        
        // create starting command according to the kind of byte
        switch (aByte) {
          case DOT_BYTE:
            str.append((" .byte "));
            break;
          case BYTE:
            str.append((" byte "));
            break;
          case DC_BYTE:
           str.append((" dc "));   
            break;
          case DC_B_BYTE:
            str.append((" dc.b "));
            break;
          case BYT_BYTE:
            str.append((" dc.b "));  
            break;
          case MARK_BYTE:
            str.append((" !byte "));   
            break;  
          case EIGHT_BYTE:
            str.append((" !8 "));  
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
          } else str.append("$").append(ByteToExe(Unsigned.done(mem.copy)));
          if (listRel.size()>0) str.append(", ");                  
        }
        list.clear();
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
       MemoryDasm memLow;
       MemoryDasm memHigh;
       MemoryDasm memRelLow;
       MemoryDasm memRelHigh;
        
       // create starting command according to the kind of byte
       switch (aWord) {
         case DOT_WORD:
           str.append((" .word "));  
           break;
         case WORD:
           str.append((" word "));   
           break;
         case DC_W_WORD:
           str.append((" dc.w "));  
           break;
         case DOT_DBYTE:
           str.append((" .dbyte "));   
           break;
         case MARK_WORD:
           str.append((" !word "));   
           break;
         case SIXTEEN_WORD:
           str.append((" !16 "));  
           break;  
       }
       
       while (!list.isEmpty()) {
         // if only 1 byte left, use byte coding
         if (list.size()==1) aByte.flush(str);
         else {
           memLow=list.peek();
           memRelLow=listRel.peek();
           memHigh=list.peek();
           memRelHigh=listRel.peek();
           
           if (memLow.type=='<' && memHigh.type=='>' && memLow.related==memHigh.related) {
             if (memRelLow.userLocation!=null && !"".equals(memRelLow.userLocation)) str.append(memRelLow.userLocation);
            else if (memRelLow.dasmLocation!=null && !"".equals(memRelLow.dasmLocation)) str.append(memRelLow.dasmLocation);
                 else str.append("$").append(ShortToExe(memRelLow.address));  
           } else {
             // if annot make a word with relative locations, force all to be of byte type
             if (memLow.type=='<' || memLow.type=='>' || memHigh.type=='>' || memHigh.type=='<')  aByte.flush(str);
             else str.append("$").append(ByteToExe(Unsigned.done(memHigh.copy))).append(ByteToExe(Unsigned.done(memLow.copy)));  
             
             // remove the used elements
             list.pop();
             list.pop();
             listRel.pop();
             listRel.pop();
           }
           if (list.size()>=2) str.append(", ");
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
    * @param aLabel the label type to use
    * @param aComment the comment type to use
    * @param aBlockComment the comment type to use
    * @param aByte the byte type to use
    * @param aWord the word type tp use
    * 
    */
   public void setOption(Option option, 
                         Assembler.Label aLabel,  
                         Assembler.Comment aComment, 
                         Assembler.BlockComment aBlockComment,
                         Assembler.Byte aByte, 
                         Assembler.Word aWord) {
     Assembler.option=option;
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
     
     switch (mem.type) {
       case 'B':
       case 'D':
       case 'Y':
       case 'R':
         return aByte;
       case 'W':
         return aWord;
           
     }
     
     // default is of Byte type
     return aByte;
   }   
}
               

