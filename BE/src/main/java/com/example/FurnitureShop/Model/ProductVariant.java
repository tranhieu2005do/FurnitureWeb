package com.example.FurnitureShop.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_variants")
@Builder
@AllArgsConstructor @NoArgsConstructor
@Data
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "length", precision = 8, scale = 2)
    private BigDecimal length;

    @Column(name = "width", precision = 8, scale = 2)
    private BigDecimal width;

    @Column(name = "height", precision = 8, scale = 2)
    private BigDecimal height;

    @Column(name = "price", precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "instock")
    private Long inStock;

    @Column(name = "color")
    private Color color;

    @Column(name = "material")
    private Material material;

    @Column(name = "is_active")
    private boolean isActive;

    public enum Material {

        GO_SOI("gỗ sồi"),
        GO_TAN_BI("gỗ tần bì"),
        GO_OC_CHO("gỗ óc chó"),
        INOX("inox"),
        NHOM("nhôm"),
        MARBLE("marble"),
        GRANITE("granite"),
        KINH("kính"),
        VAI("vải");

        private final String value;

        Material(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Material fromValue(String value) {
            for (Material m : Material.values()) {
                if (m.value.equalsIgnoreCase(value)) {
                    return m;
                }
            }
            throw new IllegalArgumentException("Invalid material: " + value);
        }
    }

    public enum Color {

        TRANG("trắng"),
        BE("be"),
        XAM_NHAT("xám nhạt"),
        XAM_DAM("xám đậm"),
        DEN("đen"),
        NAU_GO_NHAT("nâu gỗ nhạt"),
        NAU_GO_DAM("nâu gỗ đậm");

        private final String value;

        Color(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Color fromValue(String value) {
            for (Color c : Color.values()) {
                if (c.value.equalsIgnoreCase(value)) {
                    return c;
                }
            }
            throw new IllegalArgumentException("Invalid color: " + value);
        }
    }


    public boolean sameSize(ProductVariant other) {
        if (other == null) return false;
        return this.length.compareTo(other.length) == 0
                && this.width.compareTo(other.width) == 0
                && this.height.compareTo(other.height) == 0;
    }
    @OneToMany(mappedBy = "productVariant",cascade = CascadeType.ALL,  fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();
}
