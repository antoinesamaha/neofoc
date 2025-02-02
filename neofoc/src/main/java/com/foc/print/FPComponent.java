/*
 * Created on 28-Apr-2005
 */
package com.foc.print;

import java.awt.*;
import java.util.*;

/**
 * @author 01Barmaja
 */
public abstract class FPComponent implements FPInterface {

  private FPPos position = null;
  private double totalXTranslation = 0;
  private double totalYTranslation = 0;
  private int xAllignment = 0;
  private int xLimit = 0;
  private int yLimit = 0;
  private ArrayList pageSatus = null;
  private boolean finished = false;
  /*
  private int pageMin = -9;
  private int pageMax = -9;
  */
  
  private BorderSetup borderSetup = null;
  
  private Font font = null;
  private Font fontBackup = null;
  
  private Color color = null;
  private Color colorBackup = null;
  
  public final static int LEFT = 1; 
  public final static int RIGHT = 2;
  public final static int CENTER = 3;
  
  public abstract FPDimension getDimension(PrintingData data);
  public abstract ComponentPrintInfo printInt(Graphics graphics, PrintingData data, int pageIndex, int xLimit, int yLimit);
    
  public FPComponent(){
    position = new FPPos();
    pageSatus = new ArrayList();
    font = FPReport.getDefaultFont();
  }
  
  public ComponentPrintInfo print(Graphics graphics, PrintingData data, int pageIndex, int xLimit, int yLimit){
    ComponentPrintInfo info = new ComponentPrintInfo();
    if(!pageExcluded(pageIndex)){
      info = printInt(graphics, data, pageIndex, xLimit, yLimit);
      if(!info.isNeedsMorePages()){
        finished = true;
      }
    }
    return info;
  }
  
  public void includePage(int page){
    while(page >= pageSatus.size()){
      pageSatus.add(Integer.valueOf(-1));
    }
    pageSatus.set(page, Integer.valueOf(1));
  }
  
  public boolean pageExcluded(int page){
    boolean ex = false;
    if(page < pageSatus.size()){
      Integer integ = (Integer) pageSatus.get(page);
      ex = integ.intValue() == 0;
    }else{
      ex = finished;
    }
    return ex;
  }
 
  /**
   * @return Returns the position.
   */
  public FPPos getPos() {
    return position;
  }
  
  public void resetTranslation(Graphics2D g2){
    g2.translate((double) -totalXTranslation, (double) -totalYTranslation);
    totalXTranslation = 0;
    totalYTranslation = 0;
  }
  
  public void translate(Graphics2D g2, int x, int y){
    g2.translate((double)x, (double)y);
    totalXTranslation += x;
    totalYTranslation += y;
    xLimit = xLimit - x;
    yLimit = yLimit - y;    
  }

  /**
   * @return Returns the xAllignment.
   */
  public int getXAllignment() {
    return xAllignment;
  }
  
  /**
   * @param allignment The xAllignment to set.
   */
  public void setXAllignment(int allignment) {
    xAllignment = allignment;
  }
  
  /**
   * @return Returns the borderSetup.
   */
  public BorderSetup getBorderSetup() {
    return borderSetup;
  }
  
  /**
   * @param borderSetup The borderSetup to set.
   */
  public void setBorderSetup(BorderSetup borderSetup) {
    this.borderSetup = borderSetup;
  }
  
  /**
   * @return Returns the xLimit.
   */
  public int getXLimit() {
    return xLimit;
  }
  
  /**
   * @return Returns the yLimit.
   */
  public int getYLimit() {
    return yLimit;
  }
  
  /**
   * @param limit The xLimit to set.
   */
  public void setXLimit(int limit) {
    xLimit = limit;
  }
  
  /**
   * @param limit The yLimit to set.
   */
  public void setYLimit(int limit) {
    yLimit = limit;
  }
  
  /*
  public int getPage() {
    return page;
  }/*
  
  /**
   * @param page The page to set.
   */
  /*
  public void setPage(int page) {
    this.page = page;
  }*/


  public void setFont(Font font){
    this.font = font;
    if(this.font == null){
      this.font = FPReport.getDefaultFont();
    }
  }

  public Font getFont(){
    return font;
  }
  
  public void setColor(Color color){
    this.color = color;
  }

  public Color getColor(){
    return color;
  }
  
  public void placeAppearance(Graphics g){
    Font currFont = g.getFont();
    if(currFont != font && font != null){
      fontBackup = currFont;
      g.setFont(font);
    }
    
    Color currColor = g.getColor();
    if(currColor != color && color != null){
      colorBackup = currColor;
      g.setColor(color);
    }
  }
  
  public void resetAppearance(Graphics g){
    Font currFont = g.getFont();
    if(currFont != fontBackup && fontBackup != null){
      g.setFont(fontBackup);
    }
    
    Color currColor = g.getColor();
    if(currColor != colorBackup && colorBackup != null){
      g.setColor(colorBackup);
    }    
  }

  private Color backgroundColor = null;
  private Color backgroundColorBackup = null;

  public void setBackgroundColor(Color color){
    this.backgroundColor = color;
  }

  public Color getBackgroundColor(){
    return backgroundColor ;
  }
}
