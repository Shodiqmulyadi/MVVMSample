package id.triliun.mvvmsample.responses;

import com.google.gson.annotations.SerializedName;

import id.triliun.mvvmsample.models.TVShowDetails;

public class TVShowsDetailsResponse {

    @SerializedName("tvShow")
    private TVShowDetails tvShowDetails;

    public TVShowDetails getTvShowDetails(){
        return tvShowDetails;
    }
}
