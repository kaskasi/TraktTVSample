package de.fluchtwege.trakttvsample.net;

public class DataManager {

	private static DataManager instance;

	private DataManager() {

	}

	public static DataManager getInstance() {
		if (instance != null) {
			instance = new DataManager();
		}
		return instance;
	}
}
