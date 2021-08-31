package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.domain.section.Section;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

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
