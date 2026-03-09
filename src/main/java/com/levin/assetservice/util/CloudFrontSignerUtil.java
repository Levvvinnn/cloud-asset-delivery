package com.levin.assetservice.util;

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.Security;
import java.time.Instant;
import java.util.Date;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class CloudFrontSignerUtil {

    // distributionDomain e.g. d4kivs27yfm73.cloudfront.net
    // keyPairId (for Key Group / public key id) — see console where public key was created
    public static String generateSignedUrl(String distributionDomain, String objectKey, String keyPairId, Path privateKeyPath, long ttlSeconds) throws Exception {
        String resourceUrl = "https://" + distributionDomain + "/" + objectKey;
        Date expiration = Date.from(Instant.now().plusSeconds(ttlSeconds));

        // Read private key from PEM
        String privateKeyPem = Files.readString(privateKeyPath);
        PrivateKey privateKey = loadPrivateKey(privateKeyPem);

        // Use canned policy (simple expiry)
        String signedUrl = CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                resourceUrl,
                keyPairId,
                privateKey,
                expiration
        );

        return signedUrl;
    }

    private static PrivateKey loadPrivateKey(String pem) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        PEMParser pemParser = new PEMParser(new StringReader(pem));
        Object object = pemParser.readObject();
        pemParser.close();

        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) object;
        return converter.getPrivateKey(privateKeyInfo);
    }
}