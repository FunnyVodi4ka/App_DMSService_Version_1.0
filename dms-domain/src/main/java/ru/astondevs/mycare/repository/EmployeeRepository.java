package ru.astondevs.mycare.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.astondevs.mycare.models.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

}
