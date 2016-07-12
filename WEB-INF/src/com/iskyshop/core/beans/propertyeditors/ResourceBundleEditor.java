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

package com.iskyshop.core.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.Locale;
import java.util.ResourceBundle;

import com.iskyshop.core.beans.Assert;
import com.iskyshop.core.tools.StringUtils;


public class ResourceBundleEditor extends PropertyEditorSupport {

	/**
	 * The separator used to distinguish between the base name and the
	 * locale (if any) when {@link #setAsText(String) converting from a String}.
	 */
	public static final String BASE_NAME_SEPARATOR = "_";


	public void setAsText(String text) throws IllegalArgumentException {
		Assert.hasText(text);
		ResourceBundle bundle;
		String rawBaseName = text.trim();
		int indexOfBaseNameSeparator = rawBaseName.indexOf(BASE_NAME_SEPARATOR);
		if (indexOfBaseNameSeparator == -1) {
			bundle = ResourceBundle.getBundle(rawBaseName);
		} else {
			// it potentially has locale information
			String baseName = rawBaseName.substring(0, indexOfBaseNameSeparator);
			if (!StringUtils.hasText(baseName)) {
				throw new IllegalArgumentException("Bad ResourceBundle name : received '" + text + "' as argument to 'setAsText(String value)'.");
			}
			String localeString = rawBaseName.substring(indexOfBaseNameSeparator + 1);
			Locale locale = StringUtils.parseLocaleString(localeString);
			bundle = (StringUtils.hasText(localeString))
					? ResourceBundle.getBundle(baseName, locale)
					: ResourceBundle.getBundle(baseName);
		}
		setValue(bundle);
	}

}
