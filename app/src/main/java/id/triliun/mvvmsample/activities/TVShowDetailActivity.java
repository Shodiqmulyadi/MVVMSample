package id.triliun.mvvmsample.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import id.triliun.mvvmsample.R;
import id.triliun.mvvmsample.adapters.ImageSliderAdapter;
import id.triliun.mvvmsample.databinding.ActivityTvshowDetailBinding;
import id.triliun.mvvmsample.viewmodels.TVShowDetailsViewModel;

public class TVShowDetailActivity extends AppCompatActivity {

    private ActivityTvshowDetailBinding activityTvshowDetailBinding;
    private TVShowDetailsViewModel tvShowDetailsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTvshowDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_tvshow_detail);
        doInitialization();
    }

    private void doInitialization(){
        tvShowDetailsViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        activityTvshowDetailBinding.imageBack.setOnClickListener(v -> onBackPressed());
        getTVShowDetails();
    }

    private void getTVShowDetails(){
        activityTvshowDetailBinding.setIsLoading(true);
        String tvShowId = String.valueOf(getIntent().getIntExtra("id", -1));
        tvShowDetailsViewModel.getTVShowDetails(tvShowId).observe(this, tvShowsDetailsResponse -> {
            activityTvshowDetailBinding.setIsLoading(false);
            if (tvShowsDetailsResponse.getTvShowDetails() != null){
                if (tvShowsDetailsResponse.getTvShowDetails().getPictures() != null){
                    loadImageSlider(tvShowsDetailsResponse.getTvShowDetails().getPictures());
                }
                activityTvshowDetailBinding.setTvShowImageURL(
                        tvShowsDetailsResponse.getTvShowDetails().getImagePath()
                );
                activityTvshowDetailBinding.ImageTvShow.setVisibility(View.VISIBLE);
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

    private void setupSliderIndicators(int count){
        ImageView[] indicator = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        for (int i = 0; i < indicator.length; i++){
            indicator[i] = new ImageView(getApplicationContext());
            indicator[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.background_slider_indicator_inactive));
            indicator[i].setLayoutParams(layoutParams);
            activityTvshowDetailBinding.layoutSliderIndicator.addView(indicator[i]);
        }
        activityTvshowDetailBinding.layoutSliderIndicator.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);
    }

    private void setCurrentSliderIndicator(int position){
        int childCount = activityTvshowDetailBinding.layoutSliderIndicator.getChildCount();
        for (int i = 0; i < childCount; i++ ){
            ImageView imageView = (ImageView) activityTvshowDetailBinding.layoutSliderIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_active)
                );
            }else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(),R.drawable.background_slider_indicator_inactive)
                );
            }
        }
    }
}