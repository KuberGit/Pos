// get url
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/product";
}

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
	console.log(url);
	// call api
	$.ajax({
		url: url,
		type: 'POST',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function(response) {
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
//
//// FILE UPLOAD METHODS
//var fileData = [];
//var errorData = [];
//var processCount = 0;

//function processDataProduct(){
//	var file = $('#productFile')[0].files[0];
//	readFileData(file, readFileDataCallbackProduct);
//}

//function readFileDataCallbackProduct(results){
//	fileData = results.data;
//	// check no of rows
//	if(fileData.length > 5000)
//	{
//		alert("File Contains more than 5000 rows !!");
//		return;
//	}
//	uploadRowsProduct();
//}
//
//function uploadRowsProduct(){
//	//Update progress
//	updateUploadDialogProduct();
//	//If everything processed then return
//	if(processCount==fileData.length){
//		alert("File processed successfully!!");
//	   	searchProduct();
//		return;
//	}
//
//	//Process next row
//	var row = fileData[processCount];
//	processCount++;
//
//	var json = JSON.stringify(row);
//	var url = getProductUrl();
//
//	//Make ajax call
//	$.ajax({
//		url: url,
//		type: 'POST',
//		data: json,
//		headers: {
//			'Content-Type': 'application/json'
//		},
//		success: function(response) {
//			uploadRowsProduct();
//		},
//		error: function(response){
//			row.error=response.responseText
//			errorData.push(row);
//			uploadRowsProduct();
//		}
//	});
//
//}
//
//var $button = $('#download-errors-brand');
//$button.disabled = true;
document.getElementById("download-errors-product").disabled = true;

function downloadErrorsProduct(){
	writeFileData(errorData);
    document.getElementById("download-errors-product").disabled = true;
}

////UI DISPLAY METHODS
//
function displayProductList(data){
	var $tbody = $('#product-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		// dynamic buttons
		var buttonHtml = ' <button class="btn btn-outline-success" onclick="displayEditProduct(' + e.id + ')"><i class="fa fa-edit fa-lg" aria-hidden="true"></i></button>'
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

//function updateUploadDialogProduct(){
//	// update counts
//	$('#rowCountProduct').html("" + fileData.length);
//	$('#processCountProduct').html("" + processCount);
//	$('#errorCountProduct').html("" + errorData.length);
//	if(errorData.length != 0) document.getElementById("download-errors-product").disabled = false;
//}

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

//function getProductUrl(){
//   var baseUrl = $("meta[name=baseUrl]").attr("content")
//   return baseUrl + "/api/product";
//}
//
//function getBrandCategoryUrl() {
//  var baseUrl = $("meta[name=baseUrl]").attr("content");
//  return baseUrl + "/api/brand";
//}
//
////BUTTON ACTIONS
//function addProduct(event){
//    event.preventDefault();
//
//   //Set the values to update
//   var $form = $("#product-form");
//   var brandCategory = $('#inputBrandCategory').val();
//   var brandCategoryJson = extractNameAndCategory(brandCategory);
//
//   $form.append('<input type="hidden" name="brandName" value="' + brandCategoryJson.brandName + '" /> ');
//   $form.append('<input type="hidden" name="brandCategory" value="' + brandCategoryJson.brandCategory + '" /> ');
//
//   var json = toJson($form);
//
//   console.log(json);
//   var url = getProductUrl();
//
//   $.ajax({
//      url: url,
//      type: 'POST',
//      data: json,
//      headers: {
//           'Content-Type': 'application/json'
//       },
//      success: function(response) {
//             getProductList();
//         $form.trigger("reset");
//      },
//      error: handleAjaxError
//   });
//
//   return false;
//}
//
//function updateProduct(event){
//   $('#edit-product-modal').modal('toggle');
//   //Get the ID
//   var id = $("#product-edit-form input[name=id]").val();
//   var url = getProductUrl() + "/" + id;
//
//   //Set the values to update
//   var $form = $("#product-edit-form");
//   var brandCategory = $('#inputEditBrandCategory').val();
//    var brandCategoryJson = extractNameAndCategory(brandCategory);
//
//    $form.append('<input type="hidden" name="brandName" value="' + brandCategoryJson.brandName + '" /> ');
//    $form.append('<input type="hidden" name="brandCategory" value="' + brandCategoryJson.brandCategory + '" /> ');
//   var json = toJson($form);
//   console.log(json);
//
//   $.ajax({
//      url: url,
//      type: 'PUT',
//      data: json,
//      headers: {
//           'Content-Type': 'application/json'
//       },
//      success: function(response) {
//             getProductList();
//      },
//      error: handleAjaxError
//   });
//
//   return false;
//}
//
//
//function getProductList(){
//   var url = getProductUrl();
//   console.log("ProductURL: " + url);
//   $.ajax({
//      url: url,
//      type: 'GET',
//      success: function(data) {
//             displayProductList(data);
//      },
//      error: handleAjaxError
//   });
//}
//
//function deleteProduct(id){
// var url = getProductUrl() + "/" + id;
//
// $.ajax({
//    url: url,
//    type: 'DELETE',
//    success: function(data) {
//           getProductList();
//    },
//    error: handleAjaxError
// });
//}

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
         console.log("Sent data");
         console.log(res);
         $("#rowCountProduct").text(res.totalCount);
         $("#processCountProduct").text(res.successCount);
         $("#errorCountProduct").text(res.errorCount);
         if(res.errorCount != 0) document.getElementById("download-errors-product").disabled = false;
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
//
//
//function addDataToBrandCategoryDropdown(data, formId) {
//   var $brandCategory = $(`${formId} select[name=brandCategory]`);
//   $brandCategory.empty();
//
//   var brandDefaultOption = '<option value="">Select brand and category</option>';
//    $brandCategory.append(brandDefaultOption);
//
//   for (var i in data) {
//     var e = data[i];
//     var option =
//      '<option value="' +
//      e.brand + '~' + e.category +
//      '">' +
//      e.brand + '-' + e.category +
//      "</option>";
//     $brandCategory.append(option);
//   }
//  }
//
//
//function populateBrandCategoryDropDown(formType) {
//   var url = getBrandCategoryUrl();
//   $.ajax({
//     url: url,
//     type: "GET",
//     success: function (data) {
//       if(formType === 'add-form') {
//          addDataToBrandCategoryDropdown(data, "#product-form");
//      } else if(formType === 'edit-form') {
//          addDataToBrandCategoryDropdown(data, "#product-edit-form");
//        }
//     },
//     error: handleAjaxError,
//   });
//  }
//
////UI DISPLAY METHODS
//
////function displayProductList(data){
////   var $tbody = $('#product-table').find('tbody');
////   $tbody.empty();
////   for(var i in data){
////      var b = data[i];
//////    var buttonHtml = '<button onclick="deleteProduct(' + b.id + ')">delete</button>'
////      var buttonHtml = '<button class="btn btn-outline-dark px-4 mx-2" data-toggle="tooltip" title="Edit" onclick="displayEditProduct(' + b.id + ')"><i class="fa fa-edit fa-lg"></i></button>'
////      var row = '<tr>'
////      + '<td>&nbsp;</td>'
////      + '<td>' + b.barcode + '</td>'
////      + '<td>' + b.brand + '</td>'
////      + '<td>' + b.category + '</td>'
////      + '<td>' + b.product + '</td>'
////      + '<td>' + b.price + '</td>'
////      + '<td>' + buttonHtml + '</td>'
////      + '</tr>';
////        $tbody.append(row);
////   }
////}
//
//function displayEditProduct(id){
//   var url = getProductUrl() + "/" + id;
//   populateBrandCategoryDropDown("edit-form");
//
//   $.ajax({
//      url: url,
//      type: 'GET',
//      success: function(data) {
//             displayProduct(data);
//      },
//      error: handleAjaxError
//   });
//}
//
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

//function displayProduct(data) {
//    var brandCategory = data.brandName + "-" + data.brandCategory;
//   $("#product-edit-form input[name=product]").val(data.product);
//   $("#product-edit-form input[name=barcode]").val(data.barcode);
//   $("#product-edit-form select[name=brandCategory] option:contains(" + brandCategory + ")").attr("selected", true);
//   $("#product-edit-form input[name=price]").val(data.price);
//   $("#product-edit-form input[name=id]").val(data.id);
//   $('#edit-product-modal').modal('toggle');
//   console.log(data);
//}
//
//function displayAddModal() {
//   $('#add-product-modal').modal('toggle');
//   populateBrandCategoryDropDown("add-form");
//}
//
//
////INITIALIZATION CODE
//function init(){
//   $('#product-form').submit(addProduct);
//   $('#add-product-button').click(displayAddModal);
//   $('#update-product').click(updateProduct);
//   $('#refresh-data').click(getProductList);
//   $('#upload-data').click(displayUploadData);
//   $('#process-data').click(processData);
//   $('#download-errors').click(downloadErrors);
//    $('#productFile').on('change', updateFileName)
//}
//
//$(document).ready(init);
//$(document).ready(getProductList);