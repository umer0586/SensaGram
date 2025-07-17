<div align="center">
 
# SensaGram

 
<img src="https://github.com/umer0586/SensaGram/blob/main/app/src/main/ic_launcher-playstore.png" width="200">

[<img src="https://github.com/user-attachments/assets/0f628053-199f-4587-a5b2-034cf027fb99" height="100">](https://github.com/umer0586/SensaGram/releases) [<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="100">](https://f-droid.org/packages/com.github.umer0586.sensagram/)

![GitHub License](https://img.shields.io/github/license/umer0586/SensaGram?style=for-the-badge) ![Android Badge](https://img.shields.io/badge/Android-5.0+-34A853?logo=android&logoColor=fff&style=for-the-badge) ![Jetpack Compose Badge](https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=fff&style=for-the-badge) ![Static Badge](https://img.shields.io/badge/protocol-UDP-teal?style=for-the-badge) ![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/umer0586/SensaGram/build.yml?style=for-the-badge&logo=appveyor)
 ![GitHub Release](https://img.shields.io/github/v/release/umer0586/SensaGram?include_prereleases&style=for-the-badge)

### Stream real-time Android sensor data using [UDP (User Datagram Protocol)](https://en.wikipedia.org/wiki/User_Datagram_Protocol), a connectionless transport layer protocol designed for low-latency, fast data transmission without establishing a persistent connection.

<img src="https://github.com/user-attachments/assets/0f8476cd-add4-4f19-8124-64db871e2e9b" width="250">
<img src="https://github.com/user-attachments/assets/098bc959-ab34-449a-90d8-cdfdc1056e83" width="250">
<img src="https://github.com/user-attachments/assets/bab0c973-4f08-4bfc-bae9-ac8acf3202ae" width="250">
<img src="https://github.com/user-attachments/assets/54cb7935-4306-4c69-a6b6-24a195345a3a" width="250">

</div>

## Usage
In the app, select the desired sensors from the list and tap the "Stream" button. This will begin transmitting sensor data to the specified address. To receive the data, you'll need to set up a UDP server. The app sends the data in JSON format.

Note : _**You can dynamically control the data stream by selecting or deselecting sensors from the list during active streaming.This allows for flexible management of sensor data transmission.**_

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
    timestamp = jsonData.get("timestamp")
    values = jsonData.get("values")
    
    if sensorType == "android.sensor.accelerometer":
        x, y, z = values
        print(f"accelerometer : x = {x}, y = {y}, z = {z} timestamp = {timestamp} ")
    
    if sensorType == "android.sensor.gyroscope":
        x, y, z = values
        print(f"gyroscope : x = {x}, y = {y}, z = {z} timestamp = {timestamp} ")

    if sensorType == "android.gps":
        longitude, latitude, altitude = jsonData["longitude"], jsonData["latitude"], jsonData["altitude"]
        bearing, accuracy, speed, time	= jsonData["bearing"], jsonData["accuracy"], jsonData["speed"], jsonData["time"]
        
        print(f"longitude = {longitude} latitude = {latitude} altitude = {altitude}")
        
        # Fields only for Android 8.0 and above.
        speedAccuracyMetersPerSecond = jsonData.get("speedAccuracyMetersPerSecond")
        bearingAccuracyDegrees = jsonData.get("bearingAccuracyDegrees")
        elapsedRealtimeNanos = jsonData.get("elapsedRealtimeNanos")
        verticalAccuracyMeters = jsonData.get("verticalAccuracyMeters")
                

# Initialize the server to listen on all network interfaces (0.0.0.0) and port 8080
server = UDPServer(address=("0.0.0.0", 8080))
server.setDataCallBack(onData)
server.start()

```
for GPS data description see [GPS Data Description](https://github.com/umer0586/SensaGram/wiki/GPS-Data-Description)

#### 3 Run the script on the machine you want to receive data on:
```bash
python server.py
```
⚠️ Make sure your firewall settings are not blocking incoming data. see issue [Firewall caused errors](https://github.com/umer0586/SensaGram/issues/6)  

#### 4. Configure the Sensagram app:
In the app's settings, enter the IP address of the machine running this script. To find your machines's IP address:
- On **Windows**, use the `ipconfig` command.
- On **Linux**, use the `ifconfig` command.

## Stream On Boot
The app can stream sensor data to a specified address upon device boot. For devices running Android 9 or lower, the app automatically enables Wi-Fi on boot and starts the data stream. However, for devices running Android 10 and later, apps cannot directly control Wi-Fi settings. Therefore, you need to ensure your device's Wi-Fi is enabled before shutting down or restarting. This allows the system to automatically restore the Wi-Fi state when the device boots up. Additionally, make sure the app is not restricted from performing background activities, which you can adjust through your device's system settings.

### Video Demo
A demonstration of Sensagram's sensor data streaming capabilities via UDP, utilizing a Python script within Blender to map phone orientation and dynamically control the rotation of a 3D object. See [rotate.py](https://gist.github.com/a5b1247b1999848fe16dda340335dfe6.git)

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/IxHCX9Im31A/0.jpg)](https://www.youtube.com/watch?v=IxHCX9Im31A)



