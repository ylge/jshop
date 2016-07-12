package $!{packageName}.action;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.easyjf.mvc.ActionUtil;
import com.easyjf.util.CommUtil;
import com.easyjf.web.Module;
import com.easyjf.web.Page;
import com.easyjf.web.WebForm;
import com.easyjf.web.tools.AbstractCmdAction;
import com.easyjf.web.tools.IPageList;
import com.easyjf.core.support.CommUtilForTeaec;

import $!{packageName}.service.I$!{domainName}Service;
import $!{packageName}.domain.$!{domainName};
import $!{packageName}.query.$!{domainName}QueryObject;
import $!{packageName}.command.$!{domainName}Command;


##set ($domain = $!domainName.toLowerCase())
public class $!{domainName}Action extends AbstractCmdAction {
	
	List<String> errors = new ArrayList<String>();
	
	private I$!{domainName}Service service;

	public void setService(I$!{domainName}Service service) {
		this.service = service;
	}

	@Override
	public Page doInit(WebForm arg0, Module arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 客戶
	 * @param form
	 * @param module
	 * @return
	 */
	public Page doList(WebForm form, Module module) {
		$!{domainName}QueryObject qo = new $!{domainName}QueryObject();
		form.toPo(qo);
		qo.setAll(true);
		IPageList pageList = service.get$!{domainName}By(qo);
		CommUtilForTeaec.saveIPageList2WebForm(pageList, form);
		return module.findPage("list");
	}
	
	public Page doDel(WebForm form, Module module) {
		List<Serializable> ids = ActionUtil.processIds(form);
		boolean ret=this.service.batchDel$!{domainName}s(ids);	
		return this.doList(form, module);
	}

	public Page doEdit(WebForm form, Module module) {
		Long id = Long.parseLong(CommUtil.null2String(form.get("id")));
		$!domainName $!domain = this.service.get$!domainName(id);
		if ($!domain != null) {
			form.addPo($!domain);
		}
		return module.findPage("edit");
	}

	public Page doUpdate(WebForm form, Module module) {
		Long id = Long.parseLong(CommUtil.null2String(form.get("id")));
		$!{domainName}Command cmd = new $!{domainName}Command();
		form.toPo(cmd);
		errors = cmd.vaild();
		if(errors.size()>0){
			form.addResult("errors", errors);
			return module.findPage("edit");
		}
		$!{domainName} $!domain = this.service.get$!{domainName}(id);
		com.easyjf.beans.BeanUtils.copyProperties(cmd, $!domain);
		boolean ret = this.service.update$!{domainName}(id, $!domain);		
		return this.doList(form, module);
	}

	public Page doAdd(WebForm form, Module module) {
		return module.findPage("edit");
	}

	public Page doSave(WebForm form, Module module) {
		$!{domainName}Command cmd = new $!{domainName}Command();
		form.toPo(cmd);		
		errors = cmd.vaild();
		if(errors != null && errors.size()>0){
			form.addResult("errors", errors);			
			return module.findPage("edit");
		}		
		$!{domainName} $!domain = new $!{domainName}();
		com.easyjf.beans.BeanUtils.copyProperties(cmd, $!domain);
		Long ret = this.service.add$!{domainName}($!domain);		
		return this.doList(form, module);
	}
}