package project.kazumy.realhosting.model.payment.coupon;

import java.time.LocalDateTime;

public interface Coupon {

    String getName();

    Integer getLimits();

    Integer getPercentage();

    LocalDateTime getCreateAt();

    LocalDateTime getExpirateAt();
}
