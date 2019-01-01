package io.github.chanlugeon.nasus.az;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public abstract class Util {
	public static final int WAIT_TIME = 5000;
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36";;
	
	/*public static String getLengthByString(Map<String, String> query) {
			return Integer.toString(query.toString().length() - query.size() - 1); // - {} last & X 
	}*/
	
	public static int contentLength(Map<String, String> data) {
		// query.toString()
		int len = 0;
		try {
			String enc = URLEncoder.encode(data.toString(), "UTF-8").replaceAll("%0A", "%0D%0A");
			len += enc.length() - 4 - data.size() * 5 + 1;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return len;
	}
}
