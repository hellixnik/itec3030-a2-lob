package ca.yorku.cmg.lob.stockexchange.events;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import ca.yorku.cmg.lob.security.Security;
import ca.yorku.cmg.lob.security.SecurityList;

public class NewsBoard extends Subject { // 1. Extend Subject

    PriorityQueue<Event> eventQueue = new PriorityQueue<>((e1, e2) -> Long.compare(e1.getTime(), e2.getTime()));
    SecurityList securities;
    
    public NewsBoard(SecurityList x) {
        this.securities = x;
    }
    
    private static final Set<String> VALID_EVENTS = new HashSet<>(Arrays.asList("Good", "Bad"));
    
    public void loadEvents(String filePath) {
        String line;
        String delimiter = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                if (values.length != 3) continue;

                String time = values[0].trim();
                String ticker = values[1].trim();
                String event = values[2].trim();

                if (!VALID_EVENTS.contains(event)) continue;
                
                Security s = securities.getSecurityByTicker(ticker);
                if (s == null) continue;
                
                Event eventObj;
                switch (event) {
                    case "Good": eventObj = new GoodNews(Long.parseLong(time), s); break;
                    case "Bad": eventObj = new BadNews(Long.parseLong(time), s); break;
                    default: throw new IllegalArgumentException("Unexpected event value: " + event);
                }
                eventQueue.add(eventObj);
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    public Event getEventAt(long time) {
        PriorityQueue<Event> clonedQueue = new PriorityQueue<>(eventQueue);
        while (!clonedQueue.isEmpty()) {
            long next = clonedQueue.peek().getTime();
            if (time > next) { clonedQueue.poll(); }
            else if (time < next) { return null; }
            else { return clonedQueue.poll(); }
        }
        return null;
    }

    /**
     * 2. Implementation of the Observer pattern's push model.
     * Polls the queue and notifies registered observers.
     */
    public void runEventsList() {
        while (!eventQueue.isEmpty()) {
            Event e = eventQueue.poll();
            notifyObservers(e);
        }
    }
}