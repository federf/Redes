package redes;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * 
 * @author
 */
public class SyncQueue<E> {

	private PriorityQueue<E> q;
	private int length;

	public SyncQueue(int length, Comparator<E> comp) {
		q = new PriorityQueue<E>(length, comp);
	}

	public synchronized int getLength() {
		return length;
	}

	public synchronized boolean isEmpty() {
		return q.isEmpty();
	}

	public synchronized void add(E x) {
		q.add(x);
	}

	public synchronized E remove() {
		return q.remove();
	}

	public synchronized E peek() {
		return q.peek();
	}

}