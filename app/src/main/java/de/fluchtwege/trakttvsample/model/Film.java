package de.fluchtwege.trakttvsample.model;

import android.databinding.BaseObservable;

public class Film extends BaseObservable{
	private String title;
	private String year;
	private String overview;
	private String imageUrl;

	public Film(String title, String year, String overview, String imageUrl) {
		this.title = title;
		this.year = year;
		this.overview = overview;
		this.imageUrl = imageUrl;
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

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
