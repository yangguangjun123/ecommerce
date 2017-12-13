package org.myproject.ecommerce.domain;

import org.bson.codecs.pojo.annotations.BsonId;

public class Price {
    @BsonId
    private String id;

    private int price;

    public Price() {
    }

    public Price(String id, int price) {
        this.id = id;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Price price1 = (Price) o;

        if (price != price1.price) return false;
        return id != null ? id.equals(price1.id) : price1.id == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + price;
        return result;
    }

    @Override
    public String toString() {
        return "Price{" +
                "id='" + id + '\'' +
                ", price=" + price +
                '}';
    }
}
