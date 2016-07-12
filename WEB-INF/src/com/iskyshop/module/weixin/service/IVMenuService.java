package com.iskyshop.module.weixin.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.module.weixin.domain.VMenu;

public interface IVMenuService {
	/**
	 * 保存一个VMenu，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(VMenu instance);

	/**
	 * 根据一个ID得到VMenu
	 * 
	 * @param id
	 * @return
	 */
	VMenu getObjById(Long id);

	/**
	 * 删除一个VMenu
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除VMenu
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到VMenu
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个VMenu
	 * 
	 * @param id
	 *            需要更新的VMenu的id
	 * @param dir
	 *            需要更新的VMenu
	 */
	boolean update(VMenu instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<VMenu> query(String query, Map params, int begin, int max);

	/**
	 * 
	 * @param propertyName
	 * @return
	 */
	VMenu getObjByProperty(String construct,String propertyName, Object value);
}
