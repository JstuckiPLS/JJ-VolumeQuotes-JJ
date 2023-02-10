package com.pls.ax.custopenbalance.client;

import com.pls.ax.custopenbalance.client.proxy.CustOpenBalance;
import com.pls.ax.custopenbalance.client.proxy.ObjectFactory;
import com.pls.ax.custopenbalance.client.proxy.RoutingService;
import com.pls.ax.custopenbalance.client.proxy.TriCustOpenBalanceService;
import com.pls.ax.custopenbalance.client.proxy.TriCustOpenBalanceServiceGetAllCustOpenBalancesAifFaultFaultFaultMessage;
import com.pls.ax.custopenbalance.client.proxy.TriCustOpenBalanceServiceGetAllCustOpenBalancesResponse;
import com.pls.ax.custopenbalance.client.proxy.TriCustOpenBalanceServiceGetCustOpenBalanceByAccountAifFaultFaultFaultMessage;
import com.pls.ax.custopenbalance.client.proxy.TriCustOpenBalanceServiceGetCustOpenBalanceByAccountRequest;
import com.pls.ax.custopenbalance.client.proxy.TriCustOpenBalanceServiceGetCustOpenBalanceByAccountResponse;
import com.pls.ax.custopenbalance.client.proxy.TriCustTransListDC;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * A web service client for accessing customer open balance information from the AX service.
 * System properties, ax.username and ax.password, need to be set for this to work.
 *
 * @author Thomas Clancy
 */
public class AxCustOpenBalanceClient {
    public static final String DEFAULT_WSDL_LOC =
        "http://exp-axbatch.quad.local:85/MicrosoftDynamicsAXAif60/PLSCustOpenBalanceHTTP/xppservice.svc?wsdl";
    private final String wsdlLocation;

    /**
     * Default constructor uses the default wsdl location of the service.
     */
    public AxCustOpenBalanceClient() {
        this.wsdlLocation = DEFAULT_WSDL_LOC;
        Authenticator.setDefault(new NtlmAuthenticator(System.getProperty("ax.username"),
            System.getProperty("ax.password")));
    }

    /**
     * Construct with a WSDL.
     * 
     * @param wsdlLocation the WSDL URL.s
     */
    public AxCustOpenBalanceClient(String wsdlLocation) {
        this.wsdlLocation = wsdlLocation;
        Authenticator.setDefault(new NtlmAuthenticator(System.getProperty("ax.username"),
            System.getProperty("ax.password")));
    }

    /**
     * Construct with a WSDL, username and password.
     * 
     * @param wsdlLocation A WSDL URL.
     * @param username A valid ax (active directory) username.
     * @param password A valid password.
     */
    public AxCustOpenBalanceClient(String wsdlLocation, String username, String password) {
        this.wsdlLocation = wsdlLocation;
        Authenticator.setDefault(new NtlmAuthenticator(username, password));
    }

    /**
     * Returns the list of all CustOpenBalance objects.
     * 
     * @return List of all CustOpenBalance objects.
     * @throws TriCustOpenBalanceServiceGetAllCustOpenBalancesAifFaultFaultFaultMessage if there was a problem
     * connecting to the ax service.
     * @throws java.net.MalformedURLException if the WSDL URL is not valid.
     */
    public List<CustOpenBalance> getAllCustOpenBalances() throws
        TriCustOpenBalanceServiceGetAllCustOpenBalancesAifFaultFaultFaultMessage, MalformedURLException {
        TriCustOpenBalanceServiceGetAllCustOpenBalancesResponse result = getServicePort().getAllCustOpenBalances(null);
        TriCustTransListDC list = result.getResponse().getValue();
        return list.getParmCustTransList().getValue().getCustOpenBalance();
    }

    /**
     * Returns the CustOpenBalance object that matches accountNum.
     * 
     * @param accountNum The account number.
     * @return A CustOpenBalance instance or null if no match was found.
     * @throws TriCustOpenBalanceServiceGetCustOpenBalanceByAccountAifFaultFaultFaultMessage if there was a problem
     * connecting to the ax service.
     * @throws java.net.MalformedURLException if the WSDL URL is not valid.
     */
    public CustOpenBalance getCustOpenBalanceByAccount(String accountNum) throws
        TriCustOpenBalanceServiceGetCustOpenBalanceByAccountAifFaultFaultFaultMessage, MalformedURLException {
        ObjectFactory of = new ObjectFactory();
        TriCustOpenBalanceServiceGetCustOpenBalanceByAccountRequest parameters
            = of.createTriCustOpenBalanceServiceGetCustOpenBalanceByAccountRequest();
        parameters.setAccountNum(of.createTriCustOpenBalanceServiceGetCustOpenBalanceByAccountRequestAccountNum(accountNum));
        TriCustOpenBalanceServiceGetCustOpenBalanceByAccountResponse result
            = getServicePort().getCustOpenBalanceByAccount(parameters);
        return result.getResponse().getValue();
    }

    private TriCustOpenBalanceService getServicePort() throws MalformedURLException {
        RoutingService service = new RoutingService(new URL(wsdlLocation));
        return service.getBasicHttpBindingTriCustOpenBalanceService();
    }
}
