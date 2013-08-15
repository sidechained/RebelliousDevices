// Rebellious Devices
// - prototyping the network topology and networking registration

// There are two types of node, laptops and devices
// Laptops can:
// - send actuation messages to a named device e.g. /actuate/led 1
// - receive sensing messages from all devices and act on them e.g. /sense/ldr 57.5
// Devices can:
// - receive actuation messages and act on them e.g. /actuate/led 1
// (might send the correct voltage to a pinout to activate an led)
// - send sensing messages e.g. /sense/ldr 57.5
// (this may be on a particular action e.g. button press, or at regular intervals e.g. light sensing)

// This class for each type (called RDLaptop and RDDevice) which implements the basic scheme of leds and ldrs described above
// When instantiated both types of node will:
// - tell others they exist
// - listen for others and add them
// RDLaptop is

// one problem is that messages are currently received twice by each laptop
// this is due to the fact that all nodes have the same IP (localhost)
// to facilitate local prototyping, it is therefore necessary
// need to add this in NMLAddressing


// each peer would be allocated a port


(
~laptop1 = RDLaptop(\laptop1);
~laptop2 = RDLaptop(\laptop2);
~device1 = RDDevice(\device1);
)

~device1.peer
~device1.addrBook.peers
~laptop1.addrBook.peers[0].addr.port
