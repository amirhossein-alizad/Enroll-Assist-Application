package ir.proprog.enrollassist.controller.Exception.CourseException;

public class PrerequisiteCourseNotFound extends CourseException {
    private Long id;

    public PrerequisiteCourseNotFound(Long id){this.id = id;}

    @Override
    public String getMessage() {return String.format("Course with id = %s was not found.", id);}
}
