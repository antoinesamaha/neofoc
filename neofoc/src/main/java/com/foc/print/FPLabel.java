/*
 * Created on 29-Apr-2005
 */
package com.foc.print;

import java.awt.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;

import com.foc.*;
import com.foc.desc.*;
import com.foc.property.*;

import java.io.*;

/**
 * @author 01Barmaja
 */
public class FPLabel extends FPComponent implements Cloneable{
  private String text = "";
  private FPDimension dim = null;
  private int charPerLine = 0;
  private TextLine lines[] = null;
  
  protected void init(String text, int xAllignment, Font font, Color color, Color backgroundColor, BorderSetup borderSetup, int charPerLine){
    this.text = text;
    dim = new FPDimension();
    setXAllignment(xAllignment);
    setFont(font);
    setColor(color);
    setBackgroundColor(backgroundColor);
    setBorderSetup(borderSetup);
    this.charPerLine = charPerLine;
  }
  
  protected Object clone() throws CloneNotSupportedException {
    FPLabel label = (FPLabel) super.clone();
    label.init(text, getXAllignment(), getFont(), getColor(), getBackgroundColor(), getBorderSetup(), 0);
    return label;
  }

  public FPLabel newLabel(String text){
    FPLabel newLabel = null;
    try{
      newLabel = (FPLabel) clone();
      newLabel.text = text;
    }catch(Exception e){
      Globals.logException(e);
    }
    return newLabel;
  }

  public FPLabel newLabel(FProperty prop, Format fmt){
    return newLabel((String) prop.getTableDisplayObject(fmt));
  }

  public FPLabel newLabel(FocObject obj, int id, Format fmt){
    FProperty prop = obj.getFocProperty(id);
    return newLabel(prop, fmt);
  }

  public FPLabel newLabel(FProperty prop){
    return newLabel((String) prop.getTableDisplayObject(null));
  }

  public FPLabel newLabel(FocObject obj, int id){
    FProperty prop = obj.getFocProperty(id);
    return newLabel(prop, null);
  }
  
  public FPLabel(String text){
    init(text, LEFT, null, null, null, null, 0);
  }

  public FPLabel(String text, int xAllignment){
    init(text, xAllignment, null, null, null, null, 0);
  }

  public FPLabel(String text, int xAllignment, Font font){
    init(text, xAllignment, font, null, null, null, 0);
  }

  public FPLabel(String text, int xAllignment, Font font, int charPerLine){
    init(text, xAllignment, font, null, null, null, charPerLine);
  }
  
  public FPLabel(String text, int xAllignment, Font font, Color color, Color backgroundColor){
    init(text, xAllignment, font, color, backgroundColor, null, 0);
  }

  public FPLabel(String text, int xAllignment, Font font, Color color, Color backgroundColor, BorderSetup borderSetup){
    init(text, xAllignment, font, color, backgroundColor, borderSetup, 0);
  }
  
  private class TextLine{
    String text = null;
    FPDimension dim = null;
    
    public TextLine(String text){
      this.text = text;
      dim = null;      
      buildLines();
    }
    
    public FPDimension getDimension(PrintingData data){
      if(/*dim == null && */data != null){
        dim = new FPDimension();
        Rectangle2D rect = data.getGraphics().getFontMetrics().getStringBounds(text, data.getGraphics());
        
        dim.setHeight((int) Math.ceil(rect.getHeight()));
        dim.setWidth((int) Math.ceil(rect.getWidth()));      
      }
      return dim;
    }
  }  
  
