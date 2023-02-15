function getInventoryReportUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content");
    return baseUrl + "/api/inventory-report";
}

function getRole() {
    var role = $("meta[name=role]").attr("content")
    return role;
}

function getInventoryList() {
    var url = getInventoryReportUrl();
    // call api
    $.ajax({
        url: url,
        type: 'GET',
        success: function(data) {
            // display data
            displayInventoryList(data);
        },
        error: handleAjaxError
    });
}

function displayInventoryList(data) {
    var $tbody = $('#inventoryReport-table').find('tbody');
    var n = 1;
    $tbody.empty();
    for (var i in data) {
        var e = data[i];
        var row = '<tr>' +
            '<td>' + n + '</td>' +
            '<td>' + e.brand + '</td>' +
            '<td>' + e.category + '</td>' +
            '<td>' + e.quantity + '</td>' +
            '</tr>';
        $tbody.append(row);
        n++;
    }
}

function init() {}

$(document).ready(init);
$(document).ready(getInventoryList);