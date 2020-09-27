package com.example.ramhacks_invoicescan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int PHOTO_REQUEST = 28;

    private FloatingActionButton mFloatingButton;

    public FragmentList fragmentInvoicesList;
    public FragmentList fragmentDocumentsList;
    public FragmentList fragmentRandomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFloatingButton = findViewById(R.id.fab);

        // Set up viewpager
        ViewPager viewPager = (ViewPager) findViewById(R.id.home_viewpager);
        setupViewPager(viewPager);

        // Set up tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.home_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void goToActivityTakePicture(View v){
        mFloatingButton.setClickable(false);
        Intent intent = new Intent(this, ActivityTakePicture.class);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result){
        super.onActivityResult(requestCode, resultCode, result);

        if(requestCode == PHOTO_REQUEST){
            if(resultCode == Activity.RESULT_OK){

                InvoiceElement invoice = new InvoiceElement();
                invoice.mId = result.getLongExtra("imageId", -1);
                invoice.mPath = result.getStringExtra("imagePath");
                invoice.mType = result.getStringExtra("imageType");

                invoice.mStore = result.getStringExtra("imageStore");
                invoice.mCost = result.getStringExtra("imagePrice");
                invoice.mDate = result.getStringExtra("imageDate");
                invoice.mItems = result.getStringExtra("imageItems");

                fragmentInvoicesList.addNewItem(invoice);
                fragmentDocumentsList.addNewItem(invoice);
                fragmentRandomList.addNewItem(invoice);
            }
        }

        mFloatingButton.setClickable(true);
    }

    public void setupViewPager(ViewPager viewPager){
        // Create 3 views for viewpager
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragmentInvoicesList = FragmentList.newInstance(FragmentList.FRAGMENT_INVOICES);
        fragmentDocumentsList = FragmentList.newInstance(FragmentList.FRAGMENT_DOCUMENTS);
        fragmentRandomList = FragmentList.newInstance(FragmentList.FRAGMENT_RANDOM);
        adapter.addFragment(fragmentInvoicesList, getResources().getString(R.string.tab_invoices));
        adapter.addFragment(fragmentDocumentsList, getResources().getString(R.string.tab_documents));
        adapter.addFragment(fragmentRandomList, getResources().getString(R.string.tab_random));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}