package com.pls.core.domain.organization;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Base abstract class for organizations notes. Implementations must set descriminator annotation.
 * <p>
 * Currently there is only one implementation - customer notes.
 * {@link com.pls.core.domain.organization.CustomerNoteEntity}
 * 
 * @author Aleksandr Leshchenko
 */
@Entity
@Table(name = "NOTES")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "NOTE_TYPE")
public abstract class NoteEntity implements Identifiable<Integer>, HasModificationInfo {
    private static final long serialVersionUID = -1735646005309915891L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "note_sequence")
    @SequenceGenerator(name = "note_sequence", sequenceName = "NTE_SEQ", allocationSize = 1)
    @Column(name = "NOTE_ID")
    private Integer id;

    @Column(nullable = false)
    private String note;

    private Integer version = 1;

    @Column(nullable = false)
    private char status = 'A';  //todo A and I value need to be confirmed

    @Column(nullable = false)
    private char visibility = 'I';  //todo I and E value need to be confirmed

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getVersion() {
        return version;
    }

    public boolean isActiveStatus() {
        return status == 'A';
    }

    /**
     * Set status value.
     *
     * @param activeStatus status value to set
     */
    public void setActiveStatus(boolean activeStatus) {
        if (activeStatus) {
            status = 'A';
        } else {
            status = 'I';
        }
    }

    /**
     * Get internal state accordingly to visibility state.
     *
     * @return the visibility
     */
    public boolean isInternal() {
        return visibility == 'I';
    }

    /**
     * Set internal visibility state. If false, will be external state.
     *
     * @param internal if state is internal
     */
    public void setInternal(boolean internal) {
        visibility = internal ? 'I' : 'E';
    }


    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(note).
                append(version).
                append(status).
                append(visibility).
                append(getModification()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NoteEntity) {
            final NoteEntity other = (NoteEntity) obj;
            return new EqualsBuilder().
                    append(note, other.note).
                    append(version, other.version).
                    append(status, other.status).
                    append(visibility, other.visibility).
                    append(getModification(), other.getModification()).isEquals();
        } else {
            return false;
        }
    }
}
