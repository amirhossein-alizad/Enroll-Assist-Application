package ir.proprog.enrollassist.controller.Exception.CourseException;

public class PrerequisiteCourseNotFound extends CourseException {
    private Long id;

    public PrerequisiteCourseNotFound(Long id){this.id = id;}

    @Override
    public String toString() {return String.format("Course with id = %s was not found.", id);}
}
