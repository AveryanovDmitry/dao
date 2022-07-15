package com.epam.rd.autocode.dao;

public class DaoFactory {
    public EmployeeDao employeeDAO() {
        return new EmployeeDaoImplement();
    }

    public DepartmentDao departmentDAO() {
        return new DepartmentDaoImplement();
    }
}
