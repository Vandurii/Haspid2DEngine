package main.haspid;

import main.Configuration;
import main.components.Component;


import java.util.HashSet;
import java.util.LinkedHashSet;

import static main.Configuration.consoleIntro;
import static main.Configuration.isConsoleEnabled;

public class Console extends Component {

    private static Console instance;
    private static double consoleDelay;
    private static LinkedHashSet<Log> logList;
    private static double consoleDelayReset;

    private Console(){
        consoleDelayReset = Configuration.consoleDelay;
        logList = new LinkedHashSet<>();
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
