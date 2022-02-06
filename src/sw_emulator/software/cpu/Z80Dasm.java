/**
 * @(#)Z80Dasm.java 2022/02/03
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

import sw_emulator.math.Unsigned;
import sw_emulator.software.MemoryDasm;

/**
 * Disasseble the Z80 code instructions
 * This class implements the </code>disassembler</code> interface, so it must
 * disassemble one instruction and comment it.
 *
 * @author ice
 */
public class Z80Dasm extends CpuDasm implements disassembler {
    
  // extra table  
  public static final byte T_CB =-1;
  public static final byte T_DD =-2;
  public static final byte T_ED =-3;
  public static final byte T_FD =-4;  
    
   // legal instruction
  public static final byte M_ADC =0; 
  public static final byte M_ADD =1; 
  public static final byte M_AND =2; 
  public static final byte M_BIT =3;
  public static final byte M_CALL=4;
  public static final byte M_CCF =5;
  public static final byte M_CP  =6;
  public static final byte M_CPD =7;
  public static final byte M_CPDR=8;
  public static final byte M_CPI =9;
  public static final byte M_CPIR=10;
  public static final byte M_CPL =11;
  public static final byte M_DAA =12;
  public static final byte M_DEC =13;
  public static final byte M_DI  =14;
  public static final byte M_DJNZ=15;
  public static final byte M_EI  =16;
  public static final byte M_EX  =17;
  public static final byte M_EXX =18;
  public static final byte M_HALT=19;
  public static final byte M_IM  =20;
  public static final byte M_IN  =21;
  public static final byte M_INC =22;
  public static final byte M_IND =23;
  public static final byte M_INDR=24;
  public static final byte M_INI =25;
  public static final byte M_INIR=26;
  public static final byte M_JP  =27;
  public static final byte M_JR  =28;
  public static final byte M_LD  =29;
  public static final byte M_LDD =30;
  public static final byte M_LDDR=31;
  public static final byte M_LDI =32;
  public static final byte M_LDIR=33;
  public static final byte M_NEG =34;
  public static final byte M_NOP =35;
  public static final byte M_OR  =36;
  public static final byte M_OTDR=37;
  public static final byte M_OTIR=38;
  public static final byte M_OUT =39;
  public static final byte M_OUTD=40;
  public static final byte M_OUTI=41;
  public static final byte M_POP =42;
  public static final byte M_PUSH=43;
  public static final byte M_RES =44;
  public static final byte M_RET =45;
  public static final byte M_RETI=46;
  public static final byte M_RETN=47;
  public static final byte M_RL  =48;
  public static final byte M_RLA =49;
  public static final byte M_RLC =50;
  public static final byte M_RLCA=51;
  public static final byte M_RLD =52;
  public static final byte M_RR  =53;
  public static final byte M_RRA =54;
  public static final byte M_RRC =55;
  public static final byte M_RRCA=56;
  public static final byte M_RRD =57;
  public static final byte M_RST =58;
  public static final byte M_SBC =59;
  public static final byte M_SCF =60;
  public static final byte M_SET =61;
  public static final byte M_SLA =62;
  public static final byte M_SLI =63;
  public static final byte M_SRA =64;
  public static final byte M_SRL =65;
  public static final byte M_STOP=66;
  public static final byte M_SUB =67;
  public static final byte M_XOR =68;
  // extra
  public static final byte M_NUL =69;
  public static final byte M_SLL =70;
  
  // addressing mode
  public static final int A_NUL   =0;   // nothing else
  public static final int A_REG_A =1;   // register A
  public static final int A_REG_B =2;   // register B
  public static final int A_REG_C =3;   // register C
  public static final int A_REG_D =4;   // register D
  public static final int A_REG_E =5;   // register E
  public static final int A_REG_H =6;   // register H
  public static final int A_REG_L =7;   // register L
  public static final int A_BC_NN =8;   // BC absolute nn
  public static final int A_DE_NN =9;   // DE absolute nn
  public static final int A_HL_NN =10;  // HL absolute nn
  public static final int A_SP_NN =11;  // SP absolute nn
  public static final int A_IX_NN =12;  // IX absolute nn
  public static final int A_IY_NN =13;  // IY absolute nn
  public static final int A__BC_A =14;  // (BC) indirect A
  public static final int A__DE_A =15;  // (DE) indirect A
  public static final int A__HL_A =16;  // (HL) indirect A
  public static final int A__IXN_A=17;  // (IX+N) indirect A
  public static final int A__IYN_A=18;  // (IY+N) indirect A
  public static final int A__NN_A =19;  // (NN) indirect A
  public static final int A_REG_BC=20;  // registers BC
  public static final int A_REG_DE=21;  // registers DE
  public static final int A_REG_HL=22;  // registers HL
  public static final int A_REG_SP=23;  // registers SP
  public static final int A_A_N   =24;  // A reg with N
  public static final int A_B_N   =25;  // B reg with N
  public static final int A_C_N   =26;  // C reg with N
  public static final int A_D_N   =27;  // D reg with N
  public static final int A_E_N   =28;  // E reg with N
  public static final int A_H_N   =29;  // H reg with N
  public static final int A_L_N   =30;  // L reg with N
  public static final int A_AF_AF =31;  // AF with shadow AF'
  public static final int A_HL_BC =32;  // HL reg BC
  public static final int A_HL_DE =33;  // HL reg DE
  public static final int A_HL_HL =34;  // HL reg HL
  public static final int A_HL_SP =35;  // HL reg SP
  public static final int A_IX_BC =36;  // IX reg BC
  public static final int A_IX_DE =37;  // IX reg DE
  public static final int A_IX_HL =38;  // IX reg HL
  public static final int A_IX_SP =39;  // IX reg SP
  public static final int A_IY_BC =40;  // IY reg BC
  public static final int A_IY_DE =41;  // IY reg DE
  public static final int A_IY_HL =42;  // IY reg HL
  public static final int A_IY_SP =43;  // IY reg SP 
  public static final int A_A__BC =44;  // A indirect (BC)
  public static final int A_A__DE =45;  // A indirect (DE)
  public static final int A_A__HL =46;  // A indirect (HL)
  public static final int A_A__IXN=47;  // A indirect (IX+N)
  public static final int A_A__IYN=48;  // A indirect (IY+N)
  public static final int A_REL   =49;  // relative
  public static final int A_REL_NZ=50;  // relative NZ
  public static final int A_REL_Z =51;  // relative Z
  public static final int A_REL_NC=52;  // relative NC
  public static final int A_REL_C =53;  // relative C  
  public static final int A__NN_BC=54;  // (NN) ind absolute BC
  public static final int A__NN_DE=55;  // (NN) ind absolute DE
  public static final int A__NN_HL=56;  // (NN) absolute HL
  public static final int A__NN_SP=57;  // (NN) ind absolute SP
  public static final int A__NN_IX=58;  // (NN) ind absolute IX
  public static final int A__NN_IY=59;  // (NN) ind absolute IY
  public static final int A_BC__NN=60;  // BC ind absolute (NN)
  public static final int A_DE__NN=61;  // DE ind absolute (NN)
  public static final int A_HL__NN=62;  // HL ind absolute (NN)
  public static final int A_SP__NN=63;  // SP ind absolute (NN)
  public static final int A_IX__NN=64;  // IX ind absolute (NN)
  public static final int A_IY__NN=65;  // IY ind absolute (NN)
  public static final int A__HL   =66;  // ind (HL)
  public static final int A__HL_N =67;  // ind (HL) imm N
  public static final int A_A__NN =68;  // A ind (NN)
  public static final int A_A_A   =69;  // A reg A
  public static final int A_A_B   =70;  // A reg B
  public static final int A_A_C   =71;  // A reg C
  public static final int A_A_D   =72;  // A reg D
  public static final int A_A_E   =73;  // A reg E
  public static final int A_A_H   =74;  // A reg A
  public static final int A_A_L   =75;  // A reg L
  public static final int A_A_I   =76;  // A reg I
  public static final int A_A_R   =77;  // A reg R
  public static final int A_B_A   =78;  // B reg A
  public static final int A_B_B   =79;  // B reg B
  public static final int A_B_C   =80;  // B reg C
  public static final int A_B_D   =81;  // B reg D
  public static final int A_B_E   =82;  // B reg E
  public static final int A_B_H   =83;  // B reg A
  public static final int A_B_L   =84;  // B reg L
  public static final int A_C_A   =85;  // C reg A
  public static final int A_C_B   =86;  // C reg B
  public static final int A_C_C   =87;  // C reg C
  public static final int A_C_D   =88;  // C reg D
  public static final int A_C_E   =89;  // C reg E
  public static final int A_C_H   =90;  // C reg A
  public static final int A_C_L   =91;  // C reg L
  public static final int A_D_A   =92;  // D reg A
  public static final int A_D_B   =93;  // D reg B
  public static final int A_D_C   =94;  // D reg C
  public static final int A_D_D   =95;  // D reg D
  public static final int A_D_E   =96;  // D reg E
  public static final int A_D_H   =97;  // D reg A
  public static final int A_D_L   =98;  // D reg L
  public static final int A_E_A   =99;  // E reg A
  public static final int A_E_B   =100; // E reg B
  public static final int A_E_C   =101; // E reg C
  public static final int A_E_D   =102; // E reg D
  public static final int A_E_E   =103; // E reg E
  public static final int A_E_H   =104; // E reg A
  public static final int A_E_L   =105; // E reg L
  public static final int A_H_A   =106; // H reg A
  public static final int A_H_B   =107; // H reg B
  public static final int A_H_C   =108; // H reg C
  public static final int A_H_D   =109; // H reg D
  public static final int A_H_E   =110; // H reg E
  public static final int A_H_H   =111; // H reg A
  public static final int A_H_L   =112; // H reg L  
  public static final int A_L_A   =113; // L reg A
  public static final int A_L_B   =114; // L reg B
  public static final int A_L_C   =115; // L reg C
  public static final int A_L_D   =116; // L reg D
  public static final int A_L_E   =117; // L reg E
  public static final int A_L_H   =118; // L reg A
  public static final int A_L_L   =119; // L reg L 
  public static final int A_I_A   =120; // I reg A
  public static final int A_R_A   =121; // R reg A
  public static final int A_B__HL =122; // B indirect (HL)
  public static final int A_B__IXN=123; // B indirect (IX+N)
  public static final int A_B__IYN=124; // B indirect (IY+N)
  public static final int A_C__HL =125; // C indirect (HL)
  public static final int A_C__IXN=126; // C indirect (IX+N)
  public static final int A_C__IYN=127; // C indirect (IY+N)  
  public static final int A_D__HL =128; // D indirect (HL)
  public static final int A_D__IXN=129; // D indirect (IX+N)
  public static final int A_D__IYN=130; // D indirect (IY+N)  
  public static final int A_E__HL =131; // E indirect (HL)
  public static final int A_E__IXN=132; // E indirect (IX+N)
  public static final int A_E__IYN=133; // E indirect (IY+N)  
  public static final int A_H__HL =134; // H indirect (HL)
  public static final int A_H__IXN=135; // H indirect (IX+N)
  public static final int A_H__IYN=136; // H indirect (IY+N)
  public static final int A_L__HL =137; // L indirect (HL)
  public static final int A_L__IXN=138; // L indirect (IX+N)
  public static final int A_L__IYN=139; // L indirect (IY+N)
  public static final int A__HL_B =140; // (HL) indirect B
  public static final int A__HL_C =141; // (HL) indirect C
  public static final int A__HL_D =142; // (HL) indirect D  
  public static final int A__HL_E =143; // (HL) indirect E
  public static final int A__HL_H =144; // (HL) indirect H
  public static final int A__HL_I =145; // (HL) indirect I
  public static final int A__HL_L =146; // (HL) indirect L
  public static final int A_00    =147; // 00h
  public static final int A_08    =148; // 08h
  public static final int A_10    =149; // 10h
  public static final int A_18    =150; // 18h
  public static final int A_20    =151; // 20h
  public static final int A_28    =152; // 28h
  public static final int A_30    =153; // 30h
  public static final int A_38    =154; // 38h
  public static final int A_NZ    =155; // NZ cond
  public static final int A_Z     =156; // Z cond
  public static final int A_NC    =157; // NC cond
  public static final int A_C     =158; // C cond
  public static final int A_PO    =159; // PO cond
  public static final int A_P     =160; // P cond
  public static final int A_PE    =161; // PE cond
  public static final int A_M     =162; // PE cond
  public static final int A_N     =163; // immediate N
  public static final int A_NN    =164; // absolute NN
  public static final int A_REG_AF=165; // reg AF
  public static final int A__N_A  =166; // (N) immediate A
  public static final int A_A__N  =167; // A immediate (N) 
  public static final int A_SP_HL =168; // SP reg HL
  public static final int A_DE_HL =169; // DE reg HL
  public static final int A__SP_HL=170; // (SP) ind  HL
  public static final int A_NZ_NN =171; // NZ cond NN
  public static final int A_Z_NN  =172; // Z cond NN
  public static final int A_NC_NN =173; // NC cond NN
  public static final int A_C_NN  =174; // C cond NN
  public static final int A_PO_NN =175; // PO cond NN
  public static final int A_P_NN  =176; // P cond NN
  public static final int A_PE_NN =177; // PE cond NN
  public static final int A_M_NN  =178; // PE cond NN
  public static final int A_A__C  =179; // A reg ind (C)
  public static final int A_B__C  =180; // B reg ind (C)
  public static final int A_C__C  =181; // C reg ind (C)
  public static final int A_D__C  =182; // D reg ind (C)
  public static final int A_E__C  =183; // E reg ind (C)
  public static final int A_H__C  =184; // H reg ind (C)
  public static final int A_L__C  =185; // L reg ind (C)
  public static final int A___C   =186; // ind (C)
  public static final int A__C_A  =187; // ind C reg A 
  public static final int A__C_B  =188; // ind C reg B
  public static final int A__C_C  =189; // ind C reg C
  public static final int A__C_D  =190; // ind C reg D
  public static final int A__C_E  =191; // ind C reg E
  public static final int A__C_H  =192; // ind C reg H
  public static final int A__C_L  =193; // ind C reg L
  public static final int A___C_0 =194; // ind C 0
  public static final int A_0     =195; // 0
  public static final int A_1     =196; // 1
  public static final int A_2     =197; // 2
  public static final int A_0_A   =198; // 0 reg A
  public static final int A_0_B   =199; // 0 reg B
  public static final int A_0_C   =200; // 0 reg C
  public static final int A_0_D   =201; // 0 reg D
  public static final int A_0_E   =202; // 0 reg E
  public static final int A_0_H   =203; // 0 reg H
  public static final int A_0_L   =204; // 0 reg L
  public static final int A_0__HL =205; // 0 ind (HL)
  public static final int A_1_A   =206; // 1 reg A
  public static final int A_1_B   =207; // 1 reg B
  public static final int A_1_C   =208; // 1 reg C
  public static final int A_1_D   =209; // 1 reg D
  public static final int A_1_E   =210; // 1 reg E
  public static final int A_1_H   =211; // 1 reg H
  public static final int A_1_L   =212; // 1 reg L
  public static final int A_1__HL =213; // 1 ind (HL)
  public static final int A_2_A   =214; // 2 reg A
  public static final int A_2_B   =215; // 2 reg B
  public static final int A_2_C   =216; // 2 reg C
  public static final int A_2_D   =217; // 2 reg D
  public static final int A_2_E   =218; // 2 reg E
  public static final int A_2_H   =219; // 2 reg H
  public static final int A_2_L   =220; // 2 reg L
  public static final int A_2__HL =221; // 2 ind (HL)
  public static final int A_3_A   =222; // 3 reg A
  public static final int A_3_B   =223; // 3 reg B
  public static final int A_3_C   =224; // 3 reg C
  public static final int A_3_D   =225; // 3 reg D
  public static final int A_3_E   =226; // 3 reg E
  public static final int A_3_H   =227; // 3 reg H
  public static final int A_3_L   =228; // 3 reg L
  public static final int A_3__HL =229; // 3 ind (HL)
  public static final int A_4_A   =230; // 4 reg A
  public static final int A_4_B   =231; // 4 reg B
  public static final int A_4_C   =232; // 4 reg C
  public static final int A_4_D   =233; // 4 reg D
  public static final int A_4_E   =234; // 4 reg E
  public static final int A_4_H   =235; // 4 reg H
  public static final int A_4_L   =236; // 4 reg L
  public static final int A_4__HL =237; // 4 ind (HL)
  public static final int A_5_A   =238; // 5 reg A
  public static final int A_5_B   =239; // 5 reg B
  public static final int A_5_C   =240; // 5 reg C
  public static final int A_5_D   =241; // 5 reg D
  public static final int A_5_E   =242; // 5 reg E
  public static final int A_5_H   =243; // 5 reg H
  public static final int A_5_L   =244; // 5 reg L
  public static final int A_5__HL =245; // 5 ind (HL)
  public static final int A_6_A   =246; // 6 reg A
  public static final int A_6_B   =247; // 6 reg B
  public static final int A_6_C   =248; // 6 reg C
  public static final int A_6_D   =249; // 6 reg D
  public static final int A_6_E   =250; // 6 reg E
  public static final int A_6_H   =251; // 6 reg H
  public static final int A_6_L   =252; // 6 reg L
  public static final int A_6__HL =253; // 6 ind (HL)
  public static final int A_7_A   =254; // 7 reg A
  public static final int A_7_B   =255; // 7 reg B
  public static final int A_7_C   =256; // 7 reg C
  public static final int A_7_D   =257; // 7 reg D
  public static final int A_7_E   =258; // 7 reg E
  public static final int A_7_H   =259; // 7 reg H
  public static final int A_7_L   =260; // 7 reg L
  public static final int A_7__HL =261; // 7 ind (HL)
  
