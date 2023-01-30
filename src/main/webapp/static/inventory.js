// get url
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

function getRole() {
   var role = $("meta[name=role]").attr("content")
   return role;
}


//BUTTON ACTIONS


function searchInventory(event){
	//Set the values to add
	var $tbody = $('#inventory-table').find('tbody');
	$tbody.empty();
	var $form = $("#inventory-form");
	var name=$('#inputName').val().trim();
	var barcode=$('#inputBarcode').val().trim();
	var json = toJson($form);
	var url = getInventoryUrl()+"/search";
	// call api
	$.ajax({
		url: url,
		type: 'POST',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function(response) {
	   		displayInventoryList(response);
	   	},
	   	error: handleAjaxError
	   });

	return false;
}



function addInventory(event){
	//Set the values to add

	var quantity=$('#inventory-add-form input[name=quantity]').val();
	if(quantity<0){
		$.notify("Quantity can not be negative !!","error");
		return false;
	}
	var $form = $("#inventory-add-form");
	var json = toJson($form);
	var url = getInventoryUrl();
	// call api
	$.ajax({
		url: url,
		type: 'POST',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function(response) {
		    $('#inputName').val("");
		    $('#inputBarcode').val("");
			$('#add-inventory-modal').modal('toggle');
	   		$.notify("Inventory added successfully !!","success");
	   		searchInventory();
	   		document.getElementById("download-errors-inventory").disabled = true;
	   	},
	   	error: handleAjaxError
	   });

	return false;
}

function updateInventory(event){

	//Get the ID
	var id = $("#inventory-edit-form input[name=id]").val();
	var url = getInventoryUrl() + "/" + id;
    var quantity=$('#inventory-edit-form input[name=quantity]').val();
	if(quantity<0){
		$.notify("Quantity can not be negative !!","error");
		return false;
	}
	//Set the values to update
	var $form = $("#inventory-edit-form");
	var json = toJson($form);
	// call api
	$.ajax({
		url: url,
		type: 'PUT',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function(response) {
			$('#edit-inventory-modal').modal('toggle');
			$.notify("Inventory updated successfully !!","success");
	   		searchInventory();
	   },
	   error: handleAjaxError
	});

	return false;
}


function getInventoryList(){
	var url = getInventoryUrl();
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

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;

document.getElementById("download-errors-inventory").disabled = true;

function processData(){
	var file = $('#inventoryFile')[0].files[0];
	if (!file) {
	    $('.notifyjs-wrapper').trigger('notify-hide');
        $.notify('No file selected', 'error');
        return;
    }
	// readFileData(file, readFileDataCallback);
	url = "/pos/upload/file"
	var data = new FormData();
	data.append("temp", file);
	data.append("type", "inventory");

	$.ajax({
		url: url,
		type: 'POST',
		data: data,
		contentType: false,
		processData: false,
		success: function(res) {
		console.log(res.totalCount);
			$("#rowCountInventory").text(res.totalCount);
            $("#processCountInventory").text(res.successCount);
            $("#errorCountInventory").text(res.errorCount);
            if (res.errorCount > 0) {
                document.getElementById("download-errors-inventory").disabled = false;
            }
            $('.notifyjs-wrapper').trigger('notify-hide');
            $.notify("Successfully updated inventory items!", 'success');
            searchInventory();
		},
		error: function(res) {
			console.log("error: "+ res.responseText);
			$('.notifyjs-wrapper').trigger('notify-hide');
			$.notify(res.responseJSON.message, 'error');
		}
	})
}

function readFileDataCallbackInventory(results){
	fileData = results.data;
	// check no of rows
	if(fileData.length > 5000)
	{
		$.notify("File Contains more than 5000 rows !!","error");
		return;
	}
	uploadRows();
}

function uploadRows(){
	//Update progress
	updateUploadDialog();
	//If everything processed then return
	if(processCount==fileData.length){
		return;
	}

	//Process next row
	var row = fileData[processCount];
	processCount++;

	var json = JSON.stringify(row);
	var url = getInventoryUrl();

	//Make ajax call
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		uploadRows();
	   },
	   error: function(response){
	   		row.error=response.responseText
	   		errorData.push(row);
	   		uploadRows();
	   }
	});

}

function downloadErrors(){
	var url = "/pos/download/error";
    $.ajax({
       url: url,
       type: 'GET',
       success: function(data) {
            writeFileData(data);
       },
       error: handleAjaxError
    });
}

//UI DISPLAY METHODS

function displayInventoryList(data){
	var $tbody = $('#inventory-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var buttonHtml = '';
        if(getRole() === "supervisor" ){
            buttonHtml = ' <button title="Edit" class="btn btn-outline-success" onclick="displayEditInventory(' + e.id + ')"><i class="fa fa-edit fa-lg" aria-hidden="true"></i></button>';
        }
		var row = '<tr>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + e.name + '</td>'
		+ '<td>' + e.quantity + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
		$tbody.append(row);
	}
}

function displayEditInventory(id){
	var url = getInventoryUrl() + "/" + id;
	// call api
	$.ajax({
		url: url,
		type: 'GET',
		success: function(data) {
			// display data
			displayInventory(data);
		},
		error: handleAjaxError
	});
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#inventoryFile');
	$file.val('');
	$('#inventoryFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Update counts
	updateUploadDialog();
}

// update data
function updateUploadDialog(){
	$('#rowCountInventory').html("" + fileData.length);
	$('#processCountInventory').html("" + processCount);
	$('#errorCountInventory').html("" + errorData.length);
	if(errorData.length != 0) document.getElementById("download-errors-inventory").disabled = false;
}

function updateFileName(){
	var $file = $('#inventoryFile');
	var fileName = $file.val();
	$('#inventoryFileName').html(fileName);
}

function displayUploadData(){
	resetUploadDialog();
	$('#upload-inventory-modal').modal('toggle');
}

// fill entries
function displayInventory(data){
	$("#inventory-edit-form input[name=barcode]").val(data.barcode);
	$("#inventory-edit-form input[name=quantity]").val(data.quantity);
	$("#inventory-edit-form input[name=id]").val(data.id);
	$('#edit-inventory-modal').modal('toggle');
}

function showAddInventoryModal(){
	$('#add-inventory-modal').modal('toggle');
	$("#inventory-add-form input[name=barcode]").val($("#inputBarcode").val());
}
//INITIALIZATION CODE
function init(){

    $('#nav-inventory').addClass('active');
	$('#show-add-inventory-modal').click(showAddInventoryModal);
	$('#search-inventory').click(searchInventory);
	$('#upload-inventory-data').click(displayUploadData);
	$('#process-data-inventory').click(processData);
	$('#download-errors-inventory').click(downloadErrors);
	$('#inventoryFile').on('change', updateFileName);
}

$(document).ready(init);
$(document).ready(getInventoryList);
