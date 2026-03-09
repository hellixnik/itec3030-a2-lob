package ca.yorku.cmg.lob.stockexchange.events;

import java.util.ArrayList;
import java.util.List;
import ca.yorku.cmg.lob.stockexchange.tradingagent.INewsObserver;

/**
 * The Subject class manages a list of observers and notifies them of news events.
 */
public abstract class Subject {
    private List<INewsObserver> observers = new ArrayList<>();

    public void registerObserver(INewsObserver o) {
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    public void removeObserver(INewsObserver o) {
        observers.remove(o);
    }

    public void notifyObservers(Event e) {
        for (INewsObserver observer : observers) {
            observer.update(e);
        }
    }
}