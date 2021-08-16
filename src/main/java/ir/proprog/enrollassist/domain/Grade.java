package ir.proprog.enrollassist.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
public class Grade {
    private final double grade;

    public Grade() {
        this.grade = 0.0;
    }

    public Grade(double grade) throws Exception {
        if (grade < 0 || grade > 20)
            throw new Exception("Grade must be in range of (0, 20).");
        String[] number = Double.toString(grade).split("\\.");
        if (number.length > 1)
            if (number[1].length() > 2)
                throw new Exception("There should be at most 2 decimal places in grade.");
        this.grade = grade;
    }
}
