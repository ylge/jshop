package com.iskyshop.filter;

import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
/**
 * 
 * <p>
 * Title: JsRequest.java
 * </p>
 * 
 * <p>
 * Description:获取参数 js 脚本注入转义
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
 * @author jinxinzhe
 * 
 * @date 2014-12-4
 * 
 * @version iskyshop_b2b2c 2.0
 */
public class JsRequest extends HttpServletRequestWrapper {

	private Map params;
	 
    public JsRequest(HttpServletRequest request, Map newParams) {
             super(request);
             this.params = newParams;
   }

    public Map getParameterMap() {
             return params ;
   }

    public Enumeration getParameterNames() {
             Vector l = new Vector( params.keySet());
             return l.elements();
   }

    public String[] getParameterValues(String name) {
            Object v = params.get(name);
             if (v == null ) {
                       return null ;
            } else if (v instanceof String[]) {
                      String[] value = (String[]) v;
                       for (int i = 0; i < value.length; i++) {
                               value[i] = value[i].replaceAll( "<", "&lt;" );
                               value[i] = value[i].replaceAll( ">", "&gt;" );
                      }
                       return (String[]) value;
            } else if (v instanceof String) {
                      String value = (String) v;
                      value = value.replaceAll( "<", "&lt;" );
                      value = value.replaceAll( ">", "&gt;" );
                       return new String[] { (String) value };
            } else {
                       return new String[] { v.toString() };
            }
   }

    public String getParameter(String name) {
            Object v = params.get(name);
             if (v == null ) {
                       return null ;
            } else if (v instanceof String[]) {
                      String[] strArr = (String[]) v;
                       if (strArr.length > 0) {
                               String value = strArr[0];
                               value = value.replaceAll( "<", "&lt;" );
                               value = value.replaceAll( "<", "&gt;" );
                                return value;
                      } else {
                                return null ;
                      }
            } else if (v instanceof String) {
                      String value = (String) v;
                      value = value.replaceAll( "<", "&lt;" );
                      value = value.replaceAll( ">", "&gt;" );
                       return (String) value;
            } else {
                       return v.toString();
            }
   }

}
