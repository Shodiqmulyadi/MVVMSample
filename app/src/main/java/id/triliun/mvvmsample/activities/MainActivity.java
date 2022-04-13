package id.triliun.mvvmsample.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.triliun.mvvmsample.R;
import id.triliun.mvvmsample.adapters.TVShowsAdapter;
import id.triliun.mvvmsample.databinding.ActivityMainBinding;
import id.triliun.mvvmsample.listeners.TVShowListener;
import id.triliun.mvvmsample.models.TVShow;
import id.triliun.mvvmsample.viewmodels.MostPopularTVShowsViewModel;

public class MainActivity extends AppCompatActivity implements TVShowListener {

    private MostPopularTVShowsViewModel viewModel;
    private ActivityMainBinding activityMainBinding;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private int currentPage = 1;
    private int totalAvailablePages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        doInitialization();

    }

    private void doInitialization(){
        activityMainBinding.TvShowRecycleView.setHasFixedSize(true);
        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);
        tvShowsAdapter = new TVShowsAdapter(tvShows, this);
        activityMainBinding.TvShowRecycleView.setAdapter(tvShowsAdapter);
        activityMainBinding.TvShowRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!activityMainBinding.TvShowRecycleView.canScrollVertically(1)){
                    if (currentPage <= totalAvailablePages){
                        currentPage += 1;
                        getMostPopularTVShows();
                    }
                }
            }
        });
        activityMainBinding.imageWatchList.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), WatchlistActivity.class)));
        getMostPopularTVShows();

    }

    private void getMostPopularTVShows() {
        toogleLoading();
        viewModel.getMostPopularTVShows(currentPage).observe(this, mostPopularTVShowResponse ->{
            toogleLoading();
            if (mostPopularTVShowResponse != null){
                totalAvailablePages = mostPopularTVShowResponse.getTotalpages();
                if (mostPopularTVShowResponse.getTvShows() != null){
                    int oldCount = tvShows.size();
                    tvShows.addAll(mostPopularTVShowResponse.getTvShows());
                    tvShowsAdapter.notifyItemRangeInserted(oldCount, tvShows.size());

                }
            }
        });
    }

    private void toogleLoading(){
        if (currentPage == 1){
            if (activityMainBinding.getIsLoading() != null && activityMainBinding.getIsLoading()){
                activityMainBinding.setIsLoading(false);
            }else {
                activityMainBinding.setIsLoading(true);
            }
        } else {
            if (activityMainBinding.getIsLoadingMore() != null && activityMainBinding.getIsLoadingMore()){
                activityMainBinding.setIsLoadingMore(false);
            } else {
                activityMainBinding.setIsLoadingMore(true);
            }
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TVShowDetailActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);

    }
}
