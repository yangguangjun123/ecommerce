package org.myproject.ecommerce.domain;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public class Product {
    @BsonId
    private ObjectId id;

    protected String sku;
    protected String type;
    protected String genre;
    protected String title;
    protected String description;
    protected String asin;
    protected Shipping shipping;
    protected Pricing pricing;

    @BsonProperty(value = "qty")
    protected int quantity;

    protected List<CartedItem> carted;

    public Product() {
    }

    public Product(String sku, String type, String genre, String title,
                   String description, String asin, Shipping shipping,
                   Pricing pricing, int quantity, List<CartedItem> carted) {
        this.sku = sku;
        if(ProductType.fromValue(type) == null) {
            throw new IllegalArgumentException("incorrect product type: " + type);
        }
        this.type = type;
        this.genre = genre;
        this.title = title;
        this.description = description;
        this.asin = asin;
        this.shipping = shipping;
        this.pricing = pricing;
        this.quantity = quantity;
        this.carted = carted;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<CartedItem> getCarted() {
        return carted;
    }

    public void setCarted(List<CartedItem> carted) {
        this.carted = carted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (quantity != product.quantity) return false;
        if (sku != null ? !sku.equals(product.sku) : product.sku != null) return false;
        if (type != null ? !type.equals(product.type) : product.type != null) return false;
        if (genre != null ? !genre.equals(product.genre) : product.genre != null) return false;
        if (title != null ? !title.equals(product.title) : product.title != null) return false;
        if (description != null ? !description.equals(product.description) : product.description != null) return false;
        if (asin != null ? !asin.equals(product.asin) : product.asin != null) return false;
        if (shipping != null ? !shipping.equals(product.shipping) : product.shipping != null) return false;
        if (pricing != null ? !pricing.equals(product.pricing) : product.pricing != null) return false;
        return carted != null ? carted.equals(product.carted) : product.carted == null;
    }

    @Override
    public int hashCode() {
        int result = sku != null ? sku.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (genre != null ? genre.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (asin != null ? asin.hashCode() : 0);
        result = 31 * result + (shipping != null ? shipping.hashCode() : 0);
        result = 31 * result + (pricing != null ? pricing.hashCode() : 0);
        result = 31 * result + quantity;
        result = 31 * result + (carted != null ? carted.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "sku='" + sku + '\'' +
                ", type='" + type + '\'' +
                ", genre='" + genre + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", asin='" + asin + '\'' +
                ", shipping=" + shipping +
                ", pricing=" + pricing +
                ", quantity=" + quantity +
                ", carted=" + carted +
                '}';
    }

    public static class CartedItem {
        @BsonProperty("qty")
        private int quantity;

        @BsonProperty("cart_id")
        private int cartId;

        private Date timestamp;

        public CartedItem() {
        }

        public CartedItem(int quantity, int cartId, Date timestamp) {
            this.quantity = quantity;
            this.cartId = cartId;
            this.timestamp = timestamp;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getCartId() {
            return cartId;
        }

        public void setCartId(int cartId) {
            this.cartId = cartId;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CartedItem that = (CartedItem) o;

            if (quantity != that.quantity) return false;
            if (cartId != that.cartId) return false;
            return timestamp != null ? timestamp.equals(that.timestamp) : that.timestamp == null;
        }

        @Override
        public int hashCode() {
            int result = quantity;
            result = 31 * result + cartId;
            result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "CartedItem{" +
                    "quantity=" + quantity +
                    ", cartId=" + cartId +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }

    public static class ProductBuilder {
        protected String sku;
        protected String type;
        protected String genre;
        protected String title;
        protected String description;
        protected String asin;
        protected Shipping shipping;
        protected Pricing pricing;
        protected int quantity;
        protected List<CartedItem> carted;

        public ProductBuilder(String sku, String type) {
            this.sku = sku;
            if(ProductType.fromValue(type) == null) {
                throw new IllegalArgumentException("incorrect product type: " + type);
            }
            this.type = type;
        }

        public ProductBuilder buildGenre(String genre) {
            this.genre = genre;
            return this;
        }

        public ProductBuilder buildTitle(String title) {
            this.title = title;
            return this;
        }

        public ProductBuilder buildDescription(String description) {
            this.description = description;
            return this;
        }

        public ProductBuilder buildAsin(String asin) {
            this.asin = asin;
            return this;
        }

        public ProductBuilder buildShipping(int width, int height, int depth) {
            Shipping.Dimensions dimensions = new Shipping.Dimensions(width, height, depth);
            return this;
        }

        public ProductBuilder buildShipping(Shipping shipping) {
            this.shipping = shipping;
            return this;
        }

        public ProductBuilder buildPricing(Pricing pricing) {
            this.pricing = pricing;
            return this;
        }

        public ProductBuilder buildQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public ProductBuilder buildShoppingCart(List<CartedItem> carted) {
            this.carted = carted;
            return this;
        }
    }
}
