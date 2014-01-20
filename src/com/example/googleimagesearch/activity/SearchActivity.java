package com.example.googleimagesearch.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.example.googleimagesearch.R;
import com.example.googleimagesearch.model.ImageResult;
import com.example.googleimagesearch.model.ImageSearch;
import com.example.googleimagesearch.model.ImageSearch.Query;

public class SearchActivity extends Activity {
	private static final String TAG = "SearchActivity";
	private ArrayList<ImageResult> results;
	private ImageResultsAdapter imageResultsAdapter;
	private ImageSearch imageSearch;
	private Query searchQuery;
	private static final int REQUEST_OPTIONS = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		results = new ArrayList<ImageResult>();
		imageResultsAdapter = new ImageResultsAdapter(this, results);
		imageSearch = new ImageSearch(this);
		searchQuery = new Query();
		GridView imageGrid = (GridView)findViewById(R.id.gridView1);
		imageGrid.setOnScrollListener(new EndlessScrollListener() {
			
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				searchQuery.setStart(String.valueOf(page - 1));
				search();
			}
		});
		imageGrid.setAdapter(imageResultsAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}
	
	private void search() {
		imageSearch.search(searchQuery, new ImageSearch.Listener() {				
			@Override
			public void onResults(ArrayList<ImageResult> res) {					
				results.addAll(res);
				imageResultsAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void onError(String message) {
				Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public void onButtonSearch(View v) {
		EditText etQuery = (EditText)findViewById(R.id.etQuery);
		String query = etQuery.getText().toString();
		
		if (query != null && query.length() > 0) {
			results.clear();
			imageResultsAdapter.notifyDataSetChanged();
			searchQuery.setText(query);
			searchQuery.setStart("0");
			search();
		} else {
			Toast.makeText(this, "Search query cannot be empty", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void onSearchOptionsAction(MenuItem mi) {
		Intent i = new Intent(this, OptionsActivity.class);
		i.putExtra(OptionsActivity.ARG_QUERY, searchQuery);
		startActivityForResult(i, REQUEST_OPTIONS);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_OPTIONS) {
			searchQuery = (Query)data.getSerializableExtra(OptionsActivity.ARG_QUERY);
			return;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public class ImageResultsAdapter extends ArrayAdapter<ImageResult> {
		
		public ImageResultsAdapter(Context context, ArrayList<ImageResult> images) {
			super(context, R.layout.item_image, images);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflator = LayoutInflater.from(getContext());
				convertView = inflator.inflate(R.layout.item_image, parent, false);
			}
			
			NetworkImageView imageView = (NetworkImageView)convertView;
			imageView.setImageUrl(getItem(position).getThumbnailURL(), imageSearch.getImageLoader());
			
			return convertView;
		}

	}
	
	public abstract class EndlessScrollListener implements OnScrollListener {
		// The minimum amount of items to have below your current scroll position
		// before loading more.
		private int visibleThreshold = 5;
		// The current offset index of data you have loaded
		private int currentPage = 0;
		// The total number of items in the dataset after the last load
		private int previousTotalItemCount = 0;
		// True if we are still waiting for the last set of data to load.
		private boolean loading = true;
		// Sets the starting page index
		private int startingPageIndex = 0;

		public EndlessScrollListener() {
		}

		public EndlessScrollListener(int visibleThreshold) {
			this.visibleThreshold = visibleThreshold;
		}

		public EndlessScrollListener(int visibleThreshold, int startPage) {
			this.visibleThreshold = visibleThreshold;
			this.startingPageIndex = startPage;
			this.currentPage = startPage;
		}

		// This happens many times a second during a scroll, so be wary of the code you place here.
		// We are given a few useful parameters to help us work out if we need to load some more data,
		// but first we check if we are waiting for the previous load to finish.
		@Override
		public void onScroll(AbsListView view,int firstVisibleItem,int visibleItemCount,int totalItemCount) 
	        {
			// If the total item count is zero and the previous isn't, assume the
			// list is invalidated and should be reset back to initial state
			// If there are no items in the list, assume that initial items are loading
			if (!loading && (totalItemCount < previousTotalItemCount)) {
				this.currentPage = this.startingPageIndex;
				this.previousTotalItemCount = totalItemCount;
				if (totalItemCount == 0) { this.loading = true; } 
			}

			// If it’s still loading, we check to see if the dataset count has
			// changed, if so we conclude it has finished loading and update the current page
			// number and total item count.
			if (loading) {
				if (totalItemCount > previousTotalItemCount) {
					loading = false;
					previousTotalItemCount = totalItemCount;
					currentPage++;
				}
			}
			
			// If it isn’t currently loading, we check to see if we have breached
			// the visibleThreshold and need to reload more data.
			// If we do need to reload some more data, we execute onLoadMore to fetch the data.
			if (!loading && (totalItemCount - visibleItemCount)<=(firstVisibleItem + visibleThreshold)) 
	                {
			    onLoadMore(currentPage + 1, totalItemCount);
			    loading = true;
			}
		}

		// Defines the process for actually loading more data based on page
		public abstract void onLoadMore(int page, int totalItemsCount);

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// Don't take any action on changed
		}
	}

}
