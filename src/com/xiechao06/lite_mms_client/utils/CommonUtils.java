package com.xiechao06.lite_mms_client.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.widget.Toast;

public class CommonUtils {

	enum NetworkType {
		all, onlyWifi
	};

	static NetworkType networkType = NetworkType.onlyWifi;

	public static long getTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	public static boolean hasExternalStorage() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static boolean netAvailable() {
		ConnectivityManager manager = (ConnectivityManager) MyApp
				.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		}
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		// if (networkType == NetworkType.onlyWifi) {
		// return networkinfo != null
		// && networkinfo.getType() == ConnectivityManager.TYPE_WIFI;
		// } else {
		return networkinfo != null && networkinfo.isAvailable();
		// }
	}

	public static void setOnlyWifi() {
		networkType = NetworkType.onlyWifi;
	}

	public static void setAllNetworks() {
		networkType = NetworkType.all;
	}

	public static boolean isEmptyString(String s) {
		return s == null || s.isEmpty();
	}

	public static void makeExceptionToast(final Activity activity,
			final Exception e) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT)
						.show();
			}
		});

	}
}
