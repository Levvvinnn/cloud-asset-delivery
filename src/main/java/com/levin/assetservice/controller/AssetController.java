package com.levin.assetservice.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.levin.assetservice.model.AssetMetadata;

@RestController
@RequestMapping("/api")
public class AssetController {
    private final s3AssetService s3AssetService;

    public AssetController(s3AssetService s3AssetService) {
        this.s3AssetService = s3AssetService;
    }

    @PostMapping("/upload")
    public ResponseEntity<AssetMetadata> uploadAsset(@RequestParam("file") MultipartFile file) throws IOException {
        AssetMetadata metadata = s3AssetService.uploadAsset(file);
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(metadata);
    }


}
