package de.fluchtwege.trakttvsample.viewmodel;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import de.fluchtwege.trakttvsample.R;
import de.fluchtwege.trakttvsample.model.Film;

public class FilmItemViewModel {

	public final Film film;

	public FilmItemViewModel(Film film) {
		this.film = film;
	}

	@BindingAdapter({"bind:imageUrl"})
	public static void loadImage(ImageView view, String imageUrl) {
		Picasso.with(view.getContext())
				.load(imageUrl)
				.placeholder(R.mipmap.ic_launcher)
				.into(view);
	}

	public String getImageUrl() {
		return film.getImageUrl();
	}

	public String getTitle() {
		return film.getTitle();
	}

	public String getYear() {
		return film.getYear();
	}

	public String getOverview() {
		return film.getOverview();
	}
}
