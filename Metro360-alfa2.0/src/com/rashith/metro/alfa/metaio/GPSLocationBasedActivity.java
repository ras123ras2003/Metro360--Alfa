/**
 * ARViewActivityLocationBased.java
 * Example SDK Internal
 *
 * Created by Arsalan Malik on 08.03.2011
 * Copyright 2011 metaio GmbH. All rights reserved.
 *
 */

package com.rashith.metro.alfa.metaio;

import android.util.Log;

import com.metaio.tools.io.AssetsManager;
import com.metaio.unifeye.UnifeyeDebug;
import com.metaio.unifeye.UnifeyeSensorsManager;
import com.metaio.unifeye.ndk.IUnifeyeBillboardGroup;
import com.metaio.unifeye.ndk.IUnifeyeMobileGeometry;
import com.metaio.unifeye.ndk.LLACoordinate;
import com.metaio.unifeye.ndk.Vector3d;

/**
 * EXAMPLE 3
 * 
 * This example activity shows how location based contents can be displayed in AR view.
 * 
 *  Please visit the following link for a detailed explanation. 
 * {@link http://docs.metaio.com/bin/view/Main/GPSLocationBasedExample}
 * 
 * @author arsalan.malik, tim.oppermann
 * 
 */
public class GPSLocationBasedActivity extends ARViewActivity implements UnifeyeSensorsManager.Callback 
{
	/**
	 * The billboard group, collecting all billboards
	 */
	private IUnifeyeBillboardGroup mBillboardGroup;

	/**
	 * all our geometries
	 */
	private IUnifeyeMobileGeometry billboardNorth;

	private IUnifeyeMobileGeometry geometryEast;

	/**
	 *  We need special tracking data to use the GPS and the compass here. 
	 */
	private	final String mTrackingDataFileName = "TrackingData_GPSCompass.xml";
	
	/**
	 * Offset from current location
	 */
	private static final double OFFSET = 0.00001;

	
	@Override
	protected void onResume() 
	{
		super.onResume();

		// Register callback to receive sensor updates
		if (mSensorsManager != null)
			mSensorsManager.registerCallback(this);
	}


	@Override
	public void onAccelerometerSensorChanged(Vector3d vector) 
	{
		
	}

	@Override
	public void onLocationSensorChanged(LLACoordinate location) 
	{
		if (billboardNorth != null)
		{
			location.setLatitude(location.getLatitude()+OFFSET);
			UnifeyeDebug.log("billboardNorth.setMoveTranslationLLA: "+location);
			billboardNorth.setMoveTranslationLLA(location);
			location.setLatitude(location.getLatitude()-OFFSET);
		}
		
		if (geometryEast != null)
		{
			location.setLongitude(location.getLongitude()+OFFSET);
			UnifeyeDebug.log("geometryEast.setMoveTranslationLLA: "+location);
			geometryEast.setMoveTranslationLLA(location);
			location.setLongitude(location.getLongitude()-OFFSET);
		}
		
	}
	
	@Override
	public void onOrientationSensorChanged(Vector3d orientation) 
	{
		
	} 


	@Override
	protected void loadUnifeyeContents() 
	{
	
		try
		{ 
			mBillboardGroup = mMobileSDK.createBillboardGroup(580f, 800f);
		
		
			//Load Tracking data
			loadTrackingData(mTrackingDataFileName);  
			
			LLACoordinate location = mSensorsManager.getLocation();
		
			
			String filepath = AssetsManager.getAssetPath("billboard.png");
			if (filepath != null) 
			{
				// North
				billboardNorth = mMobileSDK.loadImageBillboard(filepath);
				if (billboardNorth != null)
				{
					
					location.setLatitude(location.getLatitude()+OFFSET);
					UnifeyeDebug.log("billboardNorth.setMoveTranslationLLA: "+location);
					billboardNorth.setMoveTranslationLLA(location);
					
					boolean added = mBillboardGroup.addBillboard(billboardNorth);
					UnifeyeDebug.log("Billboard added to group: "+added);  
					
					// revert to original location for next geometry
					location.setLatitude(location.getLatitude()-OFFSET); 
					
				}
			}
			

			// East
			geometryEast = loadGeometry("metaioman.md2");
			if (geometryEast != null)
			{
				location.setLongitude(location.getLongitude()+OFFSET);
				
				UnifeyeDebug.log("geometryEast.setMoveTranslationLLA: "+location);
				geometryEast.setMoveTranslationLLA(location);
			
				geometryEast.setMoveScale(new Vector3d(5f,5f, 5f));
				geometryEast.setMoveTranslation( new Vector3d(0,0, -200), true);
				// revert to original location for next geometry
				location.setLongitude(location.getLongitude()-OFFSET);
			}
		
		
		
			location.delete();
			location = null;
			
		}
		
		catch (Exception e)
		{
			UnifeyeDebug.printStackTrace(Log.ERROR, e);
		}
		
	}


}
