<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />



<style type="text/css">
    html { height: 100% }
    body { height: 100%; margin: 0; padding: 0 }
    #map-canvas { height: 100% }
    #controls {
		position:absolute;
		bottom: 25px;
		width: 90%;
		left: 0;
		right: 0;
		margin-left: auto;
		margin-right: auto;		
		background-color/**/: #ffffff;
		background-image/**/: none;
		opacity: 0.85;
		filter: alpha(opacity=85);
		padding: 20px;
		height:40px;
		border-radius: 20px;
	}
	
	#controls #slider-range{ opacity: 1; filter: alpha(opacity=100);}
	#controls #slider-range,#controls #time{margin:10px;display:block;}
</style>

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"
	type="text/javascript">
</script> 

<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js"
	type="text/javascript">
</script>

<link rel="stylesheet"
	type="text/css"
	href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/dot-luv/jquery-ui.css"
/>

<!--  Load the AJAX API -->
		
<script type="text/javascript" src="https://www.google.com/jsapi">
</script>


<script type="text/javascript"
	src= "https://maps.googleapis.com/maps/api/js?key=AIzaSyAzpD0IRSKo4UTR6ys55mTyJPclMV__3uE&sensor=false&libraries=visualization">
</script>


<script type="text/javascript">
	// Load the Visualization API and the controls package.
	google.load('visualization', '1.0', {'packages':['corechart']});
	

	var stationsUrl = "StationsServlet";
	
	// map object
	var map;

	// london center coordinate as default map center
	var londonCenter = new google.maps.LatLng(
							51.58236047264352,
							-0.06567167037554444
						);
		
	// zoom level for the map
	defaultZoom = 11;

	// info window object
	var infoWindow;

	//set error handler for jQuery AJAX requests
	$.ajaxSetup({"error":function(XMLHttpRequest,textStatus, errorThrown) {
							alert(textStatus);
						alert(errorThrown);
						alert(XMLHttpRequest.responseText);
				}
	});
 
	// option for google map object
	var mapOptions = {
		zoom: defaultZoom,
		center: londonCenter,
		mapTypeId: google.maps.MapTypeId.ROAD_MAP
	};

	// List with all markers to check if exist
	var markerList = {};
	
	// List with all flow destinations
	var destList = [];
	
	// List with all flow lines
	var flowList = [];

	// Slider start and end
	var val0, val1;
	
	// Slider values used for the last query
	var uVal0, uVal1;
	
	function getWeightColor(weight) {
		   debugger;
			var r = parseInt(weight * 255, 10) - 30;
			var g = parseInt(255 - weight * 255, 10) + 30;
			
		    var color = '#';
		    var sr = r.toString(16);
		    if (sr.length < 2)
		    	sr = '0' + sr;
		    
		    var sg = g.toString(16);
		    if (sg.length < 2)
		    	sg = '0' + sg;
		    color = color + sr;
		    color = color + sg;
		    color = color +'00';
		    return color;
	}
	
	function initialize() {
		// create new map
		map = new google.maps.Map(document.getElementById('map-canvas'),
								  mapOptions);
		
		// create new info window for marker detail pop-up
		infoWindow = new google.maps.InfoWindow();
		
		// load markers
		loadMarkers();
	}
	
	
	
	// load markers via ajax request from server
	function loadMarkers() {
		// load marker jSon data
		$.getJSON(stationsUrl, function(data) {
			// loop all markers
			$.each(data, function(i, item) {
				
				// add marker to map
				loadMarker(item);
			});
		});
	}
	

	
	// Add marker to map
	function loadMarker(markerData) {
		// Create new marker location
		var newLatLng = new google.maps.LatLng(
							markerData['lng'],
							markerData['lat']
						);
		
		// Create new marker
		var marker = new google.maps.Marker({
			id: markerData['id'],
			map: map,
			title: markerData['name'],
			position: newLatLng,
			icon: 'images//tube_icon.png'
			//animation: google.maps.Animation.DROP
		});
		
		//marker.setAnimation(google.maps.Animation.BOUNCE);
		// Add marker to list
		markerList[marker.id] = marker;
		
		// Add event listener when the marker is clicked
/* 		google.maps.event.addListener(marker, 'click', function() {
			// show marker when clicked
			showMarker(marker.id);
		}); */
		
	}
	
	function draw_chart(marker, markerId, d) {
		// Create the data table
		var data = new google.visualization.DataTable();
		data.addColumn('string', 'Segment');
		data.addColumn('number', 'Percentage');
		data.addRows([
		  ['Travel Card', d["Travel Card"]],
		  ['No Ticket', d["No Ticket"]],
		  ['Elderly', d["Elderly"]],
		  ['Disabled', d["Disabled"]],
		  ['Staff', d["Staff"]],
		  ['Others', d["Others"]]
		]);
		
		// Set chart options
		var options = {'title':marker.title,
					   'width':450,
					   'height':250};
		
		var node = document.createElement('div');
		
		
		// Instantiate and draw our chart, passing in some options.
		var chart = new google.visualization.PieChart(node);
		chart.draw(data, options);
		infoWindow.setContent(node);
		
        google.visualization.events.addListener(chart, 'select', function() {
        	var selection = chart.getSelection();
        	var selected = '';
	       	var item = selection[0];
        	if (item.row != null) {
        	 selected = data.getFormattedValue(item.row, 0);
        	}
        	$.getJSON("SegmentFlow?origin="+markerId+"&start="+uVal0+"&end="+
        			uVal1 + "&type="+selected, function(data) {
        		$.each(data, function(i, item) {
        			draw_flow(marker, item);
        		});
				infoWindow.close();
			}); 
			infoWindow.setContent(
					'<image src="images//tube_icon.png"><b>' + marker.title + '</b><br />' +
					'<div id = "loading_' + markerId + '">' +
					'<image src="images//spinner.gif"> Loading... </div>');	
        	//infoWindow.close();
        });

		//infoWindow.open(map, marker);
		//chart.draw(data, options);
	
	}
	
	// Add flow the map
	function draw_flow(origin, flowData) {
		var dest = markerList[flowData["ID"]];
		if (dest) {
			var lineCoordinates = [
			                       origin.position,
			                       dest.position
			                     ];

			                     var lineSymbol = {
			                       path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW,
			                     };

			                     var line = new google.maps.Polyline({
			                       strokeWeight: 4,
			                       strokeColor: getWeightColor(flowData["Weight"]),
			                       path: lineCoordinates,
			                       icons: [{
			                         icon: lineSymbol,
			                         offset: '100%'
			                       }],
			                       map: map
			                     });
		flowList.push(line);
		} else {
			// No location exist for this marker
		}
	}
	
	/**
	* Show marker info window
	*/
	function showMarker(markerId) {
		// Get marker information from marker list
		var marker = markerList[markerId];
		
		// check if marker was found
		if (marker) {
			// clear all flow 
			for (var i = 0; i < flowList.length; i++) {
				flowList[i].setMap(null);
			}
			flowList = [];
			
			infoWindow.close();
			// get marker details from server
			infoWindow.setContent(
					'<image src="images//tube_icon.png"><b>' + marker.title + '</b><br />' +
					'<div id = "loading_' + markerId + '">' +
					'<image src="images//spinner.gif"> Loading... </div>');					
			
			infoWindow.open(map, marker);
			uVal0 = val0;
			uVal1 = val1;
			$.getJSON("StationDashboard?station="+markerId+"&start="+val0+"&end="+val1, function(data) {
				draw_chart(marker, markerId, data);
			});

		} else {
			alert("Error: marker (" + markerId + ") not found.");
		}
	}
	google.maps.event.addDomListener(window, 'load', initialize);
	
