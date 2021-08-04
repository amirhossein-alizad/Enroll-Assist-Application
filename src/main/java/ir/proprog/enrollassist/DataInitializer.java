package ir.proprog.enrollassist;


import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.EnrollmentList;
import ir.proprog.enrollassist.domain.Section;
import ir.proprog.enrollassist.domain.Student;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import ir.proprog.enrollassist.repository.StudentRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class DataInitializer {
    StudentRepository studentRepository;
    CourseRepository courseRepository;
    SectionRepository sectionRepository;
    EnrollmentListRepository enrollmentListRepository;

    public DataInitializer(StudentRepository studentRepository, CourseRepository courseRepository, SectionRepository sectionRepository, EnrollmentListRepository enrollmentListRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.enrollmentListRepository = enrollmentListRepository;
    }

    @PostConstruct
    public void populateCourses() {
        Course math1 = new Course("4", "MATH1", 3);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course prog = new Course("7", "PROG", 4);
        Course math2 = new Course("6", "MATH2", 3).withPre(math1);
        Course phys2 = new Course("9", "PHYS2", 3).withPre(math1, phys1);
        Course ap = new Course("2", "AP", 3).withPre(prog);
        Course dm = new Course("3", "DM", 3).withPre(math1);
        Course economy = new Course("1", "ECO", 3);
        Course maaref = new Course("5", "MAAREF", 2);
        Course farsi = new Course("12", "FA", 2);
        Course english = new Course("10", "EN", 2);
        Course akhlagh = new Course("11", "AKHLAGH", 2);
        Course karafarini = new Course("13", "KAR", 3);
        courseRepository.saveAll(List.of(math1, phys1, prog, math2, phys2, ap, dm, economy, maaref, farsi, english, akhlagh, karafarini));

        Student mahsa = new Student("810199999", "Mahsa Mahsaei")
                .setGrade("t1", math1, 10)
                .setGrade("t1", phys1, 12)
                .setGrade("t1", prog, 16.3)
                .setGrade("t1", farsi, 18.5)
                .setGrade("t1", akhlagh, 15);
        studentRepository.save(mahsa);
        Student changiz = new Student("810199998", "Changiz Changizi")
                .setGrade("t1", math1, 13.2)
                .setGrade("t1", phys1, 8.3)
                .setGrade("t1", prog, 10.5)
                .setGrade("t1", english, 11)
                .setGrade("t1", akhlagh, 16);
        studentRepository.save(changiz);

        Section math1_1 = new Section(math1, "01"); sectionRepository.save(math1_1);
        Section phys1_1 = new Section(phys1, "01"); sectionRepository.save(phys1_1);
        Section math2_1 = new Section(math2, "01"); sectionRepository.save(math2_1);
        Section math2_2 = new Section(math2, "02"); sectionRepository.save(math2_2);
        Section phys2_1 = new Section(phys2, "01"); sectionRepository.save(phys2_1);
        Section phys2_2 = new Section(phys2, "02"); sectionRepository.save(phys2_2);
        Section ap_1 = new Section(ap, "01"); sectionRepository.save(ap_1);
        Section dm_1 = new Section(dm, "01"); sectionRepository.save(dm_1);
        Section akhlagh_1 = new Section(akhlagh, "01"); sectionRepository.save(akhlagh_1);
        Section english_1 = new Section(english, "01"); sectionRepository.save(english_1);
        // Section buggy = new Section(null, "01"); //sectionRepository.save(buggy);

        EnrollmentList mahsaList = new EnrollmentList("Mahsa's List", mahsa);
        mahsaList.addSections(math2_1, phys2_2, ap_1, dm_1);
        enrollmentListRepository.save(mahsaList);

        EnrollmentList changizList = new EnrollmentList("Changiz's List", changiz);
        changizList.addSections(math2_1, phys1_1, ap_1, dm_1);
        enrollmentListRepository.save(changizList);
    }
}
