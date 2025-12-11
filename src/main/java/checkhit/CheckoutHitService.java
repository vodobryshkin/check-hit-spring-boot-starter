package checkhit;

import ru.ifmo.se.gmt.checker.CheckoutManager;
import ru.ifmo.se.gmt.geometry.model.Point;
import ru.ifmo.se.gmt.request.implementations.messages.CheckoutRequest;

import java.io.IOException;
import java.math.BigDecimal;

public class CheckoutHitService {
    private final CheckoutManager checkoutManager;

    public CheckoutHitService(String configName) throws IOException {
        this.checkoutManager = new CheckoutManager(configName);
    }

    public boolean checkoutHit(BigDecimal x, BigDecimal y, BigDecimal r) {
        return checkoutManager.checkRequest(new CheckoutRequest(new Point(x, y), r));
    }
}
