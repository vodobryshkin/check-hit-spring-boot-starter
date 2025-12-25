package checkhit;

import checkhit.minio.MinioInit;
import checkhit.service.ConfigFileCheckoutHitService;
import checkhit.service.ICheckoutHitService;
import checkhit.service.MinioCheckoutHitService;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Configuration
@EnableConfigurationProperties(CheckoutProperties.class)
@ConditionalOnProperty(
        prefix = "checkout",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class CheckoutHitAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ICheckoutHitService checkoutHitService(CheckoutProperties properties) throws IOException {
        String path = properties.getConfigPath();

        CheckoutProperties.MinIO minIO = properties.getMinio();

        if (minIO != null && minIO.isMode()) {
            MinioClient client = MinioInit.build(minIO.getEndpoint(), minIO.getAccessKey(), minIO.getSecretKey());
            return new MinioCheckoutHitService(client, minIO.getBucketName(), minIO.getObjectName());
        }

        if (path.startsWith("classpath:")) {
            String resourcePath = path.substring("classpath:".length());
            if (resourcePath.startsWith("/")) resourcePath = resourcePath.substring(1);

            try (InputStream is = CheckoutHitAutoConfiguration.class.getClassLoader().getResourceAsStream(resourcePath)) {
                if (is == null) {
                    throw new IllegalStateException("Resource not found on classpath: " + resourcePath);
                }

                Path tmp = Files.createTempFile("checkout-config-", ".json");
                tmp.toFile().deleteOnExit();
                Files.copy(is, tmp, StandardCopyOption.REPLACE_EXISTING);

                return new ConfigFileCheckoutHitService(tmp);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to load config from classpath: " + resourcePath, e);
            }
        }

        try {
            return new ConfigFileCheckoutHitService(path);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to init ConfigFileCheckoutHitService with path: " + path, e);
        }
    }

}

