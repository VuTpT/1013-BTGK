package vn.edu.ktpm.minishop.shop;

/**
 * Vi pham rang buoc gio hang (so luong ngoai [1..10], qua 5 loai san pham...).
 */
public class CartException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CartException(String message) {
        super(message);
    }
}
