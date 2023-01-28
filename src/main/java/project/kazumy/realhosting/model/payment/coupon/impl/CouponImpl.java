package project.kazumy.realhosting.model.payment.coupon.impl;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import project.kazumy.realhosting.model.payment.coupon.Coupon;

import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CouponImpl implements Coupon {

    @EqualsAndHashCode.Include
    private final String name;

    private final Integer limits, percentage;
    private final LocalDateTime createAt;
    private final LocalDateTime expirateAt;
}
