package com.pls.dto.shipment;

import java.util.Collection;

import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dto.address.ZipDTO;

/**
 * DTO for shipment details.
 * 
 * @author Gleb Zgonikov
 */
public class ShipmentDetailsDTO {

    private ZipDTO zip;

    private AddressBookEntryDTO address;

    private Collection<String> accessorials;

    public ZipDTO getZip() {
        return zip;
    }

    public void setZip(ZipDTO zip) {
        this.zip = zip;
    }

    public AddressBookEntryDTO getAddress() {
        return address;
    }

    public void setAddress(AddressBookEntryDTO address) {
        this.address = address;
    }

    public Collection<String> getAccessorials() {
        return accessorials;
    }

    public void setAccessorials(Collection<String> accessorials) {
        this.accessorials = accessorials;
    }
}
