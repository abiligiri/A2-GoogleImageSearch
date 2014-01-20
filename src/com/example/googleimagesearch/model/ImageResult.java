package com.example.googleimagesearch.model;


public class ImageResult {
	private String title;
	private String thumbnailURL;
	private String imageURL;
	
	public ImageResult(String title, String thumbnailURL, String imageURL) {
		this.title = title;
		this.thumbnailURL = thumbnailURL;
		this.imageURL = imageURL;
	}

	public String getTitle() {
		return title;
	}

	public String getThumbnailURL() {
		return thumbnailURL;
	}

	public String getImageURL() {
		return imageURL;
	}
	
	public String toString() {
		return title + "<" + imageURL + ">";
	}

}
