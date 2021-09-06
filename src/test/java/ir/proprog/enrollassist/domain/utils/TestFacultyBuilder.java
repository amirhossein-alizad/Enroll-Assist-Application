package ir.proprog.enrollassist.domain.utils;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.faculty.Faculty;
import ir.proprog.enrollassist.domain.major.Major;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestFacultyBuilder {
    private String facultyName;
    private Set<Major> majors;

    public TestFacultyBuilder(){
        facultyName = "ECE";
        majors = new HashSet<>();
    }

    public TestFacultyBuilder facultyName(String _facultyName){
        facultyName = _facultyName;
        return this;
    }

    public TestFacultyBuilder majors(Major ... _majors){
        majors.addAll(Arrays.asList(_majors));
        return this;
    }

    public Faculty build() throws ExceptionList {
        Faculty faculty = new Faculty(facultyName);
        majors.forEach(faculty::addMajor);
        return faculty;
    }
}
