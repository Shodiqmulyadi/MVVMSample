package id.triliun.mvvmsample.network;

import id.triliun.mvvmsample.responses.TVShowsDetailsResponse;
import id.triliun.mvvmsample.responses.TVShowsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {

    @GET("most-popular")
    Call<TVShowsResponse> getMostPopularTVShows(@Query("page")int page);

    @GET("show-details")
    Call<TVShowsDetailsResponse> getTVShowDetails(@Query("q")String tvShowId);
}
