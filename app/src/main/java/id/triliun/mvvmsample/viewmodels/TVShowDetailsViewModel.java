package id.triliun.mvvmsample.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import id.triliun.mvvmsample.repositories.MostPopularTVShowsRepository;
import id.triliun.mvvmsample.repositories.TVShowDetailsRepository;
import id.triliun.mvvmsample.responses.TVShowsDetailsResponse;
import id.triliun.mvvmsample.responses.TVShowsResponse;

public class TVShowDetailsViewModel extends ViewModel  {

    private TVShowDetailsRepository tvShowDetailsRepository;

    public TVShowDetailsViewModel() {
        tvShowDetailsRepository = new TVShowDetailsRepository();

    }

    public LiveData<TVShowsDetailsResponse> getTVShowDetails(String tvShowId){
        return tvShowDetailsRepository.getTVShowDetails(tvShowId);
    }
}
