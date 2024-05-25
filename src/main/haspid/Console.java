package main.haspid;

import main.Configuration;
import main.components.Component;


import java.util.HashSet;

import static main.Configuration.consoleIntro;
import static main.Configuration.isConsoleEnabled;

public class Console extends Component {

    private static Console instance;
    private static double consoleDelay;
    private static HashSet<Log> logList;
    private static double consoleDelayReset;

    private Console(){
        consoleDelayReset = Configuration.consoleDelay;
        logList = new HashSet<>();
    }

    @Override
    public void update(float dt) {
        consoleDelay -= dt;

        if(logList.isEmpty()) return;

        if(isConsoleEnabled && consoleDelay < 0){
            System.out.println(consoleIntro);
            for(Log log: logList){
                System.out.println(log.logType + ": " + log.text);
            }

            logList.clear();
            consoleDelay = consoleDelayReset;
        }
    }

    public static void addLog(Log log){
        logList.add(log);
    }

    public static Console getInstance(){
        if(instance == null) instance = new Console();

        return instance;
    }
}
