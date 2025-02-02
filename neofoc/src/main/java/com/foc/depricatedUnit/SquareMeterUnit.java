/*
 * Created on 01-Feb-2005
 */
package com.foc.depricatedUnit;

/**
 * @author 01Barmaja
 */
public class SquareMeterUnit implements Unit {

  public String getTitle() {
    return "Square meter";
  }

  public String getName() {
    return "Sqm";
  }

  public int getID() {
    return Unit.SQM;
  }
}