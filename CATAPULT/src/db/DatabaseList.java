/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package db;

import javax.swing.DefaultListModel;

/**
 *
 * @author zhouyong
 */
public class DatabaseList extends DefaultListModel {

	private Object[] removedObjects = new Object[0];

	@Override
	public void clear() {
		super.clear();
	}

	@Override
	public Object remove(int index) {
		Object obj = super.remove(index);
		if (obj != null) {
			removedObjects = new Object[] { obj };
		}
		return obj;
	}

	@Override
	public void removeAllElements() {
		throw new UnsupportedOperationException("Method: removeAllElements is not supported. Use clear()");
	}

	@Override
	public boolean removeElement(Object obj) {
		if (this.contains(obj)) {
			removedObjects = new Object[] { obj };
		}
		boolean removed = super.removeElement(obj);
		return removed;
	}

	@Override
	public void removeElementAt(int index) {
		throw new UnsupportedOperationException("Method: removeElementAt is not supported. Use remove(int index)");
	}

	@Override
	public void removeRange(int fromIndex, int toIndex) {
		removedObjects = new Object[toIndex - fromIndex + 1];
		for (int i = 0; i < removedObjects.length; i++) {
			removedObjects[i] = super.get(fromIndex + i);
		}
		super.removeRange(fromIndex, toIndex);
	}

	public Object[] getRemovedObjects() {
		return removedObjects;
	}

}
