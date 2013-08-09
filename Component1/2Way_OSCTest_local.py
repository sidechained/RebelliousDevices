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

# import Adafruit_BBIO.GPIO as GPIO # uncomment on BBB
# GPIO.setup("P8_10", GPIO.OUT) # uncomment on BBB
 
# tupple with ip, port. i dont use the () but maybe you want -> send_address = ('127.0.0.1', 9000)
receive_address = '127.0.0.1', 9000
 
# OSC Server. there are three different types of server.
server = OSC.OSCServer(receive_address) # basic
##server = OSC.ThreadingOSCServer(receive_address) # threading
##server = OSC.ForkingOSCServer(receive_address) # forking
 
# this registers a 'default' handler (for unmatched messages),
# an /'error' handler, an '/info' handler.
# And, if the client supports it, a '/subscribe' & '/unsubscribe' handler
server.addDefaultHandlers()
 
# define two message-handler functions for the server to call (ledOn and ledOff).
def led_handler(addr, tags, stuff, source):
	ledstate = stuff[0]
	print "receiving msg: '{0}' '{1}'".format(addr, ledstate)
	if ledstate == 1:
		None
		# GPIO.output("P8_10", GPIO.HIGH) # uncomment on BBB
	else:
		None
		# GPIO.output("P8_10", GPIO.LOW) # uncomment on BBB
		
server.addMsgHandler("/led", led_handler) # adding our function
  
# Start OSCServer
print "\nStarting OSCServer. Use ctrl-C to quit."
st = threading.Thread( target = server.serve_forever )
st.start()

# sending
client = OSCClient()
client.connect( ("localhost", 57121) )

try :
    while 1 :
    	# sending continued
    	msg = "/led"
    	randval = random.randint(0, 1)
    	print "sending msg: '{0}' '{1}'".format(msg, randval)
    	client.send( OSCMessage( msg, randval ) )
        time.sleep(1)
 
except KeyboardInterrupt :
    print "\nClosing OSCServer."
    server.close()
    print "Waiting for Server-thread to finish"
    st.join() ##!!!
    print "Done"