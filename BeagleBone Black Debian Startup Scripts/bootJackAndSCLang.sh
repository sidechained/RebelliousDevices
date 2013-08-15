#! /bin/sh
# /etc/init.d/bootJackAndSCLang.sh

# The following part always gets executed.
echo "starting Jack and SCLang"

# The following part carries out specific functions depending on arguments.
case "$1" in
  start)
    echo "starting jack..."
    jackd -R -d alsa -d hw:1,0 & 
    # uncomment the following (check if needed):
	# export SC_JACK_DEFAULT_INPUTS="system" 
	# export SC_JACK_DEFAULT_OUTPUTS="system" 
    echo "running scserver boot script in sclang..."
    sclang bootSCServer.scd
    echo "...scserver boot script completed, now try to connect remotely to the server"
    ;;
  stop)
    echo "is it worth trying to shutdown the server here..probably not"
    ;;
  *)
    echo "Usage: /etc/init.d/bootJackAndSCLang.sh {start|stop}"
    exit 1
    ;;
esac

exit 0