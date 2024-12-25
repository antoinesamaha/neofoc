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

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import com.foc.Globals;
import com.foc.gui.table.FColumnGroupTableHeader;
import com.foc.gui.table.FTable;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.treeTable.FTreeTableModel;
import com.foc.print.FTablePrintParameters;
import com.foc.tree.FNode;

public class FTablePage {

	private int                  pageIndex        = -1;
	private FTablePrintable      tablePrintable   = null;
	private double               scaleFactor      =  1;
	private int                  rowStart         = -1;
	private int                  rowEnd           = -1;
	private int                  colStart         = -1;
	private int                  colEnd           = -1;
	private Rectangle            clip             = new Rectangle(0, 0, 0, 0);
	private boolean              nbrRowsFixed     = false;
	private boolean              nbrColsFixed     = false;
	
	public FTablePage(FTablePrintable printable, int pageIndex){
		this.tablePrintable = printable;
		this.pageIndex      = pageIndex;
	}
	
	public void dispose(){
		tablePrintable = null;
		clip           = null;
	}

	public double getScaleFactor() {
		return scaleFactor;
	}

	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	public int getRowStart() {
		return rowStart;
	}

	public void setRowStart(int rowStart) {
		this.rowStart = rowStart;
	}

	public int getRowEnd() {
		return rowEnd;
	}

	public void setRowEnd(int rowEnd) {
		this.rowEnd = rowEnd;
	}

	public int getColStart() {
		return colStart;
	}

	public void setColStart(int colStart) {
		this.colStart = colStart;
	}

	public int getColEnd() {
		return colEnd;
	}

	public void setColEnd(int colEnd) {
		this.colEnd = colEnd;
	}

	public Rectangle getClip() {
		return clip;
	}
	
	public int getAvailableSpace(){
		return getPageHeaderAndFooter().getTableImageableHeight();
		/*int availableSpace = (int) tablePrintable.getPageFormat().getImageableHeight();
		if(getPageHeaderAndFooter().getHeaderTextSpace() != 0){
			availableSpace -= getHeaderTextSpace() + FTablePrintable.H_F_SPACE;
		}
		if(getFooterTextSpace() != 0){
			availableSpace -= getFooterTextSpace() + FTablePrintable.H_F_SPACE;
		}
		
		return availableSpace;
		*/
	}
	
	public int getAvailableWidth(){
		int availableWidth = (int) tablePrintable.getPageFormat().getImageableWidth();
		availableWidth -= tablePrintable.getFixTableWidth() * scaleFactor;
		return availableWidth;
	}

	public int getTableHeaderHeight(){
		return tablePrintable.getTable().getTableHeader().getHeight();
	}
	
	private boolean isFirstLine(){
		boolean isFirst = false;
		FTablePage firstPage = tablePrintable.getPage(0);
		if(firstPage == null || firstPage.getRowStart() == getRowStart()){
			isFirst = true;
		}
		return isFirst;
	}
	
	public boolean compute(FTablePage prevPage){
		boolean error = compute_Initialize(prevPage);
		if(!error){
			int nbrCompute = 0;
			do{
				nbrCompute++;
			}while(compute_Internal() && nbrCompute == 1);
		}
		return error; 
	}

	private boolean compute_Initialize(FTablePage prevPage){
		boolean error = false;
		
		scaleFactor = tablePrintable.getDefaultScaleFactor();
		
		if(prevPage == null){
			final boolean ltr = getTable().getComponentOrientation().isLeftToRight();			
			if(ltr){
				getClip().x = 0;// adjust clip to the left of the first column
			}else{
				getClip().x = tablePrintable.getTotalColWidth();// adjust clip to the right of the first column
			}

			setRowStart(0);
			setColStart(0);
			setRowEnd(0);
			setColEnd(0);
			nbrRowsFixed = false;
			nbrColsFixed = false;
			
			getClip().y      = 0;
			getClip().width  = 0;
			getClip().height = 0;
			
			error = getColumnModel().getColumnCount() == 0 || getTable().getRowCount() == 0;
		}else{
			FTable table = tablePrintable.getTable();

			if(prevPage.getColEnd()+1 < getColumnModel().getColumnCount()){
				setColStart(prevPage.getColEnd()+1);
				setRowStart(prevPage.getRowStart());
				setRowEnd(prevPage.getRowEnd());
				clip.x       = prevPage.getClip().x + prevPage.getClip().width;
				clip.y       = prevPage.getClip().y;
				clip.height  = prevPage.getClip().height;
				nbrRowsFixed = true;
			}else{
				setColStart(0);
				setRowStart(prevPage.getRowEnd()+1);
				setRowEnd(getRowStart());
				clip.x       = 0;
				clip.y       = prevPage.getClip().y + prevPage.getClip().height;
				clip.height  = 0;
				nbrRowsFixed = false;
			}
			
			if(!isFirstLine()){
				nbrColsFixed = true;
				//search for first page with these columns
				FTablePage firstPage = tablePrintable.getFirstPageForColumn(getColStart());
				setColEnd(firstPage.getColEnd());
				clip.x     = firstPage.getClip().x;
				clip.width = firstPage.getClip().width;
			}
			
			error = getRowStart() >= table.getRowCount();
		}
		
		if(nbrRowsFixed){
			double scaleFactor_Height = computeScaleFactorForHeight((double)clip.height);
			if(scaleFactor_Height < scaleFactor){
				scaleFactor = scaleFactor_Height;
			}
		}

		return error;
	}
	
