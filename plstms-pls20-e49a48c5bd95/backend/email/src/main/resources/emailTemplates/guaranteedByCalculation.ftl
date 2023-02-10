<#if load.activeCostDetail.guaranteedBy??>
    <#if load.activeCostDetail.guaranteedBy == 2400>
        <#assign guaranteedTime = 'EOD'>
    <#else>
        <#assign hours = 12>
        <#assign am = "AM">
        <#if (load.activeCostDetail.guaranteedBy >= 100)>
            <#assign hours = (load.activeCostDetail.guaranteedBy / 100)?floor>
            <#if (hours > 12)>
                <#assign am = "PM">
                <#assign hours = hours - 12>
            <#elseif hours == 12>
                <#assign am = "PM">
            </#if>
        </#if>
        <#assign minutes = load.activeCostDetail.guaranteedBy % 100>
        <#assign guaranteedTime = hours + ":" + minutes?string("00") + " " + am>
    </#if>
</#if>