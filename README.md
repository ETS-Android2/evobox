# EVOBOX

![Image 1: General Description of EVOBOX](https://i.imgur.com/Ni1lq3a.jpg) 

EVOBOX; as my gradution project, is a smart medicine box that reminds people about their medicines and can be controlled via a mobile application.

After the user puts the pill in the box, he/she adds the pill information via the mobile application.
The selected number of pills falls into the pill glass at the selected time. If the pill is not taken, the user is notified via the mobile application.

The project consists of three layers: Arduino, Web API and Android application.
All database connections are made in WebAPI written in NodeJS. The Android application communicates it by making HTTP requests to the WebAPI.

Used Firebase Admin SDK to send notifications.
Used MySQL database.


![Image 2: Physical Prototype of EVOBOX](https://i.imgur.com/jF6WF8q.png) 
