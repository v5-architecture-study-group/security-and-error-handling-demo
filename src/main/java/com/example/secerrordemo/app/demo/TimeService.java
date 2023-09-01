package com.example.secerrordemo.app.demo;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
public class TimeService {

    private static final Logger log = LoggerFactory.getLogger(TimeService.class);
    private final ScheduledExecutorService executorService;
    private final WeakHashMap<Consumer<Instant>, Void> subscribers = new WeakHashMap<>();
    private final Clock clock;

    public TimeService(Clock clock) {
        this.clock = clock;
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(this::notifySubscribers, 1, 1, TimeUnit.SECONDS);
    }

    private void notifySubscribers() {
        // If this method throws an exception, the executor service will stop calling it until the application is restarted,
        // and so we should make sure that never happens.
        Set<Consumer<Instant>> subscribers;
        synchronized (this.subscribers) {
            subscribers = Set.copyOf(this.subscribers.keySet());
        }
        var now = clock.instant();
        log.trace("Notifying {} subscribers", subscribers.size());
        subscribers.forEach(subscriber -> {
            try {
                subscriber.accept(now);
            } catch (Throwable ex) {
                log.error("Subscriber threw an exception, logging and continuing", ex);
            }
        });
    }

    @PreDestroy
    void destroy() {
        executorService.shutdown();
    }

    public void subscribeToTimeUpdates(@Nonnull Consumer<Instant> subscriber) {
        synchronized (subscribers) {
            subscribers.put(subscriber, null);
        }
    }
}
