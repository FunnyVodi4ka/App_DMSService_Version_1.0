package ru.astondevs.mycare.service.client;


import ru.astondevs.mycare.event.client.ClientEvent;

/**
 * @author Ivan Sakharov
 * @since 11/26/2025
 */

public interface ClientService {

    void createClient(ClientEvent event);

    void updateClient(ClientEvent event);

}
