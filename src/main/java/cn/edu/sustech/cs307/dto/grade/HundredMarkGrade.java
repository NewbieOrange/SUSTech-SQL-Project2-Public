package cn.edu.sustech.cs307.dto.grade;

public final class HundredMarkGrade implements Grade {
    public final short mark;

    public HundredMarkGrade(short mark) {
        this.mark = mark;
    }

    @Override
    public <R> R when(Cases<R> cases) {
        return cases.match(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HundredMarkGrade that = (HundredMarkGrade) o;

        return mark == that.mark;
    }

    @Override
    public int hashCode() {
        return mark;
    }
}
