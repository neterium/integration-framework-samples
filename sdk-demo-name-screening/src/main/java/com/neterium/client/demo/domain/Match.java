package com.neterium.client.demo.domain;

import com.neterium.client.sdk.model.Refutable;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Match entity
 *
 * @author Bernard Ligny
 */
@Data
@Document(collection = "cp-matches")
public class Match implements Refutable {

    @Id
    String id;

    @NonNull
    String externalId; // matchingId on server

    @NonNull
    String profileId;

    @NonNull
    @Indexed
    String counterpartId;

    @NonNull
    LocalDateTime lastModified;

    Integer score;
    BigDecimal checkSum;
    String screenedText;
    String level;
    String matchedText;
    Decision decision = Decision.NONE;
    String profileDetails;
    String location;


    @Override
    public boolean isDisproved() {
        return Decision.IGNORE.equals(decision);
    }

    @Override
    public boolean hasCheckSum(@NonNull Number checkSum) {
        return checkSum.equals(this.checkSum);
    }

    public enum Decision {
        NONE,
        KEEP,
        IGNORE,
        FORWARD
    }

}
