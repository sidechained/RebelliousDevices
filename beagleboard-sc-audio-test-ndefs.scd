s.boot
s.quit


(
Ndef(\y, {
	GVerb.ar(SinOsc.ar(LFPulse.ar(8, 0.5).exprange(200, 200).lag(0.01)), SinOsc.ar(4, 0.5).exprange(0.5, 4)) * 0
}).play
)

Ndef(\y).fadeTime_(5)

x.free

"kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk".speak

(
n=LFNoise1;
Ndef(\x, {
    a=SinOsc.ar(65, Ndef(\x).ar*n.ar(0.1,3), n.ar(3,6)).tanh;
    9.do{a=AllpassL.ar(a,0.3,{0.2.rand+0.1}!2,5)};
    a.tanh
}).play
);

Ndef(\x).stop