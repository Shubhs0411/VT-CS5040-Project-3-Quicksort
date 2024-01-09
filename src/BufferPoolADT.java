/**
 * The BufferPoolADT interface for defining buffer pool operations.
 * 
 * @author Shubham Laxmikant Deshmukh
 * @version v1
 */

public interface BufferPoolADT {

    /**
     * Inserts data into the
     * buffer pool at the specified position.
     *
     * @param space
     *            the data to insert
     * @param size
     *            the size of the data to insert
     * @param position
     *            the position at which to insert
     */
    public void insert(byte[] space, int size, int position);


    /**
     * Retrieves data from the buffer
     * pool at the specified position and stores it
     * in the provided space.
     *
     * @param space
     *            the space to store the retrieved data
     * @param size
     *            the size of the data to retrieve
     * @param position
     *            the position from which to retrieve the data
     */
    public void getBytes(byte[] space, int size, int position);
}
