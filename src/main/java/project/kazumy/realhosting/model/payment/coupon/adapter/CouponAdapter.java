package project.kazumy.realhosting.model.payment.coupon.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import project.kazumy.realhosting.model.payment.coupon.Coupon;
import project.kazumy.realhosting.model.payment.coupon.impl.CouponImpl;

import java.time.LocalDateTime;

public class CouponAdapter implements SQLResultAdapter<Coupon> {

    @Override
    public Coupon adaptResult(SimpleResultSet result) {
        return CouponImpl.builder()
                .name(result.get("name"))
                .limits(Integer.parseInt(result.get("limits")))
                .percentage(Integer.parseInt(result.get("percentage")))
                .createAt(LocalDateTime.parse(result.get("createat")))
                .expirateAt(LocalDateTime.parse(result.get("expireat")))
                .build();
    }
}
