package com.rashith.metro.alfa;

import com.rashith.metro.alfa.metaio.GPSTest;
import com.rashith.metro.alfa.webservices.WebCall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class HomeActivity extends Activity {

	// test for second

	private EditText ed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		final ImageButton ib = (ImageButton) findViewById(R.id.imageButton1);
		ib.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(HomeActivity.this,
						RashARActivity.class);
				startActivity(intent);
			}
		});

		final ImageButton metaio = (ImageButton) findViewById(R.id.imageButton2);
		metaio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(HomeActivity.this, GPSTest.class);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		ed = (EditText) findViewById(R.id.editText1);
		final ImageButton web = (ImageButton) findViewById(R.id.imageButton3);

		web.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new Thread() {

					public void run() {

						String x = WebCall.getPlaces(10);
						if (x != null) {
							Log.d("Metro360�", x);
							ed.setText(x);
						}

					}
				}.start();
			}
		});
	}

}
