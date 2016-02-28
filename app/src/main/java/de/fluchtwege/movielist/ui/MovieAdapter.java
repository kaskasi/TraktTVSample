package de.fluchtwege.movielist.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.fluchtwege.movielist.R;
import de.fluchtwege.movielist.databinding.MovieItemBinding;
import de.fluchtwege.movielist.model.Movie;
import de.fluchtwege.movielist.viewmodel.MovieItemViewModel;
import timber.log.Timber;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.BindingHolder> {

	private List<Movie> movies = new ArrayList<>();

	@Override
	public void onBindViewHolder( MovieAdapter.BindingHolder holder, int position) {
		MovieItemBinding binding = holder.binding;
		binding.setViewModel(new MovieItemViewModel(movies.get(position)));
	}

	@Override
	public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		MovieItemBinding filmBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.movie_item, parent, false);
		return new BindingHolder(filmBinding);

	}
	@Override
	public int getItemCount() {
		return movies.size();
	}

	public void addMovies(List<Movie> films) {
		Timber.d("addMovies() size: " + films.size());
		int size = this.movies.size();
		this.movies.addAll(size, films);
	}

	//wrapper since Mockito can't stub final methods
	public void notifyDataChanges() { notifyDataSetChanged(); }

	public void clear() {
		this.movies.clear();
	}

	public static class BindingHolder extends RecyclerView.ViewHolder {
		private final MovieItemBinding binding;

		public BindingHolder(MovieItemBinding binding) {
			super(binding.filmCard);
			this.binding = binding;
		}
	}
}
