const re = /^[\d+][\d() -]{4,14}\d$/;
const phone = document.getElementById("newPhone");
const phoneType = document.getElementById("newPhoneType");
const phoneTbl = document.getElementById("phoneTable");
const addPhoneBtn = document.getElementById("addPhone");
const saveBtn = document.getElementById("saveBtn");
const closeBtn = document.getElementById("close");
const error = document.getElementById("error");

function validateFields() {
    let validated = true;
    Array.from(document.querySelectorAll('[required]')).forEach(function (e) {
        if (Object.is(e.value, "")) {
            e.style.border = '0.15rem ridge red';
            validated = false;
        } else {
            e.style.border = '';
        }
    });
    if (validated) {
        return true;
    } else {
        error.innerText = "Required fields not filled";
        return false;
    }
}

function validatePhone(phoneNum) {
    for (let i = 0; i < phoneTbl.rows.length; i++) {
        if (Object.is(phoneTbl.rows[i].cells[1].getElementsByTagName('input')[0].value, phoneNum)) {
            phone.style.border = '3px ridge red';
            error.innerHTML = "Phone number already exists";
            return false;
        }
    }
    if (re.test(phoneNum)) {
        phone.style.border = '3px ridge green';
        error.innerHTML = "";
        return true;
    } else {
        phone.style.border = '3px ridge red';
        error.innerHTML = "Invalid phone number format";
        return false;
    }
}

function addTableRow(phoneNum, phoneType) {
    let newRow, newCell1, newCell2, newCell3, newCell4, newCell5;
    if (phoneTbl.rows.length < 3) {
        newRow = phoneTbl.insertRow();
        newCell1 = newRow.insertCell();
        newCell1.innerHTML = "<td id='colPhoneType'></td>"
        newCell1.textContent = phoneType + " phone";
        newCell1.style.fontSize = "small";
        newCell1.style.borderRight = "solid 1px lightgrey";
        newCell1.style.width = "7rem";

        newCell2 = newRow.insertCell();
        newCell2.innerHTML = "<td id='colPhoneNum'>" +
            "<input type='tel' readonly name='phoneNum' id='phoneNum' value='" + phoneNum + "'></td>";
        newCell2.style.fontSize = "small";
        newCell2.style.width = "15rem";

        newCell3 = newRow.insertCell();
        newCell3.innerHTML = "<td><input type='hidden' name='phoneId' id='phoneId' value='0'></td>";

        newCell4 = newRow.insertCell();
        newCell4.innerHTML = "<td><input type='hidden' name='phoneType' id='phoneType' value='" + phoneType + "'></td>";

        newCell5 = newRow.insertCell();
        newCell5.innerHTML = "<td><button class='btn btn-sm btn-outline-info' type='button' id='delPhone'>x</button></td>";
        newCell5.addEventListener('click', function () {
            const a = this.closest('tr');
            a.parentElement.removeChild(a);
        });
    } else {
        phone.style.border = '3px ridge red';
        error.innerHTML = "Max phone numbers = 3";
    }
}

addPhoneBtn.addEventListener("click", function () {
    if (validatePhone(phone.value)) {
        addTableRow(phone.value, phoneType.value);
    }
});

Array.from(document.querySelectorAll("button[id=delPhone]")).forEach(function (e) {
    e.addEventListener('click', function () {
        const a = this.closest('tr');
        a.parentElement.removeChild(a);
    });
});

saveBtn.addEventListener("click", function (e) {
    if (!validateFields()) {
        e.preventDefault();
        closeBtn.click();
    }
})
