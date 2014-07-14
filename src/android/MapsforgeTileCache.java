package com.Dolphin.GeoLocation;


import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.AbstractQueue;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mapsforge.core.graphics.TileBitmap;

import android.util.Log;

/**
 * This class implements a PhoneGap plugin to access the tile cache created by
 * MapsForge. Calls to this plugin should pass a function call of "getTile" with
 * an options array containing x, y and z values that correspond to the slippy
 * map convention:<br>
 * 
 * http://.../{z}/{x}/{y}.png<br>
 * 
 * where:<br>
 * 
 * z - Zoom level<br>
 * x - Calculated x coordinate<br>
 * y - Calculated y coordinate<br>
 * 
 * On success, if a tile is found within the cache this plugin returns a
 * JSONObject with the respective tile retrieved and base64 encoded in HTML
 * image format:<br>
 * 
 * {<br>
 * tile : "data:image/png;base64,{img data}"<br>
 * }<br>
 * 
 * If no tile is found, an empty object is returned (ie {}).
 * 
 * @see <a
 *      href="http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Implementations">slippy
 *      map implementation</a> for more information.
 * 
 * @author Zach Swain
 * 
 */
public class MapsforgeTileCache extends CordovaPlugin  {
	final String TAG = "MapsforgeTileCache";

//	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
//		Log.i(TAG, "Initilizing CordovaInterface");
//		
//
//		
//		Log.i(TAG, "Initilizing ended");
//}



/*
	 * TODO: Implement an in memory tile cache to reduce disk access time.
	 * (non-Javadoc)
	 * 
	 * @see org.apache.cordova.api.Plugin#execute(java.lang.String,
	 * org.json.JSONArray, java.lang.String)
	 */
@Override
public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
	Log.i(TAG, "execute started");


if ("getTile".equalsIgnoreCase(action)) {
try {
	final long x = Long.parseLong(args.getString(0));
	final long y = Long.parseLong(args.getString(1));
	final byte z = Byte.parseByte(args.getString(2));
	
	String bitmap = StaticTileCache.get(x, y, z);
	if (bitmap != null)
	{

		JSONObject result = new JSONObject();

		if (!bitmap.equals(null) || !bitmap.equals("")) {
		try {
			result.put("tile", bitmap);
		} catch (JSONException e) {
			
			Log.i(TAG, "Error putting tile in result");
		}
	}
	callbackContext.success(result);
	
}
else {
	callbackContext.error("rendered tile is null");
				}
			}
		
 catch (Exception e) {
	//Log.i(TAG, e.getMessage());
callbackContext.error("something happened");
        
	}
return true;
}
			else
			{
				return false;
			}
			
	}
}
