const username  = document.getElementById('username');
const email     = document.getElementById('email');
const password  = document.getElementById('password');
const confirmPassword = document.getElementById('confirmPassword');
const birthName = document.getElementById('name');
const surname   = document.getElementById('surname');
const form      = document.getElementById('form');
const birthday  = document.getElementById('birthday');
const city      = document.getElementById('city');
const country   = document.getElementById('country');
const address   = document.getElementById('address');
const gender    = document.getElementsByName('gender');
const job       = document.getElementById('job');
const phone     = document.getElementById('phone-number');
const petStatus = document.getElementById('pet');
const login     = document.getElementById('loginForm');
const addressStatus = document.getElementById('addressStatus');
const loginUsername = document.getElementById('loginUsername');
const loginPassword = document.getElementById('loginPassword');
const personalPage = document.getElementById('personalpage');
const errorM    = document.getElementById('errorMsg');
const loginErrorM = document.getElementById('loginErrorMsg');
const submit    = document.getElementById('submit');
const loginSubmit = document.getElementById('submitLogin');
const credentials = document.getElementById('displayLoginCredentials');
const loginLabel = document.getElementById('loginCredentials');
const logout = document.getElementById('logout');
const pet = document.getElementById('addPetForm');
const petId = document.getElementById('petId');
const petOwnerId = document.getElementById('ownerId');
const petName = document.getElementById("petName");
const petType = document.getElementById("petType");
const petBreed = document.getElementById("petBreed");
const petGender = document.getElementsByName('petGender');
const petBirthday = document.getElementById("petBirthday");
const petWeight = document.getElementById("petWeight");
const petDescription = document.getElementById("petDescritpion");
const petPhoto = document.getElementById("petPhoto");
const petMsg = document.getElementById("addPetErrorMsg");
const petButton = document.getElementById("submitAddPet");

let flag       = true;
let JQueryFlag = true;
let petOwnerType    = '';

