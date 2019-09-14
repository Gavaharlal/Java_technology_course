package ru.ifmo.rain.dolgikh.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;

public class WebCrawler implements Crawler {

    private final Downloader downloader;
    private final int hostMax;

    private final ExecutorService downloadersPool;
    private final ExecutorService extractorsPool;
    private final ConcurrentMap<String, HostController> hostToController;

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int hostMax) {
        this.downloader = downloader;
        this.hostMax = hostMax;
        downloadersPool = Executors.newFixedThreadPool(downloaders);
        extractorsPool = Executors.newFixedThreadPool(extractors);
        hostToController = new ConcurrentHashMap<>();
    }

    public WebCrawler(int downloaders, int extractors, int hostMax) throws IOException {
        this(new CachingDownloader(), downloaders, extractors, hostMax);
    }

    private class HostController {

        private final Queue<Runnable> tasksQueue = new ArrayDeque<>();
        private int nowRunning = 0;

        private synchronized void submitTask(Runnable task) {
            if (nowRunning < hostMax) {
                downloadersPool.submit(constructTask(task));
                nowRunning++;
            } else {
                tasksQueue.add(constructTask(task));
            }
        }

        private Runnable constructTask(Runnable task) {
            return () -> {
                task.run();
                if (!tasksQueue.isEmpty()) {
                    Runnable nextTask = tasksQueue.poll();
                    downloadersPool.submit(nextTask);
                } else {
                    nowRunning--;
                }
            };
        }

    }

    @Override
    public Result download(String url, int depth) {
        Set<String> used = ConcurrentHashMap.newKeySet();
        Phaser phaser = new Phaser();
        Set<String> success = ConcurrentHashMap.newKeySet();
        ConcurrentMap<String, IOException> failure = new ConcurrentHashMap<>();

        used.add(url);
        phaser.register();
        loadExecutors(url, depth, used, phaser, success, failure);
        phaser.arriveAndAwaitAdvance();

        return new Result(new ArrayList<>(success), failure);
    }

    private void loadExecutors(String url, int depth, Set<String> used, Phaser phaser, Set<String> success, ConcurrentMap<String, IOException> failure) {
        try {
            String host = URLUtils.getHost(url);
            HostController hostController = hostToController.computeIfAbsent(host, s -> new HostController());

            phaser.register();
            hostController.submitTask(() -> {
                try {
                    Document curPage = downloader.download(url);
                    success.add(url);
                    if (depth > 1) {
                        phaser.register();
                        extractorsPool.submit(() -> {
                            try {
                                curPage.extractLinks().forEach(link -> {
                                    if (used.add(link)) {
                                        loadExecutors(link, depth, used, phaser, success, failure);
                                    }
                                });
                            } catch (IOException ignored) {
                            } finally {
                                phaser.arrive();
                            }
                        });
                    }
                } catch (IOException e) {
                    failure.put(url, e);
                } finally {
                    phaser.arrive();
                }
            });
        } catch (MalformedURLException e) {
            failure.put(url, e);
        }
    }

    @Override
    public void close() {
        downloadersPool.shutdownNow();
        extractorsPool.shutdownNow();
    }

    public static void main(String[] args) {
        if (args == null || args.length != 5) {
            System.out.println("5 arguments expected");
            return;
        }

        int[] crawlerArguments = new int[3];
        try {
            for (int i = 0; i < 3; i++) {
                crawlerArguments[i] = Integer.parseInt(args[i + 2]);
            }
        } catch (NumberFormatException e) {
            System.out.println("Integer numbers expected: " + e.getMessage());
            return;
        } catch (NullPointerException e) {
            System.out.println("Non-null arguments expected: " + e.getMessage());
            return;
        }

        try (WebCrawler crawler = new WebCrawler(crawlerArguments[0], crawlerArguments[1], crawlerArguments[2])) {
            crawler.download(args[0], Integer.parseInt(args[1]));
        } catch (IOException e) {
            System.out.println("Error creating CachingDownloader " + e.getMessage());
        }

    }
}