  /** Contains the mnemonics of instructions */
  public static final String[] mnemonics={
    // legal instruction first:
    "ADC",
    "ADD", 
    "AND", 
    "BIT", 
    "CALL",
    "CCF", 
    "CP",  
    "CPD", 
    "CPDR",
    "CPI", 
    "CPIR",
    "CPL", 
    "DAA", 
    "DEC", 
    "DI",  
    "DJNZ",
    "EI",  
    "EX",  
    "EXX", 
    "HALT",
    "IM",  
    "IN",  
    "INC", 
    "IND", 
    "INDR",
    "INI", 
    "INIR",
    "JP", 
    "JR",  
    "LD", 
    "LDD", 
    "LDDR",
    "LDI", 
    "LDIR",
    "NEG", 
    "NOP", 
    "OR", 
    "OTDR",
    "OTIR",
    "OUT", 
    "OUTD",
    "OUTI",
    "POP", 
    "PUSH",
    "RES", 
    "RET", 
    "RETI",
    "RETN",
    "RL",              
    "RLA", 
    "RLC", 
    "RLCA",
    "RLD", 
    "RR",  
    "RRA", 
    "RRC", 
    "RRCA",
    "RRD", 
    "RST", 
    "SBC", 
    "SCF", 
    "SET", 
    "SLA", 
    "SLI", 
    "SRA", 
    "SRL", 
    "STOP",
    "SUB", 
    "XOR",
    
    "???",
    "SLL"
  };    
  
  
  /** Contains the mnemonics reference for the instruction */
  public static final byte[] tableMnemonics={
    M_NOP,  M_LD,  M_LD,  M_INC, M_INC,  M_DEC, M_LD,  M_RLCA,   // 00
    M_EX,   M_ADD, M_LD,  M_DEC, M_INC,  M_DEC, M_LD,  M_RRCA,
    M_DJNZ, M_LD,  M_LD,  M_INC, M_INC,  M_DEC, M_LD,  M_RLA, 
    M_JR,   M_ADD, M_LD,  M_DEC, M_INC,  M_DEC, M_LD,  M_RRA, 
    M_JR,   M_LD,  M_LD,  M_INC, M_INC,  M_DEC, M_LD,  M_DAA,    // 20
    M_JR,   M_ADD, M_LD,  M_DEC, M_INC,  M_DEC, M_LD,  M_CPL, 
    M_JR,   M_LD,  M_LD,  M_INC, M_INC,  M_DEC, M_LD,  M_SCF,
    M_JR,   M_ADD, M_LD,  M_DEC, M_INC,  M_DEC, M_LD,  M_CCF,
    M_LD,   M_LD,  M_LD,  M_LD,  M_LD,   M_LD,  M_LD,  M_LD,    // 40
    M_LD,   M_LD,  M_LD,  M_LD,  M_LD,   M_LD,  M_LD,  M_LD,
    M_LD,   M_LD,  M_LD,  M_LD,  M_LD,   M_LD,  M_LD,  M_LD,    
    M_LD,   M_LD,  M_LD,  M_LD,  M_LD,   M_LD,  M_LD,  M_LD,
    M_LD,   M_LD,  M_LD,  M_LD,  M_LD,   M_LD,  M_LD,  M_LD,    // 60
    M_LD,   M_LD,  M_LD,  M_LD,  M_LD,   M_LD,  M_LD,  M_LD,
    M_LD,   M_LD,  M_LD,  M_LD,  M_LD,   M_LD,  M_HALT,M_LD,
    M_LD,   M_LD,  M_LD,  M_LD,  M_LD,   M_LD,  M_LD,  M_LD,
    M_ADD,  M_ADD, M_ADD, M_ADD, M_ADD,  M_ADD, M_ADD, M_ADD,   // 80
    M_ADC,  M_ADC, M_ADC, M_ADC, M_ADC,  M_ADC, M_ADC, M_ADC, 
    M_SUB,  M_SUB, M_SUB, M_SUB, M_SUB,  M_SUB, M_SUB, M_SUB, 
    M_SBC,  M_SBC, M_SBC, M_SBC, M_SBC,  M_SBC, M_SBC, M_SBC,
    M_AND,  M_AND, M_AND, M_AND, M_AND,  M_AND, M_AND, M_AND,   // A0
    M_XOR,  M_XOR, M_XOR, M_XOR, M_XOR,  M_XOR, M_XOR, M_XOR, 
    M_OR,   M_OR,  M_OR,  M_OR,  M_OR,   M_OR,  M_OR,  M_OR,
    M_CP,   M_CP,  M_CP,  M_CP,  M_CP,   M_CP,  M_CP,  M_CP,
    M_RET,  M_POP, M_JP,  M_JP,  M_CALL, M_PUSH,M_ADD, M_RST,   // C0
    M_RET,  M_RET, M_JP,  T_CB,  M_CALL, M_CALL,M_ADC, M_RST,
    M_RET,  M_POP, M_JP,  M_OUT, M_CALL, M_PUSH,M_SUB, M_RST,
    M_RET,  M_EXX, M_JP,  M_IN,  M_CALL, T_DD,  M_SBC, M_RST,
    M_RET,  M_POP, M_JP,  M_EX,  M_CALL, M_PUSH,M_AND, M_RST,   // E0
    M_RET,  M_JP,  M_JP,  M_EX,  M_CALL, T_ED,  M_XOR, M_RST,
    M_RET,  M_POP, M_JP,  M_DI,  M_CALL, M_PUSH,M_OR,  M_RST,
    M_RET,  M_LD,  M_JP,  M_EI,  M_CALL, T_FD,  M_CP,  M_RST    
  };
  
