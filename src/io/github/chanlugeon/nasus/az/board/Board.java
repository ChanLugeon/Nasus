package io.github.chanlugeon.nasus.az.board;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

import io.github.chanlugeon.nasus.az.Appzzang;
import io.github.chanlugeon.nasus.az.Header;
import io.github.chanlugeon.nasus.az.Util;
import io.github.chanlugeon.nasus.az.access.json.TokenJson;
import io.github.chanlugeon.nasus.plugin.collector.CommentCollector;

// 2456 free
/*
 *                                             <li class="disabled"><a><i class="fa fa-angle-double-left"></i></a></li><li class="disabled"><a><i class="fa fa-angle-left"></i></a></li><li class="active"><a>1</a></li><li><a href="./board.php?bo_table=humor&amp;page=2">2</a></li><li><a href="./board.php?bo_table=humor&amp;page=3">3</a></li><li><a href="./board.php?bo_table=humor&amp;page=4">4</a></li><li><a href="./board.php?bo_table=humor&amp;page=5">5</a></li><li><a href="./board.php?bo_table=humor&amp;page=6">6</a></li><li><a href="./board.php?bo_table=humor&amp;page=7">7</a></li><li><a href="./board.php?bo_table=humor&amp;page=8">8</a></li><li><a href="./board.php?bo_table=humor&amp;page=9">9</a></li><li><a href="./board.php?bo_table=humor&amp;page=10">10</a></li><li><a href="./board.php?bo_table=humor&amp;page=11"><i class="fa fa-angle-right"></i></a></li><li><a href="./board.php?bo_table=humor&amp;page=2052"><i class="fa fa-angle-double-right"></i></a></li>                                    </ul>
 */
public class Board {
	public static final String GOOD = "good";
	public static final String BAD = "nogood";
	private static final String BOARD = "http://appzzang.me/bbs/board.php";
	private static final String GOOD_APMS = "http://appzzang.me/bbs/good.apms.php";
	private static final String VIEW_COMMENT = "http://appzzang.me/bbs/view_comment.page.php";
	private static final String COMMENT_TOKEN = "http://appzzang.me/bbs/ajax.comment_token.php"; //
	private static final String WRITE_COMMENT_UPDATE = Appzzang.ORIGIN + "/bbs/write_comment_update.page.php";
	
	protected static final String WRITE = Appzzang.ORIGIN + "/bbs/write.php";
	protected static final String WRITE_UPDATE = Appzzang.ORIGIN + "/bbs/write_update.php";
	protected static final String WRITE_FORM = Appzzang.ORIGIN + "/apz_plugin/write.form.js.php";
	protected static final String AUTO_SAVE = Appzzang.ORIGIN + "/bbs/ajax.autosave.php";
	
	private static final Pattern TOKEN = Pattern.compile("apms_good[(]'\\w+', wr_id, 'good', 'wr_good', undefined, '(?<token>\\w{32})'[)];");
	private static final Pattern LINK = Pattern.compile("https[:][/][/]appzzang[.]me[/]bbs[/]board[.]php[?]bo_table[=]\\S+[&]amp[;]wr_id[=](?<wrId>[0-9]+)[&]amp[;]");
	// private static final Pattern FINAL_PAGE = Pattern.compile("page=2052\"><i class=\"fa fa-angle-double-right\"></i></a></li>                                    </ul>\r\n", Pattern.MULTILINE);
	private static final Pattern LAST_PAGE = Pattern.compile("page[=]([0-9]+)$"); // ^[.]/board.php?bo_table=humor
	
	protected static final Pattern WRITING_FORM_TOKEN = Pattern.compile("\"writingFormToken\"[:] \"(?<token>\\w{32})\"[,]");
	
	private static final String COMMENT_URL = "./view_comment.page.php";
	private static final int MAX_WR_ID_LIST = 30;
	private static final int DEFAULT_LAST_PAGE = 2050;
	
