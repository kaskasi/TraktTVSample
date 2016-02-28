package de.fluchtwege.trakttvsample.viewmodel;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import de.fluchtwege.trakttvsample.R;
import de.fluchtwege.trakttvsample.model.Movie;

public class MovieItemViewModel {

	public final Movie movie;

	public MovieItemViewModel(Movie movie) {
		this.movie = movie;
	}

	@BindingAdapter({"bind:imageUrl"})
	public static void loadImage(ImageView view, String imageUrl) {
		Picasso.with(view.getContext())
				.load(imageUrl)
				.placeholder(R.mipmap.ic_launcher)
				.into(view);
	}

	public String getImageUrl() {
		return movie.images.poster.thumb;
	}

	public String getTitle() {
		return movie.title;
	}

	public String getYear() {
		return "" + movie.year;
	}

	public String getOverview() {
		return movie.overview;
	}

	public String getRating() {
		return String.valueOf(movie.rating);
	}
}
