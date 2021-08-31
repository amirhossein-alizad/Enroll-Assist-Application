package ir.proprog.enrollassist.controller.section;

import ir.proprog.enrollassist.domain.section.Section;

public class SectionDemandView {
    private SectionView sectionView;
    private Long sectionId;
    private long demand;

    public SectionDemandView(Long sectionId, Long demand) {
        this.sectionId = sectionId;
        this.demand = demand;
    }

    public SectionDemandView(SectionView sectionView, long demand) {
        this.sectionView = sectionView;
        this.demand = demand;
    }

    public void setSectionView(Section section) {
        this.sectionView = new SectionView(section);
    }

    public Long getSectionId() {
        return sectionId;
    }
}
