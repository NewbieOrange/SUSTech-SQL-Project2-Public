package cn.edu.sustech.cs307.dto.grade;

public enum PassOrFailGrade implements Grade {
    PASS, FAIL;

    @Override
    public <R> R when(Cases<R> cases) {
        return cases.match(this);
    }
}
