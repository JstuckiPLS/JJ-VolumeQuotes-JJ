<html>
<head>
    <title></title>
</head>
<body>
<div id="header">
</div>
<div id="main">
<p>Dear <b>${carrierName}</b>,</p>
<p>According to our records, we have not received one or more of your invoices.</p>
<p>We ask that you send your complete invoice or invoices to <a href="mailto:freightbills@plslogistics.com">freightbills@plslogistics.com</a>.
For detailed load information and instructions on how to submit a copy of your invoice, please see below.</p>
<p>If your invoice was already submitted within the last seven (7) days, please disregard this message.</p>
<br>
<table border="2">
    <tr>
        <th>PLS Load ID</th>
        <th>Ship Ref #</th>
        <th>BOL #</th>
        <th>Origin</th>
        <th>Destination</th>
        <th>Pickup Date</th>
        <th>Weight</th>
        <th>Invoice #</th>
        <th>Documents</th>
        <th>Notes</th>
    </tr>
    <#list paperworkEmails as paperwork>
        <tr>
            <td>${paperwork.loadId?c}</td>
            <td>${paperwork.shipperRef!""}</td>
            <td>${paperwork.bol!""}</td>
            <td>${paperwork.origin}</td>
            <td>${paperwork.destination}</td>
            <td>${paperwork.pickupDate?string("MM/dd/yyyy")}</td>
            <td>${paperwork.weight!""} Lbs</td>
            <td>${paperwork.invoiceNumber!""}</td>
            <td>${paperwork.documents!""}</td>
            <td>${paperwork.note!""}</td>
        </tr>
    </#list>
</table>
<br>

<p><b>WHAT to send with your invoice:</b></p>
<ul>
 <li>PLS Rate Agreement</li>
 <li>Bill of Lading (BOL)</li>
 <li>Other documents, such as scale tickets and receipts (please refer to the rate agreement for a complete list of required documents)</li>
</ul>

<p><b>WHERE to send your invoice:</b></p>
Email: <a href="mailto:freightbills@plslogistics.com">freightbills@plslogistics.com</a>


<p><b>Note</b>: Payment may be delayed if the invoice is sent directly to any other email address.</p>

<p><b>TIPS to help avoid payment delays:</b></p>
<ul>
 <li>Reference the PLS Load ID number on your invoice</li>
 <li>Ensure all documents are legible</li>
 <li>Ensure BOL is signed</li>
 <li>Ensure all pages of the BOL are submitted</li>
 <li>Submit one invoice for each load transported</li>
</ul>

<p><a href="${unsubscribe}" title="Follow link to unsubscribe">Click here</a> to unsubscribe.</p>

Sincerely,<br>
PLS Logistics Services
</div>
</body>
</html>
