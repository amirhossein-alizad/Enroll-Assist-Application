package ir.proprog.enrollassist.domain.major;


import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.GraduateLevel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Major {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String majorNumber;
    private String majorName;
    private Faculty faculty;

    public Major(String majorNumber, String majorName, String faculty) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        if(majorName.equals(""))
            exceptionList.addNewException(new Exception("Major name can not be empty."));
        if(majorNumber.equals(""))
            exceptionList.addNewException(new Exception("Major number can not be empty."));
        try {
            this.faculty = Faculty.valueOf(faculty);
        } catch (Exception e) { exceptionList.addNewException(new Exception("Faculty is not valid.")); }

        if(exceptionList.hasException())
            throw exceptionList;
        this.majorName = majorName;
        this.majorNumber = majorNumber;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Major major = (Major) o;
        return majorNumber.equals(major.majorNumber);
    }

    @Override
    public int hashCode() { return Objects.hash(majorNumber, majorName); }
}
