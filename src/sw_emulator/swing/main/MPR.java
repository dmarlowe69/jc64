/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sw_emulator.swing.main;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import sw_emulator.math.Unsigned;

/**
 * Multiple Program class
 * 
 * @author stefano_tognon
 */
public class MPR {
   /** Header of file */ 
   public String header;
   
   /** Number of blocks to read */
   public int block;
   
   /** Block of data file */
   public ArrayList<byte[]> blocks; 
   
   /**
    * Get the elements inside the input stream
    * 
    * @param inB the coded input stream
    * @return true if operation is ok
    */
   public boolean getElements(byte[] inB) {
     DataInputStream in=new DataInputStream(new ByteArrayInputStream(inB));
     
     int size;
     
     byte[] buf;
     
     try {
       header=in.readUTF();
       block=in.readInt();
       blocks=new ArrayList<>(block);
       for (int i=0; i<block; i++) {
         size=in.readInt();
         buf=new byte[size];
         if (in.read(buf, 0, size)<0) return false;
         blocks.add(buf);
       }       
       
        // sort by asc memory address
        Collections.sort( blocks, new Comparator<byte[]>() {
        @Override
        public int compare(byte[] block2, byte[] block1)
        {

            return  (Unsigned.done(block1[0])+Unsigned.done(block1[1])*256)-
                    (Unsigned.done(block2[0])+Unsigned.done(block2[1])*256);
        }
    });
       
     } catch (IOException e) {
        System.err.println(e);
        return false;
     }
     return true;
   }
   
   /**
    * Set the elements of the given files if they are of the right type
    * 
    * @param files the list of file
    * @return true if operation is ok 
    */
   public boolean setElements(File[] files) {
     block=0;
     blocks=new ArrayList<>();
     
     byte[] inB;
       
     try {
       for (File file: files) {
         inB=FileManager.instance.readFile(file.getCanonicalPath());
         
         if (FileType.getFileType(inB)!=FileType.PRG) return false;
         
         block++;
         blocks.add(inB);
       }
     } catch (IOException e) {
        System.err.println(e);
        return false;
       }
     
     // sort by asc memory address
     Collections.sort( blocks, new Comparator<byte[]>() {
        @Override
        public int compare(byte[] block2, byte[] block1)
        {

            return  (Unsigned.done(block1[0])+Unsigned.done(block1[1])*256)-
                    (Unsigned.done(block2[0])+Unsigned.done(block2[1])*256);
        }
     });
          
     return true;
   }
   
   public String getDescription() {
     String res="";  
    
     if (blocks==null) return "";
     
     // sort by asc memory address
     Collections.sort( blocks, new Comparator<byte[]>() {
        @Override
        public int compare(byte[] block2, byte[] block1)
        {

            return  (Unsigned.done(block1[0])+Unsigned.done(block1[1])*256)-
                    (Unsigned.done(block2[0])+Unsigned.done(block2[1])*256);
        }
     });
     
     byte[] buf;
     int start;
     int end;
     
     Iterator<byte[]> iter=blocks.iterator();
     while (iter.hasNext()) {
       buf=iter.next();
       
       start=(Unsigned.done(buf[0])+Unsigned.done(buf[1])*256);
       end=start+buf.length-2;
       res+="START: "+String.format("0x%04X", start)+"  END="+String.format("0x%04X", end)+"\n";
     }
     
     return res;
   }
   
   /**
    * Save the file to disk 
    * 
    * @param file the file to use
    * @return true if operation is ok
    */
   public boolean saveFile(File file) {
     try {
       byte[] outB;
         
       DataOutputStream out=new DataOutputStream(
                            new BufferedOutputStream(   
                            new FileOutputStream(file)));
       
       out.writeUTF("MPRG#");
       out.writeInt(block);
       
       Iterator<byte[]> iter=blocks.iterator();
       while (iter.hasNext()) {
         outB=iter.next();
         out.writeInt(outB.length);
         out.write(outB);
       }
     } catch (Exception e) {
         System.err.println(e);
         return false;
       }
       return true;
   }
}
