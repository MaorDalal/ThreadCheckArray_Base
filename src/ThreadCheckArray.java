import java.util.ArrayList;

/**
 * A worker that searches for a solution to the Subset Sum decision problem
 * using recursion, as one of two cooperating threads.
 * <p>
 * The input numbers are read from a shared {@link SharedData} object as an
 * {@code ArrayList<Integer>}. The search space is split between two threads by the
 * last element: the thread named {@code "thread1"} assumes the last element is part of
 * the subset, and the other thread assumes it is not. As soon as one thread finds a
 * solution it sets the shared flag, and the other thread stops searching.
 * <p>
 * All access to the shared object is done inside {@code synchronized (sd)} blocks.
 */
public class ThreadCheckArray implements Runnable {
    /** {@code true} once this thread has found a valid subset. */
    private boolean flag;

    /** Marks, for every index of {@link #array}, whether this thread uses that element in its solution. */
    private boolean[] winArray;

    /** The data shared with the other thread; also used as the lock object. */
    SharedData sd;

    /** The input numbers, taken from the shared data. */
    ArrayList<Integer> array;

    /** The target sum, taken from the shared data. */
    int b;

    /**
     * Creates a worker for the given shared data.
     * Reads the input list and target sum under the lock of {@code sd} and sizes the
     * win array with {@code array.size()}.
     *
     * @param sd the shared data object holding the {@code ArrayList<Integer>} of numbers and the target sum
     */
    public ThreadCheckArray(SharedData sd) {
        this.sd = sd;
        synchronized (sd) {
            array = sd.getArray();
            b = sd.getB();
        }
        winArray = new boolean[array.size()];
    }

    /**
     * Recursively checks whether some subset of the first {@code n} elements sums to {@code b}.
     * For element {@code n-1} it first tries to include it, then to exclude it.
     * The search stops early if any thread has already set the shared flag.
     *
     * @param n the number of elements (from the start of the list) still to consider
     * @param b the remaining sum that the chosen elements must reach
     */
    void rec(int n, int b) {
        synchronized (sd) {
            if (sd.getFlag()) return;
        }
        if (n == 1) {
            if(b == 0 || b == array.get(n-1)) {
                flag = true;
                synchronized (sd) { sd.setFlag(true); }
            }
            if (b == array.get(n-1)) winArray[n-1] = true;
            return;
        }

        rec(n-1, b - array.get(n-1));
        if (flag) winArray[n-1] = true;
        synchronized (sd) {
            if (sd.getFlag()) return;
        }
        rec(n-1, b);
    }

    /**
     * Runs this thread's half of the search.
     * {@code "thread1"} searches with the last element included, any other thread
     * searches with it excluded. A list of size 1 is handled directly.
     * If this thread finds a solution, it stores its win array in the shared data.
     */
    public void run() {
        if (array.size() != 1)
            if (Thread.currentThread().getName().equals("thread1"))
                rec(array.size()-1, b - array.get(array.size() - 1));
            else
                rec(array.size()-1, b);
        if (array.size() == 1)
            if (b == array.get(0) && !flag) {
                winArray[0] = true;
                flag = true;
                synchronized (sd) { sd.setFlag(true); }
            }
        if (flag) {
            if (Thread.currentThread().getName().equals("thread1"))
                winArray[array.size() - 1] = true;
            synchronized (sd) { sd.setWinArray(winArray); }
        }
    }
}
