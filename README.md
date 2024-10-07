# SensaGram (Under progress)

<img src="https://github.com/umer0586/SensaGram/blob/main/app/src/main/ic_launcher-playstore.png" width="200">

![GitHub License](https://img.shields.io/github/license/umer0586/SensaGram?style=for-the-badge) ![Static Badge](https://img.shields.io/badge/Android-5.0%2B-blue?style=for-the-badge) ![Static Badge](https://img.shields.io/badge/Jet%20Pack%20Compose-blue?style=for-the-badge) ![Static Badge](https://img.shields.io/badge/protocol-UDP-teal?style=for-the-badge)






### Stream Android sensors over UDP (User Datagram Protocol) with low latency 

<img src="https://github.com/user-attachments/assets/0f8476cd-add4-4f19-8124-64db871e2e9b" width="250">
<img src="https://github.com/user-attachments/assets/82598003-610a-4b22-92b3-560dca22e503" width="250">
<img src="https://github.com/user-attachments/assets/bab0c973-4f08-4bfc-bae9-ac8acf3202ae" width="250">
<img src="https://github.com/user-attachments/assets/54cb7935-4306-4c69-a6b6-24a195345a3a" width="250">

# Usage
In the app, select the desired sensors from the list and tap the "Stream" button. This will begin transmitting sensor data to the specified address. To receive the data, you'll need to set up a UDP server. The app sends the data in JSON format.

```json
{
 "type": "android.sensor.accelerometer",
 "timestamp": 3925657519043709,
 "values": [0.31892395,-0.97802734,10.049896]
}
```

# Python UDP server example

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
