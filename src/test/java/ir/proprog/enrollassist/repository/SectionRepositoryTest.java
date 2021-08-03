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
    private TestDataInitializer testDataInitializer;
    @Autowired
    private SectionRepository sectionRepository;

    @BeforeEach
    public void populate() {
        testDataInitializer.populate();
    }

    @AfterEach
    public void cleanUp() {
        testDataInitializer.deleteAll();
    }

    @Test
    public void Unreal_section_not_found() {
        List<Section> findSections =  sectionRepository.findSectionsBySectionNumber(20L, "01");
        assertEquals(findSections.size(), 0);
    }

    @Test
    public void Section_find_correctly() {
        List<Section> findSections =  sectionRepository.findSectionsBySectionNumber(1L, "01");
        assertEquals(findSections.size(), 1);
        assertEquals(findSections.get(0).getSectionNo(), "01");
        assertEquals(findSections.get(0).getCourse().getId(), 1L);
    }
}
