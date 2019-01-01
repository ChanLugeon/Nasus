package io.github.chanlugeon.nasus.az.pointgame;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import io.github.chanlugeon.nasus.az.Appzzang;
import io.github.chanlugeon.nasus.az.Util;

public class PointGame {
	private static final String TAJA = Appzzang.ORIGIN + "/game/taja/";
	private static final String TAJA_POST = Appzzang.ORIGIN + "/game/taja/ajax_post.php";
	
	private static final String RSP =  Appzzang.ORIGIN + "/game/rsp/game.php";
	private static final String RSP_ON =  Appzzang.ORIGIN + "/game/rsp/game_on.php";
	
	public static final int FIXACTION = 1, SINGLE = 2, RANDOM = 3;
	public static final int SCISSORS = 1, ROCK = 2, PAPER = 3;
	
	private static final String GAME2048 =  Appzzang.ORIGIN + "/game/2048/score.php";
	
	private final Map<String, String> cookie;
	private final Map<String, String> header = new HashMap<>();
	private final String id;
	
	public PointGame(Map<String, String> cookie, String id) {
		this.cookie = cookie;
		this.id = id;
		
		header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.put("Accept-Encoding", "gzip, deflate");
		header.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
		header.put("Content-Type", "application/x-www-form-urlencoded");
		header.put("Origin", Appzzang.ORIGIN);
		header.put("Referer", Appzzang.HOME);
		header.put("Cache-Control", "no-cache");
		header.put("Connection", "keep-alive");
		header.put("DNT", "1");
		header.put("Host", Appzzang.HOST);
		header.put("Pragma", "no-cache");
		// header.put("Referer", Address.HOME);
		header.put("Upgrade-Insecure-Requests", "1");
	}
	
	public PointGame playTaja(int accuracy, int speed) throws IOException {
		if (accuracy > 100) throw new IllegalArgumentException("Accuracy should be less than 100.");
		if (speed >= 3000) throw new IllegalArgumentException("Speed should be less than 3000.");
		
		Map<String, String> data = new HashMap<>();
		data.put("mb_id", id);
		data.put("prnAccuracyCur", Integer.toString(accuracy));
		data.put("prnSpeedCur", Integer.toString(speed));
		
		Jsoup.connect(TAJA_POST)
		.timeout(Util.WAIT_TIME)
		.headers(header)
		.header("Referer", TAJA)
		.header("X-Requested-With", "XMLHttpRequest")
		.cookies(cookie)
		.data(data)
		.method(Connection.Method.POST)
		.userAgent(Util.USER_AGENT)
		.execute();
		
		return this;
	}
	
	public PointGame playRsp(int spr, int hand) throws IOException {
		String url = RSP + "?spr=" + spr;
		
		// STEAL TOKEN
		Connection.Response res = Jsoup.connect(url)
				.timeout(Util.WAIT_TIME)
				.headers(header)
				.header("Referer", Appzzang.HOME)
				.cookies(cookie)
				.method(Connection.Method.GET)
				.userAgent(Util.USER_AGENT)
				.execute();
		
		Map<String, String> data = new HashMap<>();
		data.put("tokenkey", res.parse().select("input[name=\"tokenkey\"]").val());
		data.put("spr", Integer.toString(spr));
		data.put("game_no", Integer.toString(hand));
		
		// POST
		Jsoup.connect(RSP_ON)
		.timeout(Util.WAIT_TIME)
		.headers(header)
		.header("Content-Length", Integer.toString(Util.contentLength(data)))
		.header("Content-Type", "application/x-www-form-urlencoded")
		.header("Referer", url)
		.cookies(cookie)
		.data(data)
		.method(Connection.Method.POST)
		.userAgent(Util.USER_AGENT)
		.execute();
		
		return this;
	}
	
	public PointGame play2048(int score) throws IOException {
		Map<String, String> header2 = new HashMap<>();
		header2.putAll(this.header);
		header2.put("Accept", "*/*");
		header2.put("X-Requested-With", "XMLHttpRequest");
		
		String data = "{\"metadata\":{\"score\":" + score + ",\"over\":true,\"won\":false,\"bestScore\":\"" + (score - 1) + "\",\"terminated\":true},\"grid\":{\"size\":4,\"cells\":[[]]},\"is_continue\":false}";
		
		Jsoup.connect(GAME2048)
		.timeout(Util.WAIT_TIME)
		.headers(header2)
		.header("Content-Length", Integer.toString(data.length()))
		.cookies(cookie)
		.requestBody(data)
		.method(Connection.Method.PUT)
		.userAgent(Util.USER_AGENT)
		.execute();
		
		return this;
	}
	
	@Deprecated
	public static void post2048(Map<String, String> cookie, int score) throws IOException {
		Map<String, String> header = new HashMap<>();
		header.put("Accept", "*/*");
		header.put("Accept-Encoding", "gzip, deflate");
		header.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
		header.put("Cache-Control", "no-cache");
		header.put("Connection", "keep-alive");
		//header.put("Content-Length", Integer.toString(data.length();););
		header.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		header.put("DNT", "1");
		header.put("Host", Appzzang.HOST);
		header.put("Origin", Appzzang.ORIGIN);
		header.put("Referer", Appzzang.HOME); // popup?
		header.put("X-Requested-With", "XMLHttpRequest");
		
		//String url = Game204 + "?spr=" + spr;
		String data = "{\"metadata\":{\"score\":" + score + ",\"over\":true,\"won\":false,\"bestScore\":\"" + (score - 1) + "\",\"terminated\":true},\"grid\":{\"size\":4,\"cells\":[[]]},\"is_continue\":false}";
		
		
		// PUT SCORE
		Jsoup.connect(GAME2048)
				.timeout(Util.WAIT_TIME)
				.header("Accept", "*/*")
				.header("Accept-Encoding", "gzip, deflate")
				.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
				.header("Cache-Control", "no-cache")
				.header("Connection", "keep-alive")
				.header("Content-Length", Integer.toString(data.length()))
				.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
				.header("DNT", "1")
				.header("Host", Appzzang.HOST)
				.header("Origin", Appzzang.ORIGIN)
				.header("Referer", Appzzang.HOME) // popup?
				.header("X-Requested-With", "XMLHttpRequest")
				.cookies(cookie)
				.requestBody(data)
				//.data(data)
				.method(Connection.Method.PUT)
				.userAgent(Util.USER_AGENT)
				.execute();
		
		// FETCH
		Jsoup.connect(GAME2048)
		.timeout(Util.WAIT_TIME)
		.headers(header)
		.header("Content-Length", "9") // init=true
		.cookies(cookie)
		.data("data", "true")
		//.requestBody(data)
		//.data(data)
		.method(Connection.Method.POST)
		.userAgent(Util.USER_AGENT)
		.execute();
	}
}
