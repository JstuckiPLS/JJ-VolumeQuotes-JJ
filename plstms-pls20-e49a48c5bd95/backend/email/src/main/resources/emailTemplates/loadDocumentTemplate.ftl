<#include "header.ftl">

<#if load.origin?? && load.origin.address?? && load.destination?? && load.destination.address??>
    <#assign origin = load.origin.address>
    <#assign destination = load.destination.address>
    <p>
        From: ${origin.city}, ${origin.stateCode}, ${origin.zip}<br/>
        To: ${destination.city}, ${destination.stateCode}, ${destination.zip}
    <p>
</#if>

<#if docName??>
    <p>The attached ${docName} ${verb} being sent from PLS PRO for the following order:</p>
<#else>
    <p>The attached BOL is being sent from PLS PRO for the following order:</p>
</#if>

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
