package com.rashith.metro.alfa;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import ARKit.ARLayout;
import ARKit.MyPoint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class RashARActivity extends Activity {

	
	
	public static volatile Context ctx;
	ARLayout ar;
	volatile Location curLocation = null;
	
	private Preview myCamView;
	private Camera myCam;

	private LocationManager manager;
	private LocationListener listner;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		
		ctx = this.getApplicationContext();
		ar = new ARLayout(getApplicationContext());

		
		myCamView  = new Preview(this);

		WindowManager w = getWindowManager();
		Display d = w.getDefaultDisplay();
		int width = d.getWidth();
		int height = d.getHeight();
		ar.screenHeight = height;
		ar.screenWidth = width;
		FrameLayout rl = new FrameLayout(getApplicationContext());
		rl.addView(myCamView);
		ar.debug = true;
		rl.addView(ar, width, height);
		
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout rLay = (RelativeLayout) inflater.inflate(R.layout.arview, null);
		
		ImageButton direction = (ImageButton) rLay.findViewById(R.id.imageButton1);
		
		direction.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(RashARActivity.this, "Direction Function is Not Implemented", Toast.LENGTH_LONG).show();
			}
		});
		
		ImageButton search = (ImageButton) rLay.findViewById(R.id.imageButton2);
		search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(RashARActivity.this, "Search Function is Not Implemented", Toast.LENGTH_LONG).show();
			}
		});
		
		rl.addView(rLay);
		setContentView(rl);

		Location location = new Location("Test");
		location.setLatitude(6.58);
		location.setLongitude(79.54);
		location.setAccuracy(0.1F);
		location.setAltitude(0);
		
		//SLIIT
		//location.setLatitude(6.914780);
		//location.setLongitude(79.973013);

		ar.curLocation = location;
		rePoplulate(location);
		Toast.makeText(this, "Plaese Wiat till GPS", Toast.LENGTH_LONG).show();

		manager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		listner = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub

				
				ar.GPSLocked = true;
				curLocation = location;
				ar.curLocation = location;
				rePoplulate(location);
				

			}

		};

		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1,
				listner);

	}

	public void rePoplulate(Location location) {
		if (ar != null) {
			ar.clearARViews();
			
			
			Location pLoc = new Location("pns");
			pLoc.setLatitude(6.971889);
			pLoc.setLongitude(79.909967);
			//pLoc.setAltitude(0);
			MyPoint pns = new MyPoint(ctx, location, pLoc, "pns");
			pns.distance = 20;
			ar.addARView(pns);
			
/*
			Location pLoc = new Location("pns");
			pLoc.setLatitude(6.91493);
			pLoc.setLongitude(79.97195);
			//pLoc.setAltitude(0);
			MyPoint pns = new MyPoint(ctx, location, pLoc, "pns");
			pns.distance = 20;
			ar.addARView(pns);

			Location pizza = new Location("Pizza");
			pizza.setLatitude(6.904045);
			pizza.setLongitude(79.955736);
			//pizza.setAltitude(0);
			MyPoint piz = new MyPoint(ctx, location, pizza, "pizza");
			piz.distance = 20;
			ar.addARView(piz);

			Location sapu = new Location("sapu");
			sapu.setLatitude(6.96284);
			sapu.setLongitude(79.95708);
			//sapu.setAltitude(0);
			MyPoint sapugas = new MyPoint(ctx, location, sapu, "sapu");
			sapugas.distance = 20;
			ar.addARView(sapugas);
			
			Location pizza2 = new Location("Pizza");
			pizza2.setLatitude(6.97488);
			pizza2.setLongitude(79.92282);
			
			//pizza2.setAltitude(0);
			MyPoint piz2 = new MyPoint(ctx, location, pizza2, "pizza");
			piz2.distance = 20;
			ar.addARView(piz2);
			
			
			//hard coded
			MyPoint ll = new MyPoint(ctx, null, null, "pns");
			ll.azimuth = 190.0f;
			ll.distance = 20;
			ll.inclination = -10;
			ar.addARView(ll);
			
			MyPoint l2 = new MyPoint(ctx, null, null, "pizza");
			l2.azimuth = 112.0f;
			l2.distance = 35;
			ar.addARView(l2);
			
			MyPoint sapugas2 = new MyPoint(ctx, location, sapu, "sapu");
			sapugas2.azimuth = 249;
			sapugas2.distance = 2000;
			ar.addARView(sapugas2);
			*/

			ar.postInvalidate();
		}
	}

	
	

	
	@Override
	protected void onPause() {
		super.onPause();
		manager.removeUpdates(listner);
		if(myCam != null){
			myCamView.setCamera(null);
			myCam.release();
			myCam = null;
		}
		
	}

	@Override
	protected void onDestroy() {
		if(myCam != null){
			myCamView.setCamera(null);
			myCam.release();
			myCam = null;
		}
		ar.close();
		super.onDestroy();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		myCam = Camera.open(0);
		myCamView.setCamera(myCam);
		ar.GPSLocked = false;
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1,
				listner);
		Toast.makeText(this, "Plaese Wiat till GPS", Toast.LENGTH_LONG).show();
	}

	// Googles Camara Code

	class Preview extends ViewGroup implements SurfaceHolder.Callback {
		private final String TAG = "Preview";

		SurfaceView mSurfaceView;
		SurfaceHolder mHolder;
		Size mPreviewSize;
		List<Size> mSupportedPreviewSizes;
		Camera mCamera;

		Preview(Context context) {
			super(context);

			mSurfaceView = new SurfaceView(context);
			addView(mSurfaceView);

			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = mSurfaceView.getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		public void setCamera(Camera camera) {
			mCamera = camera;
			if (mCamera != null) {
				mSupportedPreviewSizes = mCamera.getParameters()
						.getSupportedPreviewSizes();
				requestLayout();
			}
		}

		public void switchCamera(Camera camera) {
			setCamera(camera);
			try {
				camera.setPreviewDisplay(mHolder);
			} catch (IOException exception) {
				Log.e(TAG, "IOException caused by setPreviewDisplay()",
						exception);
			}
			Camera.Parameters parameters = camera.getParameters();
			parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
			requestLayout();

			camera.setParameters(parameters);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			// We purposely disregard child measurements because act as a
			// wrapper to a SurfaceView that centers the camera preview instead
			// of stretching it.
			final int width = resolveSize(getSuggestedMinimumWidth(),
					widthMeasureSpec);
			final int height = resolveSize(getSuggestedMinimumHeight(),
					heightMeasureSpec);
			setMeasuredDimension(width, height);

			if (mSupportedPreviewSizes != null) {
				mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes,
						width, height);
			}
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			if (changed && getChildCount() > 0) {
				final View child = getChildAt(0);

				final int width = r - l;
				final int height = b - t;

				int previewWidth = width;
				int previewHeight = height;
				if (mPreviewSize != null) {
					previewWidth = mPreviewSize.width;
					previewHeight = mPreviewSize.height;
				}

				// Center the child SurfaceView within the parent.
				if (width * previewHeight > height * previewWidth) {
					final int scaledChildWidth = previewWidth * height
							/ previewHeight;
					child.layout((width - scaledChildWidth) / 2, 0,
							(width + scaledChildWidth) / 2, height);
				} else {
					final int scaledChildHeight = previewHeight * width
							/ previewWidth;
					child.layout(0, (height - scaledChildHeight) / 2, width,
							(height + scaledChildHeight) / 2);
				}
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// The Surface has been created, acquire the camera and tell it
			// where
			// to draw.
			try {
				if (mCamera != null) {
					mCamera.setPreviewDisplay(holder);
				}
			} catch (IOException exception) {
				Log.e(TAG, "IOException caused by setPreviewDisplay()",
						exception);
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// Surface will be destroyed when we return, so stop the preview.
			if (mCamera != null) {
				mCamera.stopPreview();
			}
		}

		private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
			final double ASPECT_TOLERANCE = 0.1;
			double targetRatio = (double) w / h;
			if (sizes == null)
				return null;

			Size optimalSize = null;
			double minDiff = Double.MAX_VALUE;

			int targetHeight = h;

			// Try to find an size match aspect ratio and size
			for (Size size : sizes) {
				double ratio = (double) size.width / size.height;
				if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
					continue;
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}

			// Cannot find the one match the aspect ratio, ignore the
			// requirement
			if (optimalSize == null) {
				minDiff = Double.MAX_VALUE;
				for (Size size : sizes) {
					if (Math.abs(size.height - targetHeight) < minDiff) {
						optimalSize = size;
						minDiff = Math.abs(size.height - targetHeight);
					}
				}
			}
			return optimalSize;
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			// Now that the size is known, set up the camera parameters and
			// begin
			// the preview.
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
			requestLayout();

			mCamera.setParameters(parameters);
			mCamera.startPreview();
		}

	}
}