  /** Contains the modes for the instruction */
  public static final int[] tableModes={
    A_NUL,   A_BC_NN, A__BC_A, A_REG_BC, A_REG_B, A_REG_B, A_B_N,  A_NUL,  // 00
    A_AF_AF, A_HL_BC, A_A__BC, A_REG_BC, A_REG_C, A_REG_C, A_C_N,  A_NUL,    
    A_REL,   A_DE_NN, A__DE_A, A_REG_DE, A_REG_D, A_REG_D, A_D_N,  A_NUL, 
    A_REL,   A_HL_DE, A_A__DE, A_REG_DE, A_REG_E, A_REG_E, A_E_N,  A_NUL,
    A_REL_NZ,A_HL_NN, A__NN_HL,A_REG_HL, A_REG_H, A_REG_H, A_H_N,  A_NUL,  // 20
    A_REL_Z, A_HL_HL, A_HL__NN,A_REG_HL, A_REG_L, A_REG_L, A_L_N,  A_NUL,
    A_REL_NC,A_SP_NN, A__NN_A, A_REG_SP, A__HL,   A__HL,   A__HL_N,A_NUL, 
    A_REL_C, A_HL_SP, A_A__NN, A_REG_SP, A_REG_A, A_REG_A, A_A_N,  A_NUL,
    A_B_B,   A_B_C,   A_B_D,   A_B_E,    A_B_H,   A_B_L,   A_B__HL, A_B_A, // 40
    A_C_D,   A_C_C,   A_C_D,   A_C_E,    A_C_H,   A_C_L,   A_C__HL, A_C_A,
    A_D_B,   A_D_C,   A_D_D,   A_D_E,    A_D_H,   A_D_L,   A_D__HL, A_D_A,
    A_E_B,   A_E_C,   A_E_D,   A_E_E,    A_E_H,   A_E_L,   A_E__HL, A_E_A,
    A_H_B,   A_H_C,   A_H_D,   A_H_E,    A_H_H,   A_H_L,   A_H__HL, A_H_A, // 60
    A_L_B,   A_L_C,   A_L_D,   A_L_E,    A_L_H,   A_L_L,   A_L__HL, A_L_A,
    A__HL_B, A__HL_C, A__HL_D, A__HL_E,  A__HL_H, A__HL_L, A_NUL,   A__HL_A,
    A_A_B,   A_A_C,   A_A_D,   A_A_E,    A_A_H,   A_A_L,   A_A__HL, A_A_A,
    A_A_B,   A_A_C,   A_A_D,   A_A_E,    A_A_H,   A_A_L,   A_A__HL, A_A_A, // 80    
    A_A_B,   A_A_C,   A_A_D,   A_A_E,    A_A_H,   A_A_L,   A_A__HL, A_A_A,
    A_REG_B, A_REG_C, A_REG_D, A_REG_E,  A_REG_H, A_REG_L, A__HL,   A_REG_A,
    A_A_B,   A_A_C,   A_A_D,   A_A_E,    A_A_H,   A_A_L,   A_A__HL, A_A_A,
    A_REG_B, A_REG_C, A_REG_D, A_REG_E,  A_REG_H, A_REG_L, A__HL,   A_REG_A,//A0
    A_REG_B, A_REG_C, A_REG_D, A_REG_E,  A_REG_H, A_REG_L, A__HL,   A_REG_A,
    A_REG_B, A_REG_C, A_REG_D, A_REG_E,  A_REG_H, A_REG_L, A__HL,   A_REG_A,
    A_REG_B, A_REG_C, A_REG_D, A_REG_E,  A_REG_H, A_REG_L, A__HL,   A_REG_A,
    A_NZ,    A_REG_BC,A_NZ_NN, A_NN,     A_NZ_NN, A_REG_BC,A_A_N,   A_00,  // C0 
    A_Z,     A_NUL,   A_Z_NN,  0,        A_Z_NN,  A_NN,    A_A_N,   A_08,
    A_NC,    A_REG_DE,A_NC_NN, A__N_A,   A_NC_NN, A_REG_DE,A_N,     A_10,
    A_C,     A_NUL,   A_C_NN,  A_A__N,   A_C_NN,  0,       A_A_N,   A_18,
    A_PO,    A_REG_HL,A_PO_NN, A__SP_HL, A_PO_NN, A_REG_HL,A_N,     A_20,   //E0
    A_PE,    A__HL,   A_PE_NN, A_DE_HL,  A_PE_NN, 0,       A_N,     A_28,
    A_P,     A_REG_AF,A_P_NN,  A_NUL,    A_P_NN,  A_REG_AF,A_N,     A_30,
    A_M,     A_SP_HL, A_M_NN,  A_NUL,    A_M_NN,  0,       A_N,     A_38
  };
  
  /** Contains the bytes used for the instruction */
  public static final byte[] tableSize={
    1, 3, 1, 1, 1, 1, 2, 1,     // 00
    1, 1, 1, 1, 1, 1, 2, 1,
    2, 3, 1, 1, 1, 1, 2, 1, 
    2, 1, 1, 1, 1, 1, 2, 1,
    2, 3, 3, 1, 1, 1, 2, 1,     // 20  
    2, 1, 3, 1, 1, 1, 2, 1, 
    2, 3, 3, 1, 1, 1, 2, 1, 
    2, 1, 3, 1, 1, 1, 2, 1,
    1, 1, 1, 1, 1, 1, 1, 1,     // 40
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,    
    1, 1, 1, 1, 1, 1, 1, 1,     // 60
    1, 1, 1, 1, 1, 1, 1, 1, 
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,     // 80
    1, 1, 1, 1, 1, 1, 1, 1, 
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,     // A0
    1, 1, 1, 1, 1, 1, 1, 1,  
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,   
    1, 1, 3, 3, 3, 1, 2, 1,     // C0
    1, 1, 3, 0, 3, 3, 2, 1,
    1, 1, 3, 2, 3, 1, 2, 1,
    1, 1, 3, 2, 3, 0, 2, 1,
    1, 1, 3, 1, 3, 1, 2, 1,     // E0
    1, 1, 3, 1, 3, 0, 2, 1,
    1, 1, 3, 1, 3, 1, 2, 1,
    1, 1, 3, 1, 3, 0, 2, 1
  };
  
  /** Contains the mnemonics reference for the instruction */
  public static final byte[] tableMnemonicsED={
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL,  // 00
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, 
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, 
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, 
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL,  // 20
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, 
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, 
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, 
    M_IN,  M_OUT, M_SBC, M_LD,  M_NEG,M_RETN, M_IM,  M_LD,  // 40
    M_IN,  M_OUT, M_ADC, M_LD,  M_NEG,M_RETI, M_IM,  M_LD,
    M_IN,  M_OUT, M_SBC, M_LD,  M_NEG,M_RETN, M_IM,  M_LD,
    M_IN,  M_OUT, M_ADC, M_LD,  M_NEG,M_RETN, M_IM,  M_LD,
    M_IN,  M_OUT, M_SBC, M_LD,  M_NEG,M_RETN, M_IM,  M_RRD, // 60
    M_IN,  M_OUT, M_ADC, M_LD,  M_NEG,M_RETN, M_IM,  M_RLD,
    M_IN,  M_OUT, M_SBC, M_LD,  M_NEG,M_RETN, M_IM,  M_NUL,    
    M_IN,  M_OUT, M_ADC, M_LD,  M_NEG,M_RETN, M_IM,  M_NUL,
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL,  // 80
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL,
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, 
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, 
    M_LDI, M_CPI, M_INI, M_OUTI,M_NUL,M_NUL, M_NUL, M_NUL,  // A0
    M_LDD, M_CPD, M_IND, M_OUTD,M_NUL,M_NUL, M_NUL, M_NUL,
    M_LDIR,M_CPIR,M_INIR,M_OTIR,M_NUL,M_NUL, M_NUL, M_NUL,
    M_LDDR,M_CPDR,M_INDR,M_OTDR,M_NUL,M_NUL, M_NUL, M_NUL,
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, // C0
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL,
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, 
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL,
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, // E0 
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL,
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, 
    M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL, M_NUL
  };
  
  /** Contains the mnemonics reference for the instruction */
  public static final int[] tableModesED={
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL,  // 00
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL, 
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL, 
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL, 
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL,  // 20
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL, 
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL, 
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL, 
    A_B__C,A__C_A,A_HL_BC,A__NN_BC,A_NUL, A_NUL, A_0,  A_I_A,  // 40
    A_C__C,A__C_C,A_HL_BC,A_BC__NN,A_NUL, A_NUL, A_0,  A_R_A,
    A_D__C,A__C_D,A_HL_DE,A__NN_DE,A_NUL, A_NUL, A_1,  A_A_I,
    A_E__C,A__C_E,A_HL_DE,A_DE__NN,A_NUL, A_NUL, A_2,  A_A_R,
    A_H__C,A__C_H,A_HL_HL,A__NN_HL,A_NUL, A_NUL, A_0,  A_NUL,  // 60
    A_L__C,A__C_L,A_HL_HL,A_HL__NN,A_NUL, A_NUL, A_0,  A_NUL, 
    A___C, A___C_0,A_HL_SP,A__NN_SP,A_NUL,A_NUL, A_1,  A_NUL,
    A_A__C,A__C_A,A_HL_SP,A_SP__NN, A_NUL,A_NUL, A_2,  A_NUL,
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL,  // 80
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL,
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL, 
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL, 
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL,  // A0
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL,
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL,
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL,
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL, // C0
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL,
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL, 
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL,
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL, // E0 
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL,
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL, 
    A_NUL, A_NUL, A_NUL,  A_NUL,   A_NUL, A_NUL, A_NUL, A_NUL
  };
  
