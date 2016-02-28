package de.fluchtwege.trakttvsample.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.fluchtwege.trakttvsample.R;
import de.fluchtwege.trakttvsample.databinding.MovieItemBinding;
import de.fluchtwege.trakttvsample.model.Movie;
import de.fluchtwege.trakttvsample.viewmodel.MovieItemViewModel;
import timber.log.Timber;

public class MovieAdapter extends FilmsAdapter<MovieAdapter.BindingHolder> {

	private List<Movie> films = new ArrayList<>();

	@Override
	public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		MovieItemBinding filmBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.movie_item, parent, false);
		return new BindingHolder(filmBinding);
	}

	@Override
	public void onBindViewHolder(BindingHolder holder, int position) {
		MovieItemBinding binding = holder.binding;
		binding.setViewModel(new MovieItemViewModel(films.get(position)));
	}

	@Override
	public int getItemCount() {
		return films.size();
	}

	public void addFilms(List<Movie> films) {
		Timber.d("addFilms() size: " + films.size());
		int size = this.films.size();
		this.films.addAll(size, films);
	}

	public void clear() {
		this.films.clear();
	}

	//wrapper since Mockito can't stub final methods
	public void notifyDataChanges() {
		notifyDataSetChanged();
	}

	public static class BindingHolder extends RecyclerView.ViewHolder {
		private final MovieItemBinding binding;

		public BindingHolder(MovieItemBinding binding) {
			super(binding.filmCard);
			this.binding = binding;
		}
	}
}
