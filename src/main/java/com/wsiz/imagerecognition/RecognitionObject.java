package com.wsiz.imagerecognition;

import org.springframework.web.multipart.MultipartFile;

public class RecognitionObject {

	private String object;
	private MultipartFile file;
	private float confidence;
	
	public RecognitionObject(String object, MultipartFile file, float confidence) {
		super();
		this.object = object;
		this.file = file;
		this.confidence = confidence;
	}
	
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	public float getConfidence() {
		return confidence;
	}
	public void setConfidence(float confidence) {
		this.confidence = confidence;
	}
	
	
}
