package com.jimi.smt.eps_server.entity;

public class Page {

	private Integer firstIndex;
	private Integer currentPage;
	private Integer pageSize;

	public Integer getFirstIndex() {
		firstIndex = ((this.getCurrentPage() - 1) * this.getPageSize());
		return firstIndex;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		if (currentPage == null || currentPage <= 0) {
			this.currentPage = 1;
		} else {
			this.currentPage = currentPage;
		}
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		if (pageSize == null || pageSize <= 0) {
			this.pageSize = 20;
		} else {
			this.pageSize = pageSize;
		}
	}

}
