package ir.proprog.enrollassist.domain.program;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.major.Major;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@PrimaryKeyJoinColumn(name = "parentId")
public class MinorProgram extends Program{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public MinorProgram(Major major, String graduateLevel, int minimum, int maximum) throws ExceptionList {
        super(major, graduateLevel, minimum, maximum);
    }

}