  /** Contains the bytes used for the instruction */
  public static final byte[] tableSizeED={
    1, 1, 1, 1, 1, 1, 1, 1,     // 00
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1, 
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,     // 20  
    1, 1, 1, 1, 1, 1, 1, 1, 
    1, 1, 1, 1, 1, 1, 1, 1, 
    1, 1, 1, 1, 1, 1, 1, 1,
    2, 2, 2, 4, 2, 2, 2, 2,     // 40
    2, 2, 2, 4, 2, 2, 2, 2,
    2, 2, 2, 4, 2, 2, 2, 2,
    2, 2, 2, 4, 2, 2, 2, 2,    
    2, 2, 2, 4, 2, 2, 2, 2,     // 60
    2, 2, 2, 4, 2, 2, 2, 2, 
    2, 2, 2, 4, 2, 2, 2, 1,
    2, 2, 2, 4, 2, 2, 2, 1,
    1, 1, 1, 1, 1, 1, 1, 1,     // 80
    1, 1, 1, 1, 1, 1, 1, 1, 
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,
    2, 2, 2, 2, 1, 1, 1, 1,     // A0
    2, 2, 2, 2, 1, 1, 1, 1,  
    2, 2, 2, 2, 1, 1, 1, 1,
    2, 2, 2, 2, 1, 1, 1, 1,   
    1, 1, 1, 1, 1, 1, 1, 1,     // C0
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,     // E0
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1
  };
  
  
  /** Contains the mnemonics reference for the instruction */
  public static final byte[] tableMnemonicsCB={
    M_RLC, M_RLC, M_RLC, M_RLC, M_RLC, M_RLC, M_RLC, M_RLC,   // 00
    M_RRC, M_RRC, M_RRC, M_RRC, M_RRC, M_RRC, M_RRC, M_RRC,
    M_RL,  M_RL,  M_RL,  M_RL,  M_RL,  M_RL,  M_RL,  M_RL,
    M_RR,  M_RR,  M_RR,  M_RR,  M_RR,  M_RR, M_RR,  M_RR,
    M_SLA, M_SLA, M_SLA, M_SLA, M_SLA, M_SLA, M_SLA, M_SLA,   // 20
    M_SRA, M_SRA, M_SRA, M_SRA, M_SRA, M_SRA, M_SRA, M_SRA,
    M_SLL, M_SLL, M_SLL, M_SLL, M_SLL, M_SLL, M_SLL, M_SLL,
    M_SRL, M_SRL, M_SRL, M_SRL, M_SRL, M_SRL, M_SRL, M_SRL, 
    M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT,    // 40
    M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT,    
    M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT,
    M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT,
    M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT,    // 60
    M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT,
    M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT,
    M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT, M_BIT,
    M_RES, M_RES, M_RES, M_RES, M_RES, M_RES, M_RES, M_RES,    // 80
    M_RES, M_RES, M_RES, M_RES, M_RES, M_RES, M_RES, M_RES,
    M_RES, M_RES, M_RES, M_RES, M_RES, M_RES, M_RES, M_RES,
    M_RES, M_RES, M_RES, M_RES, M_RES, M_RES, M_RES, M_RES,
    M_RES, M_RES, M_RES, M_RES, M_RES, M_RES, M_RES, M_RES,    // A0
    M_RES, M_RES, M_RES, M_RES, M_RES, M_RES, M_RES, M_RES,
    M_RES, M_RES, M_RES, M_RES, M_RES, M_RES, M_RES, M_RES,
    M_RES, M_RES, M_RES, M_RES, M_RES, M_RES, M_RES, M_RES,
    M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, M_SET,    // C0
    M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, 
    M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, 
    M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, 
    M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, M_SET,    // E0
    M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, 
    M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, 
    M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, M_SET, 
  };
  
  /** Contains the mnemonics reference for the instruction */
  public static final int[] tableModesCB={
    A_REG_B, A_REG_C, A_REG_D, A_REG_E, A_REG_H, A_REG_L, A__HL,  A_REG_A, // 00
    A_REG_B, A_REG_C, A_REG_D, A_REG_E, A_REG_H, A_REG_L, A__HL,  A_REG_A,
    A_REG_B, A_REG_C, A_REG_D, A_REG_E, A_REG_H, A_REG_L, A__HL,  A_REG_A,
    A_REG_B, A_REG_C, A_REG_D, A_REG_E, A_REG_H, A_REG_L, A__HL,  A_REG_A,
    A_REG_B, A_REG_C, A_REG_D, A_REG_E, A_REG_H, A_REG_L, A__HL,  A_REG_A, // 20
    A_REG_B, A_REG_C, A_REG_D, A_REG_E, A_REG_H, A_REG_L, A__HL,  A_REG_A,
    A_REG_B, A_REG_C, A_REG_D, A_REG_E, A_REG_H, A_REG_L, A__HL,  A_REG_A,
    A_REG_B, A_REG_C, A_REG_D, A_REG_E, A_REG_H, A_REG_L, A__HL,  A_REG_A,
    A_0_B,   A_0_C,   A_0_D,   A_0_E,   A_0_H,   A_0_L,   A_0__HL,A_0_A,   // 40 
    A_1_B,   A_1_C,   A_1_D,   A_1_E,   A_1_H,   A_1_L,   A_1__HL,A_1_A,
    A_2_B,   A_2_C,   A_2_D,   A_2_E,   A_2_H,   A_2_L,   A_2__HL,A_2_A,
    A_3_B,   A_3_C,   A_3_D,   A_3_E,   A_3_H,   A_3_L,   A_3__HL,A_3_A,
    A_4_B,   A_4_C,   A_4_D,   A_4_E,   A_4_H,   A_4_L,   A_4__HL,A_4_A,   // 60
    A_5_B,   A_5_C,   A_5_D,   A_5_E,   A_5_H,   A_5_L,   A_5__HL,A_5_A,
    A_6_B,   A_6_C,   A_6_D,   A_6_E,   A_6_H,   A_6_L,   A_6__HL,A_6_A,
    A_7_B,   A_7_C,   A_7_D,   A_7_E,   A_7_H,   A_7_L,   A_7__HL,A_7_A,
    A_0_B,   A_0_C,   A_0_D,   A_0_E,   A_0_H,   A_0_L,   A_0__HL,A_0_A,   // 80 
    A_1_B,   A_1_C,   A_1_D,   A_1_E,   A_1_H,   A_1_L,   A_1__HL,A_1_A,
    A_2_B,   A_2_C,   A_2_D,   A_2_E,   A_2_H,   A_2_L,   A_2__HL,A_2_A,
    A_3_B,   A_3_C,   A_3_D,   A_3_E,   A_3_H,   A_3_L,   A_3__HL,A_3_A,
    A_4_B,   A_4_C,   A_4_D,   A_4_E,   A_4_H,   A_4_L,   A_4__HL,A_4_A,   // A0
    A_5_B,   A_5_C,   A_5_D,   A_5_E,   A_5_H,   A_5_L,   A_5__HL,A_5_A,
    A_6_B,   A_6_C,   A_6_D,   A_6_E,   A_6_H,   A_6_L,   A_6__HL,A_6_A,
    A_7_B,   A_7_C,   A_7_D,   A_7_E,   A_7_H,   A_7_L,   A_7__HL,A_7_A,
    A_0_B,   A_0_C,   A_0_D,   A_0_E,   A_0_H,   A_0_L,   A_0__HL,A_0_A,   // C0 
    A_1_B,   A_1_C,   A_1_D,   A_1_E,   A_1_H,   A_1_L,   A_1__HL,A_1_A,
    A_2_B,   A_2_C,   A_2_D,   A_2_E,   A_2_H,   A_2_L,   A_2__HL,A_2_A,
    A_3_B,   A_3_C,   A_3_D,   A_3_E,   A_3_H,   A_3_L,   A_3__HL,A_3_A,
    A_4_B,   A_4_C,   A_4_D,   A_4_E,   A_4_H,   A_4_L,   A_4__HL,A_4_A,   // E0
    A_5_B,   A_5_C,   A_5_D,   A_5_E,   A_5_H,   A_5_L,   A_5__HL,A_5_A,
    A_6_B,   A_6_C,   A_6_D,   A_6_E,   A_6_H,   A_6_L,   A_6__HL,A_6_A,
    A_7_B,   A_7_C,   A_7_D,   A_7_E,   A_7_H,   A_7_L,   A_7__HL,A_7_A,
    
  };
  
  
  /** Contains the bytes used for the instruction */
  public static final byte[] tableSizeCB={
    2, 2, 2, 2, 2, 2, 2, 2,     // 00
    2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 
    2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2,     // 20  
    2, 2, 2, 2, 2, 2, 2, 2, 
    2, 2, 2, 2, 2, 2, 2, 2, 
    2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2,     // 40
    2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2,    
    2, 2, 2, 2, 2, 2, 2, 2,     // 60
    2, 2, 2, 2, 2, 2, 2, 2, 
    2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2,     // 80
    2, 2, 2, 2, 2, 2, 2, 2, 
    2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2,     // A0
    2, 2, 2, 2, 2, 2, 2, 2,  
    2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2,   
    2, 2, 2, 2, 2, 2, 2, 2,     // C0
    2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2,     // E0
    2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2
  };  
  
