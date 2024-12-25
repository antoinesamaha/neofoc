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

import javax.swing.JTable;
import javax.swing.table.*;

import com.foc.gui.table.FColumnGroupTableHeader;
import com.foc.gui.table.FTable;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.treeTable.FTreeTableModel;
import com.foc.print.FTablePrintParameters;
import com.foc.tree.FNode;

import java.awt.*;
import java.awt.print.*;
import java.awt.geom.*;
import java.text.MessageFormat;
import java.util.ArrayList;

public class CopyOfFTablePrintable implements Printable {

	/** The table to print. */
	private FTable table                  = null;
	private FTable fixTable               = null;
	private double currentPageScaleFactor = 1;
	private double defaultScaleFactor     = 1;

	/** For quick reference to the table's header. */
	private JTableHeader header;

	/** For quick reference to the table's column model. */
	private TableColumnModel colModel;

	/** To save multiple calculations of total column width. */
	private int totalColWidth;

	/** The printing mode of this printable. */
	private JTable.PrintMode printMode;

	/** Provides the header text for the table. */
	private MessageFormat headerFormat;

	/** Provides the footer text for the table. */
	private MessageFormat footerFormat;

	/** The most recent page index asked to print. */
	private int last = -1;

	/** The next row to print. */
	private int row = 0;

	/** The next column to print. */
	private int col = 0;

	/** Used to store an area of the table to be printed. */
	private final Rectangle clip    = new Rectangle(0, 0, 0, 0);
	private final Rectangle fixClip = new Rectangle(0, 0, 0, 0);

	/** Used to store an area of the table's header to be printed. */
	private final Rectangle hclip    = new Rectangle(0, 0, 0, 0);
	private final Rectangle hFixClip = new Rectangle(0, 0, 0, 0);

	/** Saves the creation of multiple rectangles. */
	private final Rectangle tempRect = new Rectangle(0, 0, 0, 0);

	/** Vertical space to leave between table and header/footer text. */
	private static final int H_F_SPACE = 8;

	/** Font size for the header text. */
	private static final float HEADER_FONT_SIZE = 18.0f;

	/** Font size for the footer text. */
	private static final float FOOTER_FONT_SIZE = 12.0f;

	/** The font to use in rendering header text. */
	private Font headerFont;

	/** The font to use in rendering footer text. */
	private Font footerFont;

	/**
	 * Create a new <code>TablePrintable</code> for the given <code>JTable</code>.
	 * Header and footer text can be specified using the two
	 * <code>MessageFormat</code> parameters. When called upon to provide a
	 * String, each format is given the current page number.
	 * 
	 * @param table
	 *          the table to print
	 * @param printMode
	 *          the printing mode for this printable
	 * @param headerFormat
	 *          a <code>MessageFormat</code> specifying the text to be used in
	 *          printing a header, or null for none
	 * @param footerFormat
	 *          a <code>MessageFormat</code> specifying the text to be used in
	 *          printing a footer, or null for none
	 * @throws IllegalArgumentException
	 *           if passed an invalid print mode
	 */
	public CopyOfFTablePrintable(JTable table, JTable.PrintMode printMode, MessageFormat headerFormat, MessageFormat footerFormat) {

		this.table    = (FTable) table;
		if(this.table.getScrollPane() != null && this.table.getScrollPane().getFixedTable() != null){
			fixTable = this.table.getScrollPane().getFixedTable();
		}

		header        = table.getTableHeader();
		colModel      = table.getColumnModel();
		//((FTable)table).computeMaxColWidth();
		//totalColWidth = ((FTable)table).getMaxColumnWidth();
		totalColWidth = colModel.getTotalColumnWidth();
			
		if(header != null){
			// the header clip height can be set once since it's unchanging
			hclip.height    = header.getHeight();
		}

		if(fixTable != null && fixTable.getTableHeader() != null){
			hFixClip.height = fixTable.getTableHeader().getHeight();
		}
		
		this.printMode = printMode;

		this.headerFormat = headerFormat;
		this.footerFormat = footerFormat;

		// derive the header and footer font from the table's font
		headerFont = table.getFont().deriveFont(Font.BOLD, HEADER_FONT_SIZE);
		footerFont = table.getFont().deriveFont(Font.PLAIN, FOOTER_FONT_SIZE);
		
		//Getting the default scale factor
		defaultScaleFactor = 1.0D;
		FTablePrintParameters printParameters = getPrintParameters();
		if(printParameters != null){
			defaultScaleFactor = printParameters.getScale();
		}

		currentPageScaleFactor = defaultScaleFactor;
	}

