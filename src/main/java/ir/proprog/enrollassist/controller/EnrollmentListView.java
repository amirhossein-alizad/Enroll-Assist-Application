package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.EnrollmentList;

public class EnrollmentListView {
    private Long enrollmentListId;
    private String enrollmentListName;

    public EnrollmentListView() {
    }

    public EnrollmentListView(EnrollmentList enrollmentList) {
        this.enrollmentListId = enrollmentList.getId();
        this.enrollmentListName = enrollmentList.getListName();
    }

}