  @Override
  public String dasm(byte[] buffer, int pos, long pc) {
    String result="";          // result disassemble string
    int steps=0;
    
    int op=Unsigned.done(buffer[pos++]); // instruction opcode

    iType=(int)tableMnemonics[op];   // store the type for creating comment
    
    switch (iType) {
      case T_CB:
        op=Unsigned.done(buffer[pos++]);  
        iType=(int)tableMnemonicsCB[op];  
        aType=tableModesCB[op];
        steps=tableSizeCB[op];
        break;
      case T_DD: 
        iType=M_NUL;
        break;      
      case T_ED:
        op=Unsigned.done(buffer[pos++]);  
        iType=(int)tableMnemonicsED[op];  
        aType=tableModesED[op];
        steps=tableSizeED[op];
        break;
      case T_FD:
        iType=M_NUL;  
        break;
      default:
        aType=tableModes[op];  
        steps=tableSize[op];
        break;
    }        

        
    if (upperCase) result=mnemonics[iType];
    else result=mnemonics[iType].toLowerCase();  
    
    switch (result.length()) {
        case 2:
          result+="   ";
          break;
        case 3: 
          result+="  ";
          break;
        case 4:
          result+=" ";
          break;
    }          
    
    switch (aType) {
      case A_NUL:     // nothing
        break;
      case A_REG_A:    // register A  
        result+=(upperCase? "A": "a");
        break;  
      case A_REG_B:    // register B
        result+=(upperCase? "B": "b");
        break;    
      case A_REG_C:    // register C
        result+=(upperCase? "C": "c");
        break;         
      case A_REG_D:    // register D
        result+=(upperCase? "D": "d");
        break;  
      case A_REG_E:    // register E
        result+=(upperCase? "E": "e");
        break;   
      case A_REG_H:    // register H
        result+=(upperCase? "H": "h");
        break;        
      case A_BC_NN:    // BC absolute nn
        this.pos=pos;  
        result+=getRegXXNN(buffer, (upperCase? "BC": "bc"));
        pos=this.pos;
        break;
      case A_DE_NN:    // DE absolute nn
        this.pos=pos;  
        result+=getRegXXNN(buffer, (upperCase? "DE": "de"));
        pos=this.pos;
        break;    
      case A_HL_NN:    // HL absolute nn
        this.pos=pos;  
        result+=getRegXXNN(buffer, (upperCase? "HL": "hl"));
        pos=this.pos;
        break;        
      case A_SP_NN:    // SP absolute nn
        this.pos=pos;  
        result+=getRegXXNN(buffer, (upperCase? "SP": "sp"));
        pos=this.pos;
        break; 
       case A_IX_NN:    // IX absolute nn
        this.pos=pos;   
        result+=getRegXXNN(buffer, (upperCase? "IX": "ix"));
        pos=this.pos;
        break;
      case A_IY_NN:    // IY absolute nn
        this.pos=pos;  
        result+=getRegXXNN(buffer, (upperCase? "IY": "iy"));
        pos=this.pos;
        break;           
      case A__BC_A:    // (BC) indirect A
        result+=(upperCase? "(BC),A": "(bc),a");
        break;
      case A__DE_A:    // (DE) indirect A
        result+=(upperCase? "(DE),A": "(de),a");
        break;  
      case A__HL_A:    // (HL) indirect A
        result+=(upperCase? "(HL),A": "(hl),a");
        break;     
     case A__HL_B:    // (HL) indirect B
        result+=(upperCase? "(HL),B": "(hl),b");  
        break;
     case A__HL_C:    // (HL) indirect C
        result+=(upperCase? "(HL),C": "(hl),c");  
        break;   
     case A__HL_D:    // (HL) indirect D
        result+=(upperCase? "(HL),D": "(hl),d");  
        break;   
     case A__HL_E:    // (HL) indirect E
        result+=(upperCase? "(HL),E": "(hl),e");  
        break;   
     case A__HL_H:    // (HL) indirect H
        result+=(upperCase? "(HL),H": "(hl),h");  
        break;  
     case A__HL_L:    // (HL) indirect L
        result+=(upperCase? "(HL),L": "(hl),l");  
        break;           
      case A__IXN_A:   // (IX+N) indirect A
        if (pos<buffer.length) addr=Unsigned.done(buffer[pos++]);
        else addr=-1; 
        
        result+=(upperCase? "(IX+)": "(ix+")+getLabelZero(addr)+(upperCase? "),A": "),a"); 
        break;  
      case A__IYN_A:   // (IY+N) indirect A
        if (pos<buffer.length) addr=Unsigned.done(buffer[pos++]);
        else addr=-1; 
        
        result+=(upperCase? "(IY+)": "(iy+")+getLabelZero(addr)+(upperCase? "),A": "),a"); 
        break;  
      case A__NN_A:    // (NN) indirect A  
        if (pos<buffer.length) addr=Unsigned.done(buffer[pos++]);
        else addr=-1;  
        
        result+="("+getLabelZero(addr)+(upperCase? "),A": "),a"); 
        break; 
      case A_REG_BC:   // registers BC
        result+=(upperCase? "BC": "bc");   
        break;   
      case A_REG_DE:   // registers DE
        result+=(upperCase? "DE": "de");   
        break;          
      case A_REG_HL:   // registers HL
        result+=(upperCase? "HL": "hl");   
        break;  
      case A_REG_SP:   // registers SP
        result+=(upperCase? "SP": "sp");   
        break;   
      case A_REG_AF:    // register AF  
        result+=(upperCase? "AF": "af");
        break;   
      case A_A_N:     // A reg with N 
        this.pos=pos;  
        result+=getRegXN(buffer, (upperCase? "A": "a"));
        pos=this.pos;
        break;  
      case A_B_N:     // B reg with N
        this.pos=pos;  
        result+=getRegXN(buffer, (upperCase? "B": "b"));  
        pos=this.pos;
        break;  
      case A_C_N:     // C reg with N
        this.pos=pos;  
        result+=getRegXN(buffer, (upperCase? "C": "c"));
        pos=this.pos;
        break;      
      case A_D_N:     // D reg with N
        this.pos=pos;  
        result+=getRegXN(buffer, (upperCase? "D": "d"));
        pos=this.pos;
        break;        
      case A_E_N:     // E reg with N
        this.pos=pos;  
        result+=getRegXN(buffer, (upperCase? "E": "e"));
        pos=this.pos;
        break;   
      case A_H_N:     // H reg with N
        this.pos=pos;  
        result+=getRegXN(buffer, (upperCase? "H": "h"));
        pos=this.pos;
        break;   
      case A_L_N:     // L reg with N
        this.pos=pos;  
        result+=getRegXN(buffer, (upperCase? "L": "l"));
        pos=this.pos;
        break;   
      case A_AF_AF: 
        result+=(upperCase? "AF,AF'": "af,af'");  
        break; 
      case A_REL:       // relative  
        if (pos<buffer.length) addr=pc+buffer[pos++]+2;
        else addr=-1; 
        
        result+=getLabel(addr);
        setLabel(addr);  
        break;
      case A_REL_NZ:    // relative NZ  
        if (pos<buffer.length) addr=pc+buffer[pos++]+2;
        else addr=-1; 
        
        result+=(upperCase? "NZ,": "nz,")+getLabel(addr);
        setLabel(addr);          
        break;
      case A_REL_Z:    // relative Z  
        if (pos<buffer.length) addr=pc+buffer[pos++]+2;
        else addr=-1; 
        
        result+=(upperCase? "Z,": "z,")+getLabel(addr);
        setLabel(addr);          
        break;
      case A_REL_NC:    // relative NC  
        if (pos<buffer.length) addr=pc+buffer[pos++]+2;
        else addr=-1; 
        
        result+=(upperCase? "NC,": "nc,")+getLabel(addr);
        setLabel(addr);          
        break;    
      case A_REL_C:    // relative C  
        if (pos<buffer.length) addr=pc+buffer[pos++]+2;
        else addr=-1; 
        
        result+=(upperCase? "C,": "c,")+getLabel(addr);
        setLabel(addr);          
        break;  
      case A_HL_BC:     // HL reg BC
        result+=(upperCase? "HL,BC": "hl,bc");
        break;  
      case A_HL_DE:     // HL reg DE
        result+=(upperCase? "HL,DE": "hl,de");
        break;  
      case A_HL_HL:     // HL reg HL
        result+=(upperCase? "HL,HL": "hl,hl");
        break;  
      case A_HL_SP:     // HL reg SP
        result+=(upperCase? "HL,SP": "hl,sp");
        break;    
      case A_IX_BC:     // IX reg BC
        result+=(upperCase? "IX,BC": "ix,bc");
        break;  
      case A_IX_DE:     // IX reg DE
        result+=(upperCase? "IX,DE": "ix,de");
        break;  
      case A_IX_HL:     // IX reg HL
        result+=(upperCase? "IX,HL": "ix,hl");
        break;  
      case A_IX_SP:     // IX reg SP
        result+=(upperCase? "IX,SP": "ix,sp");
        break;        
      case A_IY_BC:     // IY reg BC
        result+=(upperCase? "IY,BC": "iy,bc");
        break;  
      case A_IY_DE:     // IY reg DE
        result+=(upperCase? "IY,DE": "iy,de");
        break;  
      case A_IY_HL:     // IY reg HL
        result+=(upperCase? "IY,HL": "iy,hl");
        break;  
      case A_IY_SP:     // IY reg SP
        result+=(upperCase? "IY,SP": "iy,sp");
        break;
      case A_SP_HL:     // SP reg HL 
        result+=(upperCase? "SP,HL": "sp,hl");
        break;   
      case A_DE_HL:     // DE reg HL 
        result+=(upperCase? "DE,HL": "de,hl");
        break;   
      case A__SP_HL:    // (SP) ind  HL  
        result+=(upperCase? "(SP),HL": "(sp),hl");  
        break;  
      case A_A__BC:     // A indirect (BC)  
        result+=(upperCase? "A,(BC)": "a,(bc)");  
        break;  
      case A_A__DE:     // A indirect (DE)  
        result+=(upperCase? "A,(DE)": "a,(de)");  
        break;        
      case A_A__HL:     // A indirect (HL)  
        result+=(upperCase? "A,(HL)": "a,(hl)");  
        break;        
      case A_A__IXN:    // A indirect (IX+N)  
        this.pos=pos;  
        result+=getRefXIndXXN(buffer, (upperCase? "A": "a"), (upperCase? "IX": "ix"));   
        pos=this.pos;
        break;  
      case A_A__IYN:    // A indirect (IY+N)
        this.pos=pos;  
        result+=getRefXIndXXN(buffer, (upperCase? "A": "a"), (upperCase? "IY": "iy"));  
        pos=this.pos;
        break;         
      case A_B__HL:    // B indirect (HL)  
        result+=(upperCase? "B,(HL)": "b,(hl)");  
        break;        
      case A_B__IXN:    // B indirect (IX+N) 
        this.pos=pos;  
        result+=getRefXIndXXN(buffer, (upperCase? "B": "b"), (upperCase? "IX": "ix")); 
        pos=this.pos;
        break;  
      case A_B__IYN:    // B indirect (IY+N)  
        this.pos=pos;  
        result+=getRefXIndXXN(buffer, (upperCase? "B": "b"), (upperCase? "IY": "iy"));  
        pos=this.pos;
        break; 
      case A_C__HL:     // C indirect (HL)  
        result+=(upperCase? "C,(HL)": "c,(hl)");  
        break;        
      case A_C__IXN:    // C indirect (IX+N)  
        this.pos=pos;  
        result+=getRefXIndXXN(buffer, (upperCase? "C": "c"), (upperCase? "IX": "ix")); 
        pos=this.pos;
        break;  
      case A_C__IYN:    // C indirect (IY+N) 
        this.pos=pos;  
        result+=getRefXIndXXN(buffer, (upperCase? "C": "c"), (upperCase? "IY": "iy")); 
        pos=this.pos;
        break; 
      case A_D__HL:     // D indirect (HL)  
        result+=(upperCase? "D,(HL)": "d,(hl)");  
        break;        
      case A_D__IXN:    // D indirect (IX+N)  
        this.pos=pos;  
        result+=getRefXIndXXN(buffer, (upperCase? "D": "d"), (upperCase? "IX": "ix"));  
        pos=this.pos;
        break;  
      case A_D__IYN:    // D indirect (IY+N) 
        this.pos=pos;  
        result+=getRefXIndXXN(buffer, (upperCase? "D": "d"), (upperCase? "IY": "iy")); 
        pos=this.pos;
        break; 
      case A_E__HL:     // E indirect (HL)  
        result+=(upperCase? "E,(HL)": "e,(hl)");  
        break;        
      case A_E__IXN:    // E indirect (IX+N)  
        this.pos=pos;  
        result+=getRefXIndXXN(buffer, (upperCase? "E": "e"), (upperCase? "IX": "ix")); 
        pos=this.pos;
        break;  
      case A_E__IYN:    // E indirect (IY+N)  
        this.pos=pos;  
        result+=getRefXIndXXN(buffer, (upperCase? "E": "e"), (upperCase? "IY": "iy")); 
        pos=this.pos;
        break;        
      case A_H__HL:     // H indirect (HL)  
        result+=(upperCase? "H,(HL)": "h,(hl)");  
        break;        
      case A_H__IXN:    // H indirect (IX+N) 
        this.pos=pos;  
        result+=getRefXIndXXN(buffer, (upperCase? "H": "h"), (upperCase? "IX": "ix")); 
        pos=this.pos;
        break;  
      case A_H__IYN:    // H indirect (IY+N)  
        this.pos=pos;  
        result+=getRefXIndXXN(buffer, (upperCase? "H": "h"), (upperCase? "IY": "iy"));  
        pos=this.pos;
        break;       
      case A_L__HL:     // L indirect (HL)  
        result+=(upperCase? "L,(HL)": "l,(hl)");  
        break;        
      case A_L__IXN:    // L indirect (IX+N)  
        this.pos=pos;  
        result+=getRefXIndXXN(buffer, (upperCase? "L": "l"), (upperCase? "IX": "ix"));  
        pos=this.pos;
        break;  
      case A_L__IYN:    // L indirect (IY+N)
        this.pos=pos;   
        result+=getRefXIndXXN(buffer, (upperCase? "L": "l"), (upperCase? "IY": "iy"));
        pos=this.pos;
        break;                   
      case A__NN_BC:   // (NN) ind absolute BC 
        this.pos=pos;  
        result+=getNNregX(buffer, (upperCase? "BC": "bc"));
        pos=this.pos;
        break;   
      case A__NN_DE:   // (NN) ind absolute DE 
        this.pos=pos;  
        result+=getNNregX(buffer, (upperCase? "DE": "de"));
        pos=this.pos;
        break;     
      case A__NN_HL:   // (NN) ind absolute HL 
        this.pos=pos;  
        result+=getNNregX(buffer, (upperCase? "HL": "hl"));
        pos=this.pos;
        break;          
      case A__NN_SP:   // (NN) ind absolute SP
        this.pos=pos;  
        result+=getNNregX(buffer, (upperCase? "SP": "sp"));
        pos=this.pos;
        break;    
      case A__NN_IX:   // (NN) absolute IX
        this.pos=pos;  
        result+=getNNregX(buffer, (upperCase? "IX": "ix"));
        pos=this.pos;
        break;          
      case A_BC__NN:   // BC ind absolute (NN) 
        this.pos=pos;  
        result+=getRegXXIndNN(buffer, (upperCase? "BC": "bc"));  
        pos=this.pos;
        break; 
      case A_DE__NN:   // DE ind absolute (NN) 
        this.pos=pos;  
        result+=getRegXXIndNN(buffer, (upperCase? "DE": "de")); 
        pos=this.pos;
        break;   
      case A_HL__NN:   // HL ind absolute (NN) 
        this.pos=pos;  
        result+=getRegXXIndNN(buffer, (upperCase? "HL": "hl")); 
        pos=this.pos;
        break;           
      case A_SP__NN:   // SP ind absolute (NN) 
        this.pos=pos;  
        result+=getRegXXIndNN(buffer, (upperCase? "SP": "sp")); 
        pos=this.pos;
        break;     
      case A_IX__NN:   // IX ind absolute (NN) 
        this.pos=pos;  
        result+=getRegXXIndNN(buffer, (upperCase? "IX": "ix")); 
        pos=this.pos;
        break;        
      case A_IY__NN:   // IY ind absolute (NN) 
        this.pos=pos;  
        result+=getRegXXIndNN(buffer, (upperCase? "IY": "iy")); 
        pos=this.pos;
        break;          
      case A__HL:     // ind (HL)  
        result+=(upperCase? "(HL)": "(hl)");  
        break; 
      case A__HL_N:    // ind (HL) imm N 
        if (pos<buffer.length) value=Unsigned.done(buffer[pos++]);
        else value=0;
        
        result+=(upperCase? "(HL),": "(hl), ")+getLabelImm(pc+1, value);           
        break; 
      case A_A__NN:     // A ind (NN) 
        if (pos<buffer.length-1) addr=((Unsigned.done(buffer[pos+1])<<8) | Unsigned.done(buffer[pos++]));
        else addr=-1;
        pos++;  
        
        result+=(upperCase? "A,(": "a,(")+getLabel(addr)+")";
        
        setLabel(addr);
        setLabelPlus(pc,1);
        setLabelPlus(pc,2);  
        break; 
      case A_A_A:    // A reg A
        result+=(upperCase? "A,A": "a,a");
        break;
      case A_A_B:    // A reg B
        result+=(upperCase? "A,B": "a,b");
        break;  
      case A_A_C:    // A reg C
        result+=(upperCase? "A,C": "a,c");
        break;  
      case A_A_D:    // A reg D
        result+=(upperCase? "A,D": "a,d");
        break;  
      case A_A_E:    // A reg E
        result+=(upperCase? "A,E": "a,e");
        break;  
      case A_A_H:    // A reg H
        result+=(upperCase? "A,H": "a,h");
        break;  
      case A_A_L:    // A reg L
        result+=(upperCase? "A,L": "a,l");
        break;   
      case A_A_I:    // A reg I
        result+=(upperCase? "A,I": "a,i");
        break;  
      case A_A_R:    // A reg R
        result+=(upperCase? "A,R": "a,r");
        break;  
      case A_B_A:    // B reg A
        result+=(upperCase? "B,A": "b,a");
        break;
      case A_B_B:    // B reg B
        result+=(upperCase? "B,B": "b,b");
        break;  
      case A_B_C:    // B reg C
        result+=(upperCase? "B,C": "b,c");
        break;  
      case A_B_D:    // B reg D
        result+=(upperCase? "B,D": "b,d");
        break;  
      case A_B_E:    // B reg E
        result+=(upperCase? "B,E": "b,e");
        break;  
      case A_B_H:    // B reg H
        result+=(upperCase? "B,H": "b,h");
        break;  
      case A_B_L:    // B reg L
        result+=(upperCase? "B,L": "b,l");
        break;   
      case A_C_A:    // C reg A
        result+=(upperCase? "C,A": "c,a");
        break;
      case A_C_B:    // C reg B
        result+=(upperCase? "C,B": "c,b");
        break;  
      case A_C_C:    // C reg C
        result+=(upperCase? "C,C": "c,c");
        break;  
      case A_C_D:    // C reg D
        result+=(upperCase? "C,D": "c,d");
        break;  
      case A_C_E:    // C reg E
        result+=(upperCase? "C,E": "c,e");
        break;  
      case A_C_H:    // C reg H
        result+=(upperCase? "C,H": "c,h");
        break;  
      case A_C_L:    // C reg L
        result+=(upperCase? "C,L": "c,l");
        break;
      case A_D_A:    // C reg A
        result+=(upperCase? "D,A": "d,a");
        break;
      case A_D_B:    // C reg B
        result+=(upperCase? "D,B": "d,b");
        break;  
      case A_D_C:    // C reg C
        result+=(upperCase? "D,C": "d,c");
        break;  
      case A_D_D:    // C reg D
        result+=(upperCase? "D,D": "d,d");
        break;  
      case A_D_E:    // C reg E
        result+=(upperCase? "D,E": "d,e");
        break;  
      case A_D_H:    // C reg H
        result+=(upperCase? "D,H": "d,h");
        break;  
      case A_D_L:    // C reg L
        result+=(upperCase? "D,L": "d,l");
        break;  
      case A_E_A:    // E reg A
        result+=(upperCase? "E,A": "e,a");
        break;
      case A_E_B:    // E reg B
        result+=(upperCase? "E,B": "e,b");
        break;  
      case A_E_C:    // E reg C
        result+=(upperCase? "E,C": "e,c");
        break;  
      case A_E_D:    // E reg D
        result+=(upperCase? "E,D": "e,d");
        break;  
      case A_E_E:    // E reg E
        result+=(upperCase? "E,E": "e,e");
        break;  
      case A_E_H:    // E reg H
        result+=(upperCase? "E,H": "e,h");
        break;  
      case A_E_L:    // E reg L
        result+=(upperCase? "E,L": "e,l");
        break;  
      case A_H_A:    // E reg A
        result+=(upperCase? "H,A": "h,a");
        break;
      case A_H_B:    // H reg B
        result+=(upperCase? "H,B": "h,b");
        break;  
      case A_H_C:    // H reg C
        result+=(upperCase? "H,C": "h,c");
        break;  
      case A_H_D:    // H reg D
        result+=(upperCase? "H,D": "h,d");
        break;  
      case A_H_E:    // H reg E
        result+=(upperCase? "H,E": "h,e");
        break;  
      case A_H_H:    // H reg H
        result+=(upperCase? "H,H": "h,h");
        break;  
      case A_H_L:    // H reg L
        result+=(upperCase? "H,L": "h,l");
        break;   
      case A_L_A:    // L reg A
        result+=(upperCase? "L,A": "l,a");
        break;
      case A_L_B:    // L reg B
        result+=(upperCase? "L,B": "l,b");
        break;  
      case A_L_C:    // L reg C
        result+=(upperCase? "L,C": "l,c");
        break;  
      case A_L_D:    // L reg D
        result+=(upperCase? "L,D": "l,d");
        break;  
      case A_L_E:    // L reg E
        result+=(upperCase? "L,E": "l,e");
        break;  
      case A_L_H:    // L reg H
        result+=(upperCase? "L,H": "l,h");
        break;  
      case A_L_L:    // L reg L
        result+=(upperCase? "L,L": "l,l");
        break; 
      case A_I_A:    // I reg A
        result+=(upperCase? "I,A": "i,a");
        break;
     case A_R_A:    // R reg A
        result+=(upperCase? "R,A": "r,a");
        break;  
     case A_00:     // 00h   
        result+="$00";
        break; 
     case A_08:     // 08h   
        result+="$08";
        break;    
     case A_10:     // 10h   
        result+="$10";
        break;      
     case A_18:     // 18h   
        result+="$18";
        break; 
     case A_20:     // 20h   
        result+="$20";
        break; 
     case A_28:     // 28h   
        result+="$28";
        break;       
      case A_30:    // 30h   
        result+="$30";
        break; 
     case A_38:     // 38h   
        result+="$38";
        break;  
     case A_Z:      // Z cond
        result+="Z";
        break;  
     case A_NZ:      // NZ cond
        result+="NZ";
        break;        
     case A_NC:      // NC cond
        result+="NC";
        break; 
     case A_C:      // C cond
        result+="C";
        break;         
     case A_PO:      // PO cond
        result+="PO";
        break;   
     case A_P:      // P cond
        result+="P";
        break; 
     case A_PE:      // PE cond
        result+="PE";
        break; 
     case A_M:      // M cond
        result+="M";
        break;    
     case A_N:
        if (pos<buffer.length) value=Unsigned.done(buffer[pos++]);
        else value=0;
        
        result+=getLabelImm(pc+1, value); 
        break; 
     case A_NN:    // absolute NN
        if (pos<buffer.length-1) addr=((Unsigned.done(buffer[pos+1])<<8) | Unsigned.done(buffer[pos++]));
        else addr=-1;                       
        pos++; 
        
        result+=getLabel(addr);
        setLabel(addr);
        setLabelPlus(pc,1);
        setLabelPlus(pc,2);      
        break;
     case A__N_A:  // (N) immediate A 
        if (pos<buffer.length) value=Unsigned.done(buffer[pos++]);
        else value=0;
        
        result+="("+getLabelImm(pc+1, value)+"),"+(upperCase? "A": "a"); 
        break; 
     case A_A__N:  // A immediate (N)
        if (pos<buffer.length) value=Unsigned.done(buffer[pos++]);
        else value=0;
        
        result+=(upperCase? "A": "a")+",("+getLabelImm(pc+1, value)+")";
        break; 
     case A_NZ_NN:   // NZ cond NN
        this.pos=pos; 
        result+=getRegXXNN(buffer,(upperCase? "NZ": "nz")); 
        pos=this.pos;
        break;        
     case A_Z_NN:   // Z cond NN
        this.pos=pos;
        result+=getRegXXNN(buffer,(upperCase? "Z": "z")); 
        pos=this.pos;
        break;
     case A_NC_NN:  // NC cond NN
        this.pos=pos; 
        result+=getRegXXNN(buffer,(upperCase? "NC": "nc")); 
        pos=this.pos;
        break;
     case A_C_NN:   // C cond NN
        this.pos=pos; 
        result+=getRegXXNN(buffer,(upperCase? "C": "C"));  
        pos=this.pos;
        break;
     case A_PO_NN:  // PO cond NN
        this.pos=pos; 
        result+=getRegXXNN(buffer,(upperCase? "PO": "po"));
        pos=this.pos;
        break;
     case A_P_NN:   // P cond NN
        this.pos=pos; 
        result+=getRegXXNN(buffer,(upperCase? "P": "p")); 
        pos=this.pos;
        break;
     case A_PE_NN:  // PE cond NN
        result+=getRegXXNN(buffer,(upperCase? "PE": "pe")); 
        break; 
     case A_M_NN:   // PE cond NN   
        this.pos=pos; 
        result+=getRegXXNN(buffer,(upperCase? "M": "m"));  
        pos=this.pos;
        break;
     case A_A__C:   // A reg ind (C)
        result+=(upperCase? "A,(C)": "a,(c)"); 
        break;
     case A_B__C:   // B reg ind (C)
        result+=(upperCase? "B,(C)": "b,(c)"); 
        break;   
     case A_C__C:   // C reg ind (C)
        result+=(upperCase? "C,(C)": "c,(c)"); 
        break;   
     case A_D__C:   // D reg ind (C)
        result+=(upperCase? "D,(C)": "d,(c)"); 
        break;   
     case A_E__C:   // E reg ind (C)
        result+=(upperCase? "E,(C)": "e,(c)"); 
        break;   
     case A_H__C:   // H reg ind (C)
        result+=(upperCase? "H,(C)": "h,(c)"); 
        break;   
     case A_L__C:   // L reg ind (C)
        result+=(upperCase? "L,(C)": "l,(c)"); 
        break;   
     case A___C:   // ind (C)
        result+=(upperCase? "(C)": "(c)"); 
        break;   
     case A__C_A:   // ind (C) reg A
        result+=(upperCase? "(C),A": "(c),a"); 
        break;  
     case A__C_B:   // ind (C) reg B
        result+=(upperCase? "(C),B": "(c),b"); 
        break;    
     case A__C_C:   // ind (C) reg C
        result+=(upperCase? "(C),C": "(c),c"); 
        break;     
     case A__C_D:   // ind (C) reg D
        result+=(upperCase? "(C),D": "(c),d"); 
        break;    
     case A__C_E:   // ind (C) reg E
        result+=(upperCase? "(C),E": "(c),e"); 
        break;     
     case A__C_H:   // ind (C) reg H
        result+=(upperCase? "(C),H": "(c),h"); 
        break;    
     case A__C_L:   // ind (C) reg L
        result+=(upperCase? "(C),L": "(c),l"); 
        break;    
     case A___C_0:  // ind C 0 
        result+=(upperCase? "(C),0": "(c),0"); 
        break;  
     case A_0:      // 0 
        result+="0"; 
        break; 
     case A_1:      // 1
        result+="1"; 
        break;    
     case A_2:      // 2
        result+="2"; 
        break;    
     case A_0_A:    // 0 reg A
        result+="0,"+(upperCase? "A": "a"); 
        break; 
     case A_0_B:    // 0 reg B
        result+="0,"+(upperCase? "B": "b"); 
        break;    
     case A_0_C:    // 0 reg C
        result+="0,"+(upperCase? "C": "c"); 
        break;    
     case A_0_D:    // 0 reg D
        result+="0,"+(upperCase? "D": "d"); 
        break;    
     case A_0_E:    // 0 reg E
        result+="0,"+(upperCase? "E": "e"); 
        break;    
     case A_0_H:    // 0 reg H
        result+="0,"+(upperCase? "H": "h"); 
        break;    
     case A_0_L:    // 0 reg L
        result+="0,"+(upperCase? "L": "l"); 
        break;    
     case A_0__HL:   // 0 ind (HL)
        result+="0,"+(upperCase? "(HL)": "(hl)"); 
        break;
     case A_1_A:    // 1 reg A
        result+="1,"+(upperCase? "A": "a"); 
        break; 
     case A_1_B:    // 1 reg B
        result+="1,"+(upperCase? "B": "b"); 
        break;    
     case A_1_C:    // 1 reg C
        result+="1,"+(upperCase? "C": "c"); 
        break;    
     case A_1_D:    // 1 reg D
        result+="1,"+(upperCase? "D": "d"); 
        break;    
     case A_1_E:    // 1 reg E
        result+="1,"+(upperCase? "E": "e"); 
        break;    
     case A_1_H:    // 1 reg H
        result+="1,"+(upperCase? "H": "h"); 
        break;    
     case A_1_L:    // 1 reg L
        result+="1,"+(upperCase? "L": "l"); 
        break;    
     case A_1__HL:   // 1 ind (HL)
        result+="1,"+(upperCase? "(HL)": "(hl)"); 
        break;   
     case A_2_A:    // 2 reg A
        result+="2,"+(upperCase? "A": "a"); 
        break; 
     case A_2_B:    // 2 reg B
        result+="2,"+(upperCase? "B": "b"); 
        break;    
     case A_2_C:    // 2 reg C
        result+="2,"+(upperCase? "C": "c"); 
        break;    
     case A_2_D:    // 2 reg D
        result+="2,"+(upperCase? "D": "d"); 
        break;    
     case A_2_E:    // 2 reg E
        result+="2,"+(upperCase? "E": "e"); 
        break;    
     case A_2_H:    // 2 reg H
        result+="2,"+(upperCase? "H": "h"); 
        break;    
     case A_2_L:    // 2 reg L
        result+="2,"+(upperCase? "L": "l"); 
        break;    
     case A_2__HL:   // 2 ind (HL)
        result+="2,"+(upperCase? "(HL)": "(hl)"); 
        break;        
     case A_3_A:    // 3 reg A
        result+="3,"+(upperCase? "A": "a"); 
        break; 
     case A_3_B:    // 3 reg B
        result+="3,"+(upperCase? "B": "b"); 
        break;    
     case A_3_C:    // 3 reg C
        result+="3,"+(upperCase? "C": "c"); 
        break;    
     case A_3_D:    // 3 reg D
        result+="3,"+(upperCase? "D": "d"); 
        break;    
     case A_3_E:    // 3 reg E
        result+="3,"+(upperCase? "E": "e"); 
        break;    
     case A_3_H:    // 3 reg H
        result+="3,"+(upperCase? "H": "h"); 
        break;    
     case A_3_L:    // 3 reg L
        result+="3,"+(upperCase? "L": "l"); 
        break;    
     case A_3__HL:   // 3 ind (HL)
        result+="3,"+(upperCase? "(HL)": "(hl)"); 
        break;       
     case A_4_A:    // 4 reg A
        result+="4,"+(upperCase? "A": "a"); 
        break; 
     case A_4_B:    // 4 reg B
        result+="4,"+(upperCase? "B": "b"); 
        break;    
     case A_4_C:    // 4 reg C
        result+="4,"+(upperCase? "C": "c"); 
        break;    
     case A_4_D:    // 4 reg D
        result+="4,"+(upperCase? "D": "d"); 
        break;    
     case A_4_E:    // 4 reg E
        result+="4,"+(upperCase? "E": "e"); 
        break;    
     case A_4_H:    // 4 reg H
        result+="4,"+(upperCase? "H": "h"); 
        break;    
     case A_4_L:    // 4 reg L
        result+="4,"+(upperCase? "L": "l"); 
        break;    
     case A_4__HL:   // 4 ind (HL)
        result+="4,"+(upperCase? "(HL)": "(hl)"); 
        break;   
     case A_5_A:    // 5 reg A
        result+="5,"+(upperCase? "A": "a"); 
        break; 
     case A_5_B:    // 5 reg B
        result+="5,"+(upperCase? "B": "b"); 
        break;    
     case A_5_C:    // 5 reg C
        result+="5,"+(upperCase? "C": "c"); 
        break;    
     case A_5_D:    // 5 reg D
        result+="5,"+(upperCase? "D": "d"); 
        break;    
     case A_5_E:    // 5 reg E
        result+="5,"+(upperCase? "E": "e"); 
        break;    
     case A_5_H:    // 5 reg H
        result+="5,"+(upperCase? "H": "h"); 
        break;    
     case A_5_L:    // 5 reg L
        result+="5,"+(upperCase? "L": "l"); 
        break;    
     case A_5__HL:   // 5 ind (HL)
        result+="5,"+(upperCase? "(HL)": "(hl)"); 
        break;   
     case A_6_A:    // 6 reg A
        result+="6,"+(upperCase? "A": "a"); 
        break; 
     case A_6_B:    // 6 reg B
        result+="6,"+(upperCase? "B": "b"); 
        break;    
     case A_6_C:    // 6 reg C
        result+="6,"+(upperCase? "C": "c"); 
        break;    
     case A_6_D:    // 6 reg D
        result+="6,"+(upperCase? "D": "d"); 
        break;    
     case A_6_E:    // 6 reg E
        result+="6,"+(upperCase? "E": "e"); 
        break;    
     case A_6_H:    // 6 reg H
        result+="6,"+(upperCase? "H": "h"); 
        break;    
     case A_6_L:    // 6 reg L
        result+="6,"+(upperCase? "L": "l"); 
        break;    
     case A_6__HL:   // 6 ind (HL)
        result+="6,"+(upperCase? "(HL)": "(hl)"); 
        break;  
     case A_7_A:    // 7 reg A
        result+="7,"+(upperCase? "A": "a"); 
        break; 
     case A_7_B:    // 7 reg B
        result+="7,"+(upperCase? "B": "b"); 
        break;    
     case A_7_C:    // 7 reg C
        result+="7,"+(upperCase? "C": "c"); 
        break;    
     case A_7_D:    // 7 reg D
        result+="7,"+(upperCase? "D": "d"); 
        break;    
     case A_7_E:    // 7 reg E
        result+="7,"+(upperCase? "E": "e"); 
        break;    
     case A_7_H:    // 7 reg H
        result+="7,"+(upperCase? "H": "h"); 
        break;    
     case A_7_L:    // 7 reg L
        result+="7,"+(upperCase? "L": "l"); 
        break;    
     case A_7__HL:   // 7 ind (HL)
        result+="7,"+(upperCase? "(HL)": "(hl)"); 
        break;  
    }    
    this.pc=pc+steps;
    this.pos=pos;  
    
    return result;
  }  

