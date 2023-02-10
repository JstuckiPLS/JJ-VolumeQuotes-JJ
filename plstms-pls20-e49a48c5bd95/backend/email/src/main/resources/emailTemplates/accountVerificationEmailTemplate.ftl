<#include "header.ftl">

<h3>Hello, ${firstName} ${lastName}!</h3>
    <p>Thank you for signing up for GoShip.  There's one more step needed to get you all setup.</p>
    <p>Please click the link below to finish activating your account.</p>
    <p><a href="${verificationLink}">User Verification</a>.</p>
    <p>Sincerely, The GoShip Team</p>
    
    <p>If you did not request this account or are having trouble activating the account, please contact <a href="${customerSupportEmail}">Customer Support</a></p>

<#include "GoShipFooter.ftl">