	protected final Map<String, String> cookie;
	private ArrayList<Integer> wrIdList = new ArrayList<Integer>(); // page
	protected final String boTable;
	private String token;
	private String referer = Appzzang.HOME;
	private int wrId, page = 0;
	private long appId;
	private int lastPage = DEFAULT_LAST_PAGE; // default value
	
	public Board(Map<String, String> cookie, BoardType type) {
		this.cookie = cookie;
		this.boTable = type.table();
		wrIdList.ensureCapacity(MAX_WR_ID_LIST);
	}
	
	public Board turnPage(int page) throws IOException {
		wrIdList.clear();
		
		String url = BOARD + "?bo_table=" + boTable + "&page=" + page;
		Connection.Response res = Jsoup.connect(url)
				.timeout(Util.WAIT_TIME)
				.headers(Header.getBasic())
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
				.header("Accept-Encoding", "gzip, deflate")
				.header("Host", Appzzang.HOST)
				.header("Referer", referer)
				.cookies(cookie)
				.method(Connection.Method.GET)
				.userAgent(Util.USER_AGENT)
				.execute();
		
		Document doc = res.parse();
		System.out.println(doc.html());
		Matcher m = LINK.matcher(doc.html());
		while (m.find()) {
			String token = m.group("wrId");
			
			System.out.println("wr_id: " + token);
			wrIdList.add(Integer.parseInt(token));
		}
		
		if (lastPage == DEFAULT_LAST_PAGE) {
			Matcher pm = LAST_PAGE.matcher(doc.selectFirst("#content_wrapper > div > div > div.at-wrap > div.at-main > section > div.list-wrap > div.list-page.text-center > ul > li:nth-child(14) > a").attr("href"));
			if (pm.find()) lastPage = Integer.parseInt(pm.group(1));
			System.out.println(lastPage+"lp");
		}
		
		cookie.putAll(res.cookies());
		this.page = page;
		referer = url;
		
		System.out.println(boTable + " turnPage: " + page + " " + wrIdList);
		return this;
	}
	
	public Board turnLastPage() throws IOException {
		turnPage(1);
		turnPage(lastPage);
		
		return this;
	}
	
	public int lastPage() {
		return lastPage;
	}
	
	public int currentPage() {
		return page;
	}
	
	public Board visitInReverseOrder() throws IOException {
		if (wrIdList.isEmpty()) {
			if (page == 0) {
				turnLastPage();
			} else if (page == 1) { // END
				return this;
			} else {
				turnPage(--page);
			}
		}
		
		wrId = wrIdList.get(0);
		wrIdList.remove(0);
		System.out.println("visitInReverseOrder: " + page + " " + wrId + " " + boTable);
		
		String url = BOARD + "?bo_table=" + boTable + "&wr_id=" + wrId;
		if (page > 1) url = url + "&page=" + page;
		
		Connection.Response res = Jsoup.connect(url)
				.timeout(Util.WAIT_TIME)
				.headers(Header.getBasic())
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
				.header("Accept-Encoding", "gzip, deflate")
				.header("Referer", referer)
				.cookies(cookie)
				.method(Connection.Method.GET) // POST
				.userAgent(Util.USER_AGENT)
				.execute();
		
		referer = url;
		token = stealToken(res.body());
		
		Document doc = res.parse();
		String appId = doc.select("meta[property=\"fb:app_id\"]").first().attr("content");
		this.appId = Long.parseLong(appId);
		
		return this;
	}
	
