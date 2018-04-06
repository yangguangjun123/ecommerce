package org.myproject.ecommerce.hvdfclient;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;

public class UserPurchaseMostPopularPairAggregate {
    private IdKey id;

    @BsonProperty("value")
    private Recom recom;

    public UserPurchaseMostPopularPairAggregate() {
    }

    public IdKey getId() {
        return id;
    }

    public void setId(IdKey id) {
        this.id = id;
    }

    public Recom getRecom() {
        return recom;
    }

    public void setRecom(Recom recom) {
        this.recom = recom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserPurchaseMostPopularPairAggregate that = (UserPurchaseMostPopularPairAggregate) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return recom != null ? recom.equals(that.recom) : that.recom == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (recom != null ? recom.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserPurchaseMostPopularPairAggregate{" +
                "id=" + id +
                ", recom=" + recom +
                '}';
    }

    public static class IdKey {
        private String itemId;

        public IdKey() {
        }

        public IdKey(String itemId) {
            this.itemId = itemId;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IdKey idKey = (IdKey) o;

            return itemId != null ? itemId.equals(idKey.itemId) : idKey.itemId == null;
        }

        @Override
        public int hashCode() {
            return itemId != null ? itemId.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "IdKey{" +
                    "itemId='" + itemId + '\'' +
                    '}';
        }
    }

    public static class PurchasePair {
        private String itemId;
        private int count;
        private int weight;

        public PurchasePair() {
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PurchasePair that = (PurchasePair) o;

            if (count != that.count) return false;
            if (weight != that.weight) return false;
            return itemId != null ? itemId.equals(that.itemId) : that.itemId == null;
        }

        @Override
        public int hashCode() {
            int result = itemId != null ? itemId.hashCode() : 0;
            result = 31 * result + count;
            result = 31 * result + weight;
            return result;
        }

        @Override
        public String toString() {
            return "PurchasePair{" +
                    "itemId='" + itemId + '\'' +
                    ", count=" + count +
                    ", weight=" + weight +
                    '}';
        }
    }

    public static class Recom {
        @BsonProperty("recom")
        private List<PurchasePair> pairs;

        public Recom() {
        }

        public List<PurchasePair> getPairs() {
            return pairs;
        }

        public void setPairs(List<PurchasePair> pairs) {
            this.pairs = pairs;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Recom recom = (Recom) o;

            return pairs != null ? pairs.equals(recom.pairs) : recom.pairs == null;
        }

        @Override
        public int hashCode() {
            return pairs != null ? pairs.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Recom{" +
                    "pairs=" + pairs +
                    '}';
        }
    }
}
