package com.skytakeout.service.impl;

import com.skytakeout.dto.EmployeeDTO;
import com.skytakeout.dto.EmployeeLoginDTO;
import com.skytakeout.entity.Employee;
import com.skytakeout.exception.AccountLockedException;
import com.skytakeout.exception.AccountNotFoundException;
import com.skytakeout.exception.PasswordErrorException;
import com.skytakeout.repository.EmployeeRepository;
import com.skytakeout.service.EmployeeService;
import com.skytakeout.util.SHA256Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;
@SpringBootTest
public class EmployeeServiceImplTest {

    @MockBean
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService; // 确保这是一个Spring管理的bean，通常是接口的实现类

    @BeforeEach
    void setUp() {
        // 初始化Mockito的mock对象
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginSuccess() {
        // 准备测试数据
        String username = "admin";
        String password = "123456";
        String salt = "4b12980c219bc5b7f4c693cf08fbefcd";
        String encryptedPassword;

        encryptedPassword = SHA256Util.hashPassword(password+salt);

        Employee employee = new Employee();
        employee.setUsername(username);
        employee.setPassword(encryptedPassword);
        employee.setSalt(salt);
        employee.setStatus(1);

        EmployeeLoginDTO loginDTO = new EmployeeLoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setPassword(password);

        // 模拟Repository行为
        when(employeeRepository.findByUsername(username)).thenReturn(employee);

        // 执行测试
        Employee result = employeeService.login(loginDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void loginWithNonExistentAccount() {
        // 准备测试数据
        EmployeeLoginDTO loginDTO = new EmployeeLoginDTO();
        loginDTO.setUsername("nonexistent");
        loginDTO.setPassword("123456");

        // 模拟Repository行为
        when(employeeRepository.findByUsername("nonexistent")).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(AccountNotFoundException.class, () -> {
            employeeService.login(loginDTO);
        });
    }

    @Test
    void loginWithWrongPassword() {
        // 准备测试数据
        String username = "admin";
        String correctPassword = "123456";
        String wrongPassword = "wrong";
        String encryptedPassword = SHA256Util.hashPassword(correctPassword+"4b12980c219bc5b7f4c693cf08fbefcd");

        Employee employee = new Employee();
        employee.setUsername(username);
        employee.setPassword(encryptedPassword);
        employee.setSalt("4b12980c219bc5b7f4c693cf08fbefcd");
        employee.setStatus(1);

        EmployeeLoginDTO loginDTO = new EmployeeLoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setPassword(wrongPassword);

        // 模拟Repository行为
        when(employeeRepository.findByUsername(username)).thenReturn(employee);

        // 执行测试并验证异常
        assertThrows(PasswordErrorException.class, () -> {
            employeeService.login(loginDTO);
        });
    }

    @Test
    void loginWithLockedAccount() {
        // 准备测试数据
        String username = "locked";
        String password = "123456";
        String salt = "4b12980c219bc5b7f4c693cf08fbefcd";
        String encryptedPassword = SHA256Util.hashPassword(password+salt);

        Employee employee = new Employee();
        employee.setUsername(username);
        employee.setPassword(encryptedPassword);
        employee.setSalt(salt);
        employee.setStatus(0); // 账号锁定

        EmployeeLoginDTO loginDTO = new EmployeeLoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setPassword(password);

        // 模拟Repository行为
        when(employeeRepository.findByUsername(username)).thenReturn(employee);

        // 执行测试并验证异常
        assertThrows(AccountLockedException.class, () -> {
            employeeService.login(loginDTO);
        });
    }

    @Test
    void testSaveEmployee() {
        // 准备测试数据
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setName("测试员工");
        employeeDTO.setUsername("testuser");
        employeeDTO.setPhone("13800138000");
        employeeDTO.setSex("1");
        employeeDTO.setIdNumber("110101199001010001");

        // 执行测试 - 调用void方法
        employeeService.save(employeeDTO);

        // 验证Repository的save方法是否被调用
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }
}