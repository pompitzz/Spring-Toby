package me.sun.tobyreview.non_spring.reactive.ch1;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Ex1IterableAndOb {
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);

        Iterable<Integer> iterable = list;

        for (Integer i : iterable) {
            System.out.println(i);
        }

        Iterable<Integer> iter = () -> new Iterator<Integer>() {
            int i = 0;
            final static int MAX = 10;

            @Override
            public boolean hasNext() {
                return i < MAX;
            }

            @Override
            public Integer next() {
                return ++i;
            }
        };

        for (Integer i : iter) {
            System.out.print(i + " -> ");
        }

        for (Iterator<Integer> it = iter.iterator(); it.hasNext(); ) {
            System.out.print(it.next() + " -> ");
        }

        System.out.println("======================== Observer ==============================");

        Observer ob =
                (Observable o, Object arg) -> {
                    if ((int) arg == 1)
                        System.out.println("Start: Thread Name is --> " + Thread.currentThread().getName());
                    System.out.print(arg + " ");
                };
        IntObservable io = new IntObservable();
        io.addObserver(ob);
        io.run();

        ExecutorService es = Executors.newSingleThreadExecutor();
        System.out.println("================= Other Thread ====================");
        es.execute(io);
        es.shutdown();
    }

    static class IntObservable extends Observable implements Runnable{
        @Override
        public void run() {
            System.out.println("IntObservable Start --> " + Thread.currentThread().getName());
               for(int i = 1; i <= 10; i++){
                   setChanged();
                   notifyObservers(i);
               }
        }
    }
}
