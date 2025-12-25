package checkhit.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import ru.ifmo.se.gmt.checker.CheckoutManager;
import ru.ifmo.se.gmt.geometry.model.Point;
import ru.ifmo.se.gmt.request.implementations.messages.CheckoutRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Objects;

public class MinioCheckoutHitService implements ICheckoutHitService {

    private final MinioClient minioClient;
    private final String bucketName;
    private final String objectName;

    private CheckoutManager checkoutManager;

    public MinioCheckoutHitService(
            MinioClient minioClient,
            String bucketName,
            String objectName
    ) throws IOException {
        this.minioClient = Objects.requireNonNull(minioClient);
        this.bucketName = Objects.requireNonNull(bucketName);
        this.objectName = Objects.requireNonNull(objectName);

        reloadManagerFromMinio();
    }

    @Override
    public boolean checkoutHit(String x, String y, String r) {
        return checkoutManager.checkRequest(new CheckoutRequest(new Point(new BigDecimal(x), new BigDecimal(y)), new BigDecimal(r)));
    }

    @Override
    public void updateData() throws IOException {
        reloadManagerFromMinio();
    }

    private synchronized void reloadManagerFromMinio() throws IOException {
        try (InputStream minioStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        )) {
            byte[] data = minioStream.readAllBytes();
            this.checkoutManager = new CheckoutManager(new ByteArrayInputStream(data));
        } catch (Exception e) {
            throw new IOException(
                    "Не удалось загрузить данные из MinIO: bucket=" + bucketName + ", object=" + objectName,
                    e
            );
        }
    }
}
