package ru.astondevs.mycare.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.astondevs.mycare.models.entity.Policy;
import ru.astondevs.mycare.models.enums.ClientType;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, UUID> {

    /**
     * Находит все страховые полисы по типу клиента.
     * <p>
     * Фильтрация происходит по полю {@code clientType} в сущности {@code InsuranceApplication}.
     *
     * @return Список объектов {@link Policy}, удовлетворяющих условию. Если подходящих полисов не
     * найдено, возвращается пустой список.
     */
    @Query("""
        SELECT p
        FROM Policy p
        JOIN FETCH p.insuranceContract ic
        JOIN FETCH ic.insuranceApplication ia
        JOIN FETCH ia.dmsProgram dp
        WHERE ia.clientType = :clientType
        """)
    List<Policy> findAllByClientType(@Param("clientType") ClientType clientType);

    /**
     * Найти полис со всеми необходимыми деталями (FETCH JOIN).
     * <p>
     * Оптимизированный запрос, который загружает Policy вместе с: - InsuranceContract -
     * InsuranceApplication - DmsProgram - Client (для личных данных страхователя)
     *
     * @param policyId ID полиса
     * @return Optional с полисом и всеми деталями
     */
    @Query("SELECT DISTINCT p FROM Policy p " +
           "LEFT JOIN FETCH p.insuranceContract ic " +
           "LEFT JOIN FETCH ic.insuranceApplication ia " +
           "LEFT JOIN FETCH ia.dmsProgram " +
           "LEFT JOIN FETCH ia.client " +
           "WHERE p.policyId = :policyId")
    Optional<Policy> findByIdWithDetails(@Param("policyId") UUID policyId);
}