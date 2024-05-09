package main.util;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.libc.LibCStdlib.free;
import static org.lwjgl.system.MemoryStack.*;

public class Sound implements Properties{
    private int bufferID;
    private int sourceID;
    private String filePath;
    private boolean isPlaying;

    public Sound(AudioConfig audioConfig){
        this.filePath = audioConfig.getFilePath();

        stackPush();
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer samplerRateBuffer = stackMallocInt(1);

        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(filePath, channelsBuffer, samplerRateBuffer);
        if(rawAudioBuffer == null){
            System.out.println("Couldn't load file: " + filePath);
            stackPop();
            stackPop();
            return;
        }

        int channels = channelsBuffer.get();
        int sampleRate = samplerRateBuffer.get();
        stackPop();
        stackPop();

        int format = -1;
        if(channels == 1){
            format = AL_FORMAT_MONO16;
        }else if(channels == 2){
            format = AL_FORMAT_STEREO16;
        }

        bufferID = alGenBuffers();
        alBufferData(bufferID, format, rawAudioBuffer, sampleRate);

        sourceID = alGenSources();
        alSourcei(sourceID, AL_BUFFER, bufferID);
        alSourcei(sourceID, AL_LOOPING, audioConfig.isLooping() ? 1 : 0);
        alSourcei(sourceID, AL_POSITION, 0);
        alSourcef(sourceID, AL_GAIN, 0.3f);

        free(rawAudioBuffer);
    }

    public void delete(){
        alDeleteSources(sourceID);
        alDeleteBuffers(bufferID);
    }

    public void play(){
        int state = alGetSourcei(sourceID, AL_SOURCE_STATE);
        if(state == AL_STOPPED){
            isPlaying = false;
            alSourcei(sourceID, AL_POSITION, 0);
        }

        if(!isPlaying){
            alSourcePlay(sourceID);
            isPlaying = true;
        }
    }

    public void stop(){
        if(isPlaying){
            alSourceStop(sourceID);
            isPlaying = false;
        }
    }

    public boolean isPlaying(){
        int state = alGetSourcei(sourceID, AL_SOURCE_STATE);
        if(state == AL_STOPPED) isPlaying = false;

        return isPlaying;
    }

    public String getFilePath(){
        return filePath;
    }
}
