package ru.bloodmine.cursedtree.util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public enum RandomHashGenerator {;
    private static final String SYMBOLS_ID = "abcdefghijklmnopqrstuvwxyz1234567890";

    public static String generate() {
        return ThreadLocalRandom.current().ints(4, 0, SYMBOLS_ID.length())
                .mapToObj(SYMBOLS_ID::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }
}
