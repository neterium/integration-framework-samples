package com.neterium.client.demo.files;

import com.neterium.client.sdk.model.CounterpartType;
import com.neterium.client.sdk.model.Gender;
import lombok.Data;

/**
 * CSV record with counterpart data
 *
 * @author bligny
 */
@Data
public class CsvCounterPart {

    String recordId;
    CounterpartType type;
    String firstName;
    String lastName;
    String middleNames;
    Gender gender;
    String dateOfBirth;
    String registrationCountryCode;
    String registrationNumber;
    String addressCountryCode;
    String addressCityName;

}
