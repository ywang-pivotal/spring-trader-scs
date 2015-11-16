package io.pivotal.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("service")
@Component
public class ServiceProperties {

    public static final String QUOTE_SERVICE = "quote-service";
    public static final String ACCOUNT_SERVICE = "account-service";
    public static final String PORTFOLIO_SERVICE = "portfolio-service";

    private String _protocol = "https://";
    private String _quoteService = QUOTE_SERVICE;
    private String _accountService = ACCOUNT_SERVICE;
    private String _portfolioService = PORTFOLIO_SERVICE;

    public String getProtocol() {
        return _protocol;
    }

    public void setProtocol(String protocol) {
        _protocol = protocol;
    }

    public String getQuoteService() {
        return _quoteService;
    }

    public void setQuoteService(String quoteService) {
        _quoteService = quoteService;
    }

    public String getAccountService() {
        return _accountService;
    }

    public void setAccountService(String accountService) {
        _accountService = accountService;
    }

    public String getPortfolioService() {
        return _portfolioService;
    }

    public void setPortfolioService(String portfolioService) {
        _portfolioService = portfolioService;
    }
}
