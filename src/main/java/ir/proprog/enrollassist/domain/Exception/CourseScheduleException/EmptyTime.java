package ir.proprog.enrollassist.domain.Exception.CourseScheduleException;

public class EmptyTime extends CourseScheduleException{

    @Override
    public String getMessage() {return "Time can not be empty.";}
}
