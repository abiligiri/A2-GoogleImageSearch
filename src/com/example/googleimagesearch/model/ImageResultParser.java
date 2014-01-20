package com.example.googleimagesearch.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class ImageResultParser {
	private static final String TAG = "ImageResultParser";
	
	private Listner listener;
	
	public ImageResultParser(Listner listener) {
		this.listener = listener;
	}

	public void parseResults(JSONObject json) {
		Log.d(TAG, "Begin parsing results");
		new ParseTask().execute(json);
	}
	
	private class ParseTask extends AsyncTask<JSONObject, Void, ArrayList<ImageResult>> {
		@Override
		protected ArrayList<ImageResult> doInBackground(JSONObject... params) {
			Log.d(TAG, "In Parse doInBackGround");
			ArrayList<ImageResult> res = new ArrayList<ImageResult>();
			for (JSONObject param : params) {
				try {
					JSONArray results = param.getJSONObject("responseData").getJSONArray("results");
					for (int i = 0; i < results.length(); i++) {
						JSONObject imageResult = results.getJSONObject(i);
						res.add(new ImageResult(imageResult.getString("title"),
								imageResult.getString("tbUrl"),
								imageResult.getString("url")));
					}
				} catch(JSONException e) {
					
				}
			}
			return res; 
		}
		
		@Override
		protected void onPostExecute(ArrayList<ImageResult> result) {
			Log.d(TAG, "In Parse onPostExecute");
			super.onPostExecute(result);
			ImageResultParser.this.listener.onResults(result);
		}
	}
	
	public static interface Listner {
		public void onResults(ArrayList<ImageResult> res);
	}
}