	private boolean computeWidth(boolean keepMinimum, int prevWidth, int prevColEnd){
		boolean recompute = false;

		if(!nbrColsFixed){
			setColEnd(getColStart());
			clip.width  = 0;
			
			// rather than multiplying every row and column by the scale factor
			// in findNextClip, just pass a width and height that have already
			// been divided by it
			
			// calculate the width of the table to be printed for this page
			int scaledWidth  = (int) (getAvailableWidth() / scaleFactor);
			findNextColClip(scaledWidth, keepMinimum, prevWidth, prevColEnd);
			if(clip.width <= scaledWidth){
				recompute = false;
			}else{
				scaleFactor = computeScaleFactorForWidth((double)clip.width);
			}
		}		
		return recompute;
	}
	
	private boolean computeHeight(){
		boolean recompute = false;
		
		if(!nbrRowsFixed){
			setRowEnd(getRowStart());
			clip.height = 0;
	
			int scaledHeight = (int) ((getAvailableSpace()- getTableHeaderHeight()) / scaleFactor);
			findNextRowClip(scaledHeight);
			if(clip.height <= scaledHeight){
				recompute = false;
			}else{
				scaleFactor = computeScaleFactorForHeight((double)clip.height);
				recompute = true;
			}
		}		
		return recompute;
	}	
	
	private boolean compute_Internal(){
		boolean recompute = false;
		
		recompute = computeWidth(false, 0, 0);
		if(computeHeight()){
			recompute = true;
			computeWidth(true, clip.width, getColEnd());
		}

		/*
		if(clip.width <= scaledWidth){
			recompute = false;
		}else{
			scaleFactor = computeScaleFactorForWidth((double)clip.width);

			if(nbrRowsFixed){
				double scaleFactor_Height = computeScaleFactorForHeight((double)clip.height);
				if(scaleFactor_Height < scaleFactor){
					scaleFactor = scaleFactor_Height;
				}
			}else{
				//scaleFactor = Math.floor(scaleFactor * 100) / 100;
				recompute   = true;
			}
		}
		*/
		
		return false;//recompute;
	}
	
	private double computeScaleFactorForWidth(double width){
		double totalWidthToDraw  = tablePrintable.getFixTableWidth() + width;
		double scaleFactor = ((double)tablePrintable.getPageFormat().getImageableWidth() / totalWidthToDraw);
		return scaleFactor;
	}

