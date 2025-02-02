/*
 * Created on 28-Apr-2005
 */
package com.foc.print;

import java.awt.Graphics;

/**
 * @author 01Barmaja
 */
public interface FPInterface {
  public FPDimension getDimension(PrintingData data);
  public ComponentPrintInfo printInt(Graphics graphics, PrintingData data, int pageIndex, int xLimit, int yLimit);
}
