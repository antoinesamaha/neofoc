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
package com.foc.print;

import java.awt.Graphics;

import com.foc.gui.table.print.FTablePrintable;

public class FTablePrintParameters implements ITablePrintParameters {
	private String          dateString         = null;
	private boolean         fitWidth           = false;
	private double          scale              = 1 ;
	private int             pageBreakTreeLevel = -1;
	private int             levelNotToSplit    = -1;
	private FTablePrintable tablePrintable     = null;
	
	public FTablePrintable getTablePrintable() {
		return tablePrintable;
	}

	public FTablePrintParameters(){
	}
	
	@Override
	public void dispose(){
		tablePrintable = null;
	}

	public boolean isFitWidth() {
		return fitWidth;
	}

	public void setFitWidth(boolean fitWidth) {
		this.fitWidth = fitWidth;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public int getPageBreakTreeLevel() {
		return pageBreakTreeLevel;
	}

	public void setPageBreakTreeLevel(int pageBreakTreeLevel) {
		this.pageBreakTreeLevel = pageBreakTreeLevel;
	}

	public int getLevelNotToSplit() {
		return levelNotToSplit;
	}

	public void setLevelNotToSplit(int levelNotToSplit) {
		this.levelNotToSplit = levelNotToSplit;
	}

	public String getPrintDate() {
		return this.dateString;
	}

	public void setPrintDate(String dateString) {
		this.dateString = dateString;
	}

	@Override
	public void setPrintable(FTablePrintable tablePrintable) {
		this.tablePrintable = tablePrintable;
	}

	@Override
	public int getPageFooterHeight() {
		return 0;
	}

	@Override
	public int getPageHeaderHeight() {
		return 0;
	}

	@Override
	public void printHeader(Graphics graphics) {
	}
	
	@Override
	public void printFooter(Graphics graphics) {
	}
}
