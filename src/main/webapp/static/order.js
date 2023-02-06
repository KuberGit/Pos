// URLs
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/product";
}
function getOrderItemUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/orderitem";
}
function getOrderUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order";
}
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}
function getRole() {
   var role = $("meta[name=role]").attr("content")
   return role;
}

$('#nav-orders').addClass('active');

	// Global variables
var $tbody = $('#customer-order-table').find('tbody');
var $totalItems=$('#totalItems');
var $tbodyEdit = $('#customer-order-edit-table').find('tbody');
var $totalItemsEdit=$('#totalItemsEdit');
var editOrderItemsJsonArray=[];

function getOrderList(){
		var url = getOrderUrl();
		$.ajax({
			url: url,
			type: 'GET',
			success: function(data) {
		   	// display list
		   	displayOrderList(data);  
		   },
		   error: handleAjaxError
		});
}

function deleteOrder(id){
    	var url = getOrderUrl() + "/" + id;
		$.ajax({
			url: url,
			type: 'DELETE',
			success: function(data) {
		   		// get list
		   		getOrderList();  
		   	},
		   	error: handleAjaxError
		   });
}

// open pdf
function openBillPdf(blob){
	window.open(window.URL.createObjectURL(blob),"bill.pdf");
}

function downloadBillPdf(blob){
	let link = document.createElement('a');
	link.href = window.URL.createObjectURL(blob);
	var currentdate = new Date();
	link.download = "bill_"+ currentdate.getDate() + "/"
	+ (currentdate.getMonth()+1)  + "/" 
	+ currentdate.getFullYear() + "@"  
	+ currentdate.getHours() + "h_"  
	+ currentdate.getMinutes() + "m_" 
	+ currentdate.getSeconds()+"s.pdf";
	link.click();
}	

// view order
function viewOrder(id){
	$('#view-order-modal').modal('toggle');
	getOrderItems(id);
}

// edit order
function editOrder(id){
	$('#edit-order-modal').modal('toggle');
	$("#order-edit-form input[name=id]").val(id);
	getOrderItemsEdit(id);
}

// get order items by orderId
function getOrderItemsEdit(id){
	var url = getOrderItemUrl() + "/" + id;
   $.ajax({
   	url: url,
   	type: 'GET',
   	success: function(data) {
            // display data
            displayOrderItemsEdit(data);   
        },
        error: handleAjaxError
    });   
}

function displayOrderItemsEdit(data){
	var $tbodyEditOrder = $('#customer-order-edit-table').find('tbody');
	$tbodyEditOrder.empty();
	var j=0;
	for(var i in data){
		var e = data[i];
		// create json for order items already exist for future use
		var json={
			"barcode":e.barcode,
			"quantity":e.quantity
		};
		// store json in global array
		editOrderItemsJsonArray[j]=json;
		var buttonHtml = ' <input type="button" class="btn btn-outline-danger" value="Delete" id="deleteItemButtonEdit">'
		var row = '<tr>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + e.name + '</td>'
		+ '<td>' + e.quantity + '</td>'
		+ '<td>' + e.sellingPrice + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
		$tbodyEditOrder.append(row);
		j++;
	}
	$totalItemsEdit.val(j);
}


function getOrderItems(id){
	var url = getOrderItemUrl() + "/" + id;
   $.ajax({
   	url: url,
   	type: 'GET',
   	success: function(data) {
            // display data
            displayOrderItems(data);   
        },
        error: handleAjaxError
    });   
}

function displayOrderItems(data){
	var $tbodyViewOrder = $('#view-order-table').find('tbody');
	$tbodyViewOrder.empty();

	for(var i in data){
		var e = data[i];
		var row = '<tr>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + e.name + '</td>'
		+ '<td>' + e.quantity + '</td>'
		+ '<td>' + e.sellingPrice + '</td>'
		+ '<td>' + e.sellingPrice * e.quantity + '</td>'
		+ '</tr>';
		$tbodyViewOrder.append(row);
	}
}

