const delMsgBtn = document.querySelectorAll("button[id=delMsg]");
const msgId = document.getElementById("msgId");

Array.from(delMsgBtn).forEach(function (e) {
    e.addEventListener('click', function (ev) {
        if (!confirm("Delete?")) {
            ev.preventDefault();
            return false;
        }
        msgId.value = this.closest("td").getElementsByTagName("input")[0].value;
    });
});
