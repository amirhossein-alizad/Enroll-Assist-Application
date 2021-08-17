package ir.proprog.enrollassist.domain;

import lombok.Getter;

@Getter
public class StudentNumber {
    private final String studentNumber;

    public StudentNumber(String studentNumber) {
        if(studentNumber.equals(""))
            throw new IllegalArgumentException("Student number can not be empty.");
        if(studentNumber.length() != 9)
            throw new IllegalArgumentException("Student number must be 9 digits.");
        this.studentNumber = studentNumber;
    }

    public String getMajorNumber() {return studentNumber.substring(0, 4);}

}