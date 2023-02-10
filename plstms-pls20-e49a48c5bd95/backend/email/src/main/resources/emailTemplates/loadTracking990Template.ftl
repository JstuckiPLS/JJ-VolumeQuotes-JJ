<#include "header.ftl">
<#include "guaranteedByCalculation.ftl">
<#include "addressCalculation.ftl">

This is an automated email from PLS PRO to inform you that ${load.carrier.name!"NOT SPECIFIED"} has sent a response for your order.
<br>
Response:  ${status} 
<br>
Current Order Status: ${load.status.description}<br><br>

<#if !isBlindBol>${load.organization.name}<br><br></#if>

Carrier: ${load.carrier.name!"NOT SPECIFIED"}<br><br>

<table>
    <tr>
        <td class="noPadding">BOL: ${load.numbers.bolNumber!"NOT SPECIFIED"}</td>
        <td>PU: ${load.numbers.puNumber!"NOT SPECIFIED"}</td>
    </tr>
    <tr>
        <td class="noPadding">Shipper Ref: ${load.numbers.refNumber!"NOT SPECIFIED"}</td>
        <td>PO: ${load.numbers.poNumber!"NOT SPECIFIED"}</td>
    </tr>
    <tr>
        <td class="noPadding">Load ID: ${load.id?c}</td>
        <td>SO: ${load.numbers.soNumber!"NOT SPECIFIED"}</td>
    </tr>
    <tr>
        <td class="noPadding">GL: ${load.numbers.glNumber!"NOT SPECIFIED"}</td>
        <td>Trailer: ${load.numbers.trailerNumber!"NOT SPECIFIED"}</td>
    </tr>
    <tr>
        <td class="noPadding" colspan="2">Pro: ${load.numbers.proNumber!"NOT SPECIFIED"}</td>
    </tr>
    <tr>
        <td colspan="2">&nbsp;<br></td>
    </tr>
    <tr>
        <td class="noPadding" colspan="2">
            <#if load.activeCostDetail.guaranteedBy??>
                <#if load.activeCostDetail.guaranteedNameForBOL??>
                    ${load.activeCostDetail.guaranteedNameForBOL}
                <#else>
                    Guaranteed Delivery
                </#if>
                by ${guaranteedTime}
            <#else>
                Standard Service
            </#if>
            <br>
        </td>
    </tr>
    <tr>
        <td colspan="2">&nbsp;<br></td>
    </tr>
    <tr>
        <td class="noPadding"><#if !isBlindBol>Origin:</#if></td>
        <td>Destination:</td>
    </tr>
    <tr>
        <td class="noPadding"><#if !isBlindBol>${load.origin.contact}</#if></td>
        <td>${load.destination.contact}</td>
    </tr>
    <tr>
        <td class="noPadding"><#if !isBlindBol>${originAddress}</#if></td>
        <td>${destinationAddress}</td>
    </tr>
    <tr>
        <td class="noPadding"><#if !isBlindBol>${load.origin.address.city}, ${load.origin.address.stateCode}</#if></td>
        <td>${load.destination.address.city}, ${load.destination.address.stateCode}</td>
    </tr>
    <tr>
        <td colspan="2">&nbsp;<br></td>
    </tr>
    <tr>
        <td class="noPadding">
            <#if load.origin.departure??>
                Actual Pickup Date: ${load.origin.departure?string("MM/dd/yyyy")}
            <#else>
                Scheduled Pickup Date: ${load.origin.earlyScheduledArrival?string("MM/dd/yyyy")}
            </#if>
        </td>
        <td>
            <#if load.destination.departure??>
                Actual Delivery Date: ${load.destination.departure?string("MM/dd/yyyy")}
            <#else>
                Estimated Delivery Date: ${load.destination.scheduledArrival?string("MM/dd/yyyy")}
            </#if>
        </td>
    </tr>
</table>

<br><br>
Items:
<br><br>

<table border="1">
    <tr>
        <th>Weight</th>
        <th>Class</th>
        <th>Dimensions (Inches)</th>
        <th>Qty</th>
        <th>Pack. Type</th>
        <th>Product</th>
        <th>SKU/Product Code</th>
        <th>NMFC</th>
        <th>Stackable</th>
        <th>Hazmat</th>
    </tr>
    <#list load.loadDetails as loadDetail>
        <#if loadDetail.loadAction.name() == "PICKUP" && loadDetail.pointType.name() == "ORIGIN">
            <#list loadDetail.loadMaterials as material>
                <tr>
                    <td align="right">${material.weight} Lbs</td>
                    <td>${material.commodityClass.getDbCode()}</td>
                    <td>W: ${material.width!"n/a"}, L: ${material.length!"n/a"}, H: ${material.height!"n/a"}</td>
                    <td>${material.quantity!""}</td>
                    <td>${material.packageType.getDescription()!""}</td>
                    <td>${material.productDescription!""}</td>
                    <td>${material.productCode!""}</td>
                    <td>${material.nmfc!""}</td>
                    <td>${material.stackable?string("yes", "no")}</td>
                    <td>${material.hazmat?string("yes", "no")}</td>
                </tr>
            </#list>
        </#if>
    </#list>
</table>


<#include "footer.ftl">