// get url
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/product";
}

$('#nav-products').addClass('active');

//BUTTON ACTIONS
function searchProduct(event){
	//Set the values to add
	var $tbody = $('#product-table').find('tbody');
	$tbody.empty();
	var $form = $("#product-form");
	var json = toJson($form);
	var url = getProductUrl()+"/search";
	// call api
	$.ajax({
		url: url,
		type: 'POST',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function(response) {
	   		displayProductList(response);
	   	},
	   	error: handleAjaxError
	   });

	return false;
}

//BUTTON ACTIONS
function addProduct(event){
	//Set the values to add

	var $form = $("#product-add-form");
	var json = toJson($form);
	var url = getProductUrl();
	// call api
	$.ajax({
		url: url,
		type: 'POST',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function(response) {
		    $('#inputBrand').val("");
		    $('#inputCategory').val("");
		    $('#inputName').val("");
		    $('#inputBarcode').val("");
			$('#add-product-modal').modal('toggle');
			$('#product-add-form').trigger("reset");
	   		$.notify("Product added successfully !!","success");
	   		searchProduct();
	   		document.getElementById("download-errors-product").disabled = true;
	   	},
	   	error: handleAjaxError
	   });

	return false;
}

function updateProduct(event){

	//Get the ID
	var id = $("#product-edit-form input[name=id]").val();
	var url = getProductUrl() + "/" + id;

	//Set the values to update
	var $form = $("#product-edit-form");
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
			$('#edit-product-modal').modal('toggle');
	   		$.notify("Brand updated successfully !!","success");
	   		searchProduct();
	   	},
	   	error: handleAjaxError
	   });

	return false;
}


function getProductList(){
	var url = getProductUrl();
	// call api
	$.ajax({
		url: url,
		type: 'GET',
		success: function(data) {
	   		// display data
	   		displayProductList(data);
	   	},
	   	error: handleAjaxError
	   });
}

document.getElementById("download-errors-product").disabled = true;

function downloadErrorsProduct(){
	writeFileData(errorData);
    document.getElementById("download-errors-product").disabled = true;
}

function getRole() {
   var role = $("meta[name=role]").attr("content")
   return role;
}

////UI DISPLAY METHODS
//
function displayProductList(data){
	var $tbody = $('#product-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		// dynamic buttons
		var buttonHtml = '';
        if(getRole() === "supervisor" ){
            buttonHtml = ' <button title="Edit" class="btn btn-outline-success" onclick="displayEditProduct(' + e.id + ')"><i class="fa fa-edit fa-lg" aria-hidden="true"></i></button>';
        }
		var row = '<tr>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + e.brand + '</td>'
		+ '<td>'  + e.category + '</td>'
		+ '<td>'  + e.name + '</td>'
		+ '<td>'  + e.mrp + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
		$tbody.append(row);
	}
}

function displayEditProduct(id){
	var url = getProductUrl() + "/" + id;
	// call api
	$.ajax({
		url: url,
		type: 'GET',
		success: function(data) {
	   		// display product for update
	   		displayProduct(data);
	   	},
	   	error: handleAjaxError
	   });
}

function resetUploadDialogProduct(){
	//Reset file name
	document.getElementById("download-errors-product").disabled = true;
	var $file = $('#productFile');
	$file.val('');
	$('#productFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Update counts
	updateUploadDialogProduct();

}

function updateFileNameProduct(){
	var $file = $('#productFile');
	var fileName = $file.val();
	$('#productFileName').html(fileName);
}

function displayUploadDataProduct(){
	resetUploadDialogProduct();
	$('#upload-product-modal').modal('toggle');
}

function displayProduct(data){
	// fill entries
	$("#product-edit-form input[name=barcode]").val(data.barcode);
	$("#product-edit-form input[name=brand]").val(data.brand);
	$("#brand-edit-form input[name=category]").val(data.category);
	$("#product-edit-form input[name=productName]").val(data.productName);
	$("#product-edit-form input[name=mrp]").val(data.mrp);
	$("#product-edit-form input[name=id]").val(data.id);
	$('#edit-product-modal').modal('toggle');
}

function showAddProductModal(){
	$('#add-product-modal').modal('toggle');
	$('#product-add-form').trigger("reset");
	console.log($("#inputBrand").val());
	$("#product-add-form input[name=brand]").val($("#inputBrand").val());
    $("#product-add-form input[name=category]").val($("#inputCategory").val());
    $("#product-add-form input[name=name]").val($("#inputName").val());
}
//INITIALIZATION CODE
function init(){
	$('#show-add-product-modal').click(showAddProductModal);
	$('#search-product').click(searchProduct);
	$('#upload-product-data').click(displayUploadData);
	$('#download-errors-product').click(downloadErrors);
	$('#productFile').on('change', updateFileNameProduct);
	$('#add-product').click(addProduct);
	$('#process-data-product').click(processData);
}

$(document).ready(init);
$(document).ready(getProductList);

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
   var file = $('#productFile')[0].files[0];
   console.log(file);
//   readFileData(file, readFileDataCallback);
   url = "/pos/upload/file"
   var data = new FormData();
   data.append("temp", file);
   data.append("type", "product");

   $.ajax({
      url: url,
      type: 'POST',
      data: data,
      contentType: false,
      processData: false,
      success: function(res) {
         $("#rowCountProduct").text(res.totalCount);
         $("#processCountProduct").text(res.successCount);
         $("#errorCountProduct").text(res.errorCount);
         if(res.errorCount != 0) document.getElementById("download-errors-product").disabled = false;
         $('.notifyjs-wrapper').trigger('notify-hide');
         $.notify("Successfully uploaded all valid products!", 'success');
      },
      error: function(res) {
         console.log("error: "+ res.responseText);
      }
   })
}

function readFileDataCallback(results){
   fileData = results.data;
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
   var url = getProductUrl();

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
            console.log(data);
            writeFileData(data);
       },
       error: handleAjaxError
    });
}

function resetUploadDialog(){
   //Reset file name
   var $file = $('#productFile');
   $file.val('');
   $('#productFileName').html("Choose File");
   //Reset various counts
   processCount = 0;
   fileData = [];
   errorData = [];
   //Update counts
   updateUploadDialog();
}

function updateUploadDialog(){
   $('#rowCount').html("" + fileData.length);
   $('#processCount').html("" + processCount);
   $('#errorCount').html("" + errorData.length);
}

function updateFileName(){
   var $file = $('#productFile');
   var fileName = $file.val();
   $('#productFileName').html(fileName);
}

function displayUploadData(){
   resetUploadDialog();
   $('#upload-product-modal').modal('toggle');
}

