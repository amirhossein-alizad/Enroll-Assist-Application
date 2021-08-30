package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.controller.SectionView;
import ir.proprog.enrollassist.controller.StudentView;
import ir.proprog.enrollassist.domain.EnrollmentList;
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
