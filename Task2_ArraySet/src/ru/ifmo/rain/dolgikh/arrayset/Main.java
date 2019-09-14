package ru.ifmo.rain.dolgikh.arrayset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

public class Main {

    public static void main(String[] args) {
        ArrayList<Integer> ints = new ArrayList<>();
        ints.add(699760245);
        ArraySet<Integer> integers = new ArraySet<>(ints, Comparator.<Integer>comparingInt((i) -> (i / 100)));
        System.out.println(integers.headSet(1906815160));
    }
}
