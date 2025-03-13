package com.skytakeout.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.skytakeout.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    Employee findByUsername(String username);
    @Query("select e from Employee e where (:name IS NULL OR e.name LIKE %:name%)")
    Page<Employee> findByNameLike(String name, Pageable pageable);
}