import java.util.ArrayList;

/**
 * A {@link Runnable} task that searches, using recursion, for a subset of the
 * numbers stored in a {@code ArrayList<Integer>} whose sum equals a target value {@code b}.
 * <p>
 * Two threads (named {@code "thread1"} and {@code "thread2"}) share a single
 * {@link SharedData} instance. Each thread explores a different branch of the
 * recursion tree: {@code "thread1"} assumes the last element is part of the
 * subset, while {@code "thread2"} assumes it is not. When one thread finds a
 * solution it raises the shared flag so the other thread stops searching.
 * Access to the shared object is guarded by {@code synchronized} blocks.
 */
public class ThreadCheckArray implements Runnable {

    /** {@code true} if this thread has found a valid subset. */
    private boolean flag;

    /** Marks which elements belong to the subset found by this thread. */
    private boolean[] winArray;

    /** The data object shared between all searching threads. */
    SharedData sd;

    /** The numbers to search, stored as a {@code ArrayList<Integer>}. */
    ArrayList<Integer> array;

    /** The target sum. */
    int b;

    /**
     * Creates a new search task bound to the given shared data.
     * The {@code boolean[]} result array is sized dynamically using
     * {@code array.size()}.
     *
     * @param sd the shared data containing the {@code ArrayList<Integer>} of numbers
     *           and the target sum
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
     * Recursively checks whether a subset of the first {@code n} elements
     * sums to {@code b}. Elements that belong to the found subset are marked
     * in {@code winArray}. The search stops early if another thread has
     * already found a solution.
     *
     * @param n the number of elements (from the start of the list) to consider
     * @param b the remaining sum that must be reached
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
     * Runs the search. {@code "thread1"} includes the last element in the
     * subset, any other thread excludes it. If a solution is found, the
     * result {@code boolean[]} is published to the shared data.
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
