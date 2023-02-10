<#include "header.ftl">

<#if load?? && load.items??><#assign products = load.items></#if>

<#if load?? && load.billingDetails??><#assign billingDetails = load.billingDetails></#if>
<#if billingDetails?? && billingDetails.zip??><#assign billingDetailsZip = billingDetails.zip></#if>

<#if user?? && user.parentOrganization??><#assign organization = user.parentOrganization></#if>

<div>
    This shipment was paid by GoShip user <b><#if user??>${user.firstName!" "} ${user.lastName!" "}</#if></b>, company <b><#if organization??>${organization.name!" "}, ${organization.contactEmail!""}</#if></b>.
    <br>
    But due to a failure wasn't automatically created in PLS Pro 2.0.
    <br>
    Please create a shipment manually in PLS Pro 2.0 with following info:
    <br><br>
    1.<b>Products</b>
    <br>
    <p>
        <table border="1">
            <tr>
                <th>Weight</th>
                <th>Class</th>
                <th>Dimensions (Inches)</th>
                <th>Qty</th>
                <th>Pack. Type</th>
                <th>Product Description</th>
                <th>Stackable</th>
                <th>Hazmat</th>
            </tr>
            <tr>
                <#if products?? && products?has_content>
                <#list products as product>
                    <tr>
                        <td align="right">${product.weight!" "} Lbs</td>
                        <td>${product.productClass!" "}</td>
                        <td>W: ${product.width!"n/a"}, L: ${product.length!"n/a"}, H: ${product.height!"n/a"}</td>
                        <td>${product.quantity!""}</td>
                        <td>${product.productPackage!""}</td>
                        <td>${product.description!""}</td>
                        <#if product.stackable??><td>${product.stackable?string("yes", "no")}</td><#else><td>no</td></#if>
                        <#if product.hazardous??><td>${product.hazardous?string("yes", "no")}</td><#else><td>no</td></#if>
                    </tr>
                </#list>
                </#if>
            </tr>
        </table>
    </p>
    <br>
    2.<b>Pickup</b>
    <br>
    2.1.<b>Pickup Address Details</b>
    <br>
    <p>
        <#if load?? && load.origin??>
        <table
            <tr>
                <td>Location Type:</td>
                <td>${load.origin.locationType!" "}</td>
            </tr>
            <tr>
                <td>Name/Company Name:</td>
                <td>${load.origin.name!" "}</td>
            </tr>
            <tr>
                <td>City, St, Zip:</td>
                <#if load.origin.zip??><td>${load.origin.zip.city!" "}, ${load.origin.zip.state!" "}, ${load.origin.zip.zip!" "}</td><#else><td></td></#if>
            </tr>
            <tr>
                <td>Address 1:</td>
                <td>${load.origin.address1!" "}</td>
            </tr>
            <tr>
                <td>Address 2:</td>
                <td>${load.origin.address2!" "}</td>
            </tr>
            <tr>
                <td>Pickup Date:</td>
               <#if load?? && load.pickupDate??><td>${load.pickupDate?string("EEE MM/dd/yyyy")}</td><#else><td></td></#if>
            </tr>
        </table>
        </#if>
    </p>
    <br>
    2.2.<b>Pickup Contact Details </b> 
    <br>
    <p>
        <#if load?? && load.origin??>
        <table>
            <tr>
                <td>Pickup Contact Name:</td>
                <td>${load.origin.contactName!" "}</td>
            </tr>
            <tr>
                <td>Contact Phone Number:</td>
                <td><#if load.origin.contactPhone?? && load.origin.contactPhone.number?has_content>
                       <#if load.origin.contactPhone.countryCode?has_content && load.origin.contactPhone.areaCode?has_content && load.origin.contactPhone.extension?has_content>
                           <p>Phone: +${load.origin.contactPhone.countryCode?trim}(${load.origin.contactPhone.areaCode?trim})${load.origin.contactPhone.number[0..2]} ${load.origin.contactPhone.number[3..]}  Ext.: ${load.origin.contactPhone.extension}</p>
                       <#elseif load.origin.contactPhone.countryCode?has_content && load.origin.contactPhone.areaCode?has_content>
                           <p>Phone: +${load.origin.contactPhone.countryCode?trim}(${load.origin.contactPhone.areaCode?trim})${load.origin.contactPhone.number[0..2]} ${load.origin.contactPhone.number[3..]}</p>
                       <#elseif load.origin.contactPhone.areaCode?has_content && load.origin.contactPhone.extension?has_content>
                           <p>Phone: +1(${load.origin.contactPhone.areaCode?trim})${load.origin.contactPhone.number[0..2]} ${load.origin.contactPhone.number[3..]}  Ext.: ${load.origin.contactPhone.extension}</p>
                       <#elseif load.origin.contactPhone.areaCode?has_content>
                           <p>Phone: +1(${load.origin.contactPhone.areaCode?trim})${load.origin.contactPhone.number[0..2]} ${load.origin.contactPhone.number[3..]}</p>
                       <#elseif load.origin.contactPhone.extension?has_content>
                           <p>Phone: ${load.origin.contactPhone.number}  Ext.: ${load.origin.contactPhone.extension}</p>
                       <#else>
                           <p>Phone: ${load.origin.contactPhone.number}</p>
                       </#if>
                </#if></td>
            </tr>
            <tr>
                <td>Contact Email: </td>
                <td>${load.origin.contactEmail!" "}</td>
            </tr>
            <tr>
                <td>Special Pickup Instructions:</td>
                <td>${load.origin.instructions!" "}</td>
            </tr>
        </table>
        </#if>
    </p>
    <br>
    2.3.<b>Pickup Accessorials : </b>${pickupAccessorial!""}
    <br><br>
    3.<b>Delivery</b>  
    <br>
    3.1.<b> Delivery Address Details </b> 
    <br>
    <p>
        <#if load?? && load.destination??>
        <table>
            <tr>
                <td>Location Type:</td>
                <td>${load.destination.locationType!" "}</td>
            </tr>
            <tr>
                <td>Name/Company Name:</td>
                <td>${load.destination.name!" "}</td>
            </tr>
            <tr>
                <td>City, St, Zip:</td>
                <#if load.destination.zip??><td>${load.destination.zip.city!" "}, ${load.destination.zip.state!" "}, ${load.destination.zip.zip!" "}</td><#else><td></td></#if>
            </tr>
            <tr>
                <td> Address 1: </td>
                <td>${load.destination.address1!" "}</td>
            </tr>
            <tr>
                <td> Address 2:</td>
                <td>${load.destination.address2!" "}</td>
            </tr>
        </table>
        </#if>
    </p>
    <br>
    3.2.<b>Delivery Contact Details</b>
    <br>
    <p>
        <#if load?? && load.destination??>
        <table>
            <tr>
                <td>Delivery Contact Name:</td>
                <td>${load.destination.contactName!" "}</td>
            </tr>
            <tr>
                <td>Contact Phone Number:</td>
                <td><#if load.destination.contactPhone?? && load.destination.contactPhone.number?has_content>
                       <#if load.destination.contactPhone.countryCode?has_content && load.destination.contactPhone.areaCode?has_content && load.destination.contactPhone.extension?has_content>
                           <p>Phone: +${load.destination.contactPhone.countryCode?trim}(${load.destination.contactPhone.areaCode?trim})${load.destination.contactPhone.number[0..2]} ${load.destination.contactPhone.number[3..]}  Ext.: ${load.destination.contactPhone.extension}</p>
                       <#elseif load.destination.contactPhone.countryCode?has_content && load.destination.contactPhone.areaCode?has_content>
                           <p>Phone: +${load.destination.contactPhone.countryCode?trim}(${load.destination.contactPhone.areaCode?trim})${load.destination.contactPhone.number[0..2]} ${load.destination.contactPhone.number[3..]}</p>
                       <#elseif load.destination.contactPhone.areaCode?has_content && load.destination.contactPhone.extension?has_content>
                           <p>Phone: +1(${destination.contactPhone.areaCode?trim})${destination.contactPhone.number[0..2]} ${load.destination.contactPhone.number[3..]}  Ext.: ${load.destination.contactPhone.extension}</p>
                       <#elseif load.destination.contactPhone.areaCode?has_content>
                           <p>Phone: +1(${load.destination.contactPhone.areaCode?trim})${load.destination.contactPhone.number[0..2]} ${load.destination.contactPhone.number[3..]}</p>
                       <#elseif load.destination.contactPhone.extension?has_content>
                           <p>Phone: ${load.destination.contactPhone.number}  Ext.: ${load.destination.contactPhone.extension}</p>
                       <#else>
                           <p>Phone: ${load.destination.contactPhone.number}</p>
                       </#if>
                </#if></td>
            </tr>
            <tr>
                <td>Contact Email:</td
                <td>${load.destination.contactEmail!" "}</td>
            </tr>
            <tr>
                <td> Special Pickup Instructions:</td>
                <td>${load.destination.instructions!" "}</td>
            </tr>
        </table>
        </#if>
    </p>
    <br>
    3.3.<b>Delivery  Accessorials</b>: ${deliveryAccessorial!" "}
    <br><br>
    5.<b>Carrier Info</b>:
    <br>
    <p>
        <#if load??>
        <table>
            <tr>
                <td>Carrier Name:</td>
                <#if load.quote?? && load.quote.carrier??><td>${load.quote.carrier.name!" "}</td><#else><td></td></#if>
            </tr>
            <tr>
                <td>Transit Time:</td>
                <td>${estimatedTransitDay!" "}</td>
            </tr>
            <tr>
                <td>Estimated Delivery:</td>
                <#if load.quote?? && load.quote.deliveryDate??><td>${load.quote.deliveryDate?string("EEE MM/dd/yyyy")}</td><#else><td></td></#if>
            </tr>
            <tr>
                <td>Costs:</td>
                <#if load.quote??><td>${load.quote.cost!" "}</td><#else><td></td></#if>
            </tr>
        </table>
        </#if>
    </p>
    <br>
    6.<b>Bill To Info</b>:
    <br>
    <p>
       <#if billingDetails??>
        <table>
            <tr>
                <td>Full Name</td>
                <td>${billingDetails.fullName!" "}</td>
            </tr>
            <tr>
                <td>Billing Address</td>
                <td>${billingDetails.billingAddress!" "}</td>
            </tr>
            <tr>
                <td>City, St, Zip</td>
                    <#if billingDetailsZip??><td>${billingDetailsZip.city!" "}, ${billingDetailsZip.state!" "}, ${billingDetailsZip.zip!" "}</td><#else><td></td></#if>
            </tr>
        </table>
        </#if>
    </p>
    <br>
    7.<b>Payment Information</b>:
    <br>
    <p>
       <#if load??>
        <table>
            <tr>
                <td>OriginationID:</td>
                <td>${load.originationId!" "}</td>
            </tr>
            <tr>
                <td>Transaction Date:</td>
                <#if currentDate?? && currentDate?has_content><td>${currentDate?string("EEE MM/dd/yyyy")}</td><#else><td></td></#if>
            </tr>
        </table>
        </#if>
    </p>
</div>
<#include "GoShipFooter.ftl">