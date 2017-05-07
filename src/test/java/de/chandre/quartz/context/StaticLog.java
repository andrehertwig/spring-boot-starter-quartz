package de.chandre.quartz.context;

public class StaticLog {
	private static StaticLog LOG = new StaticLog();
	private String messasge;

	private StaticLog() {
		super();
		clear();
	}

	public static StaticLog getInstance() {
		return LOG;
	}

	public String getMessasge() {
		return messasge;
	}

	public void setMessasge(String messasge) {
		this.messasge = messasge;
	}

	public void clear() {
		this.messasge = null;
	}
}