var format = /[ `!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]/;


const submitFunction = () => {
   if (
    !validateUsername(username)          ||
    !validateEmail(email)                ||
    !validatePassword(password)          ||
    !validateName(birthName, 'name')     ||
    !validateName(surname, 'surname')    ||
    !validateBirthdate(birthday)         ||
    !validateName(city, 'city')          ||
    !validateAddress(address, 'address') ||
    !validateName(job, 'job')            ||
    !validatePhone(phone))
  {
    return;
  } else {
    sendAJAXrequest();
  }
};

const sumbitLoginFunction = () => {
    if (
        !validateUsername(loginUsername)     ||
        !validateLoginPassword(loginPassword))
    {
        return;
    } else {
        loginPOSTrequest();
    }

    return false;
};

const filterString3 = () => {

    return {
                pet_id: petId.value,
                owner_id: petOwnerId.value,
                name: petName.value,
                type: petType.value,
                breed: petBreed.value,
                gender: document.querySelector('input[name="petGender"]:checked').value,
                birthyear: petBirthday.value,
                weight: petWeight.value,
                description: petDescription.value,
                photo: petPhoto.value
            };
};

const submitPet = () => {

    validatePetOwnerId(petOwnerId)
    .then(result => {
        if (!(result && validatePetId(petId))) {
            return;
        } else {
            const petUser = filterString3();
            addPetAJAXrequest(petUser);
        }
    })
    .catch(error => {
        console.error("Error during validation:", error);

    });
};

const addPetAJAXrequest = (petJson) => {
    var xhr = new XMLHttpRequest();
        var queryParams;

        xhr.onload = function () {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    const responseData = JSON.parse(xhr.responseText);
                    petMsg.innerText = responseData.str;
                    petMsg.style.color = "green";
                    petButton.style.borderColor = "green";
                    petMsg.style.display = "block";
                    console.log(responseData.res);
                    if (responseData.res === "false") {
                        console.log("goes")
                        document.getElementById("ownerId").style.borderColor = "red";
                        document.getElementById("ownerIdmsg").style.color = "red";
                        document.getElementById("ownerIdmsg").innerText = "owner does not exist";
                        document.getElementById("ownerIdmsg").style.visibility = "visible";
                        petMsg.innerText = "";
                        petButton.style.borderColor = "red";
                    }
                } else {
                    console.log("not good");
                    reject("Error occurred during the request.");
                }
            } else {
                console.log("not good");
                reject("Error occurred during the request.");
            }
        };

        const url = "addPet";
        xhr.open("POST", url);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        queryParams = `user=${encodeURIComponent(JSON.stringify(petJson))}`;
        xhr.send(queryParams);
};

const switchForm = (type) => {
    switch (type) {
        case 'register':
            form.style.display = "block";
            login.style.display = "none";
            pet.style.display = "none"; // Hide the Add Pet form
            document.getElementById("chatGTP").style.display = "none";
            break;

        case 'login':
            form.style.display = "none";
            login.style.display = "block";
            pet.style.display = "none"; // Hide the Add Pet form
            document.getElementById("chatGTP").style.display = "none";
            break;

        case 'addPet':
            form.style.display = "none";
            login.style.display = "none";
            pet.style.display = "block"; // Show the Add Pet form
            document.getElementById("chatGTP").style.display = "none";
            break;

        case 'Search':
            form.style.display = "none";
            login.style.display = "none";
            pet.style.display = "none"; // Show the Add Pet form
            document.getElementById("chatGTP").style.display = "block";
            getAllKeepers();
            break;
    }
};

const validatePetId = (pet) => {
    if (pet.value.length < 10 ) {
        onError(pet, "pet id must be atleast 10 letters");
        return false;
    } else if (!/^\d+$/.test(pet.value)) {
        onError(pet, "pet id must consist of only digits");
        return false;
    } else {
        onSuccess(pet);
        return true;
    }
};

const getAllKeepers = () => {

    var xhr = new XMLHttpRequest();

    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                const responseData = JSON.parse(xhr.responseText);
                const keepers = responseData;
                const table = $('#previewKeepers');

                table.empty();

                const headers = $('<tr>');
                headers.append($('<th>').text('Username'));
                headers.append($('<th>').text('First Name'));
                headers.append($('<th>').text('Last Name'));
                table.append(headers);

                for (let i = 0; i < keepers.size; i++) {
                    const keeperData = JSON.parse(keepers['keeper' + i]);

                    const row = $('<tr>');
                    row.append($('<td>').text(keeperData.username));
                    row.append($('<td>').text(keeperData.firstname));
                    row.append($('<td>').text(keeperData.lastname));

                    table.append(row);
                }

                $('#chatGTP').show();
            } else {
                console.log("not good");
                reject("Error occurred during the request.");
            }
        } else {
            console.log("not good");
            reject("Error occurred during the request.");
        }
    };

    const url = "getUsers";
    xhr.open("GET", url);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.send();
};

const getLink = () => {
    const text = document.getElementById('addLinkArea').value;
    const link = document.createElement('a');
    const paragraph = document.createElement('p');

    link.href = text;
    link.target = "_blank";
    link.textContent = text;

    paragraph.appendChild(link);

    document.getElementById('links').appendChild(paragraph);
}


const clearFooter = () => {
    const footer = document.getElementById('footer');
    footer.innerHTML = '';
};

const validatePetOwnerId = (petOwnerId) => {
    return new Promise((resolve, reject) => {
        var xhr = new XMLHttpRequest();
        var queryParams;

        const user = {
            owner_id: petOwnerId.value
        };

        xhr.onload = function () {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    const responseData = JSON.parse(xhr.responseText);
                    if (responseData.str !== true) {
                        onSuccess(petOwnerId);
                        resolve(true);
                    } else {
                        onError(petOwnerId, "pet id is assigned to an owner");
                        resolve(false);
                    }
                } else {
                    console.log("not good");
                    reject("Error occurred during the request.");
                }
            } else {
                console.log("not good");
                reject("Error occurred during the request.");
            }
        };

        const url = "PetCredentials";
        xhr.open("POST", url);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        queryParams = `user=${encodeURIComponent(JSON.stringify(user))}`;
        xhr.send(queryParams);
    });
};

const filterString2 = (usr, pass) => {

    return {
        username: usr,
        password: pass
    };
};

const logoutPOSTrequest = () => {

    var xhr = new XMLHttpRequest();

    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                const responseData = JSON.parse(xhr.responseText);
                login.reset();
                credentials.innerText = "";
                credentials.style.display = "none";
                loginErrorM.style.color = "green";
                logout.style.display = "none";
                loginSubmit.style.display = "block";
                loginLabel.style.display = "none";
                loginErrorM.innerText = JSON.stringify(responseData.str);
            } else {
                console.log("not good");
            }
        } else {
            console.log("not good");
        }
    };

    const url = "LogoutSession";
    xhr.open("POST", url);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.send();
    return false;
};

const displayUsersTable = (jsonArray, data) => {
    var usersData = jsonArray.map(jsonString => JSON.parse(jsonString));
    console.log(usersData);
    var table = $('<table>').addClass('users-table');

    const displayUser = (userData, table) => {
        var row = $('<tr>');
        row.append($('<td>').text(userData.username));
        row.append($('<td>').text(userData.firstname));
        row.append($('<td>').text(userData.lastname));
        var deleteButton = $('<button>').text('Delete').on('click', function() {
            const el = {
                name: userData.username,
                email: userData.email,
                password: userData.password,
            };
            customAJAX("deleteUser", "POST", el)
            row.remove();
        });
        row.append($('<td>').append(deleteButton));
        table.append(row);

        }

    usersData.forEach(user => {
        displayUser(user, table);
    });

    var texts = ["cats", "dogs", "petkeepers", "petowners"];
    var rows = [$('<tr>'), $('<tr>'), $('<tr>'), $('<tr>'), $('<tr>')];
    for (let i = 0; i < 4; i++) {
        rows[i].append($('<td>').text("number of " + texts[i]));
        rows[i].append($('<td>').text(data['data' + i]));
        table.append(rows[i]);
    };
    rows[4].append($('<td>').text("profits of petkeepers"));
    rows[4].append($('<td>').text(data['data' + 4]));
    console.log(data['data' + 4]);
    table.append(rows[4]);


    $('#displayLoginCredentials').html(table);
};

const customAJAX = (name, request, userData) => {
    var xhr = new XMLHttpRequest();
    var queryParams;

    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                const responseData = JSON.parse(xhr.responseText);
                console.log(responseData.str);
            } else {
                console.log("not good");
            }
        } else {
            console.log("not good");
        }
    };

    const url = name;
    xhr.open(request, url);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    queryParams = `user=${encodeURIComponent(JSON.stringify(userData))}`;
    xhr.send(queryParams);
}

const chatGPTajaxRequest = (text)=> {
    var xhr = new XMLHttpRequest();

    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                const responseData = JSON.parse(xhr.responseText);
                console.log(responseData.str);
            } else {
                console.log("not good");
            }
        } else {
            console.log("not good");
        }
    };

    const url = "ChatGPT";
    xhr.open("POST", url);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    queryParams = `user=${encodeURIComponent(JSON.stringify(text))}`;
    xhr.send(queryParams);
}

const displayUserTable = (jsonData, flag) => {

    var userData = JSON.parse(jsonData);
    var container = $('<div>');
    var table = $('<table>').addClass('user-table');
    var table2 = $('<table>').addClass('user-table');
    var table3 = $('<table>').addClass('user-table');
    var table4 = $('<table>').addClass('user-table');

    displayUser(userData, table);

    var submitButton = $('<button>').text('Submit Changes').on('click', function() {
        var newUserCredentials = {};

        table.find('input').each(function() {
            var inputId = $(this).attr('id');
            var inputValue = $(this).val();
            newUserCredentials[inputId] = inputValue;
        });

        changeAJAXUserCredentials(newUserCredentials);
        displayUser(newUserCredentials, table);
    });

    table.append($('<tr>').append($('<td colspan="2">').append(submitButton)));

    if (flag === true) {
        var ChatButton = $('<button>').text('Pet Care').on('click', function() {
            chatGPTajaxRequest("tell me about dog care");
        });
        getBookingToKeeper(userData.keeper_id, container);
        table.append($('<tr>').append($('<td colspan="2">').append(ChatButton)));
    } else {
        getAvailableKeepersAJAX(table2, userData.username, userData.password);
        bookingAvailability(table3, userData.username, userData.password, userData.owner_id);

    }

    container.append(table);
    container.append(table2);
    container.append(table3);
//    container.append(table4);


    $('#displayLoginCredentials').html(container);
};

const getBookingToKeeper = (id, container) => {
    var xhr = new XMLHttpRequest();
    var queryParams;

    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                const responseData = JSON.parse(xhr.responseText);
                console.log(responseData.str);
                if (responseData.str) {
                    var borrowingData = JSON.parse(responseData.str);

                    var table = document.createElement('table');
                    table.border = '1';

                    var headerRow = table.insertRow();
                    for (var key in borrowingData) {
                        var headerCell = headerRow.insertCell();
                        headerCell.appendChild(document.createTextNode(key));
                    }

                    var dataRow = table.insertRow();
                    for (var key in borrowingData) {
                        var dataCell = dataRow.insertCell();
                        dataCell.appendChild(document.createTextNode(borrowingData[key]));
                    }
                    container.append(table);

                    var rejectButton = document.createElement('button');
                    rejectButton.textContent = 'Reject';

                    rejectButton.addEventListener('click', function() {
                        sendResponse('rejected', id, borrowingData.pet_id);
                    });
                    container.append(rejectButton);

                    var acceptButton = document.createElement('button');
                    acceptButton.textContent = 'Accept';

                    acceptButton.addEventListener('click', function() {
                        sendResponse('accepted', id, borrowingData.pet_id);
                    });
                    container.append(acceptButton);
                }
            } else {
                console.log("not good");
            }
        } else {
            console.log("not good");
        }
    };

    const url = "getBookingToKeeper";
    xhr.open("POST", url);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    queryParams = `user=${encodeURIComponent(JSON.stringify({id: id}))}`;
    xhr.send(queryParams);
}

const bookingAvailability = (table, name, password, id) => {
    var xhr = new XMLHttpRequest();

    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                const responseData = JSON.parse(xhr.responseText);

                var lrow = $('<tr>');
                if (responseData.fasize > 0) lrow.append($('<td>').text("make a booking"));
                table.append(lrow);

                for (let i = 0; i < responseData.fasize; i++) {
                    const keeperData = JSON.parse(responseData['fakeeper' + i]);
                    var fromLabel = $('<label>').text('From: ');
                    var toLabel = $('<label>').text('To: ');
                    const row = $('<tr>');

                    const radioBtn = $('<input>').attr({
                        type: 'radio',
                        name: 'userRadio',
                        value: keeperData.keeper_id,
                    });
                    row.append($('<td>').append(radioBtn));

                    row.append($('<td>').text(keeperData.username));
                    row.append($('<td>').text(keeperData.firstname + ' ' + keeperData.lastname));
                    table.append(row);
                }

                const startDateInput = $('<input>').attr({
                    type: 'date',
                    id: 'startDate',
                    name: 'startDate'
                });

                const endDateInput = $('<input>').attr({
                    type: 'date',
                    id: 'endDate',
                    name: 'endDate'
                });

                const price = $('<input>').attr({
                    type: 'text',
                    id: 'bookingPrice',
                    placeholder: 'enter a price'
                });

                var fromLabelCell = $('<td>').append($('<label>').text('From: '));
                var startDateInputCell = $('<td>').append(startDateInput);
                var fromRow = $('<tr>').append(fromLabelCell, startDateInputCell);
                table.append(fromRow);

                // Create <td> for 'To' label and input
                var toLabelCell = $('<td>').append($('<label>').text('To: '));
                var endDateInputCell = $('<td>').append(endDateInput);
                var toRow = $('<tr>').append(toLabelCell, endDateInputCell);
                table.append(toRow);

                var priceCell = $('<td>').append($('<label>').text('price: '));
                var pc = $('<td>').append(price);
                var Price = $('<tr>').append(priceCell, pc);

                table.append(Price);
                table.append(fromRow);
                table.append(toRow);

                var submitBtn = $('<button>').text('Submit booking').on('click', function () {
                    var selectedUserId = $('input[name=userRadio]:checked').val();
                    console.log('Selected User ID:', selectedUserId);
                    console.log('Start Date:', startDateInput.val());
                    console.log('End Date:', endDateInput.val());
                    console.log('price:', price.val());
                    console.log('Owner_id:', id);
                    getPet(selectedUserId, id, startDateInput.val(), endDateInput.val(), price.val());
                });

                table.append($('<tr>').append($('<td>').append(submitBtn)));
            } else {
                console.log("not good");
            }
        } else {
            console.log("not good");
        }
    };


    xhr.open('POST', 'getAvailableKeepers');
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    queryParams = `user=${encodeURIComponent(JSON.stringify({name: name, password: password}))}`;
    xhr.send(queryParams);
}

const sendResponse = (response, id, pet_id) => {
    var xhr = new XMLHttpRequest();
    var queryParams;

    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                const responseData = JSON.parse(xhr.responseText);
                console.log(responseData.size);
            } else {
                console.log("not good");
            }
        } else {
            console.log("not good");
        }
    };

    const url = "respondToBooking";
    xhr.open("POST", url);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    queryParams = `user=${encodeURIComponent(JSON.stringify({response: response, id: id, pet_id: pet_id}))}`;
    xhr.send(queryParams);
}

const makeBooking = (keeper_id, owner_id, fromDate, toDate, pet_id, price) => {
    var xhr = new XMLHttpRequest();
    var queryParams;

    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                const responseData = JSON.parse(xhr.responseText);
                console.log(responseData.str);
            } else {
                console.log("not good");
            }
        } else {
            console.log("not good");
        }
    };

    const url = "booking";
    xhr.open("POST", url);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    queryParams = `user=${encodeURIComponent(JSON.stringify({owner_id: owner_id, pet_id: pet_id, keeper_id : keeper_id, fromDate: fromDate, toDate: toDate, status: "requested", price: price}))}`;
    xhr.send(queryParams);
}

const getPet = (keeper_id, owner_id, fromDate, toDate, price) => {
    var xhr = new XMLHttpRequest();
    var queryParams;

    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                const responseData = JSON.parse(xhr.responseText);
                makeBooking(keeper_id, owner_id, fromDate, toDate, responseData.str, price);
            } else {
                console.log("not good");
            }
        } else {
            console.log("not good");
        }
    };

    const url = "getPet";
    xhr.open("POST", url);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    queryParams = `user=${encodeURIComponent(JSON.stringify({owner_id: owner_id}))}`;
    xhr.send(queryParams);
}

const getAvailableKeepersAJAX = (table, name, password) => {
    var xhr = new XMLHttpRequest();

    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                const responseData = JSON.parse(xhr.responseText);
                var lrow = $('<tr>');
                if (responseData.size > 0) lrow.append($('<td>').text("you can text these users"));
                table.append(lrow);
                for (let i = 0; i < responseData.size; i++) {

                    const keeperData = JSON.parse(responseData['keeper' + i]);
                    var row = $('<tr>');

                    row.append($('<td>').text(keeperData.username));

                    row.append($('<td>').text(keeperData.username));
                    row.append($('<td>').text(keeperData.firstname + ' ' + keeperData.lastname));
                    var textButton = $('<button>').text('Text').on('click', function() {
                       console.log("text " + keeperData.username);
                    });

                    row.append($('<td>').append(textButton));
                    table.append(row);
                }
                var rrow = $('<tr>');
                if (responseData.fsize > 0) rrow.append($('<td>').text("you can rate these users"));
                table.append(rrow);
                for (let i = 0; i < responseData.fsize; i++) {

                    const keeperData = JSON.parse(responseData['fkeeper' + i]);
                    var row = $('<tr>');
                    row.append($('<td>').text(keeperData.username));

                    row.append($('<td>').text(keeperData.username));
                    row.append($('<td>').text(keeperData.firstname + ' ' + keeperData.lastname));
                    var textButton = $('<button>').text('Rate').on('click', function() {
                        console.log("Rate " + keeperData.username);
                    });

                    row.append($('<td>').append(textButton));
                    table.append(row);
//                    var ibox = $('<input>');
                }
            } else {
                console.log("not good");
            }
        } else {
            console.log("not good");
        }
    };


    xhr.open('POST', 'getAvailableKeepers');
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    queryParams = `user=${encodeURIComponent(JSON.stringify({name: name, password: password}))}`;
    xhr.send(queryParams);
}

const displayUser = (user, table) => {
    $.each(user, function(key, value) {

        if (key !== 'owner_id' && key !== 'keeper_id') {
            var row = $('<tr>');
            row.append($('<td>').text(key));

            var inputElement = $('<input>').attr({
                type: 'text',
                value: value,
                readonly: (key === 'username' || key === 'email') ? true : false,
                id: key
            });

            row.append($('<td>').append(inputElement));
            table.append(row);
        }
    });
};

const changeAJAXUserCredentials = (newUser) => {
    var xhr = new XMLHttpRequest();
    var queryParams;

    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                const responseData = JSON.parse(xhr.responseText);
                displayUserTable(responseData.str);
            } else {
                console.log("not good");
            }
        } else {
            console.log("not good");
        }
    };

    const url = "ChangeUserCredentials";
    xhr.open("POST", url);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    queryParams = `user=${encodeURIComponent(JSON.stringify(newUser))}`;
    xhr.send(queryParams);
};


const loginPOSTrequest = () => {
    var xhr = new XMLHttpRequest();
    var queryParams;

    const user = filterString2(loginUsername.value, loginPassword.value);

    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                const responseData = JSON.parse(xhr.responseText);
                loginErrorM.innerText = "";
                credentials.innerText = JSON.stringify(responseData.str);

                if (responseData.str === "admin") {
                    console.log(responseData.osize)
                    console.log(responseData.ksize)
                    let allUsers = [];
                    for (let i = 0; i < responseData.osize; i++) {
                        allUsers.push(responseData['owner' + i]);
                    }
                    for (let i = 0; i < responseData.ksize; i++) {
                        allUsers.push(responseData['keeper' + i]);
                    }
                    displayUsersTable(allUsers, responseData);
                }
                else if (responseData.str !== "user does not exist") {
                    displayUserTable(responseData.str,responseData.str.includes("keeper_id"));
                } else {
                    credentials.innerText = "user does not exist.";
                }
                credentials.style.display = "block";
                loginLabel.style.display = responseData.buttonDisplay;
                loginSubmit.style.display = responseData.display;
                logout.style.display = responseData.buttonDisplay;
            } else {
                console.log("not good");
            }
        } else {
            console.log("not good");
        }
    };

    const url = "LoginSession";
    xhr.open("POST", url);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    queryParams = `user=${encodeURIComponent(JSON.stringify(user))}`;
    xhr.send(queryParams);
};

const sendAJAXrequest = () => {
    const user = filterString(username.value, email.value, password.value, birthName.value, surname.value, gender, birthday.value, city.value, address.value, job.value, phone.value, personalPage.value, country);

    if (username.value === "") return;

    var xhr = new XMLHttpRequest();
    var queryParams;

    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                const responseData = JSON.parse(xhr.responseText);
                errorM.innerText = responseData.str;
                errorM.style.color = responseData.color;
                submit.style.borderColor = responseData.color;
            } else {
                const responseData = JSON.parse(xhr.responseText);
                errorM.innerText = responseData.str;
            }
        } else {
            const responseData = JSON.parse(xhr.responseText);
            errorM.innerText = responseData.str;
        }
    };

    if (petOwnerType === "keeper") {
        for (let i = 0; i < 3; i++) {
            if (inputArr[i].checked) {
                user.property = labelArr[i].innerText;
            }
        }
        user.propertydescription = inputArr[6].value;
        user.catkeeper = inputArr[3].checked;
        user.dogkeeper = inputArr[4].checked;
        if (inputArr[3].checked) {
            user.catprice = inputArr[5].value;
            user.dogprice = "0";
        } else {
            user.dogprice = inputArr[5].value;
            user.catprice = "0";
        }
    }

    const url = (petOwnerType === "keeper") ? "AddKeeper" : "AddOwner";
    xhr.open("POST", url);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    queryParams = `user=${encodeURIComponent(JSON.stringify(user))}`;
    xhr.send(queryParams);


};

const filterString = (usr, em, pass, bn, sn, g, bd, ct, addr, j, pn, pp, c) => {
    var lat = 0, lon = 1;
    var cntr = c.options[c.selectedIndex].text;
    g = document.querySelector('input[name="gender"]:checked').value;

    return {
        username: usr,
        email: em,
        password: pass,
        firstname: bn,
        lastname: sn,
        birthdate: bd,
        gender: g,
        country: cntr,
        city: ct,
        address: addr,
        lat: lat,
        lon: lon,
        telephone: pn,
        job: j,
        personalpage: pp
    };
};

const verifyAddressCredentials = () => {
  onError(addressStatus, "Map lookup is not configured for this local prototype.");
};

const trArr    = [];
const tdArr    = [];
const labelArr = [];
const inputArr = [];

const checkPetStatus = (status) => {
  const idArr = ['accomodation', 'type', 'price', 'description'];
  petOwnerType = status;

  if (status === 'keeper' && JQueryFlag === true) {

    for (let i = 0; i < 4; i++) {
      trArr[i] = document.createElement("tr");
      trArr[i].id = idArr[i];
    }

    let j = 0;
    for (let i = 0; i < 8; i++) {
      tdArr[i] = document.createElement("td");
      trArr[j].appendChild(tdArr[i]);
      if (i % 2 === 1) j++;
    }

    inputArr[5] = document.createElement("input");
    inputArr[6] = document.createElement("input");
    tdArr[5].appendChild(inputArr[5]);
    tdArr[7].appendChild(inputArr[6]);
    inputArr[6].placeholder = "(optional)";
    inputArr[6].style = "width: 350px;";
    // inputArr[6].style = "height: 30px;"
    for (let i = 0; i < 5; i++) {
      labelArr[i] = document.createElement("label");
      inputArr[i] = document.createElement("input");
      inputArr[i].required = "true";
      inputArr[i].type = "radio";
      if (i > 2) {
        tdArr[3].appendChild(inputArr[i]);
        tdArr[3].appendChild(labelArr[i]);
        inputArr[i].name = "pet-race";
        inputArr[i].id =  (i === 3) ? "cat-keeper" : "dog-keeper";
        inputArr[i].onclick = function() {
          inputArr[5].type = "text";
          inputArr[5].required = "true";
          inputArr[5].placeholder = (inputArr[i].id === "cat-keeper") ?
            "0 - 15€" :
            "0 - 20€";
        };
      } else {
        tdArr[1].appendChild(inputArr[i]);
        tdArr[1].appendChild(labelArr[i]);
        inputArr[i].onclick = function() {
          if (labelArr[i].innerText === "Outdoor") {
            labelArr[3].remove();
            inputArr[3].remove();
            inputArr[5].placeholder = "0 - 20€";
            inputArr[4].checked = "true";
          } else {
            tdArr[3].appendChild(inputArr[3]);
            tdArr[3].appendChild(labelArr[3]);
            labelArr[4].remove();
            inputArr[4].remove();
            tdArr[3].appendChild(inputArr[4]);
            tdArr[3].appendChild(labelArr[4]);
          }
        };
        inputArr[i].name = "pet-type";
      }
    }
    labelArr[0].innerText = "Indoor";
    labelArr[1].innerText = "Outdoor";
    labelArr[2].innerText = "Both";
    inputArr[4].value = "catkeeper";
    inputArr[3].value = "dogkeeper";
    labelArr[3].innerText = "cat keeper";
    labelArr[4].innerText = "dog keeper";

    tdArr[0].innerText = "Pet accomodation:";
    tdArr[2].innerText = "Pet type:";
    tdArr[4].innerText = "Price:";
    tdArr[6].innerText = "Residence description:";

    const parent = petStatus.parentElement;
    parent.insertBefore(trArr[0], petStatus);
    parent.insertBefore(trArr[1], petStatus);
    parent.insertBefore(trArr[2], petStatus);
    parent.insertBefore(trArr[3], petStatus);
    JQueryFlag = false;
  } else {
    if (JQueryFlag === false) {
      idArr.forEach(element => document.getElementById(element).remove());
      JQueryFlag = true;
    }
  }

};

const validateUsername = (username) => {
  if (username.value.length < 5) {
    onError(username, 'username should be more than 5 characters.');
  } else if (!isNaN(username.value[0])) {
    onError(username, 'username should not start with a number.');
  } else {
    onSuccess(username);
    return true;
  }
  return false;
};

const validateEmail = (email) => {
  if (email.value.indexOf('@') === -1 || email.value.indexOf('.') === -1) {
    onError(email, 'email is not valid.');
    return false;
  } else {
    onSuccess(email);
    return true;
  }
};

const validateLoginPassword = (password) => {
    const forbiddenPasswords = ["cat", "dog", "gata", "skulos"];
    var flag = false;

  forbiddenPasswords.forEach(element => {if (element === password.value) {
    flag = true;
  }});

  if (flag === true) {
    onError(password, 'password must not be \'dog\', \'cat\', \'gata\', \'skulos\'');
    return false;
  }

  if ((/[a-z]/i.test(password.value)        && /\d/.test(password.value)        && format.test(password.value))) {
    onSuccess(password);
  }  else {
    onError(password, 'password must contain at least a latin char, a number and a special char.');
  }
  if (password.value.length        > 15 ||        password.value.length < 8) {
    onError(password, 'password must be 8-15 characters.');
    return false;
  }

  return true;
};

const validatePassword = (password) => {
  const forbiddenPasswords = ["cat", "dog", "gata", "skulos"];
  var flag = false;

  forbiddenPasswords.forEach(element => {if (element === password.value) {
    flag = true;
  }});

  if (flag === true) {
    onError(password, 'password must not be \'dog\', \'cat\', \'gata\', \'skulos\'');
    onError(confirmPassword, "");
    return false;
  }

  if (password.value !== confirmPassword.value) {
    onError(confirmPassword, 'passwords must match.');
    onError(password, "");
    return false;
  }

  if ((/[a-z]/i.test(password.value)        && /\d/.test(password.value)        && format.test(password.value)) &&
      (/[a-z]/i.test(confirmPassword.value) && /\d/.test(confirmPassword.value) && format.test(confirmPassword.value))) {
    onSuccess(password);
    onSuccess(confirmPassword);
  }  else {
    onError(confirmPassword, '');
    onError(password, 'password must contain at least a latin char, a number and a special char.');
  }
  if ((password.value.length        > 15 ||        password.value.length < 8) &&
      (confirmPassword.value.length > 15 || confirmPassword.value.length < 8)) {
    onError(password, 'password must be 8-15 characters.');
    onError(confirmPassword, '');
    return false;
  }

  return passwordStrengthValidator(password.value);
};

const validateName = (name, str) => {
  if (name.value.length < 3 || name.value.length > 30) {
    onError(name, str + ' must be 3-30 characters.');
    return false;
  }
  onSuccess(name);
  return true;
};

const validateBirthdate = (birthday) => {
  let arr = birthday.value.split("-");

  if (arr[0] < 1920 || arr[0] > 2007) {
    onError(birthday, 'invalid date.');
    return false;
  }
  onSuccess(birthday);
  return true;
};

const validateAddress = (address, str) => {
  if (address.value.length < 10 || address.value.length > 150) {
    onError(address, str + ' must be 10-150 characters.');
    return false;
  }
  onSuccess(address);
  return true;
};

const validatePhone = (phone) => {
  if (phone.value.length < 10 || phone.value.length > 150 || /[a-z]/i.test(phone.value)) {
    onError(phone, 'phone number must be 10-14 numbers.');
    return false;
  }
  onSuccess(phone);
  return true;
};

const onSuccess = (input) => {
  const parent = input.parentElement;
  const border = parent.querySelector("input");
  const msgElement = parent.querySelector("small");
  msgElement.style.visibility = "hidden";
  msgElement.innerText = "";
  border.style.borderColor = "green";
};

const onError = (input, str) => {
  const parent = input.parentElement;
  const border = parent.querySelector("input");
  const msgElement = parent.querySelector("small");
  msgElement.style.visibility = "visible";
  msgElement.innerText = str;
  border.style.borderColor = "red";
};

const passwordMessage = (str, colour) => {
  const parent = password.parentElement;
  const border = parent.querySelector("input");
  const msgElement = parent.querySelector("small");
  msgElement.style.visibility = "visible";
  msgElement.innerText = str;
  msgElement.style.color = colour;
  border.style.borderColor = colour;
};

const passwordStrengthValidator = (password) => {
  var numberCount = 0;


  [...password].forEach(char => {
    if (/\d/.test(char)) {
      numberCount++;
    }});


  if (numberCount >= password.length / 2) {
    passwordMessage("weak password.", '#bf4040');
    onError(confirmPassword, "");
    return false;
  } else if (( /[A-Z]/i.test(password) &&/[a-z]/i.test(password) && /\d/.test(password) && format.test(password))) {
    passwordMessage("strong password.", "green");
    onSuccess(confirmPassword, "");
    return true;
  } else {
    passwordMessage("medium password.", "#bf8040");
    onSuccess(confirmPassword, "");
    return true;
  }
};

function changeValue(value) {
  flag = value;
}

const passwordFunction = (str) => {
  const input = document.getElementById(str);

  const parent = input.parentElement;
  const password = parent.querySelector("input");

   password.type = password.type === "password" ?
    "text" : "password";

    return false;
};

const loginPasswordFunction = (str) => {
    const password = document.getElementById(str);
    password.type = password.type === "password" ?
    "text" : "password";

    return false;
};
