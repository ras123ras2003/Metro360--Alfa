/**
 * ARViewActivity.java
 * Example SDK Internal
 * 
 * Created by Arsalan Malik on 03.11.2011
 * Copyright 2011 metaio GmbH. All rights reserved.

 */

package com.rashith.metro.metaio;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;



import com.metaio.tools.io.AssetsManager;
import com.metaio.unifeye.UnifeyeDebug;
import com.metaio.unifeye.UnifeyeGLSurfaceView;
import com.metaio.unifeye.UnifeyeSensorsManager;
import com.metaio.unifeye.ndk.AS_UnifeyeSDKMobile;
import com.metaio.unifeye.ndk.ERENDER_SYSTEM;
import com.metaio.unifeye.ndk.IUnifeyeMobileAndroid;
import com.metaio.unifeye.ndk.IUnifeyeMobileCallback;
import com.metaio.unifeye.ndk.IUnifeyeMobileGeometry;
import com.metaio.unifeye.ndk.PoseVector;
import com.metaio.unifeye.ndk.Vector2di;

/**
 * This is base activity to use Unifeye SDK Mobile. It creates UnifeyeGLSurface
 * view and handle all its callbacks and lifecycle.
 * 
 * 
 * @author arsalan.malik
 * 
 */
public abstract class ARViewActivity extends Activity implements
		UnifeyeGLSurfaceView.Callback, OnTouchListener {

	static {
		IUnifeyeMobileAndroid.loadNativeLibs();
	}

	/**
	 * Sensor manager
	 */
	protected UnifeyeSensorsManager mSensorsManager;

	/**
	 * Unifeye OpenGL View
	 */
	protected UnifeyeGLSurfaceView mUnifeyeSurfaceView;

	/**
	 * The detected coordinate system id of the previous frame. This is used in
	 * onDrawFrame to check if a target has been just detected.
	 */
	protected int mDetectedCosID = -1;

	/**
	 * GUI overlay, only valid in onStart and if a resource is provided in
	 * getGUILayout.
	 */
	protected View mGUIView;

	/**
	 * UnifeyeSDK object
	 */
	protected IUnifeyeMobileAndroid mMobileSDK;

	/**
	 * flag for the renderer
	 */
	private boolean rendererInitialized = false;

	/**
	 * Wake lock to avoid screen time outs.
	 * <p>
	 * The application must request WAKE_LOCK permission.
	 */
	protected PowerManager.WakeLock mWakeLock;

	/**
	 * UnifeyeMobile callback handler
	 */
	private IUnifeyeMobileCallback mHandler;

	/**
	 * Provide resource for GUI overlay if required.
	 * <p>
	 * The resource is inflated into mGUIView which is added in onStart
	 * 
	 * @return Resource ID of the GUI view
	 */
	protected int getGUILayout() {
		return 0;
	};

	/**
	 * Provide Unifeye callback handler if desired.
	 * 
	 * @see IUnifeyeMobileCallback
	 * 
	 * @return Return unifeye callback handler
	 */
	protected IUnifeyeMobileCallback getMobileSDKCallbackHandler() {
		return null;
	};

	/**
	 * Load contents to unifeye in this method, e.g. tracking data, geometries
	 * etc.
	 */
	protected abstract void loadUnifeyeContents();

	/**
	 * Called when a geometry is touched.
	 * 
	 * @param geometry
	 *            Geometry that is touched
	 */
	protected void onGeometryTouched(IUnifeyeMobileGeometry geometry) {
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UnifeyeDebug.log("ARViewActivity.onCreate()");
		mMobileSDK = null;
		mUnifeyeSurfaceView = null;

		mHandler = null;

		try {
			// create the sensor manager
			if (mSensorsManager == null) {
				mSensorsManager = new UnifeyeSensorsManager(
						getApplicationContext());
			}

			// create the MobileSDK
			Log.d("NOS", "creating UnifeyeMobile in onCreate()");
			createMobileSDK();

			// Inflate GUI view if provided
			mGUIView = View.inflate(this, getGUILayout(), null);
		} catch (Exception e) {

		}

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				getPackageName());

	}

	@Override
	protected void onStart() {
		super.onStart();
		UnifeyeDebug.log("ARViewActivity.onStart(): "
				+ Thread.currentThread().getId());

		try {
			mUnifeyeSurfaceView = null;

			// start/resume the camera
			if (mMobileSDK != null) {

				// create a empty layout, required for the camera on some
				// devices
				setContentView(new FrameLayout(this));

				Vector2di cameraResolution = mMobileSDK
						.startCamera(0, 800, 480);

				// Create views (UnifeyeGL and GUI)

				// Add Unifeye GL Surface view
				mUnifeyeSurfaceView = new UnifeyeGLSurfaceView(this);
				mUnifeyeSurfaceView.registerCallback(this);
				mUnifeyeSurfaceView.setKeepScreenOn(true);
				mUnifeyeSurfaceView.setOnTouchListener(this);

				// Get layout params that stretches surface view to entire
				// screen while keeping aspect ratio.
				// Pass false, to fit entire surface view in the center of the
				// parent
				FrameLayout.LayoutParams params = mUnifeyeSurfaceView
						.getLayoutParams(cameraResolution, true);

				UnifeyeDebug.log("UnifeyeSurfaceView layout: " + params.width
						+ ", " + params.height);

				addContentView(mUnifeyeSurfaceView, params);

				mUnifeyeSurfaceView.setZOrderMediaOverlay(true);

			}

			// If GUI view is inflated, add it
			if (mGUIView != null) {
				addContentView(mGUIView, new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				mGUIView.bringToFront();
			}

		} catch (Exception e) {
			UnifeyeDebug.log(e.getMessage());
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		UnifeyeDebug.log("ARViewActivity.onPause()");

		if (mWakeLock != null)
			mWakeLock.release();

		// pause the Unifeye surface
		if (mUnifeyeSurfaceView != null)
			mUnifeyeSurfaceView.onPause();

		if (mSensorsManager != null) {
			mSensorsManager.registerCallback(null);
			mSensorsManager = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		UnifeyeDebug.log("ARViewActivity.onResume()");

		if (mWakeLock != null)
			mWakeLock.acquire();

		// make sure to resume the Unifeye surface
		if (mUnifeyeSurfaceView != null)
			mUnifeyeSurfaceView.onResume();

		// Open all sensors
		if (mSensorsManager == null) {
			mSensorsManager = new UnifeyeSensorsManager(getApplicationContext());
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		UnifeyeDebug.log("ARViewActivity.onStop()");

		if (mMobileSDK != null) {
			// Disable camera
			mMobileSDK.stopCamera();
		}

		System.runFinalization();
		System.gc();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		UnifeyeDebug.log("ARViewActivity.onDestroy()");

		if (mMobileSDK != null) {
			mMobileSDK.delete();
			mMobileSDK = null;
		}

		if (mSensorsManager != null) {
			mSensorsManager.registerCallback(null);
			mSensorsManager = null;
		}

		

		System.runFinalization();
		System.gc();

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_UP) {
			UnifeyeDebug.log("ARViewActivity touched at: " + event.toString());

			try {
				final int x = (int) event.getX();
				final int y = (int) event.getY();

				// ask the SDK if a geometry has been hit
				IUnifeyeMobileGeometry geometry = mMobileSDK
						.getGeometryFromScreenCoordinates(x, y, true);
				if (geometry != null) {
					onGeometryTouched(geometry);
				}
			} catch (Exception e) {

			}

		}

		// don't ask why we always need to return true contrary to what
		// documentation says
		return true;
	}

	/**
	 * This function will be called, right after the OpenGL context was created.
	 * All calls to UnifeyeMobile must be done after this callback has been
	 * triggered.
	 */
	@Override
	public void onSurfaceCreated() {
		try {
			UnifeyeDebug.log("onSurfaceCreated: "
					+ Thread.currentThread().getId());

			// initialize the renderer
			if (!rendererInitialized) {

				mMobileSDK.initializeRenderer(mUnifeyeSurfaceView.getWidth(),
						mUnifeyeSurfaceView.getHeight(),
						ERENDER_SYSTEM.ERENDER_SYSTEM_OPENGL_ES_2_0);
				loadUnifeyeContents();
				rendererInitialized = true;
			} else
				mMobileSDK.reloadTextures();

			// connect the audio callbacks
			mMobileSDK.registerAudioCallback(mUnifeyeSurfaceView
					.getUnifeyeAudioRenderer());
			mHandler = getMobileSDKCallbackHandler();
			if (mHandler != null)
				mMobileSDK.registerCallback(mHandler);

		} catch (Exception e) {

		}
	}

	/**
	 * Create MobileSDK instance
	 */
	private void createMobileSDK() {
		try {
			UnifeyeDebug.log("Creating the metaio mobile SDK");

			// Make sure to provide a valid application signature
			mMobileSDK = AS_UnifeyeSDKMobile.CreateUnifeyeMobileAndroid(this,
					"Ig1hNdxRO52rNP9Qn1NPz13pGkIoXjHxgclAV26cEFI=");
			mMobileSDK.registerSensorsComponent(mSensorsManager);

		} catch (Exception e) {
			UnifeyeDebug.log(Log.ERROR,
					"Error creating unifeye mobile: " + e.getMessage());
		}

	}

	protected boolean loadTrackingData(String trackingDataFileName) {

		String filepathTracking = AssetsManager
				.getAssetPath(trackingDataFileName);
		boolean result = mMobileSDK.setTrackingData(filepathTracking);
		UnifeyeDebug.log(Log.ASSERT, "Tracking data loaded: " + result);
		return result;
	}

	/**
	 * Loads a geometry from the assets. Requires a running MobileSDK.
	 * 
	 * @param modelFileName
	 *            The name of the model as in the assets folder
	 * @return A geometry object on success, null on failure.
	 * @throws FileNotFoundException
	 */
	protected IUnifeyeMobileGeometry loadGeometry(String modelFileName)
			throws FileNotFoundException {
		IUnifeyeMobileGeometry loadedGeometry = null;
		String filepath = AssetsManager.getAssetPath(modelFileName);
		if (filepath != null) {
			loadedGeometry = mMobileSDK.loadGeometry(filepath);
			if (loadedGeometry == null) {
				throw new RuntimeException(
						"Could not load the model file named " + modelFileName);
			}
		} else {
			throw new FileNotFoundException("Could not find the model named '"
					+ modelFileName + "' at: " + filepath);
		}
		return loadedGeometry;
	}

	@Override
	public void onDrawFrame() {
		try {
			// render the the results
			mMobileSDK.render();

			//
			PoseVector poses = mMobileSDK.getValidTrackingValues();
			if (poses.size() > 0) {
				// log the detected COS
				int cosID = poses.get(0).getCosID();
				if (cosID != mDetectedCosID) {
					UnifeyeDebug.log("detected " + cosID);
					mDetectedCosID = cosID;
				}
			} else {
				// reset the detected COS if nothing has been detected
				mDetectedCosID = -1;
			}

		} catch (Exception e) {

		}

	}

	@Override
	public void onSurfaceDestroyed() {
		mUnifeyeSurfaceView = null;
		UnifeyeDebug.log("onSurfaceDestroyed");
	}

	@Override
	public void onSurfaceChanged() {
		UnifeyeDebug.log("onSurfaceChanged");
	}

	@Override
	public void onScreenshot(Bitmap bitmap) {
	}

}
