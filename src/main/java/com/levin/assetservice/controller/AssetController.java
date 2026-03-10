package com.levin.assetservice.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.levin.assetservice.model.AssetMetadata;
import com.levin.assetservice.service.S3AssetService;
import com.levin.assetservice.util.CloudFrontSignerUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class AssetController {
    private final S3AssetService s3AssetService;

    @Value("${cloudfront.domain}")
    private String distributionDomain;

    @Value("${cloudfront.key-pair-id}")
    private String keyPairId;

    @Value("${cloudfront.private-key-path}")
    private String privateKeyPath;

    public AssetController(S3AssetService s3AssetService) {
        this.s3AssetService = s3AssetService;
    }

    @PostMapping("/upload")
    public ResponseEntity<AssetMetadata> uploadAsset(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        AssetMetadata metadata = s3AssetService.upload(file);
        return ResponseEntity.ok(metadata);
    }

    @GetMapping("/files/{key}/signed-url")
    public ResponseEntity<Map<String,String>> getSignedUrl(@PathVariable String key,
                                                        @RequestParam(defaultValue = "60") long ttlSeconds) throws Exception {
        Path privateKeyPathObj = Paths.get(privateKeyPath);
        String signed = CloudFrontSignerUtil.generateSignedUrl(distributionDomain, key, keyPairId, privateKeyPathObj, ttlSeconds);
        return ResponseEntity.ok(Map.of("signedUrl", signed));
    }

}
