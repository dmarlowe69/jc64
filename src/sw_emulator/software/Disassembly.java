/**
 * @(#)Disassembly 2019/12/15
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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import sw_emulator.math.Unsigned;
import sw_emulator.software.cpu.M6510Dasm;
import sw_emulator.software.machine.C128Dasm;
import sw_emulator.software.machine.C64Dasm;
import sw_emulator.software.machine.C64MusDasm;
import sw_emulator.software.machine.C64SidDasm;
import sw_emulator.software.machine.C1541Dasm;
import sw_emulator.software.machine.CPlus4Dasm;
import sw_emulator.software.machine.CVic20Dasm;
import sw_emulator.swing.main.FileType;
import sw_emulator.swing.main.MPR;
import sw_emulator.swing.main.Option;
import sw_emulator.swing.main.TargetType;

/**
 * Disassembly the given buffer of data
 * 
 * @author ice
 */
public class Disassembly {
  /** Source of disassembly */
  public String source;
  
  /** Raw disassembly */
  public String disassembly;    
  
  /** Starting adress of data from file */
  public int startAddress;
  
  /** Ending address of data from file */
  public int endAddress;
  
   /** Starting buffer adress of data from file */
  public int startBuffer;
  
  /** Ending buffer address of data from file */
  public int endBuffer; 
  
  /** Starting address for MPR */
  public int[] startMPR;
  
  /** Ending address for MPR */
  public int[] endMPR;
  
  /** Buffer of data to disassemble */
  private byte[] inB;
  
  /** Eventual mulpiple program */
  private MPR mpr;
  
  /** The type of file */
  private FileType fileType;
  
  /** The option for disassembler */
  private Option option;
  
  /** Memory dasm */
  public MemoryDasm[] memory;
    
  /**
   * Disassemble the given data
   * 
   * @param fileType the file type
   * @param inB the buffer
   * @param option for disassembler
   * @param memory the memory for dasm
   * @param mpr eventual MPR blocks to use
   * @param targetType target machine type
   * @param asSource true if disassembly output should be as a source file
   */
  public void dissassembly(FileType fileType, byte[] inB, Option option,  MemoryDasm[] memory, MPR mpr, TargetType targetType, boolean asSource) {
    this.inB=inB;
    this.fileType=fileType;
    this.option=option;
    this.mpr=mpr;

    this.memory=memory;
    
    startMPR=null;
    endMPR=null;
      
    // avoid to precess null data  
    if (inB==null) {
      source="";
      disassembly="";
      return;
    }
    
    switch (fileType) {
      case MUS:
        dissassemblyMUS(asSource);                
        break;
      case SID:
        dissassemblySID(asSource);  
        break;
      case PRG:
        disassemlyPRG(asSource, targetType);  
        break;
      case MPR:
        disassemlyMPR(asSource, targetType);  
        break;        
      case UND:            
        source="";
        disassembly="";   
        break;
    }
  }
  
  /**
   * Disassembly a MUS file
   * 
   * @param asSource true if output should be as a source file
   */
  private void dissassemblyMUS(boolean asSource) {    
    int ind1;             // Mus file voice 1 address
    int ind2;             // Mus file voice 2 address
    int ind3;             // Mus file voice 3 address 
    int txtA;             // Mus file txt address 
    int v1Length;         // length of voice 1 data
    int v2Length;         // length of voice 2 data
    int v3Length;         // length of voice 3 data
    int musPC;            // PC value of start of mus program
    
    StringBuilder tmp=new StringBuilder();    
    C64MusDasm mus=new C64MusDasm();
    
    // don't use start/end address for mus
    startAddress=-1;
    endAddress=-1;
    startBuffer=-1;
    endBuffer=-1;
    
    musPC=Unsigned.done(inB[0])+Unsigned.done(inB[1])*256;
    v1Length=Unsigned.done(inB[2])+Unsigned.done(inB[3])*256;
    v2Length=Unsigned.done(inB[4])+Unsigned.done(inB[5])*256;
    v3Length=Unsigned.done(inB[6])+Unsigned.done(inB[7])*256;

    // calculate pointer to voice data
    ind1=8;
    ind2=ind1+v1Length;
    ind3=ind2+v2Length;
    txtA=v1Length+v2Length+v3Length+8;
    
    tmp.append(fileType.getDescription(inB));
    tmp.append("\n");
    tmp.append(fileType.getDescription(inB)).append("\n");
    tmp.append("VOICE 1 MUSIC DATA: \n\n");
    tmp.append(mus.cdasm(inB, ind1, ind2-1, ind1+musPC));
    tmp.append("\nVOICE 2 MUSIC DATA: \n\n");
    tmp.append(mus.cdasm(inB, ind2, ind3-1, ind2+musPC));
    tmp.append("\nVOICE 3 MUSIC DATA: \n\n");
    tmp.append(mus.cdasm(inB, ind3, txtA-1, ind3+musPC));    
    
    disassembly=tmp.toString();
    source="";
  }
  
