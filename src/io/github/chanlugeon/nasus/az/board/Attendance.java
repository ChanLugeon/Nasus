package io.github.chanlugeon.nasus.az.board;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import io.github.chanlugeon.nasus.az.Appzzang;
import io.github.chanlugeon.nasus.az.Util;

public class Attendance {
	private static final String UPDATE = Appzzang.ORIGIN + "/plugin/attendance/update.php";
	private static final String ATTENDANCE = Appzzang.ORIGIN + "/plugin/attendance/";
	
	private final Map<String, String> cookie;
	
	public Attendance(Map<String, String> cookie) {
		this.cookie = cookie;
	}
	
	public void attend(String comment) throws IOException { // TODO test
		Map<String, String> data = new HashMap<>();
		data.put("mobile", "");
		data.put("at_memo", comment);
		
		Jsoup.connect(UPDATE)
		.timeout(Util.WAIT_TIME)
		.header("Accept", "*/*")
		.header("Accept-Encoding", "gzip, deflate")
		.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
		.header("Cache-Control", "no-cache")
		.header("Connection", "keep-alive")
		.header("Content-Length", Integer.toString(Util.contentLength(data)))
		.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
		.header("DNT", "1")
		.header("Host", Appzzang.HOST)
		.header("Origin", Appzzang.ORIGIN)
		.header("Pragma", "no-cache")
		.header("Referer", ATTENDANCE) // popup?
		.header("X-Requested-With", "XMLHttpRequest")
		.cookies(cookie)
		.data(data)
		.method(Connection.Method.POST)
		.userAgent(Util.USER_AGENT)
		.execute();
	}
}
