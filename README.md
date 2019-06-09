# Send Mail Client Server Demonstration

* All the pre build project jars are inside the **_dist_** directory

### Step 1:
Start the Fake SMTP server

`java -jar fake-smtp/fakeSMTP-2.0.jar -o fake-smtp/mails -p 25`

### Step 2:
Start the Email Sender App (Server side) with default settings

Default settings can be found below for

`java -jar dist/email-sender-1.0-SNAPSHOT.jar`

### Step 3
Start the Email Sender Client

`java -DTHREAD_COUNT=8 -DREQUEST_COUNT=40 -jar dist/mail-client-1.0-SNAPSHOT.jar`

You can change the Arguments THREAD_COUNT and REQUEST_COUNT in the Client app

## Build From the Source
run `mvn -DskipTests=true package`

This will create the required jars in respective target directories: _email-sender_ and _mail-client_

### Additional Configurations Available with default Values

#### Send Mail Server
* thread count: `THREAD_COUNT`: 4 : _controlling this will control the load on SMTP server_
* service port: `SERVICE_PORT`: 4444 
* mail server host: `MAIL_SERVER_HOST`: 127.0.0.1
* mail server port: `MAIL_SERVER_PORT`: 25

#### Send Mail Client
* service host: `SERVICE_HOST`: 127.0.0.1
* service port: `SERVICE_PORT`: 4444

# Suggested Improvements

* Data validation for mail requests
* Cache the submitted request ID values in memory, or in a DB and check for non existence
 to handle duplicate submissions
* Facility to send mails ro multiple recipients (already supported in Javax Mail)

# Open The Project
Project can be open in Eclipse as
_`File -> Import -> Maven -> Existing Maven Projects -> Browse the Directory with parent pom`_

