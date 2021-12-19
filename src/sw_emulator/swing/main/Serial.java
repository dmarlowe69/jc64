/**
 * @(#)Serial.java 2021/12/19
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
package sw_emulator.swing.main;

import java.io.Serializable;
import java.util.UUID;
import sw_emulator.software.MemoryDasm;

/**
 * Serizable container for copy/paste action throw different instance
 * 
 * @author ice
 */
public class Serial implements Serializable {
  /** Unique UUID to check different instance */
  public UUID uuid;  
    
  /** Selected ranges of address in memory */  
  public int[] selected;  
  
  /** Memory for dasm*/
  public MemoryDasm[] memory;
}
