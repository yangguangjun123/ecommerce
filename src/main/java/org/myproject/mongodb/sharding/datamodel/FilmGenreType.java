package org.myproject.mongodb.sharding.datamodel;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum FilmGenreType {
    THRILLER("Thriller"), SCIENCE_FICTION("Science Fiction"), ACTION_ADVENTURE("Action & Adventure");

    private String type;

    private static final Map<String, FilmGenreType> MAP =
            Stream.of(FilmGenreType.values())
                  .collect(Collectors.toMap(FilmGenreType::toString, Function.identity()));

    FilmGenreType(String type) {
        this.type = type;
    }

    public static FilmGenreType fromValue(String value) {
        return MAP.get(value);
    }

    @Override
    public String toString() {
        return type;
    }
}
