package com.iskyshop.core.beans;
import java.beans.PropertyEditor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

import com.iskyshop.core.beans.propertyeditors.ByteArrayPropertyEditor;
import com.iskyshop.core.beans.propertyeditors.CharArrayPropertyEditor;
import com.iskyshop.core.beans.propertyeditors.CharacterEditor;
import com.iskyshop.core.beans.propertyeditors.ClassArrayEditor;
import com.iskyshop.core.beans.propertyeditors.ClassEditor;
import com.iskyshop.core.beans.propertyeditors.CustomBooleanEditor;
import com.iskyshop.core.beans.propertyeditors.CustomCollectionEditor;
import com.iskyshop.core.beans.propertyeditors.CustomNumberEditor;
import com.iskyshop.core.beans.propertyeditors.DateEditor;
import com.iskyshop.core.beans.propertyeditors.LocaleEditor;
import com.iskyshop.core.beans.propertyeditors.PropertiesEditor;

public class PropertyEditorRegistry {
	private static Map defaultEditors;//这里改成单列，提高性能。
	private Map customEditors;
	private Map customEditorCache;
	protected void registerDefaultEditors() {
		if(defaultEditors!=null)return;
		this.defaultEditors = new HashMap(29);

		// Simple editors, without parameterization capabilities.
		// The JDK does not contain a default editor for any of these target types.
		this.defaultEditors.put(Class.class, new ClassEditor());
		this.defaultEditors.put(Class[].class, new ClassArrayEditor());
		this.defaultEditors.put(Locale.class, new LocaleEditor());
		this.defaultEditors.put(Properties.class, new PropertiesEditor());
		
		// Default instances of collection editors.
		// Can be overridden by registering custom instances of those as custom editors.
		this.defaultEditors.put(Collection.class, new CustomCollectionEditor(Collection.class));
		this.defaultEditors.put(Set.class, new CustomCollectionEditor(Set.class));
		this.defaultEditors.put(SortedSet.class, new CustomCollectionEditor(SortedSet.class));
		this.defaultEditors.put(List.class, new CustomCollectionEditor(List.class));

		// Default editors for primitive arrays.
		this.defaultEditors.put(byte[].class, new ByteArrayPropertyEditor());
		this.defaultEditors.put(char[].class, new CharArrayPropertyEditor());

		// Default instances of character and boolean editors.
		// Can be overridden by registering custom instances of those as custom editors.
		PropertyEditor characterEditor = new CharacterEditor(false);
		PropertyEditor booleanEditor = new CustomBooleanEditor(false);

		// The JDK does not contain a default editor for char!
		this.defaultEditors.put(char.class, characterEditor);
		this.defaultEditors.put(Character.class, characterEditor);

		// Spring's CustomBooleanEditor accepts more flag values than the JDK's default editor.
		this.defaultEditors.put(boolean.class, booleanEditor);
		this.defaultEditors.put(Boolean.class, booleanEditor);

		// The JDK does not contain default editors for number wrapper types!
		// Override JDK primitive number editors with our own CustomNumberEditor.
		PropertyEditor byteEditor = new CustomNumberEditor(Byte.class, true);
		PropertyEditor shortEditor = new CustomNumberEditor(Short.class, true);
		PropertyEditor integerEditor = new CustomNumberEditor(Integer.class, true);
		PropertyEditor longEditor = new CustomNumberEditor(Long.class, true);
		PropertyEditor floatEditor = new CustomNumberEditor(Float.class, true);
		PropertyEditor doubleEditor = new CustomNumberEditor(Double.class, true);

		this.defaultEditors.put(byte.class, byteEditor);
		this.defaultEditors.put(Byte.class, byteEditor);

		this.defaultEditors.put(short.class, shortEditor);
		this.defaultEditors.put(Short.class, shortEditor);

		this.defaultEditors.put(int.class, integerEditor);
		this.defaultEditors.put(Integer.class, integerEditor);

		this.defaultEditors.put(long.class, longEditor);
		this.defaultEditors.put(Long.class, longEditor);

		this.defaultEditors.put(float.class, floatEditor);
		this.defaultEditors.put(Float.class, floatEditor);

		this.defaultEditors.put(double.class, doubleEditor);
		this.defaultEditors.put(Double.class, doubleEditor);

		this.defaultEditors.put(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
		this.defaultEditors.put(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));
		this.defaultEditors.put(Date.class,new DateEditor());
	}
	/**
	 * Retrieve the default editor for the given property type, if any.
	 * @param requiredType type of the property
	 * @return the default editor, or <code>null</code> if none found
	 */
	protected PropertyEditor getDefaultEditor(Class requiredType) {
		if (this.defaultEditors == null) {
			return null;
		}
		return (PropertyEditor) this.defaultEditors.get(requiredType);
	}

