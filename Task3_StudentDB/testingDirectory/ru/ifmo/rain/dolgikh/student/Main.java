package ru.ifmo.rain.dolgikh.student;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        Stream.of(Arrays.asList("one", "two", "three", "four")).flatMap(Collection::stream).map(String::toUpperCase).forEach(System.out::println);
    }
}
