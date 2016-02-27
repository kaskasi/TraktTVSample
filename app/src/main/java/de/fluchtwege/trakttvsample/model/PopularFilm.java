package de.fluchtwege.trakttvsample.model;

//TODO: generate pojo from http response
public class PopularFilm extends Film {
	public String title;
	public String year;
	public String key;

	public PopularFilm(String title, String year, String key) {
		this.title = title;
		this.year = year;
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
