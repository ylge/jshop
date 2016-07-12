/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iskyshop.core.beans.exception;
/**
 * Abstract superclass for all exceptions thrown in the beans package
 * and subpackages.
 *
 * <p>Note that this is a runtime (unchecked) exception. Beans exceptions
 * are usually fatal; there is no reason for them to be checked.
 *
 * @author Rod Johnson
 * @author erikzhang
 */
public class BeansException extends NestedRuntimeException {
	protected Class beanClass;
	/**
	 * Create a new BeansException with the specified message.
	 * @param msg the detail message
	 */	
	public BeansException(String msg) {
		super(msg);
	}
	/**
	 * Create a new BeansException with the specified message
	 * and root cause.
	 * @param msg 错误信息
	 * @param ex the root cause
	 */
	public BeansException(String msg, Throwable ex) {
		super(msg, ex);
	}
	public BeansException(String msg,Class beanClass)
	{
		this(msg,null,beanClass);
	}
	public BeansException(String msg, Throwable ex,Class beanClass)
	{
		super("在"+beanClass.getName()+"上发生bean操作错误:"+msg,ex);
		this.beanClass=beanClass;
	}
	public Class getBeanClass() {
		return beanClass;
	}
}
