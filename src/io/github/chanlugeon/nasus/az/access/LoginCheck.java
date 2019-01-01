package io.github.chanlugeon.nasus.az.access;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import io.github.chanlugeon.nasus.az.Appzzang;
import io.github.chanlugeon.nasus.az.Util;

public class LoginCheck {
	private static final String LOGIN_CHECK = "https://appzzang.me/bbs/login_check.php";
	private static final String REFERER = Appzzang.ORIGIN + "/bbs/login.php?url=%2F";
	private static final String URL_DATA = "/"; // %252f /
	
	private final Map<String, String> cookie;
	private final Map<String, String> header = new HashMap<>();
	
	public LoginCheck(Map<String, String> cookie) {
		this.cookie = cookie;
		
		header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.put("Accept-Encoding", "gzip, deflate, br");
		header.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
		header.put("Cache-Control", "max-age=0");
		header.put("Connection", "keep-alive");
		header.put("DNT", "1");
		header.put("Host", "appzzang.me");
		header.put("Pragma", "no-cache");
		header.put("Content-Type", "application/x-www-form-urlencoded");
		header.put("Origin", Appzzang.ORIGIN);
		header.put("Referer", REFERER);
		header.put("Upgrade-Insecure-Requests", "1");
	}
	
	public Response login(String id, String password) throws IOException {
		Map<String, String> data = new HashMap<>();
		data.put("url", URL_DATA);
		data.put("mb_id", id);
		data.put("mb_password", password);
		
		Connection.Response loginRes = Jsoup.connect(LOGIN_CHECK)
				.timeout(Util.WAIT_TIME)
				.headers(header)
				.header("Content-Length", Integer.toString(Util.contentLength(data)))
				.cookies(cookie)
				.data(data)
				.method(Connection.Method.POST)
				.userAgent(Util.USER_AGENT)
				.execute();
		
		// System.out.println(loginRes.cookies().toString());
		return loginRes.cookies().isEmpty() ? Response.FAILURE : Response.SUCCESS;
	}
	
	public enum Response {
		SUCCESS, FAILURE
	}
}
