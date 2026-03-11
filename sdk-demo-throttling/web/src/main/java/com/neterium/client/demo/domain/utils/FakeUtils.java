package com.neterium.client.demo.domain.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.neterium.client.demo.domain.Counterpart;
import com.neterium.client.demo.domain.Transaction;
import com.neterium.client.sdk.model.CounterpartType;
import net.datafaker.Faker;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * FakeUtils
 *
 * @author Bernard Ligny
 */
public class FakeUtils {

    private static final Random RANDOM = new Random();
    private static final Faker FAKER = new Faker(Locale.ENGLISH);


    private FakeUtils() {
        // Disable instantiation
    }


    public static File getBlackList() throws FileNotFoundException {
        return ResourceUtils.getFile("classpath:data/counterpart-hits.json");
    }


    public static Counterpart fakeIndividual() {
        return Counterpart.builder()
                .id(UUID.randomUUID().toString())
                .lastName(FAKER.name().lastName())
                .firstName(FAKER.name().firstName())
                .type(CounterpartType.INDIVIDUAL)
                .build();
    }


    public static Counterpart fakeIndividualFrom(JsonNode randomEntry) {
        return Counterpart.builder()
                .id(UUID.randomUUID().toString())
                .lastName(randomEntry.get("lastname").asText())
                .firstName(
                        Optional.ofNullable(randomEntry.get("firstname"))
                                .map(JsonNode::asText)
                                .orElse(null)
                )
                .type(CounterpartType.INDIVIDUAL)
                .build();
    }


    public static Transaction fakeTransaction() {
        return baseTransaction()
                .debtor(fakeTransactionParty())
                .creditor(fakeTransactionParty())
                .build();
    }


    public static Transaction fakeTransactionFrom(JsonNode randomEntry) {
        var party1 = fakeTransactionParty(randomEntry);
        var party2 = fakeTransactionParty();
        var shuffle = RANDOM.nextBoolean();
        return baseTransaction()
                .debtor(shuffle ? party1 : party2)
                .creditor(shuffle ? party2 : party1)
                .build();
    }


    private static Transaction.TransactionBuilder baseTransaction() {
        return Transaction.builder()
                .paymentRef(UUID.randomUUID().toString())
                .currency(FAKER.currency().code())
                .amount(BigDecimal.valueOf(50 + RANDOM.nextInt(200) * 1_000));
    }


    private static Transaction.Party fakeTransactionParty() {
        return fakeTransactionParty(null, null);
    }

    private static Transaction.Party fakeTransactionParty(JsonNode randomEntry) {
        var lastName = randomEntry.get("lastname").asText();
        var firstName = Optional.ofNullable(randomEntry.get("firstname"))
                .map(JsonNode::asText)
                .orElse("");
        return fakeTransactionParty(lastName, firstName);
    }


    private static Transaction.Party fakeTransactionParty(String lastName, String firstName) {
        return new Transaction.Party() {

            @Override
            public String getAccountNumber() {
                return FAKER.finance().iban();
            }

            @Override
            public String getFullName() {
                var s1 = Optional.ofNullable(firstName)
                        .orElseGet(() -> FAKER.name().firstName());
                var s2 = Optional.ofNullable(lastName)
                        .orElseGet(() -> FAKER.name().lastName());
                return s1 + " " + s2;
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
