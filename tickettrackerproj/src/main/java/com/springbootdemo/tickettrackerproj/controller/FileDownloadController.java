package com.springbootdemo.tickettrackerproj.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class FileDownloadController {

    @GetMapping("/download/sample")
    public ResponseEntity<InputStreamResource> downloadSampleCSV() throws IOException {
        ClassPathResource csvFile = new ClassPathResource("static/sample.csv");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sample.csv")
                .contentType(MediaType.parseMediaType("application/csv"))
                .contentLength(csvFile.contentLength())
                .body(new InputStreamResource(csvFile.getInputStream()));
    }
}