  public void buildLines(){
    if(lines == null){
      if(charPerLine <=0 || text.length() <= charPerLine){
        lines = new TextLine[1];
        lines[0] = new TextLine(text);
      }else{
        ArrayList a = new ArrayList();
        StringBuffer buff = new StringBuffer();
       
        StringTokenizer st = new StringTokenizer(text, " \n", true);
        while (st.hasMoreTokens()) {
          String token = st.nextToken();
             
          if(token.compareTo("\n") == 0){
            a.add(buff);
            buff = new StringBuffer();
          }else if((buff.length() + token.length() + 1) <= charPerLine){
            if(buff.length() > 0) buff.append(" ");
            buff.append(token);
          }else{
            if(token.length() > charPerLine){
              if(buff.length() > 0) buff.append(" ");
              
              int b = 0;
              int e = charPerLine - buff.length() - 1;
              
              while(b < token.length()){
                String chunk = token.substring(b, e);
                buff.append(chunk);
                a.add(buff);
                buff = new StringBuffer();
  
                b = e + 1;
                e = e + charPerLine;
                if(e > token.length() - 1){
                  e = token.length() - 1;
                }
              }
            }else{
              a.add(buff);
              buff = new StringBuffer(token);
            }
          }
        }
        
        if(buff.length() > 0){
          a.add(buff);  
        }
        
        lines = new TextLine[a.size()];
        for(int i=0; i<a.size(); i++){
          lines[i] = new TextLine(new String((StringBuffer)a.get(i)));
        }
      }  
    }
  }
  
  //FPComponent
  public FPDimension getDimension(PrintingData data) {
    buildLines();
    placeAppearance(data.getGraphics());
    dim.setHeight((int) 0);
    dim.setWidth((int) 0);
    for(int i=0; i<lines.length; i++){
      FPDimension lDim = lines[i].getDimension(data);
      
      dim.setHeight(dim.getHeight() + lDim.getHeight());
      if(dim.getWidth() < lDim.getWidth()){
        dim.setWidth(lDim.getWidth());        
      }
      
    }
    
    resetAppearance(data.getGraphics());
    return dim;
  }
  
  //------------------------------- DEBUG ------------------------------------
  
  private static PrintStream dbgFile = null;
  private static int count = 0;
  
  private static void writeTransform(Graphics2D g2){  
    if(dbgFile == null){
      try{
        dbgFile = new PrintStream("c:/01barmaja/dev/java/app/mboq/print.log");
      }catch(Exception e){
        Globals.logException(e);
      }
    }
       
    AffineTransform t = g2.getTransform();
    double dx = t.getTranslateX();
    double dy = t.getTranslateY();
    double scalex = t.getScaleX();
    double scaley = t.getScaleY();

    dbgFile.println(count+" x = "+dx*scalex+" y = "+dy*scaley);
    dbgFile.flush();
    count++;    
  }
  
  //------------------------------- DEBUG ------------------------------------
    
  //FPComponent
  public ComponentPrintInfo printInt(Graphics graphics, PrintingData data, int pageIndex, int xLimit, int yLimit) {
    Graphics2D g2 = (Graphics2D) graphics;
    buildLines();
    placeAppearance(g2);
    for(int i=0; i<lines.length; i++){
      FPDimension dim = lines[i].getDimension(data);
      translate(g2, 0, dim.getHeight());
      if(!data.isDoNotPrint()){
        //writeTransform(g2);
        g2.drawString(lines[i].text, 0, 0);
        /*
        if(lines[i].text.startsWith("ABC-")){
          
          Font font = g2.getFont();
          Globals.logString("Font ("+lines[i].text+") :"+font.getFontName()+" "+font.getStyle()+" "+font.getSize());
          AffineTransform t = g2.getTransform();
          double dx = t.getTranslateX();
          double dy = t.getTranslateY();
          double scalex = t.getScaleX();
          double scaley = t.getScaleY();
          Globals.logString(" x = "+dx+" y = "+dy+" scaleX = "+scalex+" sccaleY = "+scaley+" x.scaleX = "+dx/scalex+" y.scaleY = "+dy/scaley);

        }
        */
      }      
    }
    resetTranslation(g2);
    resetAppearance(g2);
    includePage(pageIndex);

    return new ComponentPrintInfo(false, getDimension(data));
  }  

}
