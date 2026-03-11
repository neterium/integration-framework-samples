package com.neterium.client.demo.domain;

import com.neterium.client.sdk.model.ScreenableTransaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Transaction entity
 *
 * @author Bernard Ligny
 */
@Data
@Builder
public class Transaction implements ScreenableTransaction<ScreenableTransaction.Party> {

    final String type = "DEMO";
    String paymentRef;
    BigDecimal amount;
    String currency;
    Party debtor;
    Party creditor;
}
