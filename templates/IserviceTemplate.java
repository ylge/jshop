package $!{packageName}.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import $!{packageName}.domain.$!{domainName};

public interface I$!{domainName}Service {
	/**
	 * 保存一个$!{domainName}，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save($!{domainName} instance);
	
	/**
	 * 根据一个ID得到$!{domainName}
	 * 
	 * @param id
	 * @return
	 */
	$!{domainName} getObjById(Long id);
	
	/**
	 * 删除一个$!{domainName}
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除$!{domainName}
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到$!{domainName}
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个$!{domainName}
	 * 
	 * @param id
	 *            需要更新的$!{domainName}的id
	 * @param dir
	 *            需要更新的$!{domainName}
	 */
	boolean update($!{domainName} instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<$!{domainName}> query(String query, Map params, int begin, int max);
}
