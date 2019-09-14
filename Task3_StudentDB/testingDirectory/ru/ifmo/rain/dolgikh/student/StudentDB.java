package ru.ifmo.rain.dolgikh.student;

import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StudentDB implements StudentQuery {

    private <C extends Collection<String>> C getMappedCollection(List<Student> students, Function<Student, String> mapper, Supplier<C> collectionSupplier) {
        return students.stream().map(mapper).collect(Collectors.toCollection(collectionSupplier));
    }

    private List<String> getMappedList(List<Student> students, Function<Student, String> mapper) {
        return getMappedCollection(students, mapper, ArrayList::new);
    }

    private List<Student> getSortedList(Collection<Student> students, Comparator<Student> comparator) {
        return students.stream().sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return getMappedList(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return getMappedList(students, Student::getLastName);
    }

    @Override
    public List<String> getGroups(List<Student> students) {
        return getMappedList(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return getMappedList(students, student -> student.getFirstName() + " " + student.getLastName());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return getMappedCollection(students, Student::getFirstName, TreeSet::new);
    }

    @Override
    public String getMinStudentFirstName(List<Student> students) {
        return students.stream().min(Student::compareTo).map(Student::getFirstName).orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return getSortedList(students, Student::compareTo);
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return getSortedList(students,
                Comparator
                        .comparing(Student::getLastName)
                        .thenComparing(Student::getFirstName)
                        .thenComparing(Student::getId));
    }

    private List<Student> getSortedFilteredList(Collection<Student> students, Predicate<Student> predicate) {
        return sortStudentsByName(students.stream().filter(predicate).collect(Collectors.toList()));
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return getSortedFilteredList(students, student -> student.getFirstName().equals(name));
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return getSortedFilteredList(students, student -> student.getLastName().equals(name));
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, String group) {
        return getSortedFilteredList(students, student -> student.getGroup().equals(group));
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, String group) {
        return findStudentsByGroup(students, group)
                .stream()
                .collect(Collectors.toMap(Student::getLastName, Student::getFirstName, BinaryOperator.minBy(String::compareTo)));
    }
}
