package ir.proprog.enrollassist.domain.utils;

import ir.proprog.enrollassist.domain.section.PresentationSchedule;

public class TestPresentationScheduleBuilder {
    String dayOfWeek;
    String startTime;
    String endTime;

    public TestPresentationScheduleBuilder(){
        dayOfWeek = "Monday";
        startTime = "10:30";
        endTime = "12:00";
    }

    public TestPresentationScheduleBuilder dayOfWeek(String _dayOfWeek){
        this.dayOfWeek = _dayOfWeek;
        return this;
    }

    public TestPresentationScheduleBuilder startTime(String _startTime){
        this.startTime = _startTime;
        return this;
    }

    public TestPresentationScheduleBuilder endTime(String _endTime){
        this.endTime = _endTime;
        return this;
    }

    public PresentationSchedule build() throws Exception{
        return new PresentationSchedule(dayOfWeek, startTime, endTime);
    }
}
