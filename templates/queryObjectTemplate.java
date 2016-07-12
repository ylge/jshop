##set ($domain = $!domainName.toLowerCase())
package $!{packageName}.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.query.QueryObject;

public class $!{domainName}QueryObject extends QueryObject {
	
	public $!{domainName}QueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public $!{domainName}QueryObject(String construct, String currentPage,
			ModelAndView mv, String orderBy, String orderType) {
		super(construct, currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}

	public $!{domainName}QueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
