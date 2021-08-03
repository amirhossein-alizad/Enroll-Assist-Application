package ir.proprog.enrollassist.domain;

public class PrerequisitesHaveLoop extends CourseRuleViolation{
    String course;
    public PrerequisitesHaveLoop(String course){ this.course = course;}

    @Override
    public String toString(){return String.format("%s has made a loop in prerequisites.", course);}
}
