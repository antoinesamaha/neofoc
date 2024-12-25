/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.util.*;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FMultipleChoiceContainer extends HashMap implements Container {
	
  public void addChoice(int id, String title, Object obj) {
    FMultipleChoiceItem item = new FMultipleChoiceItem(id, title);
    super.put(Integer.valueOf(id), item);
  }

	@Override
	public Item getItem(Object itemId) {
		return (Item) get(itemId);
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<?> getItemIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property getContainerProperty(Object itemId, Object propertyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getType(Object propertyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsId(Object itemId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}
}
