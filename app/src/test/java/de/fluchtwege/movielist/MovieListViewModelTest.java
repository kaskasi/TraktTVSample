package de.fluchtwege.movielist;

import android.content.Context;
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

import de.fluchtwege.movielist.ui.MovieAdapter;
import de.fluchtwege.movielist.viewmodel.MovieListViewModel;
import rx.schedulers.Schedulers;

import static junit.framework.Assert.assertTrue;
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
		verify(viewModel, times(1)).getPopularMovies();
	}

	@NonNull
	private LinearLayoutManager createMockLayoutManagerForPosition(final int childCount) {
		final LinearLayoutManager manager = mock(LinearLayoutManager.class);
		when(manager.getChildCount()).thenReturn(childCount);
		when(manager.findFirstVisibleItemPosition()).thenReturn(VISIBLE_ITMES);
		when(manager.getItemCount()).thenReturn(LIST_SIZE);
		return manager;
	}

	@NonNull
	private MovieListViewModel createAndInitializeViewModel(final LinearLayoutManager manager) {
		final MovieListViewModel viewModel = spy(new MovieListViewModel(manager, Schedulers.immediate(), Schedulers.immediate()));
		viewModel.adapter.set(spy(new MovieAdapter()));
		doNothing().when(viewModel.adapter.get()).notifyDataChanges();
		viewModel.resetToPopularFilms();
		return viewModel;
	}

	@Test
	public void When_user_has_not_reached_the_end_of_list_while_scrolling_next_10_films_are_loaded_only_once() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final MovieListViewModel viewModel = createAndInitializeViewModel(manager);
		verify(viewModel, times(1)).getPopularMovies();
	}

	@Test
	public void When_user_has_reached_the_end_of_list_while_scrolling_next_10_films_are_loaded() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_BOTTOM);
		final MovieListViewModel viewModel = createAndInitializeViewModel(manager);
		verify(viewModel).getPopularMovies();

		viewModel.scrollListener.onScrolled(mock(RecyclerView.class), 0, 10);
		verify(viewModel).getPopularMovies();
	}

	@Test
	public void When_user_presses_search_icon_in_toolbar_searchbar_is_shown() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final MovieListViewModel viewModel = createAndInitializeViewModel(manager);
		showSearchBar(viewModel);
		assertEquals(EditText.VISIBLE, viewModel.searchBarVisibility.get());
	}

	private void showSearchBar(final MovieListViewModel viewModel) {
		final MenuItem searchMenuItem = mock(MenuItem.class);
		doReturn(R.id.action_search).when(searchMenuItem).getItemId();
		viewModel.menuItemClickListener.onMenuItemClick(searchMenuItem);
	}

	@Test
	public void When_user_presses_Done_button_in_keyboard_searchbar_is_hidden() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final MovieListViewModel viewModel = createAndInitializeViewModel(manager);
//		showSearchBar(viewModel);

		final KeyEvent doneKeyEvent = mock(KeyEvent.class);
		doReturn(EditorInfo.IME_ACTION_DONE).when(doneKeyEvent).getAction();
		viewModel.editorAction.onEditorAction(spy(new TextView(mock(Context.class))), 0, doneKeyEvent);
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
		verify(viewModel).getPopularMovies();

		showSearchBar(viewModel);
		final String query = "someQuery";
		doNothing().when(viewModel.adapter.get()).notifyDataChanges();
		viewModel.textWatcher.onTextChanged(query, 0, 0, query.length());

		verify(viewModel).query(argThat(new ArgumentMatcher<String>() {
											@Override
											public boolean matches(Object argument) {
												if (argument != null) {
													String arg = (String) argument;
													return arg.equals(query);
												}
												return false;
											}
										}
		));
	}

	@Test
	public void When_user_enters_text_in_searchview_and_an_old_search_is_running_old_search_is_stopped() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final MovieListViewModel viewModel = createAndInitializeViewModel(manager);
		MenuItem searchMenuItem = mock(MenuItem.class);
		doReturn(R.id.action_search).when(searchMenuItem).getItemId();
		String query = "foo";
		viewModel.query("f");
		viewModel.query("fo");
		viewModel.query(query);

		assertTrue(false);
	}

	class IsQueryString extends ArgumentMatcher {

		@Override
		public boolean matches(Object argument) {
			return argument instanceof String;
		}
	}

	@Test
	public void When_the_viewmodel_recieves_a_teardown_call_manager_class_should_not_leak() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final MovieListViewModel viewModel = createAndInitializeViewModel(manager);
		viewModel.tearDown();
		assertEquals(null, viewModel.manager);
	}

	@Test
	public void When_the_view_model_is_reset_the_adapter_will_be_cleared() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final MovieListViewModel viewModel = createAndInitializeViewModel(manager);
		doNothing().when(viewModel.adapter.get()).notifyDataChanges();
		viewModel.resetToPopularFilms();
		verify(viewModel.adapter.get()).clear();
	}

	@Test
	public void When_a_query_is_entered_the_adapter_will_be_cleared() {
		final LinearLayoutManager manager = createMockLayoutManagerForPosition(CHILD_COUNT_MIDDLE);
		final MovieListViewModel viewModel = createAndInitializeViewModel(manager);
		//viewModel.query("fo");


		verify(viewModel.adapter.get()).clear();
	}

}
