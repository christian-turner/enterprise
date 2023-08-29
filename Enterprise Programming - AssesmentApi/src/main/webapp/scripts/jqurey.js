$(document).ready(function(){
	// if a button on the table is clicked 
	$('#tableWrapper').on("click", "button" , function () {
		// the correct form is built
		$("#deleteFilm").show();
		hideSearch();
		editFilm();
		// the nearest parameters are found and populated to the form
		$("#filmIDs").val($(this).closest("tr").find(".id2").text());
		$("#Title").val($(this).closest("tr").find(".title").text());
		$("#Year").val($(this).closest("tr").find(".year").text());
		$("#Director").val($(this).closest("tr").find(".director").text());
		$("#Stars").val($(this).closest("tr").find(".stars").text());
		$("#Review").val($(this).closest("tr").find(".review").text());
		// appropiate form populated
		$("#filmTable").hide(1000);
		$("#filmForm").show(1000);
		$("#formTitle").html("Edit Film")
		
	 });
	// if cancel button is pressed depopulate form and return back to original page
	$("#cancelSubmitFilm").click(function(){
		showSearch();
		dePopulateForm();
		$("#filmId").html('');
		$("#filmTable").show(1000);
		$("#filmForm").hide(1000);
		});
	
	//  hide form on page load
	$("#filmForm").hide();
	//  when submit is clicked send data , depopulate form
	$("#submitFilm").click(function(){
			showSearch();
			postForm(checkForm());
			dePopulateForm();
		});
	// when delete is clicked , pass the connection type and depopulate form
	$("#deleteFilm").click(function(){
			showSearch();
			postForm("DELETE");
			dePopulateForm();
		});
		
	// when the add film button is clicked
	$("#addFilm").click(function(){
		// hide/populate the appropiate paramaters
		hideSearch();
		$("#formTitle").html("Add Film")
		$("#deleteFilm").hide();
		$("#filmTable").hide(1000);
		$("#filmForm").show(1000);
		});
	
	// forces the table to populate on load	
	urlGen();
// When the search button is clicked it takes the paramaters and returns the neccercary data
  	$(function() {
    	$("#showResults").click(function(){
		$("#success").html("Searching " + $("#catagory").val()+ " in " + $("#format").val());
		urlGen();
	
});
    
});

});


// get request
function getData(url) {
	
	$.ajax({
		url : url,
		headers: {'Content-Type': 'application/' + $("#format").val() },
		dataType: $("#format").val(),
		success : buldHeaders,
	});
}

 // creating the table headers in html
 function buldHeaders(text){
	var tableContents = "<thead>";
	// creating the table headers
	var tableHeaders = ["Options",  "ID", "Title", "Year", "Director" , "Stars" , "Review" ];
	var headers= "<tr class='ui-widget-header'>";	
	// building the headers in html
	$.each(tableHeaders, function(index,value){
		headers = headers +"<th>" + value + "</th>";
	});
	tableContents += headers + "</tr></thead><tbody>";
	// html complete and returning the text
	chooseFormat(text , $("#format").val() , tableContents);
	
	}
	
//formating the data and showing it in a table
function chooseFormat(text , format , tableContents){
	if (format == "json"){
		
		// passing the to the function to show the table
		return (showTable(text , tableContents));
	}
	else if (format == "xml"){
		// converting the xml to object 
		var text = xmlToObject(text , "film");
		// passing the object and showing the table
		return (showTable(text , tableContents));
	}
	else if (format == "text"){
		// passing the headers as the text api dosent return them
		var headers = ['id', 'title', 'year', 'director', 'stars', 'review']
		// converting the text to object 
		var text = textToObject(text , headers); 
		return (showTable(text , tableContents));
	}
}

// takes the method and creates the appropiate form
function postForm(method){
	var data = {};
	if (method == "PUT"){
		
		var data = {
			"id":$("#filmIDs").val(),
		}
	}
	if (method == "PUT" || method == "POST"){
		
		data["title"] = $("#Title").val()
		data["year"] = $("#Year").val();
		data["director"] = $("#Director").val();
		data["stars"] = $("#Stars").val();
		data["review"] = $("#Review").val(); 
		
		}
	if (method == "DELETE"){
		
		data["id"] = $("#filmIDs").val()
		
	}
	
	
	formatData(data , method);
	
}
// depending on what data type is selected  , it is returned in that type
function formatData(data , method){
	if ($("#format").val() == "json"){
		return (sendData(objToJson(data) , method));	
	}
	else if ($("#format").val() == "xml"){
		return (sendData(objToXml(data) , method));			
	}
	else if ($("#format").val() == "text"){
		return (sendData(objToText(data) , method));			
	}	
}

// takes the connection type and makes the request depending
function sendData(data , method){
	// takes the connection type and the data
	$.ajax({
		type: method,
		headers: {'Content-Type': 'application/' + $("#format").val() },
		url:"filmApi",
		data:data,
		//	 if successfull goes to the handler
		success: checkPost	
	});
}

