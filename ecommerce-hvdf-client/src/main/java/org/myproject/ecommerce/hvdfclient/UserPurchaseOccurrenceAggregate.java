package org.myproject.ecommerce.hvdfclient;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class UserPurchaseOccurrenceAggregate {
    private IdKey id;

    @BsonProperty("value")
    private long count;

    public UserPurchaseOccurrenceAggregate() {
    }

    public IdKey getId() {
        return id;
    }

    public void setId(IdKey id) {
        this.id = id;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserPurchaseOccurrenceAggregate that = (UserPurchaseOccurrenceAggregate) o;

        if (count != that.count) return false;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (int) (count ^ (count >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "UserPurchaseOccurrenceAggregate{" +
                "id=" + id +
                ", count=" + count +
                '}';
    }

    public static class IdKey {
        @BsonProperty("a")
        private String item1;

        @BsonProperty("b")
        private String item2;

        public IdKey() {
        }

        public IdKey(String item1, String item2) {
            this.item1 = item1;
            this.item2 = item2;
        }

        public String getItem1() {
            return item1;
        }

        public void setItem1(String item1) {
            this.item1 = item1;
        }

        public String getItem2() {
            return item2;
        }

        public void setItem2(String item2) {
            this.item2 = item2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IdKey idKey = (IdKey) o;

            if (item1 != null ? !item1.equals(idKey.item1) : idKey.item1 != null) return false;
            return item2 != null ? item2.equals(idKey.item2) : idKey.item2 == null;
        }

        @Override
        public int hashCode() {
            int result = item1 != null ? item1.hashCode() : 0;
            result = 31 * result + (item2 != null ? item2.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "IdKey{" +
                    "item1='" + item1 + '\'' +
                    ", item2='" + item2 + '\'' +
                    '}';
        }
    }
}
