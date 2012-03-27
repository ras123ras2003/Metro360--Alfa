package com.rashith.metro.alfa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class HomeActivity extends Activity {

	// test for second
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
	}
}
