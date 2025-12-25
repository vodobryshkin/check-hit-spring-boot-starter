package checkhit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "checkout")
public class CheckoutProperties {

    private boolean enabled = true;

    private String configPath = "classpath:areas.json";

    private MinIO minio = new MinIO();

    public static class MinIO {
        private boolean mode = false;
        private String endpoint = "http://localhost:9000";
        private String accessKey = "MINIO_ACCESS_KEY";
        private String secretKey = "MINIO_SECRET_KEY";
        private String bucketName = "work-bucket";
        private String objectName = "areas.json";

        public boolean isMode() { return mode; }
        public void setMode(boolean mode) { this.mode = mode; }

        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

        public String getAccessKey() { return accessKey; }
        public void setAccessKey(String accessKey) { this.accessKey = accessKey; }

        public String getSecretKey() { return secretKey; }
        public void setSecretKey(String secretKey) { this.secretKey = secretKey; }

        public String getBucketName() { return bucketName; }
        public void setBucketName(String bucketName) { this.bucketName = bucketName; }

        public String getObjectName() { return objectName; }
        public void setObjectName(String objectName) { this.objectName = objectName; }
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getConfigPath() { return configPath; }
    public void setConfigPath(String configPath) { this.configPath = configPath; }

    public MinIO getMinio() { return minio; }
    public void setMinio(MinIO minio) { this.minio = minio; }
}
