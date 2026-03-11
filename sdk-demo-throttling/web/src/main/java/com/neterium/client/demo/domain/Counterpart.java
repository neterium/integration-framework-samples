package com.neterium.client.demo.domain;

import com.neterium.client.sdk.model.CounterpartType;
import com.neterium.client.sdk.model.ScreenableParty;
import lombok.Builder;
import lombok.Data;

/**
 * Counterpart entity
 *
 * @author Bernard Ligny
 */
@Data
@Builder
public class Counterpart implements ScreenableParty {

    String id;
    CounterpartType type;
    String lastName;
    String firstName;

}
