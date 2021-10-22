import paho
import serial.tools.list_ports
import random
import time
import  sys
import paho.mqtt.client as mqtt
from  Adafruit_IO import  MQTTClient

#
# AIO_FEED_ID = ""
# AIO_USERNAME = ""
# AIO_KEY = ""
#
# def  connected(client):
#
#     client.subscribe(AIO_FEED_ID)

def on_connect(client, userdata, flags, rc):
    print("Ket noi thanh cong...")
    print("Connected with result code "+str(rc))
    client.subscribe("TEMP")

def message(client, userdata, msg):
    print("Nhan du lieu: " + str(msg))
    ser.write((str(msg) + "#").encode())

def subscribe(client, userdata, flags, rc):
    print("Subcribe thanh cong...")

def disconnect(client):
    print("Ngat ket noi...")
    sys.exit (1)

client = mqtt.Client("P1")
client.connect("mqtt.eclipseprojects.io", 1883, 60)
client.on_connect = on_connect
client.on_disconnect = disconnect
client.on_message = message
client.on_subscribe = subscribe


client.loop_start()



client.loop_stop()
#
# def  subscribe(client , userdata , mid , granted_qos):
#     print("Subcribe thanh cong...")
#
# def  disconnected(client):
#     print("Ngat ket noi...")
#     sys.exit (1)
#
# def  message(client , feed_id , payload):
#     print("Nhan du lieu: " + payload)
#     ser.write((str(payload) + "#").encode())
#
# client = MQTTClient(AIO_USERNAME , AIO_KEY)
# client.on_connect = connected
# client.on_disconnect = disconnected
# client.on_message = message
# client.on_subscribe = subscribe
# client.connect()
# client.loop_background()

def getPort():
    ports = serial.tools.list_ports.comports()
    N = len(ports)
    commPort = "COM2"
    for i in range(0, N):
        port = ports[i]
        strPort = str(port)
        if "USB Serial Device" in strPort:
            splitPort = strPort.split(" ")
            commPort = (splitPort[0])
    return commPort

ser = serial.Serial( port=getPort(), baudrate=115200)

mess = ""
def processData(data):
    data = data.replace("!", "")
    data = data.replace("#", "")
    splitData = data.split(":")
    print(splitData)
    if splitData[1] == "TEMP":
        client.publish("TEMP    ", splitData[2])

mess = ""
def readSerial():
    bytesToRead = ser.inWaiting()
    if (bytesToRead > 0):
        global mess
        mess = mess + ser.read(bytesToRead).decode("UTF-8")
        while ("#" in mess) and ("!" in mess):
            start = mess.find("!")
            end = mess.find("#")
            processData(mess[start:end + 1])
            if (end == len(mess)):
                mess = ""
            else:
                mess = mess[end+1:]

while True:
    readSerial()
    time.sleep(1)