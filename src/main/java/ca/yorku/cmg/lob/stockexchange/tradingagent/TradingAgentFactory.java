package ca.yorku.cmg.lob.stockexchange.tradingagent;

import ca.yorku.cmg.lob.stockexchange.StockExchange;
import ca.yorku.cmg.lob.stockexchange.events.NewsBoard;
import ca.yorku.cmg.lob.trader.Trader;

public class TradingAgentFactory extends AbstractTradingAgentFactory {

    @Override
    public TradingAgent createAgent(String type, String style, Trader t, StockExchange e, NewsBoard n) {
        ITradingStrategy strategy;
        
        if ("Aggressive".equalsIgnoreCase(style)) {
            strategy = new StrategyAggressive(t, e);
        } else if ("Conservative".equalsIgnoreCase(style)) {
            strategy = new StrategyConservative(t, e);
        } else {
            throw new IllegalArgumentException("Unknown trading style: " + style);
        }

        if ("Institutional".equalsIgnoreCase(type)) {
            return new TradingAgentInstitutional(t, e, n, strategy);
        } else if ("Retail".equalsIgnoreCase(type)) {
            return new TradingAgentRetail(t, e, n, strategy);
        } else {
            throw new IllegalArgumentException("Unknown trading agent type: " + type);
        }
    }
}