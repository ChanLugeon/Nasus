package io.github.chanlugeon.nasus.az.cookie;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import io.github.chanlugeon.nasus.az.Appzzang;
import io.github.chanlugeon.nasus.az.Util;
import io.github.chanlugeon.nasus.az.access.LoginCheck;
import io.github.chanlugeon.nasus.az.board.Attendance;
import io.github.chanlugeon.nasus.az.board.Board;
import io.github.chanlugeon.nasus.az.board.BoardType;
import io.github.chanlugeon.nasus.az.board.DonationBoard;
import io.github.chanlugeon.nasus.az.pointgame.PointGame;

public class Visitor {
	private final Map<String, String> cookie;
	private String id, password;
	private PointGame pointGame = null;
	
	public Visitor() throws IOException {
		// Homepage
		Connection.Response res = Jsoup.connect(Appzzang.HOME)
				.timeout(Util.WAIT_TIME)
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
				.header("Accept-Encoding", "gzip, deflate")
				.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
				.header("Cache-Control", "max-age=0")
				.header("Connection", "keep-alive")
				.header("DNT", "1")
				.header("Host", "appzzang.me")
				.header("Pragma", "no-cache")
				.header("Upgrade-Insecure-Requests", "1")
				.userAgent(Util.USER_AGENT)
				.method(Connection.Method.GET)
				.ignoreContentType(true)
				.execute();
		cookie = res.cookies();

		//System.out.println("Account Init> " + cookie.toString() + "\n");
	}
	
	public LoginCheck.Response login(String id, String password) throws IOException {
		this.id = id;
		this.password = password;
		
		return new LoginCheck(cookie).login(id, password); // FOR ID
	}
	
	@Deprecated
	public LoginCheck newLoginCheck() { // Id and password are not saved.
		return new LoginCheck(cookie);
	}
	
	public Board newBoard(BoardType type) {
		return new Board(cookie, type); 
	}
	
	public DonationBoard newDonationBoard() {
		return new DonationBoard(cookie); 
	}
	
	public PointGame newPointGame() {
		if (pointGame == null) {
			pointGame = new PointGame(cookie, id);
		}
		
		return pointGame;
	}
	
	public Visitor playTaja(int speed) throws IOException {
		if (pointGame == null) {
			pointGame = new PointGame(cookie, id);
		}
		
		pointGame.playTaja(100, speed);
		
		return this;
	}
	
	public Visitor playRsp(int spr) throws IOException {
		if (pointGame == null) {
			pointGame = new PointGame(cookie, id);
		}
		
		pointGame.playRsp(spr, PointGame.SCISSORS);
		
		return this;
	}
	
	public Visitor play2048(int score) throws IOException {
		if (pointGame == null) {
			pointGame = new PointGame(cookie, id);
		}
		
		pointGame.play2048(score);
		
		return this;
	}
	
	public Visitor attend(String comment) throws IOException {
		new Attendance(cookie).attend(comment);
		
		return this;
	}
	
	@Deprecated
	public Map<String, String> getCookie() {
		return cookie;
	}
}
