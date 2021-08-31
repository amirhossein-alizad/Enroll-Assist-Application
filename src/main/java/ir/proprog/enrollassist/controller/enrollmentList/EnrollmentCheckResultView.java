package ir.proprog.enrollassist.controller.enrollmentList;

import ir.proprog.enrollassist.domain.EnrollmentRules.EnrollmentRuleViolation;

import java.util.List;
import java.util.stream.Collectors;

public class EnrollmentCheckResultView {
    List<String> violationMessages;

    public EnrollmentCheckResultView(List<EnrollmentRuleViolation> enrollmentRuleViolations) {
        violationMessages = enrollmentRuleViolations.stream().map(Object::toString).collect(Collectors.toList());
    }
}
