/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rda.explorer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Matthias Mahler <matthias.mahler@isc.fraunhofer.de>
 */
public class RDAReader {

    private String _filename;
    private ArrayList<Block> _blocks = new ArrayList<Block>();

    private RDAReader() {
    }

    public RDAReader(String filename) {
        this._filename = filename;
        this.parse();
    }

    public void extract(String path) {
        int extractedFilesCnt = 0;
        File destination = new File(path);
        File f;
//        if (destination.exists()) {
//            Utils.deleteRecursive(destination);
//        }
        if (!destination.exists()) {
            destination.mkdir();
        }


        RandomAccessFile fIn = null;
        BufferedOutputStream fOut = null;
        byte[] dataBuffer = null;
        try {
            fIn = new RandomAccessFile(_filename, "r");
            fIn.seek(0);
            for (Block block : _blocks) {
                for (DirEntry file : block.files) {
                    f = new File(destination.getAbsoluteFile() + File.separator + file.getRelativePath());
                    if (!Utils.createDirectoryPath(f.getParentFile())) {
                        throw new RuntimeException(String.format("Error whilte creating directory path: %s", f.getAbsoluteFile()));
                    }

                    if (file.compressed == 0 && file.offset == 0) {
                        continue;
                    }

                    if ((block.head.flags & BlockHeader.FLAG_MEMORY_RESISTENT) > 0) {
                        throw new NotImplementedException();
                    } else {
                        fIn.seek(file.offset);
                        dataBuffer = new byte[file.compressed];
                        fIn.read(dataBuffer, 0, file.compressed);
                        
                        if((block.head.flags & BlockHeader.FLAG_ENCRYPTED)>0)
                            dataBuffer = Crypt.decrypt(dataBuffer);
                        
                        if((block.head.flags & BlockHeader.FLAG_COMPRESSED)>0){
                            Compressor compressor = new Compressor();
                            dataBuffer = compressor.decompress(dataBuffer, file.filesize);
                        }
                            
                        
                        fOut = new BufferedOutputStream(new FileOutputStream(f));
                        fOut.write(dataBuffer);
                        fOut.close();
                        System.out.println(String.format("%d BufferSize: %d WrittenSize: %d",file.filesize-f.length(), dataBuffer.length, f.length()));
                        extractedFilesCnt++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fIn.close();
                fOut.close();
            } catch (Exception e) {
            }

        }
        System.out.println(String.format("%d files extracted", extractedFilesCnt));
    }

    private void parse() {
        int numBlocks = 0;
        int numFiles = 0;
        FileHeader fileHeader = null;
        Block block = null;
        byte[] buffer;
        int offset = 0;
        boolean done = false;

        RandomAccessFile fIn = null;
        try {
            File f = new File(_filename); // Helperclass to get the filesize
            fIn = new RandomAccessFile(_filename, "r"); // 

            System.out.println("Filesize: " + f.length() + " Bytes");

            buffer = new byte[FileHeader.getSize()];
            fIn.read(buffer, offset, FileHeader.getSize());
            fileHeader = new FileHeader(buffer);
            System.out.println(fileHeader);

            offset = fileHeader.firstblock;
            while (!done) {
                block = new Block();
                System.out.println("Offset: " + offset);
                fIn.seek(offset);
                buffer = new byte[BlockHeader.getSize()];
                fIn.read(buffer, 0, BlockHeader.getSize());
                block.head = new BlockHeader(buffer);
                System.out.println("Reading Block: " + block.head);
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
                    System.out.println("Encrypted size: " + buffer.length);
                    buffer = Crypt.decrypt(buffer);
//                    throw new NotImplementedException();
                }

                if ((block.head.flags & BlockHeader.FLAG_COMPRESSED) > 0) { // if it is compressed
                    System.out.println("Block ist compressed");
                    System.out.println("Compressed size: " + buffer.length);
                    Compressor compressor = new Compressor();
                    buffer = compressor.decompress(buffer, block.head.decrompressedSize);
                    System.out.println("Decompressed size: " + buffer.length);
                }
                if (buffer.length != block.head.decrompressedSize) {
                    throw new RuntimeException(String.format("read block length (%d) mismatch decrompessed block length (%d)", buffer.length, block.head.decrompressedSize));
                }
                System.out.println("DirOffset: " + fIn.getFilePointer());
                System.out.println("BufferSize: " + buffer.length);
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
                numBlocks++;
                System.out.println("---------------------------------------------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fIn.close();
            } catch (Exception e) {
            }

        }
        System.out.println(String.format("%d files within %d blocks found", numFiles, numBlocks));

    }
}
