/*
 * Created on 01-Feb-2005
 */
package com.foc.depricatedUnit;

/**
 * @author 01Barmaja
 */
public class HourUnit implements Unit {

  public String getTitle() {
    return "Hour";
  }

  public String getName() {
    return "Hr";
  }

  public int getID() {
    return Unit.HOUR;
  }
}