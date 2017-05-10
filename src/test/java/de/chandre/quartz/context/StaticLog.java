package de.chandre.quartz.context;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple message appender to unit tests
 * @author André Hertwig
 */
public class StaticLog {
	private static StaticLog LOG = new StaticLog();
	private List<String> messasges = new ArrayList<>();

	private StaticLog() {
		super();
		clear();
	}

	public static StaticLog getInstance() {
		return LOG;
	}

	public List<String> getMessasges() {
		return messasges;
	}

	public void addMessasge(String messasge) {
		this.messasges.add(messasge);
	}

	public void clear() {
		this.messasges.clear();;
	}
}
