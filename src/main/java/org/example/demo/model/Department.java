package org.example.demo.model;

/**
 * 部门实体类，对应 departments 表。
 *
 * <p>用于后台部门管理，也用于公务预约选择访问部门。</p>
 */
public class Department {
    private Long id;
    private String code;
    private String type;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
