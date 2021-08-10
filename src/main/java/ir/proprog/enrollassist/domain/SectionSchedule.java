package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//public class SectionSchedule {
//    private Date startTime;
//    private Date endTime;
//    private List<String> dayOfWeek;
//
//    public SectionSchedule(List<String> dayOfWeek, String startTime, String endTime) throws ExceptionList {
//        ExceptionList exceptionList = new ExceptionList();
//        exceptionList.addExceptions(this.validateDayOfWeek(dayOfWeek));
//        try {
//            this.validateTime(startTime, endTime);
//        } catch (Exception exception) {
//            exceptionList.addNewException(exception);
//        }
//        if (exceptionList.hasException())
//            throw exceptionList;
//
//        this.dayOfWeek = dayOfWeek;
//    }
//
//    private void validateTime(String startTime, String endTime) throws Exception {
//        SimpleDateFormat dataFormat = new SimpleDateFormat("HH:mm");
//        if (startTime.equals("") || endTime.equals(""))
//            throw new Exception("Time can not be empty.");
//        try {
//            this.startTime = dataFormat.parse(startTime);
//            this.endTime = dataFormat.parse(endTime);
//        }
//        catch (Exception exception) {
//            throw new Exception("Section time is not valid.");
//        }
//        if (this.endTime.before(this.startTime))
//            throw new Exception("End time can not be before start time.");
//    }
//
//    private List<Exception> validateDayOfWeek(List<String> dayOfWeek) {
//        List<Exception> exceptions = new ArrayList<>();
//        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
//        List<String> week = Arrays.asList(dateFormatSymbols.getWeekdays());
//        for (String d: dayOfWeek) {
//            if (!week.contains(d))
//               exceptions.add(new Exception(String.format("%s is not valid week day.", d)));
//        }
//        return exceptions;
//    }
//
//    private boolean hasSameMember(List<String> anotherListOfWeekDay){
//        for (String d: anotherListOfWeekDay) {
//            if (this.dayOfWeek.contains(d))
//                return true;
//        }
//        return false;
//    }
//
//    public boolean hasConflict(SectionSchedule anotherCourseSchedule) {
//        if (!this.hasSameMember(anotherCourseSchedule.dayOfWeek))
//            return false;
//        else if (this.endTime.before(anotherCourseSchedule.startTime) || this.endTime.equals(anotherCourseSchedule.startTime))
//            return false;
//        else if (this.startTime.after(anotherCourseSchedule.endTime) || this.startTime.equals(anotherCourseSchedule.endTime))
//            return false;
//        return true;
//    }
//
//}

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


}
