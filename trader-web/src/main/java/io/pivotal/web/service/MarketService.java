package io.pivotal.web.service;

import io.pivotal.web.config.ServiceProperties;
import io.pivotal.web.domain.*;
import io.pivotal.web.exception.OrderNotSavedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableScheduling
@RefreshScope
public class MarketService {
	private static final Logger logger = LoggerFactory
			.getLogger(MarketService.class);
	private final static Integer QUOTES_NUMBER = 3;

	private final static long REFRESH_PERIOD = 120000L;
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
    private ServiceProperties serviceProperties;

    @Value("${pivotal.app.symbols.it:EMC,IBM,VMW}")
	private String symbolsIT;
    @Value("${pivotal.app.symbols.fs:JPM,C,MS}")
	private String symbolsFS;
	
	private MarketSummary summary = new MarketSummary();
	
	public MarketSummary getMarketSummary() {
		logger.debug("Retrieving Market Summary");
		
		return summary;
	}
	
	public Quote getQuote(String symbol) {
		logger.info("Fetching quote: " + symbol);

		Quote quote = restTemplate.getForObject( serviceProperties.getProtocol() +
                serviceProperties.getQuoteService() + "/quote/{symbol}", Quote.class, symbol);
		return quote;
	}

	public List<CompanyInfo> getCompanies(String name) {
		logger.debug("Fetching companies with name or symbol matching: " + name);

		CompanyInfo[] infos = restTemplate.getForObject( serviceProperties.getProtocol() +
                serviceProperties.getQuoteService() + "/company/{name}", CompanyInfo[].class, name);
		return Arrays.asList(infos);
	}

	public Order sendOrder(Order order ) throws OrderNotSavedException{
		logger.debug("send order: " + order);
		
		//check result of http request to ensure its ok.
		
		ResponseEntity<Order>  result = restTemplate.postForEntity( serviceProperties.getProtocol() +
                serviceProperties.getPortfolioService() + "/portfolio/{accountId}", order, Order.class, order.getAccountId());
		if (result.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			throw new OrderNotSavedException("Could not save the order");
		}
		logger.debug("Order saved:: " + result.getBody());
		return result.getBody();
	}
	
	public Portfolio getPortfolio(String accountId) {
		Portfolio folio = restTemplate.getForObject( serviceProperties.getProtocol() +
                serviceProperties.getPortfolioService() + "/portfolio/{accountid}", Portfolio.class, accountId);
		logger.debug("Portfolio received: " + folio);
		return folio;
	}
	
	//TODO: prime location for a redis/gemfire caching service!
	@Scheduled(fixedRate = REFRESH_PERIOD)
	protected void retrieveMarketSummary() {
		logger.debug("Scheduled retrieval of Market Summary");

        logger.debug("IT symbols: " + symbolsIT);
        List<String> itSymbols = pickRandomThree(Arrays.asList(symbolsIT.split(",")));
        List<Quote> itQuotes = itSymbols.stream().map(this::getQuote).collect(Collectors.toList());
        summary.setTopGainers( itQuotes );

        logger.debug("FS symbols: " + symbolsFS);
        List<String> fsSymbols = pickRandomThree(Arrays.asList(symbolsFS.split(",")));
        List<Quote> fsQuotes = fsSymbols.stream().map(this::getQuote).collect(Collectors.toList());
        summary.setTopLosers(fsQuotes);
	}

	private List<String> pickRandomThree(List<String> symbols) {
		Collections.shuffle(symbols);
        return symbols.subList(0, QUOTES_NUMBER);
	}
}
