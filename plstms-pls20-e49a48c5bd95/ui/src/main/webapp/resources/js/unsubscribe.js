
var resultMessage = document.getElementsByClassName('unsubscribeMessage')[0];
var loadingMessage = document.getElementsByClassName('loadingMessage')[0];
var encodedId = window.location.href.substring(window.location.href.lastIndexOf("=") + 1);
var clientUrl = window.location.origin;
var xhr = new XMLHttpRequest();
xhr.open("GET",  clientUrl + '/restful/unsubscribe/' + encodedId, true);
xhr.send();
xhr.onreadystatechange = function() {
    if (xhr.readyState !== 4) {
        return;
    }
    loadingMessage.style.display = 'none';

    if (xhr.status !== 200) {
        resultMessage.innerHTML = "<strong>Failed to unsubscribe</strong>";
        resultMessage.classList.add('unsubscribeFailMessage');
    }
    resultMessage.style.display = "block";
};