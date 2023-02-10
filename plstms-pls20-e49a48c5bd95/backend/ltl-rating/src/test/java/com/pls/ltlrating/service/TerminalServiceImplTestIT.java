package com.pls.ltlrating.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.shared.AddressVO;
import com.pls.ltlrating.shared.GetTerminalsCO;
import com.pls.ltlrating.shared.TerminalsVO;

/**
 * Test for {@link TerminalService}.
 *
 * @author Aleksandr Leshchenko
 */
public class TerminalServiceImplTestIT extends BaseServiceITClass {
    @Autowired
    private TerminalService terminalService;

    /**
     * TestCase to test whether the terminal information is returned for USA zipcodes as origin and
     * destination.
     *
     * @throws Exception
     */
    @Test
    public void testGetTerminalInfoForUsaToUsa() throws Exception {
        TerminalsVO terminalInfo = terminalService.getTerminalInformation(getTerminalCriteria());
        Assert.assertNotNull(terminalInfo);
        Assert.assertNotNull(terminalInfo.getMileageToOrigTerminal());
        Assert.assertNotNull(terminalInfo.getMileageBtwOrigTermDestTerm());
        Assert.assertNotNull(terminalInfo.getMileageFromDestTerminal());
    }

    /**
     * TestCase to test whether the terminal information is returned for USA zipcode as origin and CAN zipcode
     * as destination.
     *
     * @throws Exception
     */
    @Test
    public void testGetTerminalInfoForUsaToCan() throws Exception {
        GetTerminalsCO terminalCO = getTerminalCriteria();
        AddressVO destAddr = new AddressVO();
        destAddr.setCity("CAMBRIDGE");
        destAddr.setCountryCode("CAN");
        destAddr.setPostalCode("N3H 4R7");
        destAddr.setStateCode("ON");
        terminalCO.setDestinationAddress(destAddr);
        TerminalsVO terminalInfo = terminalService.getTerminalInformation(terminalCO);
        Assert.assertNotNull(terminalInfo);
        Assert.assertNotNull(terminalInfo.getMileageToOrigTerminal());
        Assert.assertNotNull(terminalInfo.getMileageBtwOrigTermDestTerm());
        Assert.assertNotNull(terminalInfo.getMileageFromDestTerminal());
    }

    private GetTerminalsCO getTerminalCriteria() {
        GetTerminalsCO terminalCO = new GetTerminalsCO();
        terminalCO.setScac("HMES");
        terminalCO.setProfileDetailId(46L);
        AddressVO originAddress = new AddressVO();
        originAddress.setCity("BIRMINGHAM");
        originAddress.setCountryCode("USA");
        originAddress.setPostalCode("35294");
        originAddress.setStateCode("AL");
        terminalCO.setOriginAddress(originAddress);
        AddressVO destinationAddress = new AddressVO();
        destinationAddress.setCity("ALEXANDER CITY");
        destinationAddress.setCountryCode("USA");
        destinationAddress.setPostalCode("35010");
        destinationAddress.setStateCode("AL");
        terminalCO.setDestinationAddress(destinationAddress);
        return terminalCO;
    }
}
