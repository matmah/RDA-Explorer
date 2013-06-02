package rda.explorer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Related Design Archive File (RDA)
 * @code struct Header
 * @code {
 * @code 	wchar magic[260];
 * @code 	ubyte uk[524];
 * @code 	uint firstBlock; // offset
 * @code }
 */
public class FileHeader {

    char[] magic;
    byte[] uk;
    int firstblock; // offset

    public int getFirstblock() {
        return (int) firstblock;
    }

    public FileHeader(byte[] header) {
        String signature = "";
        for (int i = 0; i < 35; i = i + 2) {
//            System.out.println(String.format("%02X - %02X ", header[i],header[i+1]));
            signature += (char) Utils.bytesToShort(header[i], header[i + 1]);
        }
        System.out.println(signature);
        if (!signature.equals("Resource File V2.0")) {
            throw new RuntimeException("Invalid RDA file");
        }

        this.uk = new byte[524];
        for (int i = 0x208; i < 0x414; i++) {
            this.uk[i - 520] = header[i];
        }
        this.firstblock = Utils.bytesToInt(header[0x414], header[0x415], header[0x416], header[0x417]);
    }

    public static int getSize() {
        return 1048;
    }

    @Override
    public String toString() {
        return "FileHeader{" + "magic=" + magic + ", uk=" + uk + ", firstblock=" + firstblock + '}';
    }
    
    
}
