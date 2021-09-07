package ir.proprog.enrollassist.domain.utils;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.major.Major;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TestMajorBuilder {
    private String majorName;
    private String majorNumber;
    private Set<Course> courses;

    public TestMajorBuilder(){
        majorName = "CE";
        majorNumber = "8101";
        courses = new HashSet<>();
    }

    public TestMajorBuilder majorName(String _majorName){
        majorName = _majorName;
        return this;
    }

    public TestMajorBuilder majorNumber(String _majorNumber){
        majorNumber = _majorNumber;
        return this;
    }

    public TestMajorBuilder courses(Course ... _courses){
        this.courses.addAll(Arrays.asList(_courses));
        return this;
    }

    public Major build() throws ExceptionList{
        Major major = new Major(majorNumber, majorName);
        courses.forEach(major::addCourse);
        return major;
    }
}
