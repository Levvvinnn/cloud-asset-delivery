package com.levin.assetservice.controller;

import java.io.IOException;

import com.levin.assetservice.model.AssetMetadata;
import com.levin.assetservice.service.S3AssetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class AssetController {
    private final S3AssetService s3AssetService;

    public AssetController(S3AssetService s3AssetService) {
        this.s3AssetService = s3AssetService;
    }

    @PostMapping("/upload")
    public ResponseEntity<AssetMetadata> uploadAsset(@RequestParam("file") MultipartFile file) throws IOException {
        AssetMetadata metadata = s3AssetService.upload(file);
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(metadata);
    }


}
