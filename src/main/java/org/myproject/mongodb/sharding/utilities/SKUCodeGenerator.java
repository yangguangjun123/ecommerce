package org.myproject.mongodb.sharding.utilities;

import org.myproject.mongodb.sharding.datamodel.ProductType;

// SKU - Stock Keeping Unit
public class SKUCodeGenerator {
    private static long filmSku = Long.parseLong("00e8da9d", 16);
    private static long audioAlbumSku = Long.parseLong("00e8da9b", 16);
    private static int count = 1;
    private static String UNKNOWN_SKU = Long.toHexString(-1);

    public static String createSKUCode(ProductType productType) {
        if(productType == ProductType.AUDIOALBUM) {
            return String.format("%08x", audioAlbumSku++);
        } else if(productType == ProductType.FILM) {
            return String.format("%08x", filmSku++);
        } else {
            return UNKNOWN_SKU;
        }
    }
}
