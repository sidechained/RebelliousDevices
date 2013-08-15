# Two Way OSC Test Between SuperCollider and Python
# - Python file

# -- sends '\led 0' or '\led 1' messages every second
# -- receives '\led' messages and prints to the screen
# -- lines marked with '# uncomment on BBB' are intended for use in the next stage

# -- requires pyOSC library @ https://trac.v2.nl/wiki/pyOSC
# -- to install, run '$ sudo python setup.py install' from inside the pyOSC directory (avoids having to set the PYTHONPATH for each session)
# -- to check installation, look inside site-packages directory, using path from this command: >>> import site; site.getsitepackages()
# -- note: pip is recommended over setup.py, see: http://stackoverflow.com/questions/15724093/difference-between-python-setup-py-install-and-pip-install

# -- this file is based on basic test script from here: http://pastebin.com/EScysgys
# -- a better example is probably here: http://pastebin.com/FFAJtbTQ


import socket
import OSC
from OSC import OSCClient, OSCMessage
import random, time, threading

# global vars
bbbExists = "thing"
pythonActuateOscPath = "/python/actuate/led"
pythonSenseRequestOscPath = "/python/sense/request/ldr"
pythonSenseReturnOscPath = "/python/sense/return/ldr"
receiveAddress = '127.0.0.1', 10000
sendAddress = '127.0.0.1', 10001
server = OSC.OSCServer(receiveAddress)
client = OSCClient()

def parseCommandLineOptions():
	global bbbExists
	# for info on use see: http://docs.python.org/2/library/optparse.html
	from optparse import OptionParser
	parser = OptionParser()
	parser.add_option("-n", "--noBBB", action="store_false", default = True, dest="bbbExists", help="ignore BBIO commands if testing without BeagleBone Black")
	(options, args) = parser.parse_args()
	bbbExists = options.bbbExists
	if bbbExists == True:
		print "Assuming BeagleBone Black is present...please ensure it is connected"
	else:
		print "Assuming BeagleBone Black is not present...will simulate and print values for testing"
		
def setupBBIO():
	import Adafruit_BBIO.GPIO as GPIO
	GPIO.setup("P8_10", GPIO.OUT)
	import Adafruit_BBIO.ADC as ADC
	ADC.setup()

def actuateLed(addr, tags, msg, source):
	ledState = msg[0]
	print "receiving msg: '{0}' '{1}'".format(addr, ledState)
	if bbbExists == True:
		if ledState == 1:
			GPIO.output("P8_10", GPIO.HIGH)
		elif ledState == 0:
			GPIO.output("P8_10", GPIO.LOW)

def senseLdr(addr, tags, msg, source):
	if bbbExists == True:
		ldrVal = ADC.read("P9_40")
	else:
		ldrVal = random.uniform(0, 1)
	print "sending msg: '{0}' '{1}'".format(pythonSenseReturnOscPath, ldrVal)
	client.send( OSCMessage( pythonSenseReturnOscPath, ldrVal ) )

def initServer():
	print "\nStarting OSCServer. Use ctrl-C to quit."
	server.addDefaultHandlers() # registers 'default' handler (for unmatched messages + more)
	server.addMsgHandler(pythonActuateOscPath, actuateLed)
	server.addMsgHandler(pythonSenseRequestOscPath, senseLdr)
	st = threading.Thread( target = server.serve_forever )
	st.start()

def initClient():
	client.connect( (sendAddress) )

parseCommandLineOptions()
if bbbExists == True:
	setupBBIO()
initServer()
initClient()

try : 
     while 1 : 
         time.sleep(1) 

except KeyboardInterrupt :
    print "\nClosing OSCServer."
    server.close()
    print "Waiting for Server-thread to finish"
    st.join() ##!!!
    print "Done"