package com.epam.rd.autocode.dao;

import com.epam.rd.autocode.ConnectionSource;
import com.epam.rd.autocode.domain.Department;
import com.epam.rd.autocode.domain.Employee;
import com.epam.rd.autocode.domain.FullName;
import com.epam.rd.autocode.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDaoImplement implements EmployeeDao {
    private static final String BY_ID = "SELECT * FROM employee WHERE Id = ?";
    private static final String ALL = "SELECT * FROM employee";
    private static final String INSERT = "INSERT INTO employee VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE employee SET firstname = ?, lastname = ?, middlename = ?, " +
            "position = ?, manager = ?, hiredate = ?, salary = ?, department = ? WHERE Id = ?";
    private static final String DELETE = "DELETE FROM employee WHERE Id = ?";
    private static final String BY_DEPARTMENT = "SELECT * FROM employee WHERE department = ?";
    private static final String BY_MANAGER = "SELECT * FROM employee WHERE manager = ?";

    @Override
    public Optional<Employee> getById(BigInteger id) {
        Optional<Employee> employee = Optional.empty();
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(BY_ID)) {
            statement.setInt(1, id.intValue());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    employee = Optional.of(createEmployee(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }


    @Override
    public List<Employee> getAll() {
        List<Employee> employees = new ArrayList<>();
        try (Connection connection = ConnectionSource.instance().createConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(ALL)) {
            while (rs.next()) {
                Employee employee = createEmployee(rs);
                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    private Employee createEmployee (ResultSet rs) throws SQLException {
        BigInteger id = BigInteger.valueOf(rs.getLong("Id"));
        String firstname = rs.getString("firstname");
        String lastname = rs.getString("lastname");
        String middlename = rs.getString("middlename");
        String position = rs.getString("position");
        String hired = rs.getDate("hiredate").toString();
        BigDecimal salary = rs.getBigDecimal("salary");
        BigInteger manager = BigInteger.valueOf(rs.getLong("manager"));
        BigInteger department = BigInteger.valueOf(rs.getLong("department"));
        Employee employee = new Employee(id, new FullName(firstname, lastname, middlename),
                Position.valueOf(position), LocalDate.parse(hired), salary, manager, department);
        return employee;
    }

    @Override
    public Employee save(Employee employee) {
        if (getById(employee.getId()).isPresent()) {
            try (Connection connection = ConnectionSource.instance().createConnection();
                 PreparedStatement statement = connection.prepareStatement(UPDATE)) {
                statement.setString(1, employee.getFullName().getFirstName());
                statement.setString(2, employee.getFullName().getLastName());
                statement.setString(3, employee.getFullName().getMiddleName());
                statement.setString(4, employee.getPosition().toString());
                statement.setInt(5, employee.getManagerId().intValue());
                statement.setDate(6, Date.valueOf(employee.getHired()));
                statement.setDouble(7, employee.getSalary().doubleValue());
                statement.setInt(8, employee.getDepartmentId().intValue());
                statement.setInt(9, employee.getId().intValue());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try (Connection connection = ConnectionSource.instance().createConnection();
                 PreparedStatement statement = connection.prepareStatement(INSERT)) {
                statement.setInt(1, employee.getId().intValue());
                statement.setString(2, employee.getFullName().getFirstName());
                statement.setString(3, employee.getFullName().getLastName());
                statement.setString(4, employee.getFullName().getMiddleName());
                statement.setString(5, employee.getPosition().toString());
                statement.setInt(6, employee.getManagerId().intValue());
                statement.setDate(7, Date.valueOf(employee.getHired()));
                statement.setDouble(8, employee.getSalary().doubleValue());
                statement.setInt(9, employee.getDepartmentId().intValue());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return employee;
    }

    @Override
    public void delete(Employee employee) {
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setInt(1, employee.getId().intValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Employee> getByDepartment(Department department) {
        List<Employee> employees = new ArrayList<>();
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(BY_DEPARTMENT)) {
            statement.setInt(1, department.getId().intValue());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Employee employee = createEmployee(rs);
                    employees.add(employee);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    @Override
    public List<Employee> getByManager(Employee employee) {
        List<Employee> employees = new ArrayList<>();
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(BY_MANAGER)) {
            statement.setInt(1, employee.getId().intValue());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Employee worker = createEmployee(rs);
                    employees.add(worker);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }
}
