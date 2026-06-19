package org.example.demo.dao;

import org.example.demo.model.Department;
import org.example.demo.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDao {
    public List<Department> findAll() throws SQLException {
        List<Department> departments = new ArrayList<>();
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("select * from departments order by id");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                departments.add(map(resultSet));
            }
        }
        return departments;
    }

    public Department findById(long id) throws SQLException {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("select * from departments where id=?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? map(resultSet) : null;
            }
        }
    }

    public void save(Department department) throws SQLException {
        if (department.getId() == null) {
            try (Connection connection = DbUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement("insert into departments(code,type,name) values(?,?,?)")) {
                fill(statement, department);
                statement.executeUpdate();
            }
            return;
        }
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("update departments set code=?, type=?, name=? where id=?")) {
            fill(statement, department);
            statement.setLong(4, department.getId());
            statement.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("delete from departments where id=?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private void fill(PreparedStatement statement, Department department) throws SQLException {
        statement.setString(1, department.getCode());
        statement.setString(2, department.getType());
        statement.setString(3, department.getName());
    }

    private Department map(ResultSet resultSet) throws SQLException {
        Department department = new Department();
        department.setId(resultSet.getLong("id"));
        department.setCode(resultSet.getString("code"));
        department.setType(resultSet.getString("type"));
        department.setName(resultSet.getString("name"));
        return department;
    }
}
