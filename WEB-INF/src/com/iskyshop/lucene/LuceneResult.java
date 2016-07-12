package com.iskyshop.lucene;

import java.util.ArrayList;
import java.util.List;
/**
 * 
* <p>Title: LuceneResult.java</p>

* <p>Description: </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-24

* @version iskyshop_b2b2c v2.0 2015版 
 */
public class LuceneResult {
	private List<LuceneVo> vo_list = new ArrayList<LuceneVo>();
	private int pages;// 总页数
	private int rows;// 总记录数
	private int currentPage;// 当前页码
	private int pageSize;// 每页大小

	
	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<LuceneVo> getVo_list() {
		return vo_list;
	}

	public void setVo_list(List<LuceneVo> vo_list) {
		this.vo_list = vo_list;
	}

}
