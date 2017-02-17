package com.db.williamchartdemo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getSupportFragmentManager().beginTransaction().add(R.id.container, new ChartsFragment()).commit();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.menu, menu);
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
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
							  "market://details?id=" + this.getApplicationContext().getPackageName())));
				} catch (ActivityNotFoundException e) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
							  "http://play.google.com/store/apps/details?id=" +
										 "https://play.google.com/store/apps/details?id=com.db.williamchartdemo")));
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}
