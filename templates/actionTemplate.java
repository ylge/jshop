package $!{packageName}.manage.action;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.domain.GoodsBrand;
import com.iskyshop.domain.LogType;
import com.iskyshop.domain.Role;
import com.iskyshop.domain.User;
import com.iskyshop.domain.query.UserQueryObject;
import com.iskyshop.service.ISysConfigService;
import com.iskyshop.service.IUserConfigService;
import com.iskyshop.service.IUserService;
import com.iskyshop.core.annotation.Log;
import com.iskyshop.core.annotation.SecurityMapping;
import $!{packageName}.service.I$!{domainName}Service;
import $!{packageName}.domain.$!{domainName};
import $!{packageName}.domain.query.$!{domainName}QueryObject;

##set ($domain = $!domainName.toLowerCase())
@Controller
public class $!{domainName}ManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService  userConfigService;
	@Autowired
	private I$!{domainName}Service $!{domain}Service;	
	/**
	 * $!{domainName}列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@RequestMapping("/admin/$!{domain}_list.htm")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response,String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/$!{domain}_list.html", configService
				.getSysConfig(),this.userConfigService.getUserConfig(), 0, request,response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		$!{domainName}QueryObject qo = new $!{domainName}QueryObject(currentPage, mv, orderBy,
				orderType);
		// WebForm wf = new WebForm();
		// wf.toQueryPo(request, qo,$!{domainName}.class,mv);
		IPageList pList = this.$!{domain}Service.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/$!{domain}_list.htm","",
				params, pList, mv);
		return mv;
	}
	/**
	 * $!{domain}添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/admin/$!{domain}_add.htm")
	public ModelAndView add(HttpServletRequest request,HttpServletResponse response,String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/$!{domain}_add.html", configService
				.getSysConfig(),this.userConfigService.getUserConfig(), 0, request,response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	/**
	 * $!{domain}编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/admin/$!{domain}_edit.htm")
	public ModelAndView edit(HttpServletRequest request,HttpServletResponse response,String id,String currentPage)
        {
		ModelAndView mv = new JModelAndView("admin/blue/$!{domain}_add.html", configService
				.getSysConfig(),this.userConfigService.getUserConfig(), 0, request,response);
		if (id != null&&!id.equals("")) {
			$!{domainName} $!{domain} = this.$!{domain}Service.getObjById(Long.parseLong(id));
				mv.addObject("obj", $!{domain});
		    mv.addObject("currentPage", currentPage);
		    mv.addObject("edit", true);
		}
		return mv;
	}
     /**
		 * $!{domain}保存管理
		 * 
		 * @param id
		 * @return
		 */
	@RequestMapping("/admin/$!{domain}_save.htm")
	public ModelAndView save(HttpServletRequest request,HttpServletResponse response, String id,String currentPage, String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		$!{domainName} $!{domain} =null;
		if (id.equals("")) {
			 $!{domain} = wf.toPo(request, $!{domainName}.class);
			$!{domain}.setAddTime(new Date());
		}else{
			$!{domainName} obj=this.$!{domain}Service.getObjById(Long.parseLong(id));
			$!{domain} = ($!{domainName})wf.toPo(request,obj);
		}
		
		if (id.equals("")) {
			this.$!{domain}Service.save($!{domain});
		} else
			this.$!{domain}Service.update($!{domain});
	   ModelAndView mv = new JModelAndView("admin/blue/success.html",
					configService.getSysConfig(), this.userConfigService
							.getUserConfig(), 0, request,response);
			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "保存$!{domain}成功");
			if (add_url != null) {
				mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
			}
		return mv;
	}
	@RequestMapping("/admin/$!{domain}_del.htm")
	public String delete(HttpServletRequest request,HttpServletResponse response,String mulitId,String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
			  $!{domainName} $!{domain} = this.$!{domain}Service.getObjById(Long.parseLong(id));
			  this.$!{domain}Service.delete(Long.parseLong(id));
			}
		}
		return "redirect:$!{domain}_list.htm?currentPage="+currentPage;
	}
	@RequestMapping("/admin/$!{domain}_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		$!{domainName} obj = this.$!{domain}Service.getObjById(Long.parseLong(id));
		Field[] fields = $!{domainName}.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			// System.out.println(field.getName());
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if (field.getType().getName().equals("int")) {
					clz = Class.forName("java.lang.Integer");
				}
				if (field.getType().getName().equals("boolean")) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!value.equals("")) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper
							.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.$!{domain}Service.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}