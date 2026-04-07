package ru.astondevs.mycare.repository;


import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.astondevs.mycare.models.entity.Client;

/**
 * Репозиторий для доступа к сущностям {@link Client}.
 *
 * @author Ivan Sakharov
 * @since 11/16/2025
 */
public interface ClientRepository extends JpaRepository<Client, UUID> {

}
