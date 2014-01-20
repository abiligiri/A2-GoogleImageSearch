package com.example.googleimagesearch.model;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class ImageSearch {
	private static final String TAG = "ImageSearch";
	
	private RequestQueue requestQueue;
	private ImageLoader imageLoader;
	
	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public ImageSearch(Context c) {
		requestQueue = Volley.newRequestQueue(c);
		imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
			private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(25);
			@Override
			public void putBitmap(String url, Bitmap bitmap) {
				cache.put(url, bitmap);
			}
			
			@Override
			public Bitmap getBitmap(String url) {
				return cache.get(url);
			}
		});
	}

	public void search(Query query, final Listener listener) {
		StringBuilder sb = new StringBuilder("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&");
		
		for (String key : query.params.keySet()) {
			String value = query.params.get(key);
			if (value != null)
				try {
					sb.append(key).append("=").append(URLEncoder.encode(value, "utf-8")).append("&");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		Log.i(TAG, "Requesting for " + sb.toString());
		
		requestQueue.add(new JsonObjectRequest(sb.toString(), null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d(TAG, "Got json response " + response.toString());
						new ImageResultParser(new ImageResultParser.Listner() {
							@Override
							public void onResults(ArrayList<ImageResult> res) {
								Log.d(TAG, "got parsed results" + res.toString());
								listener.onResults(res);
							}
						}).parseResults(response);
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d(TAG, "Got json response " + error.toString());
						listener.onError(error.toString());
					}
				}));
	}
	
	
	public static interface Listener {
		public void onResults(ArrayList<ImageResult> res);
		public void onError(String message);
	}
	
	public static class Query implements Serializable {
		static final long serialVersionUID = 1234556789L;
		
		public final int ImageSizeAny = 0;
		public final int ImageSizeIcon = 1;
		public final int ImageSizeSmall = 2;
		public final int ImageSizeMedium = 3;
		public final int ImageSizeLarge = 4;
		public final int ImageSizeXLarge = 5; 
		public final int ImageSizeXXLarge = 6;
		public final int ImageSizeHuge = 7;
		
		private HashMap<String, String> params;
		public Query() {
			params = new HashMap<String, String>();
			params.put("start", "0");
			params.put("rsz", "8");
			setSize(ImageSizeSmall);
		}
		
		public String getText() {
			return params.get("q");
		}
		
		public void setText(String text) {
			params.put("q", text);
		}
		
		public int getSize() {
			String size = params.get("imgsz");
			
			if (size == null || size == "")
				return ImageSizeAny;
			
			if (size.equals("icon"))
				return ImageSizeIcon;
			else if (size.equals("small"))
				return ImageSizeSmall;
			else if (size.equals("medium"))
				return ImageSizeMedium;
			else if (size.equals("large"))
				return ImageSizeLarge;
			else if (size.equals("xlarge"))
				return ImageSizeXLarge;
			else if (size.equals("xxlarge"))
				return ImageSizeXXLarge;
			
			return ImageSizeHuge;
		}
		
		public void setSize(int size) {
			switch (size) {
			case ImageSizeHuge:
				params.put("imgsz", "huge");	
				break;
			case ImageSizeXXLarge:
				params.put("imgsz", "xxlarge");
				break;
			case ImageSizeXLarge:
				params.put("imgsz", "xlarge");
				break;
			case ImageSizeLarge:
				params.put("imgsz", "large");
				break;
			case ImageSizeMedium:
				params.put("imgsz", "medium");
				break;
			case ImageSizeSmall:
				params.put("imgsz", "small");
				break;
			case ImageSizeIcon:
				params.put("imgsz", "icon");
				break;
			default:
				params.remove("imgsz");
				break;
			}
		}
		
		public final int ColorFilterNone = 0;
		public final int ColorFilterBlack = 1;
		public final int ColorFilterBlue = 2;
		public final int ColorFilterBrown = 3;
		public final int ColorFilterGray = 4;
		public final int ColorFilterGreen = 5;
		public final int ColorFilterOrange = 6;
		public final int ColorFilterPink = 7;
		public final int ColorFilterPurple = 8;
		public final int ColorFilterRed = 9;
		public final int ColorFilterTeal = 10;
		public final int ColorFilterWhite = 11;
		public final int ColorFilterYellow = 12;
		
		public int getColorFilter() {
			String colorFilter = params.get("imgcolor");
			
			if (colorFilter == null)
				return ColorFilterNone;
			
			if (colorFilter.equals("black"))
				return ColorFilterBlack;
			else if (colorFilter.equals("blue"))
				return ColorFilterBlue;
			else if (colorFilter.equals("brown"))
				return ColorFilterBrown;
			else if (colorFilter.equals("gray"))
				return ColorFilterGray;
			else if (colorFilter.equals("green"))
				return ColorFilterGreen;
			else if (colorFilter.equals("orange"))
				return ColorFilterOrange;
			else if (colorFilter.equals("pink"))
				return ColorFilterPink;
			else if (colorFilter.equals("purple"))
				return ColorFilterPurple;
			else if (colorFilter.equals("red"))
				return ColorFilterRed;
			else if (colorFilter.equals("teal"))
				return ColorFilterTeal;
			else if (colorFilter.equals("white"))
				return ColorFilterWhite;
			else if (colorFilter.equals("yellow"))
				return ColorFilterYellow;
			
			return ColorFilterNone;
			
		}
		
		public void setColorFilter(int colorFilter) {
			switch (colorFilter) {
			case ColorFilterNone:
				params.remove("imgcolor");
				break;
			case ColorFilterBlack:
				params.put("imgcolor", "black");
				break;
			case ColorFilterBrown:
				params.put("imgcolor", "brown");
				break;
			case ColorFilterBlue:
				params.put("imgcolor", "blue");
				break;
			case ColorFilterGray:
				params.put("imgcolor", "gray");
				break;
			case ColorFilterGreen:
				params.put("imgcolor", "green");
				break;
			case ColorFilterOrange:
				params.put("imgcolor", "orange");
				break;
			case ColorFilterPink:
				params.put("imgcolor", "pink");
				break;
			case ColorFilterPurple:
				params.put("imgcolor", "purple");
				break;
			case ColorFilterTeal:
				params.put("imgcolor", "teal");
				break;
			case ColorFilterRed:
				params.put("imgcolor", "red");
				break;
			case ColorFilterWhite:
				params.put("imgcolor", "white");
				break;
			case ColorFilterYellow:
				params.put("imgcolor", "yellow");
				break;
			}
		}
		
	   public final int ImageTypeAny = 0;
	   public final int ImageTypeFace = 1;
	   public final int ImageTypeClipart = 2;
	   public final int ImageTypePhoto = 3; 
	   public final int ImageTypeLineArt = 4;
		
		public int getType() {
			String imageType = params.get("imgtype");
			
			if (imageType == null)
				return ImageTypeAny;
			
			if (imageType.equals("face"))
				return ImageTypeFace;
			
			if (imageType.equals("lineart"))
				return ImageTypeLineArt;
			
			if (imageType.equals("clipart"))
				return ImageTypeClipart;
			
			if (imageType.equals("photo"))
				return ImageTypePhoto;
			
			return ImageTypeAny;
		}
		
		public void setType(int type) {
			switch (type) {
			case ImageTypeFace:
				params.put("imgtype", "face");
				break;
			case ImageTypeLineArt:
				params.put("imgtype", "lineart");
				break;
			case ImageTypePhoto:
				params.put("imgtype", "photo");
				break;
			case ImageTypeClipart:
				params.put("imgtype", "clipart");
				break;
			case ImageTypeAny:
			default:
					params.remove("imgtype");
					break;
			}
		}
		
		public String getSite() {
			return params.get("site");
		}
		
		public void setSite(String site) {
			if (site == null || site.length() == 0)
				params.remove("site");
			else
				params.put("site", site);
		}
		
		public String getStart() {
			return params.get("start"); 
		}
		
		public void setStart(String start) {
			params.put("start", start);
		}
		
		public String getNumberOfResults() {
			return params.get("rsz");
		}
		
		public void setNumberOfResults(String numberOfResults) {
			params.put("rsz", numberOfResults);
		}
	}
}
