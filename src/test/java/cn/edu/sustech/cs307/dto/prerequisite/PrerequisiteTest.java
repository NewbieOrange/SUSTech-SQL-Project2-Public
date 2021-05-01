package cn.edu.sustech.cs307.dto.prerequisite;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PrerequisiteTest {
    @Test
    public void testTransformation() {
        Prerequisite calculus = new OrPrerequisite(List.of(
                new CoursePrerequisite("MA101A"),
                new CoursePrerequisite("MA101B")
        ));
        Prerequisite algebra = new CoursePrerequisite("MA103A");
        Prerequisite prerequisite = new AndPrerequisite(List.of(calculus, algebra));

        String expression = prerequisite.when(new Prerequisite.Cases<>() {
            @Override
            public String match(AndPrerequisite self) {
                String[] children = self.terms.stream()
                        .map(term -> term.when(this))
                        .toArray(String[]::new);
                return '(' + String.join(" AND ", children) + ')';
            }

            @Override
            public String match(OrPrerequisite self) {
                String[] children = self.terms.stream()
                        .map(term -> term.when(this))
                        .toArray(String[]::new);
                return '(' + String.join(" OR ", children) + ')';
            }

            @Override
            public String match(CoursePrerequisite self) {
                return self.courseID;
            }
        });
        assertEquals("((MA101A OR MA101B) AND MA103A)", expression);
    }
}
