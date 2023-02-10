using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;
using AxServiceTunnel.QueryServiceReference;
using System.Data;
using System.Xml.XPath;
using System.IO;
using System.Xml;

namespace AxServiceTunnel
{
    public class CreditBalanceService : ICreditBalanceService
    {
        public string GetCreditBalanceInfo()
        {
            QueryServiceClient client = new QueryServiceClient();
            Paging paging = null;
            DataSet dataSet = client.ExecuteStaticQuery("CustOpenBalance", ref paging);
            string xml = dataSet.GetXml();
            return xml;
        }

        public string GetCreditBalance(string accountNum)
        {
            string xml = GetCreditBalanceInfo();
            XmlDocument xmlDoc = new XmlDocument();
            xmlDoc.LoadXml(xml);
            XmlNode root = xmlDoc.DocumentElement;

            XmlNodeList nodeList = root.SelectNodes("descendant::CustTrans[AccountNum='" + accountNum+ "']");
            if (nodeList.Count == 1)
            {
                XmlNode item = nodeList.Item(0);
                return item.OuterXml;
            }
            else
            {
                return "<CustTrans></CustTrans>";
            }

        }

    }
}
