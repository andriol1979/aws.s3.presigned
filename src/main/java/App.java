import java.io.IOException;
import java.net.URISyntaxException;

public class App {
	public static void main(String[] args) throws IOException, URISyntaxException {
		for (String arg : args) {
			System.out.println("Args:" + arg);
		}
		String presignedUrl = PresignedUrlGenerator.generatePresignedLink(args[0]);
		System.out.println("presignedUrl: " + presignedUrl);
	}
}
