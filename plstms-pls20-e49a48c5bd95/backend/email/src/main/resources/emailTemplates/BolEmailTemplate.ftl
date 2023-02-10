<#macro phoneFormat phone>
    <#if phone?? && phone.number?has_content>
       <#if phone.countryCode?has_content && phone.areaCode?has_content>
           Phone: +${phone.countryCode?trim}(${phone.areaCode?trim})${phone.number[0..2]} ${phone.number[3..]}
       <#elseif phone.areaCode?has_content>
           Phone: +1(${phone.areaCode?trim})${phone.number[0..2]} ${phone.number[3..]}
       <#else>
           Phone: ${phone.number}
       </#if>
    </#if>
</#macro>

<#include "guaranteedByCalculation.ftl">
<#include "addressCalculation.ftl">

<#if load.origin?? && load.origin.address?? && load.destination?? && load.destination.address??>
    <#assign origin = load.origin>
    <#assign destination = load.destination>
    <p>
        From: ${origin.address.city}, ${origin.address.stateCode}, ${origin.address.zip}<br/>
        To: ${destination.address.city}, ${destination.address.stateCode}, ${destination.address.zip}
    <p>
</#if>

<#if isCanceled>
    <p>Order has been cancelled.<p>
</#if>

Please find below BOL being sent from PLS PRO for the following order:

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
        <td class="noPadding">Pro: ${load.numbers.proNumber!"NOT SPECIFIED"}</td>
    </tr>
</table>

<#include "footer.ftl">

<table width="100%">
    <tr>
        <td width="25%"><b>BILL OF LADING</b></font><br>
            Non-Negotiable
        </td>
        <td width="50%" align="center"><img align="center" width="180" height="65" src="http://www.plspro.com/email/pls_logo_new.jpg"/></td>
        <td width="25%">
            <table border="1" cellspacing="0" width="100%">
                <tr>
                    <td class="noPadding">ADD PRO STICKER HERE</td>
                </tr>
                <tr>
                    <td align="center"><br><br><b>${carrier.name}</b><br><br></td>
                </tr>
            </table>
        </td>
    </tr>
    <tr></tr>
</table>

<br>

<!--ADDRESSES INFO TABLE (from BOL PFD)-->
<table border="1" cellpadding="3" cellspacing="0" width="100%">
    <tr>
        <td colspan="4">
            <p ><b>BOL No: ${load.numbers.bolNumber!"NOT SPECIFIED"}</b></p>
        </td>
    </tr>
    <tr bgcolor="#bfbfbf">
        <td class="noPadding" colspan="2" width="50%">
            <p><b>Shipper Information (Origin):</b></p>
        </td>
        <td class="noPadding" colspan="2" width="50%">
            <p><b>Reference Information:</b></p>
        </td>
    </tr>
    <tr>
        <td colspan="2" rowspan="4">
        <#if !isBlindBol>
            <p><b>${origin.contact}</b><br>
            ${originAddress}<br>
            ${origin.address.city}, ${origin.address.stateCode}, ${origin.address.zip}<br>
            ${origin.contactName}<br>
            ${origin.contactPhone}</p>
        </#if>
        </td>
        <td class="noPadding">
            <p><b>PO Number: ${load.numbers.poNumber!"NOT SPECIFIED"}</b></p>
        </td>
        <td class="noPadding">
            <#if load.origin.earlyScheduledArrival??>
                <p><b>Ship Date: ${origin.earlyScheduledArrival?string("EEE MM/dd/yyyy")}</b></p>
            <#else>
                <p><b>Ship Date:</b></p>
            </#if>
        </td>
    </tr>
    <tr>
        <td class="noPadding">
            <p><b>Pick-Up Number: ${load.numbers.puNumber!"NOT SPECIFIED"}</b></p>
        </td>
        <td class="noPadding">
            <p><b>
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
            </b></p>
        </td>
    </tr>
    <tr>
        <td class="noPadding">
            <p><b>Shipper Ref Number: ${load.numbers.refNumber!"NOT SPECIFIED"}</b></p>
        </td>
        <td class="noPadding">
            <p><b>${estimatedTransitDay}</b></p>
        </td>
    </tr>
    <tr>
        <td class="noPadding" colspan="2">
            <div>Pickup Window: 
            <#if origin.arrivalWindowStart??>
                ${origin.arrivalWindowStart?string("hh:mm a")}
            </#if> 
            -
            <#if origin.arrivalWindowEnd??>
                ${origin.arrivalWindowEnd?string("hh:mm a")}
            </#if>
            </div>
            <div>Delivery Hours Of Operation: 
            <#if destination.arrivalWindowStart??>
                ${destination.arrivalWindowStart?string("hh:mm a")}
            </#if> 
            -
            <#if destination.arrivalWindowEnd??>
                ${destination.arrivalWindowEnd?string("hh:mm a")}
            </#if>
            </div>
        </td>
    </tr>
    <tr>
        <td class="noPadding" colspan="2" height="20" bgcolor="#bfbfbf">
            <p><b>Consignee Information (Destination):</b></p>
        </td>
        <td class="noPadding" colspan="2" bgcolor="#bfbfbf">
            <p><b>Bill To Information:</b></p>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <p><b>${destination.contact}</b><br>
                ${destinationAddress}<br>
                ${destination.address.city}, ${destination.address.stateCode}, ${destination.address.zip}<br>
                ${destination.contactName}<br>
                ${destination.contactPhone}</p>
        </td>
        <td colspan="2">
            <p><b>${load.freightBillPayTo.company}</b><br>
                ${load.freightBillPayTo.address.address1},<br>
                ${load.freightBillPayTo.address.city}, ${load.freightBillPayTo.address.stateCode}, ${load.freightBillPayTo.address.zip}<br>
                ${load.freightBillPayTo.contactName}
                <#if load.volumeQuoteId??>
                    </br>VOLUME QUOTE ID: ${load.volumeQuoteId}
                </#if>
                </p>
        </td>
    </tr>
