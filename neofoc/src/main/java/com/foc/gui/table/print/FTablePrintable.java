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
import javax.swing.JTable.PrintMode;
import javax.swing.table.*;

import com.foc.gui.table.FTable;
import com.foc.print.FTablePrintParameters;

import java.awt.*;
import java.awt.print.*;
import java.text.MessageFormat;
import java.util.ArrayList;

public class FTablePrintable implements Printable {

	private ArrayList<FTablePage> pagesArray = null;
	private Graphics              graphics   = null;
	private PageFormat            pageFormat = null;
	private JTable.PrintMode      printMode  = JTable.PrintMode.NORMAL;
	
	/** The table to print. */
	private FTable table                  = null;
	private FTable fixTable               = null;
	private double defaultScaleFactor     = 1;

	/** For quick reference to the table's header. */
	private JTableHeader header;

	/** For quick reference to the table's column model. */
	private TableColumnModel colModel;

	/** To save multiple calculations of total column width. */
	private int totalColWidth;

	/** Provides the header text for the table. */
	private MessageFormat headerFormat;

	/** Provides the footer text for the table. */
	private MessageFormat footerFormat;

	/** Used to store an area of the table's header to be printed. */
	private final Rectangle hclip    = new Rectangle(0, 0, 0, 0);
	private final Rectangle hFixClip = new Rectangle(0, 0, 0, 0);

	/** Vertical space to leave between table and header/footer text. */
	public static final int H_F_SPACE = 4;

	/** Font size for the header text. */
	public static final float HEADER_FONT_SIZE = 18.0f;

	/** Font size for the footer text. */
	public static final float FOOTER_FONT_SIZE = 12.0f;

	/** The font to use in rendering header text. */
	private Font headerFont;

	/** The font to use in rendering footer text. */
	private Font footerFont;

	private FPageHeaderAndFooter pageHeaderAndFooter = null;
	
	public FPageHeaderAndFooter getPageHeaderAndFooter() {
		return pageHeaderAndFooter;
	}

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
	public FTablePrintable(JTable table, JTable.PrintMode printMode, MessageFormat headerFormat, MessageFormat footerFormat) {

		this.printMode = printMode;
		this.table    = (FTable) table;
		if(this.table.getScrollPane() != null && this.table.getScrollPane().getFixedTable() != null){
			fixTable = this.table.getScrollPane().getFixedTable();
		}

		header        = table.getTableHeader();
		colModel      = table.getColumnModel();
		//((FTable)table).computeMaxColWidth();
		//totalColWidth = ((FTable)table).getMaxColumnWidth();
		totalColWidth = colModel.getTotalColumnWidth();
			
		hclip.height = 0;//getPrintParameters().getPageHeaderHeight();//Adding Page header to the table header
		if(header != null){
			// the header clip height can be set once since it's unchanging
			hclip.height += header.getHeight();
		}

		hFixClip.height = 0;//getPrintParameters().getPageHeaderHeight();//Adding Page header to the table header
		if(fixTable != null && fixTable.getTableHeader() != null){
			hFixClip.height += fixTable.getTableHeader().getHeight();
		}
		
		this.headerFormat = headerFormat;
		this.footerFormat = footerFormat;

		// derive the header and footer font from the table's font
		headerFont = table.getFont().deriveFont(Font.BOLD, HEADER_FONT_SIZE);
		footerFont = table.getFont().deriveFont(Font.PLAIN, FOOTER_FONT_SIZE);
		
		pagesArray = null;
	}
	
	public void dispose(){
		if(pagesArray != null){
			for(int i=0; i<pagesArray.size(); i++){
				FTablePage page = pagesArray.get(i);
				page.dispose();
			}
			pagesArray.clear();
			pagesArray = null;
		}
		
		table      = null;
		fixTable   = null;
		header     = null;
		headerFont = null;
		footerFont = null;
		pageFormat = null;
		graphics   = null;
		
		dispose_PageHeaderAndFooter();
	}

	public void dispose_PageHeaderAndFooter(){
		if(pageHeaderAndFooter != null){
			pageHeaderAndFooter.dispose();
			pageHeaderAndFooter = null;
		}
	}
	
	public void adjustScaleToFitWidthIfNeeded(){
		FTablePrintParameters printParameters = getPrintParameters();
		if(printMode == JTable.PrintMode.FIT_WIDTH || (printParameters != null && printParameters.isFitWidth())){
			int totalTableWidth = 0;
			if(table != null){
				totalTableWidth += table.getColumnModel().getTotalColumnWidth();
			}
			if(fixTable != null){
				totalTableWidth += fixTable.getColumnModel().getTotalColumnWidth();
			}
	
			if(totalTableWidth > getPageFormat().getImageableWidth()){
				double minScale = getPageFormat().getImageableWidth() / totalTableWidth;
				if(minScale < printParameters.getScale()){
					minScale = Math.floor(minScale * 100)/100;
					printParameters.setScale(minScale);
				}
			}
		}
		
		//Getting the default scale factor
		defaultScaleFactor = 1.0D;
		if(printParameters != null){
			defaultScaleFactor = printParameters.getScale();
		}
	}
	
