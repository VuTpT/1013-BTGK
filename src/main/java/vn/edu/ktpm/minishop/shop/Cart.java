package vn.edu.ktpm.minishop.shop;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * FR-02 - Gio hang.
 *
 * <p>Rang buoc (nguon cua cac test case bien - BVA):</p>
 * <ul>
 *   <li>So luong moi san pham: <b>1..10</b></li>
 *   <li>So loai san pham khac nhau: toi da <b>5</b></li>
 *   <li>Them san pham da co: cong don, tong khong vuot qua 10</li>
 *   <li>Moi thao tac khong hop le: <b>giu nguyen gio hang</b> va nem {@link CartException}</li>
 * </ul>
 */
public class Cart {

    public static final int MIN_QUANTITY = 1;
    public static final int MAX_QUANTITY = 10;
    public static final int MAX_DISTINCT_PRODUCTS = 5;

    private final Map<String, CartLine> lines = new LinkedHashMap<>();

    public void add(Product product, int quantity) {
        if (product == null) {
            throw new CartException("San pham khong duoc rong");
        }
        if (quantity < MIN_QUANTITY || quantity > MAX_QUANTITY) {
            throw new CartException("So luong phai tu " + MIN_QUANTITY + " den " + MAX_QUANTITY);
        }

        CartLine existing = lines.get(product.getCode());
        if (existing == null) {
            if (lines.size() >= MAX_DISTINCT_PRODUCTS) {
                throw new CartException("Gio hang chi chua toi da " + MAX_DISTINCT_PRODUCTS + " loai san pham");
            }
            lines.put(product.getCode(), new CartLine(product, quantity));
            return;
        }

        int newQuantity = existing.getQuantity() + quantity;
        if (newQuantity > MAX_QUANTITY) {
            throw new CartException("Tong so luong cua mot san pham khong duoc vuot qua " + MAX_QUANTITY);
        }
        existing.setQuantity(newQuantity);
    }

    public void remove(String productCode) {
        if (lines.remove(productCode) == null) {
            throw new CartException("San pham khong co trong gio: " + productCode);
        }
    }

    public void clear() {
        lines.clear();
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    /** So loai san pham khac nhau dang co trong gio. */
    public int distinctProductCount() {
        return lines.size();
    }

    /** Tong so luong tat ca san pham. */
    public int totalQuantity() {
        return lines.values().stream().mapToInt(CartLine::getQuantity).sum();
    }

    public int quantityOf(String productCode) {
        CartLine line = lines.get(productCode);
        return line == null ? 0 : line.getQuantity();
    }

    /** Tam tinh = tong (gia x so luong). */
    public long subtotal() {
        long total = 0;
        for (CartLine line : lines.values()) {
            total += line.getProduct().getPrice() * line.getQuantity();
        }
        return total;
    }

    public Map<String, CartLine> getLines() {
        return Collections.unmodifiableMap(lines);
    }

    /** Mot dong trong gio hang. */
    public static class CartLine {

        private final Product product;
        private int quantity;

        CartLine(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
