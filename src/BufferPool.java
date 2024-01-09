import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * The BufferPool class for managing buffer pools and file access.
 * 
 * @author Shubham Laxmikant Deshmukh
 * @version v1
 */
public class BufferPool implements BufferPoolADT {
    private RandomAccessFile file;
    private final static int BUFFERSIZE = 4096;
    private int readToDisc = 0;
    private int writeToDisc = 0;
    private ListLRU<Buffer> list;

    /**
     * Constructor for the BufferPool class.
     *
     * @param dataFileName
     *            the path to the file
     * @param numBuffers
     *            the size of the buffer pool
     */
    public BufferPool(String dataFileName, int numBuffers) {
        list = new ListLRU<Buffer>(numBuffers);
        try {
            file = new RandomAccessFile(dataFileName, "rw");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Retrieves data from the buffer
     * pool at the specified index and stores it in
     * the destination.
     *
     * @param location
     *            the location to store the retrieved data
     * @param size
     *            the size of the data to retrieve
     * @param ind
     *            the index from which to retrieve the data
     */
    public void getBytes(byte[] location, int size, int ind) {
        int position = ind * 4;
        Buffer buff = searchBuffer(position);
        if (buff != null) {
            System.arraycopy(buff.getByte(), position % BUFFERSIZE, location, 0,
                4);
        }
        else {
            byte[] newByte = new byte[BUFFERSIZE];
            try {
                file.seek((position / BUFFERSIZE) * BUFFERSIZE);
                file.read(newByte);
                readToDisc++;
                buff = new Buffer(position / BUFFERSIZE, newByte);
                System.arraycopy(buff.getByte(), position % BUFFERSIZE,
                    location, 0, 4);
                Buffer returned;
                if (list.isFull()) {
                    returned = list.get(list.size() - 1);
                    if (returned.getDirtyStatus()) {
                        file.seek(returned.getIndex() * BUFFERSIZE);
                        file.write(returned.getByte());
                        writeToDisc++;
                        returned.setDirtyStatus(false);
                    }
                    list.removeLast();
                }
                list.addition(buff);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Inserts data into the buffer pool at the specified index.
     *
     * @param source
     *            the source data to insert
     * @param size
     *            the size of the data to insert
     * @param ind
     *            the index at which to insert
     */
    public void insert(byte[] source, int size, int ind) {
        int position = ind * 4;
        Buffer buff = searchBuffer(position);
        if (buff != null) {
            System.arraycopy(source, 0, buff.getByte(), position % BUFFERSIZE,
                4);
        }
        else {
            byte[] newByte = new byte[BUFFERSIZE];
            try {
                file.seek((position / BUFFERSIZE) * BUFFERSIZE);
                file.read(newByte);
                readToDisc++;
                buff = new Buffer(position / BUFFERSIZE, newByte);
                System.arraycopy(source, 0, buff.getByte(), position
                    % BUFFERSIZE, 4);
                Buffer returned;
                if (list.isFull()) {
                    returned = list.get(list.size() - 1);
                    if (returned.getDirtyStatus()) {
                        file.seek(returned.getIndex() * BUFFERSIZE);
                        file.write(returned.getByte());
                        writeToDisc++;
                        returned.setDirtyStatus(false);
                    }
                    list.removeLast();
                }
                list.addition(buff);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        buff.setDirtyStatus(true);
    }


    /**
     * Flushes dirty buffers to disk.
     *
     * @throws IOException
     *             if there is an issue with I/O operations
     */
    public void flush() throws IOException {
        Buffer curBuff;
        for (int i = 0; i < list.size(); i++) {
            curBuff = list.get(i);
            if (curBuff.getDirtyStatus()) {
                file.seek(curBuff.getIndex() * BUFFERSIZE);
                file.write(curBuff.getByte());
                writeToDisc++;
            }
        }
    }


    /**
     * Searches for a buffer in the pool based on the given index.
     *
     * @param index
     *            the index to search for
     * @return the buffer if found, or null if not found
     */
    public Buffer searchBuffer(int index) {
        for (int i = 0; i < list.size(); i++) {
            Buffer buffer = list.get(i);
            if (buffer.getIndex() == index / BUFFERSIZE) {
                return buffer;
            }
        }
        return null;
    }


    /**
     * Gets the length of the file.
     *
     * @return the length of the file
     * @throws IOException
     *             if there is an issue with I/O operations
     */
    public int getFileLength() throws IOException {
        return (int)file.length();
    }


    /**
     * Gets the number of disk writes.
     *
     * @return the number of disk writes
     */
    public int discWrite() {
        return writeToDisc;
    }


    /**
     * Gets the number of disk reads.
     *
     * @return the number of disk reads
     */
    public int discRead() {
        return readToDisc;
    }
}
