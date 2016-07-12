##set ($domain = $!domainName.toLowerCase())
package $!{packageName}.service.impl;
import java.io.Serializable;
import java.util.List;

import com.easyjf.core.support.GenericPageList;
import com.easyjf.core.support.query.IQueryObject;
import com.easyjf.core.support.query.PageObject;
import com.easyjf.web.tools.IPageList;
import $!{packageName}.domain.$!{domainName};
import $!{packageName}.service.I$!{domainName}Service;
import $!{packageName}.dao.I$!{domainName}DAO;

#macro (upperCase $str)
#set ($upper=$!str.substring(0,1).toUpperCase())
#set ($l=$!str.substring(1))
$!upper$!l#end
public class $!{domainName}ServiceImpl implements I$!{domainName}Service{
	
	private I$!{domainName}DAO $!{domain}Dao;
	
	public void set#upperCase($!{domain})Dao(I$!{domainName}DAO $!{domain}Dao){
		this.$!{domain}Dao=$!{domain}Dao;
	}
	
	public Long add$!{domainName}($!{domainName} $!{domain}) {
		/**
		 * init other field here
		 */
		this.$!{domain}Dao.save($!{domain});
		if ($!{domain} != null && $!{domain}.get$!{id}() != null) {
			return $!{domain}.get$!{id}();
		}
		return null;
	}
	
	public $!{domainName} get$!{domainName}(Long id) {
		$!{domainName} $!{domain} = this.$!{domain}Dao.get(id);
		if ($!{domain} != null) {
			return $!{domain};
		}
		return null;
	}
	
	public boolean del$!{domainName}(Long id) {
		if (id != null) {
			this.$!{domain}Dao.remove(id);
			$!{domainName} $!{domain} = this.get$!{domainName}(id);
			if ($!{domain} == null) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	public boolean batchDel$!{domainName}s(List<Serializable> $!{domain}Ids) {
		// TODO Auto-generated method stub
		for (Serializable id : $!{domain}Ids) {
			del$!{domainName}((Long) id);
		}
		return true;
	}
	
	public IPageList get$!{domainName}By(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		List params = properties.getParamters();
		GenericPageList pList = new GenericPageList($!{domainName}.class, query,
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
	
	public boolean update$!{domainName}(Long id, $!{domainName} $!{domain}) {
		if (id != null) {
			$!{domain}.set$!{id}(id);
		} else {
			return false;
		}
		this.$!{domain}Dao.update($!{domain});
		return true;
	}	
	
}
