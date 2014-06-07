package com.example.serverconnect;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;

public class Factory<K> implements Iterable<K>{

	private Set<K> idle = new HashSet<K>();
	private Set<K> inUse = new HashSet<K>();
	private int max_number_of_Ks;
	private int number_of_Ks_created = 0;

	public Factory(int max_number_of_Ks) {
		Assert.assertTrue(max_number_of_Ks!=0);
		this.max_number_of_Ks = max_number_of_Ks;
	}

	public void addToFactory(K obj) {
		Assert.assertTrue(number_of_Ks_created < max_number_of_Ks);
		number_of_Ks_created++;
		inUse.add(obj);
	}

	public void recycle(K obj) {
		Assert.assertTrue(inUse.contains(obj));
		if (inUse.remove(obj)) {
			idle.add(obj);
		}
	}
	
	public void remove(K obj) {
		if(!inUse.remove(obj)) {
			idle.remove(obj);
		}
	}

	public boolean idleIsEmpty() {
		return idle.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public K getAndUseAnIdle() {
		K object = (K) (idle.toArray())[0];
		idle.remove(object);
		inUse.add(object);
		return object;
	}

	public boolean isFull() {
		return number_of_Ks_created >= max_number_of_Ks;
	}

	@Override
	public Iterator<K> iterator() {
		return new KIterator(number_of_Ks_created);
	}
	
	private class KIterator implements Iterator<K> {
		
		K[] idleArray;
		K[] inUseArray;
		private int number_of_elements_seen = 0;
		private int number_of_elements;
		
		@SuppressWarnings("unchecked")
		public KIterator(int number_of_elements) {
			idleArray = (K[]) idle.toArray();
			inUseArray = (K[]) inUse.toArray();
			this.number_of_elements = number_of_elements;
		}

		@Override
		public boolean hasNext() {
			return number_of_elements > number_of_elements_seen;
		}

		@Override
		public K next() {
			K obj;
			int index = idleArray.length==0 ? number_of_elements_seen : number_of_elements_seen % idleArray.length;
			if(number_of_elements_seen < idleArray.length) {
				obj = idleArray[index];
			}
			else {
				obj = inUseArray[index];
			}
			number_of_elements_seen++;
			return obj;
		}

		@Override
		public void remove() {
			Assert.assertTrue(false);	//this method should not be called.
		}
		
	}
}
