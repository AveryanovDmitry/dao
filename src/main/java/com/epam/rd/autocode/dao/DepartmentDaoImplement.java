package com.epam.rd.autocode.dao;

import com.epam.rd.autocode.ConnectionSource;
import com.epam.rd.autocode.domain.Department;

import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentDaoImplement implements DepartmentDao {
    private static final String BY_ID = "SELECT * FROM department WHERE Id = ?";
    private static final String ALL = "SELECT * FROM department";
    private static final String INSERT = "INSERT INTO department VALUES (?, ?, ?)";
    private static final String UPDATE = "UPDATE department SET name = ?, location = ? WHERE Id = ?";
    private static final String DELETE = "DELETE FROM department WHERE Id = ?";

    @Override
    public Optional<Department> getById(BigInteger Id) {
        Optional<Department> department = Optional.empty();
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(BY_ID)) {
            statement.setInt(1, Id.intValue());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString(2);
                    String location = rs.getString(3);
                    department = Optional.of(new Department(Id, name, location));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return department;
    }

    @Override
    public List<Department> getAll() {
        List<Department> list = new ArrayList<>();
        try (Connection connection = ConnectionSource.instance().createConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(ALL)) {
            while (rs.next()) {
                int Id = rs.getInt(1);
                String name = rs.getString(2);
                String location = rs.getString(3);
                Department department = new Department(BigInteger.valueOf(Id), name, location);
                list.add(department);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Department save(Department department) {
        if (getById(department.getId()).isPresent()) {
            try (Connection connection = ConnectionSource.instance().createConnection();
                 PreparedStatement statement = connection.prepareStatement(UPDATE)) {
                statement.setString(1, department.getName());
                statement.setString(2, department.getLocation());
                statement.setInt(3, department.getId().intValue());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try (Connection connection = ConnectionSource.instance().createConnection();
                 PreparedStatement statement = connection.prepareStatement(INSERT)) {
                statement.setInt(1, department.getId().intValue());
                statement.setString(2, department.getName());
                statement.setString(3, department.getLocation());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return department;
    }

    @Override
    public void delete(Department department) {
        try (Connection connection = ConnectionSource.instance().createConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setInt(1, department.getId().intValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
