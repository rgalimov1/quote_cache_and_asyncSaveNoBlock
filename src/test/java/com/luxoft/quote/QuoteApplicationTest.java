package com.luxoft.quote;

import com.luxoft.quote.domain.Elvl;
import com.luxoft.quote.domain.Quote;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SpringJUnitConfig
class QuoteApplicationTest {

	static WebTestClient client;
    static final int REQUEST_SERIES_SIZE = 100;
	static final int PUT_REQUEST_BATCH_SIZE = 10;

	@BeforeAll
	static void setUp(ApplicationContext context) {
		client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build();
	}

	@Test
	void testAddNewQuote_bidNotEmpty() {

		final String isin = generateRandomString(); // Example: "RU000A0JX0J0";

		Quote quote = new Quote(isin, BigDecimal.valueOf(1003.11f), BigDecimal.valueOf(1002.11f));

			WebTestClient.RequestBodySpec reqBodySpec = client.put().uri("/quote")
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON);

		FluxExchangeResult<Boolean> result = reqBodySpec.bodyValue(quote).exchange().returnResult(Boolean.class);
		Assert.isTrue(result.getStatus().is2xxSuccessful(), "HTTP response status is ERROR.");

		isFoundElvlByISIN(isin);
		isElvlEqualToBid(isin, quote);
	}

	@Test
	void testAddNewQuote_bidEmpty() {

		final String isin = generateRandomString(); // Example: "RU000A0JX0J0";

		Quote quote = new Quote(isin, BigDecimal.valueOf(1005.f),null);

		WebTestClient.RequestBodySpec reqBodySpec = client.put().uri("/quote")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);

		FluxExchangeResult<Integer> result = reqBodySpec.bodyValue(quote).exchange().returnResult(Integer.class);
		Assert.isTrue(result.getStatus().is2xxSuccessful(), "HTTP response status is ERROR.");

		isFoundElvlByISIN(isin);
		isElvlEqualToAsk(isin, quote);
	}

	@Test
	void testAddQuoteExistingISIN_bidGreaterThanElvl() {

		WebTestClient.RequestBodySpec reqBodySpec = client.put().uri("/quote")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);

		final String isin = generateRandomString(); // Example: "RU000A0JX0J0";

		Quote quote = new Quote(isin, BigDecimal.valueOf(1001.f), BigDecimal.valueOf(1000.f));
		FluxExchangeResult<Integer> result = reqBodySpec.bodyValue(quote).exchange().returnResult(Integer.class);
		Assert.isTrue(result.getStatus().is2xxSuccessful(), "HTTP response status is ERROR.");

		isFoundElvlByISIN(isin);

		Quote quote2 = new Quote(isin, BigDecimal.valueOf(1002.f), BigDecimal.valueOf(1001.f));
		FluxExchangeResult<Integer> result2 = reqBodySpec.bodyValue(quote2).exchange().returnResult(Integer.class);
		Assert.isTrue(result2.getStatus().is2xxSuccessful(), "HTTP response status is ERROR.");

		isElvlEqualToBid(isin, quote2);
	}

	@Test
	void testAddQuoteExistingISIN_askLessThanElvl() {

		WebTestClient.RequestBodySpec reqBodySpec = client.put().uri("/quote")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);

		final String isin = generateRandomString(); // Example: "RU000A0JX0J0";

		Quote quote = new Quote(isin, BigDecimal.valueOf(999.f), BigDecimal.valueOf(998.f));
		FluxExchangeResult<Integer> result = reqBodySpec.bodyValue(quote).exchange().returnResult(Integer.class);
		Assert.isTrue(result.getStatus().is2xxSuccessful(), "HTTP response status is ERROR.");

		isFoundElvlByISIN(isin);

		Quote quote2 = new Quote(isin, BigDecimal.valueOf(997.f), BigDecimal.valueOf(996.f));
		FluxExchangeResult<Integer> result2 = reqBodySpec.bodyValue(quote2).exchange().returnResult(Integer.class);
		Assert.isTrue(result2.getStatus().is2xxSuccessful(), "HTTP response status is ERROR.");

		isElvlEqualToAsk(isin, quote2);
	}

	void isFoundElvlByISIN(String isin) {

		EntityExchangeResult<Elvl> result = client.get().uri("/elvls/" + isin)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Elvl.class)
				.returnResult();
		Assert.isTrue(result.getResponseBody().getElvl() != null, "Elvl is empty for isin = ".concat(isin));
	}

	void isElvlEqualToBid(String isin, Quote quote) {

		EntityExchangeResult<Elvl> result = client.get().uri("/elvls/" + isin)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Elvl.class)
				.returnResult();
		Assert.isTrue(quote.getBid() != null && quote.getBid().equals(result.getResponseBody().getElvl()), "Elvl is not equal to bid");
	}

	void isElvlEqualToAsk(String isin, Quote quote) {

		EntityExchangeResult<Elvl> result = client.get().uri("/elvls/" + isin)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Elvl.class)
				.returnResult();
		Assert.isTrue(quote.getAsk() != null && quote.getAsk().equals(result.getResponseBody().getElvl()), "Elvl is not equal to ask");
	}

	public String generateRandomString() {

		String generatedString = "RU".concat(Integer.toUnsignedString(new Random().nextInt()));
		if (generatedString.length() < 12) {
			generatedString = generatedString.concat("000000000").substring(0, 12);
		}
		return generatedString;
	}

	@Test
	void testSerialPutRequests() {

		WebTestClient.RequestBodySpec putRequestBodySpec = client.put().uri("/quote")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);

        // Generate Quote[]
		Random random = new Random();
		Quote[] quotes = new Quote[REQUEST_SERIES_SIZE];
		for(int i = 0; i < REQUEST_SERIES_SIZE; i++) {
			quotes[i] = new Quote(generateRandomString(), BigDecimal.valueOf(random.nextFloat()), BigDecimal.valueOf(random.nextFloat()));
		}

		long startTime = System.currentTimeMillis();

		// Send serial "/quote" PUT requests
		for(Quote quote: quotes) {
			FluxExchangeResult<Integer> result = putRequestBodySpec.bodyValue(quote).exchange().returnResult(Integer.class);
			Assert.isTrue(result.getStatus().is2xxSuccessful(), "HTTP response status is ERROR.");
		}

		System.out.println("_spent(puts) =" + (System.currentTimeMillis() - startTime) + "[ms]");
	}

	@Test
	void testSerialMixedRequests() {

		WebTestClient.RequestBodySpec putRequestBodySpec = client.put().uri("/quote")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);

		// Generate Quote[]
		Random random = new Random();
		Quote[] quotes = new Quote[REQUEST_SERIES_SIZE/2];
		for(int i = 0; i < REQUEST_SERIES_SIZE/2; i++) {
			quotes[i] = new Quote(generateRandomString(), BigDecimal.valueOf(random.nextFloat()), BigDecimal.valueOf(random.nextFloat()));
		}

		long startTime = System.currentTimeMillis();

		// Send serial "/quote" PUT requests, mixed with serial "/elvls/RU000A0JX0J7" GET requests
		for(Quote quote: quotes) {

			FluxExchangeResult<Integer> result = putRequestBodySpec.bodyValue(quote)
					.exchange()
					.returnResult(Integer.class);
			Assert.isTrue(result.getStatus().is2xxSuccessful(), "HTTP response status is ERROR.");

			client.get().uri("/elvls/RU000A0JX0J7")
					.exchange()
					.expectStatus().isOk();
		}

		System.out.println("_spent(mixed) = " + (System.currentTimeMillis() - startTime) + " [ms]");
	}

	@Test
	void testPutSeveralQuotesInOneRequest() {

		Random random = new Random();

		WebTestClient.RequestBodySpec putRequestBodySpec = client.put().uri("/quotes")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);

		// Generate Quote[]
		Quote[] quotes = new Quote[PUT_REQUEST_BATCH_SIZE];
		for(int i = 0; i < PUT_REQUEST_BATCH_SIZE; i++) {
			quotes[i] = new Quote(generateRandomString(), BigDecimal.valueOf(random.nextFloat()), BigDecimal.valueOf(random.nextFloat()));
		}

		long startTime = System.currentTimeMillis();

		// Send "/quotes"(several Quotes) PUT request
		List<Quote> list = Arrays.asList(quotes);
		putRequestBodySpec.bodyValue(list).exchange().returnResult(Integer.class);

		System.out.println("_spent(1 batch put) = " + (System.currentTimeMillis() - startTime) + "[ms]");
	}
}