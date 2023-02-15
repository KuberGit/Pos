function getDaySalesReportUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content");
    return baseUrl + "/api/daySales-report";
}

function getRole() {
    var role = $("meta[name=role]").attr("content")
    return role;
}

function getDaySalesList() {
    var url = getDaySalesReportUrl();
    // call api
    $.ajax({
        url: url,
        type: 'GET',
        success: function(data) {
            // display data
            displayDaySalesList(data);
        },
        error: handleAjaxError
    });
}

function displayDaySalesList(data) {
    var $tbody = $('#daySalesReport-table').find('tbody');
    var n = 1;
    $tbody.empty();
    for (var i in data) {
        var e = data[i];
        var row = '<tr>' +
            '<td>' + n + '</td>' +
            '<td>' + convertTimeStampToDateTime(e.date) + '</td>' +
            '<td>' + e.orders + '</td>' +
            '<td>' + e.items + '</td>' +
            '<td>' + e.revenue.toFixed(2) + '</td>' +
            '</tr>';
        $tbody.append(row);
        n++;
    }
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

$(document).ready(getDaySalesList);