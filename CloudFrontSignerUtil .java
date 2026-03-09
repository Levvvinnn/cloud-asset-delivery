import com.amazonaws.HttpMethod;
import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;

import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.Security;
import java.time.Instant;
import java.util.Date;

public class CloudFrontSignerUtil {

    // distributionDomain e.g. d4kivs27yfm73.cloudfront.net
    // keyPairId (for Key Group / public key id) — see console where public key was created
    public static String generateSignedUrl(String distributionDomain, String objectKey, String keyPairId, Path privateKeyPath, long ttlSeconds) throws Exception {
        String resourceUrl = "https://" + distributionDomain + "/" + objectKey;
        Date expiration = Date.from(Instant.now().plusSeconds(ttlSeconds));

        // Read private key bytes (PEM)
        String privateKeyPem = Files.readString(privateKeyPath);
        PrivateKey privateKey = CloudFrontUrlSigner.getPrivateKeyFromPEM(privateKeyPem);

        // Use canned policy (simple expiry)
        URL signedUrl = CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                new URL(resourceUrl),
                keyPairId,
                privateKey,
                expiration
        );

        return signedUrl.toString();
    }
}