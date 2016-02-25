package de.fluchtwege.trakttvsample.net;

import java.util.ArrayList;
import java.util.List;

import de.fluchtwege.trakttvsample.model.Film;
import de.fluchtwege.trakttvsample.viewmodel.FilmListViewModel;

public class DataManager {

	private static DataManager instance;

	private DataManager() {

	}

	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}

	//Until there is RX implemented we need to have ViewModelInstance as Param
	public List<Film> loadPopularFilms(FilmListViewModel filmListViewModel, int offset) {
		List<Film> films= new ArrayList<>();
		Film film1 = new Film("Der Film", "1932", "Dieser Film ist der absolut beste Film überhaupt. Man kann ihn mehrere male sehen ohne ihn zu verstehen. " +
				"Er ist so witzig wie nur möglich. Action gepaart mit Spannung und beste Unterhaltung. 23 Sterne.", "http://ste.india.com/sites/default/files/2014/12/17/303980-film-700.jpg");
		Film film2 = new Film("Der Andere Film", "1965", "ddshf jksdhf ksjhdf ksjdfh ksjdhf ksjdhfkjsdhf ksjdhf skdjfhsjkdfh ksjdfh sdjkfh sdjkf " +
				"asdhaj khd kajshd kajshd kasjhd kasjhd kjashd kjads " +
				"h adjshdk aufnsjdfh sdjf xcdnscjdsksdf sdjhf sjdhf sdjf s",
				"http://ste.india.com/sites/default/files/2014/12/17/303980-film-700.jpg");
		films.add(film1);
		films.add(film2);
		filmListViewModel.onPopularFilmsloaded(films);
		return films;
	}
}
