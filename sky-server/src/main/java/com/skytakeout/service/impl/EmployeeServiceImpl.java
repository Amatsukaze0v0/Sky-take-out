package com.skytakeout.service.impl;

import com.skytakeout.constant.MessageConstant;
import com.skytakeout.constant.PasswordConstant;
import com.skytakeout.constant.StatusConstant;
import com.skytakeout.context.BaseContext;
import com.skytakeout.dto.EmployeeDTO;
import com.skytakeout.dto.EmployeeLoginDTO;
import com.skytakeout.dto.EmployeePageQueryDTO;
import com.skytakeout.entity.Employee;
import com.skytakeout.exception.AccountLockedException;
import com.skytakeout.exception.AccountNotFoundException;
import com.skytakeout.exception.PasswordErrorException;
import com.skytakeout.repository.EmployeeRepository;
import com.skytakeout.result.PageResult;
import com.skytakeout.service.EmployeeService;
import com.skytakeout.util.AttributeFillerUtil;
import com.skytakeout.util.SHA256Util;
import com.skytakeout.util.SaltGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    @Override
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        // 1. 根据用户名查询员工
        Employee employee = employeeRepository.findByUsername(username);

        // 2. 处理各种异常情况
        if (employee == null) {
            // 账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 3. 密码比对
        // 对前端传过来的明文密码进行SHA加盐加密处理
        String password_sha256 = SHA256Util.hashPassword(password+employee.getSalt());

        if (!password_sha256.equals(employee.getPassword())) {
            // 密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // 4. 判断账号是否被锁定
        if (employee.getStatus() == 0) {
            // 账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        // 5. 返回员工对象
        return employee;
    }

    @Override
    public void save(EmployeeDTO employeeDTO) {
        String password = PasswordConstant.DEFAULT_PASSWORD;         //默认密码为123456
        String salt = SaltGenerator.generateSalt();

        Employee employee = new Employee();
        BeanUtils.copyProperties( employeeDTO,employee);    //拷贝

        //其他Attribute
        employee.setPassword(SHA256Util.hashPassword(password+salt));
        employee.setStatus(StatusConstant.ENABLE);
        employee.setSalt(salt);
        employee.setCreateTime(LocalDateTime.now());    //设置时间
        employee.setUpdateTime(LocalDateTime.now());

        Long currentId = BaseContext.getCurrentID();
        if (currentId != null) {
            employee.setCreateUser(currentId);
            employee.setUpdateUser(currentId);
        } else {
            // 如果获取不到当前用户ID，设置默认值
            employee.setCreateUser(1L);
            employee.setUpdateUser(1L);
        }

        employeeRepository.save(employee);
    }

    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // 获取分页参数
        int page = employeePageQueryDTO.getPage();
        int pageSize = employeePageQueryDTO.getPageSize();
        String name = employeePageQueryDTO.getName();

        // 创建分页对象，页码从0开始，所以需要减1
        Pageable pageable = PageRequest.of(page - 1, pageSize,
                                            Sort.by(Sort.Direction.DESC, "createTime"));
        // 执行分页查询
        Page<Employee> employeePage = employeeRepository.findByNameLike(name, pageable);

        List<Employee> employeeList = employeePage.getContent();
        //转化为DTO不显示密码等无用信息
        List<EmployeeDTO> employeeDTOList = employeeList.stream().map(
                employee -> {
                    EmployeeDTO dto = new EmployeeDTO();
                    BeanUtils.copyProperties(employee, dto);
                    return dto;
                }
        ).toList();
        long total = employeePage.getTotalElements();
        return new PageResult(total, employeeDTOList);
    }

    @Override
    public void empStatus(Integer status, Long id) {

        employeeRepository.empStatus(status, id);
    }

    @Override
    public Employee getById(Long id) {
        Employee found = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("员工不存在"));
        found.setPassword("****");
        found.setSalt("****");
        return found;
    }

    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        // 使用BeanUtils复制属性，忽略空值
        BeanUtils.copyProperties(employeeDTO, employee, AttributeFillerUtil.getNullPropertyNames(employeeDTO));
        //其他需要的Attribute
        //设置时间
        employee.setUpdateTime(LocalDateTime.now());
        //修改人
        Long currentId = BaseContext.getCurrentID();
        if (currentId != null) {
            employee.setUpdateUser(currentId);
        } else {
            // 如果获取不到当前用户ID，设置默认值
            employee.setUpdateUser(1L);
        }
        employeeRepository.save(employee);
    }
//    /**
//     * 获取对象中值为null的属性名数组
//     */
//    private String[] getNullPropertyNames(Object source) {
//        final BeanWrapper src = new BeanWrapperImpl(source);
//        PropertyDescriptor[] pds = src.getPropertyDescriptors();
//
//        Set<String> emptyNames = new HashSet<>();
//        for(PropertyDescriptor pd : pds) {
//            Object srcValue = src.getPropertyValue(pd.getName());
//            if (srcValue == null) emptyNames.add(pd.getName());
//        }
//
//        String[] result = new String[emptyNames.size()];
//        return emptyNames.toArray(result);
//    }
}