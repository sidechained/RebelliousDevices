// Rebellious Devices: Sharing Servers Using Utopia

// In the Rebellious Devices topology:
// - servers run on devices
// - but are controlled from laptops
// - example below assumes two laptops and two devices
// * incomplete, need to revisit this *

(
thisProcess.openUDPPort(9000);
thisProcess.openUDPPort(9001);
thisProcess.openUDPPort(9002);
thisProcess.openUDPPort(9003);
~laptop1Peer = Peer(\laptop1, NetAddr("127.0.0.1", 9000));
~laptop2Peer = Peer(\laptop2, NetAddr("127.0.0.1", 9001));
~device1Peer = Peer(\device1, NetAddr("127.0.0.1", 9002));
~device1Peer = Peer(\device1, NetAddr("127.0.0.1", 9003));
~laptop1AddrBook = AddrBook().addMe(~laptop1Peer);
~laptop2AddrBook = AddrBook().addMe(~laptop2Peer);
~device1AddrBook = AddrBook().addMe(~device1Peer);
~device2AddrBook = AddrBook().addMe(~device2Peer);

~laptop1AddrBook.add(~laptop2Peer);
~laptop1AddrBook.add(~device1Peer);
~laptop1AddrBook.add(~device2Peer);

~laptop2AddrBook.add(~laptop1Peer);
~laptop2AddrBook.add(~device1Peer);
~laptop2AddrBook.add(~device2Peer);

~device1AddrBook.add(~laptop1Peer);
~device1AddrBook.add(~laptop2Peer);
~device1AddrBook.add(~device2Peer);

~device2AddrBook.add(~laptop1Peer);
~device2AddrBook.add(~laptop2Peer);
~device2AddrBook.add(~device1Peer);

// make some client IDs; we only need this if we want to play on each other's servers
~laptop1ClientID = 1;
~laptop2ClientID = 2;
)

// here each device creates and boots its server
(
~device1Server = Server(\device1Server, NetAddr("127.0.0.1", 57111), clientID:~me1ClientID);
~device2Server = Server(\device2Server, NetAddr("127.0.0.1", 57112), clientID:~me2ClientID);
// how do client ID's work here?
~server1.boot;
~server2.boot;
)

// here we will share server addresses using OSCObjectSpace
(
~laptop1serverAddrs = OSCObjectSpace(~laptop1AddrBook, oscPath:'/serverAddrs');
~laptop2serverAddrs = OSCObjectSpace(~laptop2AddrBook, oscPath:'/serverAddrs');
~device1serverAddrs = OSCObjectSpace(~device1AddrBook, oscPath:'/serverAddrs');
~device2serverAddrs = OSCObjectSpace(~device2AddrBook, oscPath:'/serverAddrs');
)
