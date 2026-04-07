package ru.astondevs.mycare.service.client.impl;


import static net.logstash.logback.argument.StructuredArguments.keyValue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.astondevs.mycare.event.client.ClientEvent;
import ru.astondevs.mycare.mapper.ClientMapper;
import ru.astondevs.mycare.models.entity.Client;
import ru.astondevs.mycare.repository.ClientRepository;
import ru.astondevs.mycare.service.client.ClientService;

/**
 * Реализация сервиса для управления жизненным циклом клиентов. Обеспечивает создание и обновление
 * данных клиента с использованием изолированных транзакций.
 *
 * @author Ivan Sakharov
 * @since 11/26/2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final PlatformTransactionManager transactionManager;

    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;


    /**
     * Создает нового клиента на основе события создания. Операция выполняется в новой транзакции.
     * Если клиент с такими уникальными данными уже существует ошибка логируется, но не прерывает
     * выполнение.
     *
     * @param clientEvent ДТО событие с данными для создания клиента.
     */
    @Override
    public void createClient(ClientEvent clientEvent) {

        Client client = clientMapper.changeEntity(clientEvent);
        saveClientSafelyToDataBase(client);
    }


    /**
     * Обрабатывает изменение данных клиента на основе события изменения. Конвертирует событие в
     * сущность и сохраняет её в БД в новой транзакции.
     *
     * @param clientEvent ДТО событие с обновленными данными клиента.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateClient(ClientEvent clientEvent) {
        clientRepository.findById(clientEvent.clientId())
            .ifPresentOrElse(
                existingClient -> {
                    clientMapper.changeEntity(existingClient, clientEvent);
                    log.info("Client changed",
                        keyValue("ClientId", clientEvent.clientId()));
                },
                () -> log.warn("Client not found",
                    keyValue("ClientID", clientEvent.clientId()))
            );
    }

    /**
     * Внутренний метод для безопасного сохранения клиента в БД. Использует
     * {@link TransactionTemplate} с пропагацией REQUIRES_NEW для изоляции сохранения. Перехватывает
     * {@link DataIntegrityViolationException} для подавления ошибок уникальности.
     *
     * @param client Сущность клиента для сохранения.
     */
    private void saveClientSafelyToDataBase(Client client) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        try {
            template.execute(status -> {
                clientRepository.saveAndFlush(client);
                return null;
            });
        } catch (DuplicateKeyException e) {
            log.warn("Client already exists. Skipping creation.",
                keyValue("Client id", client.getClientId())
            );
        }
    }
}


