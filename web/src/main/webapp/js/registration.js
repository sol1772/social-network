const error = document.getElementById("error");
const regBtn = document.getElementById("register");

regBtn.addEventListener('click', validateFields, false)

function validateFields(e) {
    let validated = true;
    Array.from(document.querySelectorAll('[id=violation]')).forEach(function (e) {
        e.innerText = '';
    });
    Array.from(document.querySelectorAll('[required]')).forEach(function (e) {
        if (Object.is(e.value, "")) {
            e.style.border = '0.15rem ridge red';
            validated = false;
        } else {
            e.style.border = '';
        }
    });
    if (!validated) {
        error.innerText = "Required fields not filled";
        e.preventDefault();
    }
}

