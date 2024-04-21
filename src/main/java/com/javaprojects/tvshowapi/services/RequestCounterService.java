package com.javaprojects.tvshowapi.services;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RequestCounterService {
    AtomicInteger requestCounter = new AtomicInteger(0);

    public void increment() {
        requestCounter.incrementAndGet();
    }

    public int get() {
        return requestCounter.get();
    }

    public void reset() {
        requestCounter.set(0);
    }
}
