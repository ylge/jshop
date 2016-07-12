package com.iskyshop.core.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateEditor extends PropertyEditorSupport {
	private  DateFormat format;
	private java.util.List formats=new java.util.ArrayList();
	public DateEditor()
	{
		this.format=new SimpleDateFormat("yyyy-MM-dd H:m:s");
		formats.add(new SimpleDateFormat());
		formats.add(new SimpleDateFormat("yyyy-MM-dd"));
	}
	public DateEditor(String formatText)
	{
		this.format=new SimpleDateFormat(formatText);
	}
	public DateEditor(DateFormat format)
	{
		this.format=format;
	}
	public void addFormats(DateFormat format)
	{
		this.formats.add(format);
	}
	public void setAsText(String text) throws IllegalArgumentException {
		if(text==null || "".equals(text))return;
		try{
			setValue(format.parse(text));
		}
		catch(java.text.ParseException e)
		{
			boolean op=false;
			for(int i=0;i<formats.size();i++)
			{
				try{
					DateFormat ft=(DateFormat)formats.get(i);
					setValue(ft.parse(text));
					op=true;
					break;
				}
				catch(java.text.ParseException e1)
				{
					
				}
			}
			if(!op)
			throw new IllegalArgumentException("日期格式不正确，不能正确解析: " + e.getMessage());
		}
		
	}

}