  /**
   * Disassembly a SID file
   * 
   * @param asSource true if output should be as a source file
   */
  private void dissassemblySID(boolean asSource) {
    int psidDOff;     // psid data offeset   
    int psidLAddr;    // psid load address
    int psidIAddr;    // psid init address
    int psidPAddr;    // psid play address     
    int sidPos;       // Position in buffer of start of sid program     
    int sidPC;        // PC value of start of sid program 
      
    C64SidDasm sid=new C64SidDasm();
    sid.language=option.commentLanguage;
    sid.setMemory(memory);
    sid.setOption(option);    
    
    ((C64Dasm)sid).language=option.commentLanguage;        
    ((C64Dasm)sid).commentZeroPage=option.commentC64ZeroPage;
    ((C64Dasm)sid).commentStackArea=option.commentC64StackArea;
    ((C64Dasm)sid).comment200Area=option.commentC64_200Area;
    ((C64Dasm)sid).comment300Area=option.commentC64_300Area;        
    ((C64Dasm)sid).commentScreenArea=option.commentC64ScreenArea;
    ((C64Dasm)sid).commentBasicFreeArea=option.commentC64BasicFreeArea;
    ((C64Dasm)sid).commentBasicRom=option.commentC64BasicRom;
    ((C64Dasm)sid).commentFreeRam=option.commentC64FreeRam;
    ((C64Dasm)sid).commentVicII=option.commentC64VicII;
    ((C64Dasm)sid).commentSid=option.commentC64Sid;
    ((C64Dasm)sid).commentColorArea=option.commentC64ColorArea;
    ((C64Dasm)sid).commentCia1=option.commentC64Cia1;
    ((C64Dasm)sid).commentCia2=option.commentC64Cia2;
    
    psidLAddr=Unsigned.done(inB[9])+Unsigned.done(inB[8])*256;    
    psidIAddr=Unsigned.done(inB[11])+Unsigned.done(inB[10])*256;
    psidPAddr=Unsigned.done(inB[13])+Unsigned.done(inB[12])*256;    
    memory[psidIAddr].userLocation=option.psidInitSongsLabel;
    memory[psidPAddr].userLocation=option.psidPlaySoundsLabel;
    
    psidDOff=Unsigned.done(inB[0x07])+Unsigned.done(inB[0x06])*256;
    
    //calculate address for disassembler
    if (psidLAddr==0) {
      sidPC=Unsigned.done(inB[psidDOff])+Unsigned.done(inB[psidDOff+1])*256;
      sidPos=psidDOff+2;
      startAddress=sidPC;
      endAddress=sidPC+inB.length-sidPos-1;      
    } else {
        sidPos=psidDOff;
        sidPC=psidLAddr;
        startAddress=sidPC;
        endAddress=sidPC+inB.length-sidPos-1;     
      }
    startBuffer=sidPos;
    endBuffer=sidPos+(endAddress-startAddress);
    
    markInside(startAddress, endAddress, sidPos);
    
    // search for SID frequency table
    SidFreq.instance.identifyFreq(inB, memory, sidPos, inB.length, sidPC-sidPos,
            option.sidFreqLoLabel, option.sidFreqHiLabel);
        
    StringBuilder tmp=new StringBuilder();
    
    if (asSource) {
      sid.upperCase=option.opcodeUpperCaseSource;
        
      tmp.append("  processor 6502\n\n");
      
      // calculate org for header
      int header=sidPC;   
      header-=sidPos;
      if (psidLAddr==0) header-=2;      
      tmp.append("  .org $").append(ShortToExe(header)).append("\n\n");
      
      // create header of PSID
      if (inB[0]=='P') tmp.append("  .byte \"PSID\"\n");
      else tmp.append("  .byte \"RSID\"\n");
      
      tmp.append("  .word $").append(ShortToExe(inB[0x04]+inB[0x05]*256)).append("         ; version\n");
      tmp.append("  .word $").append(ShortToExe(inB[0x06]+inB[0x07]*256)).append("         ; data offset\n");
      tmp.append("  .word $").append(ShortToExe(inB[0x08]+inB[0x09]*256)).append("         ; load address in CBM format\n");
      tmp.append("  .byte >").append(option.psidInitSongsLabel).append("\n");
      tmp.append("  .byte <").append(option.psidInitSongsLabel).append("\n");
      tmp.append("  .byte >").append(option.psidPlaySoundsLabel).append("\n");
      tmp.append("  .byte <").append(option.psidPlaySoundsLabel).append("\n");
      tmp.append("  .word $").append(ShortToExe(inB[0x0E]+inB[0x0F]*256)).append("         ; songs\n");
      tmp.append("  .word $").append(ShortToExe(inB[0x10]+inB[0x12]*256)).append("         ; default song\n");
      tmp.append("  .word $").append(ShortToExe(inB[0x12]+inB[0x13]*256)).append("         ; speed\n");
      tmp.append("  .word $").append(ShortToExe(inB[0x14]+inB[0x15]*256)).append("         ; speed\n");
   
      addString(tmp, 0x16, 0x36);
      addString(tmp, 0x36, 0x56);
      addString(tmp, 0x56, 0x76);
      
      // test if version > 1
      if (inB[0x07]>0x76) {
        tmp.append("  .word $").append(ShortToExe(inB[0x76]+inB[0x77]*256)).append("         ; word flag\n");  
        tmp.append("  .word $").append(ShortToExe(inB[0x78]+inB[0x79]*256)).append("         ; start and page length\n");  
        tmp.append("  .word $").append(ShortToExe(inB[0x7A]+inB[0x7B]*256)).append("         ; second and third SID address \n");     
      }
      tmp.append("\n");
      if (psidLAddr==0) {
        tmp.append("                      ; read load address\n");  
        tmp.append("  .byte <$").append(ShortToExe(inB[0x7C]+inB[0x7D]*256)).append("\n");
        tmp.append("  .byte >$").append(ShortToExe(inB[0x7C]+inB[0x7D]*256)).append("\n");
        psidLAddr=inB[0x7C]+inB[0x7D]*256;  // modify this value as used for org starting
      }
      tmp.append("\n");
      tmp.append("  .org $").append(ShortToExe(psidLAddr)).append("\n\n");
      
      tmp.append(sid.csdasm(inB, sidPos, inB.length, sidPC));
      source=tmp.toString();
    } else {
        sid.upperCase=option.opcodeUpperCasePreview;
        tmp.append(fileType.getDescription(inB));
        tmp.append("\n");
        tmp.append(sid.cdasm(inB, sidPos, inB.length, sidPC));
        disassembly=tmp.toString(); 
      }     
  }
  
