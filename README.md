# SailPoint IIQ Demos

This repository contains the necessary files to spin up a pre-configured VM with ready-made SailPoint Demos to be shown to clients.

## Setup
To begin the Vagrant set up, clone the IIQ Demos Git repository to your desired directory on your local machine and run the ​​command 'vagrant up' in your terminal window. Your machine should automatically begin downloading the IIQ7.1p2_Demos.box. 
Since this is a ~16GB box, the download will should take 10-20 minutes. Once the download is finished, Vagrant will begin to boot the VM.​ During the boot sequence you will see the following lines of output in the terminal:

  . . . <br>
  default: SSH username: vagrant <br>
  default: SSH auth method: private key <br>
  default: Warning: Connection reset. Retrying. . . <br>
  default: Warning: Connection aborted. Retrying. . . <br>
  . . .

where the last two lines are output repeatedly. This is due to Vagrant trying to establish an SSH connection, which is not configured on this Windows box. Eventually the terminal output will indicate a timeout. 
At this point the VM should be up and running (sans SSH capabilities). To verify visit: http://localhost:8080/identityiq/login.jsf?prompt=true​ and login to SailPoint with the standard spadmin credentials (spadmin/admin).

## Relevant Pages
With the Vagrantized version of the Demos VM, you can execute all Use-Cases from the browser on your host machine.

SailPoint URL: http://localhost:8080/identityiq/login.jsf?prompt=true <br>
OrangHRM URL: http://localhost:8081/orangehrm/symfony/web/index.php/auth/login

## Credentials
SailPoint: spadmin/admin <br>
OrangeHRM: admin/sailpoint <br>
MYSQL: 	   root/sailpoint <br>

## Use Cases and Demo Execution
Refer to the following files for more specific information on carrying out the demos for clients.
<br>
Demo Script: https://silasg365.sharepoint.com/:w:/s/EnablementTeam/Eb9HBhG1K5ZHk0FtKWITP3kBcmZopQKCAOjagePrMkaaYg?e=eToZSi <br>
Slide Deck: https://silasg365.sharepoint.com/:p:/s/EnablementTeam/EVFcZt85HoBIqQT4YS0h7vMB8OVDt8SogePJnXprAegy7A?e=Zh9BgM 