package com.neterium.client.demo.domain;

import com.neterium.client.sdk.model.CounterpartType;
import com.neterium.client.sdk.model.Gender;
import com.neterium.client.sdk.model.Partitionable;
import com.neterium.client.sdk.model.ScreenableParty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Counterpart entity
 *
 * @author Bernard Ligny
 */
@Data
@Builder
@Document(collection = "counterparts")
public class Counterpart implements ScreenableParty, Partitionable {

    @Id
    String id;

    // Counterpart data
    CounterpartType type;
    String lastName;
    String firstName;
    String middleNames;
    Gender gender;
    String dateOfBirth;
    String registrationCountryCode;
    String registrationNumber;
    String addressCountryCode;
    String addressCityName;

    // Partitionable
    String partitionKey;

    // Import info
    LocalDateTime lastImport;

    // Screening info
    LocalDateTime lastScreenedAt;
    int matchCount = 0;
    int alertCount = 0;

    public Integer getIgnoredCount() {
        return matchCount - alertCount;
    }

}
