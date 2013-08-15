#! /bin/sh
# /etc/init.d/foobar

# The following part always gets executed.
echo "This part always gets executed"

# The following part carries out specific functions depending on arguments.
case "$1" in
  start)
    echo "Starting foobar"
    echo "foobar is alive"
    ;;
  stop)
    echo "Stopping foobar"
    echo "foobar is dead"
    ;;
  *)
    echo "Usage: /etc/init.d/foobar {start|stop}"
    exit 1
    ;;
esac

exit 0