function updateOrder(){
	if($totalItemsEdit.val()==0){
		$.notify("Add items to update order !!","error");
		return false;
	}
	var table = document.getElementById("customer-order-edit-table");
	var orderData=[];
	var j=0;
	// take rows from table and process cells
	for (var i = 1, row; row = table.rows[i]; i++) {
   			// create json
   			var json = {
   				"barcode":row.cells[0].innerHTML,
   				"quantity":row.cells[2].innerHTML,
   				"sellingPrice":row.cells[3].innerHTML
   			};

   			orderData[j]=json;
   			j++;
   		}

		// call api
		//Get the ID
		var id = $("#order-edit-form input[name=id]").val();

		var url=getOrderUrl()+"/"+id;
		$.ajax({
			url: url,
			type: 'PUT',
			data: JSON.stringify(orderData),
			headers: {
				'Content-Type': 'application/json; charset=utf-8'
			},
			success: function(data) {
				// nullify array
				editOrderItemsJsonArray=[];

                $tbodyEdit.empty();
                $totalItemsEdit.val(0);
                $.notify("Order updated successfully !!","success");
                getOrderList();
                $('#edit-order-modal').modal('toggle');

            },
        error: handleAjaxError
        });
}

function createOrder(){
		if($totalItems.val()==0){
			$.notify("Add items to create order !!","error");
			return false;
		}
		var table = document.getElementById("customer-order-table");
		var orderData=[];
		var j=0;
		for (var i = 1, row; row = table.rows[i]; i++) {
   			// create json
   			var json = {
   				"barcode":row.cells[0].innerHTML,
   				"quantity":row.cells[2].innerHTML,
   				"sellingPrice":row.cells[3].innerHTML	
   			};

   			orderData[j]=json;
   			j++;
   		}
		var url=getOrderUrl();
		$.ajax({
			url: url,
			type: 'POST',
			data: JSON.stringify(orderData),
			headers: {
				'Content-Type': 'application/json; charset=utf-8'
			},	   
			success: function(data) {
            $tbody.empty();
            $totalItems.val(0);
            $.notify("Order added successfully !!","success");
            getOrderList();
            $('#add-order-modal').modal('toggle');

        },
        error: handleAjaxError
        });
}

function generateInvoice(id){
        var url = getOrderItemUrl() + "/" + id;
        $.ajax({
           	url: url,
           	type: 'GET',
           	success: function(dataRec) {
           	      var url=getOrderUrl() + "/invoice";
                  		$.ajax({
                  			url: url,
                  			type: 'POST',
                  			data: JSON.stringify(dataRec),
                  			headers: {
                  				'Content-Type': 'application/json; charset=utf-8'
                  			},
                  			success: function(data) {
                  	   		// process recieved pdf
                  	   		let binaryString = window.atob(data);

                  	   		let binaryLen = binaryString.length;

                  	   		let bytes = new Uint8Array(binaryLen);

                  	   		for (let i = 0; i < binaryLen; i++) {
                  	   			let ascii = binaryString.charCodeAt(i);
                  	   			bytes[i] = ascii;
                  	   		}
                  	   		let blob = new Blob([bytes], {type: "application/pdf"});
                              //openBillPdf(blob);
                              downloadBillPdf(blob);
                              var disableUrl = getOrderUrl() + "/invoicedisable/" + id;
                              $.ajax({
                                    url : disableUrl,
                                    type : 'POST',
                                    success : function(){
                                            getOrderList();
                                    },
                                    error:handleAjaxError


                              });
                              getOrderList();

                          },
                          error: handleAjaxError
                          });


            },
                error: handleAjaxError
            });
}

