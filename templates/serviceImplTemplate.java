#set ($domain = $!domainName.substring(0,1).toLowerCase()+$!domainName.substring(1))
package $!{packageName}.service.impl;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import $!{packageName}.domain.$!{domainName};
import $!{packageName}.service.I$!{domainName}Service;

#macro (upperCase $str)
#set ($upper=$!str.substring(0,1).toUpperCase())
#set ($l=$!str.substring(1))
$!upper$!l#end
@Service
@Transactional
public class $!{domainName}ServiceImpl implements I$!{domainName}Service{
	@Resource(name = "$!{domain}DAO")
	private IGenericDAO<$!{domainName}> $!{domain}Dao;
	
	public boolean save($!{domainName} $!{domain}) {
		/**
		 * init other field here
		 */
		try {
			this.$!{domain}Dao.save($!{domain});
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public $!{domainName} getObjById(Long id) {
		$!{domainName} $!{domain} = this.$!{domain}Dao.get(id);
		if ($!{domain} != null) {
			return $!{domain};
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.$!{domain}Dao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> $!{domain}Ids) {
		// TODO Auto-generated method stub
		for (Serializable id : $!{domain}Ids) {
			delete((Long) id);
		}
		return true;
	}
	
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList($!{domainName}.class, construct,query,
				params, this.$!{domain}Dao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
						.getCurrentPage(), pageObj.getPageSize() == null ? 0
						: pageObj.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}
	
	public boolean update($!{domainName} $!{domain}) {
		try {
			this.$!{domain}Dao.update( $!{domain});
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<$!{domainName}> query(String query, Map params, int begin, int max){
		return this.$!{domain}Dao.query(query, params, begin, max);
		
	}
}
