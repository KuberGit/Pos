function getUserUrl(){
   var baseUrl = $("meta[name=baseUrl]").attr("content")
   return baseUrl + "/api/admin/user";
}

function getRole() {
   var role = $("meta[name=role]").attr("content")
   return role;
}

function getUserList(){
   var url = getUserUrl();
   $.ajax({
      url: url,
      type: 'GET',
      success: function(data) {
             displayUserList(data);   
      },
      error: handleAjaxError
   });
}


//UI DISPLAY METHODS

function displayUserList(data){
   var $tbody = $('#user-table').find('tbody');
   $tbody.empty();
   for(var i in data){
      var e = data[i];
      var row = '<tr>'
      + '<td>' + e.id + '</td>'
      + '<td>' + e.email + '</td>'
      + '<td>' + e.role + '</td>'
      + '</tr>';
        $tbody.append(row);
   }
}


//INITIALIZATION CODE
function init(){
    $('#nav-users').addClass('active');
}

$(document).ready(init);
$(document).ready(getUserList);