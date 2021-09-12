package ir.proprog.enrollassist.controller.program;

import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.program.Program;
import lombok.Getter;

@Getter
public class ProgramView {
    private String majorName;
    private String majorNumber;
    private String graduateLevel;
    private String type;
    private int minimum;
    private int maximum;

    public ProgramView() {
    }

    public ProgramView(Program program) {
        this.majorName = program.getMajor().getMajorName();
        this.majorNumber = program.getMajor().getMajorNumber();
        this.graduateLevel = program.getGraduateLevel().name();
        this.type = program.getProgramType().name();
        this.minimum = program.getCreditRange().getMinimum();
        this.maximum = program.getCreditRange().getMaximum();
    }

}
