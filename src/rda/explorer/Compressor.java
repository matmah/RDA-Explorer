/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rda.explorer;

import java.io.UnsupportedEncodingException;
import java.util.zip.*;

public class Compressor {

    public byte[] compress(byte[] bytesToCompress) {
        Deflater deflater = new Deflater();
        deflater.setInput(bytesToCompress);
        deflater.finish();

        byte[] bytesCompressed = new byte[Short.MAX_VALUE];

        int numberOfBytesAfterCompression = deflater.deflate(bytesCompressed);

        byte[] returnValues = new byte[numberOfBytesAfterCompression];

        System.arraycopy(
                bytesCompressed,
                0,
                returnValues,
                0,
                numberOfBytesAfterCompression);

        return returnValues;
    }

    public byte[] compress(String stringToCompress) {
        byte[] returnValues = null;

        try {

            returnValues = this.compress(
                    stringToCompress.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }

        return returnValues;
    }

    public byte[] decompress(byte[] bytesToDecompress, int decompressedSize) {
        Inflater inflater = new Inflater();

        int numberOfBytesToDecompress = bytesToDecompress.length;

        inflater.setInput(
                bytesToDecompress,
                0,
                numberOfBytesToDecompress);

        byte[] bytesDecompressed = new byte[decompressedSize];

        byte[] returnValues = null;

        try {
            int numberOfBytesAfterDecompression = inflater.inflate(bytesDecompressed);

            returnValues = new byte[numberOfBytesAfterDecompression];

            System.arraycopy(
                    bytesDecompressed,
                    0,
                    returnValues,
                    0,
                    numberOfBytesAfterDecompression);
        } catch (DataFormatException dfe) {
            dfe.printStackTrace();
        }

        inflater.end();

        return returnValues;
    }

    public byte[] decompress(byte[] bytesToDecompress) {
        Inflater inflater = new Inflater();

        int numberOfBytesToDecompress = bytesToDecompress.length;

        inflater.setInput(
                bytesToDecompress,
                0,
                numberOfBytesToDecompress);

        int compressionFactorMaxLikely = 3;

        int bufferSizeInBytes =
                numberOfBytesToDecompress
                * compressionFactorMaxLikely;

        byte[] bytesDecompressed = new byte[bufferSizeInBytes];

        byte[] returnValues = null;

        try {
            int numberOfBytesAfterDecompression = inflater.inflate(bytesDecompressed);

            returnValues = new byte[numberOfBytesAfterDecompression];

            System.arraycopy(
                    bytesDecompressed,
                    0,
                    returnValues,
                    0,
                    numberOfBytesAfterDecompression);
        } catch (DataFormatException dfe) {
            dfe.printStackTrace();
        }

        inflater.end();

        return returnValues;
    }

    public String decompressToString(byte[] bytesToDecompress) {
        byte[] bytesDecompressed = this.decompress(
                bytesToDecompress);

        String returnValue = null;

        try {
            returnValue = new String(
                    bytesDecompressed,
                    0,
                    bytesDecompressed.length,
                    "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }

        return returnValue;
    }
}
