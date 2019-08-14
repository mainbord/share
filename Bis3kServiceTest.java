package ru.rt.eip.boot.lkb2b.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class Bis3kServiceTest {

    private static final BlockingQueue queue =  new ArrayBlockingQueue(10);


    @Test
    @SneakyThrows
    void testLimitRequestBlockingQueue() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);

        for (int i = 1; i <= 50; i++) {
            TaskQueue task = new TaskQueue("Task " + i);
            System.out.println("Created : " + task.getName());
//            TimeUnit.SECONDS.sleep(1);
            executor.execute(task);
        }
        executor.awaitTermination(50, TimeUnit.SECONDS);
//        while (!executor.isTerminated()){
//            System.out.println("waiting terminating");
//            TimeUnit.SECONDS.sleep(3);
//        }
        System.out.println("end");
        System.out.println("queue.size() = " + queue.size());
    }

    class TaskQueue implements Runnable {
        private String name;

        public TaskQueue(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void run() {
            try {
                Long duration = (long) (Math.random() * 10);
                queue.put(new Object());

                testRequest(name);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    queue.take();
                } catch (InterruptedException e){

                }
            }
        }
    }

    private static final AtomicInteger sessionCounter = new AtomicInteger();

    @Test
    @SneakyThrows
    void testLimitRequestCounter() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);

        for (int i = 1; i <= 50; i++) {
            TaskCounter task = new TaskCounter("Task " + i);
            System.out.println("Created : " + task.getName());
//            TimeUnit.SECONDS.sleep(1);
            executor.execute(task);
        }
        executor.awaitTermination(40, TimeUnit.SECONDS);
        System.out.println("end");
        System.out.println("sessionCounter current value = " + sessionCounter.get());
    }

    class TaskCounter implements Runnable {
        private String name;

        public TaskCounter(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @SneakyThrows
        public void run() {
            try {
                Long duration = (long) (Math.random() * 10);
                while (sessionCounter.getAndAdd(0) > 10){
                    TimeUnit.SECONDS.sleep(1);
                }
                sessionCounter.getAndIncrement();
                testRequest(name);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                sessionCounter.decrementAndGet();
            }
        }
    }

    @SneakyThrows
    private void testRequest(String name) {
        System.out.println("Executing : " + name);
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @SneakyThrows
    void testLimitRequestSynchronized() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);

        for (int i = 1; i <= 30; i++) {
            Task task = new Task("Task " + i);
            System.out.println("Created : " + task.getName());

            executor.execute(task);
        }
        System.out.println("test");
        executor.awaitTermination(60, TimeUnit.SECONDS);
//        while (!executor.isTerminated()){
//            TimeUnit.SECONDS.sleep(1);
//        }
    }

    class Task implements Runnable {
        private String name;

        public Task(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void run() {
            try {
                Long duration = (long) (Math.random() * 10);
                testRequestSynchronized(name);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    private synchronized void testRequestSynchronized(String name) {
        System.out.println("Executing : " + name);
        TimeUnit.SECONDS.sleep(1);
    }


}
