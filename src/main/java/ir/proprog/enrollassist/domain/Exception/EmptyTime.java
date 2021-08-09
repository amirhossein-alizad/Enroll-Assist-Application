package ir.proprog.enrollassist.domain.Exception;

public class EmptyTime extends CourseScheduleException{

    @Override
    public String getMessage() {return "Time can not be empty.";}
}
