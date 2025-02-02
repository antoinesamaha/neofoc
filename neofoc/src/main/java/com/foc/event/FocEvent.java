/*
 * Created on Nov 8, 2004
 */
package com.foc.event;

import java.awt.event.ActionEvent;

import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FocEvent /*extends ActionEvent*/ {
  private final static int EVENTS_TYPE_INTERVAL = 100;

  public final static int TYPE_LIST = ActionEvent.ACTION_LAST + 100;
  public final static int TYPE_OBJECT = TYPE_LIST + EVENTS_TYPE_INTERVAL;
  public final static int TYPE_PROPERTY = TYPE_OBJECT + EVENTS_TYPE_INTERVAL;
  public final static int TYPE_ORDER = TYPE_PROPERTY + EVENTS_TYPE_INTERVAL;

  public final static int ID_SAVE = 1;
  public final static int ID_DELETE = 2;
  public final static int ID_CANCEL = 3;
  public final static int ID_MODIFIED = 4;
  public final static int ID_BACKUP = 5;
  public final static int ID_RESTORE = 6;
  public final static int ID_BEFORE_LOAD = 7;
  public final static int ID_LOAD = 8;
  public final static int ID_ITEM_SELECT = 9;
  public final static int ID_ITEM_ADD = 10;
  public final static int ID_ITEM_REMOVE = 11;
  public final static int ID_ITEM_MODIFY = 12;
  public final static int ID_WAIK_UP_LIST_LISTENERS = 13;
  public final static int ID_NEW = 14;
  public final static int ID_SAVE_AFTER_PROPAGATION = 15;
  
  public final static int ID_TRANSACTION_APPROVE = 16;
  public final static int ID_TRANSACTION_CANCEL  = 17;
  public final static int ID_TRANSACTION_CLOSE   = 18;
  public final static int ID_BEFORE_REFERENCE_SET= 19;

  private Object    eventSubject    = null;
  private FProperty eventPropertySubject = null;
  private Object source  = null;
  private int    id      = 0;
  private String command = null;
  
  public FocEvent(Object source, int id, String command) {
    this.source = source;
    this.id = id;
    this.command = command;
  }

  public void dispose(){
  	eventSubject = null;
  	eventPropertySubject = null;
  	source = null;
  }
  
  static public int composeId(int eventType, int id) {
    return eventType + id;
  }

  public int getSourceType() {
    int newId = id - TYPE_LIST;
    if (newId < EVENTS_TYPE_INTERVAL) {
      return TYPE_LIST;
    }
    newId = id - TYPE_OBJECT;
    if (newId < EVENTS_TYPE_INTERVAL) {
      return TYPE_OBJECT;
    }
    newId = id - TYPE_PROPERTY;
    if (newId < EVENTS_TYPE_INTERVAL) {
      return TYPE_PROPERTY;
    }
    newId = id - TYPE_ORDER;
    if (newId < EVENTS_TYPE_INTERVAL) {
      return TYPE_ORDER;
    }
    return 0;
  }

  public int getID() {
    return id - getSourceType();
  }
  /**
   * @return Returns the eventSubject.
   */
  public Object getEventSubject() {
    return eventSubject;
  }
  /**
   * @param eventSubject The eventSubject to set.
   */
  public void setEventSubject(Object eventSubject) {
    this.eventSubject = eventSubject;
  }

	public FProperty getEventPropertySubject() {
		return eventPropertySubject;
	}

	public void setEventPropertySubject(FProperty eventPropertySubject) {
		this.eventPropertySubject = eventPropertySubject;
	}
}