	/**
	 * Copy the default editors registered in this instance to the given target registry.
	 * @param target the target registry to copy to
	 */
	protected void copyDefaultEditorsTo(PropertyEditorRegistry target) {
		target.defaultEditors = this.defaultEditors;
	}
	public void registerCustomEditor(Class requiredType, PropertyEditor propertyEditor) {
		registerCustomEditor(requiredType, null, propertyEditor);
	}

	public void registerCustomEditor(Class requiredType, String propertyPath, PropertyEditor propertyEditor) {
		if (requiredType == null && propertyPath == null) {
			throw new IllegalArgumentException("Either requiredType or propertyPath is required");
		}
		if (this.customEditors == null) {
			this.customEditors = CollectionFactory.createLinkedMapIfPossible(16);
		}
		if (propertyPath != null) {
			this.customEditors.put(propertyPath, new CustomEditorHolder(propertyEditor, requiredType));
		}
		else {
			this.customEditors.put(requiredType, propertyEditor);
			this.customEditorCache = null;
		}
	}

	public PropertyEditor findCustomEditor(Class requiredType, String propertyPath) {
		if (this.customEditors == null) {
			return null;
		}
		if (propertyPath != null) {
			// Check property-specific editor first.
			PropertyEditor editor = getCustomEditor(propertyPath, requiredType);
			if (editor == null) {
				List strippedPaths = new LinkedList();
				addStrippedPropertyPaths(strippedPaths, "", propertyPath);
				for (Iterator it = strippedPaths.iterator(); it.hasNext() && editor == null;) {
					String strippedPath = (String) it.next();
					editor = getCustomEditor(strippedPath, requiredType);
				}
			}
			if (editor != null) {
				return editor;
			}
			else if (requiredType == null) {
				requiredType = getPropertyType(propertyPath);
			}
		}
		// No property-specific editor -> check type-specific editor.
		return getCustomEditor(requiredType);
	}

	/**
	 * Determine the property type for the given property path.
	 * Called by <code>findCustomEditor</code> if no required type has been specified,
	 * to be able to find a type-specific editor even if just given a property path.
	 * <p>Default implementation always returns <code>null</code>.
	 * BeanWrapperImpl overrides this with the standard <code>getPropertyType</code>
	 * method as defined by the BeanWrapper interface.
	 * @param propertyPath the property path to determine the type for
	 * @return the type of the property, or <code>null</code> if not determinable
	 * @see BeanWrapper#getPropertyType(String)
	 */
	protected Class getPropertyType(String propertyPath) {
		return null;
	}

	/**
	 * Get custom editor that has been registered for the given property.
	 * @return the custom editor, or <code>null</code> if none specific for this property
	 */
	private PropertyEditor getCustomEditor(String propertyName, Class requiredType) {
		CustomEditorHolder holder = (CustomEditorHolder) this.customEditors.get(propertyName);
		return (holder != null ? holder.getPropertyEditor(requiredType) : null);
	}

	/**
	 * Get custom editor for the given type. If no direct match found,
	 * try custom editor for superclass (which will in any case be able
	 * to render a value as String via <code>getAsText</code>).
	 * @return the custom editor, or <code>null</code> if none found for this type
	 * @see java.beans.PropertyEditor#getAsText
	 */
	private PropertyEditor getCustomEditor(Class requiredType) {
		if (requiredType == null) {
			return null;
		}
		// Check directly registered editor for type.
		PropertyEditor editor = (PropertyEditor) this.customEditors.get(requiredType);
		if (editor == null) {
			// Check cached editor for type, registered for superclass or interface.
			if (this.customEditorCache != null) {
				editor = (PropertyEditor) this.customEditorCache.get(requiredType);
			}
			if (editor == null) {
				// Find editor for superclass or interface.
				for (Iterator it = this.customEditors.keySet().iterator(); it.hasNext() && editor == null;) {
					Object key = it.next();
					if (key instanceof Class && ((Class) key).isAssignableFrom(requiredType)) {
						editor = (PropertyEditor) this.customEditors.get(key);
						// Cache editor for search type, to avoid the overhead
						// of repeated assignable-from checks.
						if (this.customEditorCache == null) {
							this.customEditorCache = new HashMap();
						}
						this.customEditorCache.put(requiredType, editor);
					}
				}
			}
		}
		return editor;
	}

