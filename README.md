After cloning and hosting the database infrastructure on cloud, you have to update the back-end url to send the API requests.

When you define the URL to send API Requests to the backend,

Connecting to local database uses the IP address 10.0.2.2 .
(E.g. URL = http://10.0.2.2:8081/api/v1/expenses)

Connecting to the cloud database uses the DNS name: caa900debtsolverappbe.eastus.cloudapp.azure.com .
(E.g. URL = http://caa900debtsolverappbe.eastus.cloudapp.azure.com:8081/api/v1/expenses

If the DNS name does not work, then switch to use the IP address of the hosted DB. 
(E.g. http://74.235.241.67:30001/api/v1/expenses)

Note: 
If you are using a new DNS name or IP address, you must update "network_security_config.xml" file.
It is located at the path app/src/main/res/xml.

Files that need to be modified for the URL:
ExpenseManagementUserRepository
UserRepository
LoginViewModel
RegisterViewModel

Once you are done updating the URL address, run the android app using android studio. It may take some time as it is installing and building the gradle files.
