package com.levin.assetservice.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.levin.assetservice.model.AssetMetadata;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3AssetService {
    private final S3Client s3;
    private final String bucket;
    private final String cloudfrontDomain;

    public S3AssetService(S3Client s3,@Value("${aws.s3.bucket}") String bucket,@Value("${aws.s3.bucket}") String cloudfrontDomain) {
        this.s3 = s3;
        this.bucket = bucket;
        this.cloudfrontDomain = cloudfrontDomain;
    }

    public AssetMetadata upload(MultipartFile file)throws IOException {
        String original=file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String key = uuid + "-" + original.replaceAll("\\s+", "_");

        String cacheControl="public, max-age=31536000,immutable";

        PutObjectRequest putReq=PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .cacheControl(cacheControl)
                .build();

        s3.putObject(putReq, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        

    }
}
