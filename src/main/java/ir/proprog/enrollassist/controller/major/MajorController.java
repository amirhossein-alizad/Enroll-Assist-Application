package ir.proprog.enrollassist.controller.major;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@RestController
@RequestMapping("/majors")
public class MajorController {
    private MajorRepository majorRepository;

    @GetMapping
    public Iterable<MajorView> all() {
        return StreamSupport.stream(majorRepository.findAll().spliterator(), false).map(MajorView::new).collect(Collectors.toList());
    }

//    @GetMapping("/{id}/courses")
//    public Iterable<CourseView> getCourses(@PathVariable Long id) {
//        Major major = majorRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Major not found"));
//        return major.getCourses().stream().map(CourseView::new).collect(Collectors.toList());
//    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public MajorView addOne(@RequestBody MajorView majorView) {
        ExceptionList exceptions = new ExceptionList();
        if (majorRepository.findByMajorName(majorView.getMajorName()).isPresent())
            exceptions.addNewException(new Exception("Major with name " + majorView.getMajorName() + " exists."));
        Major major = null;
        try {
            major = new Major(majorView.getMajorNumber(), majorView.getMajorName(), majorView.getFaculty());
        } catch (ExceptionList exceptionList) {
            exceptions.addExceptions(exceptionList.getExceptions());
        }
        if (exceptions.hasException())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptions.toString());
        majorRepository.save(major);
        return new MajorView(major);
    }
}
