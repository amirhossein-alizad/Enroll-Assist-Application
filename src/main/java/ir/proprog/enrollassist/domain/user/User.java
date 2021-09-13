package ir.proprog.enrollassist.domain.user;

import ir.proprog.enrollassist.domain.student.Student;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // as required by JPA, don't use it in your code
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(cascade = CascadeType.MERGE)
    private Set<Student> students = new HashSet<>();

    public void addStudent(Student ... student){
        students.addAll(Arrays.asList(student));
    }

}
