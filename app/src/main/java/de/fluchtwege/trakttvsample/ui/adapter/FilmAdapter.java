package de.fluchtwege.trakttvsample.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.fluchtwege.trakttvsample.R;
import de.fluchtwege.trakttvsample.databinding.FilmItemBinding;
import de.fluchtwege.trakttvsample.model.Film;
import de.fluchtwege.trakttvsample.viewmodel.FilmItemViewModel;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.BindingHolder> {

	private List<Film> films = new ArrayList<>();

	@Override
	public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		FilmItemBinding filmBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.film_item, parent, false);
		return new BindingHolder(filmBinding);
	}

	@Override
	public void onBindViewHolder(BindingHolder holder, int position) {
		FilmItemBinding postBinding = holder.binding;
		postBinding.setViewModel(new FilmItemViewModel(films.get(position)));
	}

	@Override
	public int getItemCount() {
		return films.size();
	}

	public void addFilms(List<Film> films) {
		Log.i("TAG", "add Films()");
		int size = this.films.size();
		this.films.addAll(size, films);
	}

	public void addFilm(Film film) {
		this.films.add(film);
	}

	public void clear() {
		this.films.clear();
	}

	public static class BindingHolder extends RecyclerView.ViewHolder {
		private final FilmItemBinding binding;

		public BindingHolder(FilmItemBinding binding) {
			super(binding.filmCard);
			this.binding = binding;
		}
	}
}
