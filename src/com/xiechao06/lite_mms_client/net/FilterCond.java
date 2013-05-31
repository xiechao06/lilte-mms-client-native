package com.xiechao06.lite_mms_client.net;

import java.util.HashMap;
import java.util.Map;

public class FilterCond {
	public String name;
	public String default_value;
	public WidgetType widgetType;
	public boolean hidden;
	public Map<String, String> options;
	public String label;
	public String label_extra; 
	
	public FilterCond() {
		options = new HashMap<String, String>();
	}
}
