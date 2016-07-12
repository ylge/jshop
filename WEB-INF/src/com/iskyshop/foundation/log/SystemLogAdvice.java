package com.iskyshop.foundation.log;

import java.lang.reflect.Method;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.ip.IPSeeker;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.LogFieldType;
import com.iskyshop.foundation.domain.SysLog;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.ISysLogService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: SystemLogAdvice.java
 * </p>
 * 
 * <p>
 * Description: 系统日志管理类，这里使用Spring环绕通知和异常通知进行动态管理,系统只记录管理员操作记录，对访问列表不进行记录
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang,hezeng
 * 
 * @date 2014-9-16
 * 
 * @version iskyshop_b2b2c v2.0 2015版 
 */

@Aspect
@Component
public class SystemLogAdvice {
	@Autowired
	private ISysLogService sysLogService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IUserService userSerivce;
	@Autowired
	private IAccessoryService accessoryService;

	// 记录管理员操作日志
	@After(value = "execution(* com.iskyshop.manage.admin..*.*(..)) && @annotation(annotation)&&args(request,..)")
	public void admin_op_log(JoinPoint joinPoint, SecurityMapping annotation,
			HttpServletRequest request) throws Exception {
		if (Globals.SAVE_LOG) {
			saveLog(joinPoint, annotation, request);
		}
	}

	@After(value = "execution(* com.iskyshop.module.weixin.manage..*.*(..)) && @annotation(annotation)&&args(request,..)")
	public void admin_weixin_log(JoinPoint joinPoint,
			SecurityMapping annotation, HttpServletRequest request)
			throws Exception {
		if (Globals.SAVE_LOG) {
			saveLog(joinPoint, annotation, request);
		}
	}

	@After(value = "execution(* com.iskyshop.module.app.manage..*.*(..)) && @annotation(annotation)&&args(request,..)")
	public void admin_app_log(JoinPoint joinPoint, SecurityMapping annotation,
			HttpServletRequest request) throws Exception {
		if (Globals.SAVE_LOG) {
			saveLog(joinPoint, annotation, request);
		}
	}

	@After(value = "execution(* com.iskyshop.module.cms.manage..*.*(..)) && @annotation(annotation)&&args(request,..)")
	public void admin_cms_log(JoinPoint joinPoint, SecurityMapping annotation,
			HttpServletRequest request) throws Exception {
		if (Globals.SAVE_LOG) {
			saveLog(joinPoint, annotation, request);
		}
	}

	@After(value = "execution(* com.iskyshop.module.circle.manage..*.*(..)) && @annotation(annotation)&&args(request,..)")
	public void admin_circle_log(JoinPoint joinPoint,
			SecurityMapping annotation, HttpServletRequest request)
			throws Exception {
		if (Globals.SAVE_LOG) {
			saveLog(joinPoint, annotation, request);
		}
	}

	@After(value = "execution(* com.iskyshop.module.sns.manage..*.*(..)) && @annotation(annotation)&&args(request,..)")
	public void admin_sns_log(JoinPoint joinPoint, SecurityMapping annotation,
			HttpServletRequest request) throws Exception {
		if (Globals.SAVE_LOG) {
			saveLog(joinPoint, annotation, request);
		}
	}

