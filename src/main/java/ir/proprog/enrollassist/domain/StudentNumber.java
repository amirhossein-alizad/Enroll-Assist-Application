package ir.proprog.enrollassist.domain;

import lombok.Getter;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Embeddable
public class StudentNumber {
    private final String studentNumber;

    public StudentNumber(String studentNumber) {
        if(studentNumber.equals(""))
            throw new IllegalArgumentException("Student number can not be empty.");
        this.studentNumber = studentNumber;
    }

    @Override
    public int hashCode() { return Objects.hash(studentNumber); }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentNumber stdNo = (StudentNumber) o;
        return studentNumber.equals(stdNo.studentNumber);
    }
}