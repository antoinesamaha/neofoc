/*
 * Created on 01-Feb-2005
 */
package com.foc.depricatedUnit;

/**
 * @author 01Barmaja
 */
public class MeterUnit implements Unit {

  public String getTitle() {
    return "Meter";
  }

  public String getName() {
    return "m";
  }

  public int getID() {
    return Unit.METER;
  }
}