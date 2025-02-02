package com.foc.db;

public class ListPagination {
	public static final int DEFAULT_PAGE_NBR_OF_ROWS = 100; 
	
	private int pagesCount    =   1;
	private int currentPage   =   1;
	private int pageNbrOfRows = DEFAULT_PAGE_NBR_OF_ROWS;
	private int listMaxCount  =   0;
	
	private int offset        =  -1;
	private int offsetCount   =  -1;

	public ListPagination() {
		
	}
	
	public void dispose() {
	}

	public void reset() {
		currentPage   =   1;
		offset        =   0;
	}
	
	public int getPagesCount() {
		return pagesCount;
	}

	public void setPagesCount(int pagesCount) {
		this.pagesCount = pagesCount;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public boolean setCurrentPage(int currentPage) {
		boolean refresh = this.currentPage != currentPage;
		this.currentPage = currentPage;
		return refresh;
	}

	public int getPageNbrOfRows() {
		return pageNbrOfRows;
	}

	public void setPageNbrOfRows(int pageNbrOfRows) {
		this.pageNbrOfRows = pageNbrOfRows;
	}

	public int getListMaxCount() {
		return listMaxCount;
	}

	public void setListMaxCount(int listMaxCount) {
		this.listMaxCount = listMaxCount;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getOffsetCount() {
		return offsetCount;
	}

	public void setOffsetCount(int offsetCount) {
		this.offsetCount = offsetCount;
	}
	
	public void computeNbrOfPages() {
		if(listMaxCount > 0 && pageNbrOfRows > 0) {
			pagesCount = listMaxCount / pageNbrOfRows;
			if(listMaxCount % pageNbrOfRows > 0) {
				pagesCount++;
			}
		}
		if(pagesCount <= 0) pagesCount = 1;
	}
	
	public void computeOffsetAndCount() {
		if (currentPage > 0) {
			offset = (currentPage-1) * pageNbrOfRows;
			offsetCount = pageNbrOfRows;
		}
	}
}
