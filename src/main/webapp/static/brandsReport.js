function getBrandReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	return baseUrl + "/api/brand";
}

function getRole() {
   var role = $("meta[name=role]").attr("content")
   return role;
}

function brandCategoryReport(){
	//Set the values to add
//	var $tbody = $('#brandreport-table').find('tbody');
//	$tbody.empty();
	var $tbody = $('#brandCategoryReport-table').find('tbody');
    	$tbody.empty();
    	var $form = $("#brandreport-form");
    	var json = toJson($form);
    	var url = getBrandReportUrl()+"/search";
    	// call api
    	$.ajax({
    		url: url,
    		type: 'POST',
    		data: json,
    		headers: {
    			'Content-Type': 'application/json'
    		},
    		success: function(response) {
    	   		displayBrandList(response);
    	   	},
    	   	error: handleAjaxError
    	   });

    	return false;
}

function displayBrandList(data){
	var $tbody = $('#brandCategoryReport-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var row = '<tr>'
		+ '<td>' + e.id + '</td>'
		+ '<td>' + e.brand + '</td>'
		+ '<td>'  + e.category + '</td>'
		+ '</tr>';
		$tbody.append(row);
	}
}

function getBrandList(){
	var url = getBrandReportUrl();
	// call api
	$.ajax({
		url: url,
		type: 'GET',
		success: function(data) {
	   		// display data
	   		displayBrandList(data);
	   	},
	   	error: handleAjaxError
	   });
}

//INITIALIZATION CODE
function init(){
	$('#show-brandCategoryReport').click(brandCategoryReport);
}
$(document).ready(init);
$(document).ready(getBrandList);