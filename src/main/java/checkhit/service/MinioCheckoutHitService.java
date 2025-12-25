package checkhit.service;

import checkhit.dto.AreasFileDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.Getter;
import lombok.Setter;
import ru.ifmo.se.gmt.checker.CheckoutManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MinioCheckoutHitService extends ACheckoutHitService {
    @Setter
    private MinioClient minioClient;
    @Getter @Setter
    private String bucketName = null;
    @Getter @Setter
    private String objectName = null;

    public MinioCheckoutHitService(CheckoutManager checkoutManager, ObjectMapper objectMapper) {
        super(objectMapper, checkoutManager);
    }

    @Override
    public void updateResource(AreasFileDTO areasFileDTO) {
        try {
            CheckoutManager potCheckoutManager = new CheckoutManager(toJsonInputStream(areasFileDTO));

            try (InputStream jsonStream = toJsonInputStream(areasFileDTO)) {
                long partSize = 10L * 1024 * 1024;

                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .contentType("application/json")
                                .stream(jsonStream, -1, partSize)
                                .build()
                );
            }

            setCheckoutManager(potCheckoutManager);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AreasFileDTO getAreasData() {
        try (InputStream is = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        )) {
            return fromJsonInputStream(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private InputStream toJsonInputStream(AreasFileDTO dto) throws Exception {
        byte[] jsonBytes = getObjectMapper().writeValueAsBytes(dto);
        return new ByteArrayInputStream(jsonBytes);
    }

    private AreasFileDTO fromJsonInputStream(InputStream is) throws Exception {
        return getObjectMapper().readValue(is, AreasFileDTO.class);
    }
}
