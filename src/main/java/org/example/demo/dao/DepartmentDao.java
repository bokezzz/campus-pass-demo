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

/**
 * 部门 DAO。
 *
 * <p>负责部门的增删改查。部门数据被公务预约引用，
 * 后台管理员维护部门后，手机端公务预约表单会读取这些部门供用户选择。</p>
 */
public class DepartmentDao {
    /** 查询全部部门，用于后台列表和手机端公务预约下拉框。 */
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

    /** 按主键查询部门，用于后台“修改部门”时回显原始数据。 */
    public Department findById(long id) throws SQLException {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("select * from departments where id=?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? map(resultSet) : null;
            }
        }
    }

    /**
     * 保存部门。
     *
     * <p>id 为空表示新增；id 不为空表示修改。这种写法可以让新增和编辑共用同一个表单。</p>
     */
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

    /** 删除部门。演示系统中直接删除；真实系统可改成逻辑删除以保留历史。 */
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
