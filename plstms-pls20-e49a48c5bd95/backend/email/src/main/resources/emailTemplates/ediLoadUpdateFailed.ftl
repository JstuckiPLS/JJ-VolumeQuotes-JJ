<#include "header.ftl">

<#include "addressCalculation.ftl">
<#list loadTender.addresses as address>
    <#if address.addressType.name() == 'ORIGIN'>
        <#if address.addressCode == load.origin.addressCode>
            <#assign originContact = load.origin.contact>
            <#assign originCity = load.origin.address.city + ", " + load.origin.address.stateCode + " ">
            <#assign originZip = load.origin.address.zip>
        <#else>
            <#assign originContact = address.contactName>
            <#assign originCity = address.city + ", " + address.stateCode + " ">
            <#assign originZip = address.postalCode>
            <#if address.address1??>
                <#if address.address2??>
                    <#assign originAddress = address.address1 + ", " + address.address2>
                <#else>
                    <#assign originAddress = address.address1>
                </#if>
            <#else>
                <#if address.address2??>
                    <#assign originAddress = address.address2>
                </#if>
            </#if>
        </#if>
    <#elseif address.addressType.name() == 'DESTINATION'>
        <#if address.addressCode == load.destination.addressCode>
            <#assign destContact = load.destination.contact>
            <#assign destCity = load.destination.address.city + ", " + load.destination.address.stateCode + " ">
            <#assign destZip = load.destination.address.zip>
        <#else>
            <#assign destContact = address.contactName>
            <#assign destCity = address.city + ", " + address.stateCode + " ">
            <#assign destZip = address.postalCode>
            <#if address.address1??>
                <#if address.address2??>
                    <#assign destinationAddress = address.address1 + ", " + address.address2>
                <#else>
                    <#assign destinationAddress = address.address1>
                </#if>
            <#else>
                <#if address.address2??>
                    <#assign destinationAddress = address.address2>
                </#if>
            </#if>
        </#if>
    </#if>
</#list>

<#if loadTender.guaranteedBy??>
	<#assign guaranteedBy = loadTender.guaranteedBy?number>
    <#if guaranteedBy == 2400>
        <#assign guaranteedTime = 'EOD'>
    <#else>
        <#assign hours = 12>
        <#assign am = "AM">
        <#if (guaranteedBy >= 100)>
            <#assign hours = (guaranteedBy / 100)?floor>
            <#if (hours > 12)>
                <#assign am = "PM">
                <#assign hours = hours - 12>
            <#elseif hours == 12>
                <#assign am = "PM">
            </#if>
        </#if>
        <#assign minutes = guaranteedBy % 100>
        <#assign guaranteedTime = hours + ":" + minutes?string("00") + " " + am>
    </#if>
</#if>

<#if load.carrier??>
    <#assign carrierName = load.carrier.name>
<#else>
    <#assign carrierName = "NOT SPECIFIED">
</#if>

<#if reopened>
	This is an automated email from PLS PRO to inform you that an EDI update was received to reopen a shipment that was previously cancelled.<br><br>
<#else>
	This is an automated email from PLS PRO to inform you that an EDI update was unable to be processed.<br><br>
</#if>

Current Shipment Status: ${load.status.description!"NOT SPECIFIED"}<br><br>
Customer: ${load.organization.name!"NOT SPECIFIED"}<br><br>
Carrier: ${carrierName}<br><br> 

<div class="noPadding">
    <#if loadTender.guaranteedBy??>
		Guaranteed Delivery by ${guaranteedTime}
    <#else>
        Standard Service
    </#if>
</div>

