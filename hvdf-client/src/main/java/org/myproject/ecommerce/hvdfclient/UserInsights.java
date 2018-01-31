package org.myproject.ecommerce.hvdfclient;

import org.bson.codecs.pojo.annotations.BsonId;

public class UserInsights {
    private String id;
    private long count;

    public UserInsights(){
    }

    public UserInsights(String id, long count) {
        this.id = id;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public static long convert(UserInsights userInsights) {
        return userInsights.count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInsights that = (UserInsights) o;

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
        return "UserActivityReport{" +
                "type='" + id + '\'' +
                ", count=" + count +
                '}';
    }
}
