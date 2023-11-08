const baseUrl = window.location.pathname;
const accPath = baseUrl + "/accounts?";
const grpPath = baseUrl + "/groups?";
const queryString = window.location.search;
let urlParams = new URLSearchParams(queryString);
const contextPath = document.getElementById("contextPathHolder").getAttribute("data-contextPath");
const elError = document.getElementById("error");
const accountsTotal = document.getElementById("accountsTotal").value;
const groupsTotal = document.getElementById("groupsTotal").value;
const elPageA = document.getElementById("pageA");
const elPageG = document.getElementById("pageG");
const lastPageA = document.getElementById("lastPageA").value;
const lastPageG = document.getElementById("lastPageG").value;
const elCurPage = document.getElementById("currentPage");
let accountData = [];
let groupData = [];
const q = urlParams.get("q");
let curPage = urlParams.get("page");

async function renderTable() {
    await getAccounts()
    await getGroups()
    elCurPage.value = curPage.toString();
    let elA = "";
    accountData.forEach(account => {
        elA += "<tr>";
        elA += `<td><a href="${contextPath}/account/${account.id}">${account.id}</a></td>`;
        elA += `<td><a href="${contextPath}/account/${account.id}">${account.lastName} ${account.firstName}</a></td>`;
        elA += `<td><a href="${contextPath}/account/${account.id}">${account.email}</a></td>`;
        elA += "<tr>";
    });
    document.getElementById("accountData").innerHTML = elA;
    let elG = "";
    groupData.forEach(group => {
        elG += "<tr>";
        elG += `<td><a href="${contextPath}/group/${group.id}">${group.id}</a></td>`;
        elG += `<td><a href="${contextPath}/group/${group.id}">${group.title}</a></td>`;
        elG += `<td><a href="${contextPath}/group/${group.id}">${group.metaTitle}</a></td>`;
        elG += "<tr>";
    });
    document.getElementById("groupData").innerHTML = elG;
    document.getElementById("prevPageA").disabled = curPage === 1;
    document.getElementById("prevPageG").disabled = curPage === 1;
    document.getElementById("nextPageA").disabled = curPage === Number(`${lastPageA}`);
    document.getElementById("nextPageG").disabled = curPage === Number(`${lastPageG}`);
    if (Number(accountsTotal) === 0) {
        document.getElementById("tblAccCaption").innerText = `Accounts not found by substring "${q}"`;
    } else if (Number(curPage) <= Number(lastPageA)) {
        document.getElementById("tblAccCaption").innerText =
            `Accounts found by substring "${q}": ${accountsTotal}, page ${curPage} of ${lastPageA}`;
    } else {
        document.getElementById("tblGrpCaption").innerText =
            `Accounts found by substring "${q}": ${accountsTotal}, page ${curPage} exceeds number of pages ${lastPageA}`;
    }
    if (Number(groupsTotal) === 0) {
        document.getElementById("tblGrpCaption").innerText = `Groups not found by substring "${q}"`;
    } else if (Number(curPage) <= Number(lastPageG)) {
        document.getElementById("tblGrpCaption").innerText =
            `Groups found by substring "${q}": ${groupsTotal}, page ${curPage} of ${lastPageG}`;
    } else {
        document.getElementById("tblGrpCaption").innerText =
            `Groups found by substring "${q}": ${groupsTotal}, page ${curPage} exceeds number of pages ${lastPageG}`;
    }
}

renderTable().catch(error => {
    elError.innerHTML = `Error: ${error}`;
    console.error('There was an error!', error);
});

async function getAccounts() {
    urlParams.set("page", curPage);
    elPageA.value = curPage;
    let response = await fetch(accPath + urlParams);
    if (response.status === 200) {
        let data = await response.json();
        accountData = data;
        // console.log(data);
        return data;
    } else {
        throw new Error(response.status.toString());
    }
}

async function getGroups() {
    urlParams.set("page", curPage);
    elPageG.value = curPage;
    let response = await fetch(grpPath + urlParams);
    if (response.status === 200) {
        let data = await response.json();
        groupData = data;
        // console.log(data);
        return data;
    } else {
        throw new Error(response.status.toString());
    }
}

async function pageA(el) {
    curPage = Number(el.value);
    if (curPage <= Number(`${lastPageA}`)) {
        elCurPage.value = curPage;
        await renderTable();
    }
}

async function pageG(el) {
    curPage = Number(el.value);
    if (Number(curPage) <= Number(`${lastPageG}`)) {
        elCurPage.value = curPage;
        await renderTable();
    }
}

async function prevPage() {
    if (curPage > 1) {
        curPage--;
        await renderTable();
    }
}

async function nextPageA() {
    if (curPage <= Number(`${lastPageA}`)) {
        curPage++;
        await renderTable();
    }
}

async function nextPageG() {
    if (curPage <= Number(`${lastPageG}`)) {
        curPage++;
        await renderTable();
    }
}