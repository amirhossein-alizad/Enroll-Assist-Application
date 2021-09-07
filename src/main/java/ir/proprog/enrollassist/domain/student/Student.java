package ir.proprog.enrollassist.domain.student;

import com.google.common.annotations.VisibleForTesting;
import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.program.Program;
import ir.proprog.enrollassist.domain.section.Section;
import ir.proprog.enrollassist.domain.studyRecord.Grade;
import ir.proprog.enrollassist.domain.studyRecord.StudyRecord;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // as required by JPA, don't use it in your code
@Getter
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private GraduateLevel graduateLevel;
    @Embedded
    private StudentNumber studentNumber;
    @OneToMany(cascade = CascadeType.ALL)
    private Set<StudyRecord> grades = new HashSet<>();
    private String name;
    @ManyToMany
    private Set<Program> programs = new HashSet<>();

    @OneToMany
    private List<Student> pending = new ArrayList<>();
    @OneToMany
    private List<Student> requested = new ArrayList<>();
    @OneToMany
    private List<Student> friends = new ArrayList<>();
    @OneToMany
    private List<Student> blocked = new ArrayList<>();


    public Student(@NonNull String studentNumber, @NonNull String name) {
        this.studentNumber = new StudentNumber(studentNumber);
        this.name = name;
    }

    public Student(@NonNull String studentNumber, @NonNull String name, @NonNull String graduateLevel) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        try {
            this.studentNumber = new StudentNumber(studentNumber);
        } catch (Exception e) { exceptionList.addNewException(e); }
        if (name.equals(""))
            exceptionList.addNewException(new Exception("Student name can not be empty."));
        try {
            this.graduateLevel = GraduateLevel.valueOf(graduateLevel);
        } catch (Exception e) { exceptionList.addNewException(new Exception("Graduate level is not valid.")); }

        if (exceptionList.hasException())
            throw exceptionList;

        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return studentNumber.equals(student.studentNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentNumber);
    }


    public boolean hasPassed(Course course) {
        for (StudyRecord sr : grades) {
            if (sr.getCourse().equals(course))
                return sr.isPassed(this.graduateLevel);
        }
        return false;
    }

    public void addProgram(Program program) { this.programs.add(program); }

    public Student setGrade(String term, Course course, double grade) throws ExceptionList {
        grades.add(new StudyRecord(term, course, grade));
        return this;
    }

    public int getTotalTakenCredits() {
        return grades.stream().mapToInt(e -> e.getCourse().getCredits()).sum();
    }

    public Grade calculateGPA() {
        double sum = grades.stream().mapToDouble(StudyRecord::weightedScore).sum();
        int credits = grades.stream().mapToInt(sr -> sr.getCourse().getCredits()).sum();
        if (credits == 0) return Grade.ZERO;
        try {
            return new Grade(sum / credits);
        } catch (Exception e) {
            return Grade.ZERO;
        }
    }

    @VisibleForTesting
    List<Course> getTakeableCourses(){
        Set<Course> passed = grades.stream().filter(sr -> sr.isPassed(this.graduateLevel)).map(StudyRecord::getCourse).collect(Collectors.toSet());
        Set<Course> all = new HashSet<>();
        for (Program p: programs)
            all.addAll(p.getCourses());
        all.removeAll(passed);
        return all.stream().filter(course -> course.canBeTakenBy(this).isEmpty()).collect(Collectors.toList());
    }

    public List<Section> getTakeableSections(Iterable<Section> allSections){
        List<Course> courses = getTakeableCourses();
        List<Section> all = StreamSupport.stream(allSections.spliterator(), false).collect(Collectors.toList());
        return all.stream().filter(section -> courses.contains(section.getCourse())).collect(Collectors.toList());
    }

    public void sendFriendshipRequest(Student other) throws Exception {
        if (this.friends.contains(other))
            throw new Exception("This user is already your friend.");
        else if (this.requested.contains(other))
            throw new Exception("You requested to this user before.");
        else if (this.pending.contains(other))
            throw new Exception("This user requested first.");
        else if(this.blocked.contains(other))
            throw new Exception("You have blocked this user.");
        else if (this.equals(other))
            throw new Exception("You cannot send friendship request to yourself.");

        this.pending.add(other);
    }

    public void receiveFriendshipRequest(Student other) throws Exception {
        if(this.blocked.contains(other))
            throw new Exception("You have been blocked by this user.");

        this.requested.add(other);
    }

    public void removeFriend(Student other) throws Exception {
        if (this.requested.contains(other))
            this.requested.remove(other);
        else if (this.pending.contains(other))
            this.pending.remove(other);
        else if (this.friends.contains(other))
            this.friends.remove(other);
        else if (this.blocked.contains(other))
            this.blocked.remove(other);
        else
            throw new Exception("There is no relation between these students.");
    }

    public List<Student> getAllFriends() {
        List<Student> allFriends = new ArrayList<>();
        allFriends.addAll(this.friends);
        allFriends.addAll(this.requested);
        allFriends.addAll(this.blocked);
        allFriends.addAll(this.pending);
        return allFriends;
    }

    public void acceptRequest(Student other) throws Exception {
        if (this.requested.contains(other)) {
            this.requested.remove(other);
            this.friends.add(other);
        }
        else
            throw new Exception("This user did not request to be your friend.");

    }

    public void addFriend(Student other) {
        this.pending.remove(other);
        this.friends.add(other);
    }

    public Student blockFriend(Student other) throws Exception {
        if (this.friends.contains(other)) {
            this.friends.remove(other);
            this.blocked.add(other);
            return this;
        }
        else
            throw new Exception("This student is not your friend.");
    }

    public Student unblockFriend(Student other) throws Exception{
        if (this.blocked.contains(other)) {
            this.blocked.remove(other);
            return this;
        }
        else
            throw new Exception("This user is not blocked.");
    }

    public List<Student> getFriendsWhoDoesntBlock() {
        List<Student> friendStudents = new ArrayList<>();
        for (Student s: this.friends)
            if (s.friends.contains(this))
                friendStudents.add(s);
        return friendStudents;
    }

}
