/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rda.explorer;

import java.io.File;

/**
 * @code struct DirEntry {
 * @code    wchar filename[260];
 * @code    uint offset;
 * @code    uint compressed;
 * @code    uint filesize;
 * @code    int timestamp;
 * @code    uint uk;
 * @code }
 *
 * @author Matthias Mahler <matthias.mahler@isc.fraunhofer.de>
 */
class DirEntry {

    char filename[] = new char[260]; // filename[260]
    int offset;
    int compressed;
    int filesize;
    int timestamp;
    int uk;
    
    public DirEntry(byte[] buffer) {
        int x = 0;
        int n;
        for (int i = x; i < 260; i++) {
            n = i*2;
            filename[i] = (char)Utils.bytesToShort(buffer[n], buffer[n+1]);
        }
        x=x+2*260;
        offset = Utils.bytesToInt(buffer[x], buffer[x+1], buffer[x+2], buffer[x+3]);
        x=x+4;
        compressed = Utils.bytesToInt(buffer[x], buffer[x+1], buffer[x+2], buffer[x+3]);
        x=x+4;
        filesize = Utils.bytesToInt(buffer[x], buffer[x+1], buffer[x+2], buffer[x+3]);
        x=x+4;
        timestamp = Utils.bytesToInt(buffer[x], buffer[x+1], buffer[x+2], buffer[x+3]);
        x=x+4;
        uk = Utils.bytesToInt(buffer[x], buffer[x+1], buffer[x+2], buffer[x+3]);
        x=x+4;
        
    }
    
    public static int getSize() {
        return 540;
    }

    @Override
    public String toString() {
        return "DirEntry{" + "filename=" + (String.valueOf(filename)).trim() + ", offset=" + offset + ", compressed=" + compressed + ", filesize=" + filesize + ", timestamp=" + timestamp + ", uk=" + uk + '}';
    }

    public String getRelativePath() {
        return (String.valueOf(filename)).trim().replaceAll("/", "\\\\\\\\");
    }
    public String getFilename() {
        String[] splited = this.getRelativePath().split("\\"+File.separator);
        return splited[splited.length-1];
    }
    
    
    
}
