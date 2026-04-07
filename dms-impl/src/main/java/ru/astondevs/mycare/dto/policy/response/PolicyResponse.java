package ru.astondevs.mycare.dto.policy.response;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import ru.astondevs.mycare.models.enums.ClientType;
import ru.astondevs.mycare.models.enums.DmsType;

@Builder
public record PolicyResponse(
	UUID policyId,
	String number,
	Instant startDate,
	Instant endDate,
	String policyUrl,
	String clientId,
	ClientType clientType,
	DmsType dmsType
) {

}