//UI DISPLAY METHODS
function displayOrderList(data){
		var $tbodyOrder = $('#order-table').find('tbody');
		$tbodyOrder.empty();
		var n = 1;
		for(var i in data){
			var e = data[i];
			var buttonHtml = '';
			if(e.isInvoiceCreated == 1 && getRole() === "supervisor"){
			   buttonHtml =' <button title="Edit" class="btn btn-danger disabled" ><i class="fa fa-edit fa-lg" aria-hidden="true"></i></button>'
			}else if(getRole() === "supervisor"){
			   buttonHtml =' <button title="Edit" class="btn btn-outline-success" onclick="editOrder(' + n + ')"><i class="fa fa-edit fa-lg" aria-hidden="true"></i></button>'
			}
			buttonHtml+=' <button title="View Order" class="btn btn-outline-primary" onclick="viewOrder(' + e.id + ')"><i class="fa fa-info fa-lg" aria-hidden="true"></i></button>'
			buttonHtml+=' <button title="Generate Invoice" class="btn btn-outline-success" onclick="generateInvoice(' + e.id + ')"><i class="fa fa-file-invoice fa-lg" aria-hidden="true"></i></button>'
			var row = '<tr>'
			+ '<td>' + n + '</td>'
			+ '<td>' + Date(e.datetime).toString().split("G")[0] + '</td>'
			+ '<td>' + e.billAmount + '</td>'
			+ '<td>' + buttonHtml + '</td>'
			+ '</tr>';
			$tbodyOrder.append(row);
			n++;
		}
}
	// update fields
function updateProductDetailsForItem(data){
		// set data
		$('#inputMrp').prop('readonly', false);
		$('#inputMrp').val(data.mrp);
		$('#inputName').val(data.name);
		$('#inputQuantity').prop('readonly', false);
		$('#inputQuantity').val(1);
		// if item exist in table then minus it from available
		var table = document.getElementById("customer-order-table");
		var quantity=checkItemExist(data.barcode,table);
		var availableQuantity=data.availableQuantity - quantity;
		$('#availableQuantity').text(availableQuantity);
		$('#availableInventoryRow').show();
}

function updateProductDetailsForItemEditOrder(data){
		$('#inputMrpEditOrder').prop('readonly', false);
		$('#inputMrpEditOrder').val(data.mrp);
		$('#inputNameEditOrder').val(data.name);
		$('#inputQuantityEditOrder').prop('readonly', false);
		$('#inputQuantityEditOrder').val(1);
		// if item exist in table then minus it from available
		var table = document.getElementById("customer-order-edit-table");
		var quantity=checkItemExist(data.barcode,table);
		var availableQuantity=data.availableQuantity - quantity;
		// also check global array and add existing item quantity in order
		for(var i = 0; i < editOrderItemsJsonArray.length; i++) {
			var barcodeInArray=editOrderItemsJsonArray[i].barcode;
			var barcode=data.barcode;
			var checkEquality=barcode.localeCompare(barcodeInArray);
			if(checkEquality==0){
				availableQuantity=+availableQuantity + +editOrderItemsJsonArray[i].quantity;
			}
		}
		$('#availableQuantityEditOrder').text(availableQuantity);
		$('#availableInventoryRowEditOrder').show();
}

	// get mrp
function getProductDetails(barcode){
		var url = getProductUrl()+"/b/"+barcode;
		// call api
		$.ajax({
			url: url,
			type: 'GET',
			success: function(data) {
		   		//update mrp
		   		updateProductDetailsForItem(data);  
		   	},
		   	error: function(response){ 		
		   		handleAjaxError(response);
		   		// nullify barcode
		   		$('#inputBarcode').val('');  
		   	}
		});
}


// get mrp
function getProductDetailsEditOrder(barcode){
	var url = getProductUrl()+"/b/"+barcode;
		// call api
		$.ajax({
			url: url,
			type: 'GET',
			success: function(data) {
		   		//update mrp
		   		updateProductDetailsForItemEditOrder(data);  
		   	},
		   	error: function(response){ 		
		   		handleAjaxError(response);
		   		// nullify barcode
		   		$('#inputBarcodeEditOrder').val('');  
		   	}
		   });
	}

function updateQuantityInTable(barcode,table,finalQuantity){
	var checkEquality=9;
	// if barcode exist then add quantity in table row
	for (var i = 1, row; row = table.rows[i]; i++) {
		var cellBarcode=row.cells[0].innerHTML;
		checkEquality=cellBarcode.localeCompare(barcode);
		if(checkEquality==0)
		{
			row.cells[2].innerHTML=finalQuantity;
		}
   	}
}

