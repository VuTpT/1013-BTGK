package vn.edu.ktpm.minishop.shop;

/**
 * FR-03 - Quy tac khuyen mai, hien thuc truc tiep tu <b>bang quyet dinh</b> (ky thuat bai W5).
 *
 * <pre>
 *  C1 VIP | C2 &gt;=500k | C3 ma hop le | Giam
 *  -------+-----------+--------------+------
 *    T    |     T     |      T       | 25%
 *    T    |     T     |      F       | 15%
 *    T    |     F     |      T       | 20%
 *    T    |     F     |      F       | 10%
 *    F    |     T     |      T       | 15%
 *    F    |     T     |      F       |  5%
 *    F    |     F     |      T       | 10%
 *    F    |     F     |      F       |  0%
 * </pre>
 */
public final class DiscountPolicy {

    /** Ma giam gia hop le duy nhat. */
    public static final String VALID_COUPON = "SALE10";

    /** Nguong tam tinh de duoc uu dai (VND). */
    public static final long THRESHOLD = 500_000L;

    private DiscountPolicy() {
    }

    /** @return muc giam gia theo phan tram (0..25). */
    public static int discountPercent(boolean vip, long subtotal, String coupon) {
        boolean bigOrder = subtotal >= THRESHOLD;
        boolean validCoupon = isValidCoupon(coupon);

        if (vip && bigOrder && validCoupon) {
            return 25;
        }
        if (vip && bigOrder) {
            return 15;
        }
        if (vip && validCoupon) {
            return 20;
        }
        if (vip) {
            return 10;
        }
        if (bigOrder && validCoupon) {
            return 15;
        }
        if (bigOrder) {
            return 5;
        }
        if (validCoupon) {
            return 10;
        }
        return 0;
    }

    /** Ma giam gia khong phan biet hoa thuong va tu cat khoang trang hai dau. */
    public static boolean isValidCoupon(String coupon) {
        return coupon != null && VALID_COUPON.equalsIgnoreCase(coupon.trim());
    }

    /** Tong thanh toan = tam tinh - giam gia, lam tron XUONG den dong. */
    public static long finalAmount(boolean vip, long subtotal, String coupon) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("Tam tinh khong duoc am");
        }
        int percent = discountPercent(vip, subtotal, coupon);
        long discount = subtotal * percent / 100; // phep chia nguyen = lam tron xuong
        return subtotal - discount;
    }
}
