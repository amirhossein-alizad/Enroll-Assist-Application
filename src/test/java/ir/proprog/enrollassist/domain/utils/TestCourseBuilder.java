package ir.proprog.enrollassist.domain.utils;


import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.course.Course;

public class TestCourseBuilder {
    private String title;
    private String graduateLevel;
    private String courseNumber;
    private int credits;

    public TestCourseBuilder(){
        title = "course";
        graduateLevel = "Undergraduate";
        courseNumber = "8101546";
        credits = 3;
    }

    public TestCourseBuilder title(String _title){
        title = _title;
        return this;
    }

    public TestCourseBuilder graduateLevel(String _graduateLevel){
        graduateLevel = _graduateLevel;
        return this;
    }

    public TestCourseBuilder courseNumber(String _courseNumber){
        courseNumber = _courseNumber;
        return this;
    }

    public TestCourseBuilder credits(int _credits){
        credits = _credits;
        return this;
    }

    public Course build() throws ExceptionList {
        return new Course(courseNumber, title, credits, graduateLevel);
    }
}
