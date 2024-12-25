/*
 * Created on 29-May-2005
 */
package com.foc.print;

import javax.swing.*;

import com.foc.*;
import com.foc.gui.*;

//import javax.swing.event.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.print.*;

/**
 * @author 01Barmaja
 */
public class CopyOfPrintPreviewPanel extends FPanel {
  private PrintPreviewCanvas canvas = null;
  
  private FGLabel pageNbrLabel = null; 
  private FGButton nextButton = null;
  private FGButton previousButton = null;  

  /**
   Constructs a print preview dialog.
   @param p a Printable
   @param pf the page format
   @param pages the number of pages in p
   */
  /*
  public PrintPreviewDialog(Printable p, PageFormat pf, int pages) {
    Book book = new Book();
    book.append(p, pf, pages);
    layoutUI(book);
  }
  */

  /**
   Constructs a print preview dialog.
   @param b a Book
   */
  public CopyOfPrintPreviewPanel(Book b) {
    layoutUI(b);
  }

  /**
   Lays out the UI of the dialog.
   @param book the book to be previewed
   */
  public void layoutUI(Book book) {
    //setPreferredSize(new Dimension(WIDTH, HEIGHT));
    //setSize(new Dimension(WIDTH, HEIGHT));

    canvas = new PrintPreviewCanvas(book);
    add(canvas, 0, 0);

    FPanel buttonPanel = new FPanel();

    nextButton = new FGButton("Next");
    buttonPanel.add(nextButton, 2, 0);
    nextButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        canvas.flipPage(1);
        setPageNumber();
      }
    });

    previousButton = new FGButton("Previous");
    buttonPanel.add(previousButton, 0, 0);
    previousButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        canvas.flipPage(-1);
        setPageNumber();        
      }
    });

    FGButton closeButton = new FGButton("Close");
    buttonPanel.add(closeButton, 1, 0);
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        Globals.getDisplayManager().goBack();
      }
    });
    
    pageNbrLabel = new FGLabel();
    buttonPanel.add(pageNbrLabel, 3, 0);
    setPageNumber();
    
    add(buttonPanel, 0, 1);
  }

  public void setPageNumber(){
    int page = canvas.getCurrentPage() + 1;
    int max = canvas.getPageNumber();
    
    pageNbrLabel.setText(page+" / "+max);
    nextButton.setEnabled(page < max);
    previousButton.setEnabled(page > 1);  
  }
  
  /**
   The canvas for displaying the print preview.
   */
  class PrintPreviewCanvas extends JPanel {
    private Book book;
    private int currentPage;
    
    /**
     Constructs a print preview canvas.
     @param b the book to be previewed
     */
    public PrintPreviewCanvas(Book b) {
      book = b;
      currentPage = 0;
      
      setPreferredSize(new Dimension(400, 500));
      setSize(new Dimension(300, 300));
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;
      PageFormat pageFormat = book.getPageFormat(currentPage);

      double xoff; // x offset of page start in window
      double yoff; // y offset of page start in window
      double scale; // scale factor to fit page in window
      double px = pageFormat.getWidth();
      double py = pageFormat.getHeight();
      double sx = getWidth() - 1;
      double sy = getHeight() - 1;
      if (px / py < sx / sy) // center horizontally
      {
        scale = sy / py;
        xoff = 0.5 * (sx - scale * px);
        yoff = 0;
      } else // center vertically
      {
        scale = sx / px;
        xoff = 0;
        yoff = 0.5 * (sy - scale * py);
      }
      g2.translate((float) xoff, (float) yoff);
      g2.scale((float) scale, (float) scale);

      // draw page outline (ignoring margins)
      Rectangle2D page = new Rectangle2D.Double(0, 0, px, py);
      g2.setPaint(Color.white);
      g2.fill(page);
      g2.setPaint(Color.black);
      g2.draw(page);

      Printable printable = book.getPrintable(currentPage);
      try {
        printable.print(g2, pageFormat, currentPage);
      } catch (PrinterException exception) {
        g2.draw(new Line2D.Double(0, 0, px, py));
        g2.draw(new Line2D.Double(0, px, 0, py));
      }
    }

    /**
     Flip the book by the given number of pages.
     @param by the number of pages to flip by. Negative
     values flip backwards.
     */
    public void flipPage(int by) {
      int newPage = currentPage + by;
      if (0 <= newPage && newPage < book.getNumberOfPages()) {
        currentPage = newPage;
        repaint();
      }
    }
    
    public int getCurrentPage() {
      return currentPage;
    }
    
    public int getPageNumber(){
      return book.getNumberOfPages();
    }
  }
}
