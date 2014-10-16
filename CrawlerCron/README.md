#Requirement 

To integrate this module on your server, make sure that Movesmarter server has the IP address of the server. Note that at Movesmarter server, they always check the IP address when app connects to their server. Contact them to get access permission.

# Crawler features

1. Crawls the public social information about a user. The public information extracted include current address, gender and reviews done by the user.
2. Private Mobility related information is exracted from MoveSmarter Platform. This includes extraction of Personal Places, Radius, Regular trips, Accompany details.
3. Pulls public information at 3am.
4. No Direct API access to the component. 
5. The component connects to other components in the 3cixty Platform via KB. 
6. User Specific History of when the job was last executed successfully for the user.
7. `Accompany` uses `3cixty IDs (google IDs)` to related people.
8. inconsistent data field in `json` format output is handled.
9. Multiple `inferences` using the mobility data and personal data are made. These include `Preferred Trip Distance`, `Preferred Trip Time`, `Preferred Trip Modality`, `Preferred City`, `Preferred Country`, `preferred weather conditions`.
10. The crawler doesnot extract the private social information.

#Notes:

- Private information of the user like `knows` is extracted when the user logs in into 3cixty platform. To get `knows` users should authenticate the app to get list of people in a `circle` information. To do so, at the time of login a user should click `Edit list`. 

![alt tag](https://github.com/3cixty/profiler/blob/scrapper/CrawlerCron/images/login.png)
 
- Then select the `circles` the user wants the app to get information from. The image below shows how to select all circles

![alt tag](https://github.com/3cixty/profiler/blob/scrapper/CrawlerCron/images/login2.png)

- Then click `Ok` to return to previous screen. It is also important to select `only you`. Then click `Accept`.
