package vn.edu.ktpm.minishop.shop;

/**
 * FR-04 - Dat hang: rap noi validate + khuyen mai + xoa gio.
 *
 * <p>Day la don vi tich hop nho nhat cua tang core, dung de minh hoa su khac nhau giua
 * <i>unit test</i> (test rieng {@link CheckoutValidator}, {@link DiscountPolicy}) va
 * <i>integration test</i> (test ca luong).</p>
 */
public class CheckoutService {

    private final CheckoutValidator validator;
    private int orderSequence = 0;

    public CheckoutService() {
        this(new CheckoutValidator());
    }

    public CheckoutService(CheckoutValidator validator) {
        this.validator = validator;
    }

    /**
     * @return ma don hang neu thanh cong.
     * @throws CartException neu thong tin khong hop le (kem thong bao loi dau tien).
     */
    public String placeOrder(Cart cart, boolean vip, String coupon,
                             String fullName, String phone, String address) {
        ValidationResult result = validator.validate(fullName, phone, address, cart);
        if (!result.isValid()) {
            throw new CartException(result.getErrors().values().iterator().next());
        }
        long total = DiscountPolicy.finalAmount(vip, cart.subtotal(), coupon);
        cart.clear();
        orderSequence++;
        return String.format("MS%04d-%d", orderSequence, total);
    }
}
