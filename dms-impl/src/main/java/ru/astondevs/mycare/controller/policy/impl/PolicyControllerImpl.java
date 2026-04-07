package ru.astondevs.mycare.controller.policy.impl;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.astondevs.mycare.controller.policy.PolicyController;
import ru.astondevs.mycare.dto.policy.request.CreatePolicyRequest;
import ru.astondevs.mycare.dto.policy.request.UpdatePolicyStatusRequest;
import ru.astondevs.mycare.dto.policy.response.DmsPolicyDetailResponse;
import ru.astondevs.mycare.dto.policy.response.PolicyResponse;
import ru.astondevs.mycare.models.enums.ClientType;
import ru.astondevs.mycare.service.policy.PolicyService;

/**
 * REST-контроллер, реализующий API-контракт {@link PolicyController}.
 * <p>
 *
 * @author Ivan Segen
 * @version 1.0
 */
@RestController
@AllArgsConstructor
@Slf4j
public class PolicyControllerImpl implements PolicyController {

    /**
     * Сервис, инкапсулирующий бизнес-логику по работе с заявлениями на страхование.
     */
    private final PolicyService policyService;

    @Override
    public List<PolicyResponse> getCompanyPolicies() {
        return policyService.getPoliciesByClientType(ClientType.REPRESENTATIVE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PostMapping
    public ResponseEntity<PolicyResponse> createPolicy(
        @Valid @RequestBody CreatePolicyRequest request
    ) {
        log.info("Got createPolicy request",
            keyValue("request", request)
        );

        PolicyResponse response = policyService.createPolicy(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PatchMapping("/{id}/status")
    public ResponseEntity<PolicyResponse> updateStatus(
        @PathVariable UUID id,
        @Valid @RequestBody UpdatePolicyStatusRequest request) {

        log.info("Got updateStatus request",
            keyValue("applicationId", id),
            keyValue("request", request)
        );

        PolicyResponse response = policyService.updatePolicyStatus(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PolicyResponse> getPolicyById(@PathVariable UUID id) {

        log.info("Got getPolicyById request",
            keyValue("policy", id)
        );

        PolicyResponse response = policyService.getPolicyById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    @GetMapping("/dms/{policyId}")
    public ResponseEntity<DmsPolicyDetailResponse> getDmsPolicyDetail(
        @PathVariable UUID policyId
    ) {
        log.info("Запрос на получение подробной информации по полису",
            keyValue("policyId", policyId)
        );

        DmsPolicyDetailResponse response = policyService.getDmsPolicyDetail(policyId);

        return ResponseEntity.ok(response);
    }
}
