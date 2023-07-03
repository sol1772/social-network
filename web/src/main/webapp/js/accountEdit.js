const re = /^[\d+][\d() -]{4,14}\d$/;
const phone = document.getElementById("newPhone");
const phoneType = document.getElementById("newPhoneType");
const addPhoneBtn = document.getElementById("addPhone");
const saveBtn = document.getElementById("save");
const phoneTbl = document.getElementById("phoneTable");
const error = document.getElementById("error");

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
    let newRow, newCell1, newCell2, newCell3, newCell4;
    if (phoneTbl.rows.length < 3) {
        newRow = phoneTbl.insertRow();
        newCell1 = newRow.insertCell();
        newCell1.textContent = phoneType + " phone";
        newCell1.style.width = "105px";

        newCell2 = newRow.insertCell();
        newCell2.innerHTML = "<td><input type='tel' readonly name='phoneNum' id='phoneNum' value='" + phoneNum + "'></td>";
        newCell2.style.width = "280px";

        newCell3 = newRow.insertCell();
        newCell3.innerHTML = "<td><input type='button' id='delPhone' value='x'></td>";
        newCell3.addEventListener('click', function () {
            const a = this.closest('tr');
            a.parentElement.removeChild(a);
        });

        newCell4 = newRow.insertCell();
        newCell4.innerHTML = "<td><input type='hidden' name='phoneType' id='phoneType' value='" + phoneType + "'></td>";
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

Array.from(document.querySelectorAll("input[type='button']")).forEach(function (e) {
    e.addEventListener('click', function () {
        const a = this.closest('tr');
        a.parentElement.removeChild(a);
    });
});

saveBtn.addEventListener("click", function (e) {
    if (!confirm("Save changes?")) {
        e.preventDefault();
    }
})
