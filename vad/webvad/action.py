import os
from webvad import vad
from webvad import mono

def splitWav():
	for filename in os.listdir("./raw_wav"):
		try:
			print("mono:"+filename)
			mono.splitChannel(filename)
		except Exception:
			print("pass:"+filename)
	for filename in os.listdir("./mono"):
		try:
			print("split:"+filename)
			vad.main("0",filename)
		except Exception:
			print("pass:"+filename)

def clear():
	for filename in os.listdir("./raw_wav"):
		os.remove("./raw_wav/"+filename)
	for filename in os.listdir("./mono"):
		os.remove("./mono/"+filename)
	for filename in os.listdir("./split"):
		os.remove("./split/"+filename)