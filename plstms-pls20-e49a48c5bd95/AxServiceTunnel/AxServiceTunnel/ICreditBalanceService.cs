using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;

namespace AxServiceTunnel
{
    [ServiceContract]
    public interface ICreditBalanceService
    {
        [OperationContract]
        string GetCreditBalanceInfo();

        [OperationContract]
        string GetCreditBalance(string accountNum);
    }
}
