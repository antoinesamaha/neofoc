/*
 * Created on 10-Jun-2005
 */
package com.foc.desc;

/**
 * @author 01Barmaja
 */
public interface ReferenceChecker extends Cloneable {
	public Object  clone() throws CloneNotSupportedException;
  //public int     getNumberOfReferences(FocObject obj, StringBuffer message);
	public void    deleteObject(FocObject focObjectToRedirectFrom);
  public void    redirectReferencesToNewFocObject(FocObject focObjectToRedirectFrom, FocObject focObjectToRedirectTo);
  //public FocList getLoadedFocList();
  public boolean isPutToZeroWhenReferenceDeleted();
  public boolean isDeleteWhenReferenceDeleted();
  
  public FocDesc getFocDesc();
  public int     getObjectFieldID();
  
  public boolean applyForThisObject(FocObject focObj);
  public void    setVoidObject(Object Obj);
  public Object  getVoidObject();
  public void    dispose_Content();
}
