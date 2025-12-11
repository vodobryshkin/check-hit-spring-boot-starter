package checkhit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ifmo.se.gmt.checker.CheckoutManager;

import java.io.IOException;
import java.io.InputStream;

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
    public CheckoutHitService checkoutHitService(CheckoutProperties properties) {
        String path = properties.getConfigPath();

        if (path.startsWith("classpath:")) {
            String resourcePath = path.substring("classpath:".length());
            if (resourcePath.startsWith("/")) {
                resourcePath = resourcePath.substring(1);
            }

            try (InputStream is = CheckoutHitAutoConfiguration.class
                    .getClassLoader()
                    .getResourceAsStream(resourcePath)) {

                if (is == null) {
                    throw new IllegalStateException("Resource not found on classpath: " + resourcePath);
                }

                CheckoutManager manager = new CheckoutManager(is);
                return new CheckoutHitService(manager);

            } catch (IOException e) {
                throw new IllegalStateException("Failed to load config from classpath: " + resourcePath, e);
            }
        }

        try {
            return new CheckoutHitService(path);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to init CheckoutHitService with path: " + path, e);
        }
    }
}

