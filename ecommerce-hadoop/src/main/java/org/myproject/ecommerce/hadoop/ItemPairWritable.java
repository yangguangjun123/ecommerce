package org.myproject.ecommerce.hadoop;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ItemPairWritable implements WritableComparable<ItemPairWritable> {
    private int a;
    private int b;

    public ItemPairWritable() {
    }

    public ItemPairWritable(int a, int b) {
        setPair(a, b);
    }

    public void setPair(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(a);
        out.writeInt(b);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        a = in.readInt();
        b = in.readInt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemPairWritable that = (ItemPairWritable) o;

        if (a != that.a) return false;
        return b == that.b;
    }

    @Override
    public int hashCode() {
        int result = a;
        result = 31 * result + b;
        return result;
    }

    // Compares two ItemPairWritable
    public int compareTo(ItemPairWritable o) {
        return (this.a < o.a ? -1 : (this.a > o.a ? 1 :
                this.b < o.b ? -1 : (this.b > o.b ? 1 : 0)));
    }

    @Override
    public String toString() {
        return "ItemPairWritable{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}
