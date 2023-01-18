function getSalesReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	return baseUrl + "/api/sales-report";
}

function salesReport(){
    let startdate = $('#inputStartDate').val().trim();
	let enddate = $('#inputEndDate').val().trim();


	// validate dates
	if(startdate=="" && enddate!=""){
		$.notify("Enter both dates or none !!","error");
		return false;
	}

	if(startdate!="" && enddate==""){
		$.notify("Enter both dates or none !!","error");
		return false;
	}

	console.log(startdate);
    console.log(enddate);

	var $form = $("#salesreport-form");
	// form to json
	let json = toJson($form);

	var url = getSalesReportUrl();
	// call api
	$.ajax({
		url: url,
		type: 'POST',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function(response) {
			// display report
            displaySalesList(response);
		},
		error: function(response){
			handleAjaxError(response);
	   	}
	   });

	return false;
}

function displaySalesList(data){
	var $tbody = $('#salesReport-table').find('tbody');
	$tbody.empty();
	var n = 1;
	for(var i in data){
		var e = data[i];
		var row = '<tr>'
		+ '<td>' + n + '</td>'
		+ '<td>' + e.brand + '</td>'
		+ '<td>' + e.category + '</td>'
		+ '<td>'  + e.quantity + '</td>'
		+ '<td>'  + e.revenue + '</td>'
		+ '</tr>';
		$tbody.append(row);
		n++;
	}
}

function init(){
	$('#show-salesreport').click(salesReport);
}
$(document).ready(init);
