##set ($domain = $!domainName.toLowerCase())
package $!{packageName}.query;
import java.util.List;

import com.easyjf.core.support.query.BaseQueryObject;


public class $!{domainName}QueryObject extends BaseQueryObject {
	private String id;

	private String parentId;

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getQuery() {
		if (parentId == null||"".equals(parentId)) {
			return " 1=1 and obj.parent.id is null " + orderString();
		} else {
			return " 1=1 and obj.parent.id =" + parentId + " " + orderString();
		}
	}

	@Override
	public List getParamters() {
		// TODO Auto-generated method stub
		return this.params;
	}
}
