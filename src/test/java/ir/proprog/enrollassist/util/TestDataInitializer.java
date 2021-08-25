package ir.proprog.enrollassist.util;

import ir.proprog.enrollassist.domain.*;
import ir.proprog.enrollassist.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class TestDataInitializer {
    StudentRepository studentRepository;
    CourseRepository courseRepository;
    SectionRepository sectionRepository;
    EnrollmentListRepository enrollmentListRepository;
    MajorRepository majorRepository;

    public void populate() throws Exception{
        Course math1 = new Course("4444444", "MATH1", 3);
        Course phys1 = new Course("8888888", "PHYS1", 3);
        Course prog = new Course("7777777", "PROG", 4);
        Course math2 = new Course("6666666", "MATH2", 3).withPre(math1);
        Course phys2 = new Course("9999999", "PHYS2", 3).withPre(math1, phys1);
        Course ap = new Course("2222222", "AP", 3).withPre(prog);
        Course dm = new Course("3333333", "DM", 3).withPre(math1);
        Course economy = new Course("1111111", "ECO", 3);
        Course maaref = new Course("5555555", "MAAREF", 2);
        Course farsi = new Course("1212121", "FA", 2);
        Course english = new Course("1010101", "EN", 2);
        Course akhlagh = new Course("1122111", "AKHLAGH", 2);
        Course karafarini = new Course("1313131", "KAR", 3);
        courseRepository.saveAll(List.of(math1, phys1, prog, math2, phys2, ap, dm, economy, maaref, farsi, english, akhlagh, karafarini));

        Major ce = new Major("8101", "CE");
        Major ee = new Major("1101", "EE");
        ce.addCourse(ap, dm);
        ee.addCourse(math1, phys1);
        majorRepository.saveAll(List.of(ce, ee));

        Student mahsa = new Student("810199999", "Mahsa Mahsaei")
                .setGrade("13981", math1, 10)
                .setGrade("13981", phys1, 12)
                .setGrade("13981", prog, 16.3)
                .setGrade("13981", farsi, 18.5)
                .setGrade("13981", akhlagh, 15);
        studentRepository.save(mahsa);
        Student changiz = new Student("810199998", "Changiz Changizi")
                .setGrade("13981", math1, 13.2)
                .setGrade("13981", phys1, 8.3)
                .setGrade("13981", prog, 10.5)
                .setGrade("13981", english, 11)
                .setGrade("13981", akhlagh, 16);
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

        EnrollmentList mahsaList = new EnrollmentList("Mahsa's List", mahsa);
        mahsaList.addSections(math2_1, phys2_2, ap_1, dm_1);
        enrollmentListRepository.save(mahsaList);

        EnrollmentList changizList = new EnrollmentList("Changiz's List", changiz);
        changizList.addSections(math2_1, phys1_1, ap_1, dm_1);
        enrollmentListRepository.save(changizList);
    }

    public void deleteAll() {
        enrollmentListRepository.deleteAll();
        sectionRepository.deleteAll();
        studentRepository.deleteAll();
        courseRepository.deleteAll();
    }
}