function addItemInTable(){
		var barcode=$("#inputBarcode").val();
		var quantity=$('#inputQuantity').val();
		var sellingPrice = $('#inputMrp').val();
		// validate data
		if(barcode.length == 8){
			if(quantity <= 0){
				$.notify("Quantity for product can not be negative or zero !!","error");
				return false;
			}
			if(sellingPrice == "") {
            	$.notify("Selling Price for product can not be empty !!","error");
                return false;
            }
			if(sellingPrice < 0) {
			    $.notify("Selling Price for product can not be negative !!","error");
                return false;
			}
			var availableQuantity=parseInt($('#availableQuantity').text());
			if(availableQuantity < quantity){
				$.notify("Available quantity is "+availableQuantity,"info");
				return false;		
			}
			var table = document.getElementById("customer-order-table");
			var quantityInTable=checkItemExist(barcode,table);
			// if not exist then add new row
			if(quantityInTable == 0){
			var itemId=$totalItems.val();
			itemId++;
			$totalItems.val(itemId);
	var buttonHtml = ' <input type="button" class="btn btn-outline-danger" value="Delete" id="deleteItemButton">'
//	buttonHtml+=' <button class="btn btn-outline-success" onclick="editOrder(' + e.id + ')">Edit</button>'
	var row = '<tr>'
	+ '<td>' + $("#inputBarcode").val() + '</td>'
	+ '<td>' + $("#inputName").val() + '</td>'
	+ '<td>'  + $("#inputQuantity").val() + '</td>'
	+ '<td>' + $("#inputMrp").val() + '</td>'
	+ '<td>' + buttonHtml + '</td>'
	+ '</tr>';
	$tbody.append(row);
}else{
	var finalQuantity=+quantityInTable + +quantity;
	updateQuantityInTable(barcode,table,finalQuantity);
}
	$('#inputMrp').val('');
	$('#inputName').val('');
	$('#inputQuantity').prop('readonly', true);
	$('#inputQuantity').val('');
	$('#inputBarcode').val('');
	$('#availableInventoryRow').hide();
	$('#availableQuantity').text('');
}else{
	$.notify("Please enter valid barcode !!","error");
	return false;
}
}

function addItemInEditTable(){
	var barcode=$("#inputBarcodeEditOrder").val();
	var quantity=$('#inputQuantityEditOrder').val();
	var sellingPrice = $('#inputMrpEditOrder').val();
	if(barcode.length == 8){
		if(quantity <= 0){
			$.notify("Quantity for product can not be negative or zero !!","error");
			return false;
		}
		if(sellingPrice == "") {
             $.notify("Selling Price for product can not be empty !!","error");
             return false;
        }
		if(sellingPrice < 0) {
            $.notify("Selling Price for product can not be negative !!","error");
            return false;
        }
		var availableQuantity=parseInt($('#availableQuantityEditOrder').text());
			if(availableQuantity < quantity){
				$.notify("Available quantity is "+availableQuantity,"info");
				return false;		
			}
			var table = document.getElementById("customer-order-edit-table");
			var quantityInTable=checkItemExist(barcode,table);
			if(quantityInTable == 0){
			
		var itemId=$totalItemsEdit.val();
		itemId++;
		$totalItemsEdit.val(itemId);
	var buttonHtml = ' <input type="button" class="btn btn-outline-danger" value="Delete" id="deleteItemButton">'
	var row = '<tr>'
	+ '<td>' + $("#inputBarcodeEditOrder").val() + '</td>'
	+ '<td>' + $("#inputNameEditOrder").val() + '</td>'
	+ '<td>'  + $("#inputQuantityEditOrder").val() + '</td>'
	+ '<td>' + $("#inputMrpEditOrder").val() + '</td>'
	+ '<td>' + buttonHtml + '</td>'
	+ '</tr>';
	$tbodyEdit.append(row);
}else{
	var finalQuantity=+quantityInTable + +quantity;
	updateQuantityInTable(barcode,table,finalQuantity);
}
	$('#inputMrpEditOrder').val('');
	$('#inputNameEditOrder').val('');
	$('#inputQuantityEditOrder').prop('readonly', true);
	$('#inputQuantityEditOrder').val('');
	$('#inputBarcodeEditOrder').val('');
	$('#availableInventoryRowEditOrder').hide();
	$('#availableQuantityEditOrder').text('');

}else{
	$.notify("Please enter valid barcode !!","error");
	return false;
}
}