</table>

<#if showCustomsBroker>
    <table border="0" width="100%">
        <tr>
            <td>Customs Broker: ${load.customsBroker}</td>
            <td>Phone: ${load.customsBrokerPhone}</td>
        </tr>
    </table>
</#if>

<br>
<!--PRODUCT Table section-->
<table border="1" cellpadding="3" cellspacing="0" width="100%">
    <tbody>
        <tr bgcolor="#bfbfbf">
            <td class="noPadding" width="15%"><b>Handling Units</b></td>
            <td class="noPadding" width="5%"><b>Qty</b></td>
            <td class="noPadding" width="5%"><b>Pcs</b></td>
            <td class="noPadding" width="5%"><b>Stack</b></td>
            <td class="noPadding" width="5%"><b>HM</b></td>
            <td class="noPadding" width="27%"><b>Description</b></td>
            <td class="noPadding" width="10%"><b>Weight (Lbs)</b></td>
            <td class="noPadding" width="16%"><b>Dims (Inches)</b></td>
            <td class="noPadding" width="5%"><b>Class</b></td>
            <td class="noPadding" width="7%"><b>NMFC#</b></td>
        </tr>
        <#list load.loadDetails as loadDetail>
            <#if loadDetail.loadAction.name() == "PICKUP" && loadDetail.pointType.name() == "ORIGIN">
                <#assign totalQuantity=0 totalPieces=0 totalWeight=0>
                <#list loadDetail.loadMaterials as material>
                    <#assign totalQuantity=totalQuantity+(material.quantity!"0")?number totalPieces=totalPieces+material.pieces!0 totalWeight=totalWeight+material.weight!0>
                    <tr>
                        <td>${material.packageType.getDescription()!""}</td>
                        <td>${material.quantity!""}</td>
                        <td>${material.pieces!""}</td>
                        <td>${material.stackable?string("yes", "no")}</td>
                        <td>${material.hazmat?string("yes", "no")}</td>
                        <td>
                            <#if material.hazmat>
                                ${material.unNumber}; ${material.productDescription};
                                ${material.hazmatClass}; ${material.packingGroup};
                                EMERGENCY CONTACT: <@phoneFormat material.emergencyPhone/>; 
                                ${material.emergencyCompany} - Contract # ${material.emergencyContract}
                            <#else>
                                ${material.productDescription!""}
                            </#if>
                        </td>
                        <td>${material.weight!""}</td>
                        <td><#if material.width?? || material.length?? || material.height??>${material.length!""} x ${material.width!""} x ${material.height!""}</#if></td>
                        <td>${material.commodityClass.getDbCode()}</td>
                        <td>${material.nmfc!""}</td>
                    </tr>
                </#list>
            </#if>
        </#list>
        <tr>
            <td class="noPadding">Total:</td>
            <td class="noPadding">${totalQuantity}</td>
            <td class="noPadding"><#if totalPieces gt 0>${totalPieces}</#if></td>
            <td class="noPadding"></td>
            <td class="noPadding"></td>
            <td class="noPadding"></td>
            <td class="noPadding"><#if totalWeight gt 0>${totalWeight}</#if></td>
            <td class="noPadding"></td>
            <td class="noPadding"></td>
            <td class="noPadding"></td>
        </tr>
        <tr>
            <td class="noPadding" colspan="10"><p><b>Comments / Special Instructions for Pick-up: </b>${pickupNotes!""}<br><br></p></td>
        </tr>
        <tr>
            <td class="noPadding" colspan="10" ><p><b>Comments / Special Instructions for Delivery: </b>${deliveryNotes!""}<br><br></p></td>
        </tr>
    </tbody>
