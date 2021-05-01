package cn.edu.sustech.cs307.dto.grade;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GradeTest {
    @Test
    public void matchGrade() {
        Grade grade1 = PassOrFailGrade.PASS;
        assertEquals("PASS", grade1.when(new Grade.Cases<String>() {
            @Override
            public String match(PassOrFailGrade self) {
                return self.name();
            }

            @Override
            public String match(HundredMarkGrade self) {
                return Short.toString(self.mark);
            }
        }));

        final boolean[] reached = {false};
        Grade grade2 = new HundredMarkGrade((short) 100);
        grade2.when(new Grade.Cases<Void>() {
            @Override
            public Void match(PassOrFailGrade self) {
                fail("Should not enter this path");
                return null;
            }

            @Override
            public Void match(HundredMarkGrade self) {
                reached[0] = true;
                return null;
            }
        });
        assertTrue(reached[0]);
    }
}
