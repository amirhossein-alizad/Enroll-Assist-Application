package ir.proprog.enrollassist.domain;


import com.sun.istack.NotNull;
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
    @ManyToMany
    Set<Course> courses = new HashSet<>();
//    @ManyToOne
//    Faculty faculty;

    public Major(@NotNull String majorNumber, String majorName){
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
    public int hashCode() { return Objects.hash(majorNumber); }

    public void addCourse(Course ... course){
        this.courses.addAll(Arrays.asList(course));
    }
}
