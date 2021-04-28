
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;


/**Add descirption here**/
@SuppressWarnings("unchecked")
public class ModifiedPriorityQueue<E> extends AbstractQueue<E>
        implements java.io.Serializable {


    /**
     * Priority queue represented as a balanced binary heap: the two
     * children of queue[n] are queue[2*n+1] and queue[2*(n+1)].  The
     * priority queue is ordered by comparator, or by the elements'
     * natural ordering, if comparator is null: For each node n in the
     * heap and each descendant d of n, n <= d.  The element with the
     * lowest value is in queue[0], assuming the queue is nonempty.
     */
    transient Object[] queue; // non-private to simplify nested class access

    /**
     * The positions of each elements stored
     */
    transient HashMap<E,Integer> positions;


    /**
     * The number of elements in the priority queue.
     */
    int size;

    /**
     * The comparator, or null if priority queue uses elements'
     * natural ordering.
     */
    @SuppressWarnings("serial") // Conditionally serializable
    private final Comparator<? super E> comparator;

    /**
     * The number of times this priority queue has been
     * <i>structurally modified</i>.  See AbstractList for gory details.
     */
    transient int modCount;     // non-private to simplify nested class access


    /**
     TODO: write doc
     */
    public ModifiedPriorityQueue(int initialCapacity) {
        this(initialCapacity, (int) (initialCapacity*135f),0.75f,null);
    }


    public ModifiedPriorityQueue(int initialCapacity, Comparator<? super E> comparator) {
        this(initialCapacity, (int) (initialCapacity*135f),0.75f,comparator);
    }

    /**
     TODO: write doc
     */
    public ModifiedPriorityQueue(int queueCapacity, int hashMapCapacity, float hashMapLoadFactor,
                                 Comparator<? super E> comparator) {
        if (queueCapacity < 1)
            throw new IllegalArgumentException();
        this.queue = new Object[queueCapacity];
        this.positions = new HashMap<>(hashMapCapacity,hashMapLoadFactor);
        this.comparator = comparator;
    }

    /**
     * Inserts the specified element into this priority queue.
     *
     * @return {@code true} (as specified by {@link Collection#add})
     * @throws ClassCastException if the specified element cannot be
     *         compared with elements currently in this priority queue
     *         according to the priority queue's ordering
     * @throws NullPointerException if the specified element is null
     */
    public boolean add(E e) {
        return offer(e);
    }

    /**
     * Inserts the specified element into this priority queue.
     *
     * @return {@code true} (as specified by {@link Queue#offer})
     * @throws ClassCastException if the specified element cannot be
     *         compared with elements currently in this priority queue
     *         according to the priority queue's ordering
     * @throws NullPointerException if the specified element is null
     */
    public boolean offer(E e) {
        if (e == null)
            throw new NullPointerException();

        int i = size;

        if(contains(e))
            return false;

        // if the queue is full
        if (i >= queue.length) {
            final E p = peek();

            final Comparator<? super E> cmp;
            if ((cmp = comparator) == null)
            {
                //if(((Comparable<? super E>) e).compareTo(p) > 0) {
                    modCount++;
                    positions.remove(p);
                    siftDownComparable(0, e, queue, size, positions);
                    return true;
                //}
            }
            else {
                //if(cmp.compare(e, p) > 0) {
                    modCount++;
                    positions.remove(p);
                    siftDownUsingComparator(0,e,queue,size,cmp,positions);
                    return true;
                //}
            }
            //return false;
        }

        modCount++;


        siftUp(i, e);
        size = i + 1;
        return true;
    }

    public E peek() {
        return (E) queue[0];
    }

    private int indexOf(Object o) {
        return positions.getOrDefault(o, -1);
    }

    /**
     * Removes a single instance of the specified element from this queue,
     * if it is present.  More formally, removes an element {@code e} such
     * that {@code o.equals(e)}, if this queue contains one or more such
     * elements.  Returns {@code true} if and only if this queue contained
     * the specified element (or equivalently, if this queue changed as a
     * result of the call).
     *
     * @param o element to be removed from this queue, if present
     * @return {@code true} if this queue changed as a result of the call
     */
    public boolean remove(Object o) {
        int i = indexOf(o);
        if (i == -1)
            return false;
        else {
            removeAt(i);
            positions.remove(o);
            return true;
        }
    }


    /**
     * Returns {@code true} if this queue contains the specified element.
     * More formally, returns {@code true} if and only if this queue contains
     * at least one element {@code e} such that {@code o.equals(e)}.
     *
     * @param o object to be checked for containment in this queue
     * @return {@code true} if this queue contains the specified element
     */
    public boolean contains(Object o) {
        return positions.containsKey(o);
    }

    /**
     * Returns an array containing all of the elements in this queue.
     * The elements are in no particular order.
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this queue.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this queue
     */
    public Object[] toArray() {
        return Arrays.copyOf(queue, size);
    }

    /**
     * Returns an array containing all of the elements in this queue; the
     * runtime type of the returned array is that of the specified array.
     * The returned array elements are in no particular order.
     * If the queue fits in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this queue.
     *
     * <p>If the queue fits in the specified array with room to spare
     * (i.e., the array has more elements than the queue), the element in
     * the array immediately following the end of the collection is set to
     * {@code null}.
     *
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     *
     * <p>Suppose {@code x} is a queue known to contain only strings.
     * The following code can be used to dump the queue into a newly
     * allocated array of {@code String}:
     *
     * <pre> {@code String[] y = x.toArray(new String[0]);}</pre>
     *
     * Note that {@code toArray(new Object[0])} is identical in function to
     * {@code toArray()}.
     *
     * @param a the array into which the elements of the queue are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @return an array containing all of the elements in this queue
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this queue
     * @throws NullPointerException if the specified array is null
     */
    public <T> T[] toArray(T[] a) {
        final int size = this.size;
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(queue, size, a.getClass());
        System.arraycopy(queue, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    @Override
    public Iterator<E> iterator() {
        return (Iterator<E>) Arrays.stream(queue).iterator();
    }

    public void print() {

        System.out.println("Queue:");
        System.out.println(Arrays.toString(queue));


        System.out.println("Hashmap:");
        positions.forEach ( (val,o) ->
                System.out.println(val + " -> " + o)
        );

    }

    public int size() {
        return size;
    }

    /**
     * Removes all of the elements from this priority queue.
     * The queue will be empty after this call returns.
     */
    public void clear() {
        modCount++;
        final Object[] es = queue;
        for (int i = 0, n = size; i < n; i++)
            es[i] = null;
        size = 0;
    }

    public E poll() {
        final Object[] es;
        final HashMap<E, Integer> pos = positions;
        final E result;

        if ((result = (E) ((es = queue)[0])) != null) {
            pos.remove(result);
            modCount++;
            final int n;
            final E x = (E) es[(n = --size)];
            System.out.println("x=" + x);
            System.out.println("n=" + n);
            es[n] = null;
            if (n > 0) {
                final Comparator<? super E> cmp;
                if ((cmp = comparator) == null)
                    siftDownComparable(0, x, es, n, pos);
                else
                    siftDownUsingComparator(0, x, es, n, cmp, pos);
            }
        }
        return result;
    }

    /**
     * Removes the ith element from queue.
     *
     * Normally this method leaves the elements at up to i-1,
     * inclusive, untouched.  Under these circumstances, it returns
     * null.  Occasionally, in order to maintain the heap invariant,
     * it must swap a later element of the list with one earlier than
     * i.  Under these circumstances, this method returns the element
     * that was previously at the end of the list and is now at some
     * position before i. This fact is used by iterator.remove so as to
     * avoid missing traversing elements.
     */
    E removeAt(int i) {
        // assert i >= 0 && i < size;
        final Object[] es = queue;
        modCount++;
        int s = --size;
        if (s == i) // removed last element
            es[i] = null;
        else {
            E moved = (E) es[s];
            es[s] = null;
            siftDown(i, moved);
            if (es[i] == moved) {
                siftUp(i, moved);
                if (es[i] != moved)
                    return moved;
            }
        }
        return null;
    }

    /**
     * Inserts item x at position k, maintaining heap invariant by
     * promoting x up the tree until it is greater than or equal to
     * its parent, or is the root.
     *
     * To simplify and speed up coercions and comparisons, the
     * Comparable and Comparator versions are separated into different
     * methods that are otherwise identical. (Similarly for siftDown.)
     *
     * @param k the position to fill
     * @param x the item to insert
     */
    private void siftUp(int k, E x) {
        if (comparator != null)
            siftUpUsingComparator(k, x, queue, comparator, positions);
        else
            siftUpComparable(k, x, queue, positions);
    }

    private static <T> void siftUpComparable(int k, T x, Object[] es, HashMap<T, Integer> pos) {
        Comparable<? super T> key = (Comparable<? super T>) x;
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = es[parent];
            if (key.compareTo((T) e) >= 0)
                break;
            es[k] = e;
            pos.put((T) e,k);
            k = parent;
        }
        es[k] = key;
        pos.put((T) key,k);
    }

    private static <T> void siftUpUsingComparator(
            int k, T x, Object[] es, Comparator<? super T> cmp, HashMap<T, Integer> pos) {
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = es[parent];
            if (cmp.compare(x, (T) e) >= 0)
                break;
            es[k] = e;
            pos.put((T) e,k);
            k = parent;
        }
        es[k] = x;
        pos.put((T) x,k);
    }

    /**
     * Inserts item x at position k, maintaining heap invariant by
     * demoting x down the tree repeatedly until it is less than or
     * equal to its children or is a leaf.
     *
     * @param k the position to fill
     * @param x the item to insert
     */
    private void siftDown(int k, E x) {
        if (comparator != null)
            siftDownUsingComparator(k, x, queue, size, comparator, positions);
        else
            siftDownComparable(k, x, queue, size, positions);
    }

    private static <T> void siftDownComparable(int k, T x, Object[] es, int n, HashMap<T, Integer> pos) {
        // assert n > 0;
        Comparable<? super T> key = (Comparable<? super T>)x;
        int half = n >>> 1;           // loop while a non-leaf
        while (k < half) {
            int child = (k << 1) + 1; // assume left child is least
            Object c = es[child];
            int right = child + 1;
            if (right < n &&
                    ((Comparable<? super T>) c).compareTo((T) es[right]) > 0)
                c = es[child = right];
            if (key.compareTo((T) c) <= 0)
                break;
            es[k] = c;
            pos.put((T) c,k);
            k = child;
        }
        es[k] = key;
        pos.put((T) key,k);
    }

    private static <T> void siftDownUsingComparator(
            int k, T x, Object[] es, int n, Comparator<? super T> cmp, HashMap<T, Integer> pos) {
        // assert n > 0;
        int half = n >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = es[child];
            int right = child + 1;
            if (right < n && cmp.compare((T) c, (T) es[right]) > 0)
                c = es[child = right];
            if (cmp.compare(x, (T) c) <= 0)
                break;
            es[k] = c;
            pos.put((T) c,k);
            k = child;
        }
        es[k] = x;
        pos.put((T) x,k);
    }

    /**
     * Returns the comparator used to order the elements in this
     * queue, or {@code null} if this queue is sorted according to
     * the {@linkplain Comparable natural ordering} of its elements.
     *
     * @return the comparator used to order this queue, or
     *         {@code null} if this queue is sorted according to the
     *         natural ordering of its elements
     */
    public Comparator<? super E> comparator() {
        return comparator;
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        final int expectedModCount = modCount;
        final Object[] es = queue;
        for (int i = 0, n = size; i < n; i++)
            action.accept((E) es[i]);
        if (expectedModCount != modCount)
            throw new ConcurrentModificationException();
    }
}

