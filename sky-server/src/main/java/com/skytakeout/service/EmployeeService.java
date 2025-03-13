package com.skytakeout.service;

import com.skytakeout.dto.EmployeeDTO;
import com.skytakeout.dto.EmployeeLoginDTO;
import com.skytakeout.dto.EmployeePageQueryDTO;
import com.skytakeout.entity.Employee;
import com.skytakeout.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 分页查询员工信息
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 根据id修改员工状态
     * @param status
     * @param id
     * @return
     */
    void empStatus(Integer status, Long id);

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    Employee getById(Long id);

    /**
     * 修改员工信息
     * @param employeeDTO
     */
    void update(EmployeeDTO employeeDTO);
}