function checkItemExist(barcode,table){
	var quantity=0;
	var checkEquality=9;
	// check row wise each cell
	for (var i = 1, row; row = table.rows[i]; i++) {
		var cellBarcode=row.cells[0].innerHTML;
		checkEquality=cellBarcode.localeCompare(barcode);
		if(checkEquality==0)
		{
			quantity=row.cells[2].innerHTML;
		}
   	}
   	return quantity;
}

function showOrderModal(){
	$('#add-order-modal').modal('toggle');
}

	//INITIALIZATION CODE
function init(){
		$('#show-add-order-modal').click(showOrderModal);
		$('#add-item-button').click(addItemInTable);
		$('#add-item-button-edit').click(addItemInEditTable);
		$('#update-order').click(updateOrder);
		$("#inputBarcode").on('input',function(){
			var valOfItem=$(this).val();
			var len=valOfItem.length;
			// when length is 8 then get the details else do following
			if(len < 8){
				$('#inputQuantity').val('');
				$('#inputQuantity').prop('readonly', true);
				$('#inputName').val('');
				$('#inputMrp').val('');
				$('#availableInventoryRow').hide();
			}
			if(len==8)
			{
	      	// call api
	      	getProductDetails($(this).val());	
	      }
	  });
		$("#inputBarcodeEditOrder").on('input',function(){
			var valOfItem=$(this).val();
			var len=valOfItem.length;
			// when length is 8 then get the details else do following
			if(len < 8){
				$('#inputQuantityEditOrder').val('');
				$('#inputQuantityEditOrder').prop('readonly', true);
				$('#inputNameEditOrder').val('');
				$('#inputMrpEditOrder').val('');
				$('#availableInventoryRowEditOrder').hide();
			}
			if(len==8)
			{
	      	// call api
	      	getProductDetailsEditOrder($(this).val());	
	      }
	  });
		// delete button action
		$('#customer-order-table').on('click', 'input[type="button"]', function () {
			var closestTr=$(this).closest('tr');
			var barcodeInTable=closestTr.find('td:eq(0)').text();
			var barcode=$('#inputBarcode').val();
			var checkEquality=barcode.localeCompare(barcodeInTable);
			// check if existing item in table has same barcode as entered then update available quantity
			if(checkEquality==0){

				var quantityInTable=closestTr.find('td:eq(2)').text();
				var availableQuantity=parseInt($('#availableQuantity').text());
				var finalQuantity=+quantityInTable + +availableQuantity;
				$('#availableQuantity').text(finalQuantity);
			}
			// remove item
			closestTr.remove();
			var itemId=$totalItems.val();
			itemId--;
			$totalItems.val(itemId);
		});
		// delete button action
		$('#customer-order-edit-table').on('click', 'input[type="button"]', function () {
			var closestTr=$(this).closest('tr');
			var barcodeInTable=closestTr.find('td:eq(0)').text();
			var barcode=$('#inputBarcodeEditOrder').val();
			var checkEquality=barcode.localeCompare(barcodeInTable);
			// check if existing item in table has same barcode as entered then update available quantity
			if(checkEquality==0){
				var quantityInTable=closestTr.find('td:eq(2)').text();
				var availableQuantity=parseInt($('#availableQuantityEditOrder').text());
				var finalQuantity=+quantityInTable + +availableQuantity;
				$('#availableQuantityEditOrder').text(finalQuantity);
			}
			// remove item
			closestTr.remove();
			var itemId=$totalItemsEdit.val();
			itemId--;
			$totalItemsEdit.val(itemId);
		});

		$('#create-order').click(createOrder);

}

$(document).ready(init);
$(document).ready(getOrderList);
