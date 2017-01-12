package com.db.williamchartdemo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends AppCompatActivity implements DrawerFragment.NavigationDrawerCallbacks {


    private DrawerFragment mDrawerFragment;
    private SandboxFragment mCurrFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mDrawerFragment = (DrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {

        switch (position){
            case 0:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new LineFragment())
                        .commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new BarFragment())
                        .commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new StackedFragment())
                        .commit();
                break;
            case 3:
                mCurrFragment = new SandboxFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, mCurrFragment)
                        .commit();
            default:
                break;
        }
    }



    private void restoreActionBar() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (!mDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.menu, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.github:
                startActivity(new Intent("android.intent.action.VIEW",
                        Uri.parse("https://github.com/diogobernardino/WilliamChart")));
                return true;
            case R.id.play:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id="
                                    + this.getApplicationContext().getPackageName())));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" +
                                    "https://play.google.com/store/apps/details?id=com.db.williamchartdemo")));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onMenuClick(View view){
        mCurrFragment.onMenuClick(view);
    }

    public void onPlay(View view){
        mCurrFragment.onPlay(view);
    }

}