	public int computeAvailableWidth(int imgWidth) throws PrinterException{
		int availableWidth = imgWidth - getFixTableWidth_Scaled();
		if(availableWidth <= 0){
			throw new PrinterException("Width of printable area is too small to hold the Fixed Area.");
		}
		return availableWidth;
	}

	/**
	 * Prints the specified page of the table into the given {@link Graphics}
	 * context, in the specified format.
	 * 
	 * @param graphics
	 *          the context into which the page is drawn
	 * @param pageFormat
	 *          the size and orientation of the page being drawn
	 * @param pageIndex
	 *          the zero based index of the page to be drawn
	 * @return PAGE_EXISTS if the page is rendered successfully, or NO_SUCH_PAGE
	 *         if a non-existent page index is specified
	 * @throws PrinterException
	 *           if an error causes printing to be aborted
	 */
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

		// dictated by the previous two assertions
		assert defaultScaleFactor > 0;
		
		// for easy access to these values
		final int imgWidth  = (int) pageFormat.getImageableWidth();
		final int imgHeight = (int) pageFormat.getImageableHeight();

		if(imgWidth <= 0){
			throw new PrinterException("Width of printable area is too small.");
		}

		// to pass the page number when formatting the header and footer text
		Object[] pageNumber = new Object[] { new Integer(pageIndex + 1) };

		// fetch the formatted header text, if any
		String headerText = null;
		if(headerFormat != null){
			headerText = headerFormat.format(pageNumber);
		}

		// fetch the formatted footer text, if any
		String footerText = null;
		if(footerFormat != null){
			footerText = footerFormat.format(pageNumber);
		}

		// to store the bounds of the header and footer text
		Rectangle2D hRect = null;
		Rectangle2D fRect = null;

		// the amount of vertical space needed for the header and footer text
		int headerTextSpace = 0;
		int footerTextSpace = 0;

		// the amount of vertical space available for printing the table
		int availableSpace = imgHeight;

		// if there's header text, find out how much space is needed for it
		// and subtract that from the available space
		if(headerText != null){
			graphics.setFont(headerFont);
			hRect = graphics.getFontMetrics().getStringBounds(headerText, graphics);

			headerTextSpace = (int) Math.ceil(hRect.getHeight());
			availableSpace -= headerTextSpace + H_F_SPACE;
		}

		// if there's footer text, find out how much space is needed for it
		// and subtract that from the available space
		if(footerText != null){
			graphics.setFont(footerFont);
			fRect = graphics.getFontMetrics().getStringBounds(footerText, graphics);

			footerTextSpace = (int) Math.ceil(fRect.getHeight());
			availableSpace -= footerTextSpace + H_F_SPACE;
		}

		if(availableSpace <= 0){
			throw new PrinterException("Height of printable area is too small.");
		}

		int availableWidth = computeAvailableWidth(imgWidth);

