import collections
import contextlib
import sys
import uuid
import wave

import webrtcvad


def read_wave(path):
    with contextlib.closing(wave.open(path, 'rb')) as wf:
        num_channels = wf.getnchannels()
        assert num_channels == 1
        sample_width = wf.getsampwidth()
        assert sample_width == 2
        sample_rate = wf.getframerate()
        assert sample_rate in (8000, 16000, 32000)
        pcm_data = wf.readframes(wf.getnframes())
        return pcm_data, sample_rate


def write_wave(path, audio, sample_rate):
    with contextlib.closing(wave.open(path, 'wb')) as wf:
        wf.setnchannels(1)
        wf.setsampwidth(2)
        wf.setframerate(sample_rate)
        wf.writeframes(audio)


class Frame(object):
    def __init__(self, bytes, timestamp, duration):
        self.bytes = bytes
        self.timestamp = timestamp
        self.duration = duration


def frame_generator(frame_duration_ms, audio, sample_rate):
    n = int(sample_rate * (frame_duration_ms / 1000.0) * 2)
    offset = 0
    timestamp = 0.0
    duration = (float(n) / sample_rate) / 2.0
    while offset + n < len(audio):
        yield Frame(audio[offset:offset + n], timestamp, duration)
        timestamp += duration
        offset += n



def vad_collector(sample_rate, left, frame_duration_ms, 
                  padding_duration_ms, vad, frames):
    num_padding_frames = int(padding_duration_ms / frame_duration_ms)
    ring_buffer = collections.deque(maxlen=num_padding_frames)
    sil_buffer = collections.deque(maxlen=left)
    triggered = False
    
    voiced_frames = []
    vad_frames = []
    for frame in frames:
        is_speech = vad.is_speech(frame.bytes, sample_rate)

        sys.stdout.write('1' if is_speech else '0')
        if not triggered:
            if len(ring_buffer) >= num_padding_frames:
                tmp = ring_buffer.popleft()
                sil_buffer.append(tmp[0])
            #sil_buffer.append(frame);
            
            ring_buffer.append((frame, is_speech))
            num_voiced = len([f for f, speech in ring_buffer if speech])
            if num_voiced > 0.9 * ring_buffer.maxlen:
                triggered = True
                sys.stdout.write('+(%s)' % (ring_buffer[0][0].timestamp,))
                for f, s in ring_buffer:
                    voiced_frames.append(f)
                ring_buffer.clear()
        else:
            voiced_frames.append(frame)
            ring_buffer.append((frame, is_speech))
            num_unvoiced = len([f for f, speech in ring_buffer if not speech])
            if num_unvoiced > 0.9 * ring_buffer.maxlen:
                sys.stdout.write('-(%s)' % (frame.timestamp + frame.duration))
                triggered = False
                for i in range(len(sil_buffer)):
                    tmp = sil_buffer.popleft()
                    vad_frames.append(tmp)
                for item in voiced_frames:
                    vad_frames.append(item)
                yield b''.join([f.bytes for f in vad_frames])
                vad_frames = []
                ring_buffer.clear()
                sil_buffer.clear()
                voiced_frames = []
    if triggered:
        sys.stdout.write('-(%s)' % (frame.timestamp + frame.duration))
    sys.stdout.write('\n')
    if voiced_frames:
        for i in range(len(sil_buffer)):
            tmp = sil_buffer.popleft()
            vad_frames.append(tmp)
        for item in voiced_frames:
            vad_frames.append(item)
        yield b''.join([f.bytes for f in vad_frames])
        vad_frames = []
        ring_buffer.clear()
        sil_buffer.clear()
        voiced_frames = []
		

def main(vad,wav):
    audio, sample_rate = read_wave("./mono/"+wav)
    vad = webrtcvad.Vad(int(vad))
    frames = frame_generator(30, audio, sample_rate)
    frames = list(frames)
    segments = vad_collector(sample_rate,6, 30, 300, vad, frames)
    for i, segment in enumerate(segments):
        id=uuid.uuid1()
        temp='./split/'+wav.replace(".wav","")+'-chunk-'+str(id)+'-%002d'+'.wav'
        path = temp % (i,)
        print(' Writing %s' % (path,))
        write_wave(path, segment, sample_rate)
    return True