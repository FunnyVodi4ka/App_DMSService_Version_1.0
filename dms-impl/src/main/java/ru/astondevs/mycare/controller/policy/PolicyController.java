package ru.astondevs.mycare.controller.policy;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.astondevs.mycare.dto.policy.request.CreatePolicyRequest;
import ru.astondevs.mycare.dto.policy.request.UpdatePolicyStatusRequest;
import ru.astondevs.mycare.dto.policy.response.DmsPolicyDetailResponse;
import ru.astondevs.mycare.dto.policy.response.PolicyResponse;
import ru.astondevs.mycare.starterexceptionhandler.model.ErrorResponse;

/**
 * API-контракт для работы с полисами.
 * <p>
 *
 * @author Mikhail Ermakov
 * @version 1.0
 */
@Tag(
    name = "Policy",
    description = "API для работы с полисами"
)
@RequestMapping("/api/v1/policies/")
public interface PolicyController {

    @Operation(
        summary = "EM-DMS/5.45-EP Просмотр полисов юр.лиц",
        description = "Возвращает список полисов юр.лиц."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Заявление успешно создано",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = PolicyResponse.class))
            }
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Некорректный запрос (ошибка валидации)",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))
            }
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Для выполнения запроса необходимо авторизоваться. Убедитесь, что вы вошли в систему",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))
            }
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Недостаточно прав для выполнения этого запроса!",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))
            }
        )
    })
    @GetMapping("company/")
    List<PolicyResponse> getCompanyPolicies();

    /**
     * Создает новый полис на основе предоставленных данных.
     * <p>
     *
     * @param request DTO, содержащее все необходимые данные для создания нового полиса.
     * @return {@link ResponseEntity} со статусом 201 CREATED и телом {@link PolicyResponse},
     * содержащим DTO созданной сущности.
     */
    @Operation(
        summary = "Создать новый полис",
        description = "Принимает DTO, создает полис и возвращает его DTO."
    )
    @RequestBody(
        description = "Данные для создания нового полиса",
        required = true,
        content = @Content(
            schema = @Schema(implementation = CreatePolicyRequest.class)
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Полис успешно создан",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = PolicyResponse.class))
            }
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Некорректный запрос (ошибка валидации)",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))
            }
        )
    })
    ResponseEntity<PolicyResponse> createPolicy(
        CreatePolicyRequest request
    );

    /**
     * Обновляет статус существующего полиса по его уникальному идентификатору.
     * <p>
     *
     * @param id      Уникальный идентификатор (UUID) полиса, статус которого нужно обновить.
     * @param request DTO, содержащее новый статус.
     * @return {@link ResponseEntity} со статусом 200 OK и телом {@link PolicyResponse}, содержащим
     * DTO обновленного полиса.
     */
    @Operation(summary = "Обновить статус существующего полиса")
    @RequestBody(
        description = "DTO с новым статусом полиса",
        required = true,
        content = @Content(
            schema = @Schema(
                implementation = UpdatePolicyStatusRequest.class)
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Статус успешно обновлен",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PolicyResponse.class))
            }
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Некорректный запрос (ошибка валидации)",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))}
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Полис с указанным ID не найден",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))}
        )
    })
    ResponseEntity<PolicyResponse> updateStatus(
        @Parameter(description = "Уникальный идентификатор полиса (UUID)", required = true)
        UUID id,
        UpdatePolicyStatusRequest request
    );

    /**
     * Возвращает подробную информацию о полисе по его уникальному идентификатору.
     *
     * @param id Уникальный идентификатор (UUID) запрашиваемого полиса.
     * @return {@link ResponseEntity} со статусом 200 OK и телом {@link PolicyResponse}, содержащим
     * DTO найденного полиса.
     */
    @Operation(summary = "Получить информацию о полисе по ID")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Полис успешно найден",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = PolicyResponse.class))}
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Полис с указанным ID не найден",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))}
        )
    })
    ResponseEntity<PolicyResponse> getPolicyById(
        @Parameter(description = "Уникальный идентификатор полиса (UUID)", required = true)
        UUID id
    );

    /**
     * Возвращает подробную информацию о полисе ДМС физ. лица "Здоровье".
     *
     * @param policyId Уникальный идентификатор (UUID) полиса ДМС.
     * @return {@link ResponseEntity} со статусом 200 OK и телом {@link DmsPolicyDetailResponse},
     * содержащим детальную информацию о полисе, данные страхователя, застрахованных лиц и
     * медицинских учреждений.
     */
    @Operation(
        summary = "EM-DMS/5.28-EP Просмотр подробной информации по полису физ. лица Здоровье",
        description = "Возвращает детальную информацию о полисе ДМС для специалиста страховой компании"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Подробная информация о полисе успешно получена",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = DmsPolicyDetailResponse.class))}
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Для выполнения запроса необходимо авторизоваться. Убедитесь, что вы вошли в систему",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))}
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Недостаточно прав для выполнения этого запроса!",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))}
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Полис с указанным ID не найден",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))}
        )
    })
    @GetMapping("dms/{policyId}")
    ResponseEntity<DmsPolicyDetailResponse> getDmsPolicyDetail(
        @Parameter(description = "Уникальный идентификатор полиса ДМС (UUID)", required = true)
        @PathVariable UUID policyId
    );
}
