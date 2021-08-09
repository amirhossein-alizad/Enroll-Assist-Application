package ir.proprog.enrollassist.domain.Exception.CourseScheduleException;

public class InvalidDayOfWeek extends CourseScheduleException{
    private String day;

    public InvalidDayOfWeek(String day) {
        this.day = day;
    }

    @Override
    public String getMessage() {return String.format("%s is not valid week day.", this.day);}
}
