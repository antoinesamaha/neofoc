/*
 * Created on 01-Feb-2005
 */
package com.foc.depricatedUnit;

/**
 * @author 01Barmaja
 */
public interface Unit {
  public static final int SQM = 1;
  public static final int LM = 2;
  public static final int NB = 3;
  public static final int CUM = 4;
  public static final int HOUR = 5;
  public static final int KG = 6;
  public static final int KR = 7;
  public static final int GRAM = 8;
  public static final int METER = 9;
  public static final int TON = 10;
  
  public static final int INT_FIELD_SIZE = 5;

  public static final int USER_CODE_START = 1000;

  public String getTitle();

  public String getName();

  public int getID();
}
