package de.fluchtwege.trakttvsample.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.fluchtwege.trakttvsample.R;
import de.fluchtwege.trakttvsample.databinding.PopularFilmBinding;
import de.fluchtwege.trakttvsample.model.PopularFilm;
import de.fluchtwege.trakttvsample.viewmodel.PopularFilmViewModel;
import timber.log.Timber;

public class PopularFilmsAdapter extends FilmsAdapter<PopularFilmsAdapter.BindingHolder> {

	private List<PopularFilm> films = new ArrayList<>();

	@Override
	public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		PopularFilmBinding filmBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.popular_film, parent, false);
		return new BindingHolder(filmBinding);
	}

	@Override
	public void onBindViewHolder(BindingHolder holder, int position) {
		PopularFilmBinding binding = holder.binding;
		binding.setViewModel(new PopularFilmViewModel(films.get(position)));
	}

	@Override
	public int getItemCount() {
		return films.size();
	}

	public void addFilms(List<PopularFilm> films) {
		Timber.d("addFilms() size: " + films.size());
		this.films.addAll(films);
	}

	public void clear() {
		this.films.clear();
	}

	//wrapper since Mockito can't stub final methods
	public void notifyDataChanges() {
		notifyDataSetChanged();
	}

	public static class BindingHolder extends RecyclerView.ViewHolder {
		private final PopularFilmBinding binding;

		public BindingHolder(PopularFilmBinding binding) {
			super(binding.filmCard);
			this.binding = binding;
		}
	}
}
