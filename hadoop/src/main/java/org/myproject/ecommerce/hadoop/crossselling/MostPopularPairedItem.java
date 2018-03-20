package org.myproject.ecommerce.hadoop.crossselling;

public class MostPopularPairedItem {
    private String itemId;
    private int count;
    private int weight;

    public MostPopularPairedItem() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

}
