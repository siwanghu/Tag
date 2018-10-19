import sys
import numpy as np
from scipy.io import wavfile

def splitChannel(src):
	rate,wav=wavfile.read("./raw_wav/"+src)
	src=nameSrc(src)
	left,right=[],[]
	for item in wav:
		left.append(item[0])
		right.append(item[1])
	wavfile.write("./mono/"+src+"-left.wav",rate,np.array(left))
	wavfile.write("./mono/"+src+"-right.wav",rate,np.array(right))
	
def nameSrc(src):
	src=src.replace(".wav","")
	return src
