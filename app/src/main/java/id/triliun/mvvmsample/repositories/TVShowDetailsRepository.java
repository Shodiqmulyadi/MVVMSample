package id.triliun.mvvmsample.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import id.triliun.mvvmsample.network.APIClient;
import id.triliun.mvvmsample.network.APIService;
import id.triliun.mvvmsample.responses.TVShowsDetailsResponse;
import id.triliun.mvvmsample.responses.TVShowsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TVShowDetailsRepository {

    private APIService apiService;

    public TVShowDetailsRepository(){
        apiService = APIClient.getRetrofit().create(APIService.class);
    }

    public LiveData<TVShowsDetailsResponse> getTVShowDetails(String tvShowId) {
        MutableLiveData<TVShowsDetailsResponse> data = new MutableLiveData<>();
        apiService.getTVShowDetails(tvShowId).enqueue(new Callback<TVShowsDetailsResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsDetailsResponse> call, @NonNull Response<TVShowsDetailsResponse> response) {
                data.setValue(response.body());

            }

            @Override
            public void onFailure(@NonNull Call<TVShowsDetailsResponse> call,@NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;

    }
}
