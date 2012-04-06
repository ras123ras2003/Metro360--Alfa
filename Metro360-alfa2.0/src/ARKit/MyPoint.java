package ARKit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.util.Log;

public class MyPoint extends ARSphericalView {

	public String _name = null;
	private Bitmap bitmap = null;

	public MyPoint(Context ctx, Location deviceLocation,
			Location objectLocation, String name) {
		super(ctx, deviceLocation, objectLocation);
		_name = name;
		try {
			int x = getResources().getIdentifier(name, "drawable",
					ctx.getPackageName());
			bitmap = BitmapFactory.decodeResource(getResources(), x);
			Log.d("MyAR",
					_name + "-d-" + String.valueOf(distance) + "-- A--"
							+ String.valueOf(azimuth) + "--I--"
							+ String.valueOf(inclination));
		} catch (Exception e) {

		}
	}

	@Override
	public void draw(Canvas c) {
		super.draw(c);
		if (bitmap != null) {
			c.drawBitmap(bitmap, getLeft(), getTop(), null);
			Paint p = new Paint();
			p.setColor(Color.RED);
			p.setTextSize(24);
			c.drawText("Azu- " + String.valueOf(azimuth), getLeft(),
					getTop() + bitmap.getHeight() + 20, p);

		}
	}

}