  /**
   * Add string with 0 teminate to the given buffer as source
   * 
   * @param tmp the buffer to use 
   * @param start start address
   * @param end  end address
   */
  private void addString(StringBuilder tmp, int start, int end) {
    boolean exit=false;
    tmp.append("  .byte \"");
    for (int i=start; i<end; i++) {
      if (inB[i]==0) {
        if (exit) tmp.append(",0");
        else {
          tmp.append("\",0");
          exit=true;
        }
      } else tmp.append((char)inB[i]);
    }
    tmp.append("\n");
  }
  
  /**
   * Disassembly a PRG file
   * 
   * @param asSource true if output should be as a source file
   * @param targetType the target machine type
   */
  private void disassemlyPRG(boolean asSource, TargetType targetType) {
    M6510Dasm prg;
      
    switch (targetType) {
      case C64:
        prg=new C64Dasm();  
        ((C64Dasm)prg).language=option.commentLanguage;        
        ((C64Dasm)prg).commentZeroPage=option.commentC64ZeroPage;
        ((C64Dasm)prg).commentStackArea=option.commentC64StackArea;
        ((C64Dasm)prg).comment200Area=option.commentC64_200Area;
        ((C64Dasm)prg).comment300Area=option.commentC64_300Area;        
        ((C64Dasm)prg).commentScreenArea=option.commentC64ScreenArea;
        ((C64Dasm)prg).commentBasicFreeArea=option.commentC64BasicFreeArea;
        ((C64Dasm)prg).commentBasicRom=option.commentC64BasicRom;
        ((C64Dasm)prg).commentFreeRam=option.commentC64FreeRam;
        ((C64Dasm)prg).commentVicII=option.commentC64VicII;
        ((C64Dasm)prg).commentSid=option.commentC64Sid;
        ((C64Dasm)prg).commentColorArea=option.commentC64ColorArea;
        ((C64Dasm)prg).commentCia1=option.commentC64Cia1;
        ((C64Dasm)prg).commentCia2=option.commentC64Cia2;
        break;  
      case C1541:
        prg=new C1541Dasm();   
        ((C1541Dasm)prg).language=option.commentLanguage; 
        ((C1541Dasm)prg).commentC1541ZeroPage=option.commentC1541ZeroPage;
        ((C1541Dasm)prg).commentC1541StackArea=option.commentC64StackArea;
        ((C1541Dasm)prg).commentC1541_200Area=option.commentC1541_200Area;
        ((C1541Dasm)prg).commentC1541Buffer0=option.commentC1541Buffer0;
        ((C1541Dasm)prg).commentC1541Buffer1=option.commentC1541Buffer1;
        ((C1541Dasm)prg).commentC1541Buffer2=option.commentC1541Buffer2;
        ((C1541Dasm)prg).commentC1541Buffer3=option.commentC1541Buffer3;
        ((C1541Dasm)prg).commentC1541Buffer4=option.commentC1541Buffer4;
        ((C1541Dasm)prg).commentC1541Via1=option.commentC1541Via1;
        ((C1541Dasm)prg).commentC1541Via2=option.commentC1541Via2;
        ((C1541Dasm)prg).commentC1541Kernal=option.commentC1541Kernal;        
        break;
      case C128:
        prg=new C128Dasm();  
        ((C128Dasm)prg).language=option.commentLanguage;
        break;
      case VIC20:
        prg=new CVic20Dasm(); 
        ((CVic20Dasm)prg).language=option.commentLanguage;        
        break;
      case PLUS4:
        prg=new CPlus4Dasm();  
        ((CPlus4Dasm)prg).language=option.commentLanguage;    
        ((CPlus4Dasm)prg).commentPlus4ZeroPage=option.commentPlus4ZeroPage;
        ((CPlus4Dasm)prg).commentPlus4StackArea=option.commentPlus4StackArea;
        ((CPlus4Dasm)prg).commentPlus4_200Area=option.commentPlus4_200Area;
        ((CPlus4Dasm)prg).commentPlus4_300Area=option.commentPlus4_300Area;
        ((CPlus4Dasm)prg).commentPlus4_400Area=option.commentPlus4_400Area;
        ((CPlus4Dasm)prg).commentPlus4_500Area=option.commentPlus4_500Area;
        ((CPlus4Dasm)prg).commentPlus4_600Area=option.commentPlus4_600Area;
        ((CPlus4Dasm)prg).commentPlus4_700Area=option.commentPlus4_700Area;
        ((CPlus4Dasm)prg).commentPlus4ColorArea=option.commentPlus4ColorArea;
        ((CPlus4Dasm)prg).commentPlus4VideoArea=option.commentPlus4VideoArea;
        ((CPlus4Dasm)prg).commentPlus4BasicRamP=option.commentPlus4BasicRamP;
        ((CPlus4Dasm)prg).commentPlus4BasicRamN=option.commentPlus4BasicRamN; 
        ((CPlus4Dasm)prg).commentPlus4Luminance=option.commentPlus4Luminance;
        ((CPlus4Dasm)prg).commentPlus4ColorBitmap=option.commentPlus4ColorBitmap;
        ((CPlus4Dasm)prg).commentPlus4GraphicData=option.commentPlus4GraphicData;
        ((CPlus4Dasm)prg).commentPlus4BasicRom=option.commentPlus4BasicRom;
        ((CPlus4Dasm)prg).commentPlus4BasicExt=option.commentPlus4BasicExt;
        ((CPlus4Dasm)prg).commentPlus4Caracter=option.commentPlus4Caracter;
        ((CPlus4Dasm)prg).commentPlus4Acia=option.commentPlus4Acia;
        ((CPlus4Dasm)prg).commentPlus4_6529B_1=option.commentPlus4_6529B_1;
        ((CPlus4Dasm)prg).commentPlus4_6529B_2=option.commentPlus4_6529B_2;
        ((CPlus4Dasm)prg).commentPlus4Ted=option.commentPlus4Ted;
        ((CPlus4Dasm)prg).commentPlus4Kernal=option.commentPlus4Kernal;  
        break;
      default:  
        prg=new M6510Dasm();
    }      
    
    prg.setMemory(memory);
    prg.setOption(option);
    int start=Unsigned.done(inB[0])+Unsigned.done(inB[1])*256;
     
    // calculate start/end address
    startAddress=start;    
    startBuffer=2;
    endAddress=inB.length-1-startBuffer+startAddress;
    endBuffer=inB.length-1;
    
    markInside(startAddress, endAddress, 2);
     
    // search for SID frequency table
    SidFreq.instance.identifyFreq(inB, memory, startBuffer, inB.length, start-startBuffer,
            option.sidFreqLoLabel, option.sidFreqHiLabel);
     
    StringBuilder tmp=new StringBuilder();
    
    if (asSource) {
      prg.upperCase=option.opcodeUpperCaseSource;  
      tmp.append("  processor 6502\n\n");

      tmp.append("  .org $").append(ShortToExe(start-2)).append("\n\n");
      tmp.append("  .byte ").append(Unsigned.done(inB[0])).append("\n");
      tmp.append("  .byte ").append(Unsigned.done(inB[1])).append("\n");
      tmp.append("\n");
      tmp.append("  .org $").append(ShortToExe(start)).append("\n\n");
      
      tmp.append(prg.csdasm(inB, 2, inB.length, start));
      source=tmp.toString();
    } else {    
        prg.upperCase=option.opcodeUpperCasePreview;
        tmp.append(fileType.getDescription(inB));
        tmp.append("\n");
        tmp.append(prg.cdasm(inB, 2, inB.length, start));
        disassembly=tmp.toString();
      }       
  }    
  
