package com.xiechao06.lite_mms_client;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.xiechao06.lite_mms_client.net.FilterCond;
import com.xiechao06.lite_mms_client.net.WebService;
import com.xiechao06.lite_mms_client.utils.CommonUtils;
import com.xiechao06.lite_mms_client.utils.WidgetConverter;

import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public abstract class FilterDialogFragment extends DialogFragment {
	
	private static final String TAG = "FilterDialogFragment";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog ret = super.onCreateDialog(savedInstanceState);
		ret.setTitle(getActivity().getResources()
				.getString(R.string.please_refine_filter_conditions).toString());
		ret.setContentView(R.layout.filter);
		new GetFilterCondListTask().execute();
		return ret;
	}

	protected abstract String getModelName();

	class GetFilterCondListTask extends
			AsyncTask<String, Void, List<FilterCond>> {


		@Override
		protected List<FilterCond> doInBackground(String... params) {
			List<FilterCond> ret = null;
			Exception ex = null;
			try {
				ret =  WebService.getInstance().getFilterCondList(getModelName());
			} catch (JSONException e) {
				ex = e;
			} catch (ClientProtocolException e) {
				ex = e;
			} catch (NetworkErrorException e) {
				ex = e;
			} catch (IOException e) {
				ex = e;
			}
			if (ex != null) {
				CommonUtils.makeExceptionToast(getActivity(), ex);
			}
			return ret;
		}

		@Override
		protected void onPostExecute(List<FilterCond> result) {
			if (result != null) {
				Log.d(TAG, "size: " + result.size());
				WidgetConverter wc = new WidgetConverter(getActivity()); 
				getDialog().findViewById(R.id.progressBar1).setVisibility(View.GONE);
				for (FilterCond fc :result) {
					LinearLayout content = (LinearLayout)getDialog().findViewById(R.id.content);
					View v = wc.convert(fc, content);
					if (v != null) {
						content.addView(v);
					}
				}
			} else {
				getDialog().dismiss();
			}
		}
	}
}
