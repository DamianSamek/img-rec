package com.wsiz.imagerecognition;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class FormDTO {

	private float threshold;
	
	private List<MultipartFile> files;
	
	public FormDTO() {}

	public FormDTO(float accuracy, List<MultipartFile> files) {
		super();
		this.threshold = accuracy;
		this.files = files;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public List<MultipartFile> getFiles() {
		return files;
	}

	public void setFiles(List<MultipartFile> files) {
		this.files = files;
	}
	
	
}
