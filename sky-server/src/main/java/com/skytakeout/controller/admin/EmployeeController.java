package com.skytakeout.controller.admin;

import com.skytakeout.context.BaseContext;
import com.skytakeout.dto.EmployeeDTO;
import com.skytakeout.dto.EmployeeLoginDTO;
import com.skytakeout.dto.EmployeePageQueryDTO;
import com.skytakeout.entity.Employee;
import com.skytakeout.result.PageResult;
import com.skytakeout.result.Result;
import com.skytakeout.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Tag(name = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @param request
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "员工登录")
    public Result<Employee> login(@RequestBody EmployeeLoginDTO employeeLoginDTO, HttpServletRequest request) {
        log.info("员工登录：{}", employeeLoginDTO);

        // 调用Service进行登录处理
        Employee employee = employeeService.login(employeeLoginDTO);

        // 登录成功后，将员工ID存入Session
        request.getSession().setAttribute("employee", employee.getId());

        return Result.success(employee);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "员工退出")
    public Result<String> logout(HttpServletRequest request) {
        // 清除Session中的员工ID
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    @PostMapping
    @Operation(summary = "新增员工")
    public Result<String> save(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工：{}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }
    /**
     * 员工分页查询
     * 接收QUERY传参而非json
     * @param name, page, pageSize
     * @return Result<PageResult>
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public Result<PageResult> pageQuery(@RequestParam(required = false) String name,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {

        EmployeePageQueryDTO employeePageQueryDTO = new EmployeePageQueryDTO();
        employeePageQueryDTO.setName(name);
        employeePageQueryDTO.setPage(page);
        employeePageQueryDTO.setPageSize(pageSize);

        log.info("员工分页查询：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }
    @PostMapping("/status/{status}")
    @Operation(summary = "启用/禁用修改")
    public Result changeStatus(@PathVariable Integer status, @RequestParam Long id) {
        log.info("启用禁用员工账号:{},{}", status, id);
        employeeService.empStatus(status, id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询")
    public Result<Employee> findEmployee(Long id) {
        log.info("检索员工ID：{}", id );
        Employee founded = employeeService.getById(id);
        return Result.success(founded);
    }
    @PutMapping
    @Operation(summary = "编辑员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.update(employeeDTO);
        return Result.success();
    }
}