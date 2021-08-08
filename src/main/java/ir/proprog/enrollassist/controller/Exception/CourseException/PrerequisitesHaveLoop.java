package ir.proprog.enrollassist.controller.Exception.CourseException;

public class PrerequisitesHaveLoop extends CourseException {
    String course;
    public PrerequisitesHaveLoop(String course){ this.course = course;}

    @Override
    public String toString(){return String.format("%s has made a loop in prerequisites.", course);}
}
