/*
 * Created on 01-Feb-2005
 */
package com.foc.depricatedUnit;

/**
 * @author 01Barmaja
 */
public class Karat implements Unit {

  public String getTitle() {
    return "Kilogram";
  }

  public String getName() {
    return "Kg";
  }

  public int getID() {
    return Unit.KG;
  }
}