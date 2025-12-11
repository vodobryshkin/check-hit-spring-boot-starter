import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "checkout")
public class CheckoutProperties {
    private String configPath = "areas.json";

    public String getConfigPath() {
        return configPath;
    }

    public void setMessage(String configPath) {
        this.configPath = configPath;
    }
}