	/*
	 * NEED page
	 */
	public Board visitInNumericalOrder() throws IOException {
		if (wrIdList.isEmpty()) {
			turnPage(++page);
			// return this; // exception
		}
		
		wrId = wrIdList.get(0);
		wrIdList.remove(0);
		
		String url = BOARD + "?bo_table=" + boTable + "&wr_id=" + wrId;
		if (page > 1) url = url + "&page=" + page;
		
		Connection.Response res = Jsoup.connect(url)
				.timeout(Util.WAIT_TIME)
				.headers(Header.getBasic())
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
				.header("Accept-Encoding", "gzip, deflate")
				.header("Referer", referer)
				.cookies(cookie)
				.method(Connection.Method.GET) // POST
				.userAgent(Util.USER_AGENT)
				.execute();
		
		referer = url;
		token = stealToken(res.body());
		
		Document doc = res.parse();
		
		String appId = doc.select("meta[property=\"fb:app_id\"]").first().attr("content");
		this.appId = Long.parseLong(appId); //Long.valueOf(appId).longValue();
		
		return this;
	}
	
	public Board collect() throws IOException {
		String url = VIEW_COMMENT + "?bo_table=" + boTable + "&wr_id=" + wrId + "&crows=100";
		
		Connection.Response res = Jsoup.connect(url)
				.timeout(Util.WAIT_TIME)
				.headers(Header.getBasic())
				.header("Accept", "text/html, */*; q=0.01")
				.header("Accept-Encoding", "gzip, deflate")
				.header("Referer", referer)
				.header("X-Requested-With", "XMLHttpRequest")
				.cookies(cookie)
				.method(Connection.Method.GET) // POST
				.userAgent(Util.USER_AGENT)
				.execute();
		
		Document doc = res.parse();
		Elements es = doc.select("div.media > div.media-body > div.media-content > textarea");
		for (Element e: es) {
			System.out.printf("%s ", e.text());
			CommentCollector.collect(e.text());
		}
		
		
		// System.out.println("Board: ");
		
		return this;
	}
	
	public Board like() throws IOException {
		return evaluate(GOOD);
	}
	
	public Board dislike() throws IOException {
		return evaluate(BAD);
	}
	
	public Board evaluate(String choice) throws IOException {
		String url = GOOD_APMS + "?bo_table=" + boTable + "&wr_id=" + wrId +
				"&good=" + choice +"&token=" + token;
		
		Connection.Response res = Jsoup.connect(url)
				.timeout(Util.WAIT_TIME)
				.header("Accept", "application/json, text/javascript, */*; q=0.01")
				.header("Accept-Encoding", "gzip, deflate")
				.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
				.header("Connection", "keep-alive")
				.header("Content-Length", "5")
				.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
				.header("DNT", "1")
				.header("Host", "appzzang.me")
				.header("Origin", "http://appzzang.me")
				.header("Referer", referer)
				.header("X-Requested-With", "XMLHttpRequest")
				.cookies(cookie)
				.data("js", "on") // js=on
				.method(Connection.Method.POST)
				.userAgent(Util.USER_AGENT)
				.execute();
		
		System.out.println(res.body());
		
		return this;
	}
	
	public Board writeComment() throws IOException {
		return writeComment(Emoticon.mimic() + Emoticon.mimic());
	}
	
	// TODO Support UTF-8
	public Board writeComment(String comment) throws IOException {
		Map<String, String> data = new HashMap<>();
		data.put("token", requestCommentToken());
		data.put("w", "c");
		data.put("bo_table", boTable);
		data.put("wr_id", Integer.toString(wrId));
		data.put("comment_id", "");
		data.put("comment_url", COMMENT_URL + "?bo_table=" + boTable + "&wr_id=" + wrId + "&crows=100");
		data.put("crows", "100");
		data.put("page", "");
		data.put("is_good", "0");
		data.put("wr_content", comment);
		data.put("wr_secret", "secret");
		data.put("js", "on");
		
		Jsoup.connect(WRITE_COMMENT_UPDATE)
		.timeout(Util.WAIT_TIME)
		.header("Accept", "text/html, */*; q=0.01")
		.header("Accept-Encoding", "gzip, deflate")
		.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
		.header("Cache-Control", "no-cache")
		.header("Connection", "keep-alive")
		.header("Content-Length", Integer.toString(Util.contentLength(data))) // TODO
		.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
		.header("DNT", "1")
		.header("Host", Appzzang.HOST)
		.header("Origin", Appzzang.ORIGIN)
		.header("Pragma", "no-cache")
		.header("Referer", referer)
		.header("X-Requested-With", "XMLHttpRequest")
		.cookies(cookie)
		.data(data)
		.method(Connection.Method.POST)
		.userAgent(Util.USER_AGENT)
		.execute();
		
		//System.out.println(wrId);
		
		return this;
	}
	
