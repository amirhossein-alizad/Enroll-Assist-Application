package ir.proprog.enrollassist.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class StudentNumber {
    private String number;

    public StudentNumber(String studentNumber) {
        if(studentNumber.equals(""))
            throw new IllegalArgumentException("Student number can not be empty.");
        this.number = studentNumber;
    }

    @Override
    public int hashCode() { return Objects.hash(number); }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentNumber stdNo = (StudentNumber) o;
        return number.equals(stdNo.number);
    }
}