  @Override
  public String dcom(int iType, int aType, long addr, long value) {
    return "";
  }
  
  /**
   * Get the instruction register over byte
   * 
   * @param buffer the buffer to use
   * @param reg the reg to use
   * @return the instruction
   */
  private String getRegXN(byte[] buffer, String reg) {
    if (pos<buffer.length) value=Unsigned.done(buffer[pos++]);
    else value=-1; 
        
    return reg+","+getLabelImm(pc+1, value);  
  }
  
  /**
   * Get the instruction registers over word
   * 
   * @param buffer the buffer to use
   * @param reg the regs to use
   * @return the instruction
   */
  private String getRegXXNN(byte[] buffer, String reg) {
    if (pos<buffer.length-1) addr=((Unsigned.done(buffer[pos+1])<<8) | Unsigned.done(buffer[pos++]));
    else addr=-1;
    pos++;    
                      
    setLabel(addr);
    setLabelPlus(pc,1);
    setLabelPlus(pc,2);  
    return reg+","+getLabel(addr);   
  }
  
    /**
   * Get the instruction registers ind over word
   * 
   * @param buffer the buffer to use
   * @param reg the regs to use
   * @return the instruction
   */
  private String getRegXXIndNN(byte[] buffer, String reg) {
    if (pos<buffer.length-1) addr=((Unsigned.done(buffer[pos+1])<<8) | Unsigned.done(buffer[pos++]));
    else addr=-1;
    pos++;    
      
    setLabel(addr);
    setLabelPlus(pc,1);
    setLabelPlus(pc,2);
    return reg+",("+getLabel(addr)+")";
   }     
  
