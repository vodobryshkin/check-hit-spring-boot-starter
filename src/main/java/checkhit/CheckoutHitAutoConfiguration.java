package checkhit;

import checkhit.minio.MinioInit;
import checkhit.service.ACheckoutHitService;
import checkhit.service.ConfigFileCheckoutHitService;
import checkhit.service.MinioCheckoutHitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ifmo.se.gmt.checker.CheckoutManager;

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
    @ConditionalOnMissingBean(ACheckoutHitService.class)
    public ACheckoutHitService checkoutHitService(CheckoutProperties properties) {
        ObjectMapper objectMapper = new ObjectMapper();

        String path = properties.getConfigPath();
        CheckoutProperties.MinIO minIO = properties.getMinio();

        if (minIO != null && minIO.isMode()) {
            MinioClient client = MinioInit.build(
                    minIO.getEndpoint(),
                    minIO.getAccessKey(),
                    minIO.getSecretKey()
            );

            try (InputStream is = client.getObject(
                    GetObjectArgs.builder()
                            .bucket(minIO.getBucketName())
                            .object(minIO.getObjectName())
                            .build()
            )) {
                CheckoutManager checkoutManager = new CheckoutManager(is);

                MinioCheckoutHitService service =
                        new MinioCheckoutHitService(checkoutManager, objectMapper);

                service.setMinioClient(client);
                service.setBucketName(minIO.getBucketName());
                service.setObjectName(minIO.getObjectName());

                return service;

            } catch (Exception e) {
                throw new IllegalStateException("Failed to init MinIO checkout service", e);
            }
        }

        if (path != null && path.startsWith("classpath:")) {
            String resourcePath = path.substring("classpath:".length());
            if (resourcePath.startsWith("/")) resourcePath = resourcePath.substring(1);

            try (InputStream is = CheckoutHitAutoConfiguration.class
                    .getClassLoader()
                    .getResourceAsStream(resourcePath)) {

                if (is == null) {
                    throw new IllegalStateException("Resource not found on classpath: " + resourcePath);
                }

                Path tmp = Files.createTempFile("checkout-config-", ".json");
                tmp.toFile().deleteOnExit();
                Files.copy(is, tmp, StandardCopyOption.REPLACE_EXISTING);

                try (InputStream fileIs = Files.newInputStream(tmp)) {
                    CheckoutManager checkoutManager = new CheckoutManager(fileIs);

                    ConfigFileCheckoutHitService service =
                            new ConfigFileCheckoutHitService(objectMapper, checkoutManager);

                    service.setConfigName(tmp.toString());
                    return service;
                }

            } catch (Exception e) {
                throw new IllegalStateException("Failed to load config from classpath: " + resourcePath, e);
            }
        }

        try {
            Path file = Path.of(path);
            if (!Files.exists(file)) {
                throw new IllegalStateException("Config file not found: " + path);
            }

            try (InputStream is = Files.newInputStream(file)) {
                CheckoutManager checkoutManager = new CheckoutManager(is);

                ConfigFileCheckoutHitService service =
                        new ConfigFileCheckoutHitService(objectMapper, checkoutManager);

                service.setConfigName(path);
                return service;
            }

        } catch (IOException e) {
            throw new IllegalStateException("Failed to init ConfigFileCheckoutHitService with path: " + path, e);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create CheckoutManager from file: " + path, e);
        }
    }
}
