# Join Notifier Discord Bot
## Contents
 - [Overview](#overview) 
 - [Host server setup](#host-server-setup) 
 - [Building the application](#building-the-application-to-fork-it-or-whatever)
 - [Running in Docker](#running-in-docker)
 - [DNS Issue and solution](#dns-issue-and-solution)
## Overview
The Join Notifier Discord Bot's main functionality is to simply message in a text channel when a user joins or leaves a voice channel. That way, server members will receive a notification from the text channel that someone joined or left the video channel.

As of this writing, the minimum viable product (MVP) is done. Join Notifier will message what user joins or leaves ANY audio channel in the specified text channel. The text channel ID will be specified on the server hosting Join Notifier as an environment variable.

## Host server setup
The host server must have Java 17 or later installed in order to run Join Notifier.

In order to get Join Notifier working you must setup the following environment variables on the machine it will run on:
|Name|Description|
|----|-------------|
|JOIN_NOTIFIER_TEXT_CHANNEL_ID|You can get this ID by right clicking the text channel you want join notifier to message in.|
|JOIN_NOTIFIER_TOKEN|You'll have to get this when you create your custom app in the Discord Developer Portal (https://discord.com/developers).|
|JOIN_NOTIFIER_LOGS|Set the path you want to use for the log directory on the host machine. This variable is used in the compose.yaml. An example path, for Linux, is the following: ``/opt/join-notifier/logs``|

Then, since Join Notifier is a Spring Boot application, you can run it using the following command in a console:
```sh
java -jar join-notifier-<version>.jar
```

For example:
```sh
java -jar join-notifier-0.0.1.jar
```

If you have Apache Maven installed, you could run it with the following command:
```sh
mvn spring-boot:run
```

## Building the application (to fork it or whatever)
1. You must have Apache Maven (https://maven.apache.org/) installed and setup properly on your machine.
2. You must have Java 17 (https://openjdk.org/) or later installed and setup on your machine.
3. You need to setup the environment variables specified in the Host server setup section above (unless you modify the code to not use those environment variables)
4. Run the following Apache Maven command:
    ```sh
    mvn clean package
    ```

## Running in Docker
> [!NOTE]
> Reference the [DNS Issue and solution](#dns-issue-and-solution) section if you cannot seem to connect to ``discord.com`` due to a DNS issue.
1. Install Docker on your machine (https://www.docker.com/). 
2. Build the image from the Dockerfile contained in the root directory of this repository. See below for an example command:
   ```sh
   sudo docker build --tag=discord-join-notifier:latest
   ```
> [!NOTE]
> If you did not change the ``Dockerfile``, you must build the docker image in the same directory of where you checked out this codebase since the ``Dockerfile`` needs the JAR built in the ``target`` directory.
3. Run the container using the image you created. See below for Docker Compose and Docker Run commands in a Linux terminal which include environment variables passed from the bare metal host machine:

    ```sh
    # Make sure to run this command in your join-notifer directory. It uses the compose.yaml
    # the -d argument will make join-notifier run detached
    sudo docker compose -d
    ```
    or if you want to just use Docker Run:
    ```sh
    sudo docker run -d -e JOIN_NOTIFIER_TEXT_CHANNEL_ID=$JOIN_NOTIFIER_TEXT_CHANNEL_ID \
    -e JOIN_NOTIFIER_TOKEN=$JOIN_NOTIFIER_TOKEN \
    -p 80:3000 \
    --name join-notifier discord-join-notifier
    ```
    or if you want to use Docker Run and always have the container restarted:
    ```sh
    sudo docker run -d -e JOIN_NOTIFIER_TEXT_CHANNEL_ID=$JOIN_NOTIFIER_TEXT_CHANNEL_ID \
    -e JOIN_NOTIFIER_TOKEN=$JOIN_NOTIFIER_TOKEN \
    -p 80:3000 \
    --name join-notifier --restart=always discord-join-notifier
    ```
    
## DNS Issue and solution
An issue may occur with being able to resolve ``discord.com`` from your Docker container if you happen to have your DNS server within another Docker container on the same host as Join Notifier. I personally had this problem on a Raspberry Pi 5 when hosting Join Notifier in a docker container as well as Pi-Hole (as my DNS server as well) in a docker container.

To resolve this problem, follow the below steps (found from here: https://unix.stackexchange.com/questions/647996/docker-container-dns-not-working-with-pihole):
1. If you do not have ``openresolv`` installed, run the following command:
    ```sh
    sudo apt-get install openresolv
    ```
2. Open the ``/etc/resolvconf.conf`` file and uncomment the following line:
    ```
    # If you run a local name server, you should uncomment the below line and
    # configure your subscribers configuration files below.
    name_servers=127.0.0.1
    ```
3. Next, run the following command:
    ```sh
    sudo resolvconf -u
    ```
> [!NOTE]
> This command will update your ``/etc/resolv.conf`` to have ``127.0.0.1``(locahost) as the nameserver.
4. Your docker containers should now be able to use your localhost as the DNS server.