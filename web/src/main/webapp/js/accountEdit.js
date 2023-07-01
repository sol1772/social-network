const re = /^[\d+][\d() -]{4,14}\d$/;
const personalPhone = document.getElementById("personalPhone");
const workPhone = document.getElementById("workPhone");
const testPersonalPhone = document.getElementById("testPersonalPhone");
const testWorkPhone = document.getElementById("testWorkPhone");
const delPersonalPhone = document.getElementById("delPersonalPhone");
const delWorkPhone = document.getElementById("delWorkPhone");
const SaveBtn = document.getElementById("Save");

function validatePhone(phoneNum, id) {
    if (re.test(phoneNum)) {
        document.getElementById(id).style.border = '2px ridge green';
        return true;
    } else {
        const el = document.getElementById(id);
        el.style.border = '2px ridge red';
        el.value = "";
        alert("Invalid phone number format");
        return false;
    }
}

testPersonalPhone.addEventListener("click", function () {
    validatePhone(personalPhone.value, "personalPhone")
});

testWorkPhone.addEventListener("click", function () {
    validatePhone(workPhone.value, "workPhone")
});

delPersonalPhone.addEventListener("click", function () {
    personalPhone.value = "";
});

delWorkPhone.addEventListener("click", function () {
    workPhone.value = "";
});

SaveBtn.addEventListener("click", function (e) {
    if (!confirm("Save changes?")
        || !validatePhone(personalPhone.value, "personalPhone")
        || !validatePhone(workPhone.value, "workPhone")) {
        e.preventDefault();
    }
})