package main.util;

public class AudioConfig {
    private String filePath;
    private boolean isLooping;

    public AudioConfig(String filePath, boolean isLooping){
        this.filePath = filePath;
        this.isLooping = isLooping;

    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
    }
}
