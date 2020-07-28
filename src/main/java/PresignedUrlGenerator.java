import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class PresignedUrlGenerator {
	private final static Regions clientRegion = Regions.US_EAST_1;
	private AmazonS3 s3Client;
	private int s3ConnectionTimeout;
	private static PresignedUrlGenerator instance = new PresignedUrlGenerator();

	private PresignedUrlGenerator(){
		s3ConnectionTimeout = 1000 * 60 * 60;
		s3Client = AmazonS3ClientBuilder.standard()
				.withRegion(clientRegion)
				.withCredentials(new DefaultAWSCredentialsProviderChain())
				.build();
	}

	public static PresignedUrlGenerator getInstance() {
		return instance;
	}

	public static String generatePresignedLink(String url) {
		try {
			URI uri = new URI(url);
			String host = uri.getHost();
			if(!host.toLowerCase().contains("amazonaws.com")) {
				return url;
			}
			String bucketName = host.substring(0, host.indexOf('.'));
			String objectKey = uri.getPath();

			return getInstance().generate(bucketName, formatObjectKey(objectKey));
		}
		catch (URISyntaxException ex) {
			ex.printStackTrace(System.out);
			return url;
		}
	}

	private static String formatObjectKey(String objectKey) {
		if(objectKey.substring(0, 1).contains("/")) {
			objectKey = objectKey.substring(1);
		}
		return objectKey;
	}

	private String generate(String bucketName, String objectKey) {
		try {
			java.util.Date expiration = new java.util.Date();
			long expTimeMillis = expiration.getTime();
			expTimeMillis += s3ConnectionTimeout;
			expiration.setTime(expTimeMillis);
			GeneratePresignedUrlRequest generatePresignedUrlRequest =
					new GeneratePresignedUrlRequest(bucketName, objectKey)
							.withMethod(HttpMethod.GET)
							.withExpiration(expiration);
			URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
			return url.toString();
		}
		catch (AmazonServiceException e) {
			e.printStackTrace(System.out);
		}
		catch (SdkClientException e) {
			e.printStackTrace(System.out);
		}
		return "";
	}
}
