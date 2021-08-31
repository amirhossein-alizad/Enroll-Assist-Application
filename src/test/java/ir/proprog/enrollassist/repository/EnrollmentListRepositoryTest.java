package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.controller.section.SectionDemandView;
import ir.proprog.enrollassist.util.TestDataInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
public class EnrollmentListRepositoryTest {
    @Autowired
    private TestDataInitializer testDataInitializer;
    @Autowired
    private EnrollmentListRepository enrollmentListRepository;
    @Autowired
    private SectionRepository sectionRepository;

    @BeforeEach
    public void populate () throws Exception {
        testDataInitializer.populate();
    }

    @AfterEach
    public void cleanUp() {
        testDataInitializer.deleteAll();
    }

    @Test
    public void Demand_for_all_sections_for_two_students_is_correct() {
        List<SectionDemandView> demands = enrollmentListRepository.findDemandForAllSections();
        for (SectionDemandView demand : demands) {
            demand.setSectionView(sectionRepository.findById(demand.getSectionId()).orElseThrow());
        }
        assertThat(demands)
                .extracting("sectionView.courseTitle", "sectionView.sectionNo", "demand")
                .containsExactlyInAnyOrder(
                        tuple("MATH2", "01", 2L),
                        tuple("PHYS1", "01", 1L),
                        tuple("PHYS2", "02", 1L),
                        tuple("AP", "01", 2L),
                        tuple("DM", "01", 2L)
                );
    }
}