</table>

<br>

<!--Freight Charges section-->
<table border="1" cellpadding="3" cellspacing="0" width="100%">
    <tr>
        <td width="25%">Freight Charges: <b>Third Party</b></td>
        <td width="25%"><b>C.O.D. Amount: $__________</b></td>
        <td width="50%">
            <table cellpadding="2" width="100%">
                <tr>
                    <td rowspan="2">CARRIERS C.O.D. FEE PAID BY:</td>
                    <td><table border="1" cellspacing="0"><tr><td height="7" width="10"></td></tr></table></td>
                    <td >Shipper</td>
                </tr>
                <tr>
                    <td><table border="1" cellspacing="0"><tr><td height="7" width="10"></td></tr></table></td>
                    <td>Consignee</td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<br>

<!--Liability Limitation-->
<table border="1" cellspacing="0" width="100%">
    <tr>
        <td class="noPadding" width="50%">
            <p>Liability Limitation for loss or damage to this shipment may be<br>
                applicable. See 49 U.S.C. 14706(c)(1)(A) and (B). The agreed or<br>
                declared value of the property is hereby specifically stated by the<br>
                shipper to be not exceeding $_______per pound and Carrier's tariff<br>
                charge for such declaration of value shall be applicable to this shipment.<br>
                ____________________________________Shipper<br>
            Per ________________________________</p>
        </td>
        <td class="noPadding" width="50%">
            <p>Accepted in good order and condition, unless otherwise stated herein.<br>
            <table border="0" cellspacing="0">
                <tr>
                    <td width="60">PIECES</td>
                    <td width="140"><table border="1" cellspacing="0"><tr><td height="10" width="140"></td></tr></table></td>
                </tr>
            </table></p>
            Exceptions:<br><br><br><br><br></p>
        </td>
    </tr>
    <tr>
        <td class="noPadding"><p>Per ________________________________<br>
            <sup>(Shipper or Shipper's Agent Signature)</sup><br>
            Date and Time tendered _____________________ AM/PM<br>
            PERMANENT ADDRESS:</p>
        </td>
        <td class="noPadding"><p><b>${carrier.name}</b><br>
            Per ________________________________<br>
            <sup>(Driver's Signature)</sup><br>
            Date and Time tendered ________________________ AM/PM</p>
        </td>
    </tr>
</table>

<br>

<table border="1" cellpadding="3" cellspacing="0" width="100%">
    <tr bgcolor="#bfbfbf">
         <td class="noPadding" width="50%"><b>Shipper Certification:</b></td>
         <td class="noPadding" width="50%"><b>Carrier Certification:</b></td> 
    </tr> 
    <tr>
        <td><p><small>This is to certify that the above named materials are properly classified, described, packaged,<br>
            marked and labeled, and are in proper condition for transportation according to the applicable<br>
            regulations of the U.S. Department of Transportation.<br>
            Per: ________________________________ Date: _________________</small></p>
        </td>
        <td><p><small>Carrier acknowledges receipt of packages and required placards. Carrier certifies emergency<br>
            response information was made available and/or carrier has the Department of Transportation<br>
            emergency response guidebook or equivalent document in the vehicle.<br>
            Per: ________________________________ Date: _________________</small></p>
        </td>
    </tr>
</table>
<br>
<table border="1" cellpadding="3" cellspacing="0" width="100%">
    <tr bgcolor="#bfbfbf">
        <td><b>Receipt of Shipment:</b></td>
    </tr>
    <tr>
        <td><p><small>Shipment has been received by consignee in apparent good order unless otherwise noted. 
            Per: ________________________________ Date: _________________</small></p>
        </td>
    </tr>
</table>
<hr>
<p align="center"><small>
        PLS Logistics Services is a Property Broker and does not provide any motor carrier services and PLS does not take possession or custody of freight.
        Carrier is solely liable for loss or damage to cargo and the extent of carrier's liability may be limited by carrier's tariff or other governing publication.
        PLS is not liable for any loss or damage to cargo.
    </small>
</p>
<table width="100%">
    <tr>
        <td colspan="2"><b>Shipment created by <#if load.modification.createdDate??>(${load.modification.createdDate?string("MM/dd/yyyy")}):</#if></b></td>
    </tr>
    <tr>
        <td width="30%">${contact.contactName}</td>
        <td width="30%"><@phoneFormat contact.phone/></td>
        <td width="30%">Email: <a href="mailto:${contact.email}">${contact.email}</a></td>
    </tr>
</table>