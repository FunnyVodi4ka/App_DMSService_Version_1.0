package ru.astondevs.mycare.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.astondevs.mycare.models.entity.PolicyMedicalFacility;

@Repository
public interface PolicyMedicalFacilityRepository extends
    JpaRepository<PolicyMedicalFacility, UUID> {

    /**
     * Найти все медицинские учреждения по ID полиса.
     *
     * @param policyId ID полиса
     * @return Список связей полис-медучреждение
     */
    @EntityGraph(attributePaths = {"medicalFacility"})
    List<PolicyMedicalFacility> findByPolicyPolicyId(UUID policyId);
}
