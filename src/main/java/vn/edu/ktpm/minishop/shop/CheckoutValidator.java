package vn.edu.ktpm.minishop.shop;

import java.util.regex.Pattern;

/**
 * FR-04 - Kiem tra thong tin giao hang.
 *
 * <p>Cac hang so bien duoi day chinh la nguon cua bo test case
 * <b>phan hoach tuong duong + gia tri bien</b> (ky thuat bai W7).</p>
 */
public class CheckoutValidator {

    public static final String FIELD_NAME = "fullName";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_CART = "cart";

    public static final int NAME_MIN = 1;
    public static final int NAME_MAX = 50;
    public static final int ADDRESS_MIN = 1;
    public static final int ADDRESS_MAX = 100;
    public static final int PHONE_LENGTH = 10;

    /** Chi chu cai (ke ca tieng Viet co dau) va khoang trang. */
    private static final Pattern NAME_PATTERN = Pattern.compile("[\\p{L} ]+");
    /** Dung 10 chu so, bat dau bang so 0. */
    private static final Pattern PHONE_PATTERN = Pattern.compile("0\\d{9}");

    public ValidationResult validate(String fullName, String phone, String address, Cart cart) {
        ValidationResult result = new ValidationResult();

        if (isBlank(fullName)) {
            result.addError(FIELD_NAME, "Ho ten khong duoc de trong");
        } else if (fullName.length() > NAME_MAX) {
            result.addError(FIELD_NAME, "Ho ten toi da " + NAME_MAX + " ky tu");
        } else if (!NAME_PATTERN.matcher(fullName).matches()) {
            result.addError(FIELD_NAME, "Ho ten chi duoc chua chu cai va khoang trang");
        }

        if (isBlank(phone)) {
            result.addError(FIELD_PHONE, "So dien thoai khong duoc de trong");
        } else if (!PHONE_PATTERN.matcher(phone).matches()) {
            result.addError(FIELD_PHONE, "So dien thoai phai gom " + PHONE_LENGTH + " chu so va bat dau bang 0");
        }

        if (isBlank(address)) {
            result.addError(FIELD_ADDRESS, "Dia chi khong duoc de trong");
        } else if (address.length() > ADDRESS_MAX) {
            result.addError(FIELD_ADDRESS, "Dia chi toi da " + ADDRESS_MAX + " ky tu");
        }

        if (cart == null || cart.isEmpty()) {
            result.addError(FIELD_CART, "Gio hang dang trong");
        }

        return result;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
