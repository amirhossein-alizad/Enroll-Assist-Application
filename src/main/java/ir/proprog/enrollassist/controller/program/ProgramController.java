package ir.proprog.enrollassist.controller.program;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.course.CourseView;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.program.Program;
import ir.proprog.enrollassist.repository.MajorRepository;
import ir.proprog.enrollassist.repository.ProgramRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@RestController
@RequestMapping("/programs")
public class ProgramController {
    private ProgramRepository programRepository;
    private MajorRepository majorRepository;

    @GetMapping
    public Iterable<ProgramView> all() {
        return StreamSupport.stream(programRepository.findAll().spliterator(), false).map(ProgramView::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}/courses")
    public Iterable<CourseView> getCourses(@PathVariable Long id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found"));
        return program.getCourses().stream().map(CourseView::new).collect(Collectors.toList());
    }

    @PostMapping(value = "/{majorId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ProgramView addOne(@PathVariable Long majorId, @RequestBody ProgramView programView) {
        ExceptionList exceptions = new ExceptionList();
        Major major = majorRepository.findById(majorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Major not found"));
        Program program = null;
        try {
            program = new Program(major, programView.getGraduateLevel(), programView.getMinimum(), programView.getMaximum(), programView.getType());
        } catch (ExceptionList exceptionList) {
            exceptions.addExceptions(exceptionList.getExceptions());
        }
        if (exceptions.hasException())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptions.toString());
        programRepository.save(program);
        return new ProgramView(program);
    }
}
