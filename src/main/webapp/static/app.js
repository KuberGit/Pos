function getRole() {
    var role = $("meta[name=role]").attr("content")
    return role;
}

function handleAjaxError(response) {
    var response = JSON.parse(response.responseText);
    $.notify(response.message, 'error');
}

//HELPER METHOD
function toJson($form) {
    var serialized = $form.serializeArray();
    var s = '';
    var data = {};
    for (s in serialized) {
        data[serialized[s]['name']] = serialized[s]['value']
    }

    var json = JSON.stringify(data);
    return json;
}

function readFileData(file, callback) {
    var config = {
        header: true,
        delimiter: "\t",
        skipEmptyLines: "greedy",
        complete: function(results) {
            callback(results);
        }
    }
    Papa.parse(file, config);
}

function writeFileData(data) {
    var blob = new Blob([data], {
        type: 'text/plain'
    });
    var fileUrl = null;
    if (navigator.msSaveBlob) {
        fileUrl = navigator.msSaveBlob(blob, 'error.txt');
    } else {
        fileUrl = window.URL.createObjectURL(blob);
    }
    var tempLink = document.createElement('a');
    tempLink.href = fileUrl;
    tempLink.setAttribute('download', 'error.txt');
    tempLink.click();
}

function convertTimeStampToDateTime(timestamp) {
    var date = new Date(timestamp);
    return (
        date.getDate() +
        "/" +
        (date.getMonth() + 1) +
        "/" +
        date.getFullYear() +
        " " +
        date.getHours() +
        ":" +
        date.getMinutes() +
        ":" +
        date.getSeconds()
    );
}

function calculateTotalPrice(orderItems) {
    let totalPrice = 0;
    for (let i = 0; i < orderItems.length; i++) {
        const orderItem = orderItems[i];
        const quantity = orderItem.quantity;
        const sellingPrice = orderItem.sellingPrice;
        totalPrice = totalPrice + quantity * sellingPrice;
    }
    return totalPrice.toFixed(2);
}

function extractNameAndCategory(brandCategory) {
    var index = brandCategory.indexOf('~');
    var name = brandCategory.substr(0, index);
    var category = brandCategory.substr(index + 1);
    return {
        "brandName": name,
        "brandCategory": category
    };
}