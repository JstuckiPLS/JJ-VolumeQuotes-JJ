<#include "header.ftl">

<h3>Hello, ${user.firstName} ${user.lastName}!</h3>
    <p>You were registered in PLS PRO system.</p>
    <p>Your User ID is ${user.login}</p>
    <p>Your password is ${newPassword}</p>
    <p>Please, change this password after <a href="${clientUrl}">logging in</a>.</p>
    <p>Sincerely, PLS Notification</p>

<#include "footer.ftl">