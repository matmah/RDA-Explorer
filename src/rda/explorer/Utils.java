/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rda.explorer;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Utils {

    public static int bytesToInt(byte b1, byte b2, byte b3, byte b4) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{b1, b2, b3, b4});
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getInt();
    }

    public static short bytesToShort(byte b1, byte b2) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{b1, b2});
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getShort();
    }

    static boolean deleteRecursive(File destination) {
        for (File file : destination.listFiles()) {
            if (file.isDirectory()) {
                if (!deleteRecursive(file)) {
                    return false;
                }
            } else {
                if (!file.delete()) {
                    return false;
                }
            }
        }
        destination.delete();
        return true;
    }

    public static boolean createDirectoryPath(File f) {
//        if(f.toString().matches("^.*\\.\\w{2,4}$"))
//            f = f.getParentFile();
        if(f.exists())
            return true;
        if(f.getParentFile().exists())
            return f.mkdir();
        else
            return createDirectoryPath(f.getParentFile()) && f.mkdir();
    }
}
