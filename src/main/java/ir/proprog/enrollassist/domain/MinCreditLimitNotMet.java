package ir.proprog.enrollassist.domain;

public class MinCreditLimitNotMet extends EnrollmentRuleViolation {

    @Override
    public String toString() {
        return String.format("Taking at least 12 credits in each enrollment list is required.");
    }
}
