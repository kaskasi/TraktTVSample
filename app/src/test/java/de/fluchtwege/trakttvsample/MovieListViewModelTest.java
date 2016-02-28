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
import org.mockito.ArgumentMatcher;

import de.fluchtwege.trakttvsample.ui.adapter.MovieAdapter;
import de.fluchtwege.trakttvsample.viewmodel.MovieListViewModel;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MovieListViewModelTest {

	private static final int LIST_SIZE = 10;
	public static final int CHILD_COUNT_MIDDLE = 5;
	public static final int VISIBLE_ITMES = 2;
	public static final int CHILD_COUNT_BOTTOM = 8;

	@Test
	public void When_viewmodel_is_initialized_the_10_most_popular_films_are_loaded() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final MovieListViewModel viewModel = createAndInitializeViewModel(manager);
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
	private MovieListViewModel createAndInitializeViewModel(LinearLayoutManager manager) {
		final MovieListViewModel viewModel = spy(new MovieListViewModel(manager));
		viewModel.adapter.set(spy(new MovieAdapter()));
		viewModel.initWithSchedulers(Schedulers.immediate(), Schedulers.immediate());
		return viewModel;
	}

	@Test
	public void When_user_has_not_reached_the_end_of_list_while_scrolling_next_10_films_are_loaded_only_once() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final MovieListViewModel viewModel = createAndInitializeViewModel(manager);
		verify(viewModel, times(1)).getPopularFilms();
	}

	@Test
	public void When_user_has_reached_the_end_of_list_while_scrolling_next_10_films_are_loaded() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_BOTTOM);
		final MovieListViewModel viewModel = createAndInitializeViewModel(manager);
		verify(viewModel).getPopularFilms();

		viewModel.scrollListener.onScrolled(mock(RecyclerView.class), 0, 10);
		verify(viewModel).getPopularFilms();
	}

	@Test
	public void When_user_presses_search_icon_in_toolbar_searchbar_is_shown() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final MovieListViewModel viewModel = createAndInitializeViewModel(manager);
		showSearchBar(viewModel);
		assertEquals(EditText.VISIBLE, viewModel.searchBarVisibility.get());
	}

	private void showSearchBar(MovieListViewModel viewModel) {
		MenuItem searchMenuItem = mock(MenuItem.class);
		doReturn(R.id.action_search).when(searchMenuItem).getItemId();
		viewModel.menuItemClickListener.onMenuItemClick(searchMenuItem);
	}

	@Test
	public void When_user_presses_Done_button_in_keyboard_searchbar_is_hidden() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final MovieListViewModel viewModel = createAndInitializeViewModel(manager);
		showSearchBar(viewModel);

		KeyEvent doneKeyEvent = mock(KeyEvent.class);
		doReturn(EditorInfo.IME_ACTION_DONE).when(doneKeyEvent).getAction();
		viewModel.editorAction.onEditorAction(mock(TextView.class), 0, doneKeyEvent);

		assertEquals(EditText.GONE, viewModel.searchBarVisibility.get());
	}


	@Test
	public void When_user_presses_Back_button_in_navigation_bar_searchbar_is_hidden() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final MovieListViewModel viewModel = createAndInitializeViewModel(manager);
		showSearchBar(viewModel);
		viewModel.onBackPressed();
		assertEquals(EditText.GONE, viewModel.searchBarVisibility.get());
	}

	@Test
	public void When_user_enters_text_in_searchview_a_new_search_is_started() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final MovieListViewModel viewModel = createAndInitializeViewModel(manager);
		verify(viewModel).getPopularFilms();

		showSearchBar(viewModel);
		final String query = "someQuery";
		doNothing().when(viewModel.adapter.get()).notifyDataChanges();
		viewModel.textWatcher.onTextChanged(query, 0, 0, query.length());

		verify(viewModel).getSearchResult((String) argThat(new IsQueryString()));
	}

	@Test
	public void When_user_enters_text_in_searchview_and_an_old_search_is_running_old_search_is_stopped() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final MovieListViewModel viewModel = createAndInitializeViewModel(manager);
		MenuItem searchMenuItem = mock(MenuItem.class);
		doReturn(R.id.action_search).when(searchMenuItem).getItemId();
		viewModel.menuItemClickListener.onMenuItemClick(searchMenuItem);
		//TODO: implement throttling strategy in rx (switchOnNext, sample, debounce ? )
	}

	class IsQueryString extends ArgumentMatcher {

		@Override
		public boolean matches(Object argument) {
			return argument instanceof String;
		}
	}

}
