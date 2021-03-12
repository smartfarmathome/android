package com.smf.smf.ui.welcome;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.smf.smf.R;
import com.smf.smf.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private ViewPager2 viewPager;
    private Button btnSkip, btnNext;
    private PrefManager prefManager;
    private ArrayList<WelcomeViewData> welcomeViewDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchLoginScreen();
            finish();
        }

        // Making notification bar transparent
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tabDots);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                Log.i(TAG, "onConfigureTab: called position => " + position);
            }
        });

        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);

        welcomeViewDataList = new ArrayList<>();
        welcomeViewDataList.add(new WelcomeViewData(R.drawable.ic_food, getString(R.string.slide_1_title), getString(R.string.slide_1_desc)));
        welcomeViewDataList.add(new WelcomeViewData(R.drawable.ic_movie, getString(R.string.slide_2_title), getString(R.string.slide_2_desc)));
        welcomeViewDataList.add(new WelcomeViewData(R.drawable.ic_discount, getString(R.string.slide_3_title), getString(R.string.slide_3_desc)));
        welcomeViewDataList.add(new WelcomeViewData(R.drawable.ic_travel, getString(R.string.slide_4_title), getString(R.string.slide_4_desc)));

        // making notification bar transparent
        changeStatusBarColor();

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(this, welcomeViewDataList);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // changing the next button text 'NEXT' / 'GOT IT'
                if (position == welcomeViewDataList.size() - 1) {
                    // last page. make button text to GOT IT
                    btnNext.setText(getString(R.string.start));
                    btnSkip.setVisibility(View.GONE);
                } else {
                    // still pages are left
                    btnNext.setText(getString(R.string.next));
                    btnSkip.setVisibility(View.VISIBLE);
                }
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLoginScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < welcomeViewDataList.size()) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchLoginScreen();
                }
            }
        });

        tabLayoutMediator.attach();
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchLoginScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(this, LoginActivity.class));
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends RecyclerView.Adapter<WelcomeViewHolder> {
        private final List<WelcomeViewData> welcomeViewDataList;
        private final Context mContext;

        public MyViewPagerAdapter(Context context, List<WelcomeViewData> welcomeViewDataList) {
            this.mContext = context;
            this.welcomeViewDataList = welcomeViewDataList;
        }

        @NonNull
        @Override
        public WelcomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.welcome_slide, parent, false);

            return new WelcomeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WelcomeViewHolder holder, int position) {
            WelcomeViewData welcomeViewData = welcomeViewDataList.get(position);
            int drawableResourceID = welcomeViewData.getDrawableResourceID();
            String title = welcomeViewData.getTitle();
            String content = welcomeViewData.getContent();

            holder.welcomeImage.setImageDrawable(ContextCompat.getDrawable(mContext, drawableResourceID));
            holder.welcomeTitle.setText(title);
            holder.welcomeContent.setText(content);
        }

        @Override
        public int getItemCount() {
            return welcomeViewDataList.size();
        }
    }

    static public class WelcomeViewData {
        private final int drawableResourceID;
        private final String title;
        private final String content;

        public int getDrawableResourceID() {
            return drawableResourceID;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public WelcomeViewData(int drawableResourceID, String title, String content) {
            this.drawableResourceID = drawableResourceID;
            this.title = title;
            this.content = content;
        }
    }

    static private class WelcomeViewHolder extends RecyclerView.ViewHolder {
        ImageView welcomeImage;
        TextView welcomeTitle;
        TextView welcomeContent;

        public WelcomeViewHolder(@NonNull View itemView) {
            super(itemView);
            welcomeImage = itemView.findViewById(R.id.welcome_image);
            welcomeTitle = itemView.findViewById(R.id.welcome_title);
            welcomeContent = itemView.findViewById(R.id.welcome_content);
        }
    }
}
