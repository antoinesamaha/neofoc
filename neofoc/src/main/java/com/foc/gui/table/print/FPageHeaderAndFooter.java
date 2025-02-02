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
package com.foc.gui.table.print;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;

import com.foc.Globals;
import com.foc.print.FTablePrintParameters;

public class FPageHeaderAndFooter {
	
	//Header and Footer variables
	private String          headerText      = null;          
	private String          footerText      = null;
	private Rectangle2D     hRect           = null;
	private Rectangle2D     fRect           = null;
	private int             headerTextSpace = 0;
	private int             footerTextSpace = 0;
	private int             pageIndex       = 0;
	private int             tableTopY       = 0; 
	private int             tableImageableHeight = 0;
	
	private FTablePrintable tablePrintable  = null;
	
	public FPageHeaderAndFooter(FTablePrintable tablePrintable){
		this.tablePrintable = tablePrintable;
	}
	
	public void dispose(){
		tablePrintable = null;
		hRect = null;
		fRect = null;
	}
	
	public int getTableImageableHeight() {
		return tableImageableHeight;
	}

	public int getTableTopY() {
		return tableTopY;
	}

	public void computeHeaderFooterDataIfNeeded(){
		if(headerText == null){
			Object[] pageNumber = new Object[] { new Integer(pageIndex) };
	
			headerText = null;		
			// fetch the formatted header text, if any
			if(tablePrintable.getHeaderFormat() != null){
				headerText = tablePrintable.getHeaderFormat().format(pageNumber);
			}
			
			footerText = null;
			// fetch the formatted footer text, if any
			if(tablePrintable.getFooterFormat() != null){
				footerText = tablePrintable.getFooterFormat().format(pageNumber);
			}
			
			// if there's header text, find out how much space is needed for it
			// and subtract that from the available space
			if(headerText != null){
				tablePrintable.getGraphics().setFont(tablePrintable.getHeaderFont());
				hRect = tablePrintable.getGraphics().getFontMetrics().getStringBounds(headerText, tablePrintable.getGraphics());

				headerTextSpace = (int) Math.ceil(hRect.getHeight());
			}

			// if there's footer text, find out how much space is needed for it
			// and subtract that from the available space
			if(footerText != null){
				tablePrintable.getGraphics().setFont(tablePrintable.getFooterFont());
				fRect = tablePrintable.getGraphics().getFontMetrics().getStringBounds(footerText, tablePrintable.getGraphics());

				footerTextSpace = (int) Math.ceil(fRect.getHeight());
			}
		}		
	}
	
	public String getHeaderText(){
		computeHeaderFooterDataIfNeeded();
		return headerText;
	}
	
	public String getFooterText(){
		computeHeaderFooterDataIfNeeded();
		return footerText;
	}

	public Rectangle2D getHRect() {
		computeHeaderFooterDataIfNeeded();
		return hRect;
	}

	public Rectangle2D getFRect() {
		computeHeaderFooterDataIfNeeded();
		return fRect;
	}

	public int getHeaderTextSpace() {
		computeHeaderFooterDataIfNeeded();
		return headerTextSpace;
	}

	public int getFooterTextSpace() {
		computeHeaderFooterDataIfNeeded();
		return footerTextSpace;
	}
	
	private FTablePrintParameters getPrintParameters(){
		return tablePrintable.getPrintParameters();
	}

