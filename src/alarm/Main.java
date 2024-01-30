package alarm;

public class Main {

    public static void main(String[] args) {
       
        alarmService myAlarmService = new alarmService();

        try {
        	
            myAlarmService.alarm(1); 

        } catch (Exception e) {
           
            e.printStackTrace();
        }
    }
}

