<br/>
Email message here...
<br/>
<br/>
<br/>
<br/>
<br/>
<div>
    <#if contact.contactName??>
        <p>Contact: ${contact.contactName}</p>
    </#if>
    <#if contact.email??>
        <p>Email: <a href="mailto:${contact.email}">${contact.email}</a></p>
    </#if>
    <#if contact.phone?? && contact.phone.number??>
       <#if contact.phone.countryCode?? && contact.phone.areaCode?? && contact.phone.extension??>
           <p>Phone: +${contact.phone.countryCode?trim}(${contact.phone.areaCode?trim})${contact.phone.number[0..2]} ${contact.phone.number[3..]}  Ext.: ${contact.phone.extension}</p>
       <#elseif contact.phone.countryCode?? && contact.phone.areaCode??>
           <p>Phone: +${contact.phone.countryCode?trim}(${contact.phone.areaCode?trim})${contact.phone.number[0..2]} ${contact.phone.number[3..]}</p>
       <#elseif contact.phone.areaCode?? && contact.phone.extension??>
           <p>Phone: +1(${contact.phone.areaCode?trim})${contact.phone.number[0..2]} ${contact.phone.number[3..]}  Ext.: ${contact.phone.extension}</p>
       <#elseif contact.phone.areaCode??>
           <p>Phone: +1(${contact.phone.areaCode?trim})${contact.phone.number[0..2]} ${contact.phone.number[3..]}</p>
       <#elseif contact.phone.extension??>
           <p>Phone: ${contact.phone.number}  Ext.: ${contact.phone.extension}</p>
       <#else>
           <p>Phone: ${contact.phone.number}</p>
       </#if>
    </#if>
        <p>Thank you for using PLS Logistics Services</p>
</div>