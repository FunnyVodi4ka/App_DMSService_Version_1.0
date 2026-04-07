package ru.astondevs.mycare.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.astondevs.mycare.models.entity.InsuredPerson;

@Repository
public interface InsuredPersonRepository extends JpaRepository<InsuredPerson, UUID> {

    /**
     * Найти всех застрахованных лиц по ID заявления на страховой полис.
     *
     * @param insuranceApplicationId ID заявления
     * @return Список застрахованных лиц
     */
    List<InsuredPerson> findByInsuranceApplicationInsuranceApplicationId(
        UUID insuranceApplicationId);
}
