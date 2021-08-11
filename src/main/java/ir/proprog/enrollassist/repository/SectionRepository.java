package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.controller.SectionDemandView;
import ir.proprog.enrollassist.controller.SectionView;
import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.Section;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SectionRepository extends CrudRepository<Section, Long> {
    @Query(value = "select sec from Section sec where (sec.course.id = ?1) and (sec.sectionNo = ?2)")
    List<Section> findOneSectionOfSpecialCourse(Long courseId, String sectionNo);
}