	private double computeScaleFactorForHeight(double height){
		double totalHeightToDraw  = height + getTableHeaderHeight();
		double scaleFactor_Height = ((double)getAvailableSpace() / totalHeightToDraw);
		return scaleFactor_Height;
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
	private void findNextRowClip(int ph) {
		int row = getRowStart();
		
		// if we're ready to start a new set of rows
		// adjust clip width and height to be zero
		clip.height = 0;
		//Rectangle markClip = null;
		//int       markRow  = -1;
		
		// fit as many rows as possible, and at least one
		int     rowCount               = tablePrintable.getTable().getRowCount();
		int     rowHeight              = tablePrintable.getTable().getRowHeight(row);
		boolean atLeastOneRowIsPrinted = false;

		boolean lessThatHeight   = true;
		boolean canCut           = true;
		int     prevCanCutRow    = -1;
		int     prevCanCutHeight = -1;
		
		do{
			//Unbreakeable Node
			//-----------------
			/*
			if(atLeastOneRowIsPrinted && isNodeUnbreakeable(row, true)){
				markRow  = row;
				markClip = new Rectangle(clip.x, clip.y, clip.width, clip.height);
			}else if(isNodeHigherThanUnbreakeable(row)){
				markRow  = -1;
				markClip = null;
			}
			*/
			//-----------------
			
			//Should skip this row
		  //--------------------				
			boolean shouldSkipThisRow = shouldSkipThisRow(row);//We skip if it is at a higher level then break page
			if(shouldSkipThisRow){
				clip.y += rowHeight;
				rowHeight = 0;
				setRowStart(row+1);
			}else{
				atLeastOneRowIsPrinted = true; //atLeastOneRowIsPrinted || !isNodeUnbreakeable(row, true);
			}
			//--------------------
			
			clip.height += rowHeight;
			setRowEnd(row);
			
			if(row+1 < rowCount && canCutRow(row+1) && lessThatHeight){
				prevCanCutRow    = row;
				prevCanCutHeight = clip.height;
			}
			
			if(++row >= rowCount){
				break;
			}
			
			//Maybe we need to do a page break
			//--------------------------------
			if(atLeastOneRowIsPrinted && getPrintParameters() != null && getPrintParameters().getPageBreakTreeLevel() > 0){
				if(getTable().getModel() instanceof FTreeTableModel){
					FTreeTableModel treeTableModel = (FTreeTableModel) getTable().getModel();
					FNode           node           = treeTableModel.getNodeForRow(row);
					if(node.getNodeDepth() <= getPrintParameters().getPageBreakTreeLevel()){
						break;
					}
				}
			}
			//--------------------------------
			
			rowHeight = getTable().getRowHeight(row);
			
			if(lessThatHeight){
				lessThatHeight = clip.height + rowHeight <= ph;
			}
			
			canCut = canCutRow(row);
		
		}while (lessThatHeight || !canCut);
		
		if(prevCanCutRow > 0){
			double scalePrev = computeScaleFactorForHeight(prevCanCutHeight);
			double scaleNext = computeScaleFactorForHeight(clip.height);
			double diffPrev = scalePrev - scaleFactor;
			double diffNext = scaleNext - scaleFactor;
			if(diffNext * diffNext > diffPrev * diffPrev){
				clip.height = prevCanCutHeight;
				setRowEnd(prevCanCutRow);
			}
		}
		
		/*
		if(row < rowCount && markClip != null && !isNodeUnbreakeable(row, true)){
			clip.width  = markClip.width;
			clip.height = markClip.height;
			row         = markRow;
			setRowEnd(row);				
		}
		*/
	}
	
	private void findNextColClip(int pw, boolean keepMinimum, int prevWidth, int prevColEnd) {
		int col = getColStart();
		// adjust clip width to be zero
		clip.width = 0;

		// fit as many columns as possible, and at least one
		int     colCount        = getColumnModel().getColumnCount();
		int     colWidth        = getColumnModel().getColumn(col).getWidth();
		boolean lessThatWidth   = true;
		boolean canCut          = true;
		int     prevCanCutCol   = -1;
		int     prevCanCutWidth = -1;
		do{
			clip.width += colWidth;
			setColEnd(col);

			if(col+1 < colCount && canCutCol(col+1) && lessThatWidth){
				prevCanCutCol   = col;
				prevCanCutWidth = clip.width;
			}
			
			if(++col >= colCount){
				// reset col to 0 to indicate we're finished all columns
				col = colCount-1;
				break;
			}
							
			colWidth = getColumnModel().getColumn(col).getWidth();
			
			if(lessThatWidth){
				lessThatWidth = clip.width + colWidth <= pw;
			}
			
			canCut = canCutCol(col);
		}while (lessThatWidth || !canCut);
		
		if(prevCanCutCol > 0){
			double scalePrev = computeScaleFactorForWidth(prevCanCutWidth);
			double scaleNext = computeScaleFactorForWidth(clip.width);
			double diffPrev = scalePrev - scaleFactor;
			double diffNext = scaleNext - scaleFactor;
			if(diffNext * diffNext > diffPrev * diffPrev || keepMinimum){
				clip.width = prevCanCutWidth;
				setColEnd(prevCanCutCol);
			}
		}
		
		if(keepMinimum && clip.width < prevWidth){
			clip.width = prevWidth;
			setColEnd(prevColEnd);
		}
	}
	
	private boolean canCutCol(int col){
		boolean canCut   = true;
		int     colCount = getColumnModel().getColumnCount();		
		if(getTable().getTableHeader() instanceof FColumnGroupTableHeader && col < colCount){
			FTableColumn fCol      = getTable().getTableView().getVisibleColumnAt(col  );
			FTableColumn fCol_Prev = getTable().getTableView().getVisibleColumnAt(col-1);
			FColumnGroupTableHeader grpHeader = (FColumnGroupTableHeader) getTable().getTableHeader();
			ArrayList<Integer> grps      = grpHeader.getGroupsIndexesForColumn(fCol);
			ArrayList<Integer> grps_Prev = grpHeader.getGroupsIndexesForColumn(fCol_Prev);
			//if(grps_Prev.size() > 0 && grps.size() > 0 && grps_Prev.get(grps_Prev.size()-1) == grps.get(grps.size()-1))
			if(grps_Prev.size() > 0 && grps.size() > 0 && grps_Prev.get(0) == grps.get(0)){
				canCut = false;
			}
		}			

		return canCut;
	}

	private boolean canCutRow(int row){
		boolean canCut   = true;
		int     rowCount = getTable().getRowCount();
		if(getPrintParameters() != null && getTable().getModel() instanceof FTreeTableModel && row < rowCount && getPrintParameters().getLevelNotToSplit() > 0){
			FTreeTableModel treeTableModel = (FTreeTableModel) getTable().getModel();
			FNode           node           = treeTableModel.getNodeForRow(row  );
			FNode           node_Prev      = treeTableModel.getNodeForRow(row-1);
			FNode           nodeSplit      = treeTableModel.getNodeForRow(row  );
			FNode           nodeSplit_Prev = treeTableModel.getNodeForRow(row-1);

			FNode tempNode = node;
			while(tempNode != null){
				if(tempNode.getNodeDepth() == getPrintParameters().getLevelNotToSplit()){
					nodeSplit = tempNode;
				}
				tempNode = tempNode.getFatherNode();
			}

			tempNode = node_Prev;
			while(tempNode != null){
				if(tempNode.getNodeDepth() == getPrintParameters().getLevelNotToSplit()){
					nodeSplit_Prev = tempNode;
				}
				tempNode = tempNode.getFatherNode();
			}

			if(nodeSplit != null && nodeSplit == nodeSplit_Prev){
				canCut = false;
			}
		}			

		return canCut;
	}

	private boolean shouldSkipThisRow(int row){
		boolean shouldSkipThisRow = false;
		if(getPrintParameters() != null && getPrintParameters().getPageBreakTreeLevel() > 0){
			if(getTable().getModel() instanceof FTreeTableModel){
				FTreeTableModel treeTableModel = (FTreeTableModel) getTable().getModel();
				FNode           node           = treeTableModel.getNodeForRow(row);
				
				shouldSkipThisRow = node.getNodeDepth() < getPrintParameters().getPageBreakTreeLevel();
			}
		}
		return shouldSkipThisRow;
	}

	private boolean isNodeUnbreakeable(int row, boolean orHigherThanUnbreakeable){
		boolean unbreak = false;				
		if(getPrintParameters().getLevelNotToSplit() > 0){
			if(getTable().getModel() instanceof FTreeTableModel){
				FTreeTableModel treeTableModel = (FTreeTableModel) getTable().getModel();
				FNode           node           = treeTableModel.getNodeForRow(row);
				
				if(orHigherThanUnbreakeable){
					unbreak = node.getNodeDepth() <= getPrintParameters().getLevelNotToSplit();
				}else{
					unbreak = node.getNodeDepth() == getPrintParameters().getLevelNotToSplit();
				}
			}
		}
		return unbreak;
	}

	private boolean isNodeHigherThanUnbreakeable(int row){
		boolean unbreak = false;				
		if(getPrintParameters().getPageBreakTreeLevel() > 0){
			if(getTable().getModel() instanceof FTreeTableModel){
				FTreeTableModel treeTableModel = (FTreeTableModel) getTable().getModel();
				FNode           node           = treeTableModel.getNodeForRow(row);
				
				unbreak = node.getNodeDepth() < getPrintParameters().getLevelNotToSplit();
			}
		}
		return unbreak;
	}

	private FTablePrintParameters getPrintParameters(){
		return tablePrintable.getPrintParameters();
	}
	
	private FTable getTable(){
		return tablePrintable.getTable();
	}

	private FTable getFixTable(){
		return tablePrintable.getFixTable();
	}

	private TableColumnModel getColumnModel(){
		return tablePrintable.getTable().getColumnModel();
	}
	
	private FPageHeaderAndFooter getPageHeaderAndFooter(){
		return tablePrintable.getPageHeaderAndFooter();
	}
	
	public void print(){
		// translate into the co-ordinate system of the pageFormat
		Graphics2D g2d = (Graphics2D) tablePrintable.getGraphics();
		
		g2d.translate(0, getPageHeaderAndFooter().getTableTopY());
		
		AffineTransform veryOldTrans = g2d.getTransform();
		Shape veryOldClip = g2d.getClip();
		
		// constrain the table output to the available space
		/*
		Rectangle tempRect = new Rectangle();
		tempRect.x = 0;//getFixTableWidth_();
		tempRect.y = 0;
		tempRect.width = (int) (getAvailableWidth() / scaleFactor);
		tempRect.height = (int) (getAvailableSpace() / scaleFactor);
		g2d.clip(tempRect);
		 */
		g2d.scale(scaleFactor, scaleFactor);
		
		//g2d.translate(getFixTableWidth_Scaled(), 0);//Barmaja
		g2d.translate(tablePrintable.getFixTableWidth(), 0);
		
		// store the old transform and clip for later restoration
		AffineTransform oldTrans = g2d.getTransform();
		Shape oldClip = g2d.getClip();
		
		// if there's a table header, print the current section and
		// then translate downwards
		JTableHeader header = tablePrintable.getTable().getTableHeader();
		if(header != null){
			Rectangle hclip = new Rectangle(); 
			hclip.x      = clip.x;
			hclip.width  = clip.width;
			hclip.y      = 0;			
			hclip.height = header.getHeight();

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
		getTable().print(g2d);

		// restore the original transform and clip
		g2d.setTransform(oldTrans);
		g2d.setClip(oldClip);

		// draw a box around the table
		g2d.setColor(Color.BLACK);
		// g2d.drawRect(0, 0, clip.width, hclip.height + clip.height);

		Rectangle fixClip = new Rectangle();
		fixClip.x      = 0;
		fixClip.y      = clip.y;
		fixClip.width  = tablePrintable.getFixTableWidth();
		fixClip.height = clip.height;

		// restore the very very original transform and clip
		g2d.setTransform(veryOldTrans);
		g2d.setClip(veryOldClip);

		// if we have a scale factor, scale the graphics object to fit
		// the entire width
		g2d.scale(scaleFactor, scaleFactor);
		
		if(getFixTable() != null){
			Rectangle hFixClip = new Rectangle();
			// if there's a table header, print the current section and
			// then translate downwards
			if(getFixTable().getTableHeader() != null){
				hFixClip.x      = fixClip.x;
				hFixClip.width  = fixClip.width;
				hFixClip.y      = 0;			
				hFixClip.height = header.getHeight();
					
				g2d.translate(-hFixClip.x, 0);
				g2d.clip(hFixClip);
				getFixTable().getTableHeader().print(g2d);
	
				// restore the original transform and clip
				g2d.setTransform(veryOldTrans);
				g2d.setClip(veryOldClip);
				g2d.scale(scaleFactor, scaleFactor);
	
				// translate downwards
				g2d.translate(0, hFixClip.height);
			}
	
			// print the current section of the table
			g2d.translate(-fixClip.x, -fixClip.y);
			g2d.clip(fixClip);
			getFixTable().print(g2d);
	
			// restore the original transform and clip
			g2d.setTransform(veryOldTrans);
			g2d.setClip(veryOldClip);
			g2d.scale(scaleFactor, scaleFactor);
			
			// draw a box around the table
			g2d.setColor(Color.BLACK);
			//g2d.drawRect(0, 0, fixClip.width, hclip.height + fixClip.height);
		}
		
		
		
		
		/*
		// to save and store the transform
		AffineTransform oldTrans;

		// if there's footer text, print it at the bottom of the imageable area
		if(footerText != null){
			oldTrans = g2d.getTransform();
			g2d.translate(0, imgHeight - footerTextSpace);
			printText(g2d, footerText, fRect, tablePrintable.getFooterFont(), imgWidth);
			
			String dateStr = getPrintParameters().getPrintDate();
			if(dateStr != null && !dateStr.isEmpty()){
				Rectangle2D rect = g2d.getFontMetrics().getStringBounds(dateStr, g2d);
				
				g2d.drawString(dateStr, (int)(imgWidth - rect.getWidth()), (int)rect.getHeight());
				printText(g2d, footerText, fRect, tablePrintable.getFooterFont(), imgWidth);
			}
			
			g2d.setTransform(oldTrans);
		}

		// if there's header text, print it at the top of the imageable area
		// and then translate downwards
		if(headerText != null){
			printText(g2d, headerText, hRect, tablePrintable.getHeaderFont(), imgWidth);
			g2d.translate(0, headerTextSpace + FTablePrintable.H_F_SPACE);
		}

		//Print Page Footer
		oldTrans = g2d.getTransform();
		g2d.translate(0, imgHeight - footerTextSpace - tablePrintable.getPrintParameters().getPageFooterHeight());
		tablePrintable.getPrintParameters().printFooter(g2d);
		g2d.setTransform(oldTrans);

		//Print Page Header
		tablePrintable.getPrintParameters().printHeader(g2d);
		g2d.translate(0, tablePrintable.getPrintParameters().getPageHeaderHeight());
				
		AffineTransform veryOldTrans = g2d.getTransform();
		Shape veryOldClip = g2d.getClip();
		
		// constrain the table output to the available space
		g2d.scale(scaleFactor, scaleFactor);
		
		//g2d.translate(getFixTableWidth_Scaled(), 0);//Barmaja
		g2d.translate(tablePrintable.getFixTableWidth(), 0);
		
		// store the old transform and clip for later restoration
		oldTrans = g2d.getTransform();
		Shape oldClip = g2d.getClip();
		
		// if there's a table header, print the current section and
		// then translate downwards
		JTableHeader header = tablePrintable.getTable().getTableHeader();
		if(header != null){
			Rectangle hclip = new Rectangle(); 
			hclip.x      = clip.x;
			hclip.width  = clip.width;
			hclip.y      = 0;			
			hclip.height = header.getHeight();

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
		getTable().print(g2d);

		// restore the original transform and clip
		g2d.setTransform(oldTrans);
		g2d.setClip(oldClip);

		// draw a box around the table
		g2d.setColor(Color.BLACK);
		// g2d.drawRect(0, 0, clip.width, hclip.height + clip.height);

		Rectangle fixClip = new Rectangle();
		fixClip.x      = 0;
		fixClip.y      = clip.y;
		fixClip.width  = tablePrintable.getFixTableWidth();
		fixClip.height = clip.height;

		// restore the very very original transform and clip
		g2d.setTransform(veryOldTrans);
		g2d.setClip(veryOldClip);

		// if we have a scale factor, scale the graphics object to fit
		// the entire width
		g2d.scale(scaleFactor, scaleFactor);
		
		if(getFixTable() != null){
			Rectangle hFixClip = new Rectangle();
			// if there's a table header, print the current section and
			// then translate downwards
			if(getFixTable().getTableHeader() != null){
				hFixClip.x      = fixClip.x;
				hFixClip.width  = fixClip.width;
				hFixClip.y      = 0;			
				hFixClip.height = header.getHeight();
					
				g2d.translate(-hFixClip.x, 0);
				g2d.clip(hFixClip);
				getFixTable().getTableHeader().print(g2d);
	
				// restore the original transform and clip
				g2d.setTransform(veryOldTrans);
				g2d.setClip(veryOldClip);
				g2d.scale(scaleFactor, scaleFactor);
	
				// translate downwards
				g2d.translate(0, hFixClip.height);
			}
	
			// print the current section of the table
			g2d.translate(-fixClip.x, -fixClip.y);
			g2d.clip(fixClip);
			getFixTable().print(g2d);
	
			// restore the original transform and clip
			g2d.setTransform(veryOldTrans);
			g2d.setClip(veryOldClip);
			g2d.scale(scaleFactor, scaleFactor);
			
			// draw a box around the table
			g2d.setColor(Color.BLACK);
			//g2d.drawRect(0, 0, fixClip.width, hclip.height + fixClip.height);
		}
		*/
	}
	
	public void debugOutput(){
		if(pageIndex < 50){
			Globals.logString(" Page : "+pageIndex+ " scale = "+scaleFactor);
			Globals.logString("     Row  : "+getRowStart()+" , "+getRowEnd());
			Globals.logString("     Col  : "+getColStart()+" , "+getColEnd());
			Globals.logString("     clip : "+clip.x+", "+clip.y+", "+clip.width+", "+clip.height);
		}
	}
}
