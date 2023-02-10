package com.pls.ltlrating.shared;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.shared.AddressVO;

/**
 * The result object that contains terminal information for the selected carrier.
 * @author Hima Bindu Challa
 *
 */
public class TerminalsVO implements Serializable {

    private static final long serialVersionUID = -872352365273576237L;

    private AddressVO originTerminal; //This value should be shown on pop up as carrier terminals.
    private AddressVO destinationTerminal; //This value should be shown on pop up as carrier terminals.
    private Integer mileageToOrigTerminal;
    private Integer mileageFromDestTerminal;
    private Integer mileageBtwOrigTermDestTerm;
    private TerminalContactVO originTerminalContact; //This value should be shown on pop up as carrier terminals.
    private TerminalContactVO destTerminalContact; //This value should be shown on pop up as carrier terminals.

    public AddressVO getOriginTerminal() {
        return originTerminal;
    }
    public void setOriginTerminal(AddressVO originTerminal) {
        this.originTerminal = originTerminal;
    }
    public AddressVO getDestinationTerminal() {
        return destinationTerminal;
    }
    public void setDestinationTerminal(AddressVO destinationTerminal) {
        this.destinationTerminal = destinationTerminal;
    }
    public Integer getMileageToOrigTerminal() {
        return mileageToOrigTerminal;
    }
    public void setMileageToOrigTerminal(Integer mileageToOrigTerminal) {
        this.mileageToOrigTerminal = mileageToOrigTerminal;
    }
    public Integer getMileageFromDestTerminal() {
        return mileageFromDestTerminal;
    }
    public void setMileageFromDestTerminal(Integer mileageFromDestTerminal) {
        this.mileageFromDestTerminal = mileageFromDestTerminal;
    }
    public Integer getMileageBtwOrigTermDestTerm() {
        return mileageBtwOrigTermDestTerm;
    }
    public void setMileageBtwOrigTermDestTerm(Integer mileageBtwOrigTermDestTerm) {
        this.mileageBtwOrigTermDestTerm = mileageBtwOrigTermDestTerm;
    }
    public TerminalContactVO getOriginTerminalContact() {
        return originTerminalContact;
    }
    public void setOriginTerminalContact(TerminalContactVO originTerminalContact) {
        this.originTerminalContact = originTerminalContact;
    }
    public TerminalContactVO getDestTerminalContact() {
        return destTerminalContact;
    }
    public void setDestTerminalContact(TerminalContactVO destTerminalContact) {
        this.destTerminalContact = destTerminalContact;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("Origin Terminal Address Postal Code", originTerminal.getPostalCode())
                .append("Origin Terminal Address City", originTerminal.getCity())
                .append("Origin Terminal Address State", originTerminal.getStateCode())
                .append("Origin TerminalAddress Country", originTerminal.getCountryCode())
                .append("Dest Terminal Address Postal Code", destinationTerminal.getPostalCode())
                .append("Dest Terminal Address City", destinationTerminal.getCity())
                .append("Dest Terminal Address State", destinationTerminal.getStateCode())
                .append("Dest Terminal Address Country", destinationTerminal.getCountryCode())
                .append("Origin Terminal Contact", originTerminalContact.getContact())
                .append("Origin Terminal Contact Email", originTerminalContact.getContactEmail())
                .append("Origin Terminal Contact Name", originTerminalContact.getName())
                .append("Origin Terminal Contact Phone", originTerminalContact.getPhone())
                .append("Dest Terminal Contact", destTerminalContact.getContact())
                .append("Dest Terminal Contact Email", destTerminalContact.getContactEmail())
                .append("Dest Terminal Contact Name", destTerminalContact.getName())
                .append("Dest Terminal Contact Phone", destTerminalContact.getPhone())
                .append("Mileage from Origin to Origin Terminal", mileageToOrigTerminal)
                .append("Mileage from Destination Terminal to Destination", mileageFromDestTerminal)
                .append("Mileage between Origin Terminal and Destination Terminal", mileageBtwOrigTermDestTerm);

        return builder.toString();
    }
}
