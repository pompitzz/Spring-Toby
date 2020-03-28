package me.sun.tobyreview.non_spring.reactive.ch2;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Iterable<Integer> iterable = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        Publisher<Integer> pub = new Ex2MyPub(iterable);
        Subscriber<Integer> sub = new Ex2MySub2();

        pub.subscribe(sub);
    }
}
