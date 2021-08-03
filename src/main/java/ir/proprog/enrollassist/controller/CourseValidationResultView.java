package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.CourseRuleViolation;

import java.util.List;
import java.util.stream.Collectors;

public class CourseValidationResultView {
    List<String> violationMessages;

    public CourseValidationResultView(List<CourseRuleViolation> courseRuleViolations) {
        violationMessages = courseRuleViolations.stream().map(Object::toString).collect(Collectors.toList());
    }
}
