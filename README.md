# SensaGram

<img src="https://github.com/umer0586/SensaGram/blob/main/app/src/main/ic_launcher-playstore.png" width="200">

![GitHub License](https://img.shields.io/github/license/umer0586/SensaGram?style=for-the-badge) ![Static Badge](https://img.shields.io/badge/Android-5.0%2B-blue?style=for-the-badge) ![Static Badge](https://img.shields.io/badge/Jet%20Pack%20Compose-blue?style=for-the-badge) ![Static Badge](https://img.shields.io/badge/protocol-UDP-teal?style=for-the-badge)






### Stream real-time Android sensor data over UDP (User Datagram Protocol), a connectionless protocol. In contrast, [SensorServer](https://github.com/umer0586/SensorServer) uses WebSocket, which relies on TCP, a connection-oriented protocol. 

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

Running a UDP server in Python is straightforward. Below is a code snippet you can use to receive data from the Sensagram app. The address `0.0.0.0` means the server will listen on all available network interfaces. 

Run this script, then enter the IP address of the machine running the script in the app's settings. You can find your device's IP address by using `ipconfig` on Windows or `ifconfig` on Linux.

```python
import socket
import json


def udp_server(server_address = ('0.0.0.0', 8080), buffer_size = 1024):
   
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind(server_address)
    print("server started")

    while True:
        
        data, address = sock.recvfrom(buffer_size)
        jsonData = json.loads(data)

        timestamp = jsonData["timestamp"]
        values = jsonData["values"]
        sensor_type = jsonData["type"]

        print(f"{sensor_type} {values} {timestamp}")
        
    sock.close()


udp_server()

```

## Installation
Download apk from [release](https://github.com/umer0586/SensaGram/releases) section. Not available on F-Droid yet

