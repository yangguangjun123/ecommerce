package org.myproject.ecommerce.domain;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.List;

public class Store {
    @BsonId
    private ObjectId id;

    private String storeId;
    private String className;
    private String name;
    private StoreAddress address;
    private List<Double> location;

    private List<String> storeGroups;

    public Store() {
    }

    public Store(ObjectId id, String storeId) {
        this.id = id;
        this.storeId = storeId;
    }

    public Store(ObjectId id, String storeId, String className, String name, StoreAddress address,
                 List<Double> location, List<String> storeGroups) {
        this.id = id;
        this.storeId = storeId;
        this.className = className;
        this.name = name;
        this.address = address;
        this.location = location;
        this.storeGroups = storeGroups;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public List<String> getStoreGroups() {
        return storeGroups;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setStoreGroups(List<String> storeGroups) {
        this.storeGroups = storeGroups;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StoreAddress getAddress() {
        return address;
    }

    public void setAddress(StoreAddress address) {
        this.address = address;
    }

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Store store = (Store) o;

        if (id != null ? !id.equals(store.id) : store.id != null) return false;
        if (storeId != null ? !storeId.equals(store.storeId) : store.storeId != null) return false;
        if (className != null ? !className.equals(store.className) : store.className != null) return false;
        if (name != null ? !name.equals(store.name) : store.name != null) return false;
        if (address != null ? !address.equals(store.address) : store.address != null) return false;
        if (location != null ? !location.equals(store.location) : store.location != null) return false;
        return storeGroups != null ? storeGroups.equals(store.storeGroups) : store.storeGroups == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (storeId != null ? storeId.hashCode() : 0);
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (storeGroups != null ? storeGroups.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Store{" +
                "id='" + id + '\'' +
                ", storeId='" + storeId + '\'' +
                ", className='" + className + '\'' +
                ", name='" + name + '\'' +
                ", address=" + address +
                ", location=" + location +
                ", storeGroups=" + storeGroups +
                '}';
    }

    public static class StoreAddress {
        private String addr1;
        private String city;
        private String state;
        private String zip;
        private String country;

        public StoreAddress() {
        }

        public StoreAddress(String addr1, String city, String state, String zip, String country) {
            this.addr1 = addr1;
            this.city = city;
            this.state = state;
            this.zip = zip;
            this.country = country;
        }

        public String getAddr1() {
            return addr1;
        }

        public void setAddr1(String addr1) {
            this.addr1 = addr1;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StoreAddress that = (StoreAddress) o;

            if (addr1 != null ? !addr1.equals(that.addr1) : that.addr1 != null) return false;
            if (city != null ? !city.equals(that.city) : that.city != null) return false;
            if (state != null ? !state.equals(that.state) : that.state != null) return false;
            if (zip != null ? !zip.equals(that.zip) : that.zip != null) return false;
            return country != null ? country.equals(that.country) : that.country == null;
        }

        @Override
        public int hashCode() {
            int result = addr1 != null ? addr1.hashCode() : 0;
            result = 31 * result + (city != null ? city.hashCode() : 0);
            result = 31 * result + (state != null ? state.hashCode() : 0);
            result = 31 * result + (zip != null ? zip.hashCode() : 0);
            result = 31 * result + (country != null ? country.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "StoreAddress{" +
                    "addr1='" + addr1 + '\'' +
                    ", city='" + city + '\'' +
                    ", state='" + state + '\'' +
                    ", zip='" + zip + '\'' +
                    ", country='" + country + '\'' +
                    '}';
        }
    }
}
