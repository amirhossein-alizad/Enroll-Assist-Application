package ir.proprog.enrollassist.domain.Exception;

public class InvalidTimeSyntax extends CourseScheduleException{

    @Override
    public String getMessage() {return "Section time is not valid.";}
}