	public void buildPagesArrayIfNeeded(Graphics graphics, PageFormat pageFormat){
		if(pagesArray == null){
			pagesArray = new ArrayList<FTablePage>();

			FTablePage prevPage  = null;
			int        pageIndex = 0;
			boolean    error     = false;
			while(!error){
				FTablePage page = new FTablePage(this, pageIndex + 1);
				error = page.compute(prevPage);
				page.debugOutput();
				
				if(!error){
					addPage(page);
				}
				
				prevPage = page;
				pageIndex++;
			}
			
			//get min scale factor
			double minScaleFactor = getDefaultScaleFactor();
			for(int i=0; i<pagesArray.size(); i++){
				FTablePage page = pagesArray.get(i);
				double sf = page.getScaleFactor();
				if(sf < minScaleFactor) minScaleFactor = sf;
			}
			for(int i=0; i<pagesArray.size(); i++){
				FTablePage page = pagesArray.get(i);
				page.setScaleFactor(minScaleFactor);
			}			
		}
	}
	
	public FTablePage addPage(FTablePage page){
		pagesArray.add(page);
		return page;
	}
	
	public FTablePage getPage(int at){
		return (pagesArray != null && at < pagesArray.size()) ? pagesArray.get(at) : null;
	}
	
	public FTablePage getFirstPageForColumn(int col){
		FTablePage firstPage = null;
		for(int i=0; i<pagesArray.size() && firstPage == null; i++){
			FTablePage page = pagesArray.get(i);
			if(page.getColStart() == col){
				firstPage = page;
			}
		}
		return firstPage;
	}
	
	public int getFixTableWidth(){
		return fixTable != null ? fixTable.getWidth(): 0;
	}

	public FTablePrintParameters getPrintParameters(){
		FTablePrintParameters printParams = null;
		if(table != null){
			printParams = table.getPrintParameters();
			if(printParams != null){
				printParams.setPrintable(this);
				printParams.setFitWidth(printMode == PrintMode.FIT_WIDTH);
			}
		}
		return printParams;
	}

	public MessageFormat getHeaderFormat() {
		return headerFormat;
	}

	public void setHeaderFormat(MessageFormat headerFormat) {
		this.headerFormat = headerFormat;
	}

	public MessageFormat getFooterFormat() {
		return footerFormat;
	}

	public void setFooterFormat(MessageFormat footerFormat) {
		this.footerFormat = footerFormat;
	}

	public Font getHeaderFont() {
		return headerFont;
	}

	public void setHeaderFont(Font headerFont) {
		this.headerFont = headerFont;
	}

	public Font getFooterFont() {
		return footerFont;
	}

	public void setFooterFont(Font footerFont) {
		this.footerFont = footerFont;
	}

	public Graphics getGraphics() {
		return graphics;
	}
	
	public PageFormat getPageFormat() {
		return pageFormat;
	}

	public FTable getTable() {
		return table;
	}	
	
	public int getTotalColWidth() {
		return totalColWidth;
	}
	
	public FTable getFixTable() {
		FTable fixTable = null;
		if(this.table.getScrollPane() != null && this.table.getScrollPane().getFixedTable() != null){
			fixTable = this.table.getScrollPane().getFixedTable();
		}
		return fixTable;
	}	
	
	public double getDefaultScaleFactor(){
		return defaultScaleFactor;
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
		this.graphics   = graphics;
		this.pageFormat = pageFormat;

		adjustScaleToFitWidthIfNeeded();
		
		//This is called before the build pages so that the exact page header and footer heights are known
		printTableHeaderAndFooter(graphics, pageFormat, pageIndex);
		
		//The first time only this is needed
		buildPagesArrayIfNeeded(graphics, pageFormat);

		if(pageIndex >= pagesArray.size()){
			return NO_SUCH_PAGE;			
		}
		
		FTablePage tablePage = pagesArray.get(pageIndex);
		tablePage.print();
		
		pageFormat = null;
		graphics   = null;
		
		return PAGE_EXISTS;
	}
	
	private void printTableHeaderAndFooter(Graphics graphics, PageFormat pageFormat, int pageIndex){
		dispose_PageHeaderAndFooter();
		pageHeaderAndFooter = new FPageHeaderAndFooter(this);
		pageHeaderAndFooter.print(graphics, pageFormat, pageIndex);
	}
}
