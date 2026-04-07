package ru.astondevs.mycare.controller.insuranceapplication.impl;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.astondevs.mycare.controller.insuranceapplication.InsuranceApplicationController;
import ru.astondevs.mycare.dto.insuranceapplication.request.CreateInsuranceApplicationRequest;
import ru.astondevs.mycare.dto.insuranceapplication.request.UpdateApplicationStatusRequest;
import ru.astondevs.mycare.dto.insuranceapplication.response.InsuranceApplicationResponse;
import ru.astondevs.mycare.service.application.InsuranceApplicationService;

/**
 * REST-контроллер, реализующий API-контракт {@link InsuranceApplicationController}.
 *
 * @author Ivan Sergienko
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@Slf4j
public class InsuranceApplicationControllerImpl implements InsuranceApplicationController {

    /**
     * Сервис, инкапсулирующий бизнес-логику
     * по работе с заявлениями на страхование.
     */
    private final InsuranceApplicationService insuranceApplicationService;

    /**
     * {@inheritDoc}
     */
    @Override
    @PostMapping
    public ResponseEntity<InsuranceApplicationResponse> createApplication(
        @Valid @RequestBody CreateInsuranceApplicationRequest request) {

        log.info("Got createApplication request",
            keyValue("request", request)
        );

        InsuranceApplicationResponse response =
            insuranceApplicationService.createInsuranceApplication(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PatchMapping("/{id}/status")
    public ResponseEntity<InsuranceApplicationResponse> updateStatus(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateApplicationStatusRequest request) {

        log.info("Got updateStatus request",
            keyValue("applicationId", id),
            keyValue("request", request)
        );

        InsuranceApplicationResponse response =
            insuranceApplicationService.updateInsuranceApplicationStatus(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<InsuranceApplicationResponse> getApplicationById(
        @PathVariable UUID id) {

        log.info("Got getApplicationById request",
            keyValue("applicationId", id)
        );

        InsuranceApplicationResponse response =
            insuranceApplicationService.getInsuranceApplicationById(id);

        return ResponseEntity.ok(response);
    }
}
