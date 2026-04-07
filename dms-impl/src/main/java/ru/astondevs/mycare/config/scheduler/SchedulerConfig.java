package ru.astondevs.mycare.config.scheduler;

import javax.sql.DataSource;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Конфигурация для Spring Scheduler (фоновые задачи) и Shedlock (распределенные блокировки).
 * <p>
 * Обеспечивает создание бина {@link LockProvider} для гарантии эксклюзивного выполнения @Scheduled
 * задач в кластере.
 *
 * @author Ivan Sergienko
 * @since 1.0.0
 */
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
@EnableConfigurationProperties(SchedulerProperties.class)
public class SchedulerConfig {
	
	/**
	 * Создает бин LockProvider для Shedlock, используя JDBC (таблицу 'shedlock').
	 * <p>
	 * Мы явно указываем имя таблицы с префиксом схемы ('dms_service.shedlock') для максимальной
	 * надежности и предсказуемости, что соответствует миграции.
	 *
	 * @param dataSource Spring Boot DataSource.
	 * @return Сконфигурированный LockProvider.
	 */
	@Bean
	public LockProvider lockProvider(DataSource dataSource) {
		
		return new JdbcTemplateLockProvider(
			JdbcTemplateLockProvider.Configuration
				.builder()
				.withJdbcTemplate(new JdbcTemplate(dataSource))
				.withTableName("dms_service.shedlock")
				.build()
		);
	}
}
