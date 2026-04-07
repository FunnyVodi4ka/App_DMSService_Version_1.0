package ru.astondevs.mycare.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.astondevs.mycare.models.entity.DmsProgram;

@Repository
public interface DmsProgramRepository extends JpaRepository<DmsProgram, UUID> {

}
