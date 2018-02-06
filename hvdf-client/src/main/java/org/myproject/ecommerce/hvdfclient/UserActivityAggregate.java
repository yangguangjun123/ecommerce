package org.myproject.ecommerce.hvdfclient;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class UserActivityAggregate {
    @BsonId
    private String userId;

    @BsonProperty("value")
    private long count;

    public UserActivityAggregate() {
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

        UserActivityAggregate that = (UserActivityAggregate) o;

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
        return "UserActivityAggregate{" +
                "userId='" + userId + '\'' +
                ", count=" + count +
                '}';
    }
}
