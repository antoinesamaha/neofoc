/*
 * Created on 13-Mar-2005
 */
package com.foc.desc;

import java.lang.reflect.*;

import com.foc.*;

/**
 * @author 01Barmaja
 */
public class FocConstructor {
	private FocDesc focDesc = null;
	private Object identifierValue = null;
	private FocObject masterObject = null;

	public void init(FocDesc focDesc, Object identifierValue, FocObject masterObject) {
		this.identifierValue = identifierValue;
		this.masterObject = masterObject;
		this.focDesc = focDesc;
	}

	public FocConstructor(FocDesc focDesc, Object identifierValue, FocObject masterObject) {
		init(focDesc, identifierValue, masterObject);
	}

	public FocConstructor(FocDesc focDesc, FocConstructor fatherConstructor, FocObject masterObject) {
		init(focDesc, null, masterObject);
	}

	public FocConstructor(FocDesc focDesc, FocObject masterObject) {
		init(focDesc, null, masterObject);
	}

	public FocConstructor(FocDesc focDesc) {
		init(focDesc, null, null);
	}

	public void dispose() {
		focDesc = null;
		identifierValue = null;
		masterObject = null;
	}

	@SuppressWarnings("unchecked")
	public FocObject newItem() {
		FocObject focObject = null;
		Class objectClass = null;

		try {
			if (focDesc != null) {
				objectClass = focDesc.getFocObjectClass();
			}

			if (objectClass != null) {
				Class[] classes = new Class[1];
				classes[0] = FocConstructor.class;

				Object[] objects = new Object[1];
				objects[0] = this;

				Constructor construct = objectClass.getConstructor(classes);
				if (construct != null) {
					focObject = (FocObject) construct.newInstance(objects);
				} else {
//					classes = new Class[0];
//					construct = objectClass.getConstructor(classes);
//					if(construct != null)
//					objects = new Object[0];
//					focObject = (FocObject) construct.newInstance(objects);
				}
			}
		} catch (Exception e) {
			Globals.logException(e);
			if (objectClass != null) {
				Globals.logString("For Class:" + objectClass.getName());
			}
		}
		return focObject;
	}

	/**
	 * @return Returns the identifier.
	 */
	public Object getIdentifierValue() {
		return identifierValue;
	}

	/**
	 * @return Returns the masterObject.
	 */
	public FocObject getMasterObject() {
		return masterObject;
	}

	public FocDesc getFocDesc() {
		return focDesc;
	}

	public static <O> O newFocObject(FocDesc focDesc) {
		FocConstructor constr = new FocConstructor(focDesc, null);
		return (O) constr.newItem();
	}
}