</script>

<!-- Dashboard APIs -->
<script type="text/javascript" src="https://www.google.com/jsapi"></script>


<title>Travel Dashboard</title>
</head>
<body>

	 <div id="map-canvas">
	</div>
	
	<div id="controls">
		<div id="slider-range"></div>
		<span id="time"></span>
		
		<div id="chart_div"></div>
	</div>
	
			<script type="text/javascript">
			$("#slider-range").slider({
				range: true,
				min: 0,
				max: 1439,
				values: [0, 1439],
				slide: slideTime,
				step: 30
			});
			function slideTime(event, ui){
				val0 = $("#slider-range").slider("values", 0);
				val1 = $("#slider-range").slider("values", 1);
				var minutes0 = parseInt(val0 % 60, 10),
					hours0 = parseInt(val0 / 60 % 24, 10),
					minutes1 = parseInt(val1 % 60, 10),
					hours1 = parseInt(val1 / 60 % 24, 10);

				startTime = getTime(hours0, minutes0);
				endTime = getTime(hours1, minutes1);
				$("#time").text(startTime + ' - ' + endTime);
				//$("#time").text(val0 +" ---> " + val1);
			}
			function getTime(hours, minutes) {
				var time = null;
				minutes = minutes + "";
				if (hours < 12) {
					time = "AM";
				}
				else {
					time = "PM";
				}
				if (hours == 0) {
					hours = 12;
				}
				if (hours > 12) {
					hours = hours - 12;
				}
				if (minutes.length == 1) {
					minutes = "0" + minutes;
				}
				return hours + ":" + minutes + " " + time;
			}
			slideTime();
		</script>
</body>
</html>