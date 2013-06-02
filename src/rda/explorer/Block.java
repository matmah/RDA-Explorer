/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rda.explorer;

import java.util.ArrayList;

public class Block {
    
    

    BlockHeader head;
    int offset;             //  uint offset; // for memoryresident files
    int compressedSize;     //  uint compressed; // size of consecutive data
    int decompressedSize;   //  uint datasize; // decompressed size
    ArrayList<DirEntry> files = new ArrayList<DirEntry>();

    @Override
    public String toString() {
        return "Block{" + "head=" + head + ", offset=" + offset + ", compressedSize=" + compressedSize + ", decompressedSize=" + decompressedSize + ", files=" + files + '}';
    }
    
    public int getFileCount(){
        return files.size();
    }
    
    
}
