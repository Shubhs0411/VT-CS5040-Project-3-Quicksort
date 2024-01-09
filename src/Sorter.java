import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

/**
 * The Sorter class for performing sorting operations.
 *
 * @author {Shubham Laxmikant Deshmukh}
 * @version {v1}
 */
public class Sorter {

    private static File statsFile;
    private String filename;
    private BufferPool buffpool;
    private int cacheCount = 0;
    private int time = 0;
    private byte[] tempByte;
    private byte[] leftByte;
    private byte[] rightByte;

    /**
     * Constructor for the Sorter class.
     *
     * @param dataFileName
     *            the name of the file to be sorted
     * @param numBuffers
     *            the number of buffer slots available
     * @param statFileName
     *            the name of the output file for statistics
     */
    public Sorter(String dataFileName, int numBuffers, String statFileName) {
        filename = dataFileName;
        statsFile = new File(statFileName);
        buffpool = new BufferPool(dataFileName, numBuffers);
        tempByte = new byte[4];
        leftByte = new byte[4];
        rightByte = new byte[4];

    }


    /**
     * Write statistics to the specified output file.
     *
     * @param input
     *            the output file to write statistics to
     * @throws IOException
     *             if there is an issue with I/O operations
     */
    public void writeFile(File input) throws IOException {
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(
            input, true)));
        writer.println("Sort on " + filename);
        writer.println("Cache Hits: " + cacheCount);
        writer.println("Disk Reads: " + buffpool.discRead());
        writer.println("Disk Writes: " + buffpool.discWrite());
        writer.println("Time is " + time);
        writer.close();
    }


    /**
     * Perform the sorting operation and measure the time taken.
     *
     * @throws IOException
     *             if there is an issue with I/O operations
     */
    public void writeTime() throws IOException {
        final long start = System.currentTimeMillis();
        quicksort(buffpool, 0, (buffpool.getFileLength() - 4) / 4);
        final long end = System.currentTimeMillis();
        buffpool.flush();
        time = (int)(end - start);
        writeFile(statsFile);
    }


    /**
     * Perform the quicksort algorithm on the provided BufferPool.
     *
     * @param buff
     *            the BufferPool to perform sorting on
     * @param i
     *            the left index
     * @param j
     *            the right index
     * @throws IOException
     *             if there is an issue with I/O operations
     */
    public void quicksort(BufferPool buff, int i, int j) throws IOException {
        int pivotind = findpivot(i, j);
        swap(buff, pivotind, j);
        int l = 0;
        int temp = i;
        if (getKey(temp) == getKey(j)) {
            while (getKey(temp) == getKey(j) && temp <= j) {
                temp += 1;
            }
            if (temp > j) {
                return;
            }
        }
        l = partition(buff, i, j - 1, getKey(j));
        swap(buff, l, j);
        if ((l - i) > 1) {
            if (l - i <= 10) {
                for (int temp1 = i; temp1 <= l - 1; temp1 = temp1 + 1) {
                    for (int temp2 = temp1; (temp2 > 0) && getKey(
                        temp2) < getKey(temp2 - 1); temp2 = temp2 - 1) {
                        swap(buff, temp2, temp2 - 1);
                    }
                }
            }
            else {
                quicksort(buff, i, l - 1);
            }
        }
        if ((j - l) > 1) {
            if (j - l <= 10) {
                for (int temp1 = l + 1; temp1 <= j; temp1 = temp1 + 1) {
                    for (int temp2 = temp1; (temp2 > 0) && getKey(
                        temp2) < getKey(temp2 - 1); temp2 = temp2 - 1) {
                        swap(buff, temp2, temp2 - 1);
                    }
                }
            }
            else {
                quicksort(buff, l + 1, j);
            }
        }
    }


    /**
     * Partition the data in the BufferPool.
     *
     * @param buff
     *            the BufferPool to perform partitioning on
     * @param left
     *            the left index
     * @param right
     *            the right index
     * @param pivot
     *            the pivot value
     * @return the partition index
     */
    public int partition(BufferPool buff, int left, int right, int pivot) {
        while (left <= right) {
            while (getKey(left) < pivot) {
                left = left + 1;
            }
            while (right >= left && getKey(right) >= pivot) {
                right = right - 1;
            }
            if (right > left) {
                swap(buff, left, right);
            }
        }
        return left;
    }


    /**
     * Get the key at a specified index in the BufferPool.
     *
     * @param ind
     *            the index of the key
     * @return the key value
     */
    public int getKey(int ind) {
        buffpool.getBytes(tempByte, 4, ind);
        ByteBuffer byteBuffer = ByteBuffer.wrap(tempByte);
        int length = byteBuffer.getShort();
        return length;
    }


    /**
     * Find the pivot index for quicksort.
     *
     * @param left
     *            the left index
     * @param right
     *            the right index
     * @return the pivot index
     */
    public int findpivot(int left, int right) {
        return (left + right) / 2;
    }


    /**
     * Swap elements in the BufferPool.
     *
     * @param buff
     *            the BufferPool
     * @param left
     *            the left index
     * @param right
     *            the right index
     */
    public void swap(BufferPool buff, int left, int right) {
        cacheCount++;
        buff.getBytes(leftByte, 4, left);
        buff.getBytes(rightByte, 4, right);
        buff.insert(rightByte, 4, left);
        buff.insert(leftByte, 4, right);
    }
}
