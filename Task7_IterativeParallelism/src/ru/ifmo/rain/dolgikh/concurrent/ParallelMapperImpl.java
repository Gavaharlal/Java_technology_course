package ru.ifmo.rain.dolgikh.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {
    private final BlockingQueue<Runnable> tasks;
    private final List<Thread> workers;
    private final static int MAX_SIZE = 1;

    private class AnswerStorage<R> {
        private final List<R> result;
        private int counter = 0;

        AnswerStorage(int resSize) {
            result = new ArrayList<>(Collections.nCopies(resSize, null));
        }

        synchronized void setAnswer(int pos, R data) {
            result.set(pos, data);
            counter++;
            if (counter == result.size()) {
                notify();
            }
        }

        public synchronized List<R> getAnswer() throws InterruptedException {
            while (counter < result.size()) {
                wait();
            }
            return result;
        }
    }

    public ParallelMapperImpl(final int threads) {
        tasks = new ArrayBlockingQueue<>(MAX_SIZE, true);
        workers = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Thread thread = new Thread(() -> {
                try {
                    while (!Thread.interrupted()) {
                        Runnable task = tasks.take();
                        task.run();
                    }
                } catch (InterruptedException ignored) {
                }
            });
            workers.add(thread);
            thread.start();
        }
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        AnswerStorage<R> answerStorage = new AnswerStorage<>(args.size());
        for (int i = 0; i < args.size(); i++) {
            final int finalI = i;
            tasks.put(() -> answerStorage.setAnswer(finalI, f.apply(args.get(finalI))));
        }
        return answerStorage.getAnswer();
    }


    @Override
    public void close() {
        for (Thread thread : workers) {
            thread.interrupt();
        }
        for (Thread thread : workers) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}