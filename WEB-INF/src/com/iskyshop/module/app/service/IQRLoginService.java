package com.iskyshop.module.app.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.module.app.domain.QRLogin;

public interface IQRLoginService {
	/**
	 * 保存一个QRLogin，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(QRLogin instance);

	/**
	 * 根据一个ID得到QRLogin
	 * 
	 * @param id
	 * @return
	 */
	QRLogin getObjById(Long id);

	/**
	 * 删除一个QRLogin
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除QRLogin
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到QRLogin
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个QRLogin
	 * 
	 * @param id
	 *            需要更新的QRLogin的id
	 * @param dir
	 *            需要更新的QRLogin
	 */
	boolean update(QRLogin instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<QRLogin> query(String query, Map params, int begin, int max);
}
