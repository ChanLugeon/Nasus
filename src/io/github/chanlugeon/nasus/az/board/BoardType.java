package io.github.chanlugeon.nasus.az.board;

public enum BoardType {
	// Community
	FREE("free"),
	ANIMATION("animation"),
	HUMOR("humor"),
	BITCOIN("coin"), // Not popular
	GAME("gametalk"),
	NOVEL("novel"),
	MUSIC("music"), // Not popular
	ANONUMOUS("anonymous"), // Special
	
	// Question, Request, Donation
	QUESTION("Question"),
	REQUEST("Appl"),
	DONATION("donation"),
	
	// Gallery
	PHOTO("photo"),
	CELEBRITY("star"),
	HOT("hot"),
	
	// Others
	MY_SHARE("myshare"),
	PREFACE("preface"),
	GREETING("hi"),
	
	NOTICE("notice"),
	PARTICIPATION("participation");
	
	private final String boTable;
	
	BoardType(String boTable) {
		this.boTable = boTable;
	}
	
	public String table() {
		return boTable;
	}
}
