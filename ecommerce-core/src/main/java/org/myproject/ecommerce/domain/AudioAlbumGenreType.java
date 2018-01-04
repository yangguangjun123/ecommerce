package org.myproject.ecommerce.domain;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum AudioAlbumGenreType {
    GENEAL("General"), JAZZ("Jazz");

    private String type;

    private static final Map<String, AudioAlbumGenreType> MAP =
            Stream.of(AudioAlbumGenreType.values())
                  .collect(Collectors.toMap(AudioAlbumGenreType::toString, Function.identity()));

    AudioAlbumGenreType(String type) {
        this.type = type;
    }

    public AudioAlbumGenreType fromValue(String value) {
        return MAP.get(value);
    }

    @Override
    public String toString() {
        return type;
    }

}
