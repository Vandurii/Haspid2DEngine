package main.util;

import java.util.ArrayList;
import java.util.List;

public class AudioSheet implements Properties{
    private List<Sound> soundList;

    public AudioSheet(){
        soundList = new ArrayList<>();
    }

    public void add(Sound sound){
        soundList.add(sound);
    }

    public List<Sound> getSoundList(){
        return soundList;
    }
}