	/**
	 * Guess the property type of the specified property from the registered
	 * custom editors (provided that they were registered for a specific type).
	 * @param propertyName the name of the property
	 * @return the property type, or <code>null</code> if not determinable
	 */
	protected Class guessPropertyTypeFromEditors(String propertyName) {
		if (this.customEditors != null) {
			CustomEditorHolder editorHolder = (CustomEditorHolder) this.customEditors.get(propertyName);
			if (editorHolder == null) {
				List strippedPaths = new LinkedList();
				addStrippedPropertyPaths(strippedPaths, "", propertyName);
				for (Iterator it = strippedPaths.iterator(); it.hasNext() && editorHolder == null;) {
					String strippedName = (String) it.next();
					editorHolder = (CustomEditorHolder) this.customEditors.get(strippedName);
				}
			}
			if (editorHolder != null) {
				return editorHolder.getRegisteredType();
			}
		}
		return null;
	}

	/**
	 * Copy the custom editors registered in this instance to the given target registry.
	 * @param target the target registry to copy to
	 * @param nestedProperty the nested property path of the target registry, if any.
	 * If this is non-null, only editors registered for a path below this nested property
	 * will be copied.
	 */
	protected void copyCustomEditorsTo(PropertyEditorRegistry target, String nestedProperty) {
		String actualPropertyName =
				(nestedProperty != null ? PropertyAccessorUtils.getPropertyName(nestedProperty) : null);
		if (this.customEditors != null) {
			for (Iterator it = this.customEditors.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				if (entry.getKey() instanceof Class) {
					Class requiredType = (Class) entry.getKey();
					PropertyEditor editor = (PropertyEditor) entry.getValue();
					target.registerCustomEditor(requiredType, editor);
				}
				else if (entry.getKey() instanceof String & nestedProperty != null) {
					String editorPath = (String) entry.getKey();
					int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(editorPath);
					if (pos != -1) {
						String editorNestedProperty = editorPath.substring(0, pos);
						String editorNestedPath = editorPath.substring(pos + 1);
						if (editorNestedProperty.equals(nestedProperty) || editorNestedProperty.equals(actualPropertyName)) {
							CustomEditorHolder editorHolder = (CustomEditorHolder) entry.getValue();
							target.registerCustomEditor(
									editorHolder.getRegisteredType(), editorNestedPath, editorHolder.getPropertyEditor());
						}
					}
				}
			}
		}
	}


	/**
	 * Add property paths with all variations of stripped keys and/or indexes.
	 * Invokes itself recursively with nested paths
	 * @param strippedPaths the result list to add to
	 * @param nestedPath the current nested path
	 * @param propertyPath the property path to check for keys/indexes to strip
	 */
	private void addStrippedPropertyPaths(List strippedPaths, String nestedPath, String propertyPath) {
		int startIndex = propertyPath.indexOf(PropertyAccessor.PROPERTY_KEY_PREFIX_CHAR);
		if (startIndex != -1) {
			int endIndex = propertyPath.indexOf(PropertyAccessor.PROPERTY_KEY_SUFFIX_CHAR);
			if (endIndex != -1) {
				String prefix = propertyPath.substring(0, startIndex);
				String key = propertyPath.substring(startIndex, endIndex + 1);
				String suffix = propertyPath.substring(endIndex + 1, propertyPath.length());
				// Strip the first key.
				strippedPaths.add(nestedPath + prefix + suffix);
				// Search for further keys to strip, with the first key stripped.
				addStrippedPropertyPaths(strippedPaths, nestedPath + prefix, suffix);
				// Search for further keys to strip, with the first key not stripped.
				addStrippedPropertyPaths(strippedPaths, nestedPath + prefix + key, suffix);
			}
		}
	}


	/**
	 * Holder for a registered custom editor with property name.
	 * Keeps the PropertyEditor itself plus the type it was registered for.
	 */
	private static class CustomEditorHolder {

		private final PropertyEditor propertyEditor;

		private final Class registeredType;

		private CustomEditorHolder(PropertyEditor propertyEditor, Class registeredType) {
			this.propertyEditor = propertyEditor;
			this.registeredType = registeredType;
		}

		private PropertyEditor getPropertyEditor() {
			return propertyEditor;
		}

		private Class getRegisteredType() {
			return registeredType;
		}

		private PropertyEditor getPropertyEditor(Class requiredType) {
			// Special case: If no required type specified, which usually only happens for
			// Collection elements, or required type is not assignable to registered type,
			// which usually only happens for generic properties of type Object -
			// then return PropertyEditor if not registered for Collection or array type.
			// (If not registered for Collection or array, it is assumed to be intended
			// for elements.)
			if (this.registeredType == null ||
					(requiredType != null &&
					(ClassUtils.isAssignable(this.registeredType, requiredType) ||
					ClassUtils.isAssignable(requiredType, this.registeredType))) ||
					(requiredType == null &&
					(!Collection.class.isAssignableFrom(this.registeredType) && !this.registeredType.isArray()))) {
				return this.propertyEditor;
			}
			else {
				return null;
			}
		}
	}
}
