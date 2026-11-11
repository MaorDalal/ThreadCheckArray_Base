import java.util.ArrayList;

/**
 * Holds the data shared between the worker threads that solve the
 * Subset Sum decision problem.
 * <p>
 * The input numbers are stored in an {@code ArrayList<Integer>}. The class also keeps
 * the target sum {@code b}, a global {@code flag} that tells all threads a solution
 * was found, and a {@code boolean[]} win array that marks which elements of the
 * list take part in the solution.
 * <p>
 * Access to the mutable state of an instance is synchronized by the callers
 * (see {@link ThreadCheckArray}), which lock on the shared instance itself.
 */
public class SharedData {
    /** The input numbers of the Subset Sum problem. */
    private ArrayList<Integer> array;

    /** Marks, for every index of {@link #array}, whether that element is part of the solution. */
    private boolean[] winArray;

    /** {@code true} once any thread has found a subset whose sum equals {@link #b}. */
    private boolean flag;

    /** The target sum that a subset of {@link #array} should reach. */
    private final int b;

    /**
     * Creates the shared data object for one run of the algorithm.
     * The win array is initialized to the size of the given list, with all entries {@code false}.
     *
     * @param array the input numbers, as an {@code ArrayList<Integer>}
     * @param b     the target sum to search for
     */
    public SharedData(ArrayList<Integer> array, int b) {
        this.array = array;
        this.b = b;
        this.winArray = new boolean[array.size()];
    }

    /**
     * Returns the array that marks which elements form the solution.
     *
     * @return the win array, one {@code boolean} per element of the input list
     */
    public boolean[] getWinArray() { return winArray; }

    /**
     * Stores the array that marks which elements form the solution.
     *
     * @param winArray the win array produced by the thread that found the solution
     */
    public void setWinArray(boolean[] winArray) { this.winArray = winArray; }

    /**
     * Returns the input numbers.
     *
     * @return the input numbers as an {@code ArrayList<Integer>}
     */
    public ArrayList<Integer> getArray() { return array; }

    /**
     * Returns the target sum.
     *
     * @return the target sum {@code b}
     */
    public int getB() { return b; }

    /**
     * Tells whether a solution has already been found by any thread.
     *
     * @return {@code true} if a subset with sum {@code b} was found
     */
    public boolean getFlag() { return flag; }

    /**
     * Sets whether a solution has been found.
     *
     * @param flag {@code true} when a thread has found a valid subset
     */
    public void setFlag(boolean flag) { this.flag = flag; }
}
