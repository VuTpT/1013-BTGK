package vn.edu.ktpm.minishop.shop;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Danh muc 6 san pham cua MiniShop (FR-02).
 *
 * <p><b>Quan trong:</b> danh sach nay phai <u>trung khop</u> voi mang PRODUCTS trong
 * {@code sut-web/js/app.js} - do la co so de test tang unit va test tang UI cung kiem thu
 * mot bo du lieu.</p>
 */
public final class Catalog {

    private static final Map<String, Product> PRODUCTS = new LinkedHashMap<>();

    static {
        put(new Product("P01", "Ao thun basic", 150_000L));
        put(new Product("P02", "Quan jean slimfit", 350_000L));
        put(new Product("P03", "Giay sneaker", 500_000L));
        put(new Product("P04", "Balo laptop", 250_000L));
        put(new Product("P05", "Non luoi trai", 120_000L));
        put(new Product("P06", "Kinh ram UV400", 200_000L));
    }

    private Catalog() {
    }

    private static void put(Product product) {
        PRODUCTS.put(product.getCode(), product);
    }

    public static Product byCode(String code) {
        Product product = PRODUCTS.get(code);
        if (product == null) {
            throw new IllegalArgumentException("Khong tim thay san pham: " + code);
        }
        return product;
    }

    public static List<Product> all() {
        return Collections.unmodifiableList(List.copyOf(PRODUCTS.values()));
    }
}