  /**
   * Disassembly a MPR file
   * 
   * @param asSource true if output should be as a source file
   * @param targetType the target machine type
   */
  private void disassemlyMPR(boolean asSource, TargetType targetType) {
    M6510Dasm prg;
      
    switch (targetType) {
      case C64:
        prg=new C64Dasm();  
        ((C64Dasm)prg).language=option.commentLanguage;
        ((C64Dasm)prg).commentZeroPage=option.commentC64ZeroPage;
        ((C64Dasm)prg).commentStackArea=option.commentC64StackArea;
        ((C64Dasm)prg).comment200Area=option.commentC64_200Area;
        ((C64Dasm)prg).comment300Area=option.commentC64_300Area;        
        ((C64Dasm)prg).commentScreenArea=option.commentC64ScreenArea;
        ((C64Dasm)prg).commentBasicFreeArea=option.commentC64BasicFreeArea;
        ((C64Dasm)prg).commentBasicRom=option.commentC64BasicRom;
        ((C64Dasm)prg).commentFreeRam=option.commentC64FreeRam;
        ((C64Dasm)prg).commentVicII=option.commentC64VicII;
        ((C64Dasm)prg).commentSid=option.commentC64Sid;
        ((C64Dasm)prg).commentColorArea=option.commentC64ColorArea;
        ((C64Dasm)prg).commentCia1=option.commentC64Cia1;
        ((C64Dasm)prg).commentCia2=option.commentC64Cia2;        
        break;  
      case C1541:
        prg=new C1541Dasm();   
        ((C1541Dasm)prg).language=option.commentLanguage;      
        ((C1541Dasm)prg).commentC1541ZeroPage=option.commentC1541ZeroPage;
        ((C1541Dasm)prg).commentC1541StackArea=option.commentC64StackArea;
        ((C1541Dasm)prg).commentC1541_200Area=option.commentC1541_200Area;
        ((C1541Dasm)prg).commentC1541Buffer0=option.commentC1541Buffer0;
        ((C1541Dasm)prg).commentC1541Buffer1=option.commentC1541Buffer1;
        ((C1541Dasm)prg).commentC1541Buffer2=option.commentC1541Buffer2;
        ((C1541Dasm)prg).commentC1541Buffer3=option.commentC1541Buffer3;
        ((C1541Dasm)prg).commentC1541Buffer4=option.commentC1541Buffer4;
        ((C1541Dasm)prg).commentC1541Via1=option.commentC1541Via1;
        ((C1541Dasm)prg).commentC1541Via2=option.commentC1541Via2;
        ((C1541Dasm)prg).commentC1541Kernal=option.commentC1541Kernal;
        break;
      case C128:
        prg=new C128Dasm();  
        ((C128Dasm)prg).language=option.commentLanguage;
        break;
      case VIC20:
        prg=new CVic20Dasm(); 
        ((CVic20Dasm)prg).language=option.commentLanguage;        
        break;
      case PLUS4:
        prg=new CPlus4Dasm();  
        ((CPlus4Dasm)prg).language=option.commentLanguage; 
        ((CPlus4Dasm)prg).language=option.commentLanguage;    
        ((CPlus4Dasm)prg).commentPlus4ZeroPage=option.commentPlus4ZeroPage;
        ((CPlus4Dasm)prg).commentPlus4StackArea=option.commentPlus4StackArea;
        ((CPlus4Dasm)prg).commentPlus4_200Area=option.commentPlus4_200Area;
        ((CPlus4Dasm)prg).commentPlus4_300Area=option.commentPlus4_300Area;
        ((CPlus4Dasm)prg).commentPlus4_400Area=option.commentPlus4_400Area;
        ((CPlus4Dasm)prg).commentPlus4_500Area=option.commentPlus4_500Area;
        ((CPlus4Dasm)prg).commentPlus4_600Area=option.commentPlus4_600Area;
        ((CPlus4Dasm)prg).commentPlus4_700Area=option.commentPlus4_700Area;
        ((CPlus4Dasm)prg).commentPlus4ColorArea=option.commentPlus4ColorArea;
        ((CPlus4Dasm)prg).commentPlus4VideoArea=option.commentPlus4VideoArea;
        ((CPlus4Dasm)prg).commentPlus4BasicRamP=option.commentPlus4BasicRamP;
        ((CPlus4Dasm)prg).commentPlus4BasicRamN=option.commentPlus4BasicRamN; 
        ((CPlus4Dasm)prg).commentPlus4Luminance=option.commentPlus4Luminance;
        ((CPlus4Dasm)prg).commentPlus4ColorBitmap=option.commentPlus4ColorBitmap;
        ((CPlus4Dasm)prg).commentPlus4GraphicData=option.commentPlus4GraphicData;
        ((CPlus4Dasm)prg).commentPlus4BasicRom=option.commentPlus4BasicRom;
        ((CPlus4Dasm)prg).commentPlus4BasicExt=option.commentPlus4BasicExt;
        ((CPlus4Dasm)prg).commentPlus4Caracter=option.commentPlus4Caracter;
        ((CPlus4Dasm)prg).commentPlus4Acia=option.commentPlus4Acia;
        ((CPlus4Dasm)prg).commentPlus4_6529B_1=option.commentPlus4_6529B_1;
        ((CPlus4Dasm)prg).commentPlus4_6529B_2=option.commentPlus4_6529B_2;
        ((CPlus4Dasm)prg).commentPlus4Ted=option.commentPlus4Ted;
        ((CPlus4Dasm)prg).commentPlus4Kernal=option.commentPlus4Kernal;        
        break;
      default:  
        prg=new M6510Dasm();
    }
       
    prg.setMemory(memory);
    prg.setOption(option);
    
    if (mpr==null) return;
    
    StringBuilder tmp=new StringBuilder();
    
    byte[] inB;
    
    boolean first=true;
    
    if (asSource) {
      prg.upperCase=option.opcodeUpperCaseSource;  
      tmp.append("  processor 6502\n\n");
    } else {    
        prg.upperCase=option.opcodeUpperCasePreview;
        
        tmp.append(fileType.getDescription(this.inB));
        tmp.append("\n");        
      }
    
    // sort by asc memory address
     Collections.sort(mpr.blocks, new Comparator<byte[]>() {
        @Override
        public int compare(byte[] block2, byte[] block1)
        {

            return  (Unsigned.done(block2[0])+Unsigned.done(block2[1])*256)-
                    (Unsigned.done(block1[0])+Unsigned.done(block1[1])*256);
        }
     });
     
     startMPR=new int[mpr.block];
     endMPR=new int[mpr.block];
    
    Iterator<byte[]> iter=mpr.blocks.iterator();
    int i=0;
    while (iter.hasNext()) {
      inB=iter.next();
      
      int start=Unsigned.done(inB[0])+Unsigned.done(inB[1])*256;
 
      // calculate start/end address
      startAddress=start;    
      startBuffer=2;
      endAddress=inB.length-1-startBuffer+startAddress;
      endBuffer=inB.length-1;
      
      startMPR[i]=startAddress;
      endMPR[i]=endAddress;
      i++;
      
      markInside(inB, startAddress, endAddress, 2);
     
      // search for SID frequency table
      SidFreq.instance.identifyFreq(inB, memory, startBuffer, inB.length, start-startBuffer,
             option.sidFreqLoLabel, option.sidFreqHiLabel);
      

      if (asSource) {
        if (first) {
          tmp.append("  .org $").append(ShortToExe(start-2)).append("\n\n");        
          tmp.append("  .byte ").append(Unsigned.done(inB[0])).append("\n");
          tmp.append("  .byte ").append(Unsigned.done(inB[1])).append("\n");
          tmp.append("\n");
          first=false;
        }
        tmp.append("  .org $").append(ShortToExe(start)).append("\n\n");
        tmp.append(prg.csdasm(inB, 2, inB.length, start));
      } else {    
          tmp.append(prg.cdasm(inB, 2, inB.length, start));
        }       
    }
    
    if (asSource) source=tmp.toString();
    else disassembly=tmp.toString();
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
   * Mark the memory as inside
   * 
   * @param start the start of internal
   * @param end the end of internal
   * @param offset in buffer of start position
   */
  private void markInside(int start, int end, int offset) {
    for (int i=start; i<=end; i++) {
      memory[i].isInside=true;  
      memory[i].copy=inB[i-start+offset];
    }  
  }
  
  /**
   * Mark the memory as inside
   * 
   * @param inB the buffer to use
   * @param start the start of internal
   * @param end the end of internal
   * @param offset in buffer of start position
   */
  private void markInside(byte[] inB, int start, int end, int offset) {
    for (int i=start; i<=end; i++) {
      memory[i].isInside=true;  
      memory[i].copy=inB[i-start+offset];
    }  
  }  
}
