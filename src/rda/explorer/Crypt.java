/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rda.explorer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Vector;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Matthias Mahler <matthias.mahler@isc.fraunhofer.de>
 */
public class Crypt {

    public static byte[] decrypt(byte[] bytes) {
        try {
//            DataInputStream bIn = new DataInputStream(new ByteArrayInputStream(bytes));
            ArrayList<Short> shortList = new ArrayList<Short>();
            
//            System.out.print(String.format("byte[] b = {",bytes.length));
//            for(int i = 0;i<50;i++){
////                System.out.print(String.format("%d,", bytes[i]));
//                System.out.print(String.format("0x%x,", bytes[i]));
////                System.out.print(String.format("%d,", bytes[i]));
//            }
//            System.out.print(String.format("};"));
//            System.out.println("");
            ByteBuffer bBuffer = ByteBuffer.wrap(bytes);
            bBuffer.order(ByteOrder.LITTLE_ENDIAN);
            while (true) {
                try {
                    shortList.add(bBuffer.getShort());
//                } catch (EOFException e) {
//                    break;
                } catch (BufferUnderflowException e) {
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            bIn.close();

//            ByteArrayOutputStream bArrayOut = new ByteArrayOutputStream();
//            DataOutputStream bOut = new DataOutputStream(bArrayOut);
            bBuffer.clear();
            int x = 0xA2C2A;
            
            short y = 0;
            for (int i = 0; i < shortList.size(); i++) {
                x = x * 0x343FD + 0x269EC3;
                y = (short) (x >>> 16 & 0x7FFF);
                short w = (short) (shortList.get(i) ^ y);
//                System.out.println(String.format("%s x: %s y: %s w: %s",shortList.get(i), x,y,w));
//                bOut.writeShort(w);
                bBuffer.putShort(w);
            }
            if (bytes.length % 2 != 0) {
                bBuffer.put(bytes[bytes.length - 1]);
//                bOut.writeByte(bytes[bytes.length - 1]);
            }

            // 513 x: 943800901 y: 14401 w: 14912
            // 1027 x: 1942760436 y: 29644 w: 30671
            // 1541 x: 1682631399 y: 25674 w: 25167
            // 2055 x: 2053126670 y: 31328 w: 29287
            // 1 => 64 
            // 2 => 58 
            // 3 => 207 
            // 4 => 119 
            // 5 => 79 
            // 6 => 98 
            // 7 => 103 
            // 8 => 114 
            // 9 => 9 



//            bytes = bArrayOut.toByteArray();
//            bOut.close();
//            bArrayOut.close();
//            System.out.println(bytes.length);
            return bBuffer.array();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return bytes;
    }

    public static byte[] crypt(byte[] bytes) {
        throw new NotImplementedException();
    }
}
