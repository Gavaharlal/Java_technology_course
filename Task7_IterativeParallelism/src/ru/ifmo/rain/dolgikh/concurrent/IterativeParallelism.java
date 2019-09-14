package ru.ifmo.rain.dolgikh.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ListIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IterativeParallelism implements ListIP {

    private final ParallelMapper parallelMapper;

    public IterativeParallelism(ParallelMapper mapper) {
        this.parallelMapper = mapper;
    }

    public IterativeParallelism() {
        parallelMapper = null;
    }

    private <T, E, F> F performParalleling(int threadsNum, List<? extends T> values, Function<Stream<? extends T>, E> reductionFunction, Function<Stream<? extends E>, F> resultFunction) throws InterruptedException {

        if (values == null) {
            throw new NoSuchElementException();
        }

        threadsNum = Math.min(threadsNum, values.size());
        List<Stream<? extends T>> sublistStreams = new ArrayList<>();

        int size = values.size() / threadsNum;
        int rest = values.size() % threadsNum;
        int left, right = 0;

        for (int i = 0; i < threadsNum; i++) {
            left = right;
            right += size;
            if (rest > 0) {
                rest--;
                right++;
            }
            sublistStreams.add(values.subList(left, right).stream());
        }

        List<E> answers;
        if (parallelMapper != null) {
            answers = parallelMapper.map(reductionFunction, sublistStreams);
        } else {

            Collection<Thread> threadPool = new LinkedList<>();
            answers = new ArrayList<>(Collections.nCopies(threadsNum, null));

//            for (int i = 0; i < threadsNum; i++) {
//                answers.add(null);
//            }

            for (int i = 0; i < threadsNum; i++) {
                int thread = i;
                Thread curThread = new Thread(() -> {
                    E result = reductionFunction.apply(sublistStreams.get(thread));
                    answers.set(thread, result);
//                    answers.add(result);
                });
                threadPool.add(curThread);
                curThread.start();
            }

            for (Thread thread : threadPool) {
                thread.join();
            }

        }

        return resultFunction.apply(answers.stream());
    }


    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return performParalleling(
                threads,
                values,
                s -> s.max(comparator).get(),
                s -> s.max(comparator).get()
        );
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return maximum(threads, values, comparator.reversed());
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return performParalleling(
                threads,
                values,
                stream -> stream.allMatch(predicate),
                stream -> stream.allMatch(a -> a)
        );
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return performParalleling(
                threads,
                values,
                stream -> stream.anyMatch(predicate),
                stream -> stream.anyMatch(a -> a)
        );
    }

    @Override
    public String join(int threads, List<?> values) throws InterruptedException {
        return performParalleling(
                threads,
                values,
                stream -> stream.map(String::valueOf).reduce(String::concat).get(),
                stream -> stream.collect(Collectors.joining())
        );
    }

    @Override
    public <T> List<T> filter(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return performParalleling(
                threads,
                values,
                stream -> stream.filter(predicate).collect(Collectors.toList()),
                stream -> stream.flatMap(Collection::stream).collect(Collectors.toList())
        );
    }

    @Override
    public <T, U> List<U> map(int threads, List<? extends T> values, Function<? super T, ? extends U> f) throws InterruptedException {
        return performParalleling(
                threads,
                values,
                stream -> stream.map(f).collect(Collectors.toList()),
                stream -> stream.flatMap(Collection::stream).collect(Collectors.toList())
        );
    }
}