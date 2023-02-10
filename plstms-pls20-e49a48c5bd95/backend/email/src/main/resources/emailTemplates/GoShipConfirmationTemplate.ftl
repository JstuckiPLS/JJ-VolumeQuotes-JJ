<#include "header.ftl">
<div>
    Please make sure that BOL goes with the shipment. Give it to the driver.<b>It is very important to have the paperwork ready and given to the driver. Please ensure the person handling the shipment has the attached document printed and ready on scheduled pickup date.</b> Not providing the proper paperwork may result in Truck Ordered Not Used fees.     
    <br><br>
    <b>Please have material ready for pickup prior to driver arrival on scheduled date.</b>
    <br><br>
    This is an automated email from GoShip to inform you that your shipping # <#if load??>${load.id?c}</#if> is created.
    <br><br>
        <p>
            <#if load??>
                <table>
                    <tr>
                        <td>Customer Name:</td>
                        <#if load.organization??><td>${load.organization.name!" "}</td></#if>
                    </tr>
                    <tr>
                        <td>Carrier Name:</td>
                        <#if load.carrier?? && load.carrier.name??><td>${load.carrier.name!" "}</td></#if>
                    </tr>
                    <tr>
                        <td>Transit Time:</td>
                        <td>${estimatedTransitDay!" "}</td>
                    </tr>
                    <tr>
                        <td>Costs:</td>
                        <#if load.activeCostDetail?? && load.activeCostDetail.totalRevenue??><td>${load.activeCostDetail.totalRevenue?string.currency}</td></#if>
                    </tr>
                </table>
            </#if>
        </p>
    <br>
    <p>
        <#if load??>
            <table>
                <tr>
                    <td>Origin:</td>
                    <td>Destination:</td>
                </tr>
                <tr>
                    <td><#if load.origin?? && load.origin.address??>${load.origin.address.address1!" "} </#if></td>
                    <td><#if load.destination?? && load.destination.address??>${load.destination.address.address1!" "} </#if></td>
                </tr>
                <tr>
                    <td><#if load.origin?? && load.origin.address??> ${load.origin.address.address2!" "} </#if></td>
                    <td><#if load.destination?? && load.destination.address??>${load.destination.address.address2!" "} </#if></td>
                </tr>
                <tr>
                    <td><#if load.origin?? && load.origin.address??> ${load.origin.address.city!" "}, ${load.origin.address.stateCode!" "}, ${load.origin.address.zip!" "}</#if></td>
                    <td><#if load.destination?? && load.destination.address?? && load.destination.address??>${load.destination.address.city!" "}, ${load.destination.address.stateCode!" "}, ${load.destination.address.zip!" "}</#if></td>
                </tr>
                <tr>
                    <td><#if load.origin??>${load.origin.contactPhone!" "}</#if></td>
                    <td><#if load.destination??>${load.destination.contactPhone!" "}</#if></td>
                </tr>
            </table>
        </#if>
    </p>
    <br>
    <p>
        <#if load?? && load.origin??>
             <table>
                 <tr>
                     <td>Expected Pickup Date:</td>
                     <#if load.origin.earlyScheduledArrival??><td>${load.origin.earlyScheduledArrival?string("EEE MM/dd/yyyy")}</td><#else><td></td></#if>
                 </tr>
             </table>
        </#if>
    </p>
    <b>Products</b>
    <br>
    <p>
    <#if load??>
        <table border="2">
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
            <#list load.loadDetails as loadDetail>
                <#if loadDetail.loadAction.name() == "PICKUP" && loadDetail.pointType.name() == "ORIGIN">
                    <#assign totalWeight=0>
                    <#list loadDetail.loadMaterials as material>
                        <#assign totalWeight=totalWeight+material.weight!0>
                        <tr>
                            <td align="right">${material.weight} Lbs</td>
                            <td>${material.commodityClass.getDbCode()}</td>
                            <td>W: ${material.width!"n/a"}, L: ${material.length!"n/a"}, H: ${material.height!"n/a"}</td>
                            <td>${material.quantity!""}</td>
                            <td>${material.packageType.getDescription()!""}</td>
                            <td>${material.productDescription!""}</td>
                            <td>${material.stackable?string("yes", "no")}</td>
                            <td>${material.hazmat?string("yes", "no")}</td>
                        </tr>
                    </#list>
                </#if>
            </#list>
        </table>
        <#if totalWeight?? && totalWeight gt 0>
            Total Weight: ${totalWeight} Lbs
        </#if>
    </#if>
    <br>
    <br>
        Payment Information:
    <p>
       <#if prepaidDetails??>
        <table>
            <tr>
                <td>OriginationID:</td>
                <td>${prepaidDetails.paymentId!" "}</td>
            </tr>
            <tr>
                <td>Transaction Date:</td>
                <#if prepaidDetails.paymentDate?? && prepaidDetails.paymentDate?has_content><td>${prepaidDetails.paymentDate?string("EEE MM/dd/yyyy")}</td><#else><td></td></#if>
            </tr>
        </table>
        </#if>
    </p>
    </p>
	<p><strong>
		Transit times shown are estimated and are not guaranteed.
		The pickup date and observed holidays do not count transit days. 
		Inclement weather, inaccurate contact information, appointment related requirements
		or other issues beyond the control of the carrier may further delay transit times.
	</strong></p>
    <br>
</div>
<#include "GoShipFooter.ftl">