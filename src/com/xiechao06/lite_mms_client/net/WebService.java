package com.xiechao06.lite_mms_client.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xiechao06.lite_mms_client.BaseListFragment1;
import com.xiechao06.lite_mms_client.BaseListFragment1.SetListRunnable;
import com.xiechao06.lite_mms_client.MainActivity;
import com.xiechao06.lite_mms_client.R;
import com.xiechao06.lite_mms_client.dummy.SimpleItem;
import com.xiechao06.lite_mms_client.utils.CommonUtils;
import com.xiechao06.lite_mms_client.utils.MyApp;

import android.accounts.NetworkErrorException;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

public class WebService {
	
	private static final String TAG = "WebService";
	private static WebService instance;
	
	public static WebService getInstance() {
		if (instance == null) {
			instance = new WebService();
		}
		return instance;
	}
	
	private WebService() {
		
	}	
	
	public ListResponse<SimpleItem> getList(String modelName, int offset, int limit, String orderBy, 
			boolean desc, Map<String, String> filters) throws ClientProtocolException, IOException, JSONException, NetworkErrorException {

        ListResponse<SimpleItem> ret = null;
        
        HttpResponse response = sendGETRequest(composeListUrl(modelName, offset, limit, orderBy, desc, filters, false));
        
        int stateCode = response.getStatusLine().getStatusCode();
        String result = EntityUtils.toString(response.getEntity());
        
        if (stateCode == 200) {
        		JSONObject root = new JSONObject(result);
        		ret = new ListResponse<SimpleItem>(root.getBoolean("has_more"), root.getInt("total_cnt"));
        		JSONArray data = root.getJSONArray("data");
        		for (int i=0; i < data.length(); ++i) {
        			JSONObject item = data.getJSONObject(i);
        			ret.addItem(new SimpleItem(item.getString("id"), item.getString("repr")));
        		}
        } else {
        		JSONObject root = new JSONObject(result);
        		throw new NetworkErrorException(root.getString("reason"));
        }
        return ret;
	}
	
	public List<FilterCond> getFilterCondList(String modelName) throws ClientProtocolException, IOException, JSONException, NetworkErrorException {
		List<FilterCond> ret = null;
        HttpResponse response = sendGETRequest(composeFilterUrl(modelName));
        
        int stateCode = response.getStatusLine().getStatusCode();
        String result = EntityUtils.toString(response.getEntity());
        
        if (stateCode == 200) {
        		ret = new ArrayList<FilterCond>();
        		JSONObject root = new JSONObject(result);
        		JSONArray data = root.getJSONArray("filter_conditions");
        		
        		for (int i=0; i < data.length(); ++i) {
        			JSONObject cond = data.getJSONObject(i);
        			String type = cond.getString("type");
        			if (type.equals("select")) {
        				FilterCond fc = new FilterCond();
        				fc.widgetType = WidgetType.Select;
        				fc.label = cond.getString("label");
        				fc.label_extra = cond.getString("label_extra");
        				fc.name = cond.getString("name");
        				fc.default_value = cond.getString("default_value");
        				fc.hidden = cond.getBoolean("hidden");
        				JSONArray options = cond.getJSONArray("options");
        				for (int j=0; j < options.length(); ++j) {
        					JSONArray opt = options.getJSONArray(j);
        					fc.options.put(opt.getString(0), opt.getString(1));
        				}
        				ret.add(fc);
        			}
        		}
        } else {
        		JSONObject root = new JSONObject(result);
        		throw new NetworkErrorException(root.getString("reason"));
        }
		return ret;
	}

	
	private String composeFilterUrl(String modelName) {
		String[] bpModelPair = modelName.split("/");
		String modelName_ = bpModelPair[1].replaceAll("([A-Z]+)", "-$1");
		modelName_ = modelName_.toLowerCase();
		if (modelName_.charAt(0) == '-') {
			modelName_ = modelName_.substring(1);
		}
		String ret = "http://" + MyApp.getServerAddress() + "/" + bpModelPair[0] + "/apis";
		ret += "/" + modelName_ + "-filters";
		return ret;
	}

	private String composeListUrl(String modelName, int offset, int limit, String orderBy, 
			boolean desc, Map<String, String> filters, boolean verbose) {
		String[] bpModelPair = modelName.split("/");
		String modelName_ = bpModelPair[1].replaceAll("([A-Z]+)", "-$1");
		modelName_ = modelName_.toLowerCase();
		if (modelName_.charAt(0) == '-') {
			modelName_ = modelName_.substring(1);
		}
		String ret = "http://" + MyApp.getServerAddress() + "/" + bpModelPair[0] + "/apis";
		if (verbose) {
			ret += "/verbose";			
		}
		ret += "/" + modelName_ + "-list";
		ret += "?__offset__=" + offset;
		ret += "&__limit__=" + limit;
		if (!CommonUtils.isEmptyString(orderBy)) {
			ret += "&order_by=" + orderBy;
		}
		if (desc) {
			ret += "&desc=1";
		}
		if (filters != null) {
			for (Entry<String, String>entry: filters.entrySet()) {
				ret += "&" + entry.getKey() + "=" + entry.getValue();
			}
		}
		return ret;
	} 

	private HttpResponse sendGETRequest(String url) throws ClientProtocolException, IOException {
        HttpGet hg = new HttpGet(url);
        HttpResponse response = new DefaultHttpClient().execute(hg);
        return response;
    }

}