	private String requestCommentToken() throws IOException {
		Connection.Response res = Jsoup.connect(COMMENT_TOKEN + "?_=" + appId)
				.timeout(Util.WAIT_TIME)
				.header("Accept", "application/json, text/javascript, */*; q=0.01")
				.header("Accept-Encoding", "gzip, deflate")
				.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
				.header("Cache-Control", "no-cache")
				.header("Connection", "keep-alive")
				.header("DNT", "1")
				.header("Host", Appzzang.HOST)
				.header("Pragma", "no-cache")
				.header("Origin", Appzzang.ORIGIN)
				.header("Referer", referer)
				.header("X-Requested-With", "XMLHttpRequest")
				.cookies(cookie)
				.method(Connection.Method.GET)
				.userAgent(Util.USER_AGENT)
				.execute();
		
		TokenJson json = new Gson().fromJson(res.parse().text(), TokenJson.class);
		
		System.out.println("requestCommentToken: " + json.token +boTable);
		return json.token;
	}
	
	private String stealToken(String html) {
		Matcher m = TOKEN.matcher(html);
		m.find();
		String token = m.group("token");
		
		// System.out.println("stealToken: " + token);
		return token;
	}
	
	public Board visit(int wrId) throws IOException {
		this.wrId = wrId;
		
		String url = BOARD + "?bo_table=" + boTable + "&wr_id=" + wrId;
		if (page > 1) url = url + "&page=" + page;
		Map<String, String> data = new HashMap<>();
		data.put("bo_table", boTable);
		data.put("wr_id", Integer.toString(wrId));
		
		Connection.Response res = Jsoup.connect(url)
				.timeout(Util.WAIT_TIME)
				.headers(Header.getBasic())
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
				.header("Accept-Encoding", "gzip, deflate")
				.header("Referer", referer)
				.cookies(cookie)
				.method(Connection.Method.GET) // POST
				.userAgent(Util.USER_AGENT)
				.execute();
		
		referer = url;
		token = stealToken(res.body());
		
		String appId = res.parse().select("meta[property=\"fb:app_id\"]").first().attr("content");
		this.appId = Long.parseLong(appId); //Long.valueOf(appId).longValue();
		
		System.out.println("Board: " + this.appId);
		return this;
	}
	
	@Deprecated
	public String stealToken() throws IOException {
		String url = VIEW_COMMENT + "?bo_table=" + boTable + "&wr_id=" + wrId +
				"&crows=100";
		Connection.Response res = Jsoup.connect(url)
				.timeout(Util.WAIT_TIME)
				.header("Accept", "text/html, */*; q=0.01")
				.header("Accept-Encoding", "gzip, deflate")
				.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
				.header("Connection", "keep-alive")
				.header("DNT", "1")
				.header("Host", "appzzang.me")
				.header("Origin", "http://appzzang.me")
				.header("Referer", referer)
				.header("X-Requested-With", "XMLHttpRequest")
				.cookies(cookie)
				.method(Connection.Method.GET)
				.userAgent(Util.USER_AGENT)
				//.ignoreContentType(true)
				.execute();
		
		Pattern p = Pattern.compile("apms_good[(]'humor', wr_id, 'good', 'wr_good', undefined, '(?<token>\\w{32})'[)];");
		Matcher m = p.matcher(res.body());
		m.find();
		String token = m.group("token");
		
		System.out.println("stealToken: " + token);
		return token;
	}
}
