package ir.proprog.enrollassist.controller.enrollmentList;

import ir.proprog.enrollassist.controller.student.StudentView;
import ir.proprog.enrollassist.controller.section.SectionView;
import ir.proprog.enrollassist.domain.enrollmentList.EnrollmentList;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class EnrollmentListView {
    private Long enrollmentListId;
    private String enrollmentListName;
    private StudentView student;
    private List<SectionView> sections;

    public EnrollmentListView() {
    }

    public EnrollmentListView(EnrollmentList enrollmentList) {
        this.enrollmentListId = enrollmentList.getId();
        this.enrollmentListName = enrollmentList.getListName();
        this.student = new StudentView(enrollmentList.getOwner());
        this.sections = enrollmentList.getSections().stream().map(SectionView::new).collect(Collectors.toList());
    }

}
