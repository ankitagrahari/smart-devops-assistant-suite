package dbt.ai.dto.multiagent;

public class TestCase {

    private String className;
    private String description;
    private String code;

    @Override
    public String toString() {
        return "TestCase{" +
                "className='" + className + '\'' +
                ", description='" + description + '\'' +
                ", code='" + code + '\'' +
                '}';
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TestCase(String className, String description, String code) {
        this.className = className;
        this.description = description;
        this.code = code;
    }
}
