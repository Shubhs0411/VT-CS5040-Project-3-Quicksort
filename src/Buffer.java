/**
 * The Buffer class for managing buffer data and state.
 * 
 * @author Shubham Laxmikant Deshmukh
 * @version v1
 */
public class Buffer {
    private byte[] buffer;
    private boolean dirty;
    private int currInd;

    /**
     * Constructor for the Buffer class.
     *
     * @param ind
     *            the index of the buffer
     * @param array
     *            the input byte array
     */
    public Buffer(int ind, byte[] array) {
        buffer = array;
        dirty = false;
        currInd = ind;

    }


    /**
     * Get the index of the buffer.
     *
     * @return the index of the buffer
     */
    public int getIndex() {
        return currInd;
    }


    /**
     * Get the byte array stored in the buffer.
     *
     * @return the byte array in the buffer
     */
    public byte[] getByte() {
        return buffer;
    }


    /**
     * Get the dirty status of the buffer.
     *
     * @return true if the buffer is dirty, false otherwise
     */
    public boolean getDirtyStatus() {
        return dirty;
    }


    /**
     * Set the dirty status of the buffer.
     *
     * @param input
     *            true if the buffer is dirty, false otherwise
     */
    public void setDirtyStatus(boolean input) {
        dirty = input;
    }
}
