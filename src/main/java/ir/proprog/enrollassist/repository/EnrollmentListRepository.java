package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.controller.section.SectionDemandView;
import ir.proprog.enrollassist.domain.enrollmentList.EnrollmentList;
import ir.proprog.enrollassist.domain.student.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EnrollmentListRepository extends CrudRepository<EnrollmentList, Long> {
    @Query(value = "select new ir.proprog.enrollassist.controller.section.SectionDemandView(section.id, count(distinct list.owner)) from EnrollmentList list join list.sections as section group by section.id")
    List<SectionDemandView> findDemandForAllSections();

    @Query(value = "select list from EnrollmentList list join list.sections as section where section.id=?1")
    List<EnrollmentList> findEnrollmentListContainingSection(Long id);

    List<EnrollmentList> findByOwner(Student owner);
}
