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

/**
 * 
 * <p>
 * Title: JdkVersion.java
 * </p>
 * 
 * <p>
 * Description: 处理Java版本信息的工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-10-16
 * 
 * @version iskyshop_b2b2c 2015
 */
public class JdkVersion {
	public static final int JDK_1_2 = -1;
	public static final int JDK_1_3 = 0;
	public static final int JDK_1_4 = 1;
	public static final int JDK_1_5 = 2;
	public static final int JDK_1_6 = 3;
	public static final int JDK_1_7 = 4;

	private static final String version;
	private static final int javaVersion;

	static {
		version = System.getProperty("java.version");
		if (version.indexOf("1.7.") != -1) {
			javaVersion = JDK_1_7;
		} else if (version.indexOf("1.6.") != -1) {
			javaVersion = JDK_1_6;
		} else if (version.indexOf("1.5.") != -1) {
			javaVersion = JDK_1_5;
		} else if (version.indexOf("1.4.") != -1) {
			javaVersion = JDK_1_4;
		} else if (version.indexOf("1.3.") != -1) {
			javaVersion = JDK_1_3;
		} else {
			javaVersion = JDK_1_2;
		}
	}

	/**
	 * 得到运行环境中的java版本信息　 <code>System.getProperty("java.version")</code>.
	 */
	public static String getVersion() {
		return version;
	}

	/**
	 * 得到数字常量为代表的java版本号，可以容易得到高于某个版本或低于某个版本的信息
	 * <code>if (getMajorJavaVersion() < JAVA_14)</code>.
	 * 
	 * @return 返回以JAVA_X_X为代表的常量Java版本代码
	 * @see #JDK_1_4
	 * @see #JDK_1_5
	 */
	public static int getJavaVersion() {
		return javaVersion;
	}

	public static boolean isAnnoatationSupport() {
		return javaVersion >= JDK_1_5;
	}
}
