package com.levin.assetservice.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;

import com.levin.assetservice.model.AssetMetadata;
import com.levin.assetservice.util.CloudFrontSignerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3AssetService {
    private static final Logger logger = LoggerFactory.getLogger(S3AssetService.class);

    private final S3Client s3;
    private final String bucket;
    private final String cloudfrontDomain;
    private final String keyPairId;
    private final String privateKeyPath;

    public S3AssetService(S3Client s3,
                          @Value("${aws.s3.bucket}") String bucket,
                          @Value("${cloudfront.domain}") String cloudfrontDomain,
                          @Value("${cloudfront.key-pair-id}") String keyPairId,
                          @Value("${cloudfront.private-key-path}") String privateKeyPath) {
        this.s3 = s3;
        this.bucket = bucket;
        this.cloudfrontDomain = cloudfrontDomain;
        this.keyPairId = keyPairId;
        this.privateKeyPath = privateKeyPath;
    }

    public AssetMetadata upload(MultipartFile file)throws IOException {
        String original=file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String key = uuid + "-" + original.replaceAll("\\s+", "_");

        String cacheControl="public, max-age=31536000,immutable";

        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = java.net.URLConnection.guessContentTypeFromName(original);
        }
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }


        PutObjectRequest putReq=PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .cacheControl(cacheControl)
                .build();

        s3.putObject(putReq, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        try {
            String signedUrl = CloudFrontSignerUtil.generateSignedUrl(cloudfrontDomain, key, keyPairId, Paths.get(privateKeyPath), 3600);
            return new AssetMetadata(key, original, file.getContentType(), file.getSize(), Instant.now(), signedUrl);
        } catch (Exception e) {
            logger.error("Failed to generate signed URL for key: {}", key, e);
            throw new RuntimeException("Failed to generate signed URL", e);
        }
    }
}
