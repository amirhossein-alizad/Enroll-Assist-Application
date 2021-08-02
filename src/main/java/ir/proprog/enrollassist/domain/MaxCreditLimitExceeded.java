package ir.proprog.enrollassist.domain;

public class MaxCreditLimitExceeded extends EnrollmentRuleViolation {
    private int limit;

    public MaxCreditLimitExceeded(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return String.format("Maximum number of credits is %s", limit);
    }
}
