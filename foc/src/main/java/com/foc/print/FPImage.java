/*
 * Created on 29-Apr-2005
 */
package com.foc.print;

import java.awt.*;

import javax.swing.*;

import com.foc.*;

/**
 * @author 01Barmaja
 */
public class FPImage extends FPComponent{
  private ImageIcon image = null;
  private FPDimension dim = null;
  
  private void init(String fileName, int xAllignment, Font font, Color color, Color backgroundColor, BorderSetup borderSetup){
    this.image = Globals.getApp().getFocIcons().getAppIcon(fileName);
    dim = new FPDimension();
    setXAllignment(xAllignment);
    setFont(font);
    setColor(color);
    setBackgroundColor(backgroundColor);
    setBorderSetup(borderSetup);
  }
  
  public FPImage(String fileName){
    init(fileName, LEFT, null, null, null, null);
  }
  
  //FPComponent
  public FPDimension getDimension(PrintingData data) {
    dim.setHeight((int) image.getIconHeight());
    dim.setWidth((int) image.getIconWidth());
    return dim;
  }
  
  //FPComponent
  public ComponentPrintInfo printInt(Graphics graphics, PrintingData data, int pageIndex, int xLimit, int yLimit) {
    Graphics2D g2 = (Graphics2D) graphics;
    FPDimension dim = getDimension(data);
    translate(g2, 0, dim.getHeight());
    if(!data.isDoNotPrint()){
      placeAppearance(g2);
      g2.drawImage(image.getImage(), 0, -dim.getHeight(), dim.getWidth(), dim.getHeight(), g2.getBackground(), null);
      resetAppearance(g2);
    }
    resetTranslation(g2);
    includePage(pageIndex);
    return new ComponentPrintInfo(false, dim);
  }
}
