package checkhit.service;

import checkhit.dto.AreasFileDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.ifmo.se.gmt.checker.CheckoutManager;
import ru.ifmo.se.gmt.geometry.model.Point;
import ru.ifmo.se.gmt.request.implementations.messages.CheckoutRequest;

import java.math.BigDecimal;

@AllArgsConstructor
public abstract class ACheckoutHitService {
    @Getter
    private final ObjectMapper objectMapper;

    @Setter
    private volatile CheckoutManager checkoutManager;

    public boolean checkoutHit(String x, String y, String r) {
        CheckoutManager manager = this.checkoutManager;
        return manager.checkRequest(
                new CheckoutRequest(
                        new Point(new BigDecimal(x), new BigDecimal(y)),
                        new BigDecimal(r)
                )
        );
    }

    public abstract void updateResource(AreasFileDTO areasFileDTO);
    public abstract AreasFileDTO getAreasData();
}
