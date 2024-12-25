/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
// Printable
// Pageable
  
/*
 * Created on 28-Apr-2005
 */
package com.foc.print;

import java.awt.*;
import java.awt.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.OrientationRequested;

import com.foc.*;

/**
 * @author 01Barmaja
 */
public class FPReport implements Printable{

  private static Font defaultFont = null;
  
  FPComponent container = null;
  PrintingData data = null;
  int maxPage = -9;
  private PageFormat pageFormat = null;
  private Book book = null;
  private int pageCount = 0;
  
  public FPReport(FPComponent container, PrintingData data){
    this.container = container;
    this.data = data;    
  }
  
  public static Font getDefaultFont(){
    if(defaultFont == null){
      defaultFont = new Font("ARIAL", Font.PLAIN, 12);
    }
    return defaultFont ;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // Printable 
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo  

  public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
    if(maxPage >=0 && pageIndex > maxPage){
      return NO_SUCH_PAGE; 
    }
    
    Graphics2D g2 = (Graphics2D) graphics;
    g2.setColor(Color.black);
    
    data.setGraphics((Graphics2D) graphics);
    g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());    
    boolean needMorePages = false; 
    ComponentPrintInfo cInfo = container.print(graphics, data, pageIndex, (int)pageFormat.getImageableWidth(), (int)pageFormat.getImageableHeight());
    if(!cInfo.isNeedsMorePages()){
      maxPage = pageIndex; 
    }
    return PAGE_EXISTS; 
  }
  
  public int countPages(Graphics2D g, PageFormat pf){
    pageCount = 0;
    
    boolean pageExist = true;
    data.setDoNotPrint(true);
    while(pageExist){
      try{       
        pageExist = print((Graphics) g, pf, pageCount) == PAGE_EXISTS; 
        if(pageExist) pageCount++;
      } catch (Exception e){
        Globals.logException(e);
      }
    }
    data.setDoNotPrint(false);    
    return pageCount;
  }
  
  public int getPageCount(){
    return pageCount;
  }  
  
  private Book getBook(){
    //if(book == null){
    //  book = new Book();
      Graphics2D g2D = (Graphics2D) Globals.getDisplayManager().getMainFrame().getGraphics();
      Graphics2D g2DCopy = (Graphics2D) g2D.create();
      int expectedNbOfPages = countPages(g2D, pageFormat);
    //  book.append(this, pageFormat, expectedNbOfPages);
      //pageCount = 2;
    //}
    //return book;
      return null;
  }
  
  public void launchPrinting(int orientation){
    PrinterJob pj = PrinterJob.getPrinterJob();
    if(pageFormat == null){
      pageFormat = pj.defaultPage();
      //debug_printPageFormatInfo(pageFormat);
    }    
    pj.setPrintable(this);
    //pj.setPageable(getBook());
    
    HashPrintRequestAttributeSet attribSet = new HashPrintRequestAttributeSet();
    
    if(orientation >= 0){
      switch(orientation){
      case PageFormat.LANDSCAPE:
        attribSet.add(OrientationRequested.LANDSCAPE);
        break;  
      case PageFormat.PORTRAIT:
        attribSet.add(OrientationRequested.PORTRAIT);
        break;  
      case PageFormat.REVERSE_LANDSCAPE:
        attribSet.add(OrientationRequested.REVERSE_LANDSCAPE);
        break;
      }
    }
    
    if(pj.printDialog(attribSet)){
      /*
      Attribute a[] = attribSet.toArray();
      for(int i=0; i<a.length; i++){
        Globals.logString("Name = "+a[i].getName()+"Class = "+a[i].getClass());
      }    
      */
      try {
        pj.print(attribSet);
      } catch (Exception printException) {
        Globals.logException(printException);
      }
    }   
  }
  
  public void launchPrinting(){
    launchPrinting(-1);
  }

  public void launchPreviews(int orientation){
    if(pageFormat == null){
      PrinterJob pj = PrinterJob.getPrinterJob();        
      pageFormat = pj.defaultPage();
      if(orientation >= 0){
        pageFormat.setOrientation(orientation);        
      }
      //debug_printPageFormatInfo(pageFormat);
    }
    
    Book book = getBook();
    //if(book != null){
      PrintPreviewPanel printPreview = new PrintPreviewPanel(this);
      Globals.getDisplayManager().popupDialog(printPreview, "Print Preview", true);
    //}
  }

  public void launchPreviews(){
    launchPreviews(-1);
  }

  public PageFormat getPageFormat() {
    return pageFormat;
  }
  
  public static void debug_printPageFormatInfo(PageFormat pageFormat){
    Globals.logString(pageFormat.toString());
    Globals.logString("Imagable width = "+pageFormat.getImageableWidth()+" Imagable height = "+pageFormat.getImageableHeight() );
    Globals.logString("Imagable x = "+pageFormat.getImageableX()+" Imagable y = "+pageFormat.getImageableY() );    
  }
}
