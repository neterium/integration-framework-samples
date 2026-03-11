package com.neterium.client.demo.utils;

import com.neterium.client.demo.domain.Counterpart;
import com.neterium.client.demo.domain.Transaction;
import com.neterium.client.sdk.model.CounterpartType;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;


/**
 * Helper to generate mock/fake data
 *
 * @author Bernard Ligny
 */
public class DataGenerator {

    private static final Locale LOCALE = Locale.of("es"); // Spanish names
    private static final Faker FAKER = new Faker(LOCALE);
    private static final Random RANDOM = new Random();


    public static Stream<Counterpart> sampleCounterparts(long count) {
        var generator = Stream.generate(() ->
                Counterpart.builder()
                        .id(UUID.randomUUID().toString())
                        .lastName(FAKER.name().lastName())
                        .firstName(FAKER.name().firstName())
                        .type(CounterpartType.INDIVIDUAL)
                        .build()
        );
        return generator.limit(count);
    }


    public static Stream<Transaction> sampleTransactions(long count) {
        var generator = Stream.generate(() ->
                Transaction.builder()
                        .paymentRef(UUID.randomUUID().toString())
                        .currency(FAKER.currency().code())
                        .amount(BigDecimal.valueOf(50 + RANDOM.nextInt(200) * 1_000))
                        .debtor(fakeTransactionParty())
                        .creditor(fakeTransactionParty())
                        .build()
        );
        return generator.limit(count);
    }


    private static Transaction.Party fakeTransactionParty() {
        return new Transaction.Party() {

            @Override
            public String getAccountNumber() {
                return FAKER.finance().iban();
            }

            @Override
            public String getFullName() {
                var name = FAKER.name();
                return name.firstName() + " " + name.lastName();
            }

            @Override
            public String getAddressLine() {
                return FAKER.address().streetAddress() + " " + FAKER.address().streetAddressNumber();
            }

            @Override
            public String getAddressPostalCode() {
                return FAKER.address().zipCode();
            }

            @Override
            public String getAddressCity() {
                return FAKER.address().city();
            }

            @Override
            public String getCountryCode() {
                return FAKER.address().countryCode();
            }

            @Override
            public String toString() {
                return "%s (%s)".formatted(getAccountNumber(), getFullName());
            }
        };
    }

}