<br><br>
<table>
    <tr>
        <td class="noPadding">BOL: ${loadTender.bol!"NOT SPECIFIED"}</td>
        <td>PU: ${loadTender.pickupNum!"NOT SPECIFIED"}</td>
    </tr>
    <tr>
        <td class="noPadding">Shipper Ref: ${load.numbers.refNumber!"NOT SPECIFIED"}</td>
        <td>PO: ${loadTender.poNum!"NOT SPECIFIED"}</td>
    </tr>
    <tr>
        <td class="noPadding">Load ID: ${load.id?c}</td>
        <td>SO: ${loadTender.soNum!"NOT SPECIFIED"}</td>
    </tr>
    <tr>
        <td class="noPadding">GL: ${loadTender.glNumber!"NOT SPECIFIED"}</td>
        <td>Trailer: ${loadTender.trailerNum!"NOT SPECIFIED"}</td>
    </tr>
    <tr>
        <td class="noPadding" colspan="2">Pro: ${loadTender.proNumber!"NOT SPECIFIED"}</td>
    </tr>
    <tr>
        <td colspan="2">&nbsp;<br></td>
    </tr>
    <tr>
        <td class="noPadding" colspan="2">
        <#if directionChanged>
        <span style="background-color: #FFFF00">
            I/O: 
            <#if loadTender.inboundOutbound == "I"> 
                Inbound
            <#else>
                Outbound
            </#if>
        </span>
        <#else>
            I/O: 
            <#if loadTender.inboundOutbound == "I"> 
                Inbound
            <#else>
                Outbound
            </#if>
        </#if>
        </td>
    </tr>
    <tr>
        <td colspan="2">&nbsp;<br></td>
    </tr>
    <tr>
        <td class="noPadding">Origin:</td>
        <td>Destination:</td>
    </tr>
    <tr>
        <td class="noPadding">${originContact}</td>
        <td>${destContact}</td>
    </tr>
    
    <tr>
        <td class="noPadding">${originAddress}</td>
        <td>${destinationAddress}</td>
    </tr>
    <tr>
        <td class="noPadding">${originCity}
            <#if originZipChanged>
                <span style="background-color: #FFFF00">${originZip}</span>
            <#else>
                ${originZip}
            </#if>
        </td>
        <td>${destCity}
            <#if destZipChanged>
                <span style="background-color: #FFFF00">${destZip}</span>
            <#else>
                ${destZip}
            </#if>
        </td>
    </tr>
    <tr>
        <td colspan="2">&nbsp;<br></td>
    </tr>
    <tr>
        <td class="noPadding">
            Scheduled Pickup Date:
        </td>
        <td>
            Estimated Delivery Date:
        </td>
    </tr>
    <tr>
        <td class="noPadding">
            <b>
            <#if pickupDateChanged>
                <span style="background-color: #FFFF00">${puDate?string("MM/dd/yyyy")}</span>
            <#else>
                 ${puDate?string("MM/dd/yyyy")}
            </#if>
            </b>
            
        </td>
        <td>
            <b>${delDate?string("MM/dd/yyyy")}</b>
        </td>
    </tr>
</table>

<br><br>

<#if materialsUpdated>
    <span style="background-color: #FFFF00">
        Product information has changed
    </span>
    <br><br>
    Items Before:
    <br><br>
<#else>
    Items:
    <br><br>
</#if>

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
                    <td align="right">${material.weight} LBS</td>
                    <td>${material.commodityClass.getDbCode()}</td>
                    <td>W: ${material.width!"n/a"}, L: ${material.length!"n/a"}, H: ${material.height!"n/a"}</td>
                    <td>${material.quantity!""}</td>
                    <td>${material.packageType.getDescription()!""}</td>
                    <td>${material.productDescription!""}</td>
                    <td>${material.productCode!""}</td>
                    <td>${material.nmfc!""}</td>
                    <td>${material.stackable?string("Yes", "No")}</td>
                    <td>${material.hazmat?string("Yes", "No")}</td>
                </tr>
            </#list>
        </#if>
    </#list>
</table>
<#if materialsUpdated>
<br><br>
Items After:
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
       <#list loadTender.materials as material>
            <tr>
                <td align="right">${material.weight} ${material.weightUOM.name()}</td>
                <td>${material.commodityClassCode}</td>
                <td>W: ${material.width!"n/a"}, L: ${material.length!"n/a"}, H: ${material.height!"n/a"}</td>
                <td>${material.quantity!""}</td>
                <td>${material.packagingType!material.packagingDesc!""}</td>
                <td>${material.productDesc!""}</td>
                <td>${material.productCode!""}</td>
                <td>${material.nmfc!""}</td>
                <td><#if ('Y' == material.stackable.name())>Yes<#else>No</#if></td>
                <td><#if ('Y' == material.hazmat.name())>Yes<#else>No</#if></td>
            </tr>
        </#list>
</table>
</#if>
<#include "footer.ftl">