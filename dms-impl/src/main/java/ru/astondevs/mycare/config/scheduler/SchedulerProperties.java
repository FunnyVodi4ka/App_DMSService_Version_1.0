package ru.astondevs.mycare.config.scheduler;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Отражает конфигурацию для Shedlock,
 * определенную в application.yml (scheduler.lock.*).
 * <p>
 * Используется для предоставления метаданных
 * для IDE и Actuator.
 *
 * @author Ivan Sergienko
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "scheduler")
@Getter
@Setter
@Validated
public class SchedulerProperties {

    /**
     * Вложенная конфигурация для блокировок Shedlock.
     */
    private Lock lock = new Lock();

    /**
     * Класс, отражающий вложенную структуру
     * {@code scheduler.lock}.
     */
    @Getter
    @Setter
    public static class Lock {

        /**
         * Минимальное время удержания блокировки
         * (e.g., "PT1M").
         */
        @NotBlank
        private String atLeastFor;

        /**
         * Максимальное (аварийное) время удержания блокировки
         * (e.g., "PT6H").
         */
        @NotBlank
        private String atMostFor;
    }
}
