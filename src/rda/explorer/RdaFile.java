/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rda.explorer;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Matthias Mahler <matthias.mahler@isc.fraunhofer.de>
 */
public class RdaFile {

    private String _filename = null;
    private FileHeader _header;
    private ArrayList<Block> _blocks = new ArrayList<Block>();

    public RdaFile(String path) {
        _filename = path;
        File f = new File(path); // Helperclass to get the filesize
        if (!f.exists()) {
            throw new RuntimeException(path + " not found");
        }

        RandomAccessFile fIn = null;
        try {
            fIn = new RandomAccessFile(f.getAbsolutePath(), "r"); //  
            byte[] buffer = new byte[FileHeader.getSize()];
            fIn.read(buffer, 0, FileHeader.getSize());
            _header = new FileHeader(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.readBlocks();
        System.out.println(String.format("%d files within %d blocks found", getFileCount(), _blocks.size()));
    }
    
    public int getFileCount(){ 
        int cnt = 0;
        for (Block block : _blocks) {
            cnt += block.getFileCount();
        }
        return cnt;
    }

    @Override
    public String toString() {
        return "RdaFile{" + "_filename=" + _filename + ", _header=" + _header + ", _blocks=" + _blocks + '}';
    }
    
    

    private void readBlocks() {
        boolean done = false;
        int numFiles = 0;
        int offset = _header.firstblock;
        byte[] buffer = null;
        File f = new File(_filename);
        RandomAccessFile fIn = null;
        Block block = null;
        Compressor compressor = new Compressor();

        try {
            fIn = new RandomAccessFile(f, "r");
            while (!done) {
                block = new Block();
                fIn.seek(offset);
                buffer = new byte[BlockHeader.getSize()];
                fIn.read(buffer, 0, BlockHeader.getSize());
                block.head = new BlockHeader(buffer);

                // TODO empty block
                fIn.seek(fIn.getFilePointer() - block.head.dirSize - BlockHeader.getSize());

                // if block data is memoryresident, the block is preceded by compressed and uncompressed size
                if ((block.head.flags & BlockHeader.FLAG_MEMORY_RESISTENT) > 0) {
                    System.out.println("Block ist memoryresisdent");
                    fIn.seek(fIn.getFilePointer() - 8);
                }

                buffer = new byte[block.head.dirSize];
                fIn.read(buffer, 0, block.head.dirSize);

                if ((block.head.flags & BlockHeader.FLAG_ENCRYPTED) > 0) { // if it is encrypted
                    System.out.println("Block ist encrypted");
                    buffer = Crypt.decrypt(buffer);
                }

                if ((block.head.flags & BlockHeader.FLAG_COMPRESSED) > 0) { // if it is compressed
                    System.out.println("Block ist compressed");
                    buffer = compressor.decompress(buffer, block.head.decrompressedSize);
                }
                if (buffer.length != block.head.decrompressedSize) {
                    throw new RuntimeException(String.format("read block length (%d) mismatch decrompessed block length (%d)", buffer.length, block.head.decrompressedSize));
                }

                int passedSize = 0;
                while (passedSize < block.head.decrompressedSize) {
                    block.files.add(new DirEntry(Arrays.copyOfRange(buffer, passedSize, passedSize + DirEntry.getSize())));
                    System.out.println(block.files.toArray()[block.files.size() - 1]);
                    passedSize += DirEntry.getSize();
                    numFiles++;
                }

                // memoryresident data?
                if ((block.head.flags & BlockHeader.FLAG_MEMORY_RESISTENT) > 0) {
                    throw new NotImplementedException();
//                    hIn.read(compressed);
//                    hIn.read(datasize);
//                    offset = cast(uint)(hIn.position - 8 - block.dirSize - compressed);
                }

                _blocks.add(block);

                if (block.head.nextBlock >= f.length()) {
                    done = true;
                } else {
                    offset = block.head.nextBlock;
                }
                System.out.println("---------------------------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
