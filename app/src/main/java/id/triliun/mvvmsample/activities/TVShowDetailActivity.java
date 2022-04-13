package id.triliun.mvvmsample.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;

import id.triliun.mvvmsample.R;
import id.triliun.mvvmsample.adapters.EpisodesAdapter;
import id.triliun.mvvmsample.adapters.ImageSliderAdapter;
import id.triliun.mvvmsample.databinding.ActivityTvshowDetailBinding;
import id.triliun.mvvmsample.databinding.LayoutEpisodesBottomSheetBinding;
import id.triliun.mvvmsample.models.TVShow;
import id.triliun.mvvmsample.utilities.TempDataHolder;
import id.triliun.mvvmsample.viewmodels.TVShowDetailsViewModel;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowDetailActivity extends AppCompatActivity {

    private ActivityTvshowDetailBinding activityTvshowDetailBinding;
    private TVShowDetailsViewModel tvShowDetailsViewModel;
    private BottomSheetDialog episodesBottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    private TVShow tvShow;
    private boolean isTVShowAvailableInWatchlist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTvshowDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_tvshow_detail);
        doInitialization();
    }

    private void doInitialization() {
        tvShowDetailsViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        activityTvshowDetailBinding.imageBack.setOnClickListener(v -> onBackPressed());
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        checkTVShowInWatchlist();
        getTVShowDetails();
    }

    private void checkTVShowInWatchlist(){
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(tvShowDetailsViewModel.getTVShowFromWatchlist(String.valueOf(tvShow.getId()))
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(tvShow -> {
            isTVShowAvailableInWatchlist = true;
            activityTvshowDetailBinding.imageWatchList.setImageResource(R.drawable.ic_added);
            compositeDisposable.dispose();
        }));
    }

    private void getTVShowDetails() {
        activityTvshowDetailBinding.setIsLoading(true);
        String tvShowId = String.valueOf(tvShow.getId());
        tvShowDetailsViewModel.getTVShowDetails(tvShowId).observe(this, tvShowsDetailsResponse -> {
            activityTvshowDetailBinding.setIsLoading(false);
            if (tvShowsDetailsResponse.getTvShowDetails() != null) {
                if (tvShowsDetailsResponse.getTvShowDetails().getPictures() != null) {
                    loadImageSlider(tvShowsDetailsResponse.getTvShowDetails().getPictures());
                }
                activityTvshowDetailBinding.setTvShowImageURL(
                        tvShowsDetailsResponse.getTvShowDetails().getImagePath()
                );
                activityTvshowDetailBinding.ImageTvShow.setVisibility(View.VISIBLE);
                activityTvshowDetailBinding.setDescription(String.valueOf(
                        HtmlCompat.fromHtml(tvShowsDetailsResponse.getTvShowDetails().getDescription(),
                                HtmlCompat.FROM_HTML_MODE_LEGACY)
                ));
                activityTvshowDetailBinding.textDescription.setVisibility(View.VISIBLE);
                activityTvshowDetailBinding.textReadMore.setVisibility(View.VISIBLE);
                activityTvshowDetailBinding.textReadMore.setOnClickListener(v -> {
                    if (activityTvshowDetailBinding.textReadMore.getText().toString().equals("Read More")) {
                        activityTvshowDetailBinding.textDescription.setMaxLines(Integer.MAX_VALUE);
                        activityTvshowDetailBinding.textDescription.setEllipsize(null);
                        activityTvshowDetailBinding.textReadMore.setText(R.string.read_less);
                    } else {
                        activityTvshowDetailBinding.textDescription.setMaxLines(4);
                        activityTvshowDetailBinding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                        activityTvshowDetailBinding.textReadMore.setText(R.string.read_more);
                    }
                });

                activityTvshowDetailBinding.setRating(String.format(Locale.getDefault(), "%.2f",
                        Double.parseDouble(tvShowsDetailsResponse.getTvShowDetails().getRating())));
                if (tvShowsDetailsResponse.getTvShowDetails().getGenres() != null) {
                    activityTvshowDetailBinding.setGenre(tvShowsDetailsResponse.getTvShowDetails().getGenres()[0]);
                } else {
                    activityTvshowDetailBinding.setGenre("N/A");
                }
                activityTvshowDetailBinding.setRuntime(tvShowsDetailsResponse.getTvShowDetails().getRuntime() + "Min");
                activityTvshowDetailBinding.viewDivider1.setVisibility(View.VISIBLE);
                activityTvshowDetailBinding.layoutMisc.setVisibility(View.VISIBLE);
                activityTvshowDetailBinding.viewDivider2.setVisibility(View.VISIBLE);
                activityTvshowDetailBinding.buttonWebsite.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(tvShowsDetailsResponse.getTvShowDetails().getUrl()));
                    startActivity(intent);
                });
                activityTvshowDetailBinding.buttonWebsite.setVisibility(View.VISIBLE);
                activityTvshowDetailBinding.buttonEpisodes.setVisibility(View.VISIBLE);
                activityTvshowDetailBinding.buttonEpisodes.setOnClickListener(v -> {
                    if (episodesBottomSheetDialog == null) {
                        episodesBottomSheetDialog = new BottomSheetDialog(TVShowDetailActivity.this);
                        layoutEpisodesBottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(
                                TVShowDetailActivity.this),
                                R.layout.layout_episodes_bottom_sheet,
                                findViewById(R.id.episodesContainer), false);
                        episodesBottomSheetDialog.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
                        layoutEpisodesBottomSheetBinding.episodesRecycleView.setAdapter(
                                new EpisodesAdapter(tvShowsDetailsResponse.getTvShowDetails().getEpisodes())
                        );
                        layoutEpisodesBottomSheetBinding.textTitle.setText(String.format("Episodes | %s",
                                tvShow.getName()));
                        layoutEpisodesBottomSheetBinding.imageClose.setOnClickListener(v1 -> episodesBottomSheetDialog.dismiss());
                    }
                    // ---Optional Section Start---//
                    FrameLayout frameLayout = episodesBottomSheetDialog.findViewById(
                            com.google.android.material.R.id.design_bottom_sheet);
                    if (frameLayout != null) {
                        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                        bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    //---Optional Section End---//
                    episodesBottomSheetDialog.show();
                });
                activityTvshowDetailBinding.imageWatchList.setOnClickListener(v -> {
                    CompositeDisposable compositeDisposable = new CompositeDisposable();
                    if (isTVShowAvailableInWatchlist){
                        compositeDisposable.add(tvShowDetailsViewModel.removeTVShowFromWatchlist(tvShow)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            isTVShowAvailableInWatchlist = false;
                            TempDataHolder.IS_WATCHLIST_UPDATED = true;
                            activityTvshowDetailBinding.imageWatchList.setImageResource(R.drawable.ic_watchlist);
                            Toast.makeText(getApplicationContext(), "Removed From Watchlist", Toast.LENGTH_SHORT).show();
                            compositeDisposable.dispose();
                        }));
                    } else {
                        compositeDisposable.add(tvShowDetailsViewModel.addToWatchlist(tvShow)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    TempDataHolder.IS_WATCHLIST_UPDATED = true;
                                    activityTvshowDetailBinding.imageWatchList.setImageResource(R.drawable.ic_added);
                                    Toast.makeText(getApplicationContext(), "Added To Watchlist", Toast.LENGTH_SHORT).show();
                                    compositeDisposable.dispose();
                                }));
                    }
                });
                activityTvshowDetailBinding.imageWatchList.setVisibility(View.VISIBLE);
                loadBasicTVShowDetails();
            }
        });
    }

    private void loadImageSlider(String[] sliderimages) {
        activityTvshowDetailBinding.sliderViewPager.setOffscreenPageLimit(1);
        activityTvshowDetailBinding.sliderViewPager.setAdapter(new ImageSliderAdapter((sliderimages)));
        activityTvshowDetailBinding.sliderViewPager.setVisibility(View.VISIBLE);
        activityTvshowDetailBinding.viewFadingEdge.setVisibility(View.VISIBLE);
        setupSliderIndicators(sliderimages.length);
        activityTvshowDetailBinding.sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSliderIndicator(position);
            }
        });
    }

    private void setupSliderIndicators(int count) {
        ImageView[] indicator = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicator.length; i++) {
            indicator[i] = new ImageView(getApplicationContext());
            indicator[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.background_slider_indicator_inactive));
            indicator[i].setLayoutParams(layoutParams);
            activityTvshowDetailBinding.layoutSliderIndicator.addView(indicator[i]);
        }
        activityTvshowDetailBinding.layoutSliderIndicator.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);
    }

    private void setCurrentSliderIndicator(int position) {
        int childCount = activityTvshowDetailBinding.layoutSliderIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) activityTvshowDetailBinding.layoutSliderIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_inactive)
                );
            }
        }
    }

    private void loadBasicTVShowDetails() {
        activityTvshowDetailBinding.setTvShowName(tvShow.getName());
        activityTvshowDetailBinding.setNetworkCountry(tvShow.getNetwork() + "("
                + tvShow.getCountry() + ")");
        activityTvshowDetailBinding.setStatus(tvShow.getStatus());
        activityTvshowDetailBinding.setStartedDate(tvShow.getStartDate());

        activityTvshowDetailBinding.textName.setVisibility(View.VISIBLE);
        activityTvshowDetailBinding.textNerworkCountry.setVisibility(View.VISIBLE);
        activityTvshowDetailBinding.textStatus.setVisibility(View.VISIBLE);
        activityTvshowDetailBinding.textStarted.setVisibility(View.VISIBLE);

    }
}