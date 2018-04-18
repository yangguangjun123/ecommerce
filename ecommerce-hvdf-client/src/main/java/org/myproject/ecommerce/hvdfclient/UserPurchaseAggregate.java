package org.myproject.ecommerce.hvdfclient;

import org.bson.codecs.pojo.annotations.BsonId;

import java.util.List;

public class UserPurchaseAggregate {
    @BsonId
    private String id;

    private UserPurchaseAggregateItem value;

    public UserPurchaseAggregate() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserPurchaseAggregateItem getValue() {
        return value;
    }

    public void setValue(UserPurchaseAggregateItem value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserPurchaseAggregate that = (UserPurchaseAggregate) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserPurchaseAggregate{" +
                "id='" + id + '\'' +
                ", value=" + value +
                '}';
    }

    public static class UserPurchaseAggregateItem {
        private String userId;
        private List<String> items;

        public UserPurchaseAggregateItem() {
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserPurchaseAggregateItem that = (UserPurchaseAggregateItem) o;

            if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
            return items != null ? items.equals(that.items) : that.items == null;
        }

        @Override
        public int hashCode() {
            int result = userId != null ? userId.hashCode() : 0;
            result = 31 * result + (items != null ? items.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "UserPurchaseAggregateItem{" +
                    "userId='" + userId + '\'' +
                    ", items=" + items +
                    '}';
        }
    }
}
