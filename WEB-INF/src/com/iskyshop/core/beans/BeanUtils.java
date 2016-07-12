/*
 * Copyright 2002-2006 the original author or authors.
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

package com.iskyshop.core.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import com.iskyshop.core.beans.exception.BeansException;

public abstract class BeanUtils {

	private static final TypeConverter converter=new TypeConverter(new BeanWrapper());
	public static Object convertType(Object v,Class requiredType)
	{
		return converter.convertIfNecessary(v, requiredType, (MethodParameter)null);
	}
	/**
	 *使用默认构造子初始化一个类实例
	 * @param　要实例化的类
	 * @return the new instance
	 */
	public static Object instantiateClass(Class clazz) throws BeansException {
		Assert.notNull(clazz, "指定的类名不能为null");
		if (clazz.isInterface()) {
			throw new BeansException("指定的class是一个接口，不能初始化!",clazz);
		}
		try {
			return instantiateClass(clazz.getDeclaredConstructor((Class[]) null), null);
		}
		catch (NoSuchMethodException ex) {
			throw new BeansException( "找不到默认构造函数!", ex,clazz);
		}
	}

	/**
	 *根据构造方法初始化一个类实例
	 * @param contor 构造函数
	 * @param args 构造子的参数
	 * @return 被创建的类实例
	 */
	public static Object instantiateClass(Constructor contor, Object[] args) throws BeansException {
		Assert.notNull(contor, "构造子不能为null");
		try {
			if (!Modifier.isPublic(contor.getModifiers()) ||
					!Modifier.isPublic(contor.getDeclaringClass().getModifiers())) {
				contor.setAccessible(true);
			}
			return contor.newInstance(args);
		}		
		catch (Exception ex) {
			throw new BeansException(
					"构造实例错误!", ex,contor.getDeclaringClass());
		}
	}

	/**
	 * 通过给定的方法名称及参数类型查询类中的一个方法，若当前类没有这个方法，则进一步到其父类中进行查询。首先查询公开的方法，然后查询私有的方法。
	 * @param clazz 指定类
	 * @param methodName 方法名称
	 * @param paramTypes 参数类型
	 * @return 查找到的方法，若未找到，则为null	
	 */
	public static Method findMethod(Class clazz, String methodName, Class[] paramTypes) {
		try {
			return clazz.getMethod(methodName, paramTypes);
		}
		catch (NoSuchMethodException ex) {
			return findDeclaredMethod(clazz, methodName, paramTypes);
		}
	}

	/**
	 * Find a method with the given method name and the given parameter types,
	 * declared on the given class or one of its superclasses. Will return a public,
	 * protected, package access, or private method.
	 * <p>Checks <code>Class.getDeclaredMethod</code>, cascading upwards to all superclasses.
	 * @param clazz the class to check
	 * @param methodName the name of the method to find
	 * @param paramTypes the parameter types of the method to find
	 * @return the Method object, or <code>null</code> if not found
	 * @see java.lang.Class#getDeclaredMethod
	 */
	public static Method findDeclaredMethod(Class clazz, String methodName, Class[] paramTypes) {
		try {
			return clazz.getDeclaredMethod(methodName, paramTypes);
		}
		catch (NoSuchMethodException ex) {
			if (clazz.getSuperclass() != null) {
				return findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
			}
			return null;
		}
	}

	/**
	 * Find a method with the given method name and minimal parameters (best case: none),
	 * declared on the given class or one of its superclasses. Prefers public methods,
	 * but will return a protected, package access, or private method too.
	 * <p>Checks <code>Class.getMethods</code> first, falling back to
	 * <code>findDeclaredMethodWithMinimalParameters</code>. This allows to find public
	 * methods without issues even in environments with restricted Java security settings.
	 * @param clazz the class to check
	 * @param methodName the name of the method to find
	 * @return the Method object, or <code>null</code> if not found
	 * @throws IllegalArgumentException if methods of the given name were found but
	 * could not be resolved to a unique method with minimal parameters
	 * @see java.lang.Class#getMethods
	 * @see #findDeclaredMethodWithMinimalParameters
	 */
	public static Method findMethodWithMinimalParameters(Class clazz, String methodName)
			throws IllegalArgumentException {

		Method targetMethod = doFindMethodWithMinimalParameters(clazz.getDeclaredMethods(), methodName);
		if (targetMethod == null) {
			return findDeclaredMethodWithMinimalParameters(clazz, methodName);
		}
		return targetMethod;
	}

	/**
	 * Find a method with the given method name and minimal parameters (best case: none),
	 * declared on the given class or one of its superclasses. Will return a public,
	 * protected, package access, or private method.
	 * <p>Checks <code>Class.getDeclaredMethods</code>, cascading upwards to all superclasses.
	 * @param clazz the class to check
	 * @param methodName the name of the method to find
	 * @return the Method object, or <code>null</code> if not found
	 * @throws IllegalArgumentException if methods of the given name were found but
	 * could not be resolved to a unique method with minimal parameters
	 * @see java.lang.Class#getDeclaredMethods
	 */
	public static Method findDeclaredMethodWithMinimalParameters(Class clazz, String methodName)
			throws IllegalArgumentException {

		Method targetMethod = doFindMethodWithMinimalParameters(clazz.getDeclaredMethods(), methodName);
		if (targetMethod == null && clazz.getSuperclass() != null) {
			return findDeclaredMethodWithMinimalParameters(clazz.getSuperclass(), methodName);
		}
		return targetMethod;
	}

	/**
	 * Find a method with the given method name and minimal parameters (best case: none)
	 * in the given list of methods.
	 * @param methods the methods to check
	 * @param methodName the name of the method to find
	 * @return the Method object, or <code>null</code> if not found
	 * @throws IllegalArgumentException if methods of the given name were found but
	 * could not be resolved to a unique method with minimal parameters
	 */
	private static Method doFindMethodWithMinimalParameters(Method[] methods, String methodName)
			throws IllegalArgumentException {

		Method targetMethod = null;
		int numMethodsFoundWithCurrentMinimumArgs = 0;
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName)) {
				int numParams = methods[i].getParameterTypes().length;
				if (targetMethod == null ||
						numParams < targetMethod.getParameterTypes().length) {
					targetMethod = methods[i];
					numMethodsFoundWithCurrentMinimumArgs = 1;
				}
				else {
					if (targetMethod.getParameterTypes().length == numParams) {
						// Additional candidate with same length.
						numMethodsFoundWithCurrentMinimumArgs++;
					}
				}
			}
		}
		if (numMethodsFoundWithCurrentMinimumArgs > 1) {
			throw new IllegalArgumentException("Cannot resolve method '" + methodName +
					"' to a unique method. Attempted to resolve to overloaded method with " +
					"the least number of parameters, but there were " +
					numMethodsFoundWithCurrentMinimumArgs + " candidates.");
		}
		return targetMethod;
	}

	/**
	 * Parse a method signature in the form <code>methodName[([arg_list])]</code>,
	 * where <code>arg_list</code> is an optional, comma-separated list of fully-qualified
	 * type names, and attempts to resolve that signature against the supplied <code>Class</code>.
	 * <p>When not supplying an argument list (<code>methodName</code>) the method whose name
	 * matches and has the least number of parameters will be returned. When supplying an
	 * argument type list, only the method whose name and argument types match will be returned.
	 * <p>Note then that <code>methodName</code> and <code>methodName()</code> are <strong>not</strong>
	 * resolved in the same way. The signature <code>methodName</code> means the method called
	 * <code>methodName</code> with the least number of arguments, whereas <code>methodName()</code>
	 * means the method called <code>methodName</code> with exactly 0 arguments.
	 * <p>If no method can be found, then <code>null</code> is returned.
	 * @see #findMethod
	 * @see #findMethodWithMinimalParameters
	 */
	public static Method resolveSignature(String signature, Class clazz) {
		Assert.hasText(signature, "Signature must not be null or zero-length");
		Assert.notNull(clazz, "Class must not be null");

		int firstParen = signature.indexOf("(");
		int lastParen = signature.indexOf(")");

		if (firstParen > -1 && lastParen == -1) {
			throw new IllegalArgumentException("Invalid method signature '" + signature +
					"': expected closing ')' for args list");
		}
		else if (lastParen > -1 && firstParen == -1) {
			throw new IllegalArgumentException("Invalid method signature '" + signature +
					"': expected opening '(' for args list");
		}
		else if (firstParen == -1 && lastParen == -1) {
			return findMethodWithMinimalParameters(clazz, signature);
		}
		else {
			String methodName = signature.substring(0, firstParen);
			String[] parameterTypeNames =
					StringUtils.commaDelimitedListToStringArray(signature.substring(firstParen + 1, lastParen));
			Class[] parameterTypes = new Class[parameterTypeNames.length];
			for (int i = 0; i < parameterTypeNames.length; i++) {
				String parameterTypeName = parameterTypeNames[i].trim();
				try {
					parameterTypes[i] = ClassUtils.forName(parameterTypeName, clazz.getClassLoader());
				}
				catch (ClassNotFoundException ex) {
					throw new IllegalArgumentException("Invalid method signature: unable to locate type [" +
							parameterTypeName + "] for argument " + i);
				}
			}
			return findMethod(clazz, methodName, parameterTypes);
		}
	}


	/**
	 * Retrieve the JavaBeans <code>PropertyDescriptor</code>s of a given class.
	 * @param clazz the Class to retrieve the PropertyDescriptors for
	 * @return an array of <code>PropertyDescriptors</code> for the given class
	 * @throws BeansException if PropertyDescriptor look fails
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Class clazz) throws BeansException {
		CachedIntrospectionResults cr = CachedIntrospectionResults.forClass(clazz);
		return cr.getBeanInfo().getPropertyDescriptors();
	}

	/**
	 * Retrieve the JavaBeans <code>PropertyDescriptors</code> for the given property.
	 * @param clazz the Class to retrieve the PropertyDescriptor for
	 * @param propertyName the name of the property
	 * @return the corresponding PropertyDescriptor, or <code>null</code> if none
	 * @throws BeansException if PropertyDescriptor lookup fails
	 */
	public static PropertyDescriptor getPropertyDescriptor(Class clazz, String propertyName)
			throws BeansException {

		CachedIntrospectionResults cr = CachedIntrospectionResults.forClass(clazz);
		return cr.getPropertyDescriptor(propertyName);
	}

	/**
	 * Find a JavaBeans <code>PropertyDescriptor</code> for the given method,
	 * with the method either being the read method or the write method for
	 * that bean property.
	 * @param method the method to find a corresponding PropertyDescriptor for
	 * @return the corresponding PropertyDescriptor, or <code>null</code> if none
	 * @throws BeansException if PropertyDescriptor lookup fails
	 */
	public static PropertyDescriptor findPropertyForMethod(Method method) throws BeansException {
		Assert.notNull(method, "Method must not be null");
		PropertyDescriptor[] pds = getPropertyDescriptors(method.getDeclaringClass());
		for (int i = 0; i < pds.length; i++) {
			if (method.equals(pds[i].getReadMethod()) || method.equals(pds[i].getWriteMethod())) {
				return pds[i];
			}
		}
		return null;
	}

	/**
	 * Determine the bean property type for the given property from the
	 * given classes/interfaces, if possible.
	 * @param propertyName the name of the bean property
	 * @param beanClasses the classes to check against
	 * @return the property type, or <code>Object.class</code> as fallback
	 */
	public static Class findPropertyType(String propertyName, Class[] beanClasses) {
		if (beanClasses != null) {
			for (int i = 0; i < beanClasses.length; i++) {
				PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(beanClasses[i], propertyName);
				if (pd != null) {
					return pd.getPropertyType();
				}
			}
		}
		return Object.class;
	}

	/**
	 * Determine the canonical name for the given property path.
	 * Removes surrounding quotes from map keys:<br>
	 * <code>map['key']</code> -> <code>map[key]</code><br>
	 * <code>map["key"]</code> -> <code>map[key]</code>
	 * @param propertyName the bean property path
	 * @return the canonical representation of the property path
	 */
	public static String canonicalPropertyName(String propertyName) {
		if (propertyName == null) {
			return "";
		}

		// The following code does not use JDK 1.4's StringBuffer.indexOf(String)
		// method to retain JDK 1.3 compatibility. The slight loss in performance
		// is not really relevant, as this code will typically just run on startup.

		StringBuffer buf = new StringBuffer(propertyName);
		int searchIndex = 0;
		while (searchIndex != -1) {
			int keyStart = buf.toString().indexOf(PropertyAccessor.PROPERTY_KEY_PREFIX, searchIndex);
			searchIndex = -1;
			if (keyStart != -1) {
				int keyEnd = buf.toString().indexOf(
						PropertyAccessor.PROPERTY_KEY_SUFFIX, keyStart + PropertyAccessor.PROPERTY_KEY_PREFIX.length());
				if (keyEnd != -1) {
					String key = buf.substring(keyStart + PropertyAccessor.PROPERTY_KEY_PREFIX.length(), keyEnd);
					if ((key.startsWith("'") && key.endsWith("'")) || (key.startsWith("\"") && key.endsWith("\""))) {
						buf.delete(keyStart + 1, keyStart + 2);
						buf.delete(keyEnd - 2, keyEnd - 1);
						keyEnd = keyEnd - 2;
					}
					searchIndex = keyEnd + PropertyAccessor.PROPERTY_KEY_SUFFIX.length();
				}
			}
		}
		return buf.toString();
	}

	/**
	 * Check if the given class represents a "simple" property,
	 * i.e. a primitive, a String, a Class, or a corresponding array.
	 * Used to determine properties to check for a "simple" dependency-check.
	 * @see org.springframework.beans.factory.support.RootBeanDefinition#DEPENDENCY_CHECK_SIMPLE
	 * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#checkDependencies
	 */
	public static boolean isSimpleProperty(Class clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return clazz.isPrimitive() || ClassUtils.isPrimitiveArray(clazz) ||
				ClassUtils.isPrimitiveWrapper(clazz) || ClassUtils.isPrimitiveWrapperArray(clazz) ||
				clazz.equals(String.class) || clazz.equals(String[].class) ||
				clazz.equals(Class.class) || clazz.equals(Class[].class);
	}

	/**
	 * Copy the property values of the given source bean into the target bean.
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * <p>This is just a convenience method. For more complex transfer needs,
	 * consider using a full BeanWrapper.
	 * @param source the source bean
	 * @param target the target bean
	 * @throws BeansException if the copying failed
	 * @see BeanWrapper
	 */
	public static void copyProperties(Object source, Object target) throws BeansException {
		copyProperties(source, target, null, null);
	}

	/**
	 * Copy the property values of the given source bean into the given target bean,
	 * only setting properties defined in the given "editable" class (or interface).
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * <p>This is just a convenience method. For more complex transfer needs,
	 * consider using a full BeanWrapper.
	 * @param source the source bean
	 * @param target the target bean
	 * @param editable the class (or interface) to restrict property setting to
	 * @throws BeansException if the copying failed
	 * @see BeanWrapper
	 */
	public static void copyProperties(Object source, Object target, Class editable)
			throws BeansException {

		copyProperties(source, target, editable, null);
	}

	/**
	 * Copy the property values of the given source bean into the given target bean,
	 * ignoring the given "ignoreProperties".
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * <p>This is just a convenience method. For more complex transfer needs,
	 * consider using a full BeanWrapper.
	 * @param source the source bean
	 * @param target the target bean
	 * @param ignoreProperties array of property names to ignore
	 * @throws BeansException if the copying failed
	 * @see BeanWrapper
	 */
	public static void copyProperties(Object source, Object target, String[] ignoreProperties)
			throws BeansException {

		copyProperties(source, target, null, ignoreProperties);
	}

	/**
	 * Copy the property values of the given source bean into the given target bean.
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * @param source the source bean
	 * @param target the target bean
	 * @param editable the class (or interface) to restrict property setting to
	 * @param ignoreProperties array of property names to ignore
	 * @throws BeansException if the copying failed
	 * @see BeanWrapper
	 */
	private static void copyProperties(Object source, Object target, Class editable, String[] ignoreProperties)
			throws BeansException {

		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		Class actualEditable = target.getClass();
		if (editable != null) {
			if (!editable.isInstance(target)) {
				throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
						"] not assignable to Editable class [" + editable.getName() + "]");
			}
			actualEditable = editable;
		}
		PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
		List ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

		for (int i = 0; i < targetPds.length; i++) {
			PropertyDescriptor targetPd = targetPds[i];
			if (targetPd.getWriteMethod() != null &&
					(ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
				PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
				if (sourcePd != null && sourcePd.getReadMethod() != null) {
					try {
						Method readMethod = sourcePd.getReadMethod();
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source, new Object[0]);
						Method writeMethod = targetPd.getWriteMethod();
						if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
							writeMethod.setAccessible(true);
						}
						writeMethod.invoke(target, new Object[] {value});
					}
					catch (Exception ex) {
						throw new BeansException("Could not copy properties from source to target", ex);
					}
				}
			}
		}
	}
public static boolean checkLazyloadNull(Object value)
{
	if(value==null)return true;
	if(value.getClass().toString().indexOf("$$EnhancerByCGLIB")>0)
	{
	try{
		value.toString();
	}
	catch(NullPointerException e)
	{
	return true;
	}}
	return false;
}
}