// returns back to the table and updates it
function checkPost(){
	
	$("#filmId").html('');
	$("#filmTable").show(1000);
	$("#filmForm").hide(1000);
	showSearch();
	// shows the user the action was performed and in which data type
	$("#success").html("Action performed succesfully in "+  $("#format").val());
	urlGen()
}

// populates a id box if it is a edit form
function editFilm(){
	$("#filmId").html('<label>ID: </label>'+
    '<input type="text" class="form-control" id="filmIDs" readonly>');
     
}
// clears all data from the form when ran
function dePopulateForm(){
		
		$("#filmIDs").val("");
		$("#Title").val("");
		$("#Year").val("");
		$("#Director").val("");
		$("#Stars").val("");
		$("#Review").val("");
		
	}
	
// shows the search bar 
function showSearch(){
	
	$("#addFilm").show();
	$("#showSettings").show();
	$("#format").show();
	$("#search").show();
	
}
// hides the search bar when the add/edit film form is present
function hideSearch(){
	
	$("#addFilm").hide();
	$("#showSettings").hide();
	$("#search").hide();
	
}
// takes the search bar data and begins the request
function urlGen(){
	var url = "filmApi?";
	if ($("#catagory").val()  == "id"){
		url = url + "id=" + $("#data").val();
	}
	if ($("#catagory").val() == "title"){
		url = url + "title=" + $("#data").val();
	}
	if ($("#catagory").val() == "year"){
		url = url + "year=" + $("#data").val();
		
	}
	
	getData(url)
}

// From here are all the singular functions , ones that are dynamic and just called when needed 

// checks if the film form is add or edit
function checkForm(){
	// if the edit form is populated
	if(($("#filmIDs") && $("#filmIDs").length)){
		// connection type put
		return "PUT";
	}
	else {
		// connection type post
		return "POST";
		} 
}

// returning the connection type DELETE
function deleteFilm(){
	return "DELETE";
}

// converting a object to json
function objToJson(data){
	var film = "["+ JSON.stringify(data)+"]";
	return film
}
// converting a object to xml

function objToXml(data){
	var xml ='<?xml version="1.0" encoding="UTF-8" standalone="yes"?><film>';
	// jquery loop
	$.each(data , function(paramater , value){
		// building the xml with the paramater and matching value inbetween
		var row = "<"+paramater+">"+value+"</"+paramater+">";
		xml = xml + row;
		});
		xml = xml+ "</film>";
		return xml;
}

//WE NEED TO LOOK AT THIS FOR /N

// function that turns object to text
function objToText(data){
	var text = "";
	$.each(data , function(paramater , value){
		// creating the text from the object with an indentifier
		var row = value + "#";
		text = text+row;
		});
		return text;	
}
// LOOK AT THIS


// a function to convert xml data to a object
function xmlToObject(text , name){
	// empty list to append the objects to
	var objList = [];
	// gets elements by the tag name "film"
    var root = text.getElementsByTagName(name);
    $.each(root, function(i, element){
	// creates an empty local object
        var obj = {};
        var childElements = element.childNodes;
        $.each(childElements, function(j, childElement){
			// assigning the xml objects child nodes to a js object
            obj[childElement.nodeName] = childElement.textContent;
        });
        // pushing it to the object list
        objList.push(obj);
    });
    return objList;
	
}


// converting text to an object
function textToObject(text , headers){
	var objList  = [];
	// splitting up the text on every /n so that the films are seperated
	var rowStrings = text.split("/n");
		// creating empty array
		var rows = new Array(rowStrings.length);
		$.each(rowStrings, function(i, rowString){
			// sperating each value by the operator #
			rows = rowString.split("#");
			var obj = {};
		$.each(headers, function(x, header){
			 // putting the headers and the appropiate value in the object
			  obj[header] = rows[x];
			 });
		// pushing the object to the object list
		objList.push(obj);
		});
		return objList;


}

// function to turn a object and output it in a table
function showTable(text , tableContents){
	  $.each(text, function(i, row){
		tableContents = tableContents += "<tr class='nr'>";
		// build the html table body
		var body = 
		"<td> <button id = 'editDelete"+i+"' class='btn btn-outline-secondary' >Edit/Delete</button></td>"+
		"<td class='id2'>"+row.id+"</td>"+ 
		"<td class='title'>"+row.title+"</td>"+
		"<td class='year'>"+row.year+"</td>"+
		"<td class='director'>"+row.director+"</td>"+
		"<td class='stars'>"+row.stars+"</td>"+
		"<td class='review'>"+row.review+"</td>";
		tableContents = tableContents += body;
	});
	tableContents = tableContents + "<tbody>";
	// populate it on the html
	$("#filmTable").html(tableContents);	  
}
	
