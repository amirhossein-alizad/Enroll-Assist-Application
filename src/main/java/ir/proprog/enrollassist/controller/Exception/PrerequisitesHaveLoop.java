package ir.proprog.enrollassist.controller.Exception;

import ir.proprog.enrollassist.Exception.CourseException;

public class PrerequisitesHaveLoop extends CourseException {
    String course;
    public PrerequisitesHaveLoop(String course){ this.course = course;}

    @Override
    public String getMessage(){return String.format("%s has made a loop in prerequisites.", course);}
}
