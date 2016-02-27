package de.fluchtwege.trakttvsample.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.fluchtwege.trakttvsample.R;
import de.fluchtwege.trakttvsample.databinding.QueryResultFilmBinding;
import de.fluchtwege.trakttvsample.model.QueryResultFilm;
import de.fluchtwege.trakttvsample.viewmodel.QueryResultFilmViewModel;
import timber.log.Timber;

public class QueryResultFilmsAdapter extends FilmsAdapter<QueryResultFilmsAdapter.BindingHolder> {

	private List<QueryResultFilm> films = new ArrayList<>();

	@Override
	public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		QueryResultFilmBinding filmBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.query_result_film, parent, false);
		return new BindingHolder(filmBinding);
	}

	@Override
	public void onBindViewHolder(BindingHolder holder, int position) {
		QueryResultFilmBinding binding = holder.binding;
		binding.setViewModel(new QueryResultFilmViewModel(films.get(position)));
	}

	@Override
	public int getItemCount() {
		return films.size();
	}

	public void addFilms(List<QueryResultFilm> films) {
		Timber.d("addFilms() size: " + films.size());
		int size = this.films.size();
		this.films.addAll(size, films);
	}

	public void addAllFilms(List<QueryResultFilm> films) {
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
		private final QueryResultFilmBinding binding;

		public BindingHolder(QueryResultFilmBinding binding) {
			super(binding.filmCard);
			this.binding = binding;
		}
	}
}
