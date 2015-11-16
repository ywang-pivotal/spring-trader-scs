# Spring Trader: Spring Cloud Services Edition

This project allows you to quickly deploy the Spring Trader application, which consists of four collaborating microservices, into a Pivotal Cloud Foundry environment running the Spring Cloud Services tiles.

#### Setting up PCF

In your PCF environment, you will want to define these services:
**traderdb**: MySql database used by the account and portfolio services
**registry**: Spring Cloud Services Service Registry used by all services
**breaker**: Spring Cloud Services Circuit Breaker used by the quote service

![PCF Services](https://c2.staticflickr.com/6/5707/23064069145_9189ee742f_b.jpg)

If you decide to change the default service names provided, you'll want to update the associated manifest.yml files for each service.

#### Building and Deploying

For initial setup, build and deploy the services in the following order:
- **quote-service**
- **account-service** (depends on quote)
- **portfolio-service** (depends on quote and account)
- **trader-web** (depends on all other services)

For each service, cd into the service's base directory (trader-quots, trader-accounts, etc.) and perform the following steps:

1. mvn package
2. In the manifest.yml file, the CF_TARGET environment variable points to the PEZ Heritage environment by default. If you want to target a different environment, update the variable.
3. [cf push](http://www.vevo.com/watch/salt-n-pepa/Push-It/USIV30400109)

#### Notes

To view the Service Registry, go to Apps Manager and click the 'Manage' link under the registry service.
![Service Registry](https://c1.staticflickr.com/1/608/22645938608_33e24f79b2_o.png)


The quote-service uses a free third party API called markitondemand. The free version of this service is pretty crappy! It frequently fails with 501 errors and stuff. This actually works out ok because it demonstrates the circuit breaker functionality. If you see a stock price of 0, or a company name of Error, then you know that the circuit breaker was triggered, and you can view the activity on the hystrix dashboard.

![Circuit Breaker](https://c2.staticflickr.com/6/5682/23075520041_d6afc6bd55_o.png)

Config Server support is coming soon!


