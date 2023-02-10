<#include "header.ftl">
<div>
    You have a new invoice from <b>PLS Logistics Services</b> attached to this email.
</div>
<#if comments?has_content>
    <div>${comments}</div>
</#if>
<#if documentId gt 0>
    <div>For your convenience required paperwork can be downloaded by clicking <a href="${paperworkLink}">HERE</a>.</div>
</#if>
<br/>
<hr/>
<#include "footer.ftl">