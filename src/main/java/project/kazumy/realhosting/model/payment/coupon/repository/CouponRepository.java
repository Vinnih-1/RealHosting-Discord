package project.kazumy.realhosting.model.payment.coupon.repository;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import lombok.Data;
import project.kazumy.realhosting.model.payment.coupon.Coupon;
import project.kazumy.realhosting.model.payment.coupon.adapter.CouponAdapter;

@Data(staticConstructor = "of")
public class CouponRepository {

    private final SQLExecutor executor;

    public void save(Coupon coupon) {
        executor.updateQuery("INSERT INTO coupons (name, limits, percentage, createat, expireat) " +
                "VALUES (?, ?, ?, ?, ?)", statement -> {
            statement.set(1, coupon.getName());
            statement.set(2, coupon.getLimits());
            statement.set(3, coupon.getPercentage());
            statement.set(4, coupon.getCreateAt().toString());
            statement.set(5, coupon.getExpirateAt().toString());
        });
    }

    public Coupon findCouponByName(String name) {
        return executor.resultOneQuery("SELECT * FROM coupons WHERE name = ?", statement -> {
            statement.set(1, name);
        }, CouponAdapter.class);
    }
}
