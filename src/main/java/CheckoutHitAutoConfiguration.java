import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

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
    public CheckoutHitService greetingService(CheckoutProperties properties) throws IOException {
        return new CheckoutHitService(properties.getConfigPath());
    }
}
