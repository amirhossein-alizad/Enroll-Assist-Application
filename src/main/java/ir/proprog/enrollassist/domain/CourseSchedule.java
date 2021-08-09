package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.Exception.CourseScheduleException.EmptyTime;
import ir.proprog.enrollassist.domain.Exception.CourseScheduleException.InvalidDayOfWeek;
import ir.proprog.enrollassist.domain.Exception.CourseScheduleException.InvalidTimeConcept;
import ir.proprog.enrollassist.domain.Exception.CourseScheduleException.InvalidTimeSyntax;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CourseSchedule {
    private Date startTime;
    private Date endTime;
    private List<String> dayOfWeek;

    public CourseSchedule(List<String> dayOfWeek, String startTime, String endTime) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        exceptionList.addExceptions(this.validateDayOfWeek(dayOfWeek));
        try {
            this.validateTime(startTime, endTime);
        } catch (Exception exception) {
            exceptionList.addNewException(exception);
        }
        if (exceptionList.hasException())
            throw exceptionList;

        this.dayOfWeek = dayOfWeek;
    }

    private void validateTime(String startTime, String endTime) throws Exception {
        SimpleDateFormat dataFormat = new SimpleDateFormat("HH:mm");
        if (startTime.equals("") || endTime.equals(""))
            throw new EmptyTime();
        try {
            this.startTime = dataFormat.parse(startTime);
            this.endTime = dataFormat.parse(endTime);
        }
        catch (Exception exception) {
            throw new InvalidTimeSyntax();
        }
        if (this.endTime.before(this.startTime))
            throw new InvalidTimeConcept();
    }

    private List<Exception> validateDayOfWeek(List<String> dayOfWeek) {
        List<Exception> exceptions = new ArrayList<>();
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        List<String> week = Arrays.asList(dateFormatSymbols.getWeekdays());
        for (String d: dayOfWeek) {
            if (!week.contains(d))
               exceptions.add(new InvalidDayOfWeek(d));
        }
        return exceptions;
    }

    private boolean hasSameMember(List<String> anotherListOfWeekDay){
        for (String d: anotherListOfWeekDay) {
            if (this.dayOfWeek.contains(d))
                return true;
        }
        return false;
    }

    public boolean hasConflict(CourseSchedule anotherCourseSchedule) {
        if (!this.hasSameMember(anotherCourseSchedule.dayOfWeek))
            return false;
        else if (this.endTime.before(anotherCourseSchedule.startTime) || this.endTime.equals(anotherCourseSchedule.startTime))
            return false;
        else if (this.startTime.after(anotherCourseSchedule.endTime) || this.startTime.equals(anotherCourseSchedule.endTime))
            return false;
        return true;
    }

}
