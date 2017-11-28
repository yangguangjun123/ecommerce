package org.myproject.ecommerce.domain;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.List;

public class ProductCategory {
    @BsonId
    private ObjectId id;

    private String name;
    private ObjectId parent;
    private String slug;
    private List<Ancestor> ancestors;

    public ProductCategory() {
    }

    public ProductCategory(String name, ObjectId parent, String slug) {
        this.name = name;
        this.parent = parent;
        this.slug = slug;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectId getParent() {
        return parent;
    }

    public void setParent(ObjectId parent) {
        this.parent = parent;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public List<Ancestor> getAncestors() {
        return ancestors;
    }

    public void setAncestors(List<Ancestor> ancestors) {
        this.ancestors = ancestors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductCategory that = (ProductCategory) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        if (slug != null ? !slug.equals(that.slug) : that.slug != null) return false;
        return ancestors != null ? ancestors.equals(that.ancestors) : that.ancestors == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (slug != null ? slug.hashCode() : 0);
        result = 31 * result + (ancestors != null ? ancestors.hashCode() : 0);
        return result;
    }

    public static class Ancestor {
        private ObjectId id;
        private String slug;
        private String name;

        public Ancestor() {
        }

        public Ancestor(ObjectId id, String slug, String name) {
            this.id = id;
            this.slug = slug;
            this.name = name;
        }

        public ObjectId getId() {
            return id;
        }

        public void setId(ObjectId id) {
            this.id = id;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Ancestor ancestor = (Ancestor) o;

            if (id != null ? !id.equals(ancestor.id) : ancestor.id != null) return false;
            if (slug != null ? !slug.equals(ancestor.slug) : ancestor.slug != null) return false;
            return name != null ? name.equals(ancestor.name) : ancestor.name == null;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (slug != null ? slug.hashCode() : 0);
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }
}
