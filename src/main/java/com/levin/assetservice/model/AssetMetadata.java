package com.levin.assetservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetMetadata {
    private String key;
    private String originalFileName;
    private String contentType;
    private long size;
    private Instant uploadedAt;
    private String cdnUrl;
}
