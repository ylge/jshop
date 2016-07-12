package com.iskyshop.module.cms.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.module.cms.domain.CmsIndexTemplate;

public interface ICmsIndexTemplateService {
	/**
	 * 保存一个CmsIndexTemplate，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(CmsIndexTemplate instance);
	
	/**
	 * 根据一个ID得到CmsIndexTemplate
	 * 
	 * @param id
	 * @return
	 */
	CmsIndexTemplate getObjById(Long id);
	
	/**
	 * 删除一个CmsIndexTemplate
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除CmsIndexTemplate
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到CmsIndexTemplate
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个CmsIndexTemplate
	 * 
	 * @param id
	 *            需要更新的CmsIndexTemplate的id
	 * @param dir
	 *            需要更新的CmsIndexTemplate
	 */
	boolean update(CmsIndexTemplate instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<CmsIndexTemplate> query(String query, Map params, int begin, int max);
}
