Call Logs and Call Log Display
==============================

##Call Log:

The module is used to store api calls made by an application.

Features include:

- log of time, api, status, duration taken to execute, app that made the call.


##Call Log Display:

Is the visual reprentation of the call logs. This helps 3cixty Adminstrators to track the api/app usage.

To display call logs `callLogService` is used. 

The 3cixty Admin logs into the platform using baseurl+`/appkeyadmin_login.jsp` and provides his credentials. The default username is `3cixtyAdmin` and password is `*3cixtyI$InMilan*`.

On a successful login the Admin can view the dashboard page with features listed below

- uses Google Visualization api and google charts
- slider to chose Request/day for an app
- highlight on table data
- data view on pie chart 
- selection based on number of calls made
- when an item is selected the pieChart, it changes the areachart.
- availability to the admin of 3cixty.
- hover over to see the tooltip.
- logout feature.

snapshot of the dashboard:
