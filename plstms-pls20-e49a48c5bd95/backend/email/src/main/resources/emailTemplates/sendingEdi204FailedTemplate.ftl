<#include "header.ftl">
<#include "addressCalculation.ftl">
    <#if !carrier?has_content>
        <p>Load Tender data has not been sent to carrier.</p>
    <#elseif carrier?is_number>
        <p>Load Tender data has not been sent to carrier <b>"${carrier?c}"</b>.</p>
    <#else>
        <p>Load Tender data has not been sent to carrier <b>"${carrier.name}"</b> (SCAC: <b>"${carrier.scac}"</b>).</p>
    </#if>
    <p>Please see logs/edi.log for details.</p>
    <#if !load?has_content>
        <p>Load not found.</p>
    <#elseif load?is_number>
        <p>Load not found <b>"${load!?c}"</b>.</p>
    <#else>
        <b><u>Load:</u></b>
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
                <td colspan="2">&nbsp;<br>&nbsp;<br></td>
            </tr>
            <tr>
                <td class="noPadding">
                    <#if !isBlindBol>Origin:</#if>
                </td>
                <td>Destination:</td>
            </tr>
            <tr>
                <td class="noPadding">
                    <#if !isBlindBol>${load.origin.contactName}</#if>
                </td>
                <td>${load.destination.contactName}</td>
            </tr>
            <tr>
                <td class="noPadding">
                    <#if !isBlindBol>${originAddress}</#if>
                </td>
                <td>${destinationAddress}</td>
            </tr>
            <tr>
                <td class="noPadding">
                    <#if !isBlindBol>${load.origin.address.city}, ${load.origin.address.stateCode}</#if>
                </td>
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
    </#if>
<#include "footer.ftl">