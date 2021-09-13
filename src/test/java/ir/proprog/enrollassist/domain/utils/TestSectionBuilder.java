package ir.proprog.enrollassist.domain.utils;

import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.section.Section;

public class TestSectionBuilder {
    private String sectioNumber;
    private Course course;

    public TestSectionBuilder(){
        sectioNumber = "01";
        course = null;
    }

    public TestSectionBuilder sectionNumber(String _sectioNumber){
        sectioNumber = _sectioNumber;
        return this;
    }

    public TestSectionBuilder course(Course _course){
        course = _course;
        return this;
    }

    public Section build() throws Exception{
        return new Section(course, sectioNumber);
    }
}