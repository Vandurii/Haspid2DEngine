package main.haspid;

public class Log {
    public LogType logType;
    public String text;

    public enum LogType {
        INFO,
        WARNING,
        ERROR,
        DEBUG
    }

    public Log(LogType logType, String text){
        this.logType = logType;
        this.text = text;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Log)) return false;

        Log log = (Log) o;

        return this.text.equals(log.getText()) && this.logType == log.getLogType();
    }

    public LogType getLogType() {
        return logType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

    @Override
    public int hashCode(){
        return text.hashCode() + logType.hashCode();
    }
}
