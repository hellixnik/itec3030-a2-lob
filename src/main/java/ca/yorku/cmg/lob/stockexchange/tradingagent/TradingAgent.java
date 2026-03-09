package ca.yorku.cmg.lob.stockexchange.tradingagent;

import ca.yorku.cmg.lob.stockexchange.StockExchange;
import ca.yorku.cmg.lob.stockexchange.events.Event;
import ca.yorku.cmg.lob.stockexchange.events.NewsBoard;
import ca.yorku.cmg.lob.trader.Trader;

/**
 * An trading agent that receives news and reacts by submitting ask or bid orders.
 */
public abstract class TradingAgent implements INewsObserver { // 1. Implement interface
    protected Trader t;
    protected StockExchange exc;
    protected NewsBoard news;
    protected ITradingStrategy strategy;
    
    public TradingAgent(Trader t, StockExchange e, NewsBoard n, ITradingStrategy strategy) {
        this.t = t;
        this.exc = e;
        this.news = n;
        this.strategy = strategy;
        
        // 2. Register for notifications at object creation time
        this.news.registerObserver(this);
    }
    
    // 3. Implement the update method from INewsObserver
    @Override
    public void update(Event e) {
        examineEvent(e);
    }

    public void timeAdvancedTo(long time) {
        pollForEvents(time);
    }

    /**
     * Examine if an event is relevant for the Agent.
     */
    protected void examineEvent(Event e) { // Changed to protected/public so update() can use it
        int positionInSecurity = exc.getAccounts().getTraderAccount(t).getPosition(e.getSecrity().getTicker());
        if (positionInSecurity > 0) {
            actOnEvent(e, positionInSecurity, exc.getPrice(e.getSecrity().getTicker()));
        }
    }

    private void pollForEvents(long time) {
        Event e = news.getEventAt(time);
        if (e != null) {
            examineEvent(e);
        }
    }
    
    protected void actOnEvent(Event e, int pos, int price) {
        if (this.strategy != null) {
            this.strategy.actOnEvent(e, pos, price);
        }
    }
}