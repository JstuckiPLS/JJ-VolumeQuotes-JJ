<#include "header.ftl">

<p>
The following Shipment has been placed on the Financial Board for Invoice Audit and is waiting for your review/approval for billing:
</p>

<p>
    <b>LoadID:</b> ${load.id?c}
</p>
<p>
    <b>Customer Name:</b> ${load.organization.name}
</p>
<p>
    <b>Reason for audit:</b>
    <#list reasons as reason>${reason.description}<#if reason_has_next>, </#if></#list>
</p>

<!--NEW COST TABLE -->
<table border="1">
    <tr>
        <td></td>
        <td>Original Cost</td>
        <td>New Cost</td>
    </tr>
    <tr>
        <td>Total Revenue:</td>
        <td>${originalTotalRevenue}</td>
        <td>${originalTotalRevenue}</td>
    </tr>
    <tr>
        <td>Total Cost:</td>
        <td>${originalTotalCost}</td>
        <td>${newTotalCost}</td>
    </tr>
    <tr>
        <td>Margin:</td>
        <td>${originalMarginAmt}</td>
        <td style='color: <#if (newMarginAmt > load.activeCostDetail.marginAmt)>green<#else>red</#if>'>
            ${newCurrencyMarginAmt}
        </td>
    </tr>
    <tr>
        <td>Margin%:</td>
        <td><#if load.activeCostDetail.margin??>${load.activeCostDetail.margin}</#if></td>
        <td style='color: <#if (newMargin > load.activeCostDetail.margin)>green<#else>red</#if>'>
            ${newMargin}
        </td>
    </tr>
</table>

<p>
    Please Click <a href="${contextUrl}/financialBoard/audit">HERE</a> to be directed to the Financial Board, then Invoice Audit Tab, to review your shipment.
</p>
<p>
    This is a time sensitive issue, please review the load and update status using the Audit tab on Edit Sales Order.
</p>