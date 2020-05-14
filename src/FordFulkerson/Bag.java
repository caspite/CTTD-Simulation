/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FordFulkerson;

/**
 *
 * @author Sofi
 */
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  The <tt>Bag</tt> class represents a bag (or multiset) of
 *  generic items. It supports insertion and iterating over the
 *  items in arbitrary order.
 *  <p>
 *  The <em>add</em>, <em>isEmpty</em>, and <em>size</em>  operation
 *  take constant time. Iteration takes time proportional to the number of items.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/13stacks">Section 1.3</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */
public class Bag<Item> implements Iterable<Item> {

    private int N;         // number of elements in bag
    private Node first;    // beginning of bag

    // helper linked list class
    public class Node {

        private Item item;
        private Node next;
    }

    /**
     * Create an empty stack.
     */
    public Bag() {
        first = null;
        N = 0;
    }

    /**
     * Is the BAG empty?
     */
    public boolean isEmpty() {
        return first == null;
    }

    @Override
    public Bag<Item> clone() {
        Bag<Item> b = new Bag<Item>();
        for (Item e : this) {
            b.add(e);
        }
        return b;
    }

    /**
     * Return the number of items in the bag.
     */
    public int size() {
        return N;
    }

    /**
     * Add the item to the bag.
     */
    public void add(Item item) {
        Node oldfirst = first;
        first = new Node();
        first.item = item;
        first.next = oldfirst;
        N++;
    }

    public void clear() {
        first = null;
        N = 0;
    }

    /**
     * Return an iterator that iterates over the items in the bag.
     */
    public Iterator<Item> iterator() {
        return new ListIterator();
    }

    // an iterator, doesn't implement remove() since it's optional
    private class ListIterator implements Iterator<Item> {

        private Node current = first;
        private Node previous = null;
        private Node beforePrevious = null;

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {

            if (previous == null) {
                throw new NoSuchElementException();
            }else if(beforePrevious==null){
                first = first.next;
                N--;
            } else  {
                beforePrevious.next = current;
                N--;
            }
        }

        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            beforePrevious = previous;
            previous = current;
            Item item = current.item;
            current = current.next;
            return item;
        }
    }
}
