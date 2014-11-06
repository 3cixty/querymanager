<%@page import="eu.threecixty.querymanager.rest.Constants" %>
<%@page import="eu.threecixty.Configuration" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<!--Load the AJAX API-->
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

<%
    if (session.getAttribute("admin") == null) {
    	response.sendRedirect(Constants.OFFSET_LINK_TO_ERROR_PAGE + "error.jsp");
    } else {
%>

<script type="text/javascript">
	// Load the Visualization API and the controls package.
	google.load('visualization', '1.0', {
		'packages' : [ 'corechart', 'table', 'gauge', 'controls' ]
	});

	// Set a callback to run when the Google Visualization API is loaded.
	google.setOnLoadCallback(drawDashboard);

	// Callback that creates and populates a data table,
	// instantiates a dashboard, a range slider and a pie chart,
	// passes in the data and draws it.
	function drawDashboard() {

		 var jsonData = $.ajax({
                               type:"GET",
                               url: "<%=Configuration.get3CixtyRoot()%>/getCallRecords",
                               dataType:"json",
                               async: false
                               }) .responseText;

		// Create our data table. sample
        //var jsonData='{"cols": [{"label":"date","type":"datetime"},{"label":"AppName","type":"string"},{"label":"Requests","type":"number"}],"rows": [{"c":[{"v":"Date(2014,9,30)"},{"v":"test"},{"v":1}]}, {"c":[{"v":"Date(2014,9,31)"},{"v":"test"},{"v":3}]}]}'

        var myObject = eval('(' + jsonData + ')');

        var data = new google.visualization.DataTable(myObject);

		var grouped_data = google.visualization.data.group(data, [ 1 ], [ {
			'column' : 2,
			'aggregation' : google.visualization.data.sum,
			'type' : 'number',
		} ]);

		/* var data = google.visualization.arrayToDataTable([
				[ 'AppName', 'Calls made', 'Request/sec' ],
				[ 'AppMichael', 12, 5 ], [ 'AppElisa', 20, 7 ],
				[ 'AppRobert', 7, 3 ], [ 'AppJohn', 54, 2 ],
				[ 'AppJessica', 22, 6 ], [ 'AppAaron', 3, 1 ],
				[ 'AppMargareth', 42, 8 ], [ 'AppMiranda', 33, 6 ] ]);*/

		// Create a dashboard.
		var dashboard = new google.visualization.Dashboard(document
				.getElementById('dashboard_div'));

		// Create a range slider, passing some options
		var dateRangeFilter = new google.visualization.ControlWrapper({
			'controlType' : 'DateRangeFilter',
			'containerId' : 'dateRangeFilter_div',
			'options' : {
				'filterColumnIndex' : 0,
				'ui' : {
					'orientation' : 'vertical',
					'labelStacking' : 'vertical',
					'label' : ''
				}
			}
		});

		var requestsPerDayPicker = new google.visualization.ControlWrapper({
			'controlType' : 'NumberRangeFilter',
			'containerId' : 'requestsPerDayPicker_div',
			'options' : {
				'filterColumnIndex' : 2,
				'ui' : {
					'orientation' : 'vertical',
					'labelStacking' : 'vertical',
					'label' : ''
				}
			}
		});

		var chartRangeFilterControl = new google.visualization.ControlWrapper({
			'controlType' : 'ChartRangeFilter',
			'containerId' : 'chartRangeFilterControl_div',
			'dataTable' : data,
			'options' : {
				// Filter by the date axis.
				'filterColumnIndex' : 0,
				'ui' : {
					'chartType' : 'AreaChart',
					'chartOptions' : {
						'colors' : [ 'red', '#004411' ],
						'isStacked' : false,
						'chartArea' : {
							'width' : '90%'
						},
						'hAxis' : {
							'baselineColor' : 'none'
						}
					},
					// Display a single series that shows the value.
					// Thus, this view has two columns: the date (axis) and the value (line series).
					'chartView' : {
						'columns' : [ 0, 2 ]
					},
					// 1 day in seconds = 24 * 60 * 60  = 86,400
					'minRangeSize' : 86400
				}
			},
			// Initial range: .
			'state' : {
				'range' : {
					'start' : new Date(2014, 9, 1),
					'end' : new Date()
				}
			}
		});

		var appSelectorFilter = new google.visualization.ControlWrapper({
			'controlType' : 'CategoryFilter',
			'containerId' : 'appSelector_div',
			'options' : {
				'filterColumnIndex' : 1,
				'ui' : {
					'labelStacking' : 'vertical',
					'label' : 'AppName:',
					'allowTyping' : false,
					'allowMultiple' : false
				}
			}
		});

		var completeTable = new google.visualization.ChartWrapper({
			'chartType' : 'Table',
			'containerId' : 'completeTable_div',
			'dataTable' : data,
			'options' : {
				'page' : 'enable'
			}
		});
		var ColumnChartTotalRequests = new google.visualization.ChartWrapper({
			'chartType' : 'ColumnChart',
			'containerId' : 'ColumnChartTotalRequests_div',
			'dataTable' : grouped_data,
			'options' : {
				'hAxis' : {
					'title' : 'App Name'
				},
				'vAxis' : {
					'title' : 'Total Number of Calls Made'
				},
				'colors' : [ 'red', '#004411' ],
				'legend' : 'none'
			}
		});
		ColumnChartTotalRequests.draw()

		// Create a pie chart, passing some options
		var pieChart = new google.visualization.ChartWrapper({
			'chartType' : 'PieChart',
			'containerId' : 'pieChart_div',
			'dataTable' : grouped_data,
			'options' : {
				'pieHole' : 0.2,
				'width' : 400,
				'height' : 400,
				'pieSliceText' : 'label',
				'chartArea' : {
					'left' : 15,
					'top' : 15,
					'right' : 0,
					'bottom' : 0
				},
				'legend' : 'none'
			},
			'view' : {
				'columns' : [ 0, 1 ]
			}
		});

		var areaChart = new google.visualization.ChartWrapper({
			'chartType' : 'AreaChart',
			'containerId' : 'areaChart_div',
			'dataTable' : data,
			'title' : '',
			'options' : {
				// Use the same chart area width as the control for axis alignment.
				'chartArea' : {
					'height' : '80%',
					'width' : '90%'
				},
				'hAxis' : {
					'slantedText' : false,
					'direction' : -1
				//'title':'Date'
				},
				'vAxis' : {
					'viewWindow' : {
						'min' : 0,
						'max' : 20
					},
					'title' : 'Requests'
				},
				'legend' : {
					'position' : 'none'
				},
				'pointSize' : 5,
				'colors' : [ 'red', '#004411' ],
				'pointShape' : 'circle'
			},
			// Convert the first column from 'date' to 'string'.
			'view' : {
				'columns' : [ {
					'calc' : function(dataTable, rowIndex) {
						return dataTable.getFormattedValue(rowIndex, 0);
					},
					'type' : 'string'
				}, 2 ]
			}
		});

		pieChart.draw()

		google.visualization.events.addListener(pieChart.getChart(), 'select',
				function() {
					var selectedItem = pieChart.getChart().getSelection()[0];
					var value;
					if (selectedItem) {
						value = grouped_data.getValue(selectedItem.row, 0);
					}
					appSelectorFilter.setState({
						'selectedValues' : [ value ]
					});
					ColumnChartTotalRequests.getChart().setSelection(pieChart.getChart().getSelection());
					areaChart.setOption('title', value);
					areaChart.draw();
					appSelectorFilter.draw();
				});
	
		google.visualization.events.addListener(ColumnChartTotalRequests.getChart(), 'select',
				function() {
					var selectedItem = ColumnChartTotalRequests.getChart().getSelection()[0];
					var value;
					if (selectedItem) {
						value = grouped_data.getValue(selectedItem.row, 0);
					}
					appSelectorFilter.setState({
						'selectedValues' : [ value ]
					});
					pieChart.getChart().setSelection(ColumnChartTotalRequests.getChart().getSelection());
					areaChart.setOption('title', value);
					areaChart.draw();
					appSelectorFilter.draw();
				});

		// Establish dependencies, declaring that 'filter' drives 'pieChart',
		// so that the pie chart will only display entries that are let through
		// given the chosen slider range.
		dashboard.bind([ dateRangeFilter, requestsPerDayPicker ],
				[ completeTable ]);
		dashboard.bind([ chartRangeFilterControl, appSelectorFilter ],
				areaChart);
		dashboard.draw(data);
	}
</script>
</head>
<body>
	<div id="dashboard_div"
		style="border: 1px solid rgb(204, 204, 204); margin-top: 1em; position: relative;"padding-left: 1em">
		</p>
		<table class="columns">
			<tbody>
				<tr>
					<td colspan="4">
						<div id="ColumnChartTotalRequests_div" style="position: relative;"></div>
					</td>
				</tr>
				<tr>
					<td valign=center><font face="Sans-serif">Total Number
							of Calls made by an App</font>
						<div id="pieChart_div" style="position: relative;"></div></td>
					<td valign=center><font face="Sans-serif">Date</font>
						<div id="dateRangeFilter_div"
							style="padding-left: 0px; position: relative;"></div></td>
					<td valign=center><font face="Sans-serif">Requests</font>
						<div id="requestsPerDayPicker_div" style="position: relative;"></div>
					</td>
					<td valign=center>
						<div id="completeTable_div" style="position: relative;"></div>
					</td>
				</tr>
				<tr>
					<td colspan="4">
						<div id="areaChart_div"
							style="width: 915px; height: 300px; position: relative;"></div>
					</td>
				</tr>
				<tr>
					<td colspan="4">
						<div id="chartRangeFilterControl_div"
							style="width: 915px; height: 50px; position: relative;"></div>
					</td>
				</tr>
				<tr>
					<td colspan="4">
						<div id="appSelector_div" style="display: none"></div>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	
	<%
    }
	%>
</body>
</html>