		// This is in a loop for two reasons:
		// First, it allows us to catch up in case we're called starting
		// with a non-zero pageIndex. Second, we know that we can be called
		// for the same page multiple times. The condition of this while
		// loop acts as a check, ensuring that we don't attempt to do the
		// calculations again when we are called subsequent times for the
		// same page.
		while (last < pageIndex){
			currentPageScaleFactor = defaultScaleFactor;

			// if we are finished all columns in all rows
			if(row >= table.getRowCount() && col == 0){
				return NO_SUCH_PAGE;
			}

			int       row_Backup  = row;
			int       col_Backup  = col;
			Rectangle clip_Backup = (Rectangle) clip.clone();
			clip_Backup.x = clip.x;
			clip_Backup.y = clip.y;
			clip_Backup.width  = clip.width;
			clip_Backup.height = clip.height;

			boolean recompute = true;
			while(recompute){
				// rather than multiplying every row and column by the scale factor
				// in findNextClip, just pass a width and height that have already
				// been divided by it
				int scaledWidth  = (int) (availableWidth / currentPageScaleFactor);
				int scaledHeight = (int) ((availableSpace - hclip.height) / currentPageScaleFactor);
	
				// calculate the area of the table to be printed for this page
				findNextClip(scaledWidth, scaledHeight);
				if(clip.width <= scaledWidth){
					recompute = false;
				}else{
					double totalWidthToDraw = getFixTableWidth() + (double)clip.width;
					currentPageScaleFactor = ((double)imgWidth / totalWidthToDraw); 
					availableWidth = computeAvailableWidth(imgWidth);
					recompute      = true;
					
					col = col_Backup;
					row = row_Backup;
					clip.x = clip_Backup.x;
					clip.y = clip_Backup.y;
					clip.width  = clip_Backup.width;
					clip.height = clip_Backup.height;
				}
			}
			last++;
		}

		// translate into the co-ordinate system of the pageFormat
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

		// to save and store the transform
		AffineTransform oldTrans;

		// if there's footer text, print it at the bottom of the imageable area
		if(footerText != null){
			oldTrans = g2d.getTransform();

			g2d.translate(0, imgHeight - footerTextSpace);

			printText(g2d, footerText, fRect, footerFont, imgWidth);

			g2d.setTransform(oldTrans);
		}

		// if there's header text, print it at the top of the imageable area
		// and then translate downwards
		if(headerText != null){
			printText(g2d, headerText, hRect, headerFont, imgWidth);

			g2d.translate(0, headerTextSpace + H_F_SPACE);
		}

		AffineTransform veryOldTrans = g2d.getTransform();
		Shape veryOldClip = g2d.getClip();

		// constrain the table output to the available space
		tempRect.x = 0;//getFixTableWidth_();
		tempRect.y = 0;
		tempRect.width = (int) (availableWidth / currentPageScaleFactor);
		tempRect.height = (int) (availableSpace / currentPageScaleFactor);
		g2d.clip(tempRect);

		// if we have a scale factor, scale the graphics object to fit
		// the entire width
		//if(currentPageScaleFactor != 1.0D){
			g2d.scale(currentPageScaleFactor, currentPageScaleFactor);
		//}

		//g2d.translate(getFixTableWidth_Scaled(), 0);//Barmaja
		g2d.translate(getFixTableWidth(), 0);
		
		// store the old transform and clip for later restoration
		oldTrans = g2d.getTransform();
		Shape oldClip = g2d.getClip();
		
		// if there's a table header, print the current section and
		// then translate downwards
		if(header != null){
			hclip.x = clip.x;
			hclip.width = clip.width;

			g2d.translate(-hclip.x, 0);
			g2d.clip(hclip);
			header.print(g2d);

			// restore the original transform and clip
			g2d.setTransform(oldTrans);
			g2d.setClip(oldClip);

			// translate downwards
			g2d.translate(0, hclip.height);
		}

		// print the current section of the table
		g2d.translate(-clip.x, -clip.y);
		g2d.clip(clip);
		table.print(g2d);

		// restore the original transform and clip
		g2d.setTransform(oldTrans);
		g2d.setClip(oldClip);

		// draw a box around the table
		g2d.setColor(Color.BLACK);
		//g2d.drawRect(0, 0, clip.width, hclip.height + clip.height);

		
		
		
		

		// restore the very very original transform and clip
		g2d.setTransform(veryOldTrans);
		g2d.setClip(veryOldClip);

		// if we have a scale factor, scale the graphics object to fit
		// the entire width
		g2d.scale(currentPageScaleFactor, currentPageScaleFactor);
		
