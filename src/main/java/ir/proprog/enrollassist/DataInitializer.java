package ir.proprog.enrollassist;


import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.enrollmentList.EnrollmentList;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.program.Program;
import ir.proprog.enrollassist.domain.section.ExamTime;
import ir.proprog.enrollassist.domain.section.Section;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.repository.*;
import lombok.AllArgsConstructor;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

//@Component
@AllArgsConstructor
public class DataInitializer {
    StudentRepository studentRepository;
    CourseRepository courseRepository;
    SectionRepository sectionRepository;
    EnrollmentListRepository enrollmentListRepository;
    MajorRepository majorRepository;
    ProgramRepository programRepository;

    @PostConstruct
    public void populateCourses() throws Exception {
        Course math1 = new Course("4444444", "MATH1", 3, "Undergraduate");
        Course phys1 = new Course("8888888", "PHYS1", 3, "Undergraduate");
        Course prog = new Course("7777777", "PROG", 4, "Undergraduate");
        Course math2 = new Course("6666666", "MATH2", 3, "Undergraduate").withPre(math1);
        Course phys2 = new Course("9999999", "PHYS2", 3, "Undergraduate").withPre(math1, phys1);
        Course ap = new Course("2222222", "AP", 3, "Undergraduate").withPre(prog);
        Course dm = new Course("3333333", "DM", 3, "Undergraduate").withPre(math1);
        Course economy = new Course("1111111", "ECO", 3, "Undergraduate");
        Course maaref = new Course("5555555", "MAAREF", 2, "Undergraduate");
        Course farsi = new Course("1212121", "FA", 2, "Undergraduate");
        Course english = new Course("1010101", "EN", 2, "Undergraduate");
        Course akhlagh = new Course("1111110", "AKHLAGH", 2, "Undergraduate");
        Course karafarini = new Course("1313131", "KAR", 3, "Undergraduate");
        courseRepository.saveAll(List.of(math1, phys1, prog, math2, phys2, ap, dm, economy, maaref, farsi, english, akhlagh, karafarini));

        Major ce = new Major("8101", "CE", "Engineering");
        Major ee = new Major("8101", "EE", "Engineering");
//        ee.addCourse();
        majorRepository.saveAll(List.of(ce, ee));


        Program ceProgram = new Program(ce, "Undergraduate", 140, 140, "Major");
        ceProgram.addCourse(math1, math2, phys1, phys2);
        Program eeProgram = new Program(ee, "Undergraduate", 140, 140, "Major");
        eeProgram.addCourse(math1, math2, phys1, phys2);
        programRepository.saveAll(List.of(ceProgram, eeProgram));


        Student mahsa = new Student("810199999", "Undergraduate")
                .setGrade("11112", math1, 10)
                .setGrade("11112", phys1, 12)
                .setGrade("11112", prog, 16.3)
                .setGrade("11112", farsi, 18.5)
                .setGrade("11112", akhlagh, 15);
        mahsa.addProgram(ceProgram);
        studentRepository.save(mahsa);
        Student changiz = new Student("810199998", "Undergraduate")
                .setGrade("11112", math1, 13.2)
                .setGrade("11112", phys1, 8.3)
                .setGrade("11112", prog, 10.5)
                .setGrade("11112", english, 11)
                .setGrade("11112", akhlagh, 16);
        changiz.addProgram(eeProgram);
        studentRepository.save(changiz);

        ExamTime exam0 = new ExamTime("2021-07-10T09:00", "2021-07-10T11:00");
        ExamTime exam1 = new ExamTime("2021-07-11T09:00", "2021-07-11T11:00");
        ExamTime exam2 = new ExamTime("2021-07-12T09:00", "2021-07-12T11:00");
        ExamTime exam3 = new ExamTime("2021-07-13T09:00", "2021-07-13T11:00");
        ExamTime exam4 = new ExamTime("2021-07-14T09:00", "2021-07-14T11:00");
        ExamTime exam5 = new ExamTime("2021-07-15T09:00", "2021-07-15T11:00");
        ExamTime exam6 = new ExamTime("2021-07-16T09:00", "2021-07-16T11:00");
        ExamTime exam7 = new ExamTime("2021-07-17T09:00", "2021-07-17T11:00");

        Section math1_1 = new Section(math1, "01", exam0, Collections.emptySet()); sectionRepository.save(math1_1);
        Section phys1_1 = new Section(phys1, "01", exam1, Collections.emptySet()); sectionRepository.save(phys1_1);
        Section math2_1 = new Section(math2, "01", exam2, Collections.emptySet()); sectionRepository.save(math2_1);
        Section math2_2 = new Section(math2, "02", exam3, Collections.emptySet()); sectionRepository.save(math2_2);
        Section phys2_1 = new Section(phys2, "01", exam4, Collections.emptySet()); sectionRepository.save(phys2_1);
        Section phys2_2 = new Section(phys2, "02", exam5, Collections.emptySet()); sectionRepository.save(phys2_2);
        Section ap_1 = new Section(ap, "01", exam6, Collections.emptySet()); sectionRepository.save(ap_1);
        Section dm_1 = new Section(dm, "01", exam7, Collections.emptySet()); sectionRepository.save(dm_1);
        Section akhlagh_1 = new Section(akhlagh, "01" ,exam0, Collections.emptySet()); sectionRepository.save(akhlagh_1);
        Section english_1 = new Section(english, "01", exam1, Collections.emptySet()); sectionRepository.save(english_1);
        // Section buggy = new Section(null, "01"); //sectionRepository.save(buggy);

        EnrollmentList mahsaList = new EnrollmentList("Mahsa's List", mahsa);
        mahsaList.addSections(math2_1, phys2_2, ap_1, dm_1);
        enrollmentListRepository.save(mahsaList);

        EnrollmentList changizList = new EnrollmentList("Changiz's List", changiz);
        changizList.addSections(math2_1, phys1_1, ap_1, dm_1);
        enrollmentListRepository.save(changizList);

    }
}
