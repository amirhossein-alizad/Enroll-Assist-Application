package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SectionSchedule {
    List<String> dayOfWeek;
    List<Date> startTime = new ArrayList<>();
    List<Date> endTime = new ArrayList<>();

    public SectionSchedule(List<String> dayOfWeek, List<String> time) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        if (dayOfWeek.size() != time.size())
            exceptionList.addNewException(new Exception("Every day should have time schedule."));
        exceptionList.addExceptions(this.validateDayOfWeek(dayOfWeek));
        try {
            this.validateTime(time);
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        if (exceptionList.hasException())
            throw exceptionList;

        this.dayOfWeek = dayOfWeek;
    }

    private void validateTime(List<String> time) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        for (int i=0; i<time.size(); i++) {
            if (time.get(i).equals(""))
                exceptionList.addNewException(new Exception(String.format("Time can not be empty.(%d)", i+1)));
            else {
                List<String> timeString = Arrays.asList(time.get(i).split("-"));
                if (timeString.size() != 2)
                    exceptionList.addNewException(new Exception(String.format("%s is not valid time.", time.get(i))));
                else {
                    SimpleDateFormat dataFormat = new SimpleDateFormat("HH:mm");
                    try {
                        Date st = dataFormat.parse(timeString.get(0));
                        Date et = dataFormat.parse(timeString.get(1));

                        if (et.before(st))
                            exceptionList.addNewException(new Exception(String.format("End time can not be before start time.(%d)", i+1)));
                        else {
                            this.startTime.add(st);
                            this.endTime.add(et);
                        }
                    }
                    catch (Exception exception) {
                        exceptionList.addNewException(new Exception(String.format("%s is not valid time.", time.get(i))));
                    }
                }
            }
        }
        if (exceptionList.hasException())
            throw exceptionList;
    }


    private List<Exception> validateDayOfWeek(List<String> dayOfWeek) {
        List<Exception> exceptions = new ArrayList<>();
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        List<String> week = Arrays.asList(dateFormatSymbols.getWeekdays());
        for (String d: dayOfWeek) {
            if (!week.contains(d))
               exceptions.add(new Exception(String.format("%s is not valid week day.", d)));
        }
        return exceptions;
    }

    public boolean hasConflict(SectionSchedule anotherCourseSchedule) {
        for(int i=0; i<this.dayOfWeek.size(); i++) {
            boolean result = true;
            int index = anotherCourseSchedule.dayOfWeek.indexOf(this.dayOfWeek.get(i));
            if (index != -1) {
                if (anotherCourseSchedule.startTime.get(index).after(this.endTime.get(i)) || anotherCourseSchedule.startTime.get(index).equals(this.endTime.get(i)))
                    result = false;
                if (anotherCourseSchedule.endTime.get(index).before(this.startTime.get(i)) || anotherCourseSchedule.endTime.get(index).equals(this.startTime.get(i)))
                    result = false;
            }
            else
                result = false;
            if (result)
                return true;
        }

        return false;
    }

}
