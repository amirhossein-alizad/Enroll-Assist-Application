package ir.proprog.enrollassist.domain.Exception;

public class InvalidTimeConcept extends CourseScheduleException{
    @Override
    public String getMessage() {return "End time can not be before start time.";}
}
