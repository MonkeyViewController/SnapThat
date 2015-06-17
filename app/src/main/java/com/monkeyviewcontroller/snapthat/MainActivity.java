package com.monkeyviewcontroller.snapthat;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.support.design.widget.NavigationView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.monkeyviewcontroller.snapthat.Adapters.CurrentGameListAdapter;
import com.monkeyviewcontroller.snapthat.Models.Game;
import com.parse.FunctionCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_action_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        TextView tvNavUsername = (TextView)navigationView.findViewById(R.id.nav_username);
        tvNavUsername.setText(ParseUser.getCurrentUser().getUsername());

        //setFragment(TabbedFragment.newInstance(0)); //set the Friends viewpager as the initial view
        setFragment(new PastGamesFragment());
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        setSelectedFragment(menuItem.getItemId());
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    public void setSelectedFragment(int position) {
        Fragment fragment = null;

        switch (position) {
            case R.id.nav_friends:
                fragment = TabbedFragment.newInstance(0);
                break;
            case R.id.nav_snap:
                fragment = new SnapsFragment();
                break;
            case R.id.nav_games:
                fragment = TabbedFragment.newInstance(1);
                break;
            case R.id.nav_activity:
                fragment = new BasicFragment();
                break;
            case R.id.nav_settings:
                fragment = new BasicFragment();
                break;
            case R.id.nav_logout:
                logout();
                break;
            default:
                fragment = TabbedFragment.newInstance(0);
        }

        if (fragment != null) {
            setFragment(fragment);
        }
    }

    public void logout()
    {
        new MaterialDialog.Builder(this)
                .content("Log Out?")
                .positiveText("Yes")
                .negativeText("No")
                .positiveColorRes(R.color.ics_blue)
                .negativeColorRes(R.color.ics_blue)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Log.d("MVC", "Yes agree from dialog.");

                        ParseUser.logOut();
                        Intent intent = new Intent(MainActivity.this, SignupOrLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                })
                .show();
    }
}