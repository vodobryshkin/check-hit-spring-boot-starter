package checkhit.service;

import java.io.IOException;

public interface ICheckoutHitService {
    boolean checkoutHit(String x, String y, String r);
    void updateData() throws IOException;
}
