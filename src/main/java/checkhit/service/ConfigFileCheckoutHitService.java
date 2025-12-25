package checkhit.service;

import checkhit.dto.AreasFileDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import ru.ifmo.se.gmt.checker.CheckoutManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ConfigFileCheckoutHitService extends ACheckoutHitService {
    @Getter @Setter
    private String configName;

    public ConfigFileCheckoutHitService(ObjectMapper objectMapper, CheckoutManager checkoutManager) {
        super(objectMapper, checkoutManager);
    }

    @Override
    public void updateResource(AreasFileDTO areasFileDTO) {
        try {
            byte[] jsonBytes = getObjectMapper().writeValueAsBytes(areasFileDTO);

            CheckoutManager potCheckoutManager =
                    new CheckoutManager(new ByteArrayInputStream(jsonBytes));

            Path target = Path.of(configName);
            Path parent = target.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Path tmp = (parent != null)
                    ? Files.createTempFile(parent, target.getFileName().toString(), ".tmp")
                    : Files.createTempFile(target.getFileName().toString(), ".tmp");

            Files.write(tmp, jsonBytes);

            try {
                Files.move(tmp, target,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (Exception atomicFail) {
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
            }

            setCheckoutManager(potCheckoutManager);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AreasFileDTO getAreasData() {
        try {
            Path path = Path.of(configName);

            if (!Files.exists(path)) {
                throw new IllegalStateException("Config file not found: " + configName);
            }

            try (InputStream is = Files.newInputStream(path)) {
                return fromJsonInputStream(is);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private AreasFileDTO fromJsonInputStream(InputStream is) throws Exception {
        return getObjectMapper().readValue(is, AreasFileDTO.class);
    }
}
