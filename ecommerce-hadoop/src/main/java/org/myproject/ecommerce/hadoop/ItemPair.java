package org.myproject.ecommerce.hadoop;

import java.io.Serializable;
import java.util.Objects;

public class ItemPair implements Serializable {
    private int a;
    private int b;

    public ItemPair() {
    }

    public ItemPair(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public void setA(int a) {
        this.a = a;
    }

    public void setB(int b) {
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemPair that = (ItemPair) o;
        return a == that.a &&
                b == that.b;
    }

    @Override
    public int hashCode() {

        return Objects.hash(a, b);
    }

    @Override
    public String toString() {
        return "ItemPair{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}
