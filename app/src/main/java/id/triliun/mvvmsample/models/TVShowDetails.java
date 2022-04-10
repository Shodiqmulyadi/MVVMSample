package id.triliun.mvvmsample.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShowDetails {

    @SerializedName("url")
    private String url;

    @SerializedName("description")
    private String description;

    @SerializedName("rountime")
    private String rountime;

    @SerializedName("image_path")
    private String imagePath;

    @SerializedName("rating")
    private String rating;

    @SerializedName("genres")
    private String[] genres;

    @SerializedName("pictures")
    private String[] pictures;

    @SerializedName("episodes")
    private List<Episode> episodes;

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getRountime() {
        return rountime;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getRating() {
        return rating;
    }

    public String[] getGenres() {
        return genres;
    }

    public String[] getPictures() {
        return pictures;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }
}
