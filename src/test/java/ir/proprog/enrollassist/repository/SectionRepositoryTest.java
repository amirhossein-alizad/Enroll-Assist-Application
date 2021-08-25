package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.Section;
import ir.proprog.enrollassist.util.TestDataInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.plaf.IconUIResource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class SectionRepositoryTest {
    @Autowired
    private SectionRepository sectionRepository;

    @Test
    public void Unreal_section_not_found() {
        List<Section> findSections =  sectionRepository.findOneSectionOfSpecialCourse(20L, "01");
        assertEquals(findSections.size(), 0);
    }

}
