/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rda.explorer;

import java.io.File;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Matthias Mahler <matthias.mahler@isc.fraunhofer.de>
 */
public class RDAExplorerConsole {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 2 || !args[0].matches("extract|compact")) {
            InvalidInput();
        }
        if (args[0].matches("extract")) {
//            byte[] b = {57, 57, 33, -18, 65, -8, 45, -113, 65, -119, 120, 83, -63, 120, 11, 47, 22, 55, -78, 2, -98, 101, 98, 12, 53, 6, 116, 97, -113, -2, -20, 45, -70, -10, 101, 59, -108, 118, 5, 29, -69, 112, -72, -104, 33, 59, -65, -56, 55, -83};
//            byte[] b = {0x39,0x39,0x21,0xee,0x41,0xf8,0x2d,0x8f,0x41,0x89,0x78,0x53,0xc1,0x78,0xb,0x2f,0x16,0x37,0xb2,0x2,0x9e,0x65,0x62,0xc,0x35,0x6,0x74,0x61,0x8f,0xfe,0xec,0x2d,0xba,0xf6,0x65,0x3b,0x94,0x76,0x5,0x1d,0xbb,0x70,0xb8,0x98,0x21,0x3b,0xbf,0xc8,0x37,0xad};
//            byte[] b = {1,2,3,4,5,6,7,8,9};
//            byte[] b2 = Crypt.decrypt(b);
//            for (int i = 0; i < b.length; i++) {
//                System.out.println(String.format("% 3d => %x ", b[i],b2[i]));
//            }
//            System.exit(0);
            File rda = new File(args[1]);
            if (!rda.exists()) {
                throw new RuntimeException("file doesn't exists: " + args[1]);
            }
            String destinationPath = "";
            if (args.length == 2) {
                destinationPath = rda.getParent();
            } else {
                destinationPath = args[2];
            }
//            RDAReader reader = new RDAReader(args[1]);
            RdaFile reader = new RdaFile(args[1]);
//            reader.extract(destinationPath);
        }
        if (args[0].matches("compact")) {
            throw new NotImplementedException();
        }

    }

    public static void InvalidInput() {
        System.out.println("Wrong parameters!");
        System.out.println("Following commands exists:");
        System.out.println("\textract RDA_FILE DESTINATION_PATH");
        System.out.println("\tcompact DESTINATION_PATH RDA_FILE");
        System.exit(0);
    }
}
