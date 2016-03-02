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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MovieListViewModelTest {

	private static final int LIST_SIZE = 10;
	public static final int CHILD_COUNT_MIDDLE = 5;
	public static final int VISIBLE_ITMES = 2;
	public static final int CHILD_COUNT_BOTTOM = 8;

	@Test
	public void When_viewmodel_is_initialized_the_10_most_popular_films_are_loaded() {
		final MovieListViewModel viewModel = createAndSetUpViewModel(CHILD_COUNT_MIDDLE);
		verify(viewModel, times(1)).getPopularMovies();
	}

	@NonNull
	private MovieListViewModel createAndSetUpViewModel(final int listPosition) {
		final MovieListViewModel viewModel = spy(new MovieListViewModel());
		LinearLayoutManager mockLayoutManager = createMockLayoutManagerForPosition(listPosition);
		viewModel.adapter.set(spy(new MovieAdapter()));
		doNothing().when(viewModel.adapter.get()).notifyDataChanges();
		viewModel.setUpViewModel(mockLayoutManager, Schedulers.immediate(), Schedulers.immediate());
		return viewModel;
	}

	@NonNull
	private LinearLayoutManager createMockLayoutManagerForPosition(final int childCount) {
		final LinearLayoutManager manager = spy(new LinearLayoutManager(mock(Context.class)));
		doReturn(childCount).when(manager).getChildCount();
		doReturn(VISIBLE_ITMES).when(manager).findFirstVisibleItemPosition();
		doReturn(LIST_SIZE).when(manager).getItemCount();
		return manager;
	}

	@Test
	public void When_user_has_not_reached_the_end_of_list_while_scrolling_next_10_films_are_loaded_only_once() {
		final MovieListViewModel viewModel = createAndSetUpViewModel(CHILD_COUNT_MIDDLE);

		viewModel.scrollListener.get().onScrolled(mock(RecyclerView.class), 0, 10);
		verify(viewModel, times(1)).getPopularMovies();
	}

	@Test
	public void When_user_has_reached_the_end_of_list_while_scrolling_next_10_films_are_loaded() {
		final MovieListViewModel viewModel = createAndSetUpViewModel(CHILD_COUNT_BOTTOM);

		doReturn(true).when(viewModel).hasReachedBottomOfList();
		viewModel.scrollListener.get().onScrolled(mock(RecyclerView.class), 0, 10);
		verify(viewModel, times(2)).getPopularMovies();
	}

	@Test
	public void When_user_presses_search_icon_in_toolbar_searchbar_is_shown() {
		final MovieListViewModel viewModel = createAndSetUpViewModel(CHILD_COUNT_MIDDLE);
		showSearchBar(viewModel);
		assertEquals(EditText.VISIBLE, viewModel.searchBarVisibility.get());
	}

	@Test
	public void When_user_presses_search_icon_in_toolbar_queryChangeSubject_is_registered() {
		final MovieListViewModel viewModel = createAndSetUpViewModel(CHILD_COUNT_MIDDLE);
		showSearchBar(viewModel);
		verify(viewModel, times(1)).registerQueryChangedSubject();
	}

	private void showSearchBar(final MovieListViewModel viewModel) {
		final MenuItem searchMenuItem = mock(MenuItem.class);
		doReturn(R.id.action_search).when(searchMenuItem).getItemId();
		viewModel.menuItemClickListener.get().onMenuItemClick(searchMenuItem);
	}

	@Test
	public void When_user_presses_Done_button_in_keyboard_searchbar_is_hidden() {
		final MovieListViewModel viewModel = createAndSetUpViewModel(CHILD_COUNT_MIDDLE);

		final KeyEvent doneKeyEvent = mock(KeyEvent.class);
		doReturn(EditorInfo.IME_ACTION_DONE).when(doneKeyEvent).getAction();
		viewModel.editorAction.get().onEditorAction(spy(new TextView(mock(Context.class))), 0, doneKeyEvent);
		assertEquals(EditText.GONE, viewModel.searchBarVisibility.get());
	}

	@Test
	public void When_user_presses_Back_button_in_navigation_bar_searchbar_is_hidden() {
		final MovieListViewModel viewModel = createAndSetUpViewModel(CHILD_COUNT_MIDDLE);
		showSearchBar(viewModel);
		viewModel.onBackPressed();
		assertEquals(EditText.GONE, viewModel.searchBarVisibility.get());
	}

	@Test
	public void When_user_enters_text_in_searchview_a_new_search_is_started() {
		final MovieListViewModel viewModel = createAndSetUpViewModel(CHILD_COUNT_MIDDLE);
		verify(viewModel).clearAndShowPopularMovies();

		showSearchBar(viewModel);
		verify(viewModel).registerQueryChangedSubject();

		final String query = "someQuery";
		doNothing().when(viewModel.adapter.get()).notifyDataChanges();
		viewModel.textWatcher.get().onTextChanged(query, 0, 0, query.length());

		verify(viewModel).query(argThat(new IsQueryString()));
	}

	@Test
	public void When_user_enters_text_in_searchview_and_an_old_search_is_running_old_search_is_stopped() {
		final MovieListViewModel viewModel = createAndSetUpViewModel(CHILD_COUNT_MIDDLE);
		MenuItem searchMenuItem = mock(MenuItem.class);
		doReturn(R.id.action_search).when(searchMenuItem).getItemId();
		String query = "foo";
		viewModel.query("f");
		viewModel.query("fo");
		viewModel.query(query);

		//TODO: check if onMovieLoaded was called
		assertTrue(false);
	}

	class IsQueryString extends ArgumentMatcher<String> {

		@Override
		public boolean matches(Object argument) {
			return argument instanceof String;
		}
	}

	@Test
	public void When_the_viewmodel_recieves_a_teardown_call_manager_class_should_not_leak() {
		final MovieListViewModel viewModel = createAndSetUpViewModel(CHILD_COUNT_MIDDLE);
		viewModel.tearDownViewModel();
		assertNull(viewModel.manager);
	}

	@Test
	public void When_the_viewmodel_is_setup_uilogic_is_setup() {
		final MovieListViewModel viewModel = createAndSetUpViewModel(CHILD_COUNT_MIDDLE);
		verify(viewModel).setUpUILogic();
	}

	@Test
	public void When_the_view_model_is_reset_the_adapter_will_be_cleared() {
		final MovieListViewModel viewModel = createAndSetUpViewModel(CHILD_COUNT_MIDDLE);
		doNothing().when(viewModel.adapter.get()).notifyDataChanges();
		viewModel.clearAndShowPopularMovies();
		verify(viewModel.adapter.get(), times(2)).clear();
	}

	@Test
	public void When_a_query_is_entered_the_adapter_will_be_cleared() {
		final MovieListViewModel viewModel = createAndSetUpViewModel(CHILD_COUNT_MIDDLE);

		verify(viewModel.adapter.get()).clear();
	}

}
