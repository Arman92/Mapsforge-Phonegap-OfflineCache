package com.Dolphin.GeoLocation;


import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;










import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;

import org.mapsforge.core.graphics.TileBitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Tile;
import org.mapsforge.core.util.IOUtils;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.rendertheme.AssetsRenderTheme;
import org.mapsforge.map.layer.cache.FileSystemTileCache;
import org.mapsforge.map.layer.queue.JobQueue;
import org.mapsforge.map.layer.renderer.DatabaseRenderer;
import org.mapsforge.map.layer.renderer.RendererJob;
import org.mapsforge.map.model.DisplayModel;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.reader.MapDatabase;
import org.mapsforge.map.rendertheme.ExternalRenderTheme;
import org.mapsforge.map.rendertheme.XmlRenderTheme;


public class StaticTileCache extends Thread {
	static final String TAG = "StaticTileCache";
	public static short TILE_SIZE = 256;
	private static String mapFilePath = null;
	private static File mapFile = null;
	
	private static File mapCacheFile;
	private static FileSystemTileCache tileCache;
	private static AndroidGraphicFactory androidGraphicFactory;
	private static DisplayModel displayModel ;
	private static MapViewPosition mapViewPosition ;
	private static JobQueue<RendererJob> jobQueue;
	private static MapDatabase mapDatabase;
	private static DatabaseRenderer databaseRenderer;
	private static XmlRenderTheme xmlRenderTheme;
	private static RendererJob rendererJob ;
	private static Context context;
	
	public static boolean initialize(String locationOfMapFile, Context _context) {
		Log.i(TAG, "Initilizing started");
		
		mapFilePath = locationOfMapFile;
		
		androidGraphicFactory = AndroidGraphicFactory.INSTANCE;
		
		mapCacheFile = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/mapcache.ser");
		mapFile = new File(mapFilePath);
		
		tileCache = new FileSystemTileCache(2000, mapCacheFile, androidGraphicFactory);
		displayModel = new DisplayModel();
		mapViewPosition = new MapViewPosition(displayModel);
		jobQueue = new JobQueue<RendererJob>(mapViewPosition, displayModel);
		
		mapDatabase = new MapDatabase();
		mapDatabase.openFile(mapFile);
		
		displayModel.setFixedTileSize(256);
		
		
		
		databaseRenderer = new DatabaseRenderer(mapDatabase, androidGraphicFactory);
		context = _context;
		
		
		
		try {
			

//		xmlRenderTheme = new AssetsRenderTheme(context, "", "renderthemes/detailed.xml");
//		xmlRenderTheme = new MyXmlRenderTheme(context, "/detailed.xml", 1);
		xmlRenderTheme = new ExternalRenderTheme(new File(Environment.getExternalStorageDirectory().getPath()
				+ "/assets.xml"));
//		xmlRenderTheme = InternalRenderTheme.OSMARENDER;
		
		Log.i(TAG, "xmlRenderTheme initilized successfuly");
		}
		catch(Throwable e)
		{
			Log.i(TAG, "xmlRenderTheme error initilizing ");
			Log.i(TAG, "xmlRenderTheme;" + e.getMessage());
		}

		Log.i(TAG, "Initilizing Ended");
		

		return true;
	}

	
	public synchronized static String get(long x, long y, byte zoom) {
		String filePath = "";
		Tile tile = new Tile(x, y, zoom);
		rendererJob = new RendererJob(tile, mapFile, xmlRenderTheme,
				displayModel, 1f, true);
		
		jobQueue.add(rendererJob);
		
		try {
			Log.i(TAG, "start to get Tile");
			

		
			File cachedTile = new File(context.getFilesDir().getPath() + "/MapCache/" + 
					zoom + "/" + x + "/" + y + ".png");
			if (cachedTile.exists())//tileCache.containsKey(rendererJob))
			{
				jobQueue.get();
				
				Log.i(TAG, "cache has the key");
				return cachedTile.getAbsolutePath();
				//return tileCache.get(rendererJob);
			}
			else
			{
				Log.i(TAG, "cache doesn have the key, starting rendering");
				
				rendererJob = jobQueue.get();
				TileBitmap bitmap = databaseRenderer.executeJob(rendererJob);
				
				if (bitmap != null) {
		//			tileCache.put(rendererJob, bitmap);
		//			this.layer.requestRedraw();
//					
					//bitmap.decrementRefCount();
					
					OutputStream outputStream = null;
					
					try {
						new File(context.getFilesDir().getPath() + "/MapCache/" + 
								zoom + "/" + x + "/" ).mkdirs();
						
						File file = new File(context.getFilesDir().getPath() + "/MapCache/" + 
					zoom + "/" + x + "/" + y + ".png");
						
						outputStream = new FileOutputStream(file);
						bitmap.compress(outputStream);
						filePath = file.getAbsolutePath();
						
						}
					 catch (IOException e) {
						 Log.d(TAG, "Caching error");

					} finally {
						IOUtils.closeQuietly(outputStream);
					}
				}
	

//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				
//				try {
//					bitmap.compress(baos);
//				} catch (IOException e) {
//					
//					Log.i(TAG, "Error compressing tile");
//				}
				
//				byte[] buffer = baos.toByteArray();
//
//				String encodedTile = "data:image/png;base64,"
//						+ Base64.encodeToString(buffer, Base64.DEFAULT);

				
				return filePath;
			}
		}
		catch (Throwable e)
		{
			Log.i("TileCache", "TileCache get failed");
			Log.i("TileCache", e.getMessage());
			return null;
		}
		finally{
			jobQueue.remove(rendererJob);
		}
	}
}
	
	
