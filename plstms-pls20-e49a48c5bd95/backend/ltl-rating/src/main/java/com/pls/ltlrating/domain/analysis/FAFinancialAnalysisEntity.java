package com.pls.ltlrating.domain.analysis;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.ltlrating.domain.enums.FinancialAnalysisStatus;

/**
 * Table is used to store general info about freight analysis input/output files.
 *
 * @author Svetlana Kulish
 */
@Entity
@Table(name = "FA_FINANCIAL_ANALYSIS")
public class FAFinancialAnalysisEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 5645883014576342085L;

    public static final String Q_UPDATE_ANALYSIS_STATUS = "com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity.Q_UPDATE_ANALYSIS_STATUS";
    public static final String Q_GET_ANALYSIS_STATUS = "com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity.Q_GET_ANALYSIS_STATUS";
    public static final String Q_GET_FOR_PROCESSING = "com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity.Q_GET_FOR_PROCESSING";
    public static final String Q_MARK_ROW_COMPLETED = "com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity.Q_MARK_ROW_COMPLETED";
    public static final String Q_MARK_ANALYSIS_COMPLETED = "com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity.Q_MARK_ANALYSIS_COMPLETED";
    public static final String Q_GET_ANALYSIS_JOBS = "com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity.Q_GET_ANALYSIS_JOBS";
    public static final String Q_GET_NEXT_ANALYSIS_BY_SEQ_NUMBER
            = "com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity.Q_GET_NEXT_ANALYSIS_BY_SEQ_NUMBER";
    public static final String Q_GET_NEXT_SEQ_NUMBER = "com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity.Q_GET_NEXT_SEQ_NUMBER";
    public static final String U_SET_DOCUMENT = "com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity.U_SET_DOCUMENT";
    public static final String Q_GET_WITH_DEPENDENCIES = "com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity.Q_GET_WITH_DEPENDENCIES";
    public static final String U_DELETE_INCORRECT_OUTPUT = "com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity.U_DELETE_INCORRECT_OUTPUT";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FA_FINANCIAL_ANALYSIS_SEQUENCE")
    @SequenceGenerator(name = "FA_FINANCIAL_ANALYSIS_SEQUENCE", sequenceName = "FA_FINANCIAL_ANALYSIS_SEQ", allocationSize = 1)
    @Column(name = "ANALYSIS_ID")
    private Long id;

    @Column(name = "SEQ_NUMBER", nullable = false)
    private Integer seq;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INPUT_FILE")
    private LoadDocumentEntity inputFile;

    @Column(name = "INPUT_FILE_NAME")
    private String inputFileName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OUTPUT_FILE")
    private LoadDocumentEntity outputFile;

    @Column(name = "OUTPUT_FILE_NAME")
    private String outputFileName;

    @Column(name = "STATUS")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.ltlrating.domain.enums.FinancialAnalysisStatus"),
            @Parameter(name = "identifierMethod", value = "getCode"), @Parameter(name = "valueOfMethod", value = "getByCode") })
    private FinancialAnalysisStatus status = FinancialAnalysisStatus.Processing;

    @Column(name = "BLOCK_INDIRECT_TYPE")
    @Type(type = "yes_no")
    private Boolean blockIndirectServiceType;

    @OneToMany(mappedBy = "analysis", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<FAInputDetailsEntity> inputDetails;

    @OneToMany(mappedBy = "analysis", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<FATariffsEntity> tariffs;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public LoadDocumentEntity getInputFile() {
        return inputFile;
    }

    public void setInputFile(LoadDocumentEntity inputFile) {
        this.inputFile = inputFile;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public LoadDocumentEntity getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(LoadDocumentEntity outputFile) {
        this.outputFile = outputFile;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public FinancialAnalysisStatus getStatus() {
        return status;
    }

    public void setStatus(FinancialAnalysisStatus status) {
        this.status = status;
    }

    public Set<FAInputDetailsEntity> getInputDetails() {
        return inputDetails;
    }

    public void setInputDetails(Set<FAInputDetailsEntity> inputDetails) {
        this.inputDetails = inputDetails;
    }

    public Set<FATariffsEntity> getTariffs() {
        return tariffs;
    }

    public void setTariffs(Set<FATariffsEntity> tariffs) {
        this.tariffs = tariffs;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public Boolean getBlockIndirectServiceType() {
        return blockIndirectServiceType;
    }

    public void setBlockIndirectServiceType(Boolean blockIndirectServiceType) {
        this.blockIndirectServiceType = blockIndirectServiceType;
    }

}
