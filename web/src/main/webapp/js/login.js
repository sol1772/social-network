const re = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
const error = document.getElementById("error");
const email = document.getElementById("email");
const okBtn = document.getElementById("button_ok");

function validateEmail(input) {
    if (re.test(input)) {
        return true;
    } else {
        email.style.border = '0.15rem solid red';
        error.innerText = "Invalid email address format";
        return false;
    }
}

okBtn.addEventListener("click", function (e) {
    if (!validateEmail(email.value)) {
        e.preventDefault();
    }
})
