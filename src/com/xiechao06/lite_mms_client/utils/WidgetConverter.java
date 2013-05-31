package com.xiechao06.lite_mms_client.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.xiechao06.lite_mms_client.R;
import com.xiechao06.lite_mms_client.net.FilterCond;

public class WidgetConverter {

	private static final String TAG = "WidgetConverter";
	private Activity mActivity;
	private LayoutInflater mInflater;

	public WidgetConverter(Activity activity) {
		mActivity = activity;
		mInflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View convert(FilterCond fc, ViewGroup viewGroup) {
		View ret = null;
		Log.d(TAG, "widgetType" + fc.widgetType);
		Iterator<Entry<String, String>> it = fc.options.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> opt = it.next();
			Log.d(TAG, opt.getKey() + ":" + opt.getValue());
		}
		switch (fc.widgetType) {
		case Select:
			View widget = mInflater.inflate(R.layout.select_widget, null);
			Spinner spinner = (Spinner) widget.findViewById(R.id.spinner1); 
			SpinnerAdapter spinnerAdapter = new ArrayAdapter<String>(mActivity,
					android.R.layout.simple_spinner_item,
					new ArrayList<String>(fc.options.values()));
			spinner.setAdapter(spinnerAdapter);
			spinner.setTag(fc);
			TextView label = (TextView) widget.findViewById(R.id.textView1);
			label.setText(fc.label + " " + fc.label_extra);
			ret = widget;
		default:
			break;
		}
		return ret;
	}

}
