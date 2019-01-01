package io.github.chanlugeon.nasus.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import com.google.gson.Gson;

import io.github.chanlugeon.nasus.az.Util;
import io.github.chanlugeon.nasus.plugin.SpellCheck.Json.WordList;

public class SpellCheck {
	private static final String HOST = "www.saramin.co.kr";
	private static final String ORIGIN = "http://" + HOST;
	private static final String REFERER = ORIGIN + "/zf_user/tools/character-counter";
	private static final String REQUEST = ORIGIN + "/zf_user/tools/spell-check";
	private static final Map<String, String> HEADER = new HashMap<>();
	
	private static final SpellCheck INSTANCE = new SpellCheck();
	
	private SpellCheck() {
		if (HEADER.isEmpty()) {
			HEADER.put("Accept", "application/json, text/javascript, */*; q=0.01");
			HEADER.put("Accept-Encoding", "gzip, deflate");
			HEADER.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
			HEADER.put("Cache-Control", "no-cache");
			HEADER.put("Connection", "keep-alive");
			HEADER.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			HEADER.put("DNT", "1");
			HEADER.put("Host", HOST);
			HEADER.put("Origin", ORIGIN);
			HEADER.put("Pragma", "no-cache");
			HEADER.put("Referer", REFERER);
			HEADER.put("X-Requested-With", "XMLHttpRequest");
		}
	}
	
	public static SpellCheck getInstance() {
		return INSTANCE;
	}
	
	public SpellCheck check(String query) throws IOException {
		Map<String, String> formData = new HashMap<>();
		formData.put("content", query);
		
		Connection.Response res = Jsoup.connect(REQUEST)
				.timeout(Util.WAIT_TIME)
				.headers(HEADER)
				.header("Content-Length", Integer.toString(Util.contentLength(formData)))
				.data(formData)
				.method(Connection.Method.POST)
				.userAgent(Util.USER_AGENT)
				.ignoreContentType(true)
				.execute();
		Json json = new Gson().fromJson(res.body(), Json.class);
		
		//System.out.println(json.cand_word_list.replace("\\\\u", "\\u"));
		//System.out.println(json.cand_word_list);
		System.out.println(new Gson().toJson(json));
		if (json.result_cnt == 0) System.out.println("PASS");
		System.out.println("총 " + json.result_cnt + "개의 맞춤법 오류를 찾았습니다.");
		for (Iterator<WordList> iter = json.word_list.iterator(); iter.hasNext();) {
			WordList wl = iter.next();
			
			System.out.println(wl.errorWord + " → " + wl.candWordList);
		}
		
		
		return this;
	}
	
	class Json {
		// String cand_word_list;
		// List<String> error_words = new ArrayList<>();
		// String original_text;
		// String popup_speller_list;
		int result_cnt;
		// String result_text;
		// String speller_list;
		List<WordList> word_list = new ArrayList<>();
		
		class WordList {
			String errorWord;
			List<String> candWordList = new ArrayList<>();
		}
	}
}
