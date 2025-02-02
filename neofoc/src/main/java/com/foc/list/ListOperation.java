/*
 * Created on Dec 2, 2005
 */
package com.foc.list;

import com.foc.desc.FocObject;

/**
 * @author 01Barmaja
 */
public interface ListOperation {
  public void reset();
  public void treatObject(FocObject obj);
  public void sendResult();
  public void dispose();
}
