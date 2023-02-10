package com.pls.ltlrating.service;

import com.pls.core.domain.address.AddressEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.shared.AddressVO;
import com.pls.ltlrating.shared.GetTerminalsCO;
import com.pls.ltlrating.shared.TerminalsVO;

/**
 * Class where we calculate LTL rates for the given parameters.
 *
 * @author Aleksandr Leshchenko
 *
 */
public interface TerminalService {
    /**
     * Method to get terminal information for the selected carrier.
     *
     * @param crit
     *            Criteria object that contains origin/destination addresses and pricingDetailId
     * @return TerminalsVO that contains origin/destination terminal addresses and also mileage information.
     * @throws ApplicationException
     *             Exception thrown from mileage service.
     */
    TerminalsVO getTerminalInformation(GetTerminalsCO crit) throws ApplicationException;
	
	AddressVO buildAddressVO(AddressEntity address) throws ApplicationException;
}