  /**
   * Get the instruction word over register
   * 
   * @param buffer the bufffer to use
   * @param reg the reg to use
   * @return the instruction
   */
  private String getNNregX(byte[] buffer, String reg) {
    if (pos<buffer.length-1) addr=((Unsigned.done(buffer[pos+1])<<8) | Unsigned.done(buffer[pos++]));
    else addr=-1;
    pos++;  
        
    setLabel(addr);
    setLabelPlus(pc,1);
    setLabelPlus(pc,2);
    return getLabel(addr)+","+reg;    
  }
  
  /**
   * Get the instruction 
   * 
   * @param buffer the bufffer to use
   * @param reg the reg to use
   * @param reg2 the reg ind to use
   * @return the instruction 
   */
  private String getRefXIndXXN(byte[] buffer, String reg, String reg2) {
    if (pos<buffer.length) addr=Unsigned.done(buffer[pos++]);
    else addr=-1; 
                
    return reg+",("+reg2+"+"+getLabelZero(addr)+")";  
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

  /**
   * Comment and Disassemble a region of the buffer
   *
   * @param buffer the buffer containing the code
   * @param start the start position in buffer
   * @param end the end position in buffer
   * @param pc the programn counter for start position 
   * @return a string rapresentation of disassemble with comment
   */
  @Override
  public String cdasm(byte[] buffer, int start, int end, long pc) {    
    String tmp;                  // local temp string
    String tmp2;                 // local temp string
    MemoryDasm mem;              // memory dasm
    MemoryDasm memRel;           // memory related
    MemoryDasm memRel2;          // memory related of second kind
    int pos=start;               // actual position in buffer
    boolean isCode=true;         // true if we are decoding an instruction
    boolean wasGarbage=false;    // true if we were decoding garbage
        
    result.setLength(0);
    result.append(addConstants());
    
    this.pos=pos;
    this.pc=pc;
    while (pos<=end | pos<start) { // verify also that don't circle in the buffer        
      mem=memory[(int)pc];
      isCode=((mem.isCode || (!mem.isData && option.useAsCode)) && !mem.isGarbage);
        
        if (isCode) {    
          assembler.flush(result);
          
          // must put the org if we start from an garbage area
          if (wasGarbage) {
            wasGarbage=false;
            assembler.setOrg(result, (int)pc);
          }
            
          // add block if user declare it
          if (mem.userBlockComment!=null && !"".equals(mem.userBlockComment)) {  
            assembler.setBlockComment(result, mem);
          }   
            
          // add the label if it was declared by dasm or user           
          //if (mem.userLocation!=null && !"".equals(mem.userLocation)) result.append(mem.userLocation).append(":\n");
          //else if (mem.dasmLocation!=null && !"".equals(mem.dasmLocation)) result.append(mem.dasmLocation).append(":\n");
          if ((mem.userLocation!=null && !"".equals(mem.userLocation)) || 
             (mem.dasmLocation!=null && !"".equals(mem.dasmLocation))) {
            assembler.setLabel(result, mem);
            result.append("\n");
          }  
          
          // this is an instruction
          tmp=dasm(buffer); 
          tmp2=ShortToExe((int)pc)+"  "+ByteToExe(Unsigned.done(buffer[pos]));
          if (this.pc-pc==2) {
            if (pos+1<buffer.length) tmp2+=" "+ByteToExe(Unsigned.done(buffer[pos+1]));
            else tmp2+=" ??";
          }
          if (this.pc-pc==3) {
            if (pos+2<buffer.length) tmp2+=" "+ByteToExe(Unsigned.done(buffer[pos+1]))+
                                           " "+ByteToExe(Unsigned.done(buffer[pos+2]));
            else tmp2+=" ?????";
          }  
          if (this.pc-pc==4) {
            if (pos+3<buffer.length) tmp2+=" "+ByteToExe(Unsigned.done(buffer[pos+1]))+
                                           " "+ByteToExe(Unsigned.done(buffer[pos+2]))+
                                           " "+ByteToExe(Unsigned.done(buffer[pos+3]));
            else tmp2+=" ???????";
          }          
          
          for (int i=tmp2.length(); i<21; i++) // insert spaces
            tmp2+=" ";
          tmp=tmp2+tmp;
          tmp2="";
          for (int i=tmp.length(); i<43; i++) // insert spaces
            tmp2+=" ";
          result.append(tmp).append(tmp2);
          
          tmp2=dcom();   
          
          // if there is a user comment, then use it
          if (mem.userComment!=null) result.append(" ").append(mem.userComment).append("\n"); 
          else result.append(" ").append(tmp2).append("\n");  
          
          // always add a carriage return after a RTS, RTI or JMP
          if (iType==M_RET || iType==M_RETI || iType==M_RETN) result.append("\n");    
          
          if (pc>=0) {
            // rememeber this dasm automatic comment  
            if (!"".equals(tmp2)) mem.dasmComment=tmp2;
            else mem.dasmComment=null;
          }         
          
          pos=this.pos;
          pc=this.pc;
        } else 
            if (mem.isGarbage) {
              assembler.flush(result);
              wasGarbage=true;
              pos++;
              pc++;              
            
              this.pos=pos;
              this.pc=pc; 
            } 
          else {    
            // must put the org if we start from an garbage area
            if (wasGarbage) {
              wasGarbage=false;
              assembler.setOrg(result, (int)pc);
            }            
            
            memRel=mem.related!=-1 ? memory[mem.related & 0xFFFF]: null;
            if (memRel!=null) memRel2=memRel.related!=-1 ? memory[memRel.related & 0xFFFF]: null;
            else memRel2=null;
            assembler.putValue(result, mem, memRel, memRel2); 
            
            pos++;
            pc++;
            
            this.pos=pos;
            this.pc=pc;            
          }  
        
    } 
    assembler.flush(result);
    return result.toString();
  }
  
  /**
   * Comment and Disassemble a region of the buffer as source
   *
   * @param buffer the buffer containing the code
   * @param start the start position in buffer
   * @param end the end position in buffer
   * @param pc the programn counter for start position 
   * @return a string rapresentation of disasemble with comment
   */
  @Override
  public String csdasm(byte[] buffer, int start, int end, long pc) {
    String tmp;                  // local temp string
    String tmp2;                 // local temp string
    MemoryDasm mem;              // memory dasm
    MemoryDasm memRel;           // memory related
    MemoryDasm memRel2;          // memory related of second kind
    int pos=start;               // actual position in buffer
    boolean isCode=true;         // true if we are decoding an instruction
    boolean wasGarbage=false;    // true if we were decoding garbage
         
    result.setLength(0);
    result.append(addConstants());
    
    this.pos=pos;
    this.pc=pc;
    while (pos<=end | pos<start) { // verify also that don't circle in the buffer        
      mem=memory[(int)pc];
      isCode=((mem.isCode || (!mem.isData && option.useAsCode)) && !mem.isGarbage);
        
        if (isCode) {        
          assembler.flush(result);
          
          // must put the org if we start from an garbage area
          if (wasGarbage) {
            wasGarbage=false;
            assembler.setOrg(result, (int)pc);
          }             
          
          // add block if user declare it    
          if (mem.userBlockComment!=null && !"".equals(mem.userBlockComment)) {  
            assembler.setBlockComment(result, mem);
          }          
              
          if ((mem.userLocation!=null && !"".equals(mem.userLocation)) || 
             (mem.dasmLocation!=null && !"".equals(mem.dasmLocation))) {
            assembler.setLabel(result, mem);
            if (option.labelOnSepLine) result.append("\n");
          }  
          
          // this is an instruction
          tmp=dasm(buffer); 
  
          result.append(getInstrSpacesTabs(mem)).append(tmp).append(getInstrCSpacesTabs(tmp.length()));
          
          tmp2=dcom();   
          
          // if there is a user comment, then use it
          assembler.setComment(result, mem);
          
          // always add a carriage return after a RTS, RTI or JMP
          if (iType==M_RET || iType==M_RETI || iType==M_RETN) result.append("\n");        
          
          if (pc>=0) {
            // rememeber this dasm automatic comment  
            if (!"".equals(tmp2)) mem.dasmComment=tmp2;
            else mem.dasmComment=null;
          }         
          
          pos=this.pos;
          pc=this.pc;
        } else if (mem.isGarbage) {
              assembler.flush(result);
              wasGarbage=true;
              pos++;
              pc++;
                          
              this.pos=pos;
              this.pc=pc; 
            } 
          else { 
            // must put the org if we start from an garbage area
            if (wasGarbage) {                
              wasGarbage=false;
              assembler.setOrg(result, (int)pc);
            }   
            
            memRel=mem.related!=-1 ? memory[mem.related & 0xFFFF]: null;
            if (memRel!=null) memRel2=memRel.related!=-1 ? memory[memRel.related & 0xFFFF]: null;
            else memRel2=null;
            assembler.putValue(result, mem, memRel, memRel2);            
            
            pos++;
            pc++;
            
            this.pos=pos;
            this.pc=pc;            
          }  
        
    } 
    assembler.flush(result);
    return result.toString();
  }  
  
  /**
   * Return a comment string for the last instruction
   *
   * @return a comment string
   */
  public String dcom() {
    switch (iType) {
      case M_SLL:  
        return "Undocument instruction";  
    }
    return "";
  }
}
