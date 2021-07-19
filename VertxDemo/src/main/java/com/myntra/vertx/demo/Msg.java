package com.myntra.vertx.demo;
import java.util.concurrent.atomic.AtomicInteger;
public class Msg {
    private static final AtomicInteger COUNTER = new AtomicInteger();
    private final int id;
    private String text;
    private String destination;
    public Msg(String text, String destination) {
        this.id = COUNTER.getAndIncrement();
        this.text = text;
        this.destination = destination;
    }
    public Msg() {
        this.id = COUNTER.getAndIncrement();
    }
    public String getText() {
        return text;
    }
    public String getDestination() {
        return destination;
    }
    public int getId() {
        return id;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }
}