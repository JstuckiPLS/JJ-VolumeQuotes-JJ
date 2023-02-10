<#include "header.ftl">
<p>EDI Shipment Tracking data has not been saved.</p>
<p>
    <#if ediFileName??>
        EDI file name:<b>"${ediFileName}".</b>
    <#else>
        Data came from Sterling.
    </#if>
    <br/>
    Reason: <b>"${errorMsg}"</b>.
</p>
<p>Please see logs/edi.log for details.</p>
<#include "footer.ftl">