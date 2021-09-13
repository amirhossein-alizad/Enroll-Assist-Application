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
    private String name;
    @OneToMany(cascade = CascadeType.MERGE)
    private Set<Student> students = new HashSet<>();

    public User(String _name){
        if(_name == null)
            throw new IllegalArgumentException("Name can not be null.");
        if(_name.equals(""))
            throw new IllegalArgumentException("Name can not be empty.");
        name = _name;
    }

    public void addStudent(Student student) throws Exception {
        //Should this violation be checked?
        //Should all students of a user have the same name?
        //Should it also be equal to user's name?
        for(Student s : students)
            if(!s.getName().equals(student.getName()))
                throw new Exception("Student name can not be different.");
        students.add(student);
    }

}
