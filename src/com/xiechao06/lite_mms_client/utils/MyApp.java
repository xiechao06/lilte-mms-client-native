package com.xiechao06.lite_mms_client.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.xiechao06.lite_mms_client.R;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class MyApp extends Application {
    private static Context context;
    private static String server_address;

    public static String getServerAddress() {
        if (null == server_address) {
        		InputStream is = null;
			try {
				is = context.getAssets().open("conf.properties");
			} catch (IOException e1) {
				e1.printStackTrace();
				return "";
			}
            Properties properties = new Properties();
            try {
                properties.load(is);
                server_address = properties.getProperty("server_address");
                return server_address;
            } catch (IOException e) {
                Log.e("source", "properties file exception, can't read /assets/conf.properties");
            }
        }
        return server_address;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApp.context;
    }

}
