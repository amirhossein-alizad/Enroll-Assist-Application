package ir.proprog.enrollassist.domain.EnrollmentRules;

public class MaxCreditsLimitExceeded extends EnrollmentRuleViolation{
    private int limit;

    public MaxCreditsLimitExceeded(int limit){
        this.limit = limit;
    }

    @Override
    public String toString() {
        return String.format("Maximum number of credits(%d) exceeded.", limit);
    }
}
