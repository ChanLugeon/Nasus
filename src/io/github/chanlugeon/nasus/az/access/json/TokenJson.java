package io.github.chanlugeon.nasus.az.access.json;

public class TokenJson {
	public String error, token, url;

	@Override
	public String toString() {
		return String.format("{error:\"%s\", token:\"%s\", url:\"%s\"}", error, token, url);
	}
}
