package com.example.googleimagesearch.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.example.googleimagesearch.R;
import com.example.googleimagesearch.model.ImageSearch.Query;

public class OptionsActivity extends Activity implements OnItemSelectedListener {
	public static final String ARG_QUERY = "query";
	
	private Spinner spinnerSize, spinnerImageType, spinnerColorFilter;
	private Query query;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		
		query = (Query)getIntent().getSerializableExtra(ARG_QUERY);
		
		spinnerSize = (Spinner)findViewById(R.id.spinnerImageSize);
		spinnerSize.setOnItemSelectedListener(this);
		spinnerImageType = (Spinner)findViewById(R.id.spinnerImageType);
		spinnerImageType.setOnItemSelectedListener(this);
		spinnerColorFilter = (Spinner)findViewById(R.id.spinnerColorFilter);
		spinnerColorFilter.setOnItemSelectedListener(this);
		
		spinnerSize.setSelection(query.getSize());
		spinnerImageType.setSelection(query.getType());
		spinnerColorFilter.setSelection(query.getColorFilter());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent();
		i.putExtra(ARG_QUERY, query);
		setResult(RESULT_OK, i);
		super.onBackPressed();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long rowID) {
		switch (parent.getId()) {
		case R.id.spinnerColorFilter:
			query.setColorFilter(position);
			break;
		case R.id.spinnerImageSize:
			query.setSize(position);
			break;
		case R.id.spinnerImageType:
			query.setType(position);
			break;
		}
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}
