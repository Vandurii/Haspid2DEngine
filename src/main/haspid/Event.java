package main.haspid;

import main.editor.editorControl.MouseControls;

import java.util.ArrayList;
import java.util.List;

public class Event {

    public static List<Hint> hintList = new ArrayList<>();

    public static boolean collider = true;

    public static boolean viewProjection = true;
    public static boolean moveCamera = true;
    public static boolean activateObject = true;

    public Event(){
        hintList.add(new Hint("viewProjection", "Use scroll to change the world scale."));
        hintList.add(new Hint("activateObject", "Right-click on an Object to activate it."));
    }

    public void update(){

        if(!MouseControls.getAllActiveObjects().isEmpty()){

        }



    }

    public class Hint{
        private String text;
        private String name;
        private boolean enabled;

        public Hint(String name, String text){
            this.text = text;
            this.name = name;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
