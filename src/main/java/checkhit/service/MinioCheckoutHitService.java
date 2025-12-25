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
    public void updateResource(AreasFileDTO dto) {
        try {
            if (dto == null || dto.getAreas() == null) {
                throw new IllegalArgumentException("areas request is null: dto == null or dto.areas == null");
            }

            byte[] jsonBytes = getObjectMapper().writeValueAsBytes(dto);

            CheckoutManager potCheckoutManager =
                    new CheckoutManager(new ByteArrayInputStream(jsonBytes));

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .contentType("application/json")
                            .stream(new ByteArrayInputStream(jsonBytes), jsonBytes.length, -1)
                            .build()
            );

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

    private AreasFileDTO fromJsonInputStream(InputStream is) throws Exception {
        return getObjectMapper().readValue(is, AreasFileDTO.class);
    }
}
