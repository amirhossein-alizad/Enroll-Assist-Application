package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@RestController
@RequestMapping("/majors")
public class MajorController {
    private MajorRepository majorRepository;
    private FacultyRepository facultyRepository;

    @GetMapping
    public Iterable<MajorView> all() {
        return StreamSupport.stream(majorRepository.findAll().spliterator(), false).map(MajorView::new).collect(Collectors.toList());
    }
}
