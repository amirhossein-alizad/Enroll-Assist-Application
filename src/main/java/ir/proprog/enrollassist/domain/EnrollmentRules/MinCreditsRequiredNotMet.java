package ir.proprog.enrollassist.domain.EnrollmentRules;

public class MinCreditsRequiredNotMet extends EnrollmentRuleViolation{
    private int minCredits;

    public MinCreditsRequiredNotMet(int minCredits){this.minCredits = minCredits;}

    @Override
    public String toString(){return String.format("Minimum number of credits(%d) is not met.", minCredits);}
}
