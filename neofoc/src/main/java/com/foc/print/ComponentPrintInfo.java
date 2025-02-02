/*
 * Created on Jul 28, 2005
 */
package com.foc.print;

/**
 * @author 01Barmaja
 */
public class ComponentPrintInfo {
  private boolean needsMorePages = false;
  private FPDimension dim = null;
  
  public ComponentPrintInfo(){
    
  }
  
  public ComponentPrintInfo(boolean needsMorePages, FPDimension dim){
    this.needsMorePages = needsMorePages;
    this.dim = dim;
  }
  
  public FPDimension getDim() {
    return dim;
  }
  
  public void setDim(FPDimension dim) {
    this.dim = dim;
  }
  
  public void addHeight(int height){
    if(dim == null) dim = new FPDimension();
    dim.setHeight(dim.getHeight()+height);
  }

  public void addWidth(int width){
    if(dim == null) dim = new FPDimension();
    dim.setWidth(dim.getWidth()+width);
  }

  public void setHeight(int height){
    if(dim == null) dim = new FPDimension();
    dim.setHeight(height);
  }

  public void setWidth(int width){
    if(dim == null) dim = new FPDimension();
    dim.setWidth(width);
  }
  
  public int getHeight(){
    return dim != null ? dim.getHeight() : 0;
  }

  public int getWidth(){
    return dim != null ? dim.getWidth() : 0;
  }
  
  public boolean isNeedsMorePages() {
    return needsMorePages;
  }
  
  public void setNeedsMorePages(boolean needsMorePages) {
    this.needsMorePages = needsMorePages;
  }
}
