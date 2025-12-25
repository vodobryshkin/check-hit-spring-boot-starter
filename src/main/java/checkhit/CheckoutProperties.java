package checkhit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "checkout")
public class CheckoutProperties {

    private boolean enabled = true;

    private String configPath = "classpath:areas.json";

    private MinIO minio = new MinIO();

    @Getter
    @Setter
    public static class MinIO {
        private boolean mode = false;
        private String endpoint = "http://localhost:9000";
        private String accessKey = "MINIO_ACCESS_KEY";
        private String secretKey = "MINIO_SECRET_KEY";
        private String bucketName = "work-bucket";
        private String objectName = "areas.json";
    }

}
