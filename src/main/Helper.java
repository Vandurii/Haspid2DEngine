package main;

public class Helper {

    public static boolean isNull(Object ...obj){
        for(Object o: obj){
            if(o != null) return false;
        }

        return true;
    }

    public static void print(String s){
        System.out.println(s);
    }

    public static boolean isNotNull(Object ...obj){
        for(Object o: obj){
            if(o == null) return false;
        }

        return true;
    }
}
