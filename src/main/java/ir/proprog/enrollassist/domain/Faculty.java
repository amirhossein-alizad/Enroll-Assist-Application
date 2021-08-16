package ir.proprog.enrollassist.domain;

import com.sun.istack.NotNull;
import ir.proprog.enrollassist.Exception.ExceptionList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter

public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String facultyName;
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Major> majors = new HashSet<>();

    public Faculty(String facultyName) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        if(facultyName.equals("")){
            exceptionList.addNewException(new Exception("Faculty name can not be Empty."));
            throw exceptionList;
        }
        this.facultyName = facultyName;
    }

    @Override
    public int hashCode() { return Objects.hash(facultyName); }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faculty faculty = (Faculty) o;
        return facultyName.equals(faculty.facultyName);
    }

    public void addMajor(Major ... major){
        this.majors.addAll(Arrays.asList(major));
    }
}