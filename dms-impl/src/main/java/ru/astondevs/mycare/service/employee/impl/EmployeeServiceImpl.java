package ru.astondevs.mycare.service.employee.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import ru.astondevs.mycare.event.employee.EmployeeEvent;
import ru.astondevs.mycare.mapper.EmployeeMapper;
import ru.astondevs.mycare.models.entity.Employee;
import ru.astondevs.mycare.repository.EmployeeRepository;
import ru.astondevs.mycare.service.employee.EmployeeService;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final PlatformTransactionManager transactionManager;

    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;

    /**
     * Основной метод обновления сотрудника из {@link EmployeeEvent}.
     * Управление транзакциями происходит вручную для обозначения границ и избежания Race Condition.
     */
    @Override
    public void createOrUpdateEmployee(EmployeeEvent event) {
        boolean updated = updateIfExists(event);

        if (!updated) {
            createWithFallback(event);
        }
    }

    /**
     * Пытается найти и обновить сотрудника в стандартной транзакции.
     *
     * @return true если сотрудник найден и обновлен, false если не найден.
     */
    private boolean updateIfExists(EmployeeEvent event) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);

        return Boolean.TRUE.equals(template.execute(status ->
                employeeRepository.findById(event.employeeId())
                        .map(employee -> {
                            employeeMapper.updateEntityFromEvent(event, employee);
                            employeeRepository.saveAndFlush(employee);
                            log.info("Employee updated via event", keyValue("employeeId", event.employeeId()));
                            return Boolean.TRUE;
                        })
                        .orElse(Boolean.FALSE)
        ));
    }

    /**
     * Пытается создать сотрудника в ИЗОЛИРОВАННОЙ транзакции.
     * Если получаем DuplicateKey, транзакция откатывается чисто, не ломая поток выполнения.
     */
    private void createWithFallback(EmployeeEvent event) {

        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        try {
            template.execute(status -> {
                Employee newEmployee = employeeMapper.createEntityFromEvent(event);
                employeeRepository.saveAndFlush(newEmployee);
                log.info("Employee created via event", keyValue("employeeId", event.employeeId()));
                return null;
            });
        } catch (DataIntegrityViolationException e) {
            log.warn("Concurrent creation detected (Race Condition). Switching to update fallback.",
                    keyValue("employeeId", event.employeeId()));

            boolean recovered = updateIfExists(event);
            if (!recovered) {
                log.error("Failed to recover from race condition for employee",
                        keyValue("employeeId", event.employeeId()));
                throw e;
            }
        }
    }
}