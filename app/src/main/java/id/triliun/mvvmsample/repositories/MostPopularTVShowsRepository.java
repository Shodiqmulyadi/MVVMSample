package id.triliun.mvvmsample.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import id.triliun.mvvmsample.network.APIClient;
import id.triliun.mvvmsample.network.APIService;
import id.triliun.mvvmsample.responses.TVShowsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MostPopularTVShowsRepository {

    private APIService apiService;

    public MostPopularTVShowsRepository() {
        apiService = APIClient.getRetrofit().create(APIService.class);
    }

    public LiveData<TVShowsResponse> getMostPopularTvShows(int page) {

        MutableLiveData<TVShowsResponse> data = new MutableLiveData<>();
        apiService.getMostPopularTVShows(page).enqueue(new Callback<TVShowsResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsResponse> call,@NonNull Response<TVShowsResponse> response) {
                data.setValue(response.body());

            }

            @Override
            public void onFailure(@NonNull Call<TVShowsResponse> call,@NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
