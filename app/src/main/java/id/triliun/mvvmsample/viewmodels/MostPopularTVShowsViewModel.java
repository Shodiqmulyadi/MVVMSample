package id.triliun.mvvmsample.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import id.triliun.mvvmsample.repositories.MostPopularTVShowsRepository;
import id.triliun.mvvmsample.responses.TVShowsResponse;

public class MostPopularTVShowsViewModel extends ViewModel {

    private MostPopularTVShowsRepository mostPopularTVShowsRepository;

    public MostPopularTVShowsViewModel() {
        mostPopularTVShowsRepository = new MostPopularTVShowsRepository();

    }

    public LiveData<TVShowsResponse> getMostPopularTVShows(int page){
        return mostPopularTVShowsRepository.getMostPopularTvShows(page);
    }
}
