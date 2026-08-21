package vn.edu.ktpm.minishop.shop;

import java.util.Objects;

/**
 * San pham trong MiniShop (FR-02). Gia tinh bang VND, kieu long de tranh sai so dau phay dong.
 */
public class Product {

    private final String code;
    private final String name;
    private final long price;

    public Product(String code, String name, long price) {
        if (price < 0) {
            throw new IllegalArgumentException("Gia san pham khong duoc am");
        }
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        Product other = (Product) o;
        return Objects.equals(code, other.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code + " - " + name + " (" + price + "d)";
    }
}
