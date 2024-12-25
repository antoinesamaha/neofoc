/*
 * Created on 29-May-2005
 */
package com.foc.print;

import javax.swing.*;

import com.foc.*;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FNumField;
import com.foc.gui.*;
import com.foc.property.FDouble;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

//import javax.swing.event.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.print.*;

/**
 * @author 01Barmaja
 */
public class PrintPreviewPanel extends FPanel {
  
  private JScrollPane scroll = null;
  
  private FGLabel pageNbrLabel = null; 
  private FGButton nextButton = null;
  private FGButton previousButton = null;
  
  private FDouble zoom = null; 
  private FGNumField zoomField = null;
  private double zoomNormalisation = 0;
  
  private FPReport rep = null;
  private int currentPage = 0;
  
  /**
   Constructs a print preview dialog.
   */
  public PrintPreviewPanel(FPReport rep) {
    this.rep = rep;
    currentPage = 0;
    
    layoutUI(rep);
  }

  public void setZoom(double z){
    if(zoomNormalisation == 0){
      zoomNormalisation = z;
    }
    zoom.setDouble(z * (100/zoomNormalisation));
  }

  public double getZoom(){
    return zoom.getDouble() * (zoomNormalisation/100);
  }

  public void incrementZoom(){
    setZoom(getZoom() + zoomNormalisation * 0.2);
  }

  public void decrementZoom(){
    setZoom(getZoom() - zoomNormalisation * 0.2);
  }

  public void layoutUI(FPReport rep) {
    //setPreferredSize(new Dimension(WIDTH, HEIGHT));
    //setSize(new Dimension(WIDTH, HEIGHT));

    resetCanvasPanel();
    /*
    canvas = new PrintPreviewCanvas(rep, currentPage);
    scroll = new JScrollPane(canvas);
    scroll.setAutoscrolls(true);
    add(scroll, 0, 0);
    */

    FPanel buttonPanel = new FPanel();

    int x = 0;
    
    FGButton minusZoomButton = new FGButton("-");
    buttonPanel.add("Zoom", minusZoomButton, x, 0);x+=2;
    minusZoomButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        decrementZoom();
      }
    });
    
    FNumField numField = new FNumField("N", "N", 1, false, 3, 0);
    
    zoom = new FDouble(null, 1, 0);
    zoomField = (FGNumField) numField.getGuiComponent(zoom);;
    buttonPanel.add(zoomField, x++, 0);
    zoom.addListener(new FPropertyListener() {
      public void propertyModified(FProperty property) {
        //currentPage = 0;
        //refresh();
        //Globals.getDisplayManager().violentRefresh();
        flipPage(1);
        flipPage(-1);
      }
      
      public void dispose(){
        
      }
    });

    FGButton plusZoomButton = new FGButton("+");
    buttonPanel.add(plusZoomButton, x++, 0);
    plusZoomButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        incrementZoom();
      }
    });

    
    previousButton = new FGButton("Previous");
    buttonPanel.add(previousButton, x++, 0);
    previousButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        flipPage(-1);
      }
    });

    FGButton closeButton = new FGButton("Close");
    buttonPanel.add(closeButton, x++, 0);
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        Globals.getDisplayManager().goBack();
      }
    });
    
    nextButton = new FGButton("Next");
    buttonPanel.add(nextButton, x++, 0);
    nextButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        flipPage(1);
      }
    });
    
    pageNbrLabel = new FGLabel();
    buttonPanel.add(pageNbrLabel, x++, 0);
    setPageNumber();
    
    add(buttonPanel, 0, 1);
  }

  private void resetCanvasPanel(){
    if(scroll != null){
      scroll.setVisible(false);
      this.remove(scroll);
    }
    Dimension viewDim = Globals.getDisplayManager().getNavigator().getViewportDimension();
    PrintPreviewCanvas canvas = new PrintPreviewCanvas(rep, currentPage, viewDim);
    scroll = new JScrollPane(canvas);
    scroll.setAutoscrolls(true);
    scroll.setPreferredSize(viewDim);
    add(scroll, 0, 0);
  }

  private void refresh(){
    resetCanvasPanel();
    setPageNumber();
    repaint();
  }

  /**
  Flip the book by the given number of pages.
  @param by the number of pages to flip by. Negative
  values flip backwards.
  */
  public void flipPage(int by) {
    int newPage = currentPage + by;
    if (0 <= newPage /*&& newPage < rep.getPageCount()*/) {
      currentPage = newPage;
      resetCanvasPanel();
      setPageNumber();
      repaint();
    }
  }  
  
  public void setPageNumber(){
    int page = currentPage + 1;
    int max = rep.getPageCount();
    
    pageNbrLabel.setText(page+" / "+max);
    nextButton.setEnabled(page < max);
    previousButton.setEnabled(page > 1);  
  }
  
  /**
   The canvas for displaying the print preview.
   */
  class PrintPreviewCanvas extends JPanel {
    private FPReport rep;
    private int currentPage;
    private Dimension viewDim;
    /**
     Constructs a print preview canvas.
     @param b the book to be previewed
     */
    public PrintPreviewCanvas(FPReport rep, int currentPage, Dimension viewDim) {
      this.rep = rep;
      this.currentPage = currentPage;
      this.viewDim = viewDim; 
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;
      
      PageFormat pageFormat = rep.getPageFormat();
      //PageFormat pageFormat = rep.getPageFormat(currentPage);

      double xoff; // x offset of page start in window
      double yoff; // y offset of page start in window
      double scale = getZoom() ; // scale factor to fit page in window
      double px = pageFormat.getWidth();
      double py = pageFormat.getHeight();
      double sx = viewDim.getWidth() - 1;
      double sy = viewDim.getHeight() - 1;

      if(scale == 0){
        if (px / py < sx / sy){ 
          // center horizontally
          scale = sy / py;
          setZoom(scale);
        }else{ 
          // center vertically
          scale = sy / py;
          setZoom(scale);
        }
      }

      xoff = Math.max(0, 0.5 * (sx - scale * px));
      yoff = Math.max(0, 0.5 * (sy - scale * py));
      
      setPreferredSize(new Dimension((int)((px+xoff) * scale), (int)((py+yoff) * scale)));

      g2.translate((float) xoff, (float) yoff);
      g2.scale((float) scale, (float) scale);
      
      // draw page outline (ignoring margins)
      Rectangle2D page = new Rectangle2D.Double(0, 0, px, py);
      g2.setPaint(Color.white);
      g2.fill(page);
      g2.setPaint(Color.black);
      g2.draw(page);

      try {
        rep.print(g2, pageFormat, currentPage);
        // g2.translate((double) 0, (double) - pageFormat.getHeight());
      } catch (PrinterException exception) {
        g2.draw(new Line2D.Double(0, 0, px, py));
        g2.draw(new Line2D.Double(0, px, 0, py));
      }
    }
  }
  
  public static void fillOrientationMultipleChoiceField(FMultipleChoiceField field){
    field.addChoice(PageFormat.PORTRAIT, "Portrait");
    field.addChoice(PageFormat.LANDSCAPE, "Landscape");
    field.addChoice(PageFormat.REVERSE_LANDSCAPE, "Reverse landscape");
  }
}
