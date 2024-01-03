# Join Notifier Discord Bot
## Contents
 - [Overview](#overview) 
 - [Host server setup](#host-server-setup) 
 - [Building the application](#building-the-application-to-fork-it-or-whatever)
## Overview
The Join Notifier Discord Bot's main functionality is to simply message in a text channel when a user joins or leaves a voice channel. That way, server members will receive a notification from the text channel that someone joined or left the video channel.

As of this writing, the minimum viable product (MVP) is done. Join Notifier will message what user joins or leaves ANY audio channel in the specified text channel. The text channel ID will be specified on the server hosting Join Notifier as an environment variable.

## Host server setup
The host server must have Java 17 or later installed in order to run Join Notifier.

In order to get Join Notifier working you must setup the following environment variables on the machine it will run on:
|Name|Description|
|----|-------------|
|JOIN_NOTIFIER_TEXT_CHANNEL_ID|You can get this ID by right clicking the text channel you want join notifier to message in|
|JOIN_NOTIFIER_TOKEN|You'll have to get this when you create your custom app in the Discord Developer Portal|

Then, since Join Notifier is a Spring Boot application, you can run it using the following command in a console:

``java -jar join-notifier-<version>.jar``

For example:

``java -jar join-notifier-0.0.1.jar``

If you have Apache Maven installed, you could run it with the following command:

``mvn spring-boot:run``

## Building the application (to fork it or whatever)
1. You must have Apache Maven installed and setup properly on your machine.
2. You must have Java 17 or later installed and setup on your machine.
3. You need to setup the environment variables specified in the Host server setup section above (unless you modify the code to not use those environment variables)
4. Run the following Apache Maven command:
    
    ``mvn clean package``


