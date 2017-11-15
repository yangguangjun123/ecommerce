package org.myproject.ecommerce.datamodel;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class Pricing {
    private int list;
    private int retail;
    private int savings;

    @BsonProperty(value = "pct_savings")
    int pctSavings;

    public Pricing() {
    }

    public Pricing(int list, int retail,
                   int savings, int pctSavings) {
        this.list = list;
        this.retail = retail;
        this.savings = savings;
        this.pctSavings = pctSavings;
    }

    public int getList() {
        return list;
    }

    public void setList(int list) {
        this.list = list;
    }

    public int getRetail() {
        return retail;
    }

    public void setRetail(int retail) {
        this.retail = retail;
    }

    public int getSavings() {
        return savings;
    }

    public void setSavings(int savings) {
        this.savings = savings;
    }

    public int getPctSavings() {
        return pctSavings;
    }

    public void setPctSavings(int pctSavings) {
        this.pctSavings = pctSavings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pricing pricing = (Pricing) o;

        if (list != pricing.list) return false;
        if (retail != pricing.retail) return false;
        if (savings != pricing.savings) return false;
        return pctSavings == pricing.pctSavings;
    }

    @Override
    public int hashCode() {
        int result = list;
        result = 31 * result + retail;
        result = 31 * result + savings;
        result = 31 * result + pctSavings;
        return result;
    }

    @Override
    public String toString() {
        return "Pricing{" +
                "list=" + list +
                ", retail=" + retail +
                ", savings=" + savings +
                ", pctSavings=" + pctSavings +
                '}';
    }
}
