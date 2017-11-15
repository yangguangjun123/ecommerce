package org.myproject.ecommerce.datamodel;

public class Shipping {
    private int weight = 6;
    private Dimensions dimensions;

    public Shipping() {
    }

    public Shipping(int weight, Dimensions dimensions) {
        this.weight = weight;
        this.dimensions = dimensions;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Dimensions getDimensions() {
        return dimensions;
    }

    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shipping shipping = (Shipping) o;

        if (weight != shipping.weight) return false;
        return dimensions != null ? dimensions.equals(shipping.dimensions) : shipping.dimensions == null;
    }

    @Override
    public int hashCode() {
        int result = weight;
        result = 31 * result + (dimensions != null ? dimensions.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Shipping{" +
                "weight=" + weight +
                ", dimensions=" + dimensions +
                '}';
    }

    public static class Dimensions {
        int width = 10;
        int height = 10;
        int depth = 1;

        public Dimensions() {
        }

        public Dimensions(int width, int height,
                          int depth) {
            this.width = width;
            this.height = height;
            this.depth = depth;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getDepth() {
            return depth;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Dimensions that = (Dimensions) o;

            if (width != that.width) return false;
            if (height != that.height) return false;
            return depth == that.depth;
        }

        @Override
        public int hashCode() {
            int result = width;
            result = 31 * result + height;
            result = 31 * result + depth;
            return result;
        }

        @Override
        public String toString() {
            return "Dimensions{" +
                    "width=" + width +
                    ", height=" + height +
                    ", depth=" + depth +
                    '}';
        }
    }
}