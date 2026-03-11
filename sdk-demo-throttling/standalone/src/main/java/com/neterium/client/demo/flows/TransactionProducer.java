package com.neterium.client.demo.flows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neterium.client.demo.domain.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;


/**
 * Producer of fake/random transaction data
 *
 * @author Bernard Ligny
 */
@Component
@Slf4j
public class TransactionProducer extends FakeDataProducer<Transaction> {

    public TransactionProducer(ObjectMapper mapper) {
        super(mapper);
    }


    @Override
    protected Transaction fakeRecord() {
        return baseRecord()
                .debtor(fakeTransactionParty())
                .creditor(fakeTransactionParty())
                .build();
    }


    @Override
    protected Transaction fakeRecord(JsonNode template) {
        var party1 = fakeTransactionParty(template);
        var party2 = fakeTransactionParty();
        var shuffle = random.nextBoolean();
        return baseRecord()
                .debtor(shuffle ? party1 : party2)
                .creditor(shuffle ? party2 : party1)
                .build();
    }


    private Transaction.TransactionBuilder baseRecord() {
        return Transaction.builder()
                .paymentRef(UUID.randomUUID().toString())
                .currency(faker.currency().code())
                .amount(BigDecimal.valueOf(50 + random.nextInt(200) * 1_000));
    }


    private Transaction.Party fakeTransactionParty() {
        return fakeTransactionParty(null, null);
    }


    private Transaction.Party fakeTransactionParty(JsonNode template) {
        var lastName = template.get("lastname").asText();
        var firstName = Optional.ofNullable(template.get("firstname"))
                .map(JsonNode::asText)
                .orElse("");
        return fakeTransactionParty(lastName, firstName);
    }


    private Transaction.Party fakeTransactionParty(String lastName, String firstName) {
        return new Transaction.Party() {

            @Override
            public String getAccountNumber() {
                return faker.finance().iban();
            }

            @Override
            public String getFullName() {
                var s1 = Optional.ofNullable(firstName)
                        .orElseGet(() -> faker.name().firstName());
                var s2 = Optional.ofNullable(lastName)
                        .orElseGet(() -> faker.name().lastName());
                return s1 + " " + s2;
            }

            @Override
            public String getAddressLine() {
                return faker.address().streetAddress() + " " + faker.address().streetAddressNumber();
            }

            @Override
            public String getAddressPostalCode() {
                return faker.address().zipCode();
            }

            @Override
            public String getAddressCity() {
                return faker.address().city();
            }

            @Override
            public String getCountryCode() {
                return faker.address().countryCode();
            }

            @Override
            public String toString() {
                return "%s (%s)".formatted(getAccountNumber(), getFullName());
            }
        };
    }

}