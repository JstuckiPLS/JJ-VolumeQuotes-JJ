<#assign today = .now?string("ddMMMyyyy")?date("ddMMMyyyy")>
<#assign createdDate = quote.modification.createdDate?string("ddMMMyyyy")?date("ddMMMyyyy")>

<#assign oneDayMillis = 24 * 60 * 60 * 1000>
<#assign expireDays = 7>

<#assign pickupExpire = (quote.pickupDate?long - today?long) / oneDayMillis>
<#assign createdExpire = expireDays - ((today?long - createdDate?long) / oneDayMillis)>
<#if (pickupExpire > createdExpire)>
    <#assign expireIn = createdExpire>
<#else>
    <#assign expireIn = pickupExpire>
</#if>

<#assign accessorialsCost = 0>
<#list quote.costDetails.costDetailsItems as item>
    <#if item.owner.name() == 'S'>
        <#if item.refType == "GD">
            <#assign guaranteedCost = item.subTotal>
        <#elseif item.refType == 'FS'>
            <#assign fuelCost = item.subTotal>
        <#elseif item.refType == 'SRA'>
            <#assign baseRate = item.subTotal>
        <#else>
            <#assign accessorialsCost = accessorialsCost + item.subTotal>
        </#if>
    </#if>
</#list>

<#if quote.costDetails.guaranteedBy??>
    <#if quote.costDetails.guaranteedBy == 2400>
        <#assign guaranteedTime = 'EOD'>
    <#else>
        <#assign hours = 12>
        <#assign am = "AM">
        <#if (quote.costDetails.guaranteedBy >= 100)>
            <#assign hours = (quote.costDetails.guaranteedBy / 100)?floor>
            <#if (hours > 12)>
                <#assign am = "PM">
                <#assign hours = hours - 12>
            <#elseif hours == 12>
                <#assign am = "PM">
            </#if>
        </#if>
        <#assign minutes = quote.costDetails.guaranteedBy % 100>
        <#assign guaranteedTime = hours + ":" + minutes?string("00") + " " + am>
    </#if>
</#if>

<#include "header.ftl">

<h3 style="text-decoration: underline;">Rate Quote:</h3>

<table style="min-width:780px;">
    <tr>
        <#if quote.customer??><td style="width:65%;" class="noPadding">${quote.customer.name}</td></#if>
        <#if quote.quoteReferenceNumber??><td>Quote Ref #: ${quote.quoteReferenceNumber}</td></#if>
    </tr>
    <tr>
        <td colspan="2">&nbsp;<br>&nbsp;<br></td>
    </tr>
    <tr>
        <td class="noPadding"><#if quote.customer?? && quote.customer.address??>${quote.customer.address.address1}</#if></td>
        <td>SERVICE: ${quote.costDetails.guaranteedBy???string("GUARANTEED", "STANDARD")}</td>
    </tr>
    <#if quote.customer?? && quote.customer.address??>
        <tr>
            <td class="noPadding">${quote.customer.address.city}, ${quote.customer.address.stateCode}, ${quote.customer.address.zip}</td>
            <td></td>
        </tr>
    </#if>
    <tr>
        <td colspan="2">&nbsp;<br>&nbsp;<br></td>
    </tr>
    <tr>
        <td class="noPadding">
            Quote Date:<br/>
            <b>${createdDate?string("EEE MM/dd/yyyy")}</b><br/>
            <br/>
            Pickup Date:<br/>
            <b>${quote.pickupDate?string("EEE MM/dd/yyyy")}</b>
        </td>
        <td>
            Base Rate: <#if baseRate??>${baseRate?string.currency}</#if><br/>
            <#if quote.costDetails.guaranteedBy??>
                Guaranteed by: (${guaranteedTime}) ${guaranteedCost?string.currency}<br/>
            </#if>
            Fuel Surcharge: <#if fuelCost??>${fuelCost?string.currency}</#if><br/>
            Accessorials (Total): <#if accessorialsCost??>${accessorialsCost?string.currency}</#if><br/>
            Total Cost: <#if quote.costDetails.totalRevenue??>${quote.costDetails.totalRevenue?string.currency}</#if><br/>
            <b>Quote currency is ${quote.carrier.currencyCode!"USD"}</b>
        </td>
    </tr>
    <tr>
        <td colspan="2">&nbsp;<br>&nbsp;<br></td>
    </tr>
    <tr>
        <td class="noPadding">Origin:</td>
        <td>Destination:</td>
    </tr>
    <tr>
        <td class="noPadding">${quote.route.originCity}, ${quote.route.originState}, ${quote.route.originZip}</td>
        <td>${quote.route.destCity}, ${quote.route.destState}, ${quote.route.destZip}</td>
    </tr>
</table>

<br/>
Selected Accessorials:
<#if quote.accessorials?has_content>
    <br/>
    <strong>
        <#list quote.accessorials as accessorial>
            <#if accessorial.accessorialType??>
                ${accessorial.accessorialType.description}
                <#if accessorial.accessorialType.accessorialGroup??>
                    <#if accessorial.accessorialType.accessorialGroup.name() == "PICKUP">
                        Pickup<#if accessorial_has_next>,</#if>
                    <#else>
                        Delivery<#if accessorial_has_next>,</#if>
                    </#if>
                </#if>
            </#if>
        </#list>
    </strong>
<#else>
 None
</#if>

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
    <#list quote.materials as material>
        <tr>
            <td align="right">${material.weight}&nbsp;Lbs</td>
            <td>${material.commodityClass.getDbCode()}</td>
            <td>W:&nbsp;${material.width!"n/a"},&nbsp;L:&nbsp;${material.length!"n/a"},&nbsp;H:&nbsp;${material.height!"n/a"}</td>
            <td>${material.pieces!""}</td>
            <td><#if material.packageType??>${material.packageType.getDescription()}</#if></td>
            <td>${material.productDescription!""}</td>
            <td>${material.productCode!"n/a"}</td>
            <td>${material.nmfc!""}</td>
            <td>${material.stackable?string("yes", "no")}</td>
            <td>${material.hazmat?string("yes", "no")}</td>
        </tr>
    </#list>
</table>

<br>
<p>
    <#if (0 > expireIn)>
        This Quote has already expired.
    <#elseif 0 == expireIn>
        This Quote is subject to expiration today.
    <#elseif 1 == expireIn>
        This Quote is subject to expiration tomorrow.
    <#else>
        This Quote will expire in ${expireIn} days.
    </#if>
</p>

<p>All rates based on information supplied by customer and are subject to any variations in weight, dimensions, fuel surcharge,
origins\destinations, accessorial services, service level, and motor freight classifications.</p>
<p>Please call 1-888-757-8261 or email <a href="mailto:plsfreight1@plslogistics.com">plsfreight1@plslogistics.com</a> to place order.</p>

<small style="color:#bfbfbf;">PLS Logistics Services is a Property Broker and does not provide any motor carrier services and PLS does not take possession or custody of freight.
Carrier is solely liable for loss or damage to cargo and the extent of carrier's liability may be limited by carrier's tariff or other governing publication.
PLS is not liable for any loss or damage to cargo.</small>

<#include "footer.ftl">