# improving-app-ui-gateway
The User Interface Gateway for the improving.app implemented as a Kalix Action API

# Overview
This gateway provides the API that the user interface uses to communicate with 
the back end of the application. One of its primary purposes is to encapsulate
the back end services so that the user interface code is isolated from changes
in the services or their APIs. All such translations occur within this UI 
Gateway API. 