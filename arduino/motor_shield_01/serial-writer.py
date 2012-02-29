import serial, time

ser = serial.Serial('/dev/tty.usbmodem1a21',9600)
print ser.portstr
while True:
  ser.write('\x05')
  print 'high'
  print ser.read()
  time.sleep(2)
  ser.write('\x00')
  print 'low'
  print ser.read()
  time.sleep(2)

ser.close()
