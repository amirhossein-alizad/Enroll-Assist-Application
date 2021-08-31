package ir.proprog.enrollassist.domain.student;

import lombok.Getter;
import lombok.Value;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Embeddable
@Value
public class StudentNumber {
    String number;
    public static final StudentNumber DEFAULT = new StudentNumber();

    public StudentNumber(){
        number = "000000000";
    }
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