package io.github.chanlugeon.nasus.az;

import java.util.HashMap;
import java.util.Map;

public class Header {
	private static final Map<String, String> HEADER = new HashMap<>();
	
	public static Map<String, String> getBasic() {
		if (HEADER.isEmpty()) {
			HEADER.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
			HEADER.put("Cache-Control", "max-age=0");
			HEADER.put("Connection", "keep-alive");
			HEADER.put("DNT", "1");
			HEADER.put("Host", "appzzang.me");
			HEADER.put("Pragma", "no-cache");
			HEADER.put("Upgrade-Insecure-Requests", "1");
		}
		
		return HEADER;
	}
}