	private void saveLog(JoinPoint joinPoint, SecurityMapping annotation,
			HttpServletRequest request) throws Exception {
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userSerivce.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			// 获取操作内容
			String id = request.getParameter("id");
			String mulitId = request.getParameter("mulitId");
			String userName = SecurityUserHolder.getCurrentUser().getUserName();
			if (SecurityUserHolder.getCurrentUser().getTrueName() != null) {
				userName = userName + "（"
						+ SecurityUserHolder.getCurrentUser().getTrueName()
						+ "）";
			}
			String description = userName + "于"
					+ CommUtil.formatTime("yyyy-MM-dd HH:mm:ss", new Date());
			if (annotation.value().indexOf("index") > 0) {
				description = description + "管理员登录";
			} else if (annotation.value().indexOf("admin_pws_save") > 0) {
				String pws = request.getParameter("password");
				description = description + "修改密码为" + pws.substring(0, 1)
						+ "*****";

				String current_ip = CommUtil.getIpAddr(request);// 获得本机IP
				String ip_city = "未知地区";
				if (CommUtil.isIp(current_ip)) {
					IPSeeker ip = new IPSeeker(null, null);
					ip_city = ip.getIPLocation(current_ip).getCountry();
				}
				SysLog log = new SysLog();
				log.setTitle("管理员操作");
				log.setType(0);
				log.setAddTime(new Date());
				log.setUser_name(user.getUserName());
				log.setContent(description);
				log.setIp(current_ip);
				log.setIp_city(ip_city);
				this.sysLogService.save(log);
			} else {// 不记录访问列表记录，只记录操作记录
				if (id != null || mulitId != null
						|| annotation.value().indexOf("save") >= 0
						|| annotation.value().indexOf("edit") >= 0
						|| annotation.value().indexOf("update") >= 0) {
					String option1 = "执行";
					String option2 = "操作";
					description = description + option1 + annotation.title()
							+ option2;
					if (request.getParameter("mulitId") != null
							& !"".equals(request.getParameter("mulitId"))) {
						description = description + "。操作数据id为："
								+ request.getParameter("mulitId");
					} else {
						if (request.getParameter("id") != null
								&& !"".equals(request.getParameter("id"))) {
							description = description + "。操作数据id为："
									+ request.getParameter("id");
						}
					}
					String current_ip = CommUtil.getIpAddr(request);// 获得本机IP
					String ip_city = "未知地区";
					if (CommUtil.isIp(current_ip)) {
						IPSeeker ip = new IPSeeker(null, null);
						ip_city = ip.getIPLocation(current_ip).getCountry();
					}
					SysLog log = new SysLog();
					log.setTitle("管理员操作");
					log.setType(0);
					log.setAddTime(new Date());
					log.setUser_name(user.getUserName());
					log.setContent(description);
					log.setIp(current_ip);
					log.setIp_city(ip_city);
					this.sysLogService.save(log);
				}
			}
		}
	}

	// 异常日志记录
	@AfterThrowing(value = "execution(* com.iskyshop.manage.admin..*.*(..))&&args(request,..) ", throwing = "exception")
	public void exceptionLog(HttpServletRequest request, Throwable exception) {
		if (Globals.SAVE_LOG) {
			if (SecurityUserHolder.getCurrentUser() != null) {
				User user = this.userSerivce.getObjById(SecurityUserHolder
						.getCurrentUser().getId());
				String current_ip = CommUtil.getIpAddr(request);// 获得本机IP
				String ip_city = "未知地区";
				if (CommUtil.isIp(current_ip)) {
					IPSeeker ip = new IPSeeker(null, null);
					ip_city = ip.getIPLocation(current_ip).getCountry();
				}
				SysLog log = new SysLog();
				log.setTitle("系统异常");
				log.setType(1);
				log.setAddTime(new Date());
				log.setUser_name(user.getUserName());
				log.setContent(log.getAddTime() + "执行"
						+ request.getRequestURI() == null ? "" : request
						.getRequestURI()
						+ "时出现异常，异常代码为:"
						+ exception.getMessage());
				log.setIp(current_ip);
				log.setIp_city(ip_city);
				this.sysLogService.save(log);
			}
		}
	}

	// 记录用户登录日志

	public void loginLog() {
		System.out.println("用户登录");
	}

	private Method getMethod(ProceedingJoinPoint joinPoint) {
		// 获取连接点的方法签名对象
		MethodSignature joinPointObject = (MethodSignature) joinPoint
				.getSignature();
		// 连接点对象的方法
		Method method = joinPointObject.getMethod();
		// 连接点方法方法名
		String name = method.getName();
		Class<?>[] parameterTypes = method.getParameterTypes();
		// 获取连接点所在的目标对象
		Object target = joinPoint.getTarget();
		// 获取目标方法
		try {
			method = target.getClass().getMethod(name, parameterTypes);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			method = null;
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return method;
	}

	public String adminOptionContent(Object[] args, String mName)
			throws Exception {

		if (args == null) {
			return null;
		}
		StringBuffer rs = new StringBuffer();
		rs.append(mName);
		String className = null;
		int index = 1;
		// 遍历参数对象
		for (Object info : args) {
			// 获取对象类型
			className = info.getClass().getName();
			className = className.substring(className.lastIndexOf(".") + 1);
			boolean cal = false;
			LogFieldType[] types = LogFieldType.values();
			for (LogFieldType type : types) {
				if (type.toString().equals(className)) {
					cal = true;
				}
			}
			if (cal) {
				rs.append("[参数" + index + "，类型：" + className + "，值：");
				// 获取对象的所有方法
				Method[] methods = info.getClass().getDeclaredMethods();
				// 遍历方法，判断get方法
				for (Method method : methods) {
					String methodName = method.getName();
					// 判断是不是get方法
					if (methodName.indexOf("get") == -1) {// 不是get方法
						continue;// 不处理
					}
					Object rsValue = null;
					try {
						// 调用get方法，获取返回值
						rsValue = method.invoke(info);
						if (rsValue == null) {// 没有返回值
							continue;
						}
					} catch (Exception e) {
						continue;
					}
					// 将值加入内容中
					rs.append("(" + methodName + " : " + rsValue.toString()
							+ ")");
				}
				rs.append("]");
				index++;
			}
		}
		return rs.toString();
	}
}
