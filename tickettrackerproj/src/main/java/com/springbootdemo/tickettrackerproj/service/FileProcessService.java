package com.springbootdemo.tickettrackerproj.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileProcessService {

	void processCSVFile(MultipartFile file) throws IOException;

}