	/**
	 * A helper method that encapsulates common code for rendering the header and
	 * footer text.
	 * 
	 * @param g2d
	 *          the graphics to draw into
	 * @param text
	 *          the text to draw, non null
	 * @param rect
	 *          the bounding rectangle for this text, as calculated at the given
	 *          font, non null
	 * @param font
	 *          the font to draw the text in, non null
	 * @param imgWidth
	 *          the width of the area to draw into
	 */
	private void printText(Graphics2D g2d, String text, Rectangle2D rect, Font font, int imgWidth) {

		int tx;

		// if the text is small enough to fit, center it
		if(rect.getWidth() < imgWidth){
			tx = (int) ((imgWidth - rect.getWidth()) / 2);

			// otherwise, if the table is LTR, ensure the left side of
			// the text shows; the right can be clipped
		}else if(tablePrintable.getTable().getComponentOrientation().isLeftToRight()){
			tx = 0;

			// otherwise, ensure the right side of the text shows
		}else{
			tx = -(int) (Math.ceil(rect.getWidth()) - imgWidth);
		}

		int ty = (int) Math.ceil(Math.abs(rect.getY()));
		g2d.setColor(Color.BLACK);
		g2d.setFont(font);
		g2d.drawString(text, tx, ty);
	}

	public void print(Graphics graphics, PageFormat pageFormat, int pageIndex){
		this.pageIndex = pageIndex;
		final int imgWidth         = (int) pageFormat.getImageableWidth();
		final int imgHeight        = (int) pageFormat.getImageableHeight();
		
		FTablePrintParameters printParameters = getPrintParameters();
		
		final int pageFooterHeight = (printParameters != null) ? printParameters.getPageFooterHeight() : 0;

		int pageHeaderY = 0;
		if(headerTextSpace > 0){
			pageHeaderY += headerTextSpace + FTablePrintable.H_F_SPACE;
		}
		int pageFooterY = imgHeight - pageFooterHeight;
		if(footerTextSpace > 0){
			pageFooterY -= footerTextSpace + FTablePrintable.H_F_SPACE;
		}
		
		// translate into the co-ordinate system of the pageFormat
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		AffineTransform trans_TopImageable = g2d.getTransform();
		
		// Footer Text
		// -----------
		if(footerText != null){
			g2d.setTransform(trans_TopImageable);//Back to top page
			g2d.translate(0, imgHeight - footerTextSpace);
			printText(g2d, footerText, fRect, tablePrintable.getFooterFont(), imgWidth);
			
			String dateStr = printParameters != null ? printParameters.getPrintDate() : Globals.getDBManager().getCurrentDate().toString();
			if(dateStr != null && !dateStr.isEmpty()){
				Rectangle2D rect = g2d.getFontMetrics().getStringBounds(dateStr, g2d);
				
				g2d.drawString(dateStr, (int)(imgWidth - rect.getWidth()), (int)rect.getHeight());
				printText(g2d, footerText, fRect, tablePrintable.getFooterFont(), imgWidth);
			}
			g2d.setTransform(trans_TopImageable);//Back to top page
		}
		// -----------

		// Header Text
		// -----------
		if(headerText != null && !headerText.isEmpty()){
			g2d.setTransform(trans_TopImageable);//Back to top page
			printText(g2d, headerText, hRect, tablePrintable.getHeaderFont(), imgWidth);
			g2d.translate(0, headerTextSpace + FTablePrintable.H_F_SPACE);
			g2d.setTransform(trans_TopImageable);//Back to top page
		}
		// -----------

		//Print Page Footer
		//-----------------
		g2d.setTransform(trans_TopImageable);//Back to top page
		g2d.translate(0, pageFooterY);
		if(printParameters != null){
			printParameters.printFooter(g2d);
		}
		g2d.setTransform(trans_TopImageable);//Back to top page
		//-----------------
		
		//Print Page Header
		//-----------------
		g2d.setTransform(trans_TopImageable);//Back to top page
		g2d.translate(0, pageHeaderY);
		if(printParameters != null){
			printParameters.printHeader(g2d);
		}
		g2d.setTransform(trans_TopImageable);//Back to top page
		//-----------------

		final int pageHeaderHeight = printParameters != null ? printParameters.getPageHeaderHeight() : 0;
		tableTopY            = pageHeaderY + pageHeaderHeight;
		int tableBottomY     = pageFooterY;
		tableImageableHeight = tableBottomY - tableTopY;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
}
