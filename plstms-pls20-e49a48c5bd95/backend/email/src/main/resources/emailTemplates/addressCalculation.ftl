<#if load?? && !load?is_number && load.origin?? && load.origin.address??>
    <#if load.origin.address.address1??>
        <#if load.origin.address.address2??>
            <#assign originAddress = load.origin.address.address1 + ", " + load.origin.address.address2>
        <#else>
            <#assign originAddress = load.origin.address.address1>
        </#if>
    <#else>
        <#if load.origin.address.address2??>
            <#assign originAddress = load.origin.address.address2>
        </#if>
    </#if>
    <#if load.destination.address.address1??>
        <#if load.destination.address.address2??>
            <#assign destinationAddress = load.destination.address.address1 + ", " + load.destination.address.address2>
        <#else>
            <#assign destinationAddress = load.destination.address.address1>
        </#if>
    <#else>
        <#if load.destination.address.address2??>
            <#assign destinationAddress = load.destination.address.address2>
        </#if>
    </#if>
</#if>