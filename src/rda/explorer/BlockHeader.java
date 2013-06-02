package rda.explorer;

/**
 * @code struct BlockHeader {
 * @code    uint flags; // 2 = encrypted, 1 = compressed, 4 = memoryresident, 8 = deleted, (16 = filter?)
 * @code    uint numFiles;
 * @code    uint dirSize;
 * @code    uint decompressedSize; // directory decompressed size
 * @code    uint nextBlock; // offset of the following block (or filesize if last block)
 * @code }
 *
 * @author Matthias Mahler <matthias.mahler@isc.fraunhofer.de>
 */
public class BlockHeader {

    public static final int FLAG_COMPRESSED = 1;
    public static final int FLAG_ENCRYPTED = 2;
    public static final int FLAG_MEMORY_RESISTENT = 4;
    public static final int FLAG_DELETED = 8;
    int flags;
    int numFiles;
    int dirSize;
    int decrompressedSize;
    int nextBlock;

    BlockHeader(byte[] buffer) {
        int n = 0;
        flags = Utils.bytesToInt(buffer[n], buffer[n + 1], buffer[n + 2], buffer[n + 3]);
        n = n + 4;
        numFiles = Utils.bytesToInt(buffer[n], buffer[n + 1], buffer[n + 2], buffer[n + 3]);
        n = n + 4;
        dirSize = Utils.bytesToInt(buffer[n], buffer[n + 1], buffer[n + 2], buffer[n + 3]);
        n = n + 4;
        decrompressedSize = Utils.bytesToInt(buffer[n], buffer[n + 1], buffer[n + 2], buffer[n + 3]);
        n = n + 4;
        nextBlock = Utils.bytesToInt(buffer[n], buffer[n + 1], buffer[n + 2], buffer[n + 3]);
        n = n + 4;
    }

    public static int getSize() {
        return 20;
    }

    @Override
    public String toString() {
        return "BlockHeader{" + "flags=" + flags + ", numFiles=" + numFiles + ", dirSize=" + dirSize + ", decrompressedSize=" + decrompressedSize + ", nextBlock=" + nextBlock + '}';
    }
}
