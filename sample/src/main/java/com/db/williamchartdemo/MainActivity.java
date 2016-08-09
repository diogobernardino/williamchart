package com.db.williamchartdemo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    private android.support.v4.app.Fragment mCurrFragment;

    private int currSpinnerSelection = 0;
    private ArrayAdapter<CharSequence> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_options, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mCurrFragment = new ChartsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, mCurrFragment)
                .commit();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);

        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        spinner.setAdapter(mAdapter);
        spinner.setSelection(currSpinnerSelection);
        spinner.setOnItemSelectedListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    public void onMenuClick(View view){
        ((SandboxFragment) mCurrFragment).onMenuClick(view);
    }

    public void onPlay(View view){
        ((SandboxFragment) mCurrFragment).onPlay(view);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(position != currSpinnerSelection ) {
            switch (position) {
                case 0:
                    mCurrFragment = new ChartsFragment();
                    break;
                case 1:
                    mCurrFragment = new SandboxFragment();
                    break;
                default:
                    break;
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mCurrFragment).commit();
            currSpinnerSelection  = position;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

}
