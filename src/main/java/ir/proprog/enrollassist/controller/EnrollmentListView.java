package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.EnrollmentList;
import lombok.Getter;

@Getter
public class EnrollmentListView {
    private Long enrollmentListId;
    private String enrollmentListName;
    private String message;

    public EnrollmentListView() {
    }

    public EnrollmentListView(EnrollmentList enrollmentList) {
        this.enrollmentListId = enrollmentList.getId();
        this.enrollmentListName = enrollmentList.getListName();
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