		if(fixTable != null){
			// if there's a table header, print the current section and
			// then translate downwards
			if(fixTable.getTableHeader() != null){
				hFixClip.x = fixClip.x;
				hFixClip.width = fixClip.width;
					
				g2d.translate(-hFixClip.x, 0);
				g2d.clip(hFixClip);
				fixTable.getTableHeader().print(g2d);
	
				// restore the original transform and clip
				g2d.setTransform(veryOldTrans);
				g2d.setClip(veryOldClip);
				g2d.scale(currentPageScaleFactor, currentPageScaleFactor);
	
				// translate downwards
				g2d.translate(0, hclip.height);
			}
	
			// print the current section of the table
			g2d.translate(-fixClip.x, -fixClip.y);
			g2d.clip(fixClip);
			fixTable.print(g2d);
	
			// restore the original transform and clip
			g2d.setTransform(veryOldTrans);
			g2d.setClip(veryOldClip);
			g2d.scale(currentPageScaleFactor, currentPageScaleFactor);
			
			// draw a box around the table
			g2d.setColor(Color.BLACK);
			//g2d.drawRect(0, 0, fixClip.width, hclip.height + fixClip.height);
		}

		
		
		
		
		return PAGE_EXISTS;
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
		}else if(table.getComponentOrientation().isLeftToRight()){
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

	private boolean shouldSkipThisRow(){
		boolean shouldSkipThisRow = false;				
		if(getPrintParameters().getPageBreakTreeLevel() > 0){
			if(table.getModel() instanceof FTreeTableModel){
				FTreeTableModel treeTableModel = (FTreeTableModel) table.getModel();
				FNode           node           = treeTableModel.getNodeForRow(row);
				
				shouldSkipThisRow = node.getNodeDepth() < getPrintParameters().getPageBreakTreeLevel();
			}
		}
		return shouldSkipThisRow;
	}

	private boolean isNodeUnbreakeable(){
		boolean unbreak = false;				
		if(getPrintParameters().getLevelNotToSplit() > 0){
			if(table.getModel() instanceof FTreeTableModel){
				FTreeTableModel treeTableModel = (FTreeTableModel) table.getModel();
				FNode           node           = treeTableModel.getNodeForRow(row);
				
				unbreak = node.getNodeDepth() == getPrintParameters().getLevelNotToSplit();
			}
		}
		return unbreak;
	}

	private boolean isNodeHigherThanUnbreakeable(){
		boolean unbreak = false;				
		if(getPrintParameters().getPageBreakTreeLevel() > 0){
			if(table.getModel() instanceof FTreeTableModel){
				FTreeTableModel treeTableModel = (FTreeTableModel) table.getModel();
				FNode           node           = treeTableModel.getNodeForRow(row);
				
				unbreak = node.getNodeDepth() > getPrintParameters().getLevelNotToSplit();
			}
		}
		return unbreak;
	}
	
	/**
	 * Calculate the area of the table to be printed for the next page. This
	 * should only be called if there are rows and columns left to print.
	 * 
	 * To avoid an infinite loop in printing, this will always put at least one
	 * cell on each page.
	 * 
	 * @param pw
	 *          the width of the area to print in
	 * @param ph
	 *          the height of the area to print in
	 */
	private double findNextClip(int pw, int ph) {
		double newScaleFactor = currentPageScaleFactor;
		final boolean ltr = table.getComponentOrientation().isLeftToRight();

		// if we're ready to start a new set of rows
		if(col == 0){
			if(ltr){
				// adjust clip to the left of the first column
				clip.x = 0;
			}else{
				// adjust clip to the right of the first column
				clip.x = totalColWidth;
			}

			// adjust clip to the top of the next set of rows
			clip.y += clip.height;

			// adjust clip width and height to be zero
			clip.width = 0;
			clip.height = 0;

			Rectangle markClip = null;
			int       markRow  = -1;
			
			// fit as many rows as possible, and at least one
			int     rowCount               = table.getRowCount();
			int     rowHeight              = table.getRowHeight(row);
			boolean atLeastOneRowIsPrinted = false;
			do{
				//Unbreakeable Node
				//-----------------
				if(atLeastOneRowIsPrinted && isNodeUnbreakeable()){
					markRow  = row;
					markClip = new Rectangle(clip.x, clip.y, clip.width, clip.height);
				}else if(isNodeHigherThanUnbreakeable()){
					markRow  = -1;
					markClip = null;
				}
				//-----------------
				
				//Should skip this row
			  //--------------------				
				boolean shouldSkipThisRow = shouldSkipThisRow();//We skip if it is at a higher level then break page
				if(shouldSkipThisRow){
					clip.y += rowHeight;
					rowHeight = 0;
				}else{
					atLeastOneRowIsPrinted = true;
				}
				//--------------------
				
				clip.height += rowHeight;

				if(++row >= rowCount){
					break;
				}
				
				//Maybe we need to do a page break
  			//--------------------------------
				if(atLeastOneRowIsPrinted && getPrintParameters().getPageBreakTreeLevel() > 0){
					if(table.getModel() instanceof FTreeTableModel){
						FTreeTableModel treeTableModel = (FTreeTableModel) table.getModel();
						FNode           node           = treeTableModel.getNodeForRow(row);
						if(node.getNodeDepth() <= getPrintParameters().getPageBreakTreeLevel()){
							break;
						}
					}
				}
				//--------------------------------
				
				rowHeight = table.getRowHeight(row);
			}while (clip.height + rowHeight <= ph);
			
			if(markClip != null && !isNodeUnbreakeable()){
				clip.x      = markClip.x;
				clip.y      = markClip.y;
				clip.width  = markClip.width;
				clip.height = markClip.height;
				row         = markRow;
			}
		}

		/*
		// we can short-circuit for JTable.PrintMode.FIT_WIDTH since
		// we'll always fit all columns on the page
		if(printMode == JTable.PrintMode.FIT_WIDTH){
			clip.x = 0;
			clip.width = totalColWidth;
			return;
		}
		*/

		if(ltr){
			// adjust clip to the left of the next set of columns
			clip.x += clip.width;
		}

		// adjust clip width to be zero
		clip.width = 0;
		
		// fit as many columns as possible, and at least one
		int     colCount      = table.getColumnCount();
		int     colWidth      = colModel.getColumn(col).getWidth();
		boolean lessThatWidth = true;
		boolean canCut        = true;
		do{
			clip.width += colWidth;
			if(!ltr){
				clip.x -= colWidth;
			}

			if(++col >= colCount){
				// reset col to 0 to indicate we're finished all columns
				col = 0;

				break;
			}
			colWidth = colModel.getColumn(col).getWidth();
			
			if(lessThatWidth){
				lessThatWidth = clip.width + colWidth <= pw;
			}
			
			canCut = true;
			if(header instanceof FColumnGroupTableHeader && col < colCount){
				FTableColumn fCol      = table.getTableView().getVisibleColumnAt(col  );
				FTableColumn fCol_Prev = table.getTableView().getVisibleColumnAt(col-1);
				FColumnGroupTableHeader grpHeader = (FColumnGroupTableHeader) header;
				ArrayList<Integer> grps      = grpHeader.getGroupsIndexesForColumn(fCol);
				ArrayList<Integer> grps_Prev = grpHeader.getGroupsIndexesForColumn(fCol_Prev);
				//if(grps_Prev.size() > 0 && grps.size() > 0 && grps_Prev.get(grps_Prev.size()-1) == grps.get(grps.size()-1))
				if(grps_Prev.size() > 0 && grps.size() > 0 && grps_Prev.get(0) == grps.get(0)){
					canCut = false;
				}
			}			
		}while (lessThatWidth || !canCut);
		
		fixClip.x      = 0;
		fixClip.width  = getFixTableWidth();
		fixClip.y      = clip.y;
		fixClip.height = clip.height;
		
		return newScaleFactor;
	}

	private int getFixTableWidth(){
		return fixTable != null ? fixTable.getWidth(): 0;
	}

	private int getFixTableWidth_Scaled(){
		return (int)(getFixTableWidth() * currentPageScaleFactor);
	}
	
	private FTablePrintParameters getPrintParameters(){
		FTablePrintParameters printParams = null;
		if(table != null){
			printParams = table.getPrintParameters();
		}
		return printParams;
	}
}
