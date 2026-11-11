# ThreadCheckArray – Multithreaded Subset Sum

A Java project that solves the **Subset Sum** decision problem with recursion and two cooperating threads.
Given a list of integers and a target value `b`, the program decides whether some subset of the numbers sums exactly to `b`, and if so prints which elements form the solution.

Developed as Lab Exercise 2 in *Software Engineering Methods*, Department of Software Engineering, Braude College of Engineering.

---

## 1. The Algorithm: Recursive Multithreaded Subset Sum

### Recursion
`ThreadCheckArray.rec(n, b)` looks at the first `n` elements of the list. For the last of them (index `n-1`) there are two choices:

1. **Take it** – continue with `rec(n-1, b - array.get(n-1))`.
2. **Skip it** – continue with `rec(n-1, b)`.

The base case is `n == 1`: a solution exists if the remaining sum is `0` or equals the first element.
When a branch that took element `n-1` succeeds, that element is marked in the thread's `boolean[] winArray`.

### Splitting the work between two threads
`TestThreadCheckArray` starts two threads that share one `SharedData` object:

| Thread | Decision about the last element | First call |
|---|---|---|
| `thread1` | includes it | `rec(size-1, b - array.get(size-1))` |
| `thread2` | excludes it | `rec(size-1, b)` |

Each thread therefore explores half of the search tree.

### Thread synchronization
- `SharedData` holds a shared `flag`. The first thread to find a solution sets it to `true`.
- Every read and write of the shared object happens inside a `synchronized (sd)` block, so the threads never see a half-updated state.
- Both threads check the flag before each recursive step and stop early once the other thread has succeeded.
- The thread that found the solution stores its `winArray` in `SharedData`; `main` waits for both threads with `join()` and then prints the result.

### Example
```text
Enter array size
4
Enter numbers for array
1 2 3 7
Enter number
5
Solution for b : 5,n = 4
I:    0    1    2    3
A:    1    2    3    7
C:    0    1    1    0
```
Row `C` marks the solution: elements `2` and `3` (indexes 1 and 2) sum to `5`.
If no subset exists, the program prints `Sorry`.

---

## 2. Data Structure Refactoring: `int[]` to `ArrayList<Integer>`

The original code stored the input in a primitive array (`int[] array`). It was refactored to the generic collection `ArrayList<Integer>`:

| Before | After |
|---|---|
| `int[] array` | `ArrayList<Integer> array` |
| `array[i]` | `array.get(i)` |
| `array.length` | `array.size()` |
| `new boolean[array.length]` | `new boolean[array.size()]` |

- Only the field named `array` changed type. The boolean arrays (`winArray`) stay primitive `boolean[]` and are sized with `array.size()`.
- `SharedData`'s constructor and `getArray()` now take and return `ArrayList<Integer>`, and both classes import `java.util.ArrayList`.
- All `synchronized` blocks were kept unchanged.

---

## 3. Code Documentation with Javadoc

Every class, field, constructor and method is documented with Javadoc comments.
Generic types inside Javadoc are written as `{@code ArrayList<Integer>}`, because raw angle brackets such as `<Integer>` are read as HTML tags and make the `javadoc` tool fail with *malformed HTML* errors.

To generate the HTML documentation in IntelliJ IDEA:
**Tools → Generate JavaDoc… → Output directory: `docs` (in the project root) → Whole project → OK**, then open `docs/index.html` in a browser.
The `docs/` folder is listed in `.gitignore`, so generated files are not committed.

---

## 4. Authors and Git Collaboration Roles

| Role | Name | GitHub | Responsibilities |
|---|---|---|---|
| Student A | Maor Dalal | [@MaorDalal](https://github.com/MaorDalal) | Created the local and remote repository, initial commit, refactored `SharedData.java` and `ThreadCheckArray.java` on `main`, wrote this README |
| Student B | Neta Baron | [@NetaBaron](https://github.com/NetaBaron) | Cloned the repository, created the branch `feature/arraylist-refactor-b`, refactored `TestThreadCheckArray.java` and `ThreadCheckArray.java` with her own AI prompt, merged the branch into `main` and resolved the merge conflict with IntelliJ's 3-way merge tool |

### Git workflow
1. Student A initialised the repository on `main` and pushed it to GitHub.
2. Student B cloned it and worked on `feature/arraylist-refactor-b`.
3. Both students edited `ThreadCheckArray.java` at the same time on different branches.
4. Merging the feature branch into `main` produced a merge conflict, resolved with the 3-way merge tool.
5. The merged `main` was pushed and pulled by both students.

---

## Project Structure
```text
ThreadCheckArray_Base/
├── .gitignore
├── README.md
└── src/
    ├── SharedData.java            shared state: numbers, target, flag, win array
    ├── ThreadCheckArray.java      Runnable worker with the recursive search
    └── TestThreadCheckArray.java  main: reads input, runs the threads, prints the result
```

## Requirements
- IntelliJ IDEA 2026.2
- BellSoft Liberica Full JDK 25 LTS

## How to Run
Open the project in IntelliJ IDEA, open `src/TestThreadCheckArray.java` and run `main()`. Enter the array size, the numbers, and the target sum.

---

## 5. License

This project is licensed under the **Braude College Academic License**.
It was developed for instructional use in the Department of Software Engineering at Braude College of Engineering. Distribution, publishing, or sharing outside the official course boundary is prohibited.
