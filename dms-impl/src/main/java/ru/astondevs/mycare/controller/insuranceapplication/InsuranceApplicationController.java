package ru.astondevs.mycare.controller.insuranceapplication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import ru.astondevs.mycare.dto.insuranceapplication.request.CreateInsuranceApplicationRequest;
import ru.astondevs.mycare.dto.insuranceapplication.request.UpdateApplicationStatusRequest;
import ru.astondevs.mycare.dto.insuranceapplication.response.InsuranceApplicationResponse;
import ru.astondevs.mycare.starterexceptionhandler.model.ErrorResponse;

/**
 * API-контракт для управления заявлениями на страхование.
 *
 * @author Ivan Sergienko
 * @version 1.0
 */
@Tag(
	name = "Insurance Applications",
	description = "API для работы с заявлениями на страхование"
)
public interface InsuranceApplicationController {
	
	/**
	 * Создает новое заявление на страхование на основе предоставленных данных.
	 *
	 * @param request DTO, содержащее все необходимые данные для создания нового заявления.
	 * @return {@link ResponseEntity} со статусом 201 CREATED и телом
	 * {@link InsuranceApplicationResponse}, содержащим DTO созданной сущности.
	 */
	@Operation(
		summary = "Создать новое заявление на страхование",
		description = "Принимает DTO, создает заявление и возвращает его DTO."
	)
	@RequestBody(
		description = "Данные для создания нового заявления",
		required = true,
		content = @Content(
			schema = @Schema(implementation = CreateInsuranceApplicationRequest.class)
		)
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201",
			description = "Заявление успешно создано",
			content = {@Content(mediaType = "application/json",
				schema = @Schema(implementation = InsuranceApplicationResponse.class))
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
	ResponseEntity<InsuranceApplicationResponse> createApplication(
		CreateInsuranceApplicationRequest request
	);
	
	/**
	 * Обновляет статус существующего заявления по его уникальному идентификатору.
	 *
	 * @param id      Уникальный идентификатор (UUID) заявления, статус которого нужно обновить.
	 * @param request DTO, содержащее новый статус и, опционально, причину изменения
	 *                ({@link UpdateApplicationStatusRequest}).
	 * @return {@link ResponseEntity} со статусом 200 OK и телом
	 * {@link InsuranceApplicationResponse}, содержащим DTO обновленного заявления.
	 */
	@Operation(summary = "Обновить статус существующего заявления")
	@RequestBody(
		description = "DTO с новым статусом заявления",
		required = true,
		content = @Content(
			schema = @Schema(
				implementation = UpdateApplicationStatusRequest.class)
		)
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "Статус успешно обновлен",
			content = {@Content(
				mediaType = "application/json",
				schema = @Schema(implementation = InsuranceApplicationResponse.class))
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
			description = "Заявление с указанным ID не найдено",
			content = {@Content(mediaType = "application/json",
				schema = @Schema(implementation = ErrorResponse.class))}
		)
	})
	ResponseEntity<InsuranceApplicationResponse> updateStatus(
		@Parameter(description = "Уникальный идентификатор заявления (UUID)", required = true)
		UUID id,
		UpdateApplicationStatusRequest request
	);
	
	/**
	 * Возвращает подробную информацию о заявлении по его уникальному идентификатору.
	 *
	 * @param id Уникальный идентификатор (UUID) запрашиваемого заявления.
	 * @return {@link ResponseEntity} со статусом 200 OK и телом
	 * {@link InsuranceApplicationResponse}, содержащим DTO найденного заявления.
	 */
	@Operation(summary = "Получить информацию о заявлении по ID")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "Заявление успешно найдено",
			content = {@Content(mediaType = "application/json",
				schema = @Schema(implementation = InsuranceApplicationResponse.class))}
		),
		@ApiResponse(
			responseCode = "404",
			description = "Заявление с указанным ID не найдено",
			content = {@Content(mediaType = "application/json",
				schema = @Schema(implementation = ErrorResponse.class))}
		)
	})
	ResponseEntity<InsuranceApplicationResponse> getApplicationById(
		@Parameter(description = "Уникальный идентификатор заявления (UUID)", required = true)
		UUID id
	);
}
