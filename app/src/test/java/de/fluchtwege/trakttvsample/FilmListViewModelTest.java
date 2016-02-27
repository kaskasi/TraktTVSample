package de.fluchtwege.trakttvsample;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import de.fluchtwege.trakttvsample.model.Film;
import de.fluchtwege.trakttvsample.ui.adapter.FilmAdapter;
import de.fluchtwege.trakttvsample.viewmodel.FilmListViewModel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class FilmListViewModelTest {

	private static final int LIST_SIZE = 10;
	public static final int CHILD_COUNT_MIDDLE = 5;
	public static final int VISIBLE_ITMES = 2;
	public static final int CHILD_COUNT_BOTTOM = 8;
	public static final int FIRST_PAGE = 0;
	private static final int SECOND_PAGE = 1;


	@Test
	public void When_viewmodel_is_initialized_the_10_most_popular_films_are_loaded() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final FilmListViewModel viewModel = createAndInitializeViewModel(manager);
		verify(viewModel, times(1)).getPopularFilms();
	}


	@NonNull
	private LinearLayoutManager createMockLayoutManagerForPosition(int childCount) {
		final LinearLayoutManager manager = mock(LinearLayoutManager.class);
		when(manager.getChildCount()).thenReturn(childCount);
		when(manager.findFirstVisibleItemPosition()).thenReturn(VISIBLE_ITMES);
		when(manager.getItemCount()).thenReturn(LIST_SIZE);
		return manager;
	}

	@NonNull
	private FilmListViewModel createAndInitializeViewModel(LinearLayoutManager manager) {
		final FilmListViewModel viewModel = spy(new FilmListViewModel(manager));
		doNothing().when(viewModel).getPopularFilms();
		viewModel.adapter = spy(new FilmAdapter());
		viewModel.init();
		return viewModel;
	}

	@Test
	public void When_10_most_popular_films_are_loaded_on_start_they_are_added_to_the_list() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final FilmListViewModel viewModel = createAndInitializeViewModel(manager);

		assertEquals(10, viewModel.adapter.getItemCount());
	}

	public List<List<Film>> getFilms() {
		final List<List<Film>> result = new ArrayList<>();
		result.add(createListOf10Films(FIRST_PAGE));
		result.add(createListOf10Films(SECOND_PAGE));
		return result;
	}

	public List<Film> createListOf10Films(int pagesLoaded) {
		ArrayList<Film> films = new ArrayList<>();

		for (int i = 0; i < 10; i += 2) {
			films.add(new Film("Der Film", "1932 : " + i * pagesLoaded, "Dieser Film ist der absolut beste Film überhaupt. Man kann ihn mehrere male sehen ohne ihn zu verstehen. " +
					"Er ist so witzig wie nur möglich. Action gepaart mit Spannung und beste Unterhaltung. 23 Sterne.", "http://ste.india.com/sites/default/files/2014/12/17/303980-film-700.jpg"));
			films.add(new Film("Der Andere Film", "1965", "ddshf jksdhf ksjhdf ksjdfh ksjdhf ksjdhfkjsdhf ksjdhf skdjfhsjkdfh ksjdfh sdjkfh sdjkf " +
					"asdhaj khd kajshd kajshd kasjhd kasjhd kjashd kjads " +
					"h adjshdk aufnsjdfh sdjf xcdnscjdsksdf sdjhf sjdhf sdjf s",
					"http://ste.india.com/sites/default/files/2014/12/17/303980-film-700.jpg"));

		}
		return films;
	}

	@Test
	public void When_user_has_not_reached_the_end_of_list_while_scrolling_next_10_films_are_not_loaded() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final FilmListViewModel viewModel = createAndInitializeViewModel(manager);
		verifyNoMoreInteractions(viewModel.adapter);
	}


	@Test
	public void When_user_has_reached_the_end_of_list_while_scrolling_next_10_films_are_loaded() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_BOTTOM);
		final FilmListViewModel viewModel = createAndInitializeViewModel(manager);
		verify(viewModel).getPopularFilms();

		viewModel.scrollListener.onScrolled(mock(RecyclerView.class), 0, 10);
		verify(viewModel).getPopularFilms();
		assertEquals(20, viewModel.adapter.getItemCount());

	}

	@Test
	public void When_next_10_films_are_laoded_the_entries_should_be_added_to_the_end_of_the_list() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_BOTTOM);
		final FilmListViewModel viewModel = createAndInitializeViewModel(manager);
		viewModel.scrollListener.onScrolled(mock(RecyclerView.class), 0, 10);
		verify(viewModel, times(2)).getPopularFilms();
		//assertEquals(20, viewModel.adapter.getItemCount());
	}


	@Test
	public void When_user_presses_search_icon_in_toolbar_searchbar_is_shown() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final FilmListViewModel viewModel = createAndInitializeViewModel(manager);
		MenuItem searchMenuItem = mock(MenuItem.class);
		doReturn(R.id.action_search).when(searchMenuItem).getItemId();
		viewModel.menuItemClickListener.onMenuItemClick(searchMenuItem);
		assertEquals(EditText.VISIBLE, viewModel.searchBarVisibility);
	}

	@Test
	public void When_user_presses_Done_button_in_keyboard_searchbar_is_hidden() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final FilmListViewModel viewModel = createAndInitializeViewModel(manager);
		KeyEvent doneKeyEvent = mock(KeyEvent.class);
		doReturn(EditorInfo.IME_ACTION_DONE).when(doneKeyEvent).getAction();
		viewModel.editorAction.onEditorAction(mock(TextView.class), 0, doneKeyEvent);
		assertEquals(EditText.GONE, viewModel.searchBarVisibility);

	}

	@Test
	public void When_user_enters_text_in_searchview_a_new_search_is_started() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final FilmListViewModel viewModel = createAndInitializeViewModel(manager);
		MenuItem searchMenuItem = mock(MenuItem.class);
		doReturn(R.id.action_search).when(searchMenuItem).getItemId();
		viewModel.menuItemClickListener.onMenuItemClick(searchMenuItem);

		viewModel.textWatcher.onTextChanged("sss", 2, 2, 2);
		verify(viewModel).getSearchResult("ssss");
	}

	@Test
	public void When_user_enters_text_in_searchview_and_an_old_search_is_running_old_search_is_stopped() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final FilmListViewModel viewModel = createAndInitializeViewModel(manager);
		MenuItem searchMenuItem = mock(MenuItem.class);
		doReturn(R.id.action_search).when(searchMenuItem).getItemId();
		viewModel.menuItemClickListener.onMenuItemClick(searchMenuItem);
	}


}
