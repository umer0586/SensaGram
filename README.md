# SensaGram

<img src="https://github.com/umer0586/SensaGram/blob/main/app/src/main/ic_launcher-playstore.png" width="200">

![GitHub License](https://img.shields.io/github/license/umer0586/SensaGram?style=for-the-badge) ![Android Badge](https://img.shields.io/badge/Android-5.0+-34A853?logo=android&logoColor=fff&style=for-the-badge) ![Jetpack Compose Badge](https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=fff&style=for-the-badge) ![Static Badge](https://img.shields.io/badge/protocol-UDP-teal?style=for-the-badge) 






#### Stream real-time Android sensor data using [UDP](https://en.wikipedia.org/wiki/User_Datagram_Protocol) (User Datagram Protocol), a connectionless transport layer protocol that provides low latency. In comparison, the [SensorServer](https://github.com/umer0586/SensorServer) project utilizes WebSockets, which operate over [TCP](https://en.wikipedia.org/wiki/Transmission_Control_Protocol) (Transmission Control Protocol), a connection-oriented protocol that typically incurs higher latency due to its overhead for ensuring reliable data transmission.

<img src="https://github.com/user-attachments/assets/0f8476cd-add4-4f19-8124-64db871e2e9b" width="250">
<img src="https://github.com/user-attachments/assets/82598003-610a-4b22-92b3-560dca22e503" width="250">
<img src="https://github.com/user-attachments/assets/bab0c973-4f08-4bfc-bae9-ac8acf3202ae" width="250">
<img src="https://github.com/user-attachments/assets/54cb7935-4306-4c69-a6b6-24a195345a3a" width="250">

## Usage
In the app, select the desired sensors from the list and tap the "Stream" button. This will begin transmitting sensor data to the specified address. To receive the data, you'll need to set up a UDP server. The app sends the data in JSON format.

```json
{
 "type": "android.sensor.accelerometer",
 "timestamp": 3925657519043709,
 "values": [0.31892395,-0.97802734,10.049896]
}
```

![axis_device](https://user-images.githubusercontent.com/35717992/179351418-bf3b511a-ebea-49bb-af65-5afd5f464e14.png)

where

| Array Item  | Description |
| ------------- | ------------- |
| values[0]  | Acceleration force along the x axis (including gravity)  |
| values[1]  | Acceleration force along the y axis (including gravity)  |
| values[2]  | Acceleration force along the z axis (including gravity)  |

And [timestamp](https://developer.android.com/reference/android/hardware/SensorEvent#timestamp) is the time in nanoseconds at which the event happened

Use `JSON` parser to get these individual values.

 
**Note** : *Refer to the following official Android documentation links to understand what each value in the **values** array represents.*
- For motion sensors [/topics/sensors/sensors_motion](https://developer.android.com/guide/topics/sensors/sensors_motion)
- For position sensors [/topics/sensors/sensors_position](https://developer.android.com/guide/topics/sensors/sensors_position)
- For Environmental sensors [/topics/sensors/sensors_environment](https://developer.android.com/guide/topics/sensors/sensors_environment)

## Python example

The following Python code allows you to receive sensor data from the Sensagram app. The server will listen on all available network interfaces, which is indicated by the address `0.0.0.0`. To set up the server, follow these steps:

#### 1. Clone the Gist repository:
see : [udpserver.py](https://gist.github.com/umer0586/1331ac524c525bae7b1c94667ed571de)
```bash
git clone https://gist.github.com/umer0586/1331ac524c525bae7b1c94667ed571de example
cd example
```

#### 2. Create a Python file (e.g., server.py) in the example directory and add the following code:

```python
from udpserver import UDPServer
import json

def onData(data):
    jsonData = json.loads(data)
    sensorType = jsonData["type"]
    timestamp = jsonData["timestamp"]
    values = jsonData["values"]
    print(f"{sensorType} {values} {timestamp}")

# Initialize the server to listen on all network interfaces (0.0.0.0) and port 8080
server = UDPServer(address=("0.0.0.0", 8080))
server.setDataCallBack(onData)
server.start()
```
#### 3 Run the script on the machine you want to receive data on:
```bash
python server.py
```
#### 4. Configure the Sensagram app:
In the app's settings, enter the IP address of the machine running this script. To find your machines's IP address:
- On **Windows**, use the `ipconfig` command.
- On **Linux**, use the `ifconfig` command.


## Installation
Download apk from [release](https://github.com/umer0586/SensaGram/releases) section. Not available on F-Droid yet

