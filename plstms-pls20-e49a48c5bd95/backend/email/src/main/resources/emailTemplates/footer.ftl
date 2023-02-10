        </div>
        <br/><br/>
        <div id="footer">
            <div>
				<#if contact??>
					<#if contact.contactName?has_content>
						<p>Contact: ${contact.contactName}</p>
					<#else>
						<p>PLS Contact: LTL Customer Service</p>
					</#if>
					<#if contact.email?has_content>
                        <p>Email: <a href="mailto:${contact.email}">${contact.email}</a></p>
                    <#else>
                        <p>Email: <a href="mailto:plsfreight1@plslogistics.com">plsfreight1@plslogistics.com</a></p>
                    </#if>
					<#if contact.phone?? && contact.phone.number?has_content>
					   <#if contact.phone.countryCode?has_content && contact.phone.areaCode?has_content && contact.phone.extension?has_content>
                           <p>Phone: +${contact.phone.countryCode?trim}(${contact.phone.areaCode?trim})${contact.phone.number[0..2]} ${contact.phone.number[3..]}  Ext.: ${contact.phone.extension}</p>
					   <#elseif contact.phone.countryCode?has_content && contact.phone.areaCode?has_content>
					       <p>Phone: +${contact.phone.countryCode?trim}(${contact.phone.areaCode?trim})${contact.phone.number[0..2]} ${contact.phone.number[3..]}</p>
					   <#elseif contact.phone.areaCode?has_content && contact.phone.extension?has_content>
                           <p>Phone: +1(${contact.phone.areaCode?trim})${contact.phone.number[0..2]} ${contact.phone.number[3..]}  Ext.: ${contact.phone.extension}</p>
					   <#elseif contact.phone.areaCode?has_content>
					       <p>Phone: +1(${contact.phone.areaCode?trim})${contact.phone.number[0..2]} ${contact.phone.number[3..]}</p>
					   <#elseif contact.phone.extension?has_content>
                           <p>Phone: ${contact.phone.number}  Ext.: ${contact.phone.extension}</p>
                       <#else>
					       <p>Phone: ${contact.phone.number}</p>
					   </#if>
					</#if>
				<#else>
					<p>PLS Contact: LTL Customer Service</p>
					<p>Email: <a href="mailto:plsfreight1@plslogistics.com">plsfreight1@plslogistics.com</a></p>
					<p>Phone: +1(888)757 8261</p>
					<p>Thank you for using <a href="${clientUrl}">PLS Logistics Services</a></p>
				</#if>
            </div>
			<#if isManualBol??>
				<#if logoPath??>
					<div id="logo" class="left"></div>
					<div id="printLogo" class="left">
						<img align="center" width="100" height="40" src=${logoPath}/>
					</div>
					<div id="headerImage" class="right"></div>
				</#if>
			<#else>
				<div id="logo" class="left"></div>
				<div id="printLogo" class="left">
					<img align="center" width="100" height="40" src="http://www.plspro.com/email/pls_logo_new.jpg"/>
				</div>
				<div id="headerImage" class="right"></div>
			</#if>
        </div>
    </body>
</html>