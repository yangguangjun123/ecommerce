package org.myproject.ecommerce.hvdfclient;

import org.bson.codecs.pojo.annotations.BsonId;

public class UserActivityAggregate {
    @BsonId
    private String id;

    private UserActivityAggregateItem value;

    public UserActivityAggregate() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserActivityAggregate that = (UserActivityAggregate) o;

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
        return "UserActivityAggregate{" +
                "id='" + id + '\'' +
                ", value=" + value +
                '}';
    }

    public UserActivityAggregateItem getValue() {
        return value;
    }

    public void setValue(UserActivityAggregateItem value) {
        this.value = value;
    }

    public static class UserActivityAggregateItem {
        private String userId;
        private long count;

        public UserActivityAggregateItem() {
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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

            UserActivityAggregateItem that = (UserActivityAggregateItem) o;

            if (count != that.count) return false;
            return userId != null ? userId.equals(that.userId) : that.userId == null;
        }

        @Override
        public int hashCode() {
            int result = userId != null ? userId.hashCode() : 0;
            result = 31 * result + (int) (count ^ (count >>> 32));
            return result;
        }

        @Override
        public String toString() {
            return "UserActivityAggregateItem{" +
                    "userId='" + userId + '\'' +
                    ", count=" + count +
                    '}';
        }
    }

}
