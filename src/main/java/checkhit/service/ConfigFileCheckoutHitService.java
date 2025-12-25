package checkhit.service;

import ru.ifmo.se.gmt.checker.CheckoutManager;
import ru.ifmo.se.gmt.geometry.model.Point;
import ru.ifmo.se.gmt.request.implementations.messages.CheckoutRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ConfigFileCheckoutHitService implements ICheckoutHitService {

    private final Path configPath;

    private volatile CheckoutManager checkoutManager;

    public ConfigFileCheckoutHitService(String configName) throws IOException {
        this(Path.of(configName));
    }

    public ConfigFileCheckoutHitService(Path configPath) throws IOException {
        this.configPath = Objects.requireNonNull(configPath, "configPath");
        reload();
    }

    @Override
    public boolean checkoutHit(String x, String y, String r) {
        CheckoutManager manager = this.checkoutManager; // локальная ссылка (чтобы не поймать смену посередине)
        return manager.checkRequest(
                new CheckoutRequest(
                        new Point(new BigDecimal(x), new BigDecimal(y)),
                        new BigDecimal(r)
                )
        );
    }

    @Override
    public void updateData() throws IOException {
        reload();
    }

    private synchronized void reload() throws IOException {
        if (!Files.exists(configPath)) {
            throw new IOException("Config file not found: " + configPath.toAbsolutePath());
        }
        if (!Files.isRegularFile(configPath)) {
            throw new IOException("Config path is not a regular file: " + configPath.toAbsolutePath());
        }
        this.checkoutManager = new CheckoutManager(configPath.toString());
    }
}
