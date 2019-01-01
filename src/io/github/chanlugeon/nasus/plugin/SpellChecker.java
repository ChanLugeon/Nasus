package io.github.chanlugeon.nasus.plugin;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import io.github.chanlugeon.nasus.az.Util;

public class SpellChecker {
	private static final String HOST = "speller.cs.pusan.ac.kr";
	private static final String ORIGIN = "http://" + HOST;
	private static final String ADDRESS = ORIGIN + "/PnuWebSpeller/lib/check.asp";
	private static Map<String, String> formHeader = new HashMap<>();
	
	public SpellChecker() {
		if (formHeader.isEmpty()) {
			
		}
	}
	
	public static void check(String content) throws IOException {
		String encodedContent = URLEncoder.encode(content, "UTF-8");
		encodedContent = encodedContent.replaceAll("%0A", "%0D%0A"); // new line
		String text1 = "text1=" + encodedContent;
		
		if (formHeader.isEmpty()) {
			formHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			formHeader.put("Accept-Encoding", "gzip, deflate");
			formHeader.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
			formHeader.put("Cache-Control", "max-age=0");
			formHeader.put("Connection", "keep-alive");
			formHeader.put("Content-Length", Integer.toString(text1.length()));
			formHeader.put("Content-Type", "application/x-www-form-urlencoded");

			formHeader.put("DNT", "1");
			formHeader.put("Host", HOST);
			formHeader.put("Origin", ORIGIN);
			formHeader.put("Referer", "http://speller.cs.pusan.ac.kr/PnuWebSpeller/Default.htm");
			formHeader.put("Upgrade-Insecure-Requests", "1");
			formHeader.put("user-agent", Util.USER_AGENT);
		}
		
		Connection.Response post = Jsoup.connect(ADDRESS)
				.timeout(Util.WAIT_TIME)
				.headers(formHeader)
				.data("text1", content)
				.method(Connection.Method.POST)
				.userAgent(Util.USER_AGENT)
				.referrer("https://search.naver.com/search.naver?sm=top_sug.pre&fbm=1&acr=1&acq=%EB%A7%9E%EC%B6%A4%E3%85%82&qdt=0&ie=utf8&query=%EB%A7%9E%EC%B6%A4%EB%B2%95%EA%B2%80%EC%82%AC%EA%B8%B0")
				.execute();
		Document doc = post.parse();
		Elements es = doc.select("#tableErr_0");
		System.out.println(es.toString());
		
		Elements err = doc.select("#tableErr_0");;
		for (int i = 0; !err.isEmpty();) {
			System.out.println(i + "~~~");
			Elements errWord = err.select(".tdErrWord");
			System.out.printf("%s -> ", errWord.text());
			Elements replace = err.select(".tdReplace");
			String descrizione = Jsoup.parse(replace.html().replaceAll("(?i)<br[^>]*>", "br2n")).text();
			String text = descrizione.replaceAll("br2n", ", ");
			System.out.printf("%s\n", text);
			Elements help = err.select(".tdETNor");
			System.out.printf("HELP: ", help.text().split("\n")[0]);
			
			err = doc.select("#tableErr_" + ++i);
		}
